CREATE TABLE cosmeticos (
    id_cosmeticos INT PRIMARY KEY AUTO_INCREMENT,
    tokens INT NOT NULL DEFAULT 0,
    icones_de_perfil INT NOT NULL DEFAULT 0,
    dados_personalizados INT NOT NULL DEFAULT 0
);

CREATE TABLE conquista (
    id_conquista INT PRIMARY KEY AUTO_INCREMENT,
    moedas INT NOT NULL DEFAULT 0,
    id_cosmeticos INT,
    FOREIGN KEY (id_cosmeticos) REFERENCES cosmeticos(id_cosmeticos)
);

CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nome_usuario VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    id_conquista INT UNIQUE,
    premium BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (id_conquista) REFERENCES conquista(id_conquista)
);

CREATE TABLE mestre (
    id_mestre INT PRIMARY KEY AUTO_INCREMENT,
    status ENUM('ativo', 'inativo') NOT NULL DEFAULT 'ativo',
    id_usuario INT NOT NULL UNIQUE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE player (
    id_player INT PRIMARY KEY AUTO_INCREMENT,
    status ENUM('ativo', 'inativo') NOT NULL DEFAULT 'ativo',
    id_usuario INT NOT NULL UNIQUE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE mesa (
    id_mesa INT PRIMARY KEY AUTO_INCREMENT,
    mesa_privada BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('ativa', 'encerrada') NOT NULL DEFAULT 'ativa',
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    codigo_convite VARCHAR(50) NOT NULL UNIQUE,
    id_mestre INT NOT NULL,
    FOREIGN KEY (id_mestre) REFERENCES mestre(id_mestre)
);

CREATE TABLE mesa_player (
    id_mesa INT NOT NULL,
    id_player INT NOT NULL,
    PRIMARY KEY (id_mesa, id_player),
    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa) ON DELETE CASCADE,
    FOREIGN KEY (id_player) REFERENCES player(id_player) ON DELETE CASCADE
);

CREATE TABLE raca (
    id_raca INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(300) NOT NULL
);

CREATE TABLE sub_raca (
    id_sub_raca INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(300) NOT NULL,
    id_raca INT NOT NULL,
    UNIQUE (id_sub_raca, id_raca),
    FOREIGN KEY (id_raca) REFERENCES raca(id_raca)
);

CREATE TABLE raca_atributo (
    id_raca INT NOT NULL,
    atributo ENUM('forca', 'destreza', 'constituicao', 'inteligencia', 'sabedoria', 'carisma') NOT NULL,
    bonus INT NOT NULL,
    PRIMARY KEY (id_raca, atributo),
    FOREIGN KEY (id_raca) REFERENCES raca(id_raca)
);

CREATE TABLE sub_raca_atributo (
    id_sub_raca INT NOT NULL,
    atributo ENUM('forca', 'destreza', 'constituicao', 'inteligencia', 'sabedoria', 'carisma') NOT NULL,
    bonus INT NOT NULL,
    PRIMARY KEY (id_sub_raca, atributo),
    FOREIGN KEY (id_sub_raca) REFERENCES sub_raca(id_sub_raca)
);

CREATE TABLE atributos (
    id_atributos INT PRIMARY KEY AUTO_INCREMENT,
    forca INT NOT NULL DEFAULT 10,
    destreza INT NOT NULL DEFAULT 10,
    constituicao INT NOT NULL DEFAULT 10,
    inteligencia INT NOT NULL DEFAULT 10,
    sabedoria INT NOT NULL DEFAULT 10,
    carisma INT NOT NULL DEFAULT 10,
    CHECK (forca BETWEEN 1 AND 30 AND destreza BETWEEN 1 AND 30 AND constituicao BETWEEN 1 AND 30
        AND inteligencia BETWEEN 1 AND 30 AND sabedoria BETWEEN 1 AND 30 AND carisma BETWEEN 1 AND 30)
);

CREATE TABLE pericia (
    id_pericia INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    atributo ENUM('forca', 'destreza', 'inteligencia', 'sabedoria', 'carisma') NOT NULL
);

CREATE TABLE item (
    id_item INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    id_criador INT,
    FOREIGN KEY (id_criador) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE magia (
    id_magia INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    id_criador INT,
    FOREIGN KEY (id_criador) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE classe (
    id_classe INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descricao VARCHAR(300) NOT NULL
);

CREATE TABLE subclasse (
    id_subclasse INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(300) NOT NULL,
    id_classe INT NOT NULL,
    UNIQUE (id_subclasse, id_classe),
    FOREIGN KEY (id_classe) REFERENCES classe(id_classe)
);

CREATE TABLE habilidade_classe (
    id_habilidade_classe INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    nivel INT NOT NULL,
    id_classe INT NOT NULL,
    FOREIGN KEY (id_classe) REFERENCES classe(id_classe)
);

CREATE TABLE habilidade_subclasse (
    id_habilidade_subclasse INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    nivel INT NOT NULL,
    id_subclasse INT NOT NULL,
    FOREIGN KEY (id_subclasse) REFERENCES subclasse(id_subclasse)
);

CREATE TABLE antecedente (
    id_antecedente INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NOT NULL,
    caracteristica_nome VARCHAR(100) NOT NULL,
    caracteristica_descricao TEXT NOT NULL,
    idiomas_adicionais INT NOT NULL DEFAULT 0,
    ferramentas_proficiencia VARCHAR(255),
    equipamento_inicial TEXT NOT NULL
);

CREATE TABLE antecedente_pericia (
    id_antecedente INT NOT NULL,
    id_pericia INT NOT NULL,
    PRIMARY KEY (id_antecedente, id_pericia),
    FOREIGN KEY (id_antecedente) REFERENCES antecedente(id_antecedente) ON DELETE CASCADE,
    FOREIGN KEY (id_pericia) REFERENCES pericia(id_pericia)
);

CREATE TABLE talento (
    id_talento INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE,
    pre_requisito VARCHAR(255),
    descricao TEXT NOT NULL,
    id_criador INT,
    FOREIGN KEY (id_criador) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE personagem (
    id_personagem INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    nivel INT NOT NULL DEFAULT 1,
    id_classe INT NOT NULL,
    id_subclasse INT,
    id_raca INT NOT NULL,
    id_sub_raca INT,
    id_antecedente INT,
    id_player INT NOT NULL,
    id_atributos INT NOT NULL UNIQUE,
    id_mesa INT,
    hp_atual INT NOT NULL DEFAULT 1,
    hp_maximo INT NOT NULL DEFAULT 1,
    ca INT NOT NULL DEFAULT 10,
    deslocamento INT NOT NULL DEFAULT 30,

    CHECK (nivel BETWEEN 1 AND 20),
    CHECK (hp_maximo >= 1 AND hp_atual >= 0 AND hp_atual <= hp_maximo),

    FOREIGN KEY (id_classe)
        REFERENCES classe(id_classe),

    FOREIGN KEY (id_subclasse, id_classe)
        REFERENCES subclasse(id_subclasse, id_classe),

    FOREIGN KEY (id_raca)
        REFERENCES raca(id_raca),

    FOREIGN KEY (id_sub_raca, id_raca)
        REFERENCES sub_raca(id_sub_raca, id_raca),

    FOREIGN KEY (id_antecedente)
        REFERENCES antecedente(id_antecedente),

    FOREIGN KEY (id_player)
        REFERENCES player(id_player),

    FOREIGN KEY (id_atributos)
        REFERENCES atributos(id_atributos),

    FOREIGN KEY (id_mesa)
        REFERENCES mesa(id_mesa)
);

CREATE TABLE personagem_pericia (
    id_personagem INT NOT NULL,
    id_pericia INT NOT NULL,
    proficiente BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_personagem, id_pericia),
    FOREIGN KEY (id_personagem) REFERENCES personagem(id_personagem) ON DELETE CASCADE,
    FOREIGN KEY (id_pericia) REFERENCES pericia(id_pericia)
);

CREATE TABLE personagem_magia (
    id_personagem INT NOT NULL,
    id_magia INT NOT NULL,
    PRIMARY KEY (id_personagem, id_magia),
    FOREIGN KEY (id_personagem) REFERENCES personagem(id_personagem) ON DELETE CASCADE,
    FOREIGN KEY (id_magia) REFERENCES magia(id_magia) ON DELETE CASCADE
);

CREATE TABLE personagem_talento (
    id_personagem INT NOT NULL,
    id_talento INT NOT NULL,
    PRIMARY KEY (id_personagem, id_talento),
    FOREIGN KEY (id_personagem) REFERENCES personagem(id_personagem) ON DELETE CASCADE,
    FOREIGN KEY (id_talento) REFERENCES talento(id_talento) ON DELETE CASCADE
);

CREATE TABLE inventario (
    id_inventario INT PRIMARY KEY AUTO_INCREMENT,
    id_personagem INT NOT NULL UNIQUE,
    FOREIGN KEY (id_personagem) REFERENCES personagem(id_personagem) ON DELETE CASCADE
);

CREATE TABLE inventario_item (
    id_inventario INT NOT NULL,
    id_item INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 1 CHECK (quantidade > 0),
    PRIMARY KEY (id_inventario, id_item),
    FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario) ON DELETE CASCADE,
    FOREIGN KEY (id_item) REFERENCES item(id_item) ON DELETE CASCADE
);

CREATE TABLE log_personagem (
    id_log INT PRIMARY KEY AUTO_INCREMENT,
    id_personagem INT NOT NULL,
    nivel_antigo INT NOT NULL,
    nivel_novo INT NOT NULL,
    hp_antigo INT NOT NULL,
    hp_novo INT NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    alterado_em DATETIME NOT NULL
);

CREATE INDEX idx_magia_nome ON magia(nome);
CREATE INDEX idx_item_nome ON item(nome);
CREATE INDEX idx_log_personagem_id_personagem ON log_personagem(id_personagem);
CREATE INDEX idx_talento_nome ON talento(nome);

CREATE TABLE monstro (
    id_monstro INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    tamanho ENUM('Miúdo', 'Pequeno', 'Médio', 'Grande', 'Enorme', 'Imenso') NOT NULL,
    tipo VARCHAR(100) NOT NULL,
    tendencia VARCHAR(100),
    ca INT NOT NULL DEFAULT 10,
    hp_maximo INT NOT NULL DEFAULT 1,
    deslocamento VARCHAR(100) NOT NULL,
    forca INT NOT NULL DEFAULT 10,
    destreza INT NOT NULL DEFAULT 10,
    constituicao INT NOT NULL DEFAULT 10,
    inteligencia INT NOT NULL DEFAULT 10,
    sabedoria INT NOT NULL DEFAULT 10,
    carisma INT NOT NULL DEFAULT 10,
    desafio VARCHAR(10) NOT NULL,
    xp INT NOT NULL DEFAULT 0,
    descricao TEXT NOT NULL,
    id_criador INT,

    CHECK (forca BETWEEN 1 AND 30 AND destreza BETWEEN 1 AND 30 AND constituicao BETWEEN 1 AND 30
        AND inteligencia BETWEEN 1 AND 30 AND sabedoria BETWEEN 1 AND 30 AND carisma BETWEEN 1 AND 30),
    CHECK (hp_maximo >= 1 AND ca >= 0 AND xp >= 0),

    FOREIGN KEY (id_criador) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

-- Tabela de instância de monstros adicionados pelo Mestre no campo de batalha da Mesa
CREATE TABLE mesa_monstro (
    id_mesa_monstro INT PRIMARY KEY AUTO_INCREMENT,
    id_mesa INT NOT NULL,
    id_monstro INT NOT NULL,
    id_mestre INT NOT NULL,
    apelido VARCHAR(100), -- Ex: 'Goblin Arqueiro 1', 'Goblin Chefe'
    hp_atual INT NOT NULL,
    hp_maximo INT NOT NULL,
    iniciativa INT DEFAULT 0,
    status ENUM('ativo', 'derrotado', 'fugiu') NOT NULL DEFAULT 'ativo',
    adicionado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (hp_maximo >= 1 AND hp_atual >= 0 AND hp_atual <= hp_maximo),

    FOREIGN KEY (id_mesa) REFERENCES mesa(id_mesa) ON DELETE CASCADE,
    FOREIGN KEY (id_monstro) REFERENCES monstro(id_monstro) ON DELETE RESTRICT,
    FOREIGN KEY (id_mestre) REFERENCES mestre(id_mestre) ON DELETE RESTRICT
);

CREATE INDEX idx_monstro_nome ON monstro(nome);
CREATE INDEX idx_mesa_monstro_mesa ON mesa_monstro(id_mesa);
CREATE INDEX idx_mesa_monstro_mestre ON mesa_monstro(id_mestre);

INSERT INTO pericia (nome, atributo) VALUES
('Acrobacia', 'destreza'),
('Adestrar Animais', 'sabedoria'),
('Arcanismo', 'inteligencia'),
('Atletismo', 'forca'),
('Atuação', 'carisma'),
('Enganação', 'carisma'),
('Furtividade', 'destreza'),
('História', 'inteligencia'),
('Intimidação', 'carisma'),
('Intuição', 'sabedoria'),
('Investigação', 'inteligencia'),
('Medicina', 'sabedoria'),
('Natureza', 'inteligencia'),
('Percepção', 'sabedoria'),
('Persuasão', 'carisma'),
('Prestidigitação', 'destreza'),
('Religião', 'inteligencia'),
('Sobrevivência', 'sabedoria');

-- CLASSES (Livro do Jogador, ordem do livro)
INSERT INTO classe (nome, descricao) VALUES
('Bárbaro', 'Um feroz guerreiro de origem primitiva que pode entrar em fúria durante uma batalha.'),
('Bardo', 'Um místico inspirador que possui poderes que ecoam a música da criação.'),
('Bruxo', 'Um portador de magia derivada de barganha com uma entidade planar.'),
('Clérigo', 'Um campeão sacerdotal que empunha magia divina a serviço de um poder maior.'),
('Druida', 'Um sacerdote da Crença Antiga, detentor dos poderes da natureza e capaz de adotar formas animais.'),
('Feiticeiro', 'Um conjurador que possui magia latente advinda de um dom ou linhagem.'),
('Guerreiro', 'Um mestre do combate, perito em uma vasta gama de armas e armaduras.'),
('Ladino', 'Um trapaceiro que utiliza de furtividade e astúcia para sobrepujar os obstáculos e inimigos.'),
('Mago', 'Um usuário de magia escolado, capaz de manipular as estruturas da realidade.'),
('Monge', 'Um mestre das artes marciais, utilizando o poder corporal para atingir a perfeição física e espiritual.'),
('Paladino', 'Um guerreiro divino vinculado a um juramento sagrado.'),
('Patrulheiro', 'Um guerreiro que utiliza de poderio marcial e magia natural para combater ameaças nos limites da civilização.');

-- SUBCLASSES (Livro do Jogador)
INSERT INTO subclasse (nome, descricao, id_classe) VALUES
-- Bárbaro (Caminho Primitivo)
('Caminho do Furioso', 'Bárbaro para quem a fúria é um meio para a violência, entrando em frenesi e atacando com mais frequência em batalha.', (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Caminho do Guerreiro Totêmico', 'Jornada espiritual em que o bárbaro aceita um espírito animal (águia, lobo ou urso) como guia e protetor.', (SELECT id_classe FROM classe WHERE nome='Bárbaro')),

-- Bardo (Colégio de Bardo)
('Colégio do Conhecimento', 'Bardos que colecionam saberes de fontes diversas e usam a perspicácia para atrapalhar a confiança dos outros.', (SELECT id_classe FROM classe WHERE nome='Bardo')),
('Colégio da Bravura', 'Escaldos destemidos que cantam os feitos dos heróis, inspirando aliados em combate e lutando ao lado deles.', (SELECT id_classe FROM classe WHERE nome='Bardo')),

-- Bruxo (Patrono Transcendental)
('A Arquifada', 'Pacto com um senhor ou senhora das fadas, com magias de sedução, ilusão e fuga.', (SELECT id_classe FROM classe WHERE nome='Bruxo')),
('O Corruptor', 'Pacto com um corruptor dos planos inferiores, que concede poder destrutivo, fogo e resistência.', (SELECT id_classe FROM classe WHERE nome='Bruxo')),
('O Grande Antigo', 'Pacto com uma entidade alienígena e misteriosa, que concede poderes mentais e telepatia.', (SELECT id_classe FROM classe WHERE nome='Bruxo')),

-- Clérigo (Domínio Divino)
('Domínio do Conhecimento', 'Clérigos de deuses que valorizam o estudo e a compreensão acima de tudo, com idiomas, perícias e leitura de pensamentos.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Enganação', 'Clérigos de deuses trapaceiros, que criam duplicatas ilusórias e se tornam invisíveis.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Guerra', 'Clérigos de deuses da guerra, que se destacam em batalha e recebem proficiência marcial e armaduras pesadas.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Luz', 'Clérigos de deuses da luz, que usam radiação divina para banir a escuridão e punir inimigos.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Natureza', 'Clérigos de deuses da natureza, que enfeitiçam animais e plantas e usam armaduras pesadas.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Tempestade', 'Clérigos de deuses da tormenta, que comandam o trovão e o relâmpago.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),
('Domínio da Vida', 'Clérigos focados em vitalidade e saúde, cujas magias de cura são mais efetivas.', (SELECT id_classe FROM classe WHERE nome='Clérigo')),

-- Druida (Círculo Druídico)
('Círculo da Terra', 'Místicos e sábios que guardam ritos antigos; recuperam magia e ganham magias ligadas ao terreno de sua iniciação.', (SELECT id_classe FROM classe WHERE nome='Druida')),
('Círculo da Lua', 'Ferrenhos guardiões da natureza, mestres em transformar-se em formas animais mais poderosas.', (SELECT id_classe FROM classe WHERE nome='Druida')),

-- Feiticeiro (Origem de Feitiçaria)
('Linhagem Dracônica', 'Magia inata vinda de sangue de dragão, que concede escamas, resiliência, asas e afinidade elemental.', (SELECT id_classe FROM classe WHERE nome='Feiticeiro')),
('Magia Selvagem', 'Magia inata vinda de forças caóticas, com surtos de magia selvagem aleatórios e controle sobre o acaso.', (SELECT id_classe FROM classe WHERE nome='Feiticeiro')),

-- Guerreiro (Arquétipo Marcial)
('Campeão', 'Foco na pura força física e perfeição mortal, com acertos críticos mais frequentes.', (SELECT id_classe FROM classe WHERE nome='Guerreiro')),
('Cavaleiro Arcano', 'Une a maestria marcial a magias de abjuração e evocação, além de vincular uma arma ao guerreiro.', (SELECT id_classe FROM classe WHERE nome='Guerreiro')),
('Mestre de Batalha', 'Guerreiro que emprega técnicas marciais tradicionais, usando manobras e dados de superioridade.', (SELECT id_classe FROM classe WHERE nome='Guerreiro')),

-- Ladino (Arquétipo de Ladino)
('Assassino', 'Especialista em emboscadas e morte, com subterfúgio, veneno e disfarces para eliminar oponentes.', (SELECT id_classe FROM classe WHERE nome='Ladino')),
('Ladrão', 'Especialista no furto de pequenos itens, ágil, furtivo e hábil em usar itens mágicos.', (SELECT id_classe FROM classe WHERE nome='Ladino')),
('Trapaceiro Arcano', 'Ladino que une furtividade e agilidade a magias de encantamento e ilusão.', (SELECT id_classe FROM classe WHERE nome='Ladino')),

-- Mago (Tradição Arcana: as oito escolas)
('Escola de Abjuração', 'Magias que bloqueiam, expulsam ou protegem; cria uma proteção arcana que absorve dano.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Adivinhação', 'Magias de discernimento, visão remota e previsão; o mago rola dados de premonição.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Conjuração', 'Magias que produzem objetos e criaturas do nada, além de teletransporte.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Encantamento', 'Magias que seduzem e controlam outras pessoas e monstros.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Evocação', 'Magias de efeitos elementais poderosos, que podem poupar aliados de suas próprias magias.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Ilusão', 'Magias que ofuscam os sentidos, confundem a mente e enganam até os mais sábios.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Necromancia', 'Magias sobre as forças da vida, morte e morte-vida, incluindo drenar energia vital e criar mortos-vivos.', (SELECT id_classe FROM classe WHERE nome='Mago')),
('Escola de Transmutação', 'Magias que modificam energia e matéria, como transformar substâncias e criar a pedra de transmutador.', (SELECT id_classe FROM classe WHERE nome='Mago')),

-- Monge (Tradição Monástica)
('Caminho da Mão Aberta', 'Mestres supremos do combate marcial, que empurram e derrubam oponentes e manipulam o chi para curar.', (SELECT id_classe FROM classe WHERE nome='Monge')),
('Caminho da Sombra', 'Tradição de furtividade e subterfúgio, como ninjas e dançarinos das sombras, espiões e assassinos.', (SELECT id_classe FROM classe WHERE nome='Monge')),
('Caminho dos Quatro Elementos', 'Tradição que ensina a dominar os elementos, com disciplinas mágicas alimentadas por pontos de chi.', (SELECT id_classe FROM classe WHERE nome='Monge')),

-- Paladino (Juramento Sagrado)
('Juramento de Devoção', 'Os mais sublimes ideais de justiça, virtude e ordem: o cavaleiro sagrado em armadura brilhante.', (SELECT id_classe FROM classe WHERE nome='Paladino')),
('Juramento dos Anciões', 'Cavaleiros feéricos que protegem a luz e a vida, preservando as coisas belas e vivificantes do mundo.', (SELECT id_classe FROM classe WHERE nome='Paladino')),
('Juramento de Vingança', 'Compromisso solene de punir quem cometeu pecados graves, a qualquer custo.', (SELECT id_classe FROM classe WHERE nome='Paladino')),

-- Patrulheiro (Conclave de Patrulheiro)
('Conclave da Besta', 'Patrulheiros que criam um vínculo poderoso com uma besta, o companheiro animal.', (SELECT id_classe FROM classe WHERE nome='Patrulheiro')),
('Conclave do Caçador', 'Patrulheiros que dominam técnicas de luta contra as mais terríveis ameaças do ermo.', (SELECT id_classe FROM classe WHERE nome='Patrulheiro')),
('Conclave do Rastreador Subterrâneo', 'Patrulheiros que descem ao Subterrâneo para derrotar ameaças antes que cheguem à superfície.', (SELECT id_classe FROM classe WHERE nome='Patrulheiro'));

INSERT INTO magia (nome, descricao) VALUES
('Amizade', 'Truque de encantamento. Vantagem em testes de Carisma contra uma criatura não hostil; ao fim ela percebe e fica hostil.'),
('Ataque Certeiro', 'Truque de adivinhação. Vantagem na primeira jogada de ataque contra o alvo no seu próximo turno.'),
('Bordão Místico', 'Truque de transmutação. Clava ou bordão usa sua habilidade de conjuração, causa d8 e se torna mágico.'),
('Chama Sagrada', 'Truque de evocação. Radiação causa 1d8 de dano radiante; o alvo não recebe benefício de cobertura.'),
('Chicote de Espinhos', 'Truque de transmutação. Ataque que causa 1d6 de dano perfurante e puxa criatura Grande ou menor até 3 m.'),
('Consertar', 'Truque de transmutação. Repara uma quebra ou fissura de até 30 cm em um objeto.'),
('Criar Chamas', 'Truque de conjuração. Chama na sua mão que ilumina e pode ser arremessada para causar 1d8 de dano de fogo.'),
('Druidismo', 'Truque de transmutação. Efeitos naturais pequenos: prever o clima, florescer uma planta, efeito sensorial ou acender/apagar chama.'),
('Espirro Ácido', 'Truque de conjuração. Bolha de ácido que causa 1d6 de dano ácido a uma ou duas criaturas.'),
('Estabilizar', 'Truque de necromancia. Estabiliza uma criatura viva com 0 pontos de vida.'),
('Globos de Luz', 'Truque de evocação. Até quatro luzes do tamanho de tochas que você pode mover com ação bônus.'),
('Ilusão Menor', 'Truque de ilusão. Cria um som ou a imagem de um objeto de até 1,5 m cúbico por 1 minuto.'),
('Luz', 'Truque de evocação. Um objeto tocado emite luz plena num raio de 6 m por 1 hora.'),
('Mãos Mágicas', 'Truque de conjuração. Mão espectral que manipula objetos e carrega até 5 kg.'),
('Mensagem', 'Truque de transmutação. Sussurra uma mensagem a uma criatura, que responde em sussurro.'),
('Orientação', 'Truque de adivinhação. O alvo pode somar 1d4 a um teste de habilidade.'),
('Prestidigitação', 'Truque de transmutação. Truque mágico simples com vários efeitos menores.'),
('Proteção contra Lâminas', 'Truque de abjuração. Resistência a dano de concussão, cortante e perfurante de armas até o fim do próximo turno.'),
('Raio de Fogo', 'Truque de evocação. Ataque à distância que causa 1d10 de dano de fogo.'),
('Raio de Gelo', 'Truque de evocação. Ataque à distância que causa 1d8 de dano de frio e reduz o deslocamento em 3 m.'),
('Rajada de Veneno', 'Truque de conjuração. Sopro de gás tóxico que causa 1d12 de dano de veneno.'),
('Rajada Mística', 'Truque de evocação. Feixe que causa 1d10 de dano de energia; mais feixes em níveis altos.'),
('Resistência', 'Truque de abjuração. O alvo pode somar 1d4 a um teste de resistência.'),
('Taumaturgia', 'Truque de transmutação. Pequenas maravilhas: voz ampliada, tremores, sons, abrir portas ou mudar os olhos.'),
('Toque Arrepiante', 'Truque de necromancia. Ataque à distância que causa 1d8 de dano necrótico e impede cura até o próximo turno.'),
('Toque Chocante', 'Truque de evocação. Ataque corpo a corpo que causa 1d8 de dano elétrico e impede reações.'),
('Zombaria Viciosa', 'Truque de encantamento. Insultos causam 1d4 de dano psíquico e impõem desvantagem no próximo ataque.'),
('Alarme', '1º nível, abjuração (ritual). Alerta, de forma mental ou audível, quando uma criatura Miúda ou maior entra na área protegida. Dura 8 horas.'),
('Amizade Animal', '1º nível, encantamento. Besta de Inteligência 3 ou menos fica enfeitiçada por 24 horas se falhar em Sabedoria.'),
('Área Escorregadia', '1º nível, conjuração. Graxa cobre um quadrado de 3 m, tornando-o terreno difícil e derrubando criaturas.'),
('Armadura Arcana', '1º nível, abjuração. A CA base de uma criatura voluntária sem armadura vira 13 + Destreza por 8 horas.'),
('Armadura de Agathys', '1º nível, abjuração. Concede 5 pontos de vida temporários; quem a atingir corpo a corpo sofre 5 de dano de frio.'),
('Auxílio Divino', '1º nível, evocação. Seus ataques com arma causam 1d4 de dano radiante extra.'),
('Bênção', '1º nível, encantamento. Até três criaturas somam 1d4 em jogadas de ataque e testes de resistência.'),
('Bom Fruto', '1º nível, transmutação. Cria até dez frutos mágicos; cada um restaura 1 PV e sustenta uma criatura por um dia.'),
('Braços de Hadar', '1º nível, conjuração. Tentáculos atingem criaturas a até 3 m: 2d6 de dano necrótico e sem reações.'),
('Bruxaria', '1º nível, encantamento. Maldição: 1d6 de dano necrótico extra e desvantagem em testes de uma habilidade escolhida.'),
('Comando', '1º nível, encantamento. Uma palavra de comando que a criatura obedece no próximo turno se falhar em Sabedoria.'),
('Compreender Idiomas', '1º nível, adivinhação (ritual). Compreende qualquer idioma falado ou escrito (tocando o texto) por 1 hora.'),
('Constrição', '1º nível, conjuração. Ervas e vinhas brotam do solo, criando terreno difícil e impedindo criaturas que falharem em Força.'),
('Convocar Familiar', '1º nível, conjuração (ritual). Adquire um familiar, espírito em forma de animal, que age independentemente de você.'),
('Criar ou Destruir Água', '1º nível, transmutação. Cria ou destrói 30 litros de água ou nevoeiro.'),
('Curar Ferimentos', '1º nível, evocação. Uma criatura tocada recupera 1d8 + modificador de habilidade de conjuração em pontos de vida.'),
('Destruição Colérica', '1º nível, evocação. Próximo acerto corpo a corpo causa 1d6 psíquico extra e pode amedrontar o alvo.'),
('Destruição Lancinante', '1º nível, evocação. Próximo acerto corpo a corpo causa 1d6 de fogo extra e incendeia o alvo.'),
('Destruição Trovejante', '1º nível, evocação. Próximo acerto corpo a corpo causa 2d6 trovejante extra e pode empurrar e derrubar.'),
('Detectar Magia', '1º nível, adivinhação (ritual). Sente a presença de magia a até 9 m e pode ver a aura de itens e criaturas.'),
('Detectar o Bem e Mal', '1º nível, adivinhação. Detecta aberrações, celestiais, corruptores, elementais, fadas e mortos-vivos a até 9 m.'),
('Detectar Veneno e Doença', '1º nível, adivinhação (ritual). Sente venenos, criaturas venenosas e doenças a até 9 m.'),
('Disco Flutuante de Tenser', '1º nível, conjuração (ritual). Disco de energia que flutua e carrega até 250 kg, seguindo você.'),
('Disfarçar-se', '1º nível, ilusão. Muda sua aparência e a de seus pertences por 1 hora.'),
('Duelo Compelido', '1º nível, encantamento. Compele uma criatura a duelar com você, com desvantagem contra outros e restrição de movimento.'),
('Enfeitiçar Pessoa', '1º nível, encantamento. Enfeitiça um humanoide que falhar em Sabedoria por 1 hora.'),
('Escrita Ilusória', '1º nível, ilusão (ritual). Escreve um texto que só você e criaturas designadas conseguem ler.'),
('Escudo Arcano', '1º nível, abjuração. Reação que dá +5 de CA e imunidade a mísseis mágicos até seu próximo turno.'),
('Escudo da Fé', '1º nível, abjuração. Campo cintilante que dá +2 de CA a uma criatura.'),
('Falar com Animais', '1º nível, adivinhação (ritual). Compreende e se comunica verbalmente com bestas por 10 minutos.'),
('Falar com Plantas', '1º nível, adivinhação (ritual). Dá consciência limitada às plantas num raio de 9 m, permitindo se comunicar com elas.'),
('Fogo das Fadas', '1º nível, evocação. Delineia objetos e criaturas com luz, dando vantagem em ataques contra eles.'),
('Golpe Constritor', '1º nível, conjuração. Próximo acerto com arma cria vinhas espinhentas que impedem o alvo e causam 1d6 de dano.'),
('Heroísmo', '1º nível, encantamento. Criatura imune a medo que ganha pontos de vida temporários a cada turno.'),
('Identificação', '1º nível, adivinhação (ritual). Descobre as propriedades de um item mágico ou as magias que afetam uma criatura.'),
('Imagem Silenciosa', '1º nível, ilusão. Imagem visual de até 4,5 m cúbicos, sem som ou outros efeitos.'),
('Infligir Ferimentos', '1º nível, necromancia. Ataque corpo a corpo com magia que causa 3d10 de dano necrótico.'),
('Leque Cromático', '1º nível, ilusão. Luzes coloridas em cone de 4,5 m cegam criaturas conforme o total de pontos de vida.'),
('Mãos Flamejantes', '1º nível, evocação. Cone de 4,5 m de chamas que causa 3d6 de dano de fogo.'),
('Marca do Caçador', '1º nível, adivinhação. Marca uma criatura: 1d6 de dano extra e vantagem para encontrá-la.'),
('Mísseis Mágicos', '1º nível, evocação. Três dardos de energia que causam 1d4 + 1 de dano cada.'),
('Névoa Obscurecente', '1º nível, conjuração. Esfera de névoa de 6 m de raio, com escuridão densa.'),
('Onda Trovejante', '1º nível, evocação. Cubo de 4,5 m que causa 2d8 de dano trovejante e empurra criaturas.'),
('Orbe Cromática', '1º nível, evocação. Esfera de energia que causa 3d8 de dano de um tipo elemental à escolha.'),
('Palavra Curativa', '1º nível, evocação. Ação bônus: uma criatura recupera 1d4 + modificador de pontos de vida.'),
('Passos Longos', '1º nível, transmutação. Aumenta o deslocamento do alvo em 3 m por 1 hora.'),
('Perdição', '1º nível, encantamento. Até três criaturas subtraem 1d4 de ataques e resistências se falharem em Carisma.'),
('Proteção contra o Bem e Mal', '1º nível, abjuração. Protege o alvo de aberrações, celestiais, corruptores, elementais, fadas e mortos-vivos.'),
('Purificar Alimentos', '1º nível, transmutação (ritual). Purifica comida e bebida não mágicas de venenos e doenças.'),
('Queda Suave', '1º nível, transmutação. Reação que reduz a queda de até cinco criaturas a 18 m por rodada, sem dano.'),
('Raio Adoecente', '1º nível, necromancia. Ataque à distância que causa 2d8 de dano de veneno e pode envenenar.'),
('Raio de Bruxa', '1º nível, evocação. Arco elétrico que causa 1d12 de dano elétrico por turno.'),
('Raio Guiador', '1º nível, evocação. Ataque à distância que causa 4d6 de dano radiante e dá vantagem ao próximo ataque.'),
('Recuo Acelerado', '1º nível, transmutação. Ação bônus: você pode realizar a ação de Disparada.'),
('Repreensão Infernal', '1º nível, evocação. Reação: envolve em chamas quem lhe causou dano, com 2d10 de dano de fogo.'),
('Riso Histérico de Tasha', '1º nível, encantamento. A criatura cai na gargalhada e fica incapacitada se falhar em Sabedoria.'),
('Salto', '1º nível, transmutação. Triplica a distância de salto do alvo.'),
('Santuário', '1º nível, abjuração. Protege uma criatura: atacantes precisam passar em Sabedoria para escolher outro alvo.'),
('Saraivada de Espinhos', '1º nível, conjuração. Próximo acerto à distância cria chuva de espinhos que causa 1d10 de dano perfurante em área.'),
('Servo Invisível', '1º nível, conjuração (ritual). Força invisível e sem mente que realiza tarefas simples.'),
('Sono', '1º nível, encantamento. Põe criaturas a dormir conforme um total de 5d8 de pontos de vida.'),
('Sussurros Dissonantes', '1º nível, encantamento. Causa 3d6 de dano psíquico e força o alvo a se afastar.'),
('Vitalidade Falsa', '1º nível, necromancia. Você ganha 1d4 + 4 pontos de vida temporários por 1 hora.'),
('Acalmar Emoções', '2º nível, encantamento. Suprime emoções fortes de humanoides numa esfera de 6 m: encerra enfeitiçado/amedrontado ou torna o alvo indiferente a hostis.'),
('Ajuda', '2º nível, abjuração. Até três criaturas aumentam em 5 o máximo e os atuais pontos de vida por 8 horas.'),
('Alterar-se', '2º nível, transmutação. Escolha: adaptação aquática, mudar aparência ou armas naturais. Dura até 1 hora.'),
('Aprimorar Habilidade', '2º nível, transmutação. Concede vantagem em testes de uma habilidade, com benefício extra conforme a opção escolhida.'),
('Arma Espiritual', '2º nível, evocação. Arma espectral que ataca com ação bônus, causando 1d8 + modificador de dano de energia.'),
('Arma Mágica', '2º nível, transmutação. Uma arma não mágica ganha +1 nas jogadas de ataque e dano.'),
('Arrombar', '2º nível, transmutação. Destranca ou destrava um objeto e suprime tranca arcana por 10 minutos; emite uma batida audível.'),
('Augúrio', '2º nível, adivinhação (ritual). Recebe um presságio sobre o resultado de uma ação planejada para os próximos 30 minutos.'),
('Aumentar/Reduzir', '2º nível, transmutação. Aumenta ou reduz o tamanho de uma criatura ou objeto, com bônus ou penalidades em Força.'),
('Aura Mágica de Nystul', '2º nível, ilusão. Ilusão em criatura ou objeto que dá informações falsas a magias de adivinhação. Dura 24 horas.'),
('Boca Encantada', '2º nível, ilusão (ritual). Implanta em um objeto uma mensagem que é falada quando uma condição é satisfeita.'),
('Cativar', '2º nível, encantamento. Criaturas escolhidas ficam com desvantagem em Percepção para notar qualquer criatura além de você.'),
('Cegueira/Surdez', '2º nível, necromancia. Cega ou ensurdece uma criatura que falhar em Constituição.'),
('Chama Contínua', '2º nível, evocação. Chama sem calor, como uma tocha, que dura até ser dissipada.'),
('Convocar Montaria', '2º nível, conjuração. Convoca um espírito em forma de montaria inteligente e leal.'),
('Cordão de Flechas', '2º nível, transmutação. Quatro munições protegem uma área e voam contra criaturas que se aproximam, causando 1d6 perfurante.'),
('Coroa da Loucura', '2º nível, encantamento. Um humanoide enfeitiçado ataca criaturas que você escolher mentalmente.'),
('Crescer Espinhos', '2º nível, transmutação. Área de 6 m de raio vira terreno difícil e causa 2d4 de dano perfurante por 1,5 m percorrido.'),
('Despedaçar', '2º nível, evocação. Som estridente numa esfera de 3 m que causa 3d8 de dano trovejante.'),
('Detectar Pensamentos', '2º nível, adivinhação. Lê os pensamentos de criaturas a até 9 m, podendo sondar mais fundo.'),
('Encontrar Armadilhas', '2º nível, adivinhação. Sente a presença de armadilhas dentro do alcance e na sua linha de visão.'),
('Escuridão', '2º nível, evocação. Escuridão mágica numa esfera de 4,5 m de raio.'),
('Esfera Flamejante', '2º nível, conjuração. Esfera de fogo móvel que causa 2d6 de dano de fogo a criaturas próximas.'),
('Esquentar Metal', '2º nível, transmutação. Objeto de metal fica incandescente e causa 2d8 de dano de fogo.'),
('Flecha Ácida de Melf', '2º nível, evocação. Flecha de ácido que causa 4d4 de dano imediato e 2d4 no fim do próximo turno do alvo.'),
('Força Fantasmagórica', '2º nível, ilusão. Ilusão na mente de uma criatura que pode lhe causar 1d6 de dano psíquico por turno.'),
('Imobilizar Pessoa', '2º nível, encantamento. Paralisa um humanoide que falhar em Sabedoria.'),
('Invisibilidade', '2º nível, ilusão. Torna a criatura tocada invisível até ela atacar ou conjurar magia.'),
('Lâmina Flamejante', '2º nível, evocação. Evoca uma lâmina ardente que causa 3d6 de dano de fogo em ataques corpo a corpo com magia.'),
('Levitação', '2º nível, transmutação. Uma criatura ou objeto de até 250 kg flutua até 6 m do chão.'),
('Localizar Animais ou Plantas', '2º nível, adivinhação (ritual). Descobre a direção e a distância do animal ou planta de um tipo descrito, a até 7,5 km.'),
('Localizar Objeto', '2º nível, adivinhação. Sente a direção de um objeto familiar a até 300 m.'),
('Lufada de Vento', '2º nível, evocação. Linha de vento forte de 18 m que empurra criaturas e apaga chamas.'),
('Marca da Punição', '2º nível, evocação. Próximo acerto com arma causa 2d6 radiante extra e revela o alvo invisível.'),
('Mensageiro Animal', '2º nível, encantamento (ritual). Usa uma besta Miúda para entregar uma mensagem de até 25 palavras.'),
('Nublar', '2º nível, ilusão. Seu corpo fica turvo e criaturas atacam você com desvantagem.'),
('Nuvem de Adagas', '2º nível, conjuração. Adagas giratórias num cubo de 1,5 m que causam 4d4 de dano cortante.'),
('Oração Curativa', '2º nível, evocação. Até seis criaturas recuperam 2d8 + modificador de pontos de vida.'),
('Passo Nebuloso', '2º nível, conjuração. Ação bônus: teletransporta você até 9 m para um espaço visível.'),
('Passos sem Pegadas', '2º nível, abjuração. Você e criaturas até 9 m recebem +10 em Furtividade e não deixam rastros.'),
('Patas de Aranha', '2º nível, transmutação. O alvo escala superfícies verticais e tetos com deslocamento de escalada.'),
('Pele de Árvore', '2º nível, transmutação. A CA do alvo não pode ser inferior a 16.'),
('Proteção contra Veneno', '2º nível, abjuração. Neutraliza um veneno e dá vantagem e resistência a veneno por 1 hora.'),
('Raio Ardente', '2º nível, evocação. Três raios de fogo, cada um causando 2d6 de dano de fogo.'),
('Raio do Enfraquecimento', '2º nível, necromancia. Ataque à distância que faz o alvo causar metade do dano com ataques de Força.'),
('Raio Lunar', '2º nível, evocação. Raio prateado em cilindro que causa 2d10 de dano radiante.'),
('Reflexos', '2º nível, ilusão. Três duplicatas ilusórias confundem ataques contra você.'),
('Repouso Tranquilo', '2º nível, necromancia (ritual). Protege um corpo da decomposição e de virar morto-vivo por 10 dias.'),
('Restauração Menor', '2º nível, abjuração. Encerra uma doença ou uma condição: cego, surdo, paralisado ou envenenado.'),
('Sentido Bestial', '2º nível, adivinhação (ritual). Você usa os sentidos de uma besta voluntária.'),
('Silêncio', '2º nível, ilusão (ritual). Esfera de 6 m onde nenhum som pode ser criado ou atravessar.'),
('Sugestão', '2º nível, encantamento. Sugere um curso de atividade que o alvo segue se falhar em Sabedoria.'),
('Teia', '2º nível, conjuração. Massa de teias num cubo de 6 m que impede criaturas.'),
('Tranca Arcana', '2º nível, abjuração. Tranca uma porta, janela ou baú até ser dissipada.'),
('Truque de Corda', '2º nível, transmutação. Uma corda sobe e abre um espaço extradimensional para até oito criaturas.'),
('Ver o Invisível', '2º nível, adivinhação. Você vê criaturas e objetos invisíveis e o Plano Etéreo por 1 hora.'),
('Vínculo Protetor', '2º nível, abjuração. O alvo recebe +1 de CA e resistências e resistência a dano, mas você sofre o dano que ele sofrer.'),
('Visão no Escuro', '2º nível, transmutação. Concede visão no escuro de 18 m por 8 horas.'),
('Zona da Verdade', '2º nível, encantamento. Esfera de 4,5 m onde criaturas que falharem em Carisma não podem mentir.'),
('Ampliar Plantas', '3º nível, transmutação. Plantas ficam espessas e atrasam o movimento, ou a terra é enriquecida e rende o dobro de comida.'),
('Andar na Água', '3º nível, transmutação (ritual). Até dez criaturas caminham sobre superfícies líquidas como se fossem chão sólido por 1 hora.'),
('Animar Mortos', '3º nível, necromancia. Ergue ossos ou um cadáver como esqueleto ou zumbi sob seu controle por 24 horas.'),
('Arma Elemental', '3º nível, transmutação. Arma não mágica fica mágica, com +1 de ataque e 1d4 de dano elemental extra.'),
('Aura de Vitalidade', '3º nível, evocação. Aura de 9 m; com ação bônus, uma criatura na aura recupera 2d6 pontos de vida.'),
('Bola de Fogo', '3º nível, evocação. Explosão em esfera de 6 m de raio que causa 8d6 de dano de fogo.'),
('Círculo Mágico', '3º nível, abjuração. Cilindro que impede criaturas de um tipo (celestial, corruptor etc.) de entrar ou atacar a partir dele.'),
('Clarividência', '3º nível, adivinhação. Cria um sensor invisível a até 1,5 km para ver ou ouvir.'),
('Conjurar Animais', '3º nível, conjuração. Invoca espíritos feéricos em forma de bestas de acordo com o nível de desafio total.'),
('Conjurar Rajada', '3º nível, conjuração. Dispara munição criando um cone de 18 m que causa 3d8 de dano do tipo da arma.'),
('Contramágica', '3º nível, abjuração. Reação que interrompe uma criatura conjurando uma magia.'),
('Convocar Relâmpagos', '3º nível, conjuração. Nuvem tempestuosa que dispara relâmpagos de 3d10 de dano elétrico a cada turno.'),
('Criar Alimentos', '3º nível, conjuração. Cria 25 kg de comida e 100 litros de água, suficientes para até quinze humanoides por 24 horas.'),
('Destruição Cegante', '3º nível, evocação. Próximo acerto com arma causa 3d8 radiante extra e pode cegar o alvo.'),
('Dificultar Detecção', '3º nível, abjuração. Esconde o alvo de magias de adivinhação e sensores de vidência por 8 horas.'),
('Dissipar Magia', '3º nível, abjuração. Encerra magias no alvo; as de 4º nível ou mais exigem teste de habilidade.'),
('Enviar Mensagem', '3º nível, evocação. Envia uma mensagem de até 25 palavras a uma criatura familiar, a qualquer distância.'),
('Espíritos Guardiões', '3º nível, conjuração. Espíritos ao redor causam 3d8 de dano radiante ou necrótico e reduzem o deslocamento.'),
('Falar com os Mortos', '3º nível, necromancia. Dá a um corpo a capacidade de responder até cinco perguntas.'),
('Flecha Relampejante', '3º nível, transmutação. Ataque à distância vira relâmpago, causando 4d8 elétrico e dano em área.'),
('Fome de Hadar', '3º nível, conjuração. Esfera de negritude e frio que causa 2d6 de dano de frio e ácido e cega criaturas.'),
('Forjar Morte', '3º nível, necromancia (ritual). Coloca uma criatura voluntária em estado catatônico que parece morte.'),
('Forma Gasosa', '3º nível, transmutação. Transforma uma criatura voluntária em nuvem nebulosa com voo de 3 m.'),
('Glifo de Vigilância', '3º nível, abjuração. Inscreve um glifo de runas explosivas (5d8) ou que armazena uma magia de até 3º nível.'),
('Idiomas', '3º nível, adivinhação. O alvo compreende e fala qualquer idioma que ouvir, e todos o compreendem.'),
('Imagem Maior', '3º nível, ilusão. Imagem de até 6 m cúbicos com som, cheiro e temperatura, que você pode mover.'),
('Lentidão', '3º nível, transmutação. Até seis criaturas têm deslocamento reduzido, -2 de CA e limitações de ação.'),
('Luz do Dia', '3º nível, evocação. Esfera de luz de 18 m de raio que dura 1 hora.'),
('Manto do Cruzado', '3º nível, evocação. Aura de 9 m: criaturas não hostis causam 1d4 de dano radiante extra com armas.'),
('Medo', '3º nível, ilusão. Cone de 9 m: criaturas largam o que seguram e ficam amedrontadas se falhar em Sabedoria.'),
('Mesclar-se às Rochas', '3º nível, transmutação (ritual). Funde-se com uma rocha grande o bastante por 8 horas.'),
('Montaria Fantasmagórica', '3º nível, ilusão (ritual). Cria uma montaria quase real e Grande por 1 hora.'),
('Muralha de Vento', '3º nível, evocação. Muralha de ventos fortes que causa 3d8 de concussão e desvia projéteis e gases.'),
('Nevasca', '3º nível, conjuração. Chuva congelante e neve num cilindro de 12 m de raio: escuridão densa e gelo escorregadio.'),
('Névoa Fétida', '3º nível, conjuração. Esfera de gás nauseante de 6 m; criaturas falham em Constituição e perdem a ação.'),
('Padrão Hipnótico', '3º nível, ilusão. Padrão de cores num cubo de 9 m que enfeitiça e incapacita criaturas.'),
('Palavra Curativa em Massa', '3º nível, evocação. Ação bônus: até seis criaturas recuperam 1d4 + modificador de pontos de vida.'),
('Pequena Cabana de Leomund', '3º nível, evocação (ritual). Domo imóvel de energia que abriga até nove criaturas Médias por 8 horas.'),
('Piscar', '3º nível, transmutação. Você pode desaparecer para o Plano Etéreo no fim de cada turno com resultado 11 ou mais.'),
('Proteção contra Energia', '3º nível, abjuração. Resistência a um tipo de dano: ácido, elétrico, fogo, frio ou trovejante.'),
('Relâmpago', '3º nível, evocação. Linha de 30 m que causa 8d6 de dano elétrico.'),
('Remover Maldição', '3º nível, abjuração. Encerra todas as maldições que afetam uma criatura ou objeto.'),
('Respirar na Água', '3º nível, transmutação (ritual). Até dez criaturas respiram debaixo d agua por 24 horas.'),
('Revivificar', '3º nível, necromancia. Traz à vida uma criatura que morreu há até 1 minuto, com 1 PV.'),
('Rogar Maldição', '3º nível, necromancia. Amaldiçoa uma criatura tocada, com um entre quatro efeitos.'),
('Sinal de Esperança', '3º nível, abjuração. Alvos têm vantagem em resistências de Sabedoria e contra a morte e curam o máximo possível.'),
('Toque Vampírico', '3º nível, necromancia. Ataque corpo a corpo que causa 3d6 de dano necrótico e cura metade do dano em você.'),
('Velocidade', '3º nível, transmutação. Dobra o deslocamento do alvo, +2 de CA e uma ação adicional por turno.'),
('Voo', '3º nível, transmutação. O alvo ganha deslocamento de voo de 18 m por até 10 minutos.'),
('Adivinhação', '4º nível, adivinhação (ritual). Contato com uma divindade para fazer uma pergunta sobre um evento dos próximos 7 dias.'),
('Arca Secreta de Leomund', '4º nível, conjuração. Esconde um baú e seu conteúdo no Plano Etéreo, podendo ser chamado de volta com uma réplica.'),
('Assassino Fantasmagórico', '4º nível, ilusão. Manifesta os medos da criatura: ela fica amedrontada e sofre 4d10 psíquico por turno se falhar em Sabedoria.'),
('Aura de Pureza', '4º nível, abjuração. Aura de 9 m: impede doenças, dá resistência a veneno e vantagem contra várias condições.'),
('Aura de Vida', '4º nível, abjuração. Aura de 9 m: resistência a necrótico, PV máximo não reduzido e cura 1 PV de quem começa o turno com 0.'),
('Banimento', '4º nível, abjuração. Envia uma criatura a outro plano se ela falhar em Carisma.'),
('Cão Fiel de Mordenkainen', '4º nível, conjuração. Cão de guarda invisível que late para intrusos e morde causando 4d8 de dano perfurante.'),
('Compulsão', '4º nível, encantamento. Força criaturas a se mover numa direção horizontal que você designa.'),
('Confusão', '4º nível, encantamento. Embaralha as mentes numa esfera de 3 m; cada criatura rola um d10 para determinar seu comportamento.'),
('Conjurar Elementais Menores', '4º nível, conjuração. Invoca elementais de ND baixo, em quantidade conforme o ND escolhido.'),
('Conjurar Seres da Floresta', '4º nível, conjuração. Invoca criaturas feéricas de ND baixo para lutar ao seu lado.'),
('Controlar a Água', '4º nível, transmutação. Controla um corpo de água: inundação, dividir água, redirecionar fluxo ou redemoinho.'),
('Destruição Estonteante', '4º nível, evocação. Próximo acerto corpo a corpo causa 4d6 psíquico extra e pode impor desvantagem.'),
('Dominar Besta', '4º nível, encantamento. Enfeitiça uma besta e permite comandá-la telepaticamente.'),
('Escudo de Fogo', '4º nível, evocação. Chamas dão resistência a frio ou fogo e causam 2d8 de dano a atacantes corpo a corpo.'),
('Esfera Resiliente de Otiluke', '4º nível, evocação. Esfera de energia que aprisiona uma criatura ou objeto, imune a dano.'),
('Fabricar', '4º nível, transmutação. Converte matéria-prima em produtos do mesmo material.'),
('Guardião da Fé', '4º nível, conjuração. Guardião espectral que causa 20 de dano radiante a criaturas hostis próximas, até 60 de dano total.'),
('Inseto Gigante', '4º nível, transmutação. Transforma insetos e aracnídeos em versões gigantes que obedecem a você.'),
('Invisibilidade Maior', '4º nível, ilusão. Torna você ou a criatura tocada invisível, mesmo depois de atacar ou conjurar.'),
('Localizar Criatura', '4º nível, adivinhação. Sente a direção de uma criatura familiar a até 300 m.'),
('Malogro', '4º nível, necromancia. Drena a umidade de uma criatura, causando 8d8 de dano necrótico.'),
('Metamorfose', '4º nível, transmutação. Transforma uma criatura numa besta de ND igual ou menor.'),
('Moldar Rochas', '4º nível, transmutação. Modela um objeto de pedra de tamanho Médio ou menor.'),
('Movimentação Livre', '4º nível, abjuração. O alvo ignora terreno difícil e não é paralisado nem impedido por magias.'),
('Muralha de Fogo', '4º nível, evocação. Muralha de fogo opaca que causa 5d8 de dano de fogo.'),
('Olho Arcano', '4º nível, adivinhação. Olho mágico invisível que você move e através do qual vê.'),
('Pele de Pedra', '4º nível, abjuração. O alvo ganha resistência a dano de concussão, cortante e perfurante não mágico.'),
('Porta Dimensional', '4º nível, conjuração. Teletransporta você, e opcionalmente uma criatura, até 150 m.'),
('Proteção contra a Morte', '4º nível, abjuração. O alvo cai a 1 PV em vez de 0 na primeira vez e é protegido de morte instantânea.'),
('Santuário Particular de Mordenkainen', '4º nível, abjuração. Torna uma área segura contra som, visão, adivinhação, teletransporte e viagem planar.'),
('Tempestade de Gelo', '4º nível, evocação. Granizo num cilindro de 6 m de raio que causa 2d8 de concussão e 4d6 de frio.'),
('Tentáculos Negros de Evard', '4º nível, conjuração. Tentáculos num quadrado de 6 m que causam 3d6 de concussão e impedem criaturas.'),
('Terreno Alucinógeno', '4º nível, ilusão. Faz o terreno natural num cubo de 45 m parecer outro tipo de terreno.'),
('Vinha Esmagadora', '4º nível, conjuração. Vinha que enlaça e arrasta uma criatura, controlada com ação bônus.'),
('Aljava Veloz', '5º nível, transmutação. A aljava produz munição sem fim e permite dois ataques à distância com ação bônus por turno.'),
('Âncora Planar', '5º nível, abjuração. Obriga um celestial, corruptor, elemental ou fada a servir por 24 horas se falhar em Carisma.'),
('Animar Objetos', '5º nível, transmutação. Anima até dez objetos não mágicos, que se tornam criaturas sob seu comando.'),
('Caminhar em Árvores', '5º nível, conjuração. Entra em uma árvore e sai de outra da mesma espécie a até 150 m.'),
('Círculo de Poder', '5º nível, abjuração. Aura de 9 m que dá vantagem em resistências contra magias a aliados.'),
('Círculo de Teletransporte', '5º nível, conjuração. Abre um portal para um círculo de teletransporte permanente cuja sequência de selos você conheça.'),
('Coluna de Chamas', '5º nível, evocação. Coluna divina que causa 4d6 de dano de fogo e 4d6 de dano radiante.'),
('Comunhão', '5º nível, adivinhação (ritual). Faz até três perguntas de sim ou não à sua divindade.'),
('Comunhão com a Natureza', '5º nível, adivinhação (ritual). Você ganha conhecimento sobre o território ao redor.'),
('Cone de Frio', '5º nível, evocação. Cone de 18 m que causa 8d8 de dano de frio.'),
('Conhecimento Lendário', '5º nível, adivinhação. Traz à mente informações sobre uma pessoa, local ou objeto de importância lendária.'),
('Conjurar Elemental', '5º nível, conjuração. Invoca um elemental de ND 5 ou inferior; fica hostil se sua concentração falhar.'),
('Conjurar Saraivada', '5º nível, conjuração. Chuva de munição num cilindro de 12 m de raio que causa 8d8 de dano.'),
('Consagrar', '5º nível, evocação. Infunde uma área de até 18 m de raio com poder sagrado ou profano, com efeitos extras à escolha.'),
('Contato Extraplanar', '5º nível, adivinhação (ritual). Contata uma entidade de outro plano e faz até cinco perguntas, com risco de dano mental.'),
('Criação', '5º nível, ilusão. Cria um objeto inanimado de matéria sombria; a duração depende do material.'),
('Criar Passagem', '5º nível, transmutação. Abre uma passagem em madeira, gesso ou rocha por 1 hora.'),
('Cúpula Antivida', '5º nível, abjuração. Barreira de 3 m de raio que impede a passagem de criaturas vivas, exceto mortos-vivos e constructos.'),
('Curar Ferimentos em Massa', '5º nível, evocação. Até seis criaturas numa esfera de 9 m recuperam 3d8 + modificador de pontos de vida.'),
('Despertar', '5º nível, transmutação. Concede Inteligência 10 e fala a uma besta ou planta.'),
('Despistar', '5º nível, ilusão. Você fica invisível e uma cópia ilusória aparece onde você estava.'),
('Destruição Banidora', '5º nível, abjuração. Próximo acerto com arma causa 5d6 de energia extra e pode banir o alvo a 50 PV ou menos.'),
('Dissipar o Bem e Mal', '5º nível, abjuração. Protege de fadas, mortos-vivos e extraplanares, com funções especiais de cancelar encantamento e demissão.'),
('Dominar Pessoa', '5º nível, encantamento. Enfeitiça um humanoide e permite comandá-lo telepaticamente.'),
('Imobilizar Monstro', '5º nível, encantamento. Paralisa uma criatura que falhar em Sabedoria.'),
('Ligação Telepática de Rary', '5º nível, adivinhação (ritual). Liga até oito criaturas telepaticamente por 1 hora.'),
('Mão de Bigby', '5º nível, evocação. Mão Grande de energia que esmaga, interpõe-se, empurra ou golpeia por 4d8 de energia.'),
('Missão', '5º nível, encantamento. Impõe um comando mágico a uma criatura, que sofre 5d6 psíquico ao desobedecer.'),
('Modificar Memória', '5º nível, encantamento. Altera uma memória de um evento recente de uma criatura enfeitiçada.'),
('Muralha de Energia', '5º nível, evocação. Muralha invisível de energia imune a dano, que bloqueia qualquer passagem.'),
('Muralha de Pedra', '5º nível, conjuração. Muralha não mágica de rocha sólida, que pode se tornar permanente com concentração total.'),
('Névoa Mortal', '5º nível, conjuração. Esfera de nevoeiro venenoso que causa 5d8 de dano de veneno e se move.'),
('Onda Destrutiva', '5º nível, evocação. Explosão de energia divina de 9 m: 5d6 trovejante, 5d6 radiante ou necrótico e derruba.'),
('Praga', '5º nível, necromancia. Ataque de toque que impõe uma doença à escolha, com efeitos debilitantes.'),
('Praga de Insetos', '5º nível, conjuração. Enxame de gafanhotos numa esfera de 6 m que causa 4d10 de dano perfurante.'),
('Reencarnação', '5º nível, transmutação. Forma um novo corpo para um humanoide morto, em geral de outra raça.'),
('Restauração Maior', '5º nível, abjuração. Remove um efeito debilitante: exaustão, enfeitiçar, petrificar, maldição ou redução de habilidades.'),
('Reviver os Mortos', '5º nível, necromancia. Traz à vida uma criatura morta há até 10 dias, com 1 PV.'),
('Similaridade', '5º nível, ilusão. Muda a aparência de qualquer quantidade de criaturas por 8 horas.'),
('Sonho', '5º nível, ilusão. Molda o sonho de uma criatura conhecida e pode causar pesadelo com 3d6 de dano psíquico.'),
('Telecinésia', '5º nível, transmutação. Move ou manipula criaturas ou objetos pelo pensamento.'),
('Vidência', '5º nível, adivinhação. Vê e ouve uma criatura escolhida no mesmo plano por meio de um sensor.'),
('Aliado Planar', '6º nível, conjuração. Pede a uma entidade cósmica que envie um celestial, corruptor ou elemental para auxiliar mediante pagamento.'),
('Ataque Visual', '6º nível, necromancia. Seus olhos impõem adormecer, apavorar ou adoecer a uma criatura que falhar em Sabedoria.'),
('Banquete de Heróis', '6º nível, conjuração. Banquete para até doze criaturas: cura doenças, concede imunidade a veneno e medo e aumenta o PV máximo.'),
('Barreira de Lâminas', '6º nível, evocação. Muralha de lâminas giratórias que causa 6d10 de dano cortante.'),
('Caminhar no Vento', '6º nível, transmutação. Você e até dez criaturas assumem forma de nuvem, com voo de 90 m, por 8 horas.'),
('Carne para Pedra', '6º nível, transmutação. Petrifica gradualmente uma criatura que falhar em Constituição.'),
('Círculo da Morte', '6º nível, necromancia. Esfera de 18 m de energia negativa que causa 8d6 de dano necrótico.'),
('Conjurar Fada', '6º nível, conjuração. Invoca uma criatura feérica de ND 6 ou inferior; fica hostil se sua concentração falhar.'),
('Contingência', '6º nível, evocação. Prepara uma magia que se ativa sozinha quando uma circunstância ocorre.'),
('Corrente de Relâmpagos', '6º nível, evocação. Raio de 10d8 de dano elétrico que salta para até três outros alvos.'),
('Criar Mortos-Vivos', '6º nível, necromancia. Só à noite: transforma até três corpos em carniçais sob seu controle.'),
('Cura Completa', '6º nível, evocação. Restaura 70 pontos de vida e acaba com cegueira, surdez e doenças.'),
('Dança Irresistível de Otto', '6º nível, encantamento. O alvo dança no lugar, com desvantagem em Destreza e ataques, e fica vulnerável a ataques.'),
('Desintegrar', '6º nível, transmutação. Raio que causa 10d6 + 40 de dano de energia e pode reduzir o alvo a pó.'),
('Doença Plena', '6º nível, necromancia. Causa 14d6 de dano necrótico e reduz o PV máximo do alvo.'),
('Encontrar o Caminho', '6º nível, adivinhação. Descobre a rota mais curta e direta até um local estático conhecido.'),
('Esfera Congelante de Otiluke', '6º nível, evocação. Esfera de 18 m de raio que causa 10d6 de dano de frio e pode congelar água.'),
('Globo de Invulnerabilidade', '6º nível, abjuração. Barreira de 3 m que bloqueia magias de 5º nível ou inferior conjuradas de fora.'),
('Ilusão Programada', '6º nível, ilusão. Ilusão que se ativa quando uma condição especificada ocorre.'),
('Invocação Instantânea de Drawmij', '6º nível, conjuração (ritual). Marca um objeto que pode ser trazido à sua mão ao esmagar a safira usada.'),
('Mover Terra', '6º nível, transmutação. Remodela terra, areia ou barro numa área de até 12 m de lado.'),
('Muralha de Espinhos', '6º nível, conjuração. Muralha de arbustos espinhosos que causa 7d8 de dano perfurante e cortante.'),
('Muralha de Gelo', '6º nível, evocação. Muralha de gelo que causa 10d6 de dano de frio ao surgir e deixa ar gelado ao ser destruída.'),
('Palavra de Recordação', '6º nível, conjuração. Teletransporta você e até cinco criaturas a um santuário previamente designado.'),
('Portal Arcano', '6º nível, conjuração. Cria dois portais de teletransporte conectados por 10 minutos.'),
('Proibição', '6º nível, abjuração (ritual). Protege uma área contra viagem mágica e causa dano a tipos de criatura escolhidos.'),
('Proteger Fortaleza', '6º nível, abjuração. Protege uma área de até 225 m² com corredores nevoentos, portas trancadas, teias e outros efeitos.'),
('Raio Solar', '6º nível, evocação. Linha de luz que causa 6d8 de dano radiante e cega.'),
('Recipiente Arcano', '6º nível, necromancia. Sua alma entra num recipiente e pode possuir um humanoide.'),
('Sugestão em Massa', '6º nível, encantamento. Sugere um curso de atividade a até doze criaturas por 24 horas.'),
('Teletransporte por Árvores', '6º nível, conjuração. Liga uma planta Grande ou maior a outra planta a qualquer distância no mesmo plano.'),
('Visão da Verdade', '6º nível, adivinhação. Concede visão verdadeira a 36 m por 1 hora.'),
('Bola de Fogo Controlável', '7º nível, evocação. Conta de fogo que explode ao fim da magia; dano base de 12d6 que aumenta se ela demorar.'),
('Conjurar Celestial', '7º nível, conjuração. Invoca um celestial de ND 4 ou inferior que luta ao seu lado.'),
('Dedo da Morte', '7º nível, necromancia. Causa 7d8 + 30 de dano necrótico; um humanoide morto se ergue como zumbi sob seu controle.'),
('Espada de Mordenkainen', '7º nível, evocação. Espada de energia que ataca causando 3d10 de dano de energia e se move com ação bônus.'),
('Forma Etérea', '7º nível, transmutação. Você entra na Fronteira Etérea por até 8 horas.'),
('Inverter a Gravidade', '7º nível, transmutação. Inverte a gravidade num cilindro de 15 m de raio por 30 m de altura.'),
('Isolamento', '7º nível, transmutação. Esconde uma criatura ou objeto; a criatura entra em animação suspensa.'),
('Mansão Magnífica de Mordenkainen', '7º nível, conjuração. Residência extradimensional com salão, banquetes e 100 servos, por 24 horas.'),
('Miragem', '7º nível, ilusão. Faz o terreno numa área de até 1,5 km² parecer e soar como outro, por 10 dias.'),
('Palavra Divina', '7º nível, evocação. Criaturas que falham em Carisma sofrem efeitos conforme seus PV, até morte; bane extraplanares.'),
('Prisão de Energia', '7º nível, evocação. Prisão cúbica invisível de energia, em formato de cela ou caixa, por 1 hora.'),
('Projetar Imagem', '7º nível, ilusão. Cria uma cópia ilusória sua que fala e através da qual você vê e ouve.'),
('Rajada Prismática', '7º nível, evocação. Oito raios de cores diferentes em cone de 18 m, com efeitos distintos.'),
('Regeneração', '7º nível, transmutação. Cura 4d8 + 15 e regenera 1 PV por turno e membros decepados.'),
('Ressurreição', '7º nível, necromancia. Traz de volta uma criatura morta há até um século, restaurando partes do corpo.'),
('Símbolo', '7º nível, abjuração. Glifo nocivo com efeitos como atordoamento, desespero, dor, insanidade, medo, morte ou sono.'),
('Simulacro', '7º nível, ilusão. Cria uma duplicata de neve ou gelo de uma besta ou humanoide, com metade dos PV.'),
('Teletransporte', '7º nível, conjuração. Transporta você e até oito criaturas a um destino conhecido no mesmo plano.'),
('Tempestade de Fogo', '7º nível, evocação. Até dez cubos de 3 m de chamas que causam 7d10 de dano de fogo.'),
('Viagem Planar', '7º nível, conjuração. Transporta você e até oito criaturas a outro plano de existência.'),
('Antipatia/Simpatia', '8º nível, encantamento. Aura que repele ou atrai criaturas de um tipo escolhido, durante 10 dias.'),
('Aura Sagrada', '8º nível, abjuração. Luz divina dá vantagem em resistências aos aliados e desvantagem aos atacantes; pode cegar corruptores e mortos-vivos.'),
('Campo Antimagia', '8º nível, abjuração. Esfera de 3 m que suprime magias e itens mágicos dentro dela.'),
('Clone', '8º nível, necromancia. Cria uma duplicata inerte que recebe a alma da criatura original se ela morrer.'),
('Controlar o Clima', '8º nível, transmutação. Muda a precipitação, a temperatura e o vento numa área de 7,5 km.'),
('Dominar Monstro', '8º nível, encantamento. Enfeitiça uma criatura e permite comandá-la telepaticamente.'),
('Enfraquecer Intelecto', '8º nível, encantamento. Causa 4d6 psíquico e, se o alvo falhar em Inteligência, reduz Inteligência e Carisma a 1.'),
('Explosão Solar', '8º nível, evocação. Luz solar num raio de 18 m causa 12d6 de dano radiante e cegueira.'),
('Formas Animais', '8º nível, transmutação. Transforma criaturas voluntárias em bestas Grandes ou menores de ND 4 ou inferior.'),
('Labirinto', '8º nível, conjuração. Bane uma criatura para um semiplano labiríntico, de onde escapa com teste de Inteligência CD 20.'),
('Limpar a Mente', '8º nível, abjuração. Imunidade a dano psíquico, leitura de pensamentos, adivinhação e enfeitiçar, por 24 horas.'),
('Loquacidade', '8º nível, transmutação. Em testes de Carisma você pode tratar o resultado como 15, e magias não detectam suas mentiras.'),
('Nuvem Incendiária', '8º nível, conjuração. Nuvem de fumaça e brasas que causa 10d8 de dano de fogo e se move.'),
('Palavra de Poder Atordoar', '8º nível, encantamento. Atordoa uma criatura com 150 pontos de vida ou menos.'),
('Semiplano', '8º nível, conjuração. Cria uma porta para um semiplano de uma sala vazia.'),
('Telepatia', '8º nível, adivinhação. Ligação telepática com uma criatura voluntária no mesmo plano por 24 horas.'),
('Terremoto', '8º nível, evocação. Distúrbio sísmico num círculo de 30 m de raio que derruba criaturas e abre fissuras.'),
('Tsunami', '8º nível, conjuração. Muralha de água de até 90 m que causa 6d10 de dano de concussão e se move.'),
('Alterar Forma', '9º nível, transmutação. Assume a forma de uma criatura de ND igual ao seu nível ou menor e pode trocar de forma durante a duração.'),
('Aprisionamento', '9º nível, abjuração. Prende uma criatura (enterrar, acorrentar, prisão cercada, contenção reduzida ou torpor) até ser dissipada.'),
('Chuva de Meteoros', '9º nível, evocação. Quatro explosões de 12 m de raio, cada uma com 20d6 de fogo e 20d6 de concussão.'),
('Cura Completa em Massa', '9º nível, evocação. Restaura até 700 pontos de vida divididos entre criaturas e cura doenças, cegueira e surdez.'),
('Desejo', '9º nível, conjuração. A magia mais poderosa: copia magias de até 8º nível ou cria efeitos maiores, com risco de estresse.'),
('Encarnação Fantasmagórica', '9º nível, ilusão. Ilusões dos maiores medos amedrontam criaturas e causam 4d10 psíquico por turno.'),
('Metamorfose Verdadeira', '9º nível, transmutação. Transforma criatura em criatura, objeto em criatura ou criatura em objeto; permanente com concentração total.'),
('Muralha Prismática', '9º nível, abjuração. Muralha de luz em sete camadas coloridas, cada uma com um efeito diferente.'),
('Palavra de Poder Curar', '9º nível, evocação. Restaura todos os pontos de vida da criatura tocada e encerra condições.'),
('Palavra de Poder Matar', '9º nível, encantamento. Mata uma criatura com 100 pontos de vida ou menos.'),
('Parar o Tempo', '9º nível, transmutação. Você realiza 1d4 + 1 turnos enquanto o tempo está parado para os outros.'),
('Portal', '9º nível, conjuração. Abre um portal para um local preciso em outro plano, podendo puxar uma criatura nomeada.'),
('Projeção Astral', '9º nível, necromancia. Projeta você e até oito criaturas para o Plano Astral.'),
('Ressurreição Verdadeira', '9º nível, necromancia. Revive uma criatura morta há até 200 anos, curando tudo e podendo criar novo corpo.'),
('Sexto Sentido', '9º nível, adivinhação. O alvo não pode ser surpreendido e tem vantagem em ataques e testes por 8 horas.'),
('Tempestade da Vingança', '9º nível, conjuração. Nuvem tempestuosa de 108 m de raio com efeitos adicionais a cada rodada.');

INSERT INTO raca (nome, descricao) VALUES
('Anão', 'Baixos e robustos, hábeis guerreiros, mineradores e ferreiros, apegados ao clã e à tradição. +2 Constituição. Sub-raças: Anão da Colina e Anão da Montanha.'),
('Elfo', 'Povo mágico de graça sobrenatural, que ama a natureza, a arte e a magia. +2 Destreza. Sub-raças: Alto Elfo, Elfo da Floresta e Elfo Negro (Drow).'),
('Halfling', 'Pequenos e práticos, buscam os confortos do lar, a boa comida e a paz, mas também há andarilhos aventureiros. +2 Destreza. Sub-raças: Pés Leves e Robusto.'),
('Humano', 'A mais jovem das raças comuns: inovadores, realizadores e pioneiros, fisicamente muito diversos. +1 em todos os valores de habilidade.'),
('Draconato', 'Descendentes de dragões, orgulhosos e temidos, com escamas e um sopro de energia dracônica. +2 Força e +1 Carisma.'),
('Gnomo', 'Pequenos, curiosos e cheios de energia, que adoram invenções, exploração e brincadeiras. +2 Inteligência. Sub-raças: Gnomo da Floresta e Gnomo das Rochas.'),
('Meio-Elfo', 'Descendentes de humanos e elfos, que combinam a curiosidade e a ambição humanas com a graça élfica. +2 Carisma e +1 em dois outros valores à escolha.'),
('Meio-Orc', 'Filhos de humanos e orcs, de grande força e resistência, que podem ser comandantes ou aventureiros. +2 Força e +1 Constituição.'),
('Tiefling', 'Descendentes de um pacto infernal, carregam o estigma de sua linhagem e a desconfiança dos outros povos. +1 Inteligência e +2 Carisma.');

-- SUB-RAÇAS (Livro do Jogador)
INSERT INTO sub_raca (nome, descricao, id_raca) VALUES
('Anão da Colina', 'Anão de sentidos aguçados, maior intuição e notável resiliência. Tenacidade Anã: +1 ponto de vida máximo e +1 a cada nível.', (SELECT id_raca FROM raca WHERE nome='Anão')),
('Anão da Montanha', 'Anão forte e resistente, acostumado a uma vida difícil em terrenos difíceis. Treinamento Anão com Armaduras: proficiência em armaduras leves e médias.', (SELECT id_raca FROM raca WHERE nome='Anão')),
('Alto Elfo', 'Elfo de mente afiada e domínio básico da magia. Tem treinamento élfico com armas, um truque de mago à escolha e um idioma adicional.', (SELECT id_raca FROM raca WHERE nome='Elfo')),
('Elfo da Floresta', 'Elfo de sentidos e intuição aguçados, de pés ágeis, que se move rápida e furtivamente. Tem deslocamento de 10,5 m e Máscara da Natureza.', (SELECT id_raca FROM raca WHERE nome='Elfo')),
('Elfo Negro (Drow)', 'Descendentes de elfos de pele negra banidos da superfície, que vivem no Subterrâneo. Visão no escuro superior, Magia Drow e sensibilidade à luz solar.', (SELECT id_raca FROM raca WHERE nome='Elfo')),
('Pés Leves', 'Halfling que se esconde com facilidade, mesmo atrás de outras criaturas, afável e propenso a viajar. Furtividade Natural.', (SELECT id_raca FROM raca WHERE nome='Halfling')),
('Robusto', 'Halfling mais resistente que a média, com resistência a venenos. Resiliência dos Robustos: vantagem em resistências contra veneno e resistência a dano de veneno.', (SELECT id_raca FROM raca WHERE nome='Halfling')),
('Gnomo da Floresta', 'Gnomo com traquejo natural com ilusões, velocidade e furtividade. Tem Ilusionista Nato (truque ilusão menor) e Falar com Bestas Pequenas.', (SELECT id_raca FROM raca WHERE nome='Gnomo')),
('Gnomo das Rochas', 'Gnomo de inventividade e resistência acima dos outros gnomos. Tem Conhecimento de Artífice e Engenhoqueiro.', (SELECT id_raca FROM raca WHERE nome='Gnomo'));

-- BÔNUS DE ATRIBUTO DAS RAÇAS
INSERT INTO raca_atributo (id_raca, atributo, bonus) VALUES
((SELECT id_raca FROM raca WHERE nome='Anão'), 'constituicao', 2),
((SELECT id_raca FROM raca WHERE nome='Elfo'), 'destreza', 2),
((SELECT id_raca FROM raca WHERE nome='Halfling'), 'destreza', 2),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'forca', 1),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'destreza', 1),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'constituicao', 1),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'inteligencia', 1),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'sabedoria', 1),
((SELECT id_raca FROM raca WHERE nome='Humano'), 'carisma', 1),
((SELECT id_raca FROM raca WHERE nome='Draconato'), 'forca', 2),
((SELECT id_raca FROM raca WHERE nome='Draconato'), 'carisma', 1),
((SELECT id_raca FROM raca WHERE nome='Gnomo'), 'inteligencia', 2),
((SELECT id_raca FROM raca WHERE nome='Meio-Elfo'), 'carisma', 2),
((SELECT id_raca FROM raca WHERE nome='Meio-Orc'), 'forca', 2),
((SELECT id_raca FROM raca WHERE nome='Meio-Orc'), 'constituicao', 1),
((SELECT id_raca FROM raca WHERE nome='Tiefling'), 'inteligencia', 1),
((SELECT id_raca FROM raca WHERE nome='Tiefling'), 'carisma', 2);

-- BÔNUS DE ATRIBUTO DAS SUB-RAÇAS
INSERT INTO sub_raca_atributo (id_sub_raca, atributo, bonus) VALUES
((SELECT id_sub_raca FROM sub_raca WHERE nome='Anão da Colina'), 'sabedoria', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Anão da Montanha'), 'forca', 2),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Alto Elfo'), 'inteligencia', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Elfo da Floresta'), 'sabedoria', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Elfo Negro (Drow)'), 'carisma', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Pés Leves'), 'carisma', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Robusto'), 'constituicao', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Gnomo da Floresta'), 'destreza', 1),
((SELECT id_sub_raca FROM sub_raca WHERE nome='Gnomo das Rochas'), 'constituicao', 1);

