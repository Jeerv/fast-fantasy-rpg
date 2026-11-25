# Fast Fantasy RPG no IntelliJ

## Base do back-end compartilhado

O projeto inclui uma API Spring Boot para autenticação, catálogo de monstros, estado da conta e mesas compartilhadas por código de convite. A API verifica no servidor que somente o mestre pode alterar a iniciativa. Os dados ficam no MySQL; a primeira inicialização cria as tabelas da API em `src/main/resources/schema.sql`.

Configure as variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD` antes de iniciar o back-end. Use `DB_URL` para apontar ao MySQL local ou gerenciado na nuvem. `PORT` e `APP_CORS_ORIGINS` ajustam a porta e os endereços dos clientes autorizados. O arquivo `.env.example` mostra os nomes esperados; não coloque senhas reais no projeto.

Para um banco vazio na nuvem, crie o banco no provedor e importe `db/ff_rpg-base.sql` nele. Esse script é o SQL de tabelas e catálogos enviado para o projeto, sem `CREATE DATABASE`/`USE`; a API cria suas tabelas de sessão, estado e campanhas ao iniciar.

Para hospedar em um serviço que aceite contêineres, o `Dockerfile` gera o back-end como um JAR executável. A hospedagem e o banco ainda precisam ser criados e configurados no provedor escolhido.

### Configuração Railway

Na Railway, crie o serviço MySQL e o serviço Java no mesmo projeto. Importe `db/ff_rpg-base.sql` no banco antes da primeira inicialização da API. Configure as variáveis do serviço Java usando as referências do serviço MySQL (`DB_USER` para `MYSQLUSER`, `DB_PASSWORD` para `MYSQLPASSWORD` e `DB_URL` para `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?serverTimezone=UTC`). Se o serviço do banco tiver outro nome, ajuste `MySQL` nas referências. Configure `APP_CORS_ORIGINS` com a origem HTTPS do front, sem `/api`, e gere um domínio público HTTPS para a API. Railway oferece um serviço MySQL e variáveis para conectar serviços dentro do mesmo projeto; consulte a [documentação oficial](https://docs.railway.com/databases/mysql).

O front tem um segundo Dockerfile e precisa ser enviado como outro serviço, usando a pasta `C:\xampp\htdocs\fast_fantasy_rpg`. No serviço do front, configure `FAST_FANTASY_API_URL` como `https://<dominio-publico-da-api>/api`, gere o domínio HTTPS e depois atualize `APP_CORS_ORIGINS` no serviço Java com a origem pública do front. Os domínios públicos são criados nas configurações de rede de cada serviço Railway; veja [domínios públicos](https://docs.railway.com/networking/domains/working-with-domains).

## Abrir a interface do front pelo projeto Java

1. Inicie o **MySQL** no XAMPP Control Panel.
2. Configure `DB_URL`, `DB_USER` e `DB_PASSWORD` na configuração de execução do IntelliJ.
3. Abra este projeto no IntelliJ e carregue/recarregue o `pom.xml` como projeto Maven. Na primeira vez, o IntelliJ precisa baixar as dependências JavaFX e Spring.
4. Execute `View.MainView` (ou use o objetivo Maven `javafx:run`).

A janela Java inicia a API e o servidor PHP embutido do XAMPP em `http://127.0.0.1:8090` e exibe o front nela. Não é necessário iniciar o Apache. O processo PHP e a API são encerrados quando a janela fecha.

O caminho padrão do front é `C:\xampp\htdocs\fast_fantasy_rpg` e o do PHP é `C:\xampp\php\php.exe`. Se o XAMPP estiver instalado em outro local, configure as variáveis de ambiente `FAST_FANTASY_WEB_ROOT` e `FAST_FANTASY_PHP`. A porta pode ser alterada com `FAST_FANTASY_PORT`.

O front e o back acessam o mesmo banco `ff_rpg`; mantenha o MySQL ligado durante o uso local. Para a versão desktop distribuída apontar à nuvem, configure `FAST_FANTASY_FRONT_URL` com o HTTPS do front hospedado e `FAST_FANTASY_API_URL` com o HTTPS da API. O pacote Windows ainda precisa ser gerado; o Android ainda depende de Node/npm e Android Studio.
