<?php
declare(strict_types=1);

ini_set('display_errors', '0');
ini_set('session.use_strict_mode', '1');
$secureCookie = !empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off';
session_set_cookie_params([
    'lifetime' => 0,
    'path' => '/',
    'secure' => $secureCookie,
    'httponly' => true,
    'samesite' => 'Lax',
]);
session_start();

header('Content-Type: application/json; charset=utf-8');
header('Cache-Control: no-store, max-age=0');
header('X-Content-Type-Options: nosniff');

function respond(int $status, array $payload): void
{
    http_response_code($status);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_INVALID_UTF8_SUBSTITUTE);
    exit;
}

function database(): PDO
{
    static $connection = null;
    if ($connection instanceof PDO) {
        return $connection;
    }

    $host = getenv('DB_HOST');
    $port = getenv('DB_PORT');
    $name = getenv('DB_NAME');
    $user = getenv('DB_USER');
    $password = getenv('DB_PASSWORD');

    $host = $host !== false && $host !== '' ? $host : 'localhost';
    $port = $port !== false && $port !== '' ? $port : '3306';
    // Os valores padrão correspondem à ConnectionFactory usada pelo Java.
    // Variáveis de ambiente continuam permitindo configuração por instalação.
    $name = $name !== false && $name !== '' ? $name : 'ff_rpg';
    $user = $user !== false && $user !== '' ? $user : 'app_ff';
    $password = $password !== false ? $password : '';

    $dsn = "mysql:host={$host};port={$port};dbname={$name};charset=utf8mb4";
    $connection = new PDO($dsn, $user, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]);
    return $connection;
}

function bearerToken(): string
{
    $header = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if ($header === '' && function_exists('getallheaders')) {
        foreach (getallheaders() as $name => $value) {
            if (strtolower((string)$name) === 'authorization') {
                $header = (string)$value;
                break;
            }
        }
    }
    return preg_match('/^Bearer\s+(.+)$/i', $header, $match) ? trim($match[1]) : '';
}

function signedInUserId(): int
{
    $provided = bearerToken();
    $stored = (string)($_SESSION['api_token'] ?? '');
    if ($provided === '' || $stored === '' || !hash_equals($stored, $provided)) {
        return 0;
    }
    return (int)($_SESSION['user_id'] ?? 0);
}

function createSessionForUser(int $userId): string
{
    session_regenerate_id(true);
    $token = bin2hex(random_bytes(32));
    $_SESSION['user_id'] = $userId;
    $_SESSION['api_token'] = $token;
    return $token;
}

function publicUser(array $row): array
{
    return [
        'id' => (int)$row['id_usuario'],
        'name' => (string)$row['nome_usuario'],
        'email' => (string)$row['email'],
        'premium' => (bool)$row['premium'],
    ];
}

function legacyPasswordMatches(string $password, string $stored): bool
{
    $parts = explode(':', $stored);
    if (count($parts) !== 3 || !ctype_digit($parts[0])) {
        return false;
    }

    $iterations = (int)$parts[0];
    $salt = base64_decode($parts[1], true);
    $expected = base64_decode($parts[2], true);
    if ($iterations < 10000 || $iterations > 1000000 || $salt === false || $expected === false || strlen($expected) !== 32) {
        return false;
    }

    $passwordInputs = [$password];
    if (function_exists('iconv')) {
        $utf16be = iconv('UTF-8', 'UTF-16BE', $password);
        $utf16le = iconv('UTF-8', 'UTF-16LE', $password);
        if ($utf16be !== false) $passwordInputs[] = $utf16be;
        if ($utf16le !== false) $passwordInputs[] = $utf16le;
    }

    foreach (array_unique($passwordInputs) as $passwordInput) {
        $actual = hash_pbkdf2('sha256', $passwordInput, $salt, $iterations, 32, true);
        if (hash_equals($expected, $actual)) {
            return true;
        }
    }
    return false;
}

function verifyStoredPassword(string $password, string $stored): bool
{
    return password_verify($password, $stored) || legacyPasswordMatches($password, $stored);
}

function javaCompatiblePasswordHash(string $password): string
{
    $iterations = 120000;
    $salt = random_bytes(16);
    // Java's PBEKeySpec receives UTF-16 characters; this format is also
    // understood by the legacy PBKDF2 verifier above.
    $passwordBytes = iconv('UTF-8', 'UTF-16BE', $password);
    if ($passwordBytes === false) {
        throw new RuntimeException('Não foi possível preparar a senha.');
    }
    $hash = hash_pbkdf2('sha256', $passwordBytes, $salt, $iterations, 32, true);
    return $iterations . ':' . base64_encode($salt) . ':' . base64_encode($hash);
}

function readUser(PDO $db, int $userId): ?array
{
    $statement = $db->prepare('SELECT id_usuario, nome_usuario, email, premium FROM usuario WHERE id_usuario = :id LIMIT 1');
    $statement->execute(['id' => $userId]);
    $row = $statement->fetch();
    return $row === false ? null : publicUser($row);
}

$path = trim((string)($_GET['path'] ?? $_SERVER['PATH_INFO'] ?? ''), '/');
$method = strtoupper((string)($_SERVER['REQUEST_METHOD'] ?? 'GET'));