INSERT INTO item (nome, descricao) VALUES
('Armadura Acolchoada', 'Armadura leve. CA 11 + modificador de Des. Desvantagem em Furtividade. 5 po, 4 kg.'),
('Armadura de Couro', 'Armadura leve. CA 11 + modificador de Des. 10 po, 5 kg.'),
('Armadura de Couro Batido', 'Armadura leve. CA 12 + modificador de Des. 45 po, 6,5 kg.'),
('Gibão de Peles', 'Armadura média. CA 12 + modificador de Des (máx. +2). 10 po, 6 kg.'),
('Camisão de Malha', 'Armadura média. CA 13 + modificador de Des (máx. +2). 50 po, 10 kg.'),
('Brunea', 'Armadura média. CA 14 + modificador de Des (máx. +2). Desvantagem em Furtividade. 50 po, 22,5 kg.'),
('Peitoral', 'Armadura média. CA 14 + modificador de Des (máx. +2). 400 po, 10 kg.'),
('Meia-Armadura', 'Armadura média. CA 15 + modificador de Des (máx. +2). Desvantagem em Furtividade. 750 po, 20 kg.'),
('Cota de Anéis', 'Armadura pesada. CA 14. Desvantagem em Furtividade. 30 po, 20 kg.'),
('Cota de Malha', 'Armadura pesada. CA 16. Exige For 13. Desvantagem em Furtividade. 75 po, 27,5 kg.'),
('Cota de Talas', 'Armadura pesada. CA 17. Exige For 15. Desvantagem em Furtividade. 200 po, 30 kg.'),
('Armadura de Placas', 'Armadura pesada. CA 18. Exige For 15. Desvantagem em Furtividade. 1.500 po, 32,5 kg.'),
('Escudo', 'Escudo. +2 de CA. 10 po, 3 kg.'),
('Adaga', 'Arma simples corpo a corpo. 1d4 perfurante. Acuidade, leve, arremesso (distância 6/18). 2 po, 0,5 kg.'),
('Azagaia', 'Arma simples corpo a corpo. 1d6 perfurante. Arremesso (distância 9/36). 5 pp, 1 kg.'),
('Bordão', 'Arma simples corpo a corpo. 1d6 concussão. Versátil (1d8). 2 pp, 2 kg.'),
('Clava Grande', 'Arma simples corpo a corpo. 1d8 concussão. Pesada, duas mãos. 2 pp, 5 kg.'),
('Foice Curta', 'Arma simples corpo a corpo. 1d4 cortante. Leve. 1 po, 1 kg.'),
('Lança', 'Arma simples corpo a corpo. 1d6 perfurante. Arremesso (distância 6/18), versátil (1d8). 1 po, 1,5 kg.'),
('Maça', 'Arma simples corpo a corpo. 1d6 concussão. 5 po, 2 kg.'),
('Machadinha', 'Arma simples corpo a corpo. 1d6 cortante. Leve, arremesso (distância 6/18). 5 po, 1 kg.'),
('Martelo Leve', 'Arma simples corpo a corpo. 1d4 concussão. Leve, arremesso (distância 6/18). 2 po, 1 kg.'),
('Porrete', 'Arma simples corpo a corpo. 1d4 concussão. Leve. 1 pp, 1 kg.'),
('Arco Curto', 'Arma simples à distância. 1d6 perfurante. Munição (distância 24/96), duas mãos. 25 po, 1 kg.'),
('Beste Leve', 'Arma simples à distância. 1d8 perfurante. Munição (distância 24/96), recarga, duas mãos. 25 po, 2,5 kg.'),
('Dardo', 'Arma simples à distância. 1d4 perfurante. Acuidade, arremesso (distância 6/18). 5 pc, 0,1 kg.'),
('Funda', 'Arma simples à distância. 1d4 concussão. Munição (distância 9/36). 1 pp.'),
('Alabarda', 'Arma marcial corpo a corpo. 1d10 cortante. Pesada, alcance, duas mãos. 20 po, 3 kg.'),
('Cimitarra', 'Arma marcial corpo a corpo. 1d6 cortante. Acuidade, leve. 25 po, 1,5 kg.'),
('Chicote', 'Arma marcial corpo a corpo. 1d4 cortante. Acuidade, alcance. 2 po, 1,5 kg.'),
('Espada Curta', 'Arma marcial corpo a corpo. 1d6 perfurante. Acuidade, leve. 10 po, 1 kg.'),
('Espada Grande', 'Arma marcial corpo a corpo. 2d6 cortante. Pesada, duas mãos. 50 po, 3 kg.'),
('Espada Longa', 'Arma marcial corpo a corpo. 1d8 cortante. Versátil (1d10). 15 po, 1,5 kg.'),
('Glaive', 'Arma marcial corpo a corpo. 1d10 cortante. Pesada, alcance, duas mãos. 20 po, 3 kg.'),
('Lança de Montaria', 'Arma marcial corpo a corpo. 1d12 perfurante. Alcance, especial (desvantagem a até 1,5 m; duas mãos se não estiver montado). 10 po, 3 kg.'),
('Lança Longa', 'Arma marcial corpo a corpo. 1d10 perfurante. Pesada, alcance, duas mãos. 5 po, 4 kg.'),
('Maça Estrela', 'Arma marcial corpo a corpo. 1d8 perfurante. 15 po, 2 kg.'),
('Machado Grande', 'Arma marcial corpo a corpo. 1d12 cortante. Pesada, duas mãos. 30 po, 3,5 kg.'),
('Machado de Batalha', 'Arma marcial corpo a corpo. 1d8 cortante. Versátil (1d10). 10 po, 2 kg.'),
('Malho', 'Arma marcial corpo a corpo. 2d6 concussão. Pesada, duas mãos. 10 po, 5 kg.'),
('Mangual', 'Arma marcial corpo a corpo. 1d8 concussão. 10 po, 1 kg.'),
('Martelo de Guerra', 'Arma marcial corpo a corpo. 1d8 concussão. Versátil (1d10). 15 po, 1 kg.'),
('Picareta de Guerra', 'Arma marcial corpo a corpo. 1d8 perfurante. 5 po, 1 kg.'),
('Rapieira', 'Arma marcial corpo a corpo. 1d8 perfurante. Acuidade. 25 po, 1 kg.'),
('Tridente', 'Arma marcial corpo a corpo. 1d6 perfurante. Arremesso (distância 6/18), versátil (1d8). 5 po, 2 kg.'),
('Arco Longo', 'Arma marcial à distância. 1d8 perfurante. Munição (distância 45/180), pesada, duas mãos. 50 po, 1 kg.'),
('Besta de Mão', 'Arma marcial à distância. 1d6 perfurante. Munição (distância 9/36), leve, recarga. 75 po, 1,5 kg.'),
('Besta Pesada', 'Arma marcial à distância. 1d10 perfurante. Munição (distância 30/120), pesada, recarga, duas mãos. 50 po, 4,5 kg.'),
('Rede', 'Arma marcial à distância. Sem dano. Especial, arremesso (distância 1,5/4,5). Criatura Grande ou menor atingida fica impedida. 1 po, 1,5 kg.'),
('Zarabatana', 'Arma marcial à distância. 1 perfurante. Munição (distância 7,5/30), recarga. 10 po, 0,5 kg.'),
('Ábaco', 'Instrumento de cálculo com fichas ou contas deslizantes em hastes paralelas. 2 po, 1 kg.'),
('Ácido (vidro)', 'Despejado em criatura a 1,5 m ou arremessado a 6 m como arma improvisada. Se acertar, 2d6 de dano ácido. 25 po, 0,5 kg.'),
('Água Benta (frasco)', 'Espalhada ou arremessada a 6 m como arma improvisada. Causa 2d6 de dano radiante em corruptor ou morto-vivo. 25 po, 0,5 kg.'),
('Algemas', 'Prendem criatura Pequena ou Média. Escapar: Des CD 20. Quebrar: For CD 20. 15 PV. Vêm com chave. 2 po, 2 kg.'),
('Algibeira', 'Bolsa de pano ou couro. Guarda até 20 munições de funda ou 50 de zarabatana. 5 po, 0,5 kg.'),
('Aljava', 'Guarda até 20 flechas. 1 po, 0,5 kg.'),
('Ampulheta', 'Equipamento de aventura. 25 po, 0,5 kg.'),
('Antídoto (vidro)', 'Quem beber tem vantagem em resistências contra venenos por 1 hora. Sem efeito em mortos-vivos e constructos. 50 po.'),
('Apito de Advertência', 'Equipamento de aventura. 25 po, 0,5 kg.'),
('Aríete Portátil', 'Para quebrar portas: +4 no teste de Força. Outra criatura que ajude concede vantagem. 4 po, 17,5 kg.'),
('Armadilha de Caça', 'Anel de aço dentado. Des CD 13 ou 1d4 perfurante e para de se mover. Libertar: For CD 13. 5 po, 12,5 kg.'),
('Arpéu', 'Equipamento de aventura. 2 po, 2 kg.'),
('Balança de Mercador', 'Balança com pesos de até 1 kg, para medir o peso exato de objetos pequenos. 5 po, 1,5 kg.'),
('Balde', 'Recipiente com capacidade de 12 litros. 5 pc, 1 kg.'),
('Barril', 'Recipiente com capacidade de 160 litros. 2 po, 35 kg.'),
('Baú', 'Recipiente que comporta até 150 kg de equipamentos. 5 po, 12,5 kg.'),
('Bolsa de Componentes', 'Bolsa de couro à prova dagua com compartimentos para os componentes materiais de magias sem custo específico. 25 po, 1 kg.'),
('Caixa de Fogo', 'Pederneira, isqueiro e pavio. Acender uma tocha leva 1 ação; outro fogo, 1 minuto. 5 pp, 0,5 kg.'),
('Caneca', 'Recipiente com capacidade de 500 ml. 2 pc, 0,5 kg.'),
('Caneta Tinteiro', 'Equipamento de aventura. 2 pc.'),
('Cantil', 'Recipiente com capacidade de 2 litros. 2 pp, 2,5 kg.'),
('Cesto', 'Comporta até 20 kg de equipamentos. 4 pp, 1 kg.'),
('Cobertor de Inverno', 'Equipamento de aventura. 5 pp, 1,5 kg.'),
('Corda de Cânhamo (15 metros)', 'Tem 2 PV e pode ser arrebentada com teste de Força CD 17. 1 po, 5 kg.'),
('Corda de Seda (15 metros)', 'Tem 2 PV e pode ser arrebentada com teste de Força CD 17. 10 po, 2,5 kg.'),
('Corrente (3 metros)', 'Tem 10 PV e pode ser arrebentada com teste de Força CD 20. 5 po, 5 kg.'),
('Equipamento de Pescaria', 'Vara de pesca de madeira, linha de seda, boias de cortiça, anzóis de aço, chumbadas, iscas e redes. 1 po, 2 kg.'),
('Escada (3 metros)', 'Equipamento de aventura. 1 pp, 12,5 kg.'),
('Esferas de Metal', 'Sacola com 1.000 esferas. Despejadas cobrem 3 m de lado; Des CD 10 ou a criatura cai. 1 po, 1 kg.'),
('Espelho de Aço', 'Equipamento de aventura. 5 po, 0,25 kg.'),
('Estrepes', 'Bolsa com 20. Cobrem 1,5 m de lado; Des CD 15 ou 1 perfurante, para de se mover e deslocamento -3 m. 1 po, 1 kg.'),
('Fechadura', 'Vem com chave. Sem a chave, abrir exige ferramentas de ladrão e Des CD 15. 10 po, 0,5 kg.'),
('Foco Arcano (Bastão)', 'Foco de conjuração para feiticeiro, bruxo ou mago. 10 po, 1 kg.'),
('Foco Arcano (Cajado)', 'Foco de conjuração para feiticeiro, bruxo ou mago. 5 po, 2 kg.'),
('Foco Arcano (Cristal)', 'Foco de conjuração para feiticeiro, bruxo ou mago. 10 po, 0,5 kg.'),
('Foco Arcano (Orbe)', 'Foco de conjuração para feiticeiro, bruxo ou mago. 20 po, 1,5 kg.'),
('Foco Arcano (Varinha)', 'Foco de conjuração para feiticeiro, bruxo ou mago. 10 po, 0,5 kg.'),
('Foco Druídico (Cajado de Madeira)', 'Foco de conjuração para druida. 5 po, 2 kg.'),
('Foco Druídico (Ramo de Visco)', 'Foco de conjuração para druida. 1 po.'),
('Foco Druídico (Totem)', 'Foco de conjuração para druida. 1 po.'),
('Foco Druídico (Varinha de Teixo)', 'Foco de conjuração para druida. 10 po, 0,5 kg.'),
('Fogo Alquímico (frasco)', 'Arremessado a 6 m como arma improvisada. Alvo sofre 1d4 de fogo por turno; Des CD 10 apaga. 50 po, 0,5 kg.'),
('Frasco', 'Recipiente com capacidade de 120 ml. 2 pc, 1 kg.'),
('Garrafa de Vidro', 'Recipiente com capacidade de 750 ml. 1 po, 1 kg.'),
('Giz', 'Uma peça. 1 pc.'),
('Grimório', 'Volume encadernado em couro com 100 páginas de pergaminho em branco, para armazenar magias. 50 po, 1,5 kg.'),
('Jarra', 'Recipiente com capacidade de 5 litros. 4 pc, 2 kg.'),
('Kit de Escalada', 'Pítons, botas pontiagudas, luvas e cinto. Com 1 ação você se ancora e não cai mais de 7,5 m. 25 po, 6 kg.'),
('Kit de Primeiros-Socorros', 'Dez usos. Com 1 ação estabiliza criatura com 0 PV, sem teste de Medicina. 5 po, 1,5 kg.'),
('Lâmpada', 'Luz plena em 4,5 m e penumbra por mais 9 m. Queima 6 horas por frasco de óleo. 5 pp, 0,5 kg.'),
('Lanterna Coberta', 'Luz plena em 9 m e penumbra por mais 9 m. Queima 6 horas por frasco de óleo. 5 po, 1 kg.'),
('Lanterna Furta-Fogo', 'Luz plena em cone de 18 m e penumbra por mais 18 m. Queima 6 horas por frasco de óleo. 10 po, 1 kg.'),
('Lente de Aumento', 'Vantagem ao avaliar itens pequenos ou detalhados; também acende fogo. 100 po.'),
('Livro', 'Pode conter poesia, história, saberes, diagramas ou outros textos e imagens. 25 po, 2,5 kg.'),
('Luneta', 'Objetos vistos através dela são ampliados ao dobro do tamanho. 1.000 po, 0,5 kg.'),
('Manto', 'Equipamento de aventura. 1 po, 2 kg.'),
('Marreta', 'Equipamento de aventura. 2 po, 5 kg.'),
('Martelo', 'Equipamento de aventura. 1 po, 1,5 kg.'),
('Mochila', 'Comporta até 15 kg de equipamentos; itens podem ser presos por fora. 2 po, 2,5 kg.'),
('Balas de Funda', 'Munição para funda. Pacote com 20. 4 pc, 0,75 kg.'),
('Flechas', 'Munição para arcos. Pacote com 20. 1 po, 0,5 kg.'),
('Virotes', 'Munição para bestas. Pacote com 20. 1 po, 0,75 kg.'),
('Agulhas de Zarabatana', 'Munição para zarabatana. Pacote com 50. 1 po, 0,5 kg.'),
('Óleo (frasco)', 'Frasco de 500 ml. Arremessado a 6 m cobre o alvo; fogo antes de secar causa 5 de dano extra. 1 pp, 0,5 kg.'),
('Pá', 'Equipamento de aventura. 2 po, 2,5 kg.'),
('Panela de Ferro', 'Recipiente com capacidade de 4 litros. 2 po, 5 kg.'),
('Papel', 'Uma folha. 2 pp.'),
('Parafina', 'Equipamento de aventura. 5 pp.'),
('Pé de Cabra', 'Vantagem em testes de Força em que uma alavanca possa ser aplicada. 2 po, 2,5 kg.'),
('Pedra de Amolar', 'Equipamento de aventura. 1 pc.'),
('Perfume (frasco)', 'Equipamento de aventura. 5 po.'),
('Pergaminho', 'Uma folha. 1 pp.'),
('Picareta de Minerador', 'Equipamento de aventura. 2 po, 5 kg.'),
('Píton', 'Equipamento de aventura. 5 pc.'),
('Poção de Cura', 'Líquido vermelho mágico que restaura 2d4+2 pontos de vida. Beber ou administrar exige uma ação. 50 po, 0,25 kg.'),
('Porta Mapas ou Pergaminhos', 'Estojo cilíndrico de couro para até 10 folhas de papel ou 5 de pergaminho enroladas. 1 po, 0,5 kg.'),
('Porta Virotes', 'Estojo de madeira para até 20 virotes de besta. 1 po, 0,5 kg.'),
('Pregos de Ferro', 'Dez pregos. 1 po, 2,5 kg.'),
('Rações de Viagem (1 dia)', 'Alimentos desidratados para viagens longas: carne seca, frutas secas, bolachas e nozes. 5 pp, 1 kg.'),
('Robes', 'Vestimenta. 1 po, 2 kg.'),
('Roldana e Polia', 'Cabo com gancho que permite içar até quatro vezes o peso normalmente erguido. 1 po, 2,5 kg.'),
('Roupas Comuns', 'Vestimenta. 5 pp, 1,5 kg.'),
('Roupas de Viajante', 'Vestimenta. 2 po, 2 kg.'),
('Roupas de Entretenimento', 'Vestimenta. 5 po, 2 kg.'),
('Roupas Finas', 'Vestimenta. 15 po, 3 kg.'),
('Sabão', 'Equipamento de aventura. 2 pc.'),
('Saco', 'Comporta até 13 kg de equipamentos. 1 pc, 0,25 kg.'),
('Saco de Dormir', 'Equipamento de aventura. 1 po, 3,5 kg.'),
('Símbolo Sagrado (Amuleto)', 'Foco de conjuração para clérigo ou paladino. 5 po, 0,5 kg.'),
('Símbolo Sagrado (Emblema)', 'Foco de conjuração para clérigo ou paladino. 5 po.'),
('Símbolo Sagrado (Relicário)', 'Foco de conjuração para clérigo ou paladino. 5 po, 1 kg.'),
('Sinete', 'Equipamento de aventura. 5 po.'),
('Sino', 'Equipamento de aventura. 1 po.'),
('Tenda para Duas Pessoas', 'Abrigo simples e portátil que acomoda duas pessoas. 2 po, 10 kg.'),
('Tocha', 'Queima 1 hora: luz plena em 6 m e penumbra por mais 6 m. Em ataque corpo a corpo acesa, causa 1 de dano de fogo. 1 pc, 0,5 kg.'),
('Tinta (frasco de 30 ml)', 'Equipamento de aventura. 10 po.'),
('Vara (3 metros)', 'Equipamento de aventura. 5 pc, 3,5 kg.'),
('Vela', 'Por 1 hora, luz plena em 1,5 m e penumbra por mais 1,5 m. 1 pc.'),
('Veneno Básico (frasco)', 'Cobre lâmina cortante ou perfurante ou 3 munições; Con CD 10 ou 1d4 de veneno. Dura 1 minuto. 100 po.'),
('Ferramentas de Carpinteiro', 'Ferramentas de artesão. 8 po, 3 kg.'),
('Ferramentas de Cartógrafo', 'Ferramentas de artesão. 15 po, 3 kg.'),
('Ferramentas de Costureiro', 'Ferramentas de artesão. 1 po, 2,5 kg.'),
('Ferramentas de Coureiro', 'Ferramentas de artesão. 5 po, 2,5 kg.'),
('Ferramentas de Entalhador', 'Ferramentas de artesão. 1 po, 2,5 kg.'),
('Ferramentas de Ferreiro', 'Ferramentas de artesão. 20 po, 4 kg.'),
('Ferramentas de Funileiro', 'Ferramentas de artesão. 50 po, 5 kg.'),
('Ferramentas de Joalheiro', 'Ferramentas de artesão. 25 po, 1 kg.'),
('Ferramentas de Oleiro', 'Ferramentas de artesão. 10 po, 1,5 kg.'),
('Ferramentas de Pedreiro', 'Ferramentas de artesão. 10 po, 4 kg.'),
('Ferramentas de Pintor', 'Ferramentas de artesão. 10 po, 2,5 kg.'),
('Ferramentas de Sapateiro', 'Ferramentas de artesão. 5 po, 2,5 kg.'),
('Ferramentas de Vidreiro', 'Ferramentas de artesão. 30 po, 2,5 kg.'),
('Suprimentos de Alquimista', 'Ferramentas de artesão. 50 po, 4 kg.'),
('Suprimentos de Cervejeiro', 'Ferramentas de artesão. 20 po, 4,5 kg.'),
('Suprimentos de Caligrafia', 'Ferramentas de artesão. 10 po, 2,5 kg.'),
('Utensílios de Cozinheiro', 'Ferramentas de artesão. 1 po, 4 kg.'),
('Ferramentas de Navegação', 'Instrumentos de navegação marítima: traçar rumo, seguir cartas e não se perder no mar. 25 po, 1 kg.'),
('Ferramentas de Ladrão', 'Pasta, chaves mestras, espelho em alça, tesoura de lâminas estreitas e alicates. Para desarmar armadilhas e abrir fechaduras. 25 po, 0,5 kg.'),
('Alaúde', 'Instrumento musical. 35 po, 1 kg.'),
('Flauta', 'Instrumento musical. 2 po, 0,5 kg.'),
('Flauta de Pã', 'Instrumento musical. 12 po, 1 kg.'),
('Gaita de Foles', 'Instrumento musical. 30 po, 3 kg.'),
('Lira', 'Instrumento musical. 30 po, 1 kg.'),
('Oboé', 'Instrumento musical. 2 po, 0,5 kg.'),
('Tambor', 'Instrumento musical. 6 po, 1,5 kg.'),
('Trombeta', 'Instrumento musical. 3 po, 1 kg.'),
('Violino', 'Instrumento musical. 30 po, 3 kg.'),
('Xilofone', 'Instrumento musical. 25 po, 5 kg.'),
('Kit de Disfarce', 'Bolsa de cosméticos, tintura de cabelo e pequenos adereços para criar disfarces. 25 po, 1,5 kg.'),
('Kit de Falsificação', 'Kit de ferramentas. 15 po, 2,5 kg.'),
('Kit de Herbalismo', 'Kit de ferramentas. 5 po, 1,5 kg.'),
('Baralho de Cartas', 'Kit de jogo. 5 pp.'),
('Conjunto de Dados', 'Kit de jogo. 1 pp.'),
('Jogo dos Três Dragões', 'Kit de jogo. 5 po.'),
('Xadrez do Dragão', 'Kit de jogo. 1 po, 0,25 kg.'),
('Kit de Venenos', 'Kit de ferramentas. 50 po, 1 kg.');

