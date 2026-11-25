/* Mapeia o front ao schema MySQL. A gravação de personagens ainda não é feita pela API. */
const DBContract={
  id(value,label){const n=Number(value);if(!Number.isInteger(n)||n<1)throw new Error(`A API precisa fornecer ${label} como ID válido.`);return n;},
  usuario(user,refs={}){return{nome_usuario:String(user.name||''),email:String(user.email||''),senha:String(refs.senhaHash||''),id_conquista:refs.id_conquista??null,premium:Boolean(user.premium)};},
  player(userId){return{status:'ativo',id_usuario:this.id(userId,'id_usuario')};},
  mestre(userId,mesaId){return{status:'ativo',id_usuario:this.id(userId,'id_usuario'),id_mesa:this.id(mesaId,'id_mesa')};},
  mesa(table){return{mesa_privada:Boolean(table.priv),status:table.status==='encerrada'?'encerrada':'ativa',nome:String(table.name||''),descricao:String(table.desc||''),codigo_convite:String(table.code||'')};},
  personagem(character,refs){
    const fields={
      nome:String(character.name||''),
      nivel:Number(character.lvl),
      id_classe:this.id(refs.id_classe,'id_classe'),
      id_subclasse:this.id(refs.id_subclasse,'id_subclasse'),
      id_player:this.id(refs.id_player,'id_player'),
      id_atributos:this.id(refs.id_atributos,'id_atributos'),
      id_habilidade_classe:this.id(refs.id_habilidade_classe,'id_habilidade_classe'),
      id_habilidade_subclasse:this.id(refs.id_habilidade_subclasse,'id_habilidade_subclasse'),
      id_inventario:this.id(refs.id_inventario,'id_inventario'),
      id_magia:this.id(refs.id_magia,'id_magia'),
      id_raca:this.id(refs.id_raca,'id_raca'),
      HP_atual:Number(character.hp),
      HP_maximo:Number(character.maxHp),
      CA:Number(character.ac),
      deslocamento:Number(character.speed)
    };
    if(!fields.nome||!Number.isInteger(fields.nivel)||fields.nivel<1)throw new Error('Nome e nível válidos são obrigatórios.');
    if(![fields.HP_atual,fields.HP_maximo,fields.CA,fields.deslocamento].every(Number.isInteger)||fields.HP_atual<0||fields.HP_maximo<1||fields.HP_atual>fields.HP_maximo||fields.CA<0||fields.deslocamento<0)throw new Error('HP, CA e deslocamento precisam ser números inteiros válidos.');
    const attributes={forca:Number(character.attrs.for),destreza:Number(character.attrs.des),constituicao:Number(character.attrs.con),inteligencia:Number(character.attrs.int),sabedoria:Number(character.attrs.sab),carisma:Number(character.attrs.car)};
    const pericias=(character.skills||[]).map(key=>{const entry=SKILLS[key];if(!entry)throw new Error(`Perícia desconhecida: ${key}`);const id=refs.pericias?.[entry[0]];return{id_pericia:this.id(id,`id_pericia de ${entry[0]}`),proficiente:true};});
    // O schema não tem personagem.id_sub_raca; sub-raça e histórico de melhorias ficam locais.
    // atributos recebe os valores finais, incluindo bônus de raça e sub-raça aplicados pelo front.
    return{personagem:fields,atributos:attributes,pericias};
  }
};