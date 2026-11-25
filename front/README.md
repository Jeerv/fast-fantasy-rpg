# Fast Fantasy RPG

App responsivo de RPG de mesa. Sirva a pasta pelo XAMPP/Apache e abra `http://localhost/fast_fantasy_rpg/`.

## Integração atual

- O cliente usa a API Java por meio de `window.API_BASE_URL` (ou do parâmetro `api` usado pela janela Java). No endereço local, a configuração padrão é `http://localhost:8081/api`; na nuvem, o front hospedado deve apontar para a URL pública.
- A API Java compartilha o banco MySQL com os DAOs, autentica contas, lista monstros e sincroniza personagens e configurações da mesma conta entre dispositivos.
- Mesas criadas pela API ficam compartilhadas pelo código de convite; mudanças são buscadas pelos clientes a cada poucos segundos. O back-end rejeita mudanças de iniciativa feitas por quem não é o mestre.
- O cadastro usa o formato PBKDF2 reconhecido pelo back Java; hashes bcrypt existentes também podem entrar pelo back Java.
- A API de partidas usa o banco central para compartilhar mesas entre contas diferentes. A sincronização por conta mantém personagens e configurações pessoais.
- O formulário de personagem usa os nomes de raça/sub-raça e bônus presentes no SQL. HP atual/máximo, CA e deslocamento já têm colunas em `personagem`; a gravação de fichas pela API ainda não está implementada.
- O mapeamento e as limitações do esquema estão em `docs/contrato-banco.md`.
- Para abrir pela janela desktop, inicie o MySQL e execute `View.MainView` no IntelliJ. Para abrir no navegador pelo XAMPP, inicie Apache e MySQL e também rode a API Java na porta 8081. O front usa a API Java como padrão local; `api.php` permanece apenas para compatibilidade/desenvolvimento.
- Bootstrap 5.3.9 é carregado do CDN oficial; o navegador precisa de acesso à internet para obter esses arquivos.

## Estrutura do front

- `index.php`: página inicial, Bootstrap e ordem de carregamento dos scripts.
- `css/`: tema e estilos responsivos.
- `js/data.js`: catálogos locais, bônus de raça/sub-raça e mapeamento de colunas SQL.
- `js/db-contract.js`: conversores entre os dados do front e os campos do SQL.
- `js/api.js`: chamadas do front à API configurada.
- `js/screens/`: telas de autenticação, personagem, mesas, jogo e conta.
- `api.php`: API PHP de desenvolvimento; a API Java é a base destinada à hospedagem.

## Android com Capacitor

O projeto prepara o front para gerar os arquivos Android com Capacitor. Instale Node.js com npm e Android Studio; configure `FAST_FANTASY_API_URL` com a URL HTTPS da API, depois rode `npm install`, `npm run build:mobile`, `npm run cap:add:android` e `npm run cap:open:android`. A pasta `mobile/` é gerada e não deve ser editada manualmente. Para sincronizar personagens e configurações pessoais entre desktop e celular, use a mesma conta; para jogar na mesma mesa com contas diferentes, entre com o código de convite da campanha.

O projeto Java expõe uma API HTTP Spring Boot. A janela JavaFX local inicia essa API junto com o servidor PHP que entrega o front. Para hospedagem, o front e os clientes Android/Windows apontarão para a URL pública dessa API.

## Hospedagem na Railway

O back-end Java e o front estático são serviços separados e usam os Dockerfiles de suas respectivas pastas. Na Railway, crie um projeto com um serviço MySQL e dois serviços Docker, um apontando para a pasta do back-end Java (raiz deste repositório) e outro para a pasta do front (`front/`). O Dockerfile do front serve a página como HTML estático, sem executar PHP.

No serviço MySQL, confira os backups e guarde as credenciais. Importe primeiro `db/ff_rpg-base.sql` para o banco; depois, no serviço Java, configure `DB_URL`, `DB_USER`, `DB_PASSWORD`, `APP_CORS_ORIGINS` e `PORT`. Use variáveis referenciadas do serviço MySQL para preencher URL, usuário e senha. O Spring cria as tabelas adicionais da API na inicialização. Gere um domínio HTTPS público para o serviço Java e outro para o front; no front defina `FAST_FANTASY_API_URL` com a URL pública da API seguida de `/api`. Em `APP_CORS_ORIGINS`, informe somente a origem pública do front (esquema e domínio, sem caminho). O servidor do front fornece `/health` para verificação de inicialização.

As pastas do Java e do front ainda são projetos locais separados, sem conexão Git configurada. Para implantação pela Railway CLI, conecte cada serviço ao projeto certo e envie cada pasta separadamente (`railway up`), ou publique os projetos em repositórios Git e vincule cada serviço à pasta correspondente. A implantação cria serviços expostos à internet e pode gerar cobrança conforme o plano e o uso; confira o painel antes de habilitar a publicação.

Os dados de autenticação não são guardados como senha no navegador. Contas locais do protótipo não são migradas automaticamente para o banco.

## Mesa em jogo: PV e flanqueamento

- **PV na sessão**: o mestre toca no token (ou no card do jogador) para alterar os PV de jogadores e inimigos; o jogador altera só o próprio personagem. Há dano, cura, definir PV e PV temporários (o dano consome primeiro os temporários). Cada alteração é registrada no chat da mesa.
- **Flanqueamento** (regra opcional do Livro do Mestre, cap. 8): o mestre liga/desliga em "Gerenciar mesa". Dois combatentes adjacentes em lados ou cantos opostos de um inimigo têm vantagem nos ataques corpo a corpo contra ele. O grid destaca o flanqueado (anel laranja com ⚔) e os flanqueadores (contorno tracejado), e a lista abaixo do mapa traz o botão "Atacar com vantagem". Quem está com 0 PV não flanqueia; o mestre pode marcar "Não pode flanquear" (incapacitado ou sem ver o alvo).
- Os dados novos (`rules.flanking`, `noFlank`, `tempHp`) ficam no `localStorage` junto com a mesa, como o restante do estado do jogo.

## Animação dos dados

- Todas as rolagens (d4 a d100 no chat da mesa, d20 normal, com vantagem e com desvantagem, e testes de perícia/atributo/salvaguarda da ficha) mostram o dado girando e caindo antes do resultado. O resultado é sorteado antes e a mensagem só aparece no chat quando o dado para.
- Com vantagem/desvantagem aparecem dois d20: o que vale fica brilhando e o descartado escurece. Com bônus, o total aparece como `14 + 5 = 19`. Crítico (20) e falha crítica (1) têm destaque próprio.
- Tocar na tela pula a animação e fecha. Dá para desligar em Conta > Configurações > "Animação ao rolar dados", e o navegador com "reduzir movimento" ativo recebe uma versão sem giro.
- Arquivos: `js/dice.js` e `css/dice.css`.