INSERT INTO habilidade_classe (nome, descricao, nivel, id_classe) VALUES
('Fúria', 'Em batalha, você luta com uma ferocidade primitiva. No seu turno, você pode entrar em fúria com uma ação bônus...', 1, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Defesa sem Armadura', 'Quando você não estiver vestindo qualquer armadura, sua Classe de Armadura será 10 + seu modificador de Destreza + seu modificador de Constituição...', 1, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Ataque Descuidado', 'A partir do 2° nível, você pode desistir de toda preocupação com sua defesa para atacar com um desespero feroz...', 2, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Sentido de Perigo', 'No 2° nível, você adquire um sentido sobrenatural de quando as coisas próximas não estão como deveriam...', 2, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Caminho Primitivo', 'No 3° nível, você escolhe um caminho que molda a natureza da sua fúria...', 3, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Incremento no Valor de Habilidade', 'Quando você atinge o 4° nível e novamente no 8°, 12°, 16° e 19° nível...', 4, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Ataque Extra', 'A partir do 5° nível, você pode atacar duas vezes, ao invés de uma...', 5, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Movimento Rápido', 'Começando no 5° nível, seu deslocamento aumenta em 3 metros...', 5, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Instinto Selvagem', 'No 7° nível, seu instinto está tão apurado que você recebe vantagem nas jogadas de iniciativa...', 7, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Crítico Brutal', 'A partir do 9° nível, você pode rolar um dado de dano de arma adicional...', 9, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Fúria Implacável', 'A partir do 11° nível, sua fúria pode manter você lutando independente da gravidade dos seus ferimentos...', 11, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Fúria Persistente', 'A partir do 15° nível, sua fúria é tão brutal que ela só termina prematuramente...', 15, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Força Indomável', 'A partir do 18° nível, se o total de um teste de Força seu for menor que o seu valor de Força...', 18, (SELECT id_classe FROM classe WHERE nome='Bárbaro')),
('Campeão Primitivo', 'No 20° nível, você incorpora os poderes da natureza...', 20, (SELECT id_classe FROM classe WHERE nome='Bárbaro'));

