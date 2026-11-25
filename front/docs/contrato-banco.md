# Contrato do app com o banco MySQL

O cliente principal usa a API Spring Boot Java, que compartilha o banco `ff_rpg` configurado em `ConnectionFactory.java`. Cadastro, login e catálogo acessam `usuario`, `player` e `monstro`; o hash PBKDF2 é compatível com `SenhaUtil.java`, e hashes bcrypt existentes também podem ser verificados. `api.php` permanece para compatibilidade/desenvolvimento. Estado pessoal da conta é salvo na tabela `api_account_state`; campanhas, convite, participantes e estado compartilhado ficam em `api_campaign` e `api_campaign_member`. O cliente busca atualizações periodicamente. A API confere a permissão do mestre para alterar iniciativa.

## Campos usados pelo front

- `usuario`: `nome_usuario`, `email`, `senha`, `premium`; o backend grava senha com hash. `id_conquista` permanece nulo no cadastro inicial.
- `player`: criado com `status = 'ativo'` e o `id_usuario` retornado pelo banco.
- `personagem`: os campos de ficha correspondem a `nome`, `nivel`, IDs de classe/subclasse/player/atributos/habilidades/inventário/magia/raça, `HP_atual`, `HP_maximo`, `CA` e `deslocamento`.
- `atributos`: guarda os seis valores finais. O front calcula os bônus fixos de `raca_atributo` e `sub_raca_atributo` antes das melhorias escolhidas por nível.
- `personagem_pericia`: o mapeamento transforma as perícias selecionadas em `id_pericia` e `proficiente = true`.
- Testes de resistência são calculados no front pela classe, nível e atributo: modificador do atributo + bônus de proficiência quando a classe é proficiente naquela salvaguarda. Esse valor é derivado e não é salvo em coluna própria.
- Os catálogos de magia e habilidades do front foram extraídos dos dumps SQL enviados. A ficha mostra habilidades de classe/subclasse até o nível do personagem e permite selecionar magias do catálogo local.
- O catálogo de magias agrupa os níveis identificados no início das descrições e limita a seleção ao maior círculo alcançado pela classe/subclasse no nível atual; também separa truques das magias com nível. O filtro permite pesquisar por nome/descrição, escolher círculo e exibir disponíveis, todas ou selecionadas. A progressão segue as regras de classes de D&D 5e 2014, incluindo Magia Mística do Bruxo.
- A seleção aplica limites separados para truques e para magias conhecidas, preparadas ou no grimório, conforme classe, nível e atributo de conjuração quando aplicável. O Mago usa a quantidade básica do grimório (seis no 1º nível e mais duas ao subir de nível); magias copiadas de outras fontes não são contabilizadas.
- A tabela `magia` não associa magia à classe. O front usa `SPELL_CLASS_LISTS` em `js/screens/characters.js`, pareada ao catálogo SQL pelas listas base do PHB 2014. A busca mostra somente a lista da classe atual; Cavaleiro Arcano e Trapaceiro Arcano usam a lista de Mago. Restrições de escola dessas subclasses, magias concedidas por domínio/círculo/juramento e listas expandidas do Bruxo não são modeladas.
- Magias selecionadas e outros campos sem representação direta no schema relacional podem ser preservados no estado JSON sincronizado da conta.
- PV máximo e CA são calculados no front: PV usa o dado de vida da classe, Constituição e média fixa nos níveis seguintes; CA usa armadura, escudo e as características sem armadura implementadas. O estado da conta sincroniza os dados serializados do cliente.
- Perícias de classe são limitadas às opções e à quantidade da classe. Perícias de antecedente ainda não são representadas separadamente no app.
- `mesa`: o esquema de campanhas da API mantém nome, código de convite, mestre, membros e estado JSON da campanha, sem depender das limitações das tabelas legadas `mesa`.

As opções de raça, sub-raça e seus bônus no front foram copiadas dos INSERTs enviados e ficam espelhadas em `js/data.js`; não são carregadas do MySQL em tempo real. O bônus flexível do Meio-Elfo vem do texto de `raca.descricao`, porque `raca_atributo` armazena apenas o bônus fixo de Carisma. O front aplica os bônus numéricos estruturados; os outros efeitos descritos em texto de sub-raça são informativos e não são calculados automaticamente. Os atributos finais podem ser gravados em `atributos`, mas a escolha dos dois bônus flexíveis e o histórico da matriz/ASI não podem ser recuperados depois.

## Limitações que permanecem no schema

- `personagem` não tem `id_sub_raca`. O front permite selecionar sub-raça e mantém a seleção local, mas não há coluna que a ligue à ficha.
- `personagem` exige IDs não nulos e únicos para habilidade_classe, habilidade_subclasse e magia. Cada coluna só aceita uma linha; não representa a lista de habilidades adquiridas nem várias magias escolhidas. Essas listas permanecem locais e a ficha ainda não é sincronizada pela API.
- `inventario` aponta para um único `item`; `item.quantidade` pertence ao catálogo, não à posse de cada personagem.
- `atributos` não guarda a matriz original nem o histórico das melhorias de atributo por nível. Apenas os seis valores atuais são mapeáveis.
- A tabela `magia` contém apenas nome e descrição, sem vínculo com classe ou nível. O filtro por classe é uma relação local no front, não uma relação persistida no MySQL; as exceções de subclasse descritas acima continuam fora do modelo.
- O cálculo de CA usa os tipos de armadura presentes no catálogo local atual (couro, couro batido e cota de malha), escudo e características de classe/subclasse cobertas. Outros tipos de armadura, magia, itens mágicos e talentos não são calculados.
- `mesa` não guarda limite de jogadores nem associação entre jogadores e mesas. Participantes, tokens de mesa e limite permanecem locais.
- `cosmeticos` guarda contagens, mas não registra qual token foi escolhido para uma conta ou personagem.
- `usuario.email` não tem restrição `UNIQUE`; a API confere duplicidade no próprio processo, mas o banco não garante isso entre múltiplos processos.
- `personagem.id_subclasse` é obrigatório, inclusive antes do nível em que a classe normalmente escolhe uma subclasse.

`js/db-contract.js` mapeia os nomes de coluna e verifica IDs reais recebidos da API. O estado JSON sincroniza os dados do aplicativo sem exigir que cada campo tenha uma coluna relacional; as tabelas legadas ainda têm as limitações listadas acima para consultas relacionais diretas.
