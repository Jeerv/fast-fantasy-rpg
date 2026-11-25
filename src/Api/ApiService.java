package Api;

import Util.SenhaUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@Service
public class ApiService {
    private static final long SESSION_DAYS = 30;

    private final JdbcTemplate jdbc;
    private final ObjectMapper json;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();

    public ApiService(JdbcTemplate jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    public Map<String, Object> health() {
        jdbc.queryForObject("SELECT 1", Integer.class);
        return Map.of("message", "API Java e conexão com MySQL funcionando.");
    }

    @Transactional
    public Map<String, Object> register(String name, String email, String password) {
        final String normalizedName = name == null ? "" : name.trim();
        final String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedName.isBlank() || normalizedName.length() > 100) throw new ApiException(422, "Informe um nome com até 100 caracteres.");
        if (!normalizedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || normalizedEmail.length() > 150) {
            throw new ApiException(422, "E-mail inválido.");
        }
        if (password == null || password.length() < 6 || password.length() > 256) {
            throw new ApiException(422, "A senha precisa ter ao menos 6 caracteres.");
        }
        Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE email = ?", Integer.class, normalizedEmail);
        if (exists != null && exists > 0) throw new ApiException(409, "Já existe uma conta com esse e-mail.");

        KeyHolder key = new GeneratedKeyHolder();
        String passwordHash = SenhaUtil.gerarHash(password);
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO usuario (nome_usuario, email, senha, premium, id_conquista) VALUES (?, ?, ?, 0, NULL)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, normalizedName);
            statement.setString(2, normalizedEmail);
            statement.setString(3, passwordHash);
            return statement;
        }, key);
        Number generated = key.getKey();
        if (generated == null) throw new ApiException(500, "Não foi possível criar a conta.");
        int userId = generated.intValue();
        jdbc.update("INSERT INTO player (status, id_usuario) VALUES ('ativo', ?)", userId);
        return sessionFor(userId, readUser(userId));
    }

    public Map<String, Object> login(String email, String password) {
        email = email == null ? "" : email.trim().toLowerCase();
        List<Map<String, Object>> matches = jdbc.queryForList(
                "SELECT id_usuario, senha FROM usuario WHERE email = ? LIMIT 1", email);
        if (matches.isEmpty() || password == null) throw new ApiException(401, "E-mail ou senha incorretos.");

        Map<String, Object> row = matches.get(0);
        int userId = ((Number) row.get("id_usuario")).intValue();
        String stored = String.valueOf(row.get("senha"));
        boolean valid = verifyPassword(password, stored);
        if (!valid) throw new ApiException(401, "E-mail ou senha incorretos.");
        if (!stored.contains(":")) jdbc.update("UPDATE usuario SET senha = ? WHERE id_usuario = ?", SenhaUtil.gerarHash(password), userId);
        return sessionFor(userId, readUser(userId));
    }

    public Map<String, Object> currentUser(String token) {
        int userId = requireUserId(token);
        return Map.of("user", readUser(userId));
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) return;
        jdbc.update("DELETE FROM ff_app_session WHERE token_hash = ?", sha256(token));
    }

    public int requireUserId(String token) {
        if (token == null || token.isBlank()) throw new ApiException(401, "Sessão expirada. Entre novamente.");
        try {
            return jdbc.queryForObject(
                    "SELECT id_usuario FROM ff_app_session WHERE token_hash = ? AND expires_at > CURRENT_TIMESTAMP",
                    Integer.class, sha256(token));
        } catch (EmptyResultDataAccessException error) {
            throw new ApiException(401, "Sessão expirada. Entre novamente.");
        }
    }

    public Map<String, Object> listMonsters(String token, String search) {
        int userId = requireUserId(token);
        List<Map<String, Object>> monsters = jdbc.queryForList(
                "SELECT id_monstro, nome, tamanho, tipo, tendencia, ca, hp_maximo, deslocamento, " +
                        "forca, destreza, constituicao, inteligencia, sabedoria, carisma, desafio, xp, descricao " +
                        "FROM monstro WHERE (id_criador IS NULL OR id_criador = ?) AND nome LIKE ? ORDER BY nome",
                userId, "%" + (search == null ? "" : search.trim()) + "%");
        return Map.of("monsters", monsters);
    }

    public Map<String, Object> loadState(String token) {
        int userId = requireUserId(token);
        try {
            Map<String, Object> row = jdbc.queryForMap(
                    "SELECT state_json, version, updated_at FROM ff_app_user_state WHERE id_usuario = ?", userId);
            Map<String, Object> state = json.readValue(String.valueOf(row.get("state_json")), new TypeReference<>() {});
            return Map.of("state", state, "version", ((Number) row.get("version")).longValue(),
                    "updatedAt", String.valueOf(row.get("updated_at")));
        } catch (EmptyResultDataAccessException error) {
            return Map.of("state", Map.of(), "version", 0, "updatedAt", "");
        } catch (JsonProcessingException error) {
            throw new ApiException(500, "O estado salvo da conta está inválido.");
        }
    }

    @Transactional
    public Map<String, Object> saveState(String token, Map<String, Object> state) {
        int userId = requireUserId(token);
        final String payload;
        try {
            payload = json.writeValueAsString(state);
        } catch (JsonProcessingException error) {
            throw new ApiException(422, "Não foi possível salvar os dados da conta.");
        }
        jdbc.update("INSERT INTO ff_app_user_state (id_usuario, state_json, version, updated_at) " +
                        "VALUES (?, ?, 1, CURRENT_TIMESTAMP) ON DUPLICATE KEY UPDATE " +
                        "state_json = VALUES(state_json), version = version + 1, updated_at = CURRENT_TIMESTAMP",
                userId, payload);
        Long version = jdbc.queryForObject("SELECT version FROM ff_app_user_state WHERE id_usuario = ?", Long.class, userId);
        return Map.of("saved", true, "version", version == null ? 1 : version, "updatedAt", Instant.now().toString());
    }

    @Transactional
    public Map<String, Object> createCampaign(String token, String stateJson) {
        int userId = requireUserId(token);
        Map<String, Object> table = parseObject(stateJson);
        String id = string(table.get("id"));
        String name = string(table.get("name"));
        if (id.isBlank() || id.length() > 64 || name.isBlank() || name.length() > 100) {
            throw new ApiException(422, "Informe um nome válido para a mesa.");
        }
        String email = string(readUser(userId).get("email"));
        table.put("gmUser", email);
        String code = createInviteCode();
        table.put("code", code);
        table.put("serverVersion", 1);
        boolean privateTable = Boolean.TRUE.equals(table.get("priv"));
        String payload = writeJson(table);
        jdbc.update("INSERT INTO ff_app_campaign (campaign_id, invite_code, owner_user_id, is_private, state_json, version) VALUES (?, ?, ?, ?, ?, 1)",
                id, code, userId, privateTable, payload);
        jdbc.update("INSERT INTO ff_app_campaign_member (campaign_id, id_usuario, role_name) VALUES (?, ?, 'gm')", id, userId);
        return campaignResult(table, 1);
    }

    public Map<String, Object> listCampaigns(String token) {
        int userId = requireUserId(token);
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT state_json, version FROM ff_app_campaign WHERE is_private = FALSE OR " +
                        "campaign_id IN (SELECT campaign_id FROM ff_app_campaign_member WHERE id_usuario = ?) ORDER BY updated_at DESC",
                userId);
        List<Map<String, Object>> campaigns = rows.stream().map(this::campaignFromRow).toList();
        return Map.of("campaigns", campaigns);
    }

    public Map<String, Object> getCampaignByCode(String token, String code) {
        requireUserId(token);
        if (code == null || code.isBlank()) throw new ApiException(422, "Informe o código da mesa.");
        try {
            return campaignFromRow(jdbc.queryForMap(
                    "SELECT state_json, version FROM ff_app_campaign WHERE invite_code = ?", code.trim().toUpperCase()));
        } catch (EmptyResultDataAccessException error) {
            throw new ApiException(404, "Código não encontrado.");
        }
    }

    @Transactional
    public void leaveCampaign(String token, String id) {
        int userId = requireUserId(token);
        Map<String, Object> row;
        try {
            row = jdbc.queryForMap("SELECT c.owner_user_id, c.state_json, c.version FROM ff_app_campaign c " +
                    "JOIN ff_app_campaign_member m ON m.campaign_id = c.campaign_id WHERE c.campaign_id = ? AND m.id_usuario = ? FOR UPDATE", id, userId);
        } catch (EmptyResultDataAccessException error) {
            return;
        }
        if (number(row.get("owner_user_id")) == userId) throw new ApiException(409, "O mestre precisa encerrar a mesa.");
        String email = string(readUser(userId).get("email"));
        Map<String, Object> table = parseObject(string(row.get("state_json")));
        table.put("members", listOfMaps(table.get("members")).stream()
                .filter(member -> !email.equals(string(member.get("user")))).toList());
        table.put("tokens", listOfMaps(table.get("tokens")).stream()
                .filter(entry -> !email.equals(string(entry.get("owner")))).toList());
        long version = number(row.get("version")) + 1;
        table.put("serverVersion", version);
        jdbc.update("DELETE FROM ff_app_campaign_member WHERE campaign_id = ? AND id_usuario = ?", id, userId);
        jdbc.update("UPDATE ff_app_campaign SET state_json = ?, version = ?, updated_at = CURRENT_TIMESTAMP WHERE campaign_id = ?",
                writeJson(table), version, id);
    }

    @Transactional
    public void deleteCampaign(String token, String id) {
        int userId = requireUserId(token);
        int deleted = jdbc.update("DELETE FROM ff_app_campaign WHERE campaign_id = ? AND owner_user_id = ?", id, userId);
        if (deleted == 0) throw new ApiException(403, "Apenas o mestre pode encerrar esta mesa.");
        jdbc.update("DELETE FROM ff_app_campaign_member WHERE campaign_id = ?", id);
    }

    @Transactional
    public Map<String, Object> joinCampaign(String token, String code, String memberJson, String tokenJson) {
        int userId = requireUserId(token);
        Map<String, Object> account = readUser(userId);
        Map<String, Object> row;
        try {
            row = jdbc.queryForMap("SELECT campaign_id, state_json, version FROM ff_app_campaign WHERE invite_code = ? FOR UPDATE",
                    code == null ? "" : code.trim().toUpperCase());
        } catch (EmptyResultDataAccessException error) {
            throw new ApiException(404, "Código não encontrado.");
        }
        String id = string(row.get("campaign_id"));
        Map<String, Object> table = parseObject(string(row.get("state_json")));
        List<Map<String, Object>> members = listOfMaps(table.get("members"));
        if (members.stream().anyMatch(member -> string(member.get("user")).equals(string(account.get("email"))))) {
            return campaignResult(table, number(row.get("version")));
        }
        int max = (int) number(table.get("max"));
        if (members.size() >= max) throw new ApiException(409, "Mesa lotada.");

        Map<String, Object> member = parseObject(memberJson);
        String email = string(account.get("email"));
        member.put("id", email);
        member.put("user", email);
        member.put("name", account.get("name"));
        members.add(member);
        table.put("members", members);

        List<Map<String, Object>> tokens = listOfMaps(table.get("tokens"));
        Map<String, Object> tokenEntry = parseObject(tokenJson);
        tokenEntry.put("owner", email);
        tokens.add(tokenEntry);
        table.put("tokens", tokens);
        List<Map<String, Object>> chat = listOfMaps(table.get("chat"));
        chat.add(Map.of("sys", true, "text", string(member.get("charName")) + " entrou na mesa.", "t", System.currentTimeMillis()));
        table.put("chat", chat);

        long version = number(row.get("version")) + 1;
        table.put("serverVersion", version);
        jdbc.update("INSERT INTO ff_app_campaign_member (campaign_id, id_usuario, role_name) VALUES (?, ?, 'player')", id, userId);
        jdbc.update("UPDATE ff_app_campaign SET state_json = ?, version = ?, updated_at = CURRENT_TIMESTAMP WHERE campaign_id = ?",
                writeJson(table), version, id);
        return campaignResult(table, version);
    }

    @Transactional
    public Map<String, Object> saveCampaign(String token, Map<String, Object> body) {
        int userId = requireUserId(token);
        Map<String, Object> incoming = objectValue(body.get("table"));
        String id = string(incoming.get("id"));
        if (id.isBlank()) throw new ApiException(422, "Mesa inválida.");
        try {
            Map<String, Object> row = jdbc.queryForMap(
                    "SELECT c.owner_user_id, c.invite_code, c.state_json, c.version, m.role_name " +
                            "FROM ff_app_campaign c JOIN ff_app_campaign_member m ON m.campaign_id = c.campaign_id " +
                            "WHERE c.campaign_id = ? AND m.id_usuario = ? FOR UPDATE", id, userId);
            Map<String, Object> current = parseObject(string(row.get("state_json")));
            boolean gm = number(row.get("owner_user_id")) == userId;
            String email = string(readUser(userId).get("email"));
            if (!gm && listOfMaps(current.get("members")).stream().noneMatch(member -> email.equals(string(member.get("user"))))) {
                throw new ApiException(403, "Você não participa desta mesa.");
            }
            if (!gm && !sameJson(current.get("initiative"), incoming.get("initiative"))) {
                throw new ApiException(403, "Apenas o mestre pode alterar a iniciativa.");
            }
            if (!gm) {
                for (String key : List.of("id", "code", "name", "desc", "gm", "gmUser", "max", "priv", "created", "rules", "status")) {
                    incoming.put(key, current.get(key));
                }
                incoming.put("members", mergeOwnMember(current, incoming, string(readUser(userId).get("email"))));
                incoming.put("tokens", mergeOwnTokens(current, incoming, string(readUser(userId).get("email"))));
            }
            incoming.put("id", id);
            incoming.put("code", string(row.get("invite_code")));
            incoming.put("gmUser", string(readUser((int) number(row.get("owner_user_id"))).get("email")));
            long version = number(row.get("version")) + 1;
            incoming.put("serverVersion", version);
            jdbc.update("UPDATE ff_app_campaign SET state_json = ?, is_private = ?, version = ?, updated_at = CURRENT_TIMESTAMP WHERE campaign_id = ?",
                    writeJson(incoming), Boolean.TRUE.equals(incoming.get("priv")), version, id);
            if (gm) syncCampaignMembers(id, userId, incoming);
            return campaignResult(incoming, version);
        } catch (EmptyResultDataAccessException error) {
            throw new ApiException(403, "Você não participa desta mesa.");
        }
    }

    private List<Map<String, Object>> mergeOwnMember(Map<String, Object> current, Map<String, Object> incoming, String email) {
        Map<String, Object> submitted = listOfMaps(incoming.get("members")).stream()
                .filter(member -> email.equals(string(member.get("user")))).findFirst().orElse(null);
        List<Map<String, Object>> merged = listOfMaps(current.get("members"));
        if (submitted != null) {
            for (Map<String, Object> existing : merged) {
                if (email.equals(string(existing.get("user")))) {
                    Object hp = submitted.get("hp"), maxHp = existing.get("maxHp");
                    if (hp instanceof Number value && maxHp instanceof Number maximum) {
                        existing.put("hp", Math.max(0, Math.min(value.intValue(), maximum.intValue())));
                    }
                }
            }
        }
        return merged;
    }

    private List<Map<String, Object>> mergeOwnTokens(Map<String, Object> current, Map<String, Object> incoming, String email) {
        List<Map<String, Object>> merged = listOfMaps(current.get("tokens"));
        List<Map<String, Object>> submitted = listOfMaps(incoming.get("tokens"));
        for (Map<String, Object> oldToken : merged) {
            if (!email.equals(string(oldToken.get("owner")))) continue;
            submitted.stream().filter(candidate -> string(candidate.get("id")).equals(string(oldToken.get("id"))))
                    .findFirst().ifPresent(candidate -> { oldToken.put("x", candidate.get("x")); oldToken.put("y", candidate.get("y")); });
        }
        return merged;
    }

    private void syncCampaignMembers(String campaignId, int ownerId, Map<String, Object> table) {
        jdbc.update("DELETE FROM ff_app_campaign_member WHERE campaign_id = ? AND id_usuario <> ?", campaignId, ownerId);
        for (Map<String, Object> member : listOfMaps(table.get("members"))) {
            String email = string(member.get("user"));
            if (email.isBlank()) continue;
            try {
                Integer userId = jdbc.queryForObject("SELECT id_usuario FROM usuario WHERE email = ?", Integer.class, email);
                if (userId != null && userId != ownerId) {
                    jdbc.update("INSERT IGNORE INTO ff_app_campaign_member (campaign_id, id_usuario, role_name) VALUES (?, ?, 'player')",
                            campaignId, userId);
                }
            } catch (EmptyResultDataAccessException ignored) {
                // Membros sem conta registrada não recebem acesso à mesa do servidor.
            }
        }
    }

    private boolean sameJson(Object first, Object second) {
        JsonNode a = json.valueToTree(first), b = json.valueToTree(second);
        return a.equals(b);
    }

    private Map<String, Object> campaignFromRow(Map<String, Object> row) {
        Map<String, Object> table = parseObject(string(row.get("state_json")));
        return campaignResult(table, number(row.get("version")));
    }

    private Map<String, Object> campaignResult(Map<String, Object> table, long version) {
        table.put("serverVersion", version);
        return Map.of("table", table, "version", version);
    }

    private Map<String, Object> parseObject(String value) {
        try { return json.readValue(value == null ? "{}" : value, new TypeReference<>() {}); }
        catch (JsonProcessingException error) { throw new ApiException(422, "Dados da mesa inválidos."); }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectValue(Object value) {
        if (value instanceof Map<?, ?> map) return (Map<String, Object>) map;
        throw new ApiException(422, "Dados da mesa inválidos.");
    }

    private List<Map<String, Object>> listOfMaps(Object value) {
        if (!(value instanceof List<?> list)) return new java.util.ArrayList<>();
        return list.stream().filter(Map.class::isInstance).map(this::objectValue).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
    }

    private String writeJson(Object value) {
        try { return json.writeValueAsString(value); }
        catch (JsonProcessingException error) { throw new ApiException(422, "Não foi possível salvar a mesa."); }
    }

    private String createInviteCode() {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder code = new StringBuilder(6);
            for (int i = 0; i < 6; i++) code.append(alphabet.charAt(random.nextInt(alphabet.length())));
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM ff_app_campaign WHERE invite_code = ?", Integer.class, code.toString());
            if (count == null || count == 0) return code.toString();
        }
        throw new ApiException(503, "Não foi possível gerar um código para a mesa.");
    }

    private long number(Object value) {
        return value instanceof Number number ? number.longValue() : 0;
    }

    private String string(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean verifyPassword(String password, String stored) {
        try {
            if (stored.contains(":")) return SenhaUtil.verificar(password, stored);
            return bcrypt.matches(password, stored);
        } catch (RuntimeException error) {
            return false;
        }
    }

    private Map<String, Object> sessionFor(int userId, Map<String, Object> user) {
        byte[] raw = new byte[32];
        random.nextBytes(raw);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        jdbc.update("INSERT INTO ff_app_session (id_usuario, token_hash, expires_at) VALUES (?, ?, ?)",
                userId, sha256(token), Timestamp.from(Instant.now().plusSeconds(SESSION_DAYS * 24 * 60 * 60)));
        return Map.of("token", token, "user", user);
    }

    private Map<String, Object> readUser(int userId) {
        try {
            Map<String, Object> row = jdbc.queryForMap(
                    "SELECT id_usuario, nome_usuario, email, premium FROM usuario WHERE id_usuario = ?", userId);
            return Map.of("id", ((Number) row.get("id_usuario")).intValue(),
                    "name", String.valueOf(row.get("nome_usuario")),
                    "email", String.valueOf(row.get("email")),
                    "premium", Boolean.TRUE.equals(row.get("premium")) || "1".equals(String.valueOf(row.get("premium"))));
        } catch (EmptyResultDataAccessException error) {
            throw new ApiException(401, "Conta não encontrada.");
        }
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception error) {
            throw new IllegalStateException("Não foi possível validar a sessão.", error);
        }
    }
}