INSERT INTO habilidade_subclasse (nome, descricao, nivel, id_subclasse) VALUES
('Frenesi', 'Começando no momento que você escolhe esse caminho no 3° nível...', 3, (SELECT id_subclasse FROM subclasse WHERE nome='Caminho do Furioso')),
('Fúria Inconsciente', 'A partir do 6° nível, você não pode ser enfeitiçado ou amedrontado enquanto estiver em fúria...', 6, (SELECT id_subclasse FROM subclasse WHERE nome='Caminho do Furioso')),
('Presença Intimidante', 'A partir do 10° nível, você pode usar sua ação para amedrontar alguém...', 10, (SELECT id_subclasse FROM subclasse WHERE nome='Caminho do Furioso')),
('Retaliação', 'A partir do 14° nível, quando você sofrer dano de uma criatura...', 14, (SELECT id_subclasse FROM subclasse WHERE nome='Caminho do Furioso'));

INSERT INTO monstro (
    nome, tamanho, tipo, tendencia, ca, hp_maximo, deslocamento,
    forca, destreza, constituicao, inteligencia, sabedoria, carisma,
    desafio, xp, descricao, id_criador
) VALUES
('Aarakocra', 'Médio', 'humanoide (aarakocra)', 'neutro e bom', 12, 13, '6 m, voo 15 m', 10, 14, 10, 11, 12, 11, '1/4', 50, 'Humanoides aviários que habitam as altas montanhas.', NULL),
('Abolete', 'Grande', 'aberração', 'leal e mau', 17, 135, '3 m, natação 12 m', 21, 9, 15, 18, 15, 18, '10', 5900, 'Aberração anfíbia ancestral com poderes telepáticos.', NULL),
('Ankheg', 'Grande', 'monstruosidade', 'imparcial', 14, 39, '9 m, escavação 3 m', 17, 11, 13, 1, 13, 6, '2', 450, 'Predador insetoide subterrâneo.', NULL),
('Goblin', 'Pequeno', 'humanoide (goblinóide)', 'neutro e mau', 15, 7, '9 m', 8, 14, 10, 10, 8, 8, '1/4', 50, 'Pequeno humanoide astuto.', NULL),
('Ogro', 'Grande', 'gigante', 'caótico e mau', 11, 59, '12 m', 19, 8, 16, 5, 7, 7, '2', 450, 'Gigante corpulento.', NULL),
('Zumbi', 'Médio', 'morto-vivo', 'neutro e mau', 8, 22, '6 m', 13, 6, 16, 3, 6, 5, '1/4', 50, 'Cadáver reanimado.', NULL);