try {
    if ($path === 'health' && $method === 'GET') {
        database()->query('SELECT 1')->fetchColumn();
        respond(200, ['message' => 'API PHP e conexão com MySQL funcionando.']);
    }

    if ($path === 'auth/register' && $method === 'POST') {
        $name = trim((string)($_POST['name'] ?? ''));
        $email = strtolower(trim((string)($_POST['email'] ?? '')));
        $password = (string)($_POST['password'] ?? '');

        if ($name === '' || strlen($name) > 100) {
            respond(422, ['message' => 'Informe um nome de usuário com até 100 caracteres.']);
        }
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || strlen($email) > 150) {
            respond(422, ['message' => 'E-mail inválido.']);
        }
        if (strlen($password) < 6 || strlen($password) > 256) {
            respond(422, ['message' => 'A senha precisa ter ao menos 6 caracteres.']);
        }

        $db = database();
        $check = $db->prepare('SELECT id_usuario FROM usuario WHERE email = :email LIMIT 1');
        $check->execute(['email' => $email]);
        if ($check->fetch()) {
            respond(409, ['message' => 'Já existe uma conta cadastrada com esse e-mail.']);
        }

        $db->beginTransaction();
        try {
            $insertUser = $db->prepare(
                'INSERT INTO usuario (nome_usuario, email, senha, premium, id_conquista) VALUES (:name, :email, :password, 0, NULL)'
            );
            $insertUser->execute([
                'name' => $name,
                'email' => $email,
                'password' => javaCompatiblePasswordHash($password),
            ]);
            $userId = (int)$db->lastInsertId();

            $insertPlayer = $db->prepare("INSERT INTO player (status, id_usuario) VALUES ('ativo', :user_id)");
            $insertPlayer->execute(['user_id' => $userId]);
            $db->commit();
        } catch (Throwable $error) {
            if ($db->inTransaction()) $db->rollBack();
            throw $error;
        }

        $user = readUser($db, $userId);
        if ($user === null) {
            respond(500, ['message' => 'Não foi possível carregar a conta criada.']);
        }
        respond(201, ['token' => createSessionForUser($userId), 'user' => $user]);
    }

    if ($path === 'auth/login' && $method === 'POST') {
        $email = strtolower(trim((string)($_POST['email'] ?? '')));
        $password = (string)($_POST['password'] ?? '');
        if (!filter_var($email, FILTER_VALIDATE_EMAIL) || $password === '') {
            respond(401, ['message' => 'E-mail ou senha incorretos.']);
        }

        $db = database();
        $statement = $db->prepare('SELECT id_usuario, nome_usuario, email, senha, premium FROM usuario WHERE email = :email LIMIT 1');
        $statement->execute(['email' => $email]);
        $row = $statement->fetch();
        if ($row === false || !verifyStoredPassword($password, (string)$row['senha'])) {
            respond(401, ['message' => 'E-mail ou senha incorretos.']);
        }

        // Mantém hashes PBKDF2 no formato compartilhado com SenhaUtil.java.
        respond(200, [
            'token' => createSessionForUser((int)$row['id_usuario']),
            'user' => publicUser($row),
        ]);
    }

    if ($path === 'auth/me' && $method === 'GET') {
        $userId = signedInUserId();
        if ($userId < 1) {
            respond(401, ['message' => 'Sessão expirada. Entre novamente.']);
        }
        $user = readUser(database(), $userId);
        if ($user === null) {
            $_SESSION = [];
            respond(401, ['message' => 'Sessão expirada. Entre novamente.']);
        }
        respond(200, ['user' => $user]);
    }

    if ($path === 'auth/logout' && $method === 'POST') {
        $_SESSION = [];
        if (ini_get('session.use_cookies')) {
            $params = session_get_cookie_params();
            setcookie(session_name(), '', [
                'expires' => time() - 42000,
                'path' => $params['path'],
                'domain' => $params['domain'],
                'secure' => $params['secure'],
                'httponly' => $params['httponly'],
                'samesite' => $params['samesite'] ?? 'Lax',
            ]);
        }
        session_destroy();
        respond(200, ['message' => 'Sessão encerrada.']);
    }

    if ($path === 'monsters' && $method === 'GET') {
        $userId = signedInUserId();
        if ($userId < 1) {
            respond(401, ['message' => 'Sessão expirada. Entre novamente.']);
        }

        $search = trim((string)($_GET['search'] ?? ''));
        $statement = database()->prepare(
            'SELECT id_monstro, nome, tamanho, tipo, tendencia, ca, hp_maximo, deslocamento, '
            . 'forca, destreza, constituicao, inteligencia, sabedoria, carisma, desafio, xp, descricao '
            . 'FROM monstro WHERE (id_criador IS NULL OR id_criador = :user_id) '
            . 'AND nome LIKE :search ORDER BY nome'
        );
        $statement->execute(['user_id' => $userId, 'search' => '%' . $search . '%']);
        respond(200, ['monsters' => $statement->fetchAll()]);
    }

    if (in_array($path, ['health', 'auth/register', 'auth/login', 'auth/me', 'auth/logout', 'monsters'], true)) {
        header('Allow: GET, POST');
        respond(405, ['message' => 'Método não permitido.']);
    }
    respond(404, ['message' => 'Rota não encontrada.']);
} catch (PDOException $error) {
    error_log('Fast Fantasy RPG PDO: ' . $error->getMessage());
    respond(503, ['message' => 'Não foi possível concluir a solicitação. Verifique a conexão com o MySQL.']);
} catch (Throwable $error) {
    error_log('Fast Fantasy RPG API: ' . $error->getMessage());
    respond(500, ['message' => 'Não foi possível concluir a solicitação.']);
}
