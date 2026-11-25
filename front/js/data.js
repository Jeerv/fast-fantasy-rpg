/* Dados fixos do jogo: atributos, perícias, classes, itens e tamanho do tabuleiro */
const ATTRS=[['for','Força'],['des','Destreza'],['con','Constituição'],['int','Inteligência'],['sab','Sabedoria'],['car','Carisma']];
const SKILLS={acrobacia:['Acrobacia','des'],adestrar:['Adestrar Animais','sab'],arcanismo:['Arcanismo','int'],atletismo:['Atletismo','for'],atuacao:['Atuação','car'],enganacao:['Enganação','car'],furtividade:['Furtividade','des'],historia:['História','int'],intimidacao:['Intimidação','car'],intuicao:['Intuição','sab'],investigacao:['Investigação','int'],medicina:['Medicina','sab'],natureza:['Natureza','int'],percepcao:['Percepção','sab'],persuasao:['Persuasão','car'],prestidigitacao:['Prestidigitação','des'],religiao:['Religião','int'],sobrevivencia:['Sobrevivência','sab']};
const CLASSES=['Bárbaro','Bardo','Bruxo','Clérigo','Druida','Feiticeiro','Guerreiro','Ladino','Mago','Monge','Paladino','Patrulheiro'];
const SAVE_PROFICIENCIES={
  'Bárbaro':['for','con'],'Bardo':['des','car'],'Bruxo':['sab','car'],
  'Clérigo':['sab','car'],'Druida':['int','sab'],'Feiticeiro':['con','car'],
  'Guerreiro':['for','con'],'Ladino':['des','int'],'Mago':['int','sab'],
  'Monge':['for','des'],'Paladino':['sab','car'],'Patrulheiro':['for','des']
};
const CLASS_HIT_DICE={'Bárbaro':12,'Bardo':8,'Bruxo':8,'Clérigo':8,'Druida':8,'Feiticeiro':6,'Guerreiro':10,'Ladino':8,'Mago':6,'Monge':8,'Paladino':10,'Patrulheiro':10};
const CLASS_SKILL_RULES={
  'Bárbaro':{count:2,options:['adestrar','atletismo','intimidacao','natureza','percepcao','sobrevivencia']},
  'Bardo':{count:3,options:Object.keys(SKILLS)},
  'Bruxo':{count:2,options:['arcanismo','enganacao','historia','intimidacao','investigacao','natureza','religiao']},
  'Clérigo':{count:2,options:['historia','intuicao','medicina','persuasao','religiao']},
  'Druida':{count:2,options:['arcanismo','adestrar','intuicao','medicina','natureza','percepcao','religiao','sobrevivencia']},
  'Feiticeiro':{count:2,options:['arcanismo','enganacao','intuicao','intimidacao','persuasao','religiao']},
  'Guerreiro':{count:2,options:['acrobacia','adestrar','atletismo','historia','intuicao','intimidacao','percepcao','sobrevivencia']},
  'Ladino':{count:4,options:['acrobacia','atletismo','enganacao','intuicao','intimidacao','investigacao','percepcao','atuacao','persuasao','prestidigitacao','furtividade']},
  'Mago':{count:2,options:['arcanismo','historia','intuicao','investigacao','medicina','religiao']},
  'Monge':{count:2,options:['acrobacia','atletismo','historia','intuicao','religiao','furtividade']},
  'Paladino':{count:2,options:['atletismo','intuicao','intimidacao','medicina','persuasao','religiao']},
  'Patrulheiro':{count:3,options:['adestrar','atletismo','intuicao','investigacao','natureza','percepcao','furtividade','sobrevivencia']}
};
const ARMOR_CLASS_STATS={
  couro:{base:11,dexMax:null},
  couro_batido:{base:12,dexMax:null},
  cota_malha:{base:16,dexMax:0}
};
const STANDARD_ARRAY=[15,14,13,12,10,8];
const ASI_LEVELS={
  'Bárbaro':[4,8,12,16,19],'Bardo':[4,8,12,16,19],'Bruxo':[4,8,12,16,19],
  'Clérigo':[4,8,12,16,19],'Druida':[4,8,12,16,19],'Feiticeiro':[4,8,12,16,19],
  'Guerreiro':[4,6,8,12,14,16,19],'Ladino':[4,8,10,12,16,19],
  'Mago':[4,8,12,16,19],'Monge':[4,8,12,16,19],'Paladino':[4,8,12,16,19],
  'Patrulheiro':[4,8,12,16,19]
};
/* Opções lidas das tabelas raca, classe e subclasse do SQL enviado.
   Usar nomes no front; os IDs AUTO_INCREMENT devem vir da API quando existir. */