-- ============================================================================
-- ANTECEDENTES (Livro do Jogador, Capítulo 4, págs. 129-143)
-- ============================================================================

INSERT INTO antecedente (nome, descricao, caracteristica_nome, caracteristica_descricao, idiomas_adicionais, ferramentas_proficiencia, equipamento_inicial) VALUES
(
    'Acólito',
    'Você viveu a serviço de um templo de algum deus específico ou de um panteão de deuses, agindo como um intermediário entre o reino divino e o reino dos mortais, realizando rituais e ofertando sacrifícios.',
    'Abrigo dos Fiéis',
    'Você detém o respeito daqueles que compartilham de sua fé e pode realizar cerimônias de sua divindade. Você e seus companheiros de aventura podem receber cura e caridade de um templo, santuário ou posto de sua fé (devendo fornecer componentes materiais), e os fiéis custeiam a você um estilo de vida modesto.',
    2,
    NULL,
    'Um símbolo sagrado, um livro de preces ou conta de orações, 5 varetas de incenso, vestimentas, um conjunto de roupas comuns e uma algibeira contendo 15 po.'
),
(
    'Artesão de Guilda',
    'Você é membro de uma guilda de artesãos, perito em um campo específico e intimamente associado a outros artesãos, livre das restrições de uma ordem social feudal graças aos seus talentos e riqueza.',
    'Associados da Guilda',
    'Seus camaradas membros da guilda irão provê-lo com hospedagem e comida, se necessário, e pagarão pelo seu funeral se preciso for. O salão da guilda oferece um local central para conhecer patrões, aliados e empregados, e a guilda pode ampará-lo legalmente ou conectá-lo a figuras políticas mediante cotas de 5 po por mês.',
    1,
    'Um tipo de ferramenta de artesão',
    'Um conjunto de ferramentas de artesão (à sua escolha), uma carta de apresentação da sua guilda, um conjunto de roupas de viajante e uma algibeira com 15 po.'
),
(
    'Artista',
    'Você cresce em frente a uma audiência e sabe como fasciná-la, entretê-la e inspirá-la por meio da poesia, música, dança, atuação ou acrobacias.',
    'Pela Demanda Popular',
    'Você sempre encontra um lugar para atuar (tavernas, estalagens, circos, teatros ou cortes nobres), recebendo alojamento e comida modesta ou confortável de graça contanto que atue a cada noite, além de ser reconhecido como figura local nas cidades onde já atuou.',
    0,
    'Kit de disfarce, um tipo de instrumento musical',
    'Um instrumento musical (à sua escolha), um presente de um admirador (carta de amor, mecha de cabelo ou bijuteria), um traje e uma algibeira contendo 15 po.'
),
(
    'Charlatão',
    'Você sempre teve jeito com as pessoas, sabendo destrinchar seus desejos após alguns minutos de conversa e convencê-las a acreditar em maravilhas improváveis e esquemas lucrativos.',
    'Identidade Falsa',
    'Você criou uma segunda identidade que inclui documentos, conhecidos estabelecidos e disfarces que possibilitam assumir essa persona. Além disso, você pode forjar documentos oficiais e cartas pessoais, contanto que tenha visto um exemplo desse tipo de documento ou da caligrafia.',
    0,
    'Kit de disfarce, kit de falsificação',
    'Um conjunto de roupas finas, um kit de disfarce, ferramentas de trapaça à sua escolha (dez garrafas com líquidos coloridos, dados viciados, baralho de cartas marcadas ou anel de sinete de um duque imaginário) e uma algibeira contendo 15 po.'
),
(
    'Criminoso',
    'Você é um criminoso experiente com um histórico de contravenções, mantendo contato com outros criminosos e com o submundo do assassinato, roubo e violência.',
    'Contato Criminal',
    'Você possui contatos de confiança que agem como seus informantes em uma rede criminosa e sabe como se comunicar com eles mesmo em grandes distâncias por meio de mensageiros locais, mestres de caravana corruptos e marinheiros escusos.',
    0,
    'Um tipo de kit de jogo, ferramentas de ladrão',
    'Um pé de cabra, um conjunto de roupas escuras comuns com capuz e uma algibeira contendo 15 po.'
),
(
    'Eremita',
    'Você viveu em reclusão — em uma comunidade isolada como um monastério ou completamente sozinho — por um período importante da sua vida, encontrando quietude, solidão e respostas.',
    'Descoberta',
    'A calma reclusão de seu eremitério prolongado lhe deu acesso a uma descoberta única e poderosa: uma grande verdade sobre o cosmos, os deuses, outros planos, forças da natureza, um local desconhecido ou um fato esquecido que pode reescrever a história.',
    1,
    'Kit de herbalismo',
    'Um estojo de pergaminho cheio de notas dos seus estudos e orações, um cobertor de inverno, um conjunto de roupas comuns, um kit de herbalismo e 5 po.'
),
(
    'Forasteiro',
    'Você cresceu em uma área selvagem, longe da civilização e dos confortos da cidade, sobrevivendo a climas extremos e à solidão como nômade, explorador, recluso, forrageador ou saqueador.',
    'Andarilho',
    'Você tem uma memória excelente para mapas e geografia, podendo sempre recobrar o plano geral de terrenos e assentamentos ao seu redor. Além disso, pode encontrar comida e água fresca para você e até cinco outras pessoas a cada dia, desde que a terra ofereça recursos.',
    1,
    'Um tipo de instrumento musical',
    'Um bordão, uma armadilha de caça, um fetiche de um animal que você matou, um conjunto de roupas de viajante e uma algibeira contendo 10 po.'
),
(
    'Herói do Povo',
    'Você veio de uma parcela humilde da sociedade, mas o povo de sua vila já o reconhece como campeão após um evento definidor, e seu destino o conduz a batalhas contra tiranos e monstros.',
    'Hospitalidade Rústica',
    'Por ter ascendido dentre as pessoas comuns, você pode facilmente se misturar a elas e encontrar lugar entre camponeses para se esconder, descansar ou se recuperar, sendo escondido da lei e de perseguidores desde que isso não ofereça risco direto às vidas deles.',
    0,
    'Um tipo de ferramenta de artesão, veículos (terrestre)',
    'Um conjunto de ferramentas de artesão (à sua escolha), uma pá, um pote de ferro, um conjunto de roupas comuns e uma algibeira contendo 10 po.'
),
(
    'Marinheiro',
    'Você navegou em um navio pelo mar por anos, enfrentando poderosas tormentas, monstros das profundezas e perigos em alto-mar.',
    'Passagem de Navio',
    'Quando precisar, você pode conseguir passagem de graça em um navio para você e seus companheiros de aventura em uma embarcação onde trabalhou ou com a qual teve boas relações, ajudando a tripulação durante a viagem em troca da passagem.',
    0,
    'Ferramentas de navegador, veículo (aquático)',
    'Uma malagueta (clava), 15 metros de corda de seda, um amuleto da sorte (como um pé de coelho ou pedra furada), um conjunto de trajes comuns e uma algibeira contendo 10 po.'
),
(
    'Nobre',
    'Você entende de riqueza, poder e privilégios, carrega um título de nobreza conectado à sua família e exerce influência política significativa.',
    'Posição Privilegiada',
    'Graças à sua origem nobre, você é bem-vindo na alta sociedade, pessoas comuns fazem esforços para acomodá-lo, outros nobres o tratam como igual e você pode conseguir uma audiência com um nobre local se precisar.',
    1,
    'Um tipo de kit de jogos',
    'Um conjunto de trajes finos, um anel de sinete, um pergaminho de linhagem e uma algibeira contendo 25 po.'
),
(
    'Órfão',
    'Você cresceu nas ruas, sozinho, órfão e pobre, sobrevivendo por meio de astúcia, força e agilidade em telhados e becos.',
    'Segredos da Cidade',
    'Você conhece os padrões secretos e o fluxo das cidades e pode encontrar passagens através da expansão urbana que outros deixariam passar. Fora de combate, você e os companheiros que guiar podem viajar entre dois locais quaisquer na cidade com o dobro da velocidade normal.',
    0,
    'Kit de disfarce, ferramentas de ladrão',
    'Uma faca pequena, um mapa da cidade em que você cresceu, um rato de estimação, um pequeno objeto para lembrar dos seus pais, um conjunto de roupas comuns e uma algibeira contendo 10 po.'
),
(
    'Sábio',
    'Você passou anos aprendendo sobre o conhecimento do multiverso, decorando manuscritos, estudando pergaminhos e tornando-se um mestre em seu campo de estudo.',
    'Pesquisador',
    'Quando tentar obter ou recuperar um fragmento de conhecimento que você não saiba, você descobre aonde e com quem pode obter essa informação (bibliotecas, arquivos de escribas, universidades ou outros sábios), salvo se estiver oculta em local quase inacessível.',
    2,
    NULL,
    'Um vidro de tinta escura, uma pena, uma faca pequena, uma carta de um falecido colega com uma pergunta sem resposta, um conjunto de roupas comuns e uma algibeira contendo 10 po.'
),
(
    'Soldado',
    'A guerra esteve na sua vida desde que você se recorda; você foi treinado desde jovem no uso de armas, armaduras e técnicas de sobrevivência militar.',
    'Patente Militar',
    'Você possui uma patente militar da sua época como soldado. Soldados leais à sua antiga organização reconhecem sua autoridade e influência, permitindo que você requisite equipamentos simples ou cavalos temporariamente e tenha acesso a acampamentos militares e fortalezas aliadas.',
    0,
    'Um tipo de kit de jogo, veículo (terrestre)',
    'Uma insígnia de patente, um fetiche obtido de um inimigo caído, um conjunto de dados de osso ou baralho, um conjunto de roupas comuns e uma algibeira contendo 10 po.'
);

-- RELAÇÃO ANTECEDENTE x PERÍCIAS PROFICIENTES (2 perícias por antecedente)
INSERT INTO antecedente_pericia (id_antecedente, id_pericia) VALUES
((SELECT id_antecedente FROM antecedente WHERE nome='Acólito'), (SELECT id_pericia FROM pericia WHERE nome='Intuição')),
((SELECT id_antecedente FROM antecedente WHERE nome='Acólito'), (SELECT id_pericia FROM pericia WHERE nome='Religião')),

((SELECT id_antecedente FROM antecedente WHERE nome='Artesão de Guilda'), (SELECT id_pericia FROM pericia WHERE nome='Intuição')),
((SELECT id_antecedente FROM antecedente WHERE nome='Artesão de Guilda'), (SELECT id_pericia FROM pericia WHERE nome='Persuasão')),

((SELECT id_antecedente FROM antecedente WHERE nome='Artista'), (SELECT id_pericia FROM pericia WHERE nome='Acrobacia')),
((SELECT id_antecedente FROM antecedente WHERE nome='Artista'), (SELECT id_pericia FROM pericia WHERE nome='Atuação')),

((SELECT id_antecedente FROM antecedente WHERE nome='Charlatão'), (SELECT id_pericia FROM pericia WHERE nome='Enganação')),
((SELECT id_antecedente FROM antecedente WHERE nome='Charlatão'), (SELECT id_pericia FROM pericia WHERE nome='Prestidigitação')),