const RACES=['Anão','Elfo','Halfling','Humano','Draconato','Gnomo','Meio-Elfo','Meio-Orc','Tiefling'];
const SUBRACES_BY_RACE={
  'Anão':['Anão da Colina','Anão da Montanha'],
  'Elfo':['Alto Elfo','Elfo da Floresta','Elfo Negro (Drow)'],
  'Halfling':['Pés Leves','Robusto'],
  'Gnomo':['Gnomo da Floresta','Gnomo das Rochas']
};
/* Bônus extraídos dos INSERTs de raca_atributo e sub_raca_atributo. */
const RACE_ATTRIBUTE_BONUSES={
  'Anão':{con:2},'Elfo':{des:2},'Halfling':{des:2},
  'Humano':{for:1,des:1,con:1,int:1,sab:1,car:1},
  'Draconato':{for:2,car:1},'Gnomo':{int:2},'Meio-Elfo':{car:2},
  'Meio-Orc':{for:2,con:1},'Tiefling':{int:1,car:2}
};
const SUBRACE_ATTRIBUTE_BONUSES={
  'Anão da Colina':{sab:1},'Anão da Montanha':{for:2},'Alto Elfo':{int:1},
  'Elfo da Floresta':{sab:1},'Elfo Negro (Drow)':{car:1},
  'Pés Leves':{car:1},'Robusto':{con:1},'Gnomo da Floresta':{des:1},
  'Gnomo das Rochas':{con:1}
};
const SUBRACE_DESCRIPTIONS={
  'Anão da Colina':'Anão de sentidos aguçados, maior intuição e notável resiliência. Tenacidade Anã: +1 ponto de vida máximo e +1 a cada nível.',
  'Anão da Montanha':'Anão forte e resistente, acostumado a uma vida difícil em terrenos difíceis. Treinamento Anão com Armaduras: proficiência em armaduras leves e médias.',
  'Alto Elfo':'Elfo de mente afiada e domínio básico da magia. Tem treinamento élfico com armas, um truque de mago à escolha e um idioma adicional.',
  'Elfo da Floresta':'Elfo de sentidos e intuição aguçados, de pés ágeis, que se move rápida e furtivamente. Tem deslocamento de 10,5 m e Máscara da Natureza.',
  'Elfo Negro (Drow)':'Descendentes de elfos de pele negra banidos da superfície, que vivem no Subterrâneo. Visão no escuro superior, Magia Drow e sensibilidade à luz solar.',
  'Pés Leves':'Halfling que se esconde com facilidade, mesmo atrás de outras criaturas, afável e propenso a viajar. Furtividade Natural.',
  'Robusto':'Halfling mais resistente que a média, com resistência a venenos. Resiliência dos Robustos: vantagem em resistências contra veneno e resistência a dano de veneno.',
  'Gnomo da Floresta':'Gnomo com traquejo natural com ilusões, velocidade e furtividade. Tem Ilusionista Nato (truque ilusão menor) e Falar com Bestas Pequenas.',
  'Gnomo das Rochas':'Gnomo de inventividade e resistência acima dos outros gnomos. Tem Conhecimento de Artífice e Engenhoqueiro.'
};
const SUBCLASSES_BY_CLASS={
  'Bárbaro':['Caminho do Furioso','Caminho do Guerreiro Totêmico'],
  'Bardo':['Colégio do Conhecimento','Colégio da Bravura'],
  'Bruxo':['A Arquifada','O Corruptor','O Grande Antigo'],
  'Clérigo':['Domínio do Conhecimento','Domínio da Enganação','Domínio da Guerra','Domínio da Luz','Domínio da Natureza','Domínio da Tempestade','Domínio da Vida'],
  'Druida':['Círculo da Terra','Círculo da Lua'],
  'Feiticeiro':['Linhagem Dracônica','Magia Selvagem'],
  'Guerreiro':['Campeão','Cavaleiro Arcano','Mestre de Batalha'],
  'Ladino':['Assassino','Ladrão','Trapaceiro Arcano'],
  'Mago':['Escola de Abjuração','Escola de Adivinhação','Escola de Conjuração','Escola de Encantamento','Escola de Evocação','Escola de Ilusão','Escola de Necromancia','Escola de Transmutação'],
  'Monge':['Caminho da Mão Aberta','Caminho da Sombra','Caminho dos Quatro Elementos'],
  'Paladino':['Juramento de Devoção','Juramento dos Anciões','Juramento de Vingança'],
  'Patrulheiro':['Conclave da Besta','Conclave do Caçador','Conclave do Rastreador Subterrâneo']
};
const SQL_CONTRACT={usuario:['nome_usuario','email','senha','id_conquista','premium'],personagem:['nome','nivel','id_classe','id_subclasse','id_player','id_atributos','id_habilidade_classe','id_habilidade_subclasse','id_inventario','id_magia','id_raca','HP_atual','HP_maximo','CA','deslocamento'],atributos:['forca','destreza','constituicao','inteligencia','sabedoria','carisma'],personagem_pericia:['id_personagem','id_pericia','proficiente'],mesa:['mesa_privada','status','nome','descricao','codigo_convite'],raca:['nome','descricao'],sub_raca:['nome','descricao','id_raca'],raca_atributo:['id_raca','atributo','bonus'],sub_raca_atributo:['id_sub_raca','atributo','bonus']};
const SLOTS=[['arma','Arma'],['escudo','Escudo'],['armadura','Armadura'],['kit','Kit']];
const ITEMS={
  espada_longa:{name:'Espada Longa',slot:'arma',price:'15 po.',line:'1d8 cortante.',weight:'1,5 kg.',props:'Versátil (1d10)'},
  machado:{name:'Machado de Batalha',slot:'arma',price:'10 po.',line:'1d8 cortante.',weight:'2 kg.',props:'Versátil (1d10)'},
  espada_curta:{name:'Espada Curta',slot:'arma',price:'10 po.',line:'1d6 perfurante.',weight:'1 kg.',props:'Acuidade, leve'},
  adaga:{name:'Adaga',slot:'arma',price:'2 po.',line:'1d4 perfurante.',weight:'0,5 kg.',props:'Acuidade, leve, arremesso (6/18 m)'},
  arco_longo:{name:'Arco Longo',slot:'arma',price:'50 po.',line:'1d8 perfurante.',weight:'1 kg.',props:'Munição (45/180 m), pesada, duas mãos'},
  cajado:{name:'Bordão',slot:'arma',price:'2 pp.',line:'1d6 contundente.',weight:'2 kg.',props:'Versátil (1d8)'},
  escudo:{name:'Escudo',slot:'escudo',price:'10 po.',line:'+2 na CA.',weight:'3 kg.',props:''},
  couro:{name:'Armadura de Couro',slot:'armadura',price:'10 po.',line:'CA 11 + Destreza.',weight:'5 kg.',props:'Leve'},
  couro_batido:{name:'Couro Batido',slot:'armadura',price:'45 po.',line:'CA 12 + Destreza.',weight:'6,5 kg.',props:'Leve'},
  cota_malha:{name:'Cota de Malha',slot:'armadura',price:'75 po.',line:'CA 16.',weight:'27,5 kg.',props:'Pesada, exige Força 13, desvantagem em Furtividade'},
  kit_aventureiro:{name:'Kit de Aventureiro',slot:'kit',price:'10 po.',line:'',weight:'',props:'',desc:'Mochila, saco de dormir, kit de refeição, caixa de fogo, 10 tochas, 10 dias de rações, cantil e 15 m de corda.'},
  kit_masmorra:{name:'Kit de Masmorra',slot:'kit',price:'12 po.',line:'',weight:'',props:'',desc:'Mochila, pé de cabra, martelo, 10 pítons, 10 tochas, caixa de fogo, 10 dias de rações, cantil e 15 m de corda.'},
  kit_estudioso:{name:'Kit de Estudioso',slot:'kit',price:'40 po.',line:'',weight:'',props:'',desc:'Mochila, livro de conhecimento, tinta, pena, 10 folhas de pergaminho, saquinho de areia e uma faca pequena.'}
};
const GW=10,GH=8;