((SELECT id_antecedente FROM antecedente WHERE nome='Criminoso'), (SELECT id_pericia FROM pericia WHERE nome='Enganação')),
((SELECT id_antecedente FROM antecedente WHERE nome='Criminoso'), (SELECT id_pericia FROM pericia WHERE nome='Furtividade')),

((SELECT id_antecedente FROM antecedente WHERE nome='Eremita'), (SELECT id_pericia FROM pericia WHERE nome='Medicina')),
((SELECT id_antecedente FROM antecedente WHERE nome='Eremita'), (SELECT id_pericia FROM pericia WHERE nome='Religião')),

((SELECT id_antecedente FROM antecedente WHERE nome='Forasteiro'), (SELECT id_pericia FROM pericia WHERE nome='Atletismo')),
((SELECT id_antecedente FROM antecedente WHERE nome='Forasteiro'), (SELECT id_pericia FROM pericia WHERE nome='Sobrevivência')),

((SELECT id_antecedente FROM antecedente WHERE nome='Herói do Povo'), (SELECT id_pericia FROM pericia WHERE nome='Adestrar Animais')),
((SELECT id_antecedente FROM antecedente WHERE nome='Herói do Povo'), (SELECT id_pericia FROM pericia WHERE nome='Sobrevivência')),

((SELECT id_antecedente FROM antecedente WHERE nome='Marinheiro'), (SELECT id_pericia FROM pericia WHERE nome='Atletismo')),
((SELECT id_antecedente FROM antecedente WHERE nome='Marinheiro'), (SELECT id_pericia FROM pericia WHERE nome='Percepção')),

((SELECT id_antecedente FROM antecedente WHERE nome='Nobre'), (SELECT id_pericia FROM pericia WHERE nome='História')),
((SELECT id_antecedente FROM antecedente WHERE nome='Nobre'), (SELECT id_pericia FROM pericia WHERE nome='Persuasão')),

((SELECT id_antecedente FROM antecedente WHERE nome='Órfão'), (SELECT id_pericia FROM pericia WHERE nome='Furtividade')),
((SELECT id_antecedente FROM antecedente WHERE nome='Órfão'), (SELECT id_pericia FROM pericia WHERE nome='Prestidigitação')),

((SELECT id_antecedente FROM antecedente WHERE nome='Sábio'), (SELECT id_pericia FROM pericia WHERE nome='Arcanismo')),
((SELECT id_antecedente FROM antecedente WHERE nome='Sábio'), (SELECT id_pericia FROM pericia WHERE nome='História')),

((SELECT id_antecedente FROM antecedente WHERE nome='Soldado'), (SELECT id_pericia FROM pericia WHERE nome='Atletismo')),
((SELECT id_antecedente FROM antecedente WHERE nome='Soldado'), (SELECT id_pericia FROM pericia WHERE nome='Intimidação'));

-- ============================================================================
-- TALENTOS (Livro do Jogador, Capítulo 6, págs. 167-172)
-- ============================================================================

INSERT INTO talento (nome, pre_requisito, descricao, id_criador) VALUES
(
    'Adepto Elemental',
    'Capacidade de conjurar pelo menos uma magia',
    'Escolha um tipo de dano entre ácido, elétrico, fogo, frio ou trovão. Suas magias ignoram resistência a esse tipo de dano e, ao rolar o dano de uma magia desse tipo, qualquer resultado 1 no dado de dano é tratado como 2. Pode ser escolhido diversas vezes para tipos diferentes.',
    NULL
),
(
    'Adepto Marcial',
    NULL,
    'Você aprende duas manobras à sua escolha do arquétipo Mestre de Batalha (CD = 8 + bônus de proficiência + modificador de Força ou Destreza) e recebe 1 dado de superioridade d6 adicional (ou passa a ter 1d6), recuperado após descanso curto ou longo.',
    NULL
),
(
    'Alerta',
    NULL,
    'Você recebe +5 de bônus em iniciativa, não pode ser surpreso enquanto estiver consciente e outras criaturas não ganham vantagem nas jogadas de ataque contra você por não serem visíveis por você.',
    NULL
),
(
    'Ambidestro',
    NULL,
    'Você ganha +1 de bônus na CA enquanto empunhar uma arma corpo-a-corpo em cada mão, pode usar combater com duas armas mesmo que as armas de uma mão não sejam leves, e pode sacar ou guardar duas armas de uma mão simultaneamente.',
    NULL
),
(
    'Atacante Bestial',
    NULL,
    'Uma vez por turno, quando você rolar o dano para um ataque corpo-a-corpo com arma, você pode jogar novamente o dado de dano da arma e usar qualquer dos valores.',
    NULL
),
(
    'Atirador Aguçado',
    NULL,
    'Atacar além da distância normal não impõe desvantagem com armas à distância; seus ataques à distância ignoram meia-cobertura e três-quartos de cobertura; antes de atacar com arma à distância proficiente, você pode sofrer -5 na jogada de ataque para adicionar +10 no dano se acertar.',
    NULL
),
(
    'Atirador de Magia',
    'Capacidade de conjurar pelo menos uma magia',
    'Dobra o alcance de magias que exijam jogada de ataque; seus ataques à distância com magia ignoram meia-cobertura e três-quartos de cobertura; você aprende um truque que requer jogada de ataque da lista de bardo, bruxo, clérigo, druida, feiticeiro ou mago.',
    NULL
),
(
    'Atleta',
    NULL,
    'Aumenta Força ou Destreza em 1 (máx. 20); levantar-se quando caído custa apenas 1,5 m de deslocamento; escalar não custa movimento adicional; saltos em distância ou altura correndo exigem apenas 1,5 m de impulso em vez de 3 m.',
    NULL
),
(
    'Ator',
    NULL,
    'Aumenta Carisma em 1 (máx. 20); vantagem em testes de Carisma (Atuação) e Carisma (Enganação) ao tentar se passar por outra pessoa; permite imitar a fala de outra pessoa ou sons de criaturas ouvidos por pelo menos 1 minuto.',
    NULL
),
(
    'Combatente Montado',
    NULL,
    'Enquanto montado e não incapacitado: vantagem em ataques corpo-a-corpo contra criaturas desmontadas menores que sua montaria; pode redirecionar ataques feitos contra a montaria para você; se a montaria passar em resistência de Destreza para metade do dano, não sofre dano algum (e apenas metade se falhar).',
    NULL
),
(
    'Conjurador de Guerra',
    'Capacidade de conjurar pelo menos uma magia',
    'Vantagem em testes de resistência de Constituição para manter concentração ao sofrer dano; pode realizar componentes somáticos mesmo com armas ou escudo nas mãos; pode usar sua reação para conjurar uma magia de 1 ação em uma única criatura como ataque de oportunidade.',
    NULL
),
(
    'Conjurador de Ritual',
    'Inteligência ou Sabedoria 13 ou maior',
    'Você adquire um livro de rituais com duas magias de 1º nível com o descritor ritual de uma classe escolhida (bardo, bruxo, clérigo, druida, feiticeiro ou mago) e pode copiar outras magias de ritual encontradas dessa classe (nível até metade do seu nível, arredondado para cima, levando 2 horas e 50 po por nível).',
    NULL
),
(
    'Curandeiro',
    NULL,
    'Ao usar um kit de primeiros-socorros para estabilizar uma criatura morrendo, ela recupera 1 PV; com uma ação e um uso do kit, você pode restaurar 1d6 + 4 + total de Dados de Vida da criatura em PV (uma vez por descanso curto ou longo por criatura).',
    NULL
),
(
    'Duelista Defensivo',
    'Destreza 13 ou maior',
    'Enquanto empunhar uma arma de acuidade na qual seja proficiente e for atingido por um ataque corpo-a-corpo, você pode usar sua reação para adicionar seu bônus de proficiência à sua CA para esse ataque.',
    NULL
),
(
    'Especialista em Besta',
    NULL,
    'Você ignora a propriedade recarga de bestas proficientes; estar a 1,5 m de criatura hostil não impõe desvantagem em ataques à distância; ao usar a ação de Ataque com uma arma de uma mão, pode usar a ação bônus para atacar com uma besta de mão carregada.',
    NULL
),
(
    'Especialista em Briga',
    NULL,
    'Aumenta Força ou Constituição em 1 (máx. 20); ataques desarmados causam 1d4 de dano; ao atingir com ataque desarmado ou arma improvisada no seu turno, pode usar uma ação bônus para tentar agarrar o alvo.',
    NULL
),
(
    'Explorador de Cavernas',
    NULL,
    'Vantagem em testes de Sabedoria (Percepção) e Inteligência (Investigação) para detectar portas secretas; vantagem em resistências contra armadilhas; resistência ao dano de armadilhas; pode procurar armadilhas viajando em ritmo normal.',
    NULL
),
(
    'Imobilizador',
    'Força 13 ou maior',
    'Você tem vantagem nas jogadas de ataque contra uma criatura agarrada por você e pode usar sua ação para realizar outro teste de agarrar a fim de imobilizá-la (deixando ambos impedidos até o agarre terminar).',
    NULL
),
(
    'Iniciado em Magia',
    NULL,
    'Escolha uma classe (bardo, bruxo, clérigo, druida, feiticeiro ou mago): você aprende dois truques e uma magia de 1º nível da lista dessa classe, podendo conjurar a magia de 1º nível uma vez por descanso longo.',
    NULL
),
(
    'Investida Poderosa',
    NULL,
    'Ao usar a ação de Disparada, pode usar sua ação bônus para realizar um ataque corpo-a-corpo ou empurrar uma criatura; se mover pelo menos 3 m em linha reta antes, ganha +5 no dano do ataque ou empurra o alvo até 3 m.',
    NULL
),
(
    'Líder Inspirador',
    'Carisma 13 ou maior',
    'Gastando 10 minutos inspirando até seis criaturas amigáveis a até 9 m que possam vê-lo ou ouvi-lo, cada uma ganha pontos de vida temporários iguais ao seu nível + seu modificador de Carisma (uma vez por descanso curto ou longo).',
    NULL
),
(
    'Maestria em Arma de Haste',
    NULL,
    'Ao atacar com glaive, alabarda ou bordão, pode usar uma ação bônus para atacar com a outra extremidade (1d4 de concussão); enquanto empunhar glaive, alabarda, lança longa ou bastão, criaturas provocam ataque de oportunidade ao entrar no seu alcance.',
    NULL
),
(
    'Maestria em Armadura Média',
    'Proficiência em armadura média',
    'Vestir armadura média não impõe desvantagem em Destreza (Furtividade) e permite adicionar até +3 de modificador de Destreza à CA (em vez de +2) se você tiver Destreza 16 ou maior.',
    NULL
),
(
    'Maestria em Armadura Pesada',
    'Proficiência em armadura pesada',
    'Aumenta Força em 1 (máx. 20) e, enquanto vestir armadura pesada, reduz em 3 o dano de concussão, cortante e perfurante recebido de ataques não mágicos.',
    NULL
),
(
    'Matador de Conjuradores',
    NULL,
    'Pode usar sua reação para atacar corpo-a-corpo uma criatura a até 1,5 m que conjurar uma magia; criaturas que sofrerem dano seu têm desvantagem para manter concentração; você tem vantagem em resistências contra magias conjuradas a até 1,5 m.',
    NULL
),
(
    'Mente Afiada',
    NULL,
    'Aumenta Inteligência em 1 (máx. 20); você sempre sabe a direção do norte e as horas restantes para o próximo nascer ou pôr do sol; pode relembrar com precisão qualquer coisa vista ou ouvida no último mês.',
    NULL
),
(
    'Mestre de Armas',
    NULL,
    'Aumenta Força ou Destreza em 1 (máx. 20) e você ganha proficiência com quatro armas simples ou marciais à sua escolha.',
    NULL
),
(
    'Mestre de Armas Grandes',
    NULL,
    'Ao obter acerto crítico corpo-a-corpo ou reduzir uma criatura a 0 PV no seu turno, pode fazer um ataque corpo-a-corpo como ação bônus; antes de atacar com arma pesada proficiente, pode sofrer -5 no ataque para adicionar +10 no dano se acertar.',
    NULL
),
(
    'Mestre de Escudo',
    NULL,
    'Ao realizar a ação de Ataque, pode usar ação bônus para tentar empurrar uma criatura a até 1,5 m com o escudo; soma o bônus de CA do escudo em testes de resistência de Destreza contra efeitos que tenham apenas você como alvo; usa reação para não sofrer dano ao passar em resistência de Destreza que reduziria o dano à metade.',
    NULL
),
(
    'Mobilidade',
    NULL,
    'Seu deslocamento aumenta em 3 metros; terreno difícil não custa movimento extra ao usar Disparada; realizar um ataque corpo-a-corpo contra uma criatura evita que ela provoque ataques de oportunidade de você pelo resto do turno.',
    NULL
),
(
    'Observador',
    NULL,
    'Aumenta Inteligência ou Sabedoria em 1 (máx. 20); pode ler lábios de idiomas que compreende; recebe +5 de bônus nos valores passivos de Sabedoria (Percepção) e Inteligência (Investigação).',
    NULL
),
(
    'Perito',
    NULL,
    'Você ganha proficiência em qualquer combinação de três perícias ou ferramentas, à sua escolha.',
    NULL
),
(
    'Poliglota',
    NULL,
    'Aumenta Inteligência em 1 (máx. 20); aprende três idiomas à sua escolha; pode criar criptogramas escritos cuja decifração exige ensino seu, teste de Inteligência (CD = seu valor de Inteligência + bônus de proficiência) ou magia.',
    NULL
),
(
    'Proteção Leve',
    NULL,
    'Aumenta Força ou Destreza em 1 (máx. 20) e você ganha proficiência com armaduras leves.',
    NULL
),
(
    'Proteção Moderada',
    'Proficiência em armadura leve',
    'Aumenta Força ou Destreza em 1 (máx. 20) e você ganha proficiência com armaduras médias e escudos.',
    NULL
),
(
    'Proteção Pesada',
    'Proficiência em armadura média',
    'Aumenta Força em 1 (máx. 20) e você ganha proficiência com armaduras pesadas.',
    NULL
),
(
    'Resiliente',
    NULL,
    'Escolha um valor de habilidade: aumenta esse valor em 1 (máx. 20) e você ganha proficiência nos testes de resistência dessa habilidade.',
    NULL
),
(
    'Resistente',
    NULL,
    'Aumenta Constituição em 1 (máx. 20) e, ao rolar um Dado de Vida para recuperar PV, o valor mínimo recuperado é igual a duas vezes seu modificador de Constituição (mínimo de 2).',
    NULL
),
(
    'Robusto',
    NULL,
    'Seu máximo de pontos de vida aumenta em um valor igual a duas vezes seu nível ao adquirir este talento, e aumenta em 2 pontos de vida adicionais a cada nível subsequente.',
    NULL
),
(
    'Sentinela',
    NULL,
    'Acertar um ataque de oportunidade reduz o deslocamento da criatura a 0 no turno; criaturas provocam ataque de oportunidade de você mesmo usando Desengajar; pode usar sua reação para atacar corpo-a-corpo uma criatura a até 1,5 m que ataque outro alvo sem este talento.',
    NULL
),
(
    'Sorrateiro',
    'Destreza 13 ou maior',
    'Pode tentar se esconder quando estiver levemente obscurecido; errar um ataque à distância enquanto escondido não revela sua posição; penumbra não impõe desvantagem nos testes de Sabedoria (Percepção) baseados na visão.',
    NULL
),
(
    'Sortudo',
    NULL,
    'Você possui 3 pontos de sorte (recuperados em descanso longo). Pode gastar 1 ponto de sorte para rolar um d20 adicional e escolher qual resultado usar em uma jogada de ataque, teste de habilidade, teste de resistência ou ataque feito contra você.',
    NULL
);

-- ============================================================================
-- 4. VIEWS
-- ============================================================================

CREATE VIEW vw_monstro_oficial AS
SELECT id_monstro, nome, tamanho, tipo, tendencia, ca, hp_maximo, deslocamento,
       forca, destreza, constituicao, inteligencia, sabedoria, carisma,
       desafio, xp, descricao
FROM monstro
WHERE id_criador IS NULL;

CREATE VIEW vw_campo_batalha_monstros AS
SELECT mm.id_mesa_monstro,
       mm.id_mesa,
       m.nome AS nome_mesa,
       mm.id_mestre,
       u.nome_usuario AS nome_mestre,
       mo.id_monstro,
       mo.nome AS monstro,
       mm.apelido,
       mo.tamanho,
       mo.tipo,
       mo.ca,
       mm.hp_atual,
       mm.hp_maximo,
       mo.deslocamento,
       mo.desafio,
       mo.xp,
       mm.iniciativa,
       mm.status
FROM mesa_monstro mm
JOIN mesa m ON m.id_mesa = mm.id_mesa
JOIN mestre me ON me.id_mestre = mm.id_mestre
JOIN usuario u ON u.id_usuario = me.id_usuario
JOIN monstro mo ON mo.id_monstro = mm.id_monstro;

CREATE VIEW vw_usuario_publico AS
SELECT id_usuario, nome_usuario, premium
FROM usuario;

CREATE VIEW vw_mesa_publica AS
SELECT id_mesa, nome, descricao, status
FROM mesa
WHERE mesa_privada = FALSE;

CREATE VIEW vw_magia_oficial AS
SELECT id_magia, nome, descricao
FROM magia
WHERE id_criador IS NULL;

CREATE VIEW vw_item_oficial AS
SELECT id_item, nome, descricao
FROM item
WHERE id_criador IS NULL;

CREATE VIEW vw_talento_oficial AS
SELECT id_talento, nome, pre_requisito, descricao
FROM talento
WHERE id_criador IS NULL;

CREATE VIEW vw_ficha_personagem AS
SELECT p.id_personagem, p.nome, p.nivel,
       c.nome AS classe, s.nome AS subclasse,
       r.nome AS raca, sr.nome AS sub_raca,
       ant.nome AS antecedente,
       a.forca, a.destreza, a.constituicao, a.inteligencia, a.sabedoria, a.carisma,
       p.hp_atual, p.hp_maximo, p.ca, p.deslocamento,
       p.id_player, p.id_mesa
FROM personagem p
JOIN classe c ON c.id_classe = p.id_classe
JOIN raca r ON r.id_raca = p.id_raca
JOIN atributos a ON a.id_atributos = p.id_atributos
LEFT JOIN subclasse s ON s.id_subclasse = p.id_subclasse
LEFT JOIN sub_raca sr ON sr.id_sub_raca = p.id_sub_raca
LEFT JOIN antecedente ant ON ant.id_antecedente = p.id_antecedente;

-- ============================================================================
-- 5. TRIGGER DE VALIDAÇÃO
-- ============================================================================

DELIMITER $$

CREATE TRIGGER trg_mesa_monstro_bi_valida
BEFORE INSERT ON mesa_monstro
FOR EACH ROW
BEGIN
    DECLARE v_mestre_mesa INT;
    DECLARE v_hp_base INT;

    SELECT id_mestre INTO v_mestre_mesa
    FROM mesa
    WHERE id_mesa = NEW.id_mesa;

    IF v_mestre_mesa IS NULL OR v_mestre_mesa <> NEW.id_mestre THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Apenas o mestre responsável pela mesa pode adicionar monstros ao campo de batalha';
    END IF;

    IF NEW.hp_maximo IS NULL OR NEW.hp_maximo <= 0 THEN
        SELECT hp_maximo INTO v_hp_base
        FROM monstro
        WHERE id_monstro = NEW.id_monstro;

        SET NEW.hp_maximo = v_hp_base;
    END IF;

    IF NEW.hp_atual IS NULL THEN
        SET NEW.hp_atual = NEW.hp_maximo;
    END IF;
END$$

CREATE TRIGGER trg_personagem_bi_valida
BEFORE INSERT ON personagem
FOR EACH ROW
BEGIN
    SET NEW.nome = TRIM(NEW.nome);
    IF CHAR_LENGTH(NEW.nome) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'O personagem precisa ter um nome';
    END IF;
END$$

CREATE TRIGGER trg_personagem_au_log
AFTER UPDATE ON personagem
FOR EACH ROW
BEGIN
    IF OLD.nivel <> NEW.nivel OR OLD.hp_atual <> NEW.hp_atual THEN
        INSERT INTO log_personagem
            (id_personagem, nivel_antigo, nivel_novo, hp_antigo, hp_novo, usuario, alterado_em)
        VALUES
            (OLD.id_personagem, OLD.nivel, NEW.nivel, OLD.hp_atual, NEW.hp_atual, USER(), NOW());
    END IF;
END$$

DELIMITER ;

-- ============================================================================
-- 6. PERMISSÕES DA ROLE DE CONSULTA E APLICAÇÃO
-- ============================================================================

CREATE ROLE IF NOT EXISTS 'ff_app', 'ff_consulta';

GRANT SELECT, INSERT, UPDATE, DELETE ON ff_rpg.* TO 'ff_app';

GRANT SELECT ON ff_rpg.vw_monstro_oficial TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_campo_batalha_monstros TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_usuario_publico TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_mesa_publica TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_magia_oficial TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_item_oficial TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_talento_oficial TO 'ff_consulta';
GRANT SELECT ON ff_rpg.vw_ficha_personagem TO 'ff_consulta';

-- ATENÇÃO: troque os valores abaixo por senhas suas ANTES de executar este script.
-- Não grave senhas reais neste arquivo nem no repositório. A aplicação lê a senha de
-- 'app_ff' pela variável de ambiente DB_PASSWORD (veja .env.example).
CREATE USER IF NOT EXISTS 'app_ff'@'localhost' IDENTIFIED BY 'TROQUE_A_SENHA_DO_APP';
CREATE USER IF NOT EXISTS 'relatorio_ff'@'localhost' IDENTIFIED BY 'TROQUE_A_SENHA_DO_RELATORIO';

GRANT 'ff_app' TO 'app_ff'@'localhost';
GRANT 'ff_consulta' TO 'relatorio_ff'@'localhost';

SET DEFAULT ROLE 'ff_app' TO 'app_ff'@'localhost';
SET DEFAULT ROLE 'ff_consulta' TO 'relatorio_ff'@'localhost';
