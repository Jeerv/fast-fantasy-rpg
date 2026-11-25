/* Personagens: lista, ficha, formulário e detalhe de item */
SCREENS.chars=function(){const u=me();return hdr('Personagens')+`<div class="body">
  ${u.characters.length?u.characters.map(c=>`<button class="card item dark" onclick="go('char',{id:'${c.id}'})"><span class="cicon token-wrap">${tokenAvatar(c.tokenId??0,'character-token')}</span><span class="grow"><b>${esc(c.name)}</b><br><small class="muted">${esc(c.race||'Raça não definida')}${c.subrace?` · ${esc(c.subrace)}`:''} · ${esc(c.cls)}${c.subclass?` · ${esc(c.subclass)}`:''}</small></span><span>▶</span></button>`).join('')
  :`<p class="muted center" style="margin:30px 0">Você ainda não tem personagens.</p>`}
  <button class="btn big" style="font-size:26px;padding:10px" onclick="go('charForm')" aria-label="Novo personagem">+</button>
  ${u.characters.length?'':`<button class="btn big sm" style="margin-top:14px;font-size:14px" onclick="addExamples()">Adicionar personagens de exemplo</button>`}
  </div>`+nav('chars');};

SCREENS.char=function(r){const c=getChar(r.id);if(!c)return null;if(syncDerivedStats(c))save();const pb=prof(c.lvl);
  return hdr('Personagens',{right:`<button class="ibtn" onclick="go('charForm',{id:'${c.id}'})" aria-label="Editar">✎</button>`})+`<div class="body">
  <div class="chead"><span class="cicon yellow token-wrap">${tokenAvatar(c.tokenId??0,'detail-token')}</span><div><div class="muted sm">${esc(c.race||'Raça não definida')}${c.subrace?` · ${esc(c.subrace)}`:''} · ${esc(c.cls)}${c.subclass?` · ${esc(c.subclass)}`:''} · Nível ${c.lvl}</div><div class="cname">${esc(c.name)}</div></div></div>
  ${c.background||c.backstory?`<section class="card character-story"><h3>${esc(c.background||'Antecedente')}</h3><p class="muted sm">${esc(c.backstory||'')}</p></section>`:''}
  <button class="hprow" onclick="hpModal('${c.id}')"><span>HP</span>${hpBar(c)}<b>${c.hp}</b></button>
  <div class="two"><div class="chip">CA<b>${c.ac}</b></div><div class="chip">Velocidade<b>${c.speed}ft</b></div></div>
  ${ATTRS.map(([k,l])=>`<div class="arow"><button class="btn attr" onclick="rollCheck('${l}',${mod(c.attrs[k])})">${l}:</button><span class="box">${c.attrs[k]}</span><span class="box">${fmt(mod(c.attrs[k]))}</span></div>`).join('')}
  ${c.asiChoices?.length?`<div class="card sm"><b>Melhorias aplicadas</b><div class="muted">${c.asiChoices.map(x=>`Nível ${x.level}: ${x.mode==='two'?`+2 ${ATTRS.find(([k])=>k===x.a)?.[1]}`:`+1 ${ATTRS.find(([k])=>k===x.a)?.[1]} e +1 ${ATTRS.find(([k])=>k===x.b)?.[1]}`}`).join(' · ')}</div></div>`:''}
  <h3 class="h2">Testes de resistência (salvaguardas)</h3>
  <div class="card">${saveThrowRows(c)}</div>
  <h3 class="h2">Perícias</h3>
  <div class="card">${c.skills.length?c.skills.map(s=>{const [n,a]=SKILLS[s];const b=mod(c.attrs[a])+pb;return `<button class="skill" onclick="rollCheck('${n}',${b})"><span>${n}:</span><b>${fmt(b)}</b></button>`;}).join(''):'<span class="muted">Nenhuma perícia treinada.</span>'}
  <div class="muted sm" style="margin-top:6px">Bônus de proficiência: ${fmt(pb)} · toque para rolar</div></div>
  ${characterAbilitiesMarkup(c)}
  ${characterMagicMarkup(c)}
  <h3 class="h2">Equipamento</h3>
  ${SLOTS.map(([s,l])=>`<div class="eq"><select class="inp form-select" onchange="setEquip('${c.id}','${s}',this.value)"><option value="">Sem ${l.toLowerCase()}</option>${Object.entries(ITEMS).filter(([,i])=>i.slot===s).map(([k,i])=>`<option value="${k}" ${c.equip[s]===k?'selected':''}>${i.name}</option>`).join('')}</select>${c.equip[s]?`<button class="btn" onclick="go('item',{k:'${c.equip[s]}'})" aria-label="Detalhes">ⓘ</button>`:''}</div>`).join('')}
  <button class="btn red big" style="margin-top:20px;font-size:16px" onclick="delChar('${c.id}')">Excluir personagem</button></div>`;};

SCREENS.charForm=function(r){const e=r.id?getChar(r.id):null;if(r.id&&!e)return null;
  const legacy=e&&!e.baseAttrs,baseFromLegacy=e?.baseFromLegacy??legacy;
  const baseAttrs=e?.baseAttrs||(baseFromLegacy?e.attrs:standardBase(e?.attrs));
  const asiStartLevel=e?.asiStartLevel??(legacy?e.lvl:1);
  const c=e?Object.assign({},e,{baseAttrs,baseFromLegacy,asiStartLevel}):{name:'',race:'',subrace:'',raceBonusChoices:[],cls:'Guerreiro',subclass:'',lvl:1,maxHp:10,ac:10,speed:30,tokenId:0,attrs:{for:15,des:14,con:13,int:12,sab:10,car:8},baseAttrs:{for:15,des:14,con:13,int:12,sab:10,car:8},baseFromLegacy:false,asiStartLevel:1,asiChoices:[],skills:[],equip:{}};
  const legacyMsg=c.baseFromLegacy?'<p class="muted sm">Esta ficha foi criada antes do registro de melhorias. Os valores atuais foram preservados; as próximas melhorias serão registradas a partir do nível atual.</p>':'';
  const arrayMsg=e&&!c.baseFromLegacy&&!isStandardArray(e.baseAttrs||e.attrs)?'<p class="err">A ficha antiga não usa a matriz padrão; os valores exibidos foram organizados na distribuição padrão. Revise antes de salvar.</p>':'';
  return hdr(e?'Editar Personagem':'Novo Personagem')+`<form id="char-form" class="body form-page" onsubmit="saveChar(event,'${r.id||''}')">
  <input type="hidden" name="asiStartLevel" value="${c.asiStartLevel}"><input type="hidden" name="baseFromLegacy" value="${c.baseFromLegacy?'true':'false'}">
  <label class="fl">Nome</label><input class="inp form-control" name="name" required maxlength="100" value="${esc(c.name)}" placeholder="Ex.: Thorim Pedraforte">
  <label class="fl">Raça</label><select class="inp form-select" name="race" required onchange="updateRaceOptions(this.value)"><option value="">Selecione uma raça</option>${RACES.map(x=>`<option value="${esc(x)}" ${c.race===x?'selected':''}>${esc(x)}</option>`).join('')}</select>
  <label class="fl">Sub-raça (quando houver)</label><select class="inp form-select" id="subrace-select" name="subrace" ${SUBRACES_BY_RACE[c.race]?.length?'required':'disabled'} onchange="updateRaceBonusSummary()">${subraceOptions(c.race,c.subrace)}</select>
  <p id="subrace-description" class="muted sm">${esc(SUBRACE_DESCRIPTIONS[c.subrace]||'')}</p>
  <div id="flex-race-bonuses">${raceBonusChoicesForm(c.race,c.raceBonusChoices||[])}</div>
  <p id="race-bonus-summary" class="muted sm">${esc(raceBonusSummary(c.race,c.subrace,c.raceBonusChoices||[],c.baseFromLegacy))}</p>
  <label class="fl">Classe</label><select class="inp form-select" name="cls" onchange="updateSubclasses(this.value);updateASIForm();updateClassSkillOptions(this.value);updateDerivedStats()">${CLASSES.map(x=>`<option ${c.cls===x?'selected':''}>${x}</option>`).join('')}</select>
  <label class="fl">Subclasse</label><select class="inp form-select" id="subclass-select" name="subclass" required onchange="updateDerivedStats()">${subclassOptions(c.cls,c.subclass)}</select>
  <div class="g3"><div><label class="fl">Nível</label><input class="inp form-control" type="number" name="lvl" min="${e?c.lvl:1}" max="20" value="${c.lvl}" onchange="updateASIForm()"></div><div><label class="fl">HP máx. automático</label><input class="inp form-control" type="number" id="derived-max-hp" value="${calculateMaxHp(c)}" readonly></div><div><label class="fl">CA automática</label><input class="inp form-control" type="number" id="derived-ac" value="${calculateArmorClass(c)}" readonly></div></div>
  <p class="muted sm">PV máximo usa o valor máximo no nível 1 e a média fixa do dado de vida nos níveis seguintes, somando Constituição. A CA considera o equipamento selecionado e as características de classe implementadas.</p>
  <p class="muted sm">Raça, classe, subclasse e nível correspondem às tabelas do SQL. O SQL tem colunas para HP atual/máximo, CA e deslocamento, mas as fichas ainda não são sincronizadas pela API. A sub-raça não tem vínculo com personagem; token e histórico de melhorias também permanecem locais.</p>
  <label class="fl">Velocidade (ft)</label><input class="inp form-control" type="number" name="speed" min="0" max="200" value="${c.speed}">
  <label class="fl">${c.baseFromLegacy?'Atributos atuais (base para novas melhorias)':'Atributos base no nível 1'}</label>${arrayMsg}${legacyMsg}<div class="attribute-grid">${attributeFields(c.baseAttrs,c.baseFromLegacy)}</div>${c.baseFromLegacy?'':'<p id="array-warning" class="err" hidden>Os seis valores devem ser diferentes e pertencer à matriz padrão.</p>'}
  ${c.baseFromLegacy?'':`<p class="muted sm">Distribua 15, 14, 13, 12, 10 e 8, usando cada valor uma vez. A matriz é antes dos bônus de raça/sub-raça cadastrados no SQL; os atributos escolhidos para o Meio-Elfo seguem a descrição da raça no SQL.</p>`}
  <label class="fl">Melhorias por nível de classe</label><p class="muted sm">Em cada nível elegível, escolha +2 em um atributo ou +1 em dois atributos diferentes. O máximo por melhoria é 20.</p><div id="asi-list">${asiFields(c.cls,c.lvl,c.asiStartLevel,c.asiChoices||[])}</div>
  <label class="fl">Token do personagem</label>${tokenPicker('tokenId',c.tokenId??0)}<label class="fl">Perícias de classe (escolha ${CLASS_SKILL_RULES[c.cls]?.count||0})</label><p class="muted sm">Selecione exatamente a quantidade indicada entre as opções da classe. Perícias de antecedente ainda não são modeladas.</p><div id="class-skill-options">${classSkillFields(c.cls,c.skills||[])}</div><p id="skill-count" class="muted sm">Selecionadas ${Math.min((c.skills||[]).filter(key=>(CLASS_SKILL_RULES[c.cls]?.options||[]).includes(key)).length,CLASS_SKILL_RULES[c.cls]?.count||0)} de ${CLASS_SKILL_RULES[c.cls]?.count||0}</p>
  <label class="fl">Equipamento</label>${SLOTS.map(([s,l])=>`<select class="inp form-select" name="e_${s}" style="margin-bottom:8px" onchange="updateDerivedStats()"><option value="">Sem ${l.toLowerCase()}</option>${Object.entries(ITEMS).filter(([,i])=>i.slot===s).map(([k,i])=>`<option value="${k}" ${c.equip[s]===k?'selected':''}>${i.name}</option>`).join('')}</select>`).join('')}
  <button class="btn big" style="margin-top:16px">${e?'Salvar':'Criar personagem'}</button></form>`;};

function featureRowsMarkup(rows){
  if(!rows.length)return '<p class="muted sm">Nenhuma habilidade desta categoria foi alcançada até este nível.</p>';
  return rows.map(feature=>`<details class="feature-entry"><summary><b>${esc(feature.name)}</b><small class="muted"> · Nível ${feature.level}</small></summary><p class="muted sm" style="white-space:pre-line">${esc(feature.description)}</p></details>`).join('');
}
function characterAbilitiesMarkup(c){
  const classRow=CLASS_CATALOG.find(row=>row.name===c.cls),subclassRow=SUBCLASS_CATALOG.find(row=>row.name===c.subclass&&row.classId===classRow?.id);
  const classRows=classRow?CLASS_FEATURE_CATALOG.filter(row=>row.classId===classRow.id&&row.level<=Number(c.lvl)).sort((a,b)=>a.level-b.level||a.id-b.id):[];
  const subclassRows=subclassRow?SUBCLASS_FEATURE_CATALOG.filter(row=>row.subclassId===subclassRow.id&&row.level<=Number(c.lvl)).sort((a,b)=>a.level-b.level||a.id-b.id):[];
  return `<h3 class="h2">Habilidades</h3><div class="card"><h4>Classe · ${esc(c.cls)}</h4>${featureRowsMarkup(classRows)}<h4>Subclasse · ${esc(c.subclass||'Não definida')}</h4>${featureRowsMarkup(subclassRows)}<p class="muted sm">Lista derivada das tabelas de habilidades e do nível atual da ficha.</p></div>`;
}
function spellLevelOf(spell){
  const description=String(spell?.description||'').trim();
  if(/^truque\b/i.test(description))return 0;
  const match=description.match(/^([1-9])(?:º|°|o)\s*n[ií]vel/i);
  return match?Number(match[1]):null;
}
function spellLevelLabel(level){return level===0?'Truques':level<0?'Sem magia de classe':Number.isInteger(level)?`${level}º nível`:'Nível não identificado';}
const SPELL_CLASS_LISTS={"Bardo":[1,2,6,11,12,13,14,15,17,18,27,29,39,43,47,51,53,54,57,58,59,61,62,63,70,72,73,74,77,83,87,88,89,91,94,97,101,102,103,107,109,110,114,116,117,118,121,122,125,127,139,141,142,146,149,150,157,164,165,166,168,171,173,174,175,179,184,185,187,194,199,206,207,218,219,221,223,227,232,235,236,239,244,252,253,254,257,258,261,262,270,271,272,273,275,277,288,291,294,302,305,307,311,312,316,318,319,321,322,323,325,333,334,338,339,341,352,354,355,360],"Bruxo":[1,2,12,14,17,18,21,22,25,32,36,37,39,53,54,75,79,81,82,87,102,107,109,112,117,118,127,129,131,135,137,142,156,160,165,170,172,174,175,179,185,191,196,198,199,204,220,221,227,232,248,258,273,275,277,281,282,283,286,300,305,307,310,312,318,327,333,339,341,342,347,352,355,357,358,360],"Cl\u00e9rigo":[4,6,10,13,15,16,23,24,34,38,42,43,47,48,49,56,72,74,75,76,80,85,91,92,94,95,98,103,111,117,122,128,133,138,139,141,147,149,151,152,156,157,162,165,166,167,168,171,173,174,177,180,186,189,191,193,194,195,199,204,210,216,219,222,223,228,235,240,241,244,247,252,256,261,267,268,270,271,275,276,279,286,287,290,291,299,301,307,309,312,317,321,322,323,326,327,329,330,332,344,349,357,358,359],"Druida":[1,3,5,6,7,8,13,16,21,23,29,35,40,42,43,47,49,53,57,58,59,69,70,72,73,76,84,94,108,111,113,114,117,119,121,122,123,125,130,132,133,136,139,140,148,150,151,158,161,165,171,177,180,182,183,189,192,199,207,208,209,210,212,217,219,220,221,222,223,224,226,230,232,233,235,237,241,242,245,251,252,253,261,264,267,268,269,270,275,278,280,283,287,291,296,297,303,306,313,316,321,322,325,326,327,328,332,335,336,344,345,346,359,360,361],"Feiticeiro":[1,2,6,9,11,12,13,14,15,17,18,19,20,21,25,26,31,39,47,51,53,55,63,65,66,68,69,70,71,77,78,79,81,84,88,90,93,94,97,99,103,107,109,110,112,116,117,118,120,123,126,127,129,131,134,137,142,143,146,148,151,155,157,160,165,172,174,175,176,177,179,183,184,185,188,189,190,192,197,198,199,204,207,212,218,220,221,224,226,227,230,236,239,243,249,257,258,264,265,267,268,272,274,277,282,285,289,293,296,300,303,305,307,308,310,312,313,320,325,326,327,333,335,340,341,344,348,350,355,356,357],"Mago":[1,2,6,9,11,12,13,14,15,17,18,19,20,21,25,26,28,30,31,39,41,47,50,51,53,54,55,62,63,65,66,68,69,70,71,73,75,77,78,79,81,83,84,85,87,88,90,93,96,97,99,100,101,103,107,109,110,112,113,115,116,117,118,120,122,123,126,127,129,131,134,135,137,138,142,143,144,145,146,148,152,155,156,157,160,164,165,166,171,172,173,174,175,176,179,181,183,184,185,187,188,189,190,191,192,194,196,197,198,199,200,201,204,205,207,208,210,213,214,215,218,219,220,221,222,224,225,226,227,229,230,231,232,235,236,239,243,244,245,248,249,250,254,257,258,259,260,261,262,263,264,265,272,273,274,275,277,281,282,284,285,286,288,289,292,293,294,295,296,298,300,302,303,304,305,307,308,310,311,312,313,314,316,318,319,320,323,324,325,327,328,330,331,332,333,335,337,338,340,341,342,343,346,347,348,350,351,352,353,355,356,357,358,360],"Paladino":[13,33,34,38,43,44,45,46,47,48,49,52,56,61,75,76,92,96,105,122,124,133,139,149,153,154,156,162,163,165,177,178,191,193,199,202,203,204,211,219,228,238,255,256,261,266,271],"Patrulheiro":[1,13,28,29,35,43,47,49,57,58,60,67,69,73,84,86,106,108,111,121,122,125,130,132,133,139,140,141,148,150,151,158,159,164,169,177,182,189,192,199,209,219,223,226,233,234,237,241,242,246]};
function spellListKeyForCharacter(c){
  if((c.cls==='Guerreiro'&&c.subclass==='Cavaleiro Arcano')||(c.cls==='Ladino'&&c.subclass==='Trapaceiro Arcano'))return'Mago';
  return Object.prototype.hasOwnProperty.call(SPELL_CLASS_LISTS,c.cls)?c.cls:null;
}
function spellClassIdsForCharacter(c){const key=spellListKeyForCharacter(c);return new Set(key?SPELL_CLASS_LISTS[key]:[]);}
function spellClassContextMarkup(c){
  const key=spellListKeyForCharacter(c);
  const label=key?(key===c.cls?`Lista de ${esc(key)}.`:`Lista de ${esc(key)} para ${esc(c.subclass)}.`):`${esc(c.cls)} não possui lista de magias de classe cadastrada no PHB 2014.`;
  return `<p class="muted sm">${label} O filtro usa as listas base do livro; magias automáticas de subclasse e restrições por escola do Cavaleiro Arcano e do Trapaceiro Arcano não são aplicadas.</p>`;
}
function maxSpellLevel(c){
  const level=Math.max(1,Math.min(20,Number(c.lvl)||1));
  if(['Bardo','Clérigo','Druida','Feiticeiro','Mago'].includes(c.cls))return Math.min(9,Math.ceil(level/2));
  if(c.cls==='Bruxo'){
    const pactLevel=Math.min(5,Math.ceil(level/2));
    const arcanumLevel=level>=17?9:level>=15?8:level>=13?7:level>=11?6:0;
    return Math.max(pactLevel,arcanumLevel);
  }
  if(c.cls==='Paladino'||c.cls==='Patrulheiro')return level<2?-1:Math.min(5,Math.floor((level+3)/4));
  const thirdCaster=[0,0,0,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,4,4];
  if(c.cls==='Guerreiro'&&c.subclass==='Cavaleiro Arcano')return thirdCaster[level]||-1;
  if(c.cls==='Ladino'&&c.subclass==='Trapaceiro Arcano')return thirdCaster[level]||-1;
  return -1;
}
function canCastClassCantrips(c){
  if(['Bardo','Bruxo','Clérigo','Druida','Feiticeiro','Mago'].includes(c.cls))return true;
  const level=Math.max(1,Math.min(20,Number(c.lvl)||1));
  return (c.cls==='Guerreiro'&&c.subclass==='Cavaleiro Arcano'||c.cls==='Ladino'&&c.subclass==='Trapaceiro Arcano')&&level>=3;
}
function canAccessSpell(c,spell){
  const level=spellLevelOf(spell);
  if(level===0)return canCastClassCantrips(c);
  return Number.isInteger(level)&&level>=1&&level<=maxSpellLevel(c);
}
function characterSpellIds(c){
  const values=Array.isArray(c.spellIds)?c.spellIds:(c.id_magia?[c.id_magia]:[]);
  return Array.from(new Set(values.map(Number).filter(Number.isInteger)));
}
const SPELL_CANTRIP_LIMITS={
  Bardo:[2,2,2,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4],
  Clérigo:[3,3,3,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5],
  Druida:[2,2,2,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4],
  Feiticeiro:[4,4,4,5,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6,6],
  Bruxo:[2,2,2,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4],
  Mago:[3,3,3,4,4,4,4,4,4,5,5,5,5,5,5,5,5,5,5,5],
  Guerreiro:[0,0,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3],
  Ladino:[0,0,3,3,3,3,3,3,3,4,4,4,4,4,4,5,5,5,5,5]
};
const SPELL_KNOWN_LIMITS={
  Bardo:[4,5,6,7,8,9,10,11,12,14,15,15,16,18,19,19,20,22,22,22],
  Feiticeiro:[2,3,4,5,6,7,8,9,10,11,12,12,13,13,14,14,15,15,15,15],
  Bruxo:[2,3,4,5,6,7,8,9,10,10,11,11,12,12,13,13,14,14,15,15],
  Patrulheiro:[0,2,3,3,4,4,4,5,5,6,6,7,7,8,8,9,9,10,10,11],
  Guerreiro:[0,0,3,4,4,4,5,6,6,7,8,8,9,10,10,11,11,11,12,13],
  Ladino:[0,0,3,4,4,4,5,6,6,7,8,8,9,10,10,11,11,11,12,13]
};
function spellSelectionLimits(c){
  const level=Math.max(1,Math.min(20,Number(c.lvl)||1)),index=level-1;
  const cantrips=SPELL_CANTRIP_LIMITS[c.cls]?.[index]??0;
  let spells=0,label='Magias';
  if(c.cls==='Bardo'||c.cls==='Feiticeiro'||c.cls==='Bruxo'||c.cls==='Patrulheiro'){
    spells=SPELL_KNOWN_LIMITS[c.cls][index];label='Conhecidas';
    if(c.cls==='Bruxo')spells+=Number(level>=11)+Number(level>=13)+Number(level>=15)+Number(level>=17);
    if(c.cls==='Bruxo'&&level>=11)label='Conhecidas + Arcanum';
  }else if(c.cls==='Mago'){
    spells=6+2*(level-1);label='Grimório';
  }else if(c.cls==='Clérigo'||c.cls==='Druida'){
    const ability=mod(Number(c.attrs?.sab??10));spells=Math.max(1,ability+level);label='Preparadas';
  }else if(c.cls==='Paladino'){
    const ability=mod(Number(c.attrs?.car??10));spells=level<2?0:Math.max(1,ability+Math.floor(level/2));label='Preparadas';
  }else if(c.cls==='Guerreiro'&&c.subclass==='Cavaleiro Arcano'){
    spells=SPELL_KNOWN_LIMITS.Guerreiro[index];label='Conhecidas';
  }else if(c.cls==='Ladino'&&c.subclass==='Trapaceiro Arcano'){
    spells=SPELL_KNOWN_LIMITS.Ladino[index];label='Conhecidas';
  }
  return {cantrips,spells,label};
}
function characterSpellCounts(c){
  let cantrips=0,spells=0;
  for(const id of characterSpellIds(c)){
    const entry=SPELL_CATALOG.find(item=>item.id===id);
    if(entry&&spellLevelOf(entry)===0)cantrips++;else spells++;
  }
  return {cantrips,spells};
}
function spellCapacityMarkup(c){
  const limits=spellSelectionLimits(c),counts=characterSpellCounts(c);
  const over=counts.cantrips>limits.cantrips||counts.spells>limits.spells;
  return `<p class="muted sm">Capacidade: Truques ${counts.cantrips}/${limits.cantrips} · ${esc(limits.label)} ${counts.spells}/${limits.spells}${over?' · acima do limite; remova opções para liberar espaço':''}.</p>`;
}
function spellSelectionBlock(c,spell,alreadySelected=false){
  if(alreadySelected)return '';
    if(!spellClassIdsForCharacter(c).has(spell.id))return 'Esta magia não pertence à lista de classe usada por esta ficha.';
  const level=spellLevelOf(spell);
  if(!canAccessSpell(c,spell))return level===null?'O nível desta magia não pôde ser identificado no catálogo.':`${spellLevelLabel(level)} ainda não está disponível para esta classe e nível.`;
  const limits=spellSelectionLimits(c),counts=characterSpellCounts(c);
  if(level===0&&counts.cantrips>=limits.cantrips)return `Limite de truques atingido (${limits.cantrips}). Remova um truque escolhido antes de adicionar outro.`;
  if(level>=1&&counts.spells>=limits.spells)return `Limite de ${limits.label.toLocaleLowerCase('pt-BR')} atingido (${limits.spells}). Remova uma magia escolhida antes de adicionar outra.`;
  if(c.cls==='Bruxo'&&level>=6){
    const hasArcanum=characterSpellIds(c).some(id=>{
      const chosen=SPELL_CATALOG.find(row=>row.id===id);
      return chosen&&spellLevelOf(chosen)===level;
    });
    if(hasArcanum)return `A ficha já tem uma Magia Mística de ${spellLevelLabel(level)}.`;
  }
  return '';
}
function characterMagicMarkup(c){
  const maxLevel=maxSpellLevel(c),entries=characterSpellIds(c).map(id=>SPELL_CATALOG.find(spell=>spell.id===id)).filter(Boolean);
  const levels=Array.from(new Set(entries.map(spellLevelOf))).sort((a,b)=>(a??99)-(b??99));
  const selected=entries.length?levels.map(level=>`<h4>${spellLevelLabel(level)}</h4>`+entries.filter(spell=>spellLevelOf(spell)===level).map(spell=>{
    const locked=!canAccessSpell(c,spell);
    return `<div class="spell-picked"><div class="row between"><b>${esc(spell.name)}${locked?'<small class="muted sm"> · acima do limite atual</small>':''}</b><button class="btn sm" onclick="toggleCharacterSpell('${c.id}',${spell.id})">Remover</button></div><details><summary class="muted sm">Ver descrição</summary><p class="muted sm" style="white-space:pre-line">${esc(spell.description)}</p></details></div>`;
  }).join('')).join(''):'<p class="muted sm">Nenhuma magia escolhida para esta ficha.</p>';
  return `<h3 class="h2">Magias</h3><div class="card"><p class="muted sm">Maior círculo disponível: ${spellLevelLabel(maxLevel)} para ${esc(c.cls)} nível ${c.lvl}.</p>${spellCapacityMarkup(c)}${selected}<button class="btn big" style="margin-top:10px;font-size:15px" onclick="go('charSpells',{id:'${c.id}'})">Buscar no catálogo SQL (${SPELL_CATALOG.length})</button><p class="muted sm" style="margin-top:8px">Quando a capacidade de truques ou magias estiver cheia, remova uma escolhida para abrir espaço. A seleção fica local. O filtro de classe fica no front; a tabela MySQL não relaciona magias à lista da classe nem marca magias automáticas de subclasse; cópias adicionais de grimório também não são contabilizadas.</p></div>`;
}
function normalizedSpellSearch(value){return String(value||'').trim().toLocaleLowerCase('pt-BR');}
function spellCatalogRows(characterId,query='',levelFilter='all',accessFilter='available'){
  const c=getChar(characterId);if(!c)return '<p class="muted">Personagem não encontrado.</p>';
  const q=normalizedSpellSearch(query),selected=new Set(characterSpellIds(c)),classSpellIds=spellClassIdsForCharacter(c),maxLevel=maxSpellLevel(c);
  const matches=SPELL_CATALOG.filter(spell=>{
    const level=spellLevelOf(spell),text=spell.name.toLocaleLowerCase('pt-BR')+' '+spell.description.toLocaleLowerCase('pt-BR');
    if(q&&!text.includes(q))return false;
    if(levelFilter!=='all'&&String(level)!==levelFilter)return false;
    if(accessFilter==='selected'){if(!selected.has(spell.id))return false;}
    else if(!classSpellIds.has(spell.id))return false;
    if(accessFilter==='available'&&!selected.has(spell.id)&&spellSelectionBlock(c,spell))return false;
    return true;
  });
  const counter=spellClassContextMarkup(c)+spellCapacityMarkup(c)+`<p class="muted sm">Maior círculo disponível: ${spellLevelLabel(maxLevel)}.</p>`;
  if(!matches.length){const message=accessFilter==='selected'?'Nenhuma magia selecionada nesta ficha.':classSpellIds.size?'Nenhuma magia da lista desta classe corresponde aos filtros ou cabe nos limites atuais.':'Esta classe/subclasse não possui lista de magias para adicionar; veja “Selecionadas” para remover escolhas antigas.';return counter+`<p class="muted">${message}</p>`;}
  const levels=Array.from(new Set(matches.map(spellLevelOf))).sort((a,b)=>(a??99)-(b??99));
  const open=Boolean(q)||levelFilter!=='all'||accessFilter!=='available';
  return counter+`<p class="muted sm">${matches.length} magia(s) neste filtro</p>`+levels.map(level=>{
    const group=matches.filter(spell=>spellLevelOf(spell)===level);
    return `<details class="spell-level-group" ${open?'open':''}><summary><b>${spellLevelLabel(level)}</b><small class="muted"> · ${group.length}</small></summary><div class="spell-level-list">${group.map(spell=>{
      const already=selected.has(spell.id),block=spellSelectionBlock(c,spell,already),action=already?'Remover':block?'Bloqueada':'Adicionar';
      const outsideClass=already&&!classSpellIds.has(spell.id);
      return `<article class="card spell-option"><div class="row between"><b>${esc(spell.name)}</b><button class="btn sm" ${!already&&block?'disabled':''} onclick="toggleCharacterSpell('${characterId}',${spell.id})">${action}</button></div>${outsideClass?'<p class="muted sm">Fora da lista atual da classe; ainda pode remover.</p>':''}${!already&&block?`<p class="muted sm">${esc(block)}</p>`:''}<details><summary class="muted sm">Descrição do banco</summary><p class="muted sm" style="white-space:pre-line">${esc(spell.description)}</p></details></article>`;
    }).join('')}</div></details>`;
  }).join('');
}
function filterCharacterSpells(characterId){
  const root=$('#spell-results');if(!root)return;
  root.innerHTML=spellCatalogRows(characterId,$('#spell-search')?.value||'',$('#spell-level-filter')?.value||'all',$('#spell-access-filter')?.value||'available');
}
function toggleCharacterSpell(characterId,spellId){
  const c=getChar(characterId);if(!c)return;
  const spell=SPELL_CATALOG.find(item=>item.id===Number(spellId)),ids=characterSpellIds(c),has=ids.includes(Number(spellId));
  if(!spell)return;
  if(!has){const block=spellSelectionBlock(c,spell);if(block)return toast(block);}
  c.spellIds=has?ids.filter(id=>id!==Number(spellId)):[...ids,Number(spellId)];
  save();
  if($('#spell-results'))filterCharacterSpells(characterId);else render();
  toast(has?'Magia removida da ficha':'Magia adicionada à ficha');
}
SCREENS.charSpells=function(r){
  const c=getChar(r.id);if(!c)return null;
  const levelOptions='<option value="all">Todos os níveis</option><option value="0">Truques</option>'+Array.from({length:9},(_,i)=>`<option value="${i+1}">${i+1}º nível</option>`).join('');
  return hdr('Magias do personagem')+`<div class="body"><p class="muted sm">Limite da ficha: ${spellLevelLabel(maxSpellLevel(c))}. A seleção tem capacidade por tipo de conjurador; ao lotar, remova uma magia escolhida.</p>${spellCapacityMarkup(c)}<label class="fl">Buscar por nome ou descrição</label><input class="inp form-control" id="spell-search" type="search" placeholder="Ex.: Mísseis Mágicos" oninput="filterCharacterSpells('${c.id}')"><div class="g2"><div><label class="fl">Nível da magia</label><select class="inp form-select" id="spell-level-filter" onchange="filterCharacterSpells('${c.id}')">${levelOptions}</select></div><div><label class="fl">Exibir</label><select class="inp form-select" id="spell-access-filter" onchange="filterCharacterSpells('${c.id}')"><option value="available">Selecionáveis da classe</option><option value="all">Todas da classe (inclui bloqueadas)</option><option value="selected">Selecionadas na ficha</option></select></div></div><div id="spell-results">${spellCatalogRows(c.id,'','all','available')}</div></div>`;
};SCREENS.item=function(r){const i=ITEMS[r.k];if(!i)return null;
  return hdr('')+`<div class="body"><h2 class="hello center" style="font-size:30px">${i.name}</h2><div class="itemart">${ART[i.slot]}</div>
  <div class="card" style="font-size:18px">${[i.price,i.line,i.weight,i.props].filter(Boolean).map(esc).join('<br>')}${i.desc?`<div style="margin-top:8px;font-size:15px">${esc(i.desc)}</div>`:''}</div></div>`;};

function calculateMaxHp(c){
  const die=CLASS_HIT_DICE[c.cls],level=Math.max(1,Math.min(20,Number(c.lvl)||1));
  if(!die)return 0;
  const constitution=mod(c.attrs?.con??10),firstLevel=Math.max(1,die+constitution),laterLevel=Math.max(1,Math.floor(die/2)+1+constitution);
  let total=firstLevel+(level-1)*laterLevel;
  if(c.subrace==='Anão da Colina')total+=level;
  if(c.cls==='Feiticeiro'&&c.subclass==='Linhagem Dracônica')total+=level;
  return total;
}
function calculateArmorClass(c){
  const dexterity=mod(c.attrs?.des??10),equip=c.equip||{},armor=ARMOR_CLASS_STATS[equip.armadura];
  let ac;
  if(armor){
    const dexBonus=armor.dexMax===null?dexterity:armor.dexMax===0?0:Math.min(dexterity,armor.dexMax);
    ac=armor.base+dexBonus;
  }else if(c.cls==='Bárbaro')ac=10+dexterity+mod(c.attrs?.con??10);
  else if(c.cls==='Monge'&&!equip.escudo)ac=10+dexterity+mod(c.attrs?.sab??10);
  else if(c.cls==='Feiticeiro'&&c.subclass==='Linhagem Dracônica')ac=13+dexterity;
  else ac=10+dexterity;
  if(equip.escudo==='escudo')ac+=2;
  return ac;
}
function syncDerivedStats(c){
  const oldMax=Number(c.maxHp),oldAc=Number(c.ac),nextMax=calculateMaxHp(c),current=Number(c.hp)||0;
  if(Number.isFinite(oldMax)&&oldMax>0&&oldMax!==nextMax)c.hp=Math.max(0,Math.min(nextMax,current+Math.max(0,nextMax-oldMax)));
  else c.hp=Math.max(0,Math.min(nextMax,current));
  c.maxHp=nextMax;c.ac=calculateArmorClass(c);
  return oldMax!==nextMax||oldAc!==c.ac;
}
function formCharacterForDerivedStats(form){
  const attrs={};
  ATTRS.forEach(([key])=>{const value=Number(form.elements['base_'+key]?.value);attrs[key]=Number.isFinite(value)?value:10;});
  const race=form.elements.race.value,subrace=form.elements.subrace.value||'',cls=form.elements.cls.value,subclass=form.elements.subclass.value||'';
  const flex=race==='Meio-Elfo'?[form.elements.race_bonus_1?.value||'',form.elements.race_bonus_2?.value||'']:[];
  const legacy=form.elements.baseFromLegacy.value==='true';
  const finalAttrs=legacy?Object.assign({},attrs):applyAncestryBonuses(attrs,race,subrace,flex);
  form.querySelectorAll('.asi-card').forEach(card=>{
    const level=Number(card.querySelector('h3')?.textContent.replace(/\D/g,''))||0,mode=form.elements[`asi_mode_${level}`]?.value,a=form.elements[`asi_a_${level}`]?.value,b=form.elements[`asi_b_${level}`]?.value;
    if(mode==='two'&&a&&finalAttrs[a]!==undefined)finalAttrs[a]+=2;
    if(mode==='split'&&a&&b&&finalAttrs[a]!==undefined&&finalAttrs[b]!==undefined){finalAttrs[a]++;finalAttrs[b]++;}
  });
  const equip={};SLOTS.forEach(([slot])=>equip[slot]=form.elements[`e_${slot}`]?.value||'');
  return{cls,subclass,subrace,lvl:Number(form.elements.lvl.value)||1,attrs:finalAttrs,equip};
}
function updateDerivedStats(){
  const form=$('#char-form');if(!form)return;
  const c=formCharacterForDerivedStats(form),hp=$('#derived-max-hp'),ac=$('#derived-ac');
  if(hp)hp.value=calculateMaxHp(c);
  if(ac)ac.value=calculateArmorClass(c);
  updateSkillCount();
}
function classSkillFields(cls,selected=[]){
  const rule=CLASS_SKILL_RULES[cls]||{count:0,options:[]},picked=new Set(selected.filter(key=>rule.options.includes(key)).slice(0,rule.count));
  return rule.options.map(key=>{const label=SKILLS[key]?.[0]||key;return `<label class="chipsel"><input type="checkbox" name="sk" value="${key}" ${picked.has(key)?'checked':''} onchange="enforceSkillLimit(this)"><span>${label}</span></label>`;}).join('');
}
function updateSkillCount(){
  const form=$('#char-form'),output=$('#skill-count');if(!form||!output)return;
  const rule=CLASS_SKILL_RULES[form.elements.cls.value]||{count:0,options:[]},selected=form.querySelectorAll('input[name="sk"]:checked').length;
  output.textContent=`Selecionadas ${selected} de ${rule.count}`;
  output.classList.toggle('err',selected!==rule.count);
}
function updateClassSkillOptions(cls){
  const root=$('#class-skill-options'),form=$('#char-form');if(!root||!form)return;
  const selected=Array.from(form.querySelectorAll('input[name="sk"]:checked'),input=>input.value);
  root.innerHTML=classSkillFields(cls,selected);
  updateSkillCount();
}
function enforceSkillLimit(input){
  const form=$('#char-form'),rule=CLASS_SKILL_RULES[form?.elements.cls.value]||{count:0};
  if(input.checked&&form.querySelectorAll('input[name="sk"]:checked').length>rule.count){input.checked=false;toast(`Esta classe permite escolher ${rule.count} perícias.`);}
  updateSkillCount();
}
function hasSaveProficiency(cls,ability){return(SAVE_PROFICIENCIES[cls]||[]).includes(ability);}
function saveThrowBonus(c,ability){return mod(c.attrs[ability])+(hasSaveProficiency(c.cls,ability)?prof(c.lvl):0);}
function saveThrowRows(c){return ATTRS.map(([ability,label])=>{const trained=hasSaveProficiency(c.cls,ability),bonus=saveThrowBonus(c,ability);return `<button class="skill" onclick="rollCheck('Salvaguarda de ${label}',${bonus})"><span>${label}${trained?'<small class="muted sm"> · Proficiência</small>':''}</span><b>${fmt(bonus)}</b></button>`;}).join('')+`<div class="muted sm" style="margin-top:6px">Proficiência: ${fmt(prof(c.lvl))} · toque para rolar</div>`;}
function addExamples(){me().characters.push(...examples());save();render();toast('Personagens de exemplo adicionados');}
  async function rollCheck(label,b){const r=await rollDice({sides:20,bonus:b,label});if(!r.animated){vib();toast(`${label}: 🎲${r.result} ${fmt(b)} = ${r.total}${r.crit?' ✨':r.fumble?' 💀':''}`);}}
function setEquip(id,slot,v){const c=getChar(id);c.equip[slot]=v;syncDerivedStats(c);save();render();}
function hpModal(id){const c=getChar(id);if(!c)return;if(syncDerivedStats(c))save();modal(mh(esc(c.name))+`<div class="bigtxt">HP ${c.hp}/${c.maxHp}</div>${hpBar(c)}<div class="g4">${[-5,-1,1,5].map(d=>`<button class="btn" onclick="hpAdd('${id}',${d})">${d>0?'+':''}${d}</button>`).join('')}</div><button class="btn big" style="font-size:16px" onclick="hpAdd('${id}',999)">Descanso longo</button>`);}
function hpAdd(id,d){const c=getChar(id);syncDerivedStats(c);c.hp=Math.max(0,Math.min(c.maxHp,c.hp+d));save();hpModal(id);render();}
function delChar(id){if(!confirm('Excluir este personagem?'))return;const u=me();u.characters=u.characters.filter(c=>c.id!==id);save();back();toast('Personagem excluído');}
function saveChar(e,id){e.preventDefault();const f=new FormData(e.target);
  const n=(k,min,max,d)=>{let v=parseInt(f.get(k),10);if(isNaN(v))v=d;return Math.max(min,Math.min(max,v));};
  const lvl=n('lvl',1,20,1),cls=String(f.get('cls')||''),baseFromLegacy=f.get('baseFromLegacy')==='true',asiStartLevel=n('asiStartLevel',1,20,1),baseAttrs={};
  ATTRS.forEach(([k])=>baseAttrs[k]=n('base_'+k,1,20,10));
  const race=f.get('race'),subrace=f.get('subrace')||'';
  const raceBonusChoices=race==='Meio-Elfo'?[f.get('race_bonus_1')||'',f.get('race_bonus_2')||'']:[];
  if(!race)return toast('Selecione uma raça');
  const skillRule=CLASS_SKILL_RULES[cls],selectedSkills=f.getAll('sk');
  if(!skillRule)return toast('Selecione uma classe válida');
  if(selectedSkills.some(key=>!skillRule.options.includes(key)))return toast('Há uma perícia que não está disponível para esta classe');
  if(selectedSkills.length!==skillRule.count)return toast(`Escolha exatamente ${skillRule.count} perícias para ${cls}`);
  const availableSubraces=SUBRACES_BY_RACE[race]||[];
  if(availableSubraces.length&&!subrace)return toast('Selecione uma sub-raça');
  if(subrace&&!availableSubraces.includes(subrace))return toast('A sub-raça não corresponde à raça selecionada');
  if(race==='Meio-Elfo'&&(!raceBonusChoices[0]||!raceBonusChoices[1]||raceBonusChoices[0]===raceBonusChoices[1]||raceBonusChoices.includes('car')))return toast('Escolha dois atributos diferentes de Carisma para o bônus do Meio-Elfo');
  if(!baseFromLegacy&&!isStandardArray(baseAttrs))return toast('Use cada valor da matriz padrão uma única vez');
  const eligible=(ASI_LEVELS[f.get('cls')]||[]).filter(level=>level>asiStartLevel&&level<=lvl),choices=[];
  for(const level of eligible){
    const mode=f.get(`asi_mode_${level}`),a=f.get(`asi_a_${level}`),b=f.get(`asi_b_${level}`);
    if(!mode||!a||(mode==='split'&&!b))return toast(`Escolha a melhoria de atributo do nível ${level}`);
    if(mode==='split'&&a===b)return toast('As duas melhorias precisam usar atributos diferentes');
    choices.push({level,mode,a,b:mode==='split'?b:''});
  }
  const ancestryBase=baseFromLegacy?Object.assign({},baseAttrs):applyAncestryBonuses(baseAttrs,race,subrace,raceBonusChoices);
  const applied=applyASI(ancestryBase,choices);
  if(applied.error)return toast(applied.error);
  const attrs=applied.attrs;
  const equip={};SLOTS.forEach(([s])=>equip[s]=f.get('e_'+s)||'');
  const subclass=String(f.get('subclass')||''),derived={cls,subclass,subrace,lvl,attrs,equip};
  const d={name:String(f.get('name')).trim(),race,subrace,raceBonusChoices,cls,subclass,lvl,maxHp:calculateMaxHp(derived),ac:calculateArmorClass(derived),speed:n('speed',0,200,30),tokenId:Number(f.get('tokenId')||0),attrs,baseAttrs,baseFromLegacy,asiStartLevel,asiChoices:choices,skills:selectedSkills,equip};
  if(!d.race||!d.subclass)return toast('Selecione a raça e a subclasse do personagem');
  if(!d.name)return toast('Dê um nome ao personagem');
  const u=me();if(id){const c=getChar(id),oldMax=Number(c.maxHp)||d.maxHp,current=Number(c.hp)||0;Object.assign(c,d);c.hp=Math.max(0,Math.min(c.maxHp,current+Math.max(0,c.maxHp-oldMax)));}else u.characters.push(Object.assign({id:uid(),hp:d.maxHp},d));
  save();back();toast(id?'Ficha atualizada':'Personagem criado!');}

function standardBase(attrs){const fallback={for:15,des:14,con:13,int:12,sab:10,car:8};return attrs&&isStandardArray(attrs)?Object.assign({},attrs):fallback;}
function isStandardArray(attrs){const values=ATTRS.map(([k])=>Number(attrs?.[k]));return values.every(v=>STANDARD_ARRAY.includes(v))&&new Set(values).size===STANDARD_ARRAY.length;}
function attributeFields(attrs,legacy){return ATTRS.map(([k,label])=>`<label class="attribute-field"><span>${label}</span>${legacy?`<input class="inp form-control" type="number" name="base_${k}" min="1" max="20" value="${attrs[k]}" oninput="updateDerivedStats()">`:`<select class="inp form-select array-score" name="base_${k}" onchange="updateStandardArray('${k}')">${STANDARD_ARRAY.map(v=>`<option value="${v}" ${Number(attrs[k])===v?'selected':''}>${v}</option>`).join('')}</select>`}</label>`).join('');}
function asiFields(cls,lvl,startLevel,choices=[]){const old=Object.fromEntries(choices.map(c=>[c.level,c]));const levels=(ASI_LEVELS[cls]||[]).filter(n=>n>startLevel&&n<=lvl);if(!levels.length)return '<p class="muted sm">Nenhuma melhoria de atributo desta classe foi alcançada neste nível.</p>';return `<div class="asi-grid">${levels.map(level=>{const c=old[level]||{},mode=c.mode||'two';return `<section class="asi-card"><h3>Nível ${level}</h3><label class="fl">Tipo de melhoria</label><select class="inp form-select" name="asi_mode_${level}" onchange="updateASISecond(${level})"><option value="two" ${mode==='two'?'selected':''}>+2 em um atributo</option><option value="split" ${mode==='split'?'selected':''}>+1 em dois atributos</option></select><label class="fl">${mode==='split'?'Primeiro atributo':'Atributo'}</label><select class="inp form-select" name="asi_a_${level}" required onchange="updateDerivedStats()">${abilityOptions(c.a)}</select><label class="fl asi-second" id="asi-second-${level}" ${mode==='split'?'':'hidden'}>Segundo atributo<select class="inp form-select" name="asi_b_${level}" ${mode==='split'?'required':'disabled'} onchange="updateDerivedStats()">${abilityOptions(c.b)}</select></label></section>`;}).join('')}</div>`;}
function abilityOptions(selected=''){return `<option value="">Selecione</option>${ATTRS.map(([k,label])=>`<option value="${k}" ${selected===k?'selected':''}>${label}</option>`).join('')}`;}
function updateASISecond(level){const f=$('#char-form'),mode=f?.elements[`asi_mode_${level}`]?.value,wrap=$(`#asi-second-${level}`),second=f?.elements[`asi_b_${level}`];if(wrap&&second){wrap.hidden=mode!=='split';second.disabled=mode!=='split';second.required=mode==='split';}updateDerivedStats();}
function updateASIForm(){const f=$('#char-form'),root=$('#asi-list');if(!f||!root)return;const previous=[];root.querySelectorAll('.asi-card').forEach(card=>{const level=Number(card.querySelector('h3').textContent.replace(/\D/g,''));previous.push({level,mode:card.querySelector(`[name="asi_mode_${level}"]`).value,a:card.querySelector(`[name="asi_a_${level}"]`).value,b:card.querySelector(`[name="asi_b_${level}"]`).value});});root.innerHTML=asiFields(f.elements.cls.value,Number(f.elements.lvl.value),Number(f.elements.asiStartLevel.value),previous);updateDerivedStats();}
function updateStandardArray(){const f=$('#char-form'),warning=$('#array-warning');if(!f||!warning)return;const values=Array.from(f.querySelectorAll('.array-score'),s=>Number(s.value));warning.hidden=values.length===STANDARD_ARRAY.length&&values.every(v=>STANDARD_ARRAY.includes(v))&&new Set(values).size===STANDARD_ARRAY.length;updateDerivedStats();}
function applyASI(base,choices){const attrs=Object.assign({},base);for(const choice of choices){const keys=choice.mode==='two'?[choice.a]:[choice.a,choice.b];for(const key of keys){if(!ATTRS.some(([k])=>k===key))return{error:'Selecione atributos válidos para a melhoria.'};attrs[key]=Number(attrs[key])+ (choice.mode==='two'?2:1);if(attrs[key]>20)return{error:`${ATTRS.find(([k])=>k===key)[1]} não pode passar de 20 com uma melhoria de nível.`};}}return{attrs};}
function subclassOptions(cls,selected=''){return `<option value="">Selecione uma subclasse</option>${(SUBCLASSES_BY_CLASS[cls]||[]).map(x=>`<option value="${esc(x)}" ${selected===x?'selected':''}>${esc(x)}</option>`).join('')}`;}
function updateSubclasses(cls){const s=$('#subclass-select');if(s)s.innerHTML=subclassOptions(cls);}

function subraceOptions(race,selected=''){
  const options=SUBRACES_BY_RACE[race]||[];
  if(!options.length)return '<option value="">Nenhuma sub-raça cadastrada</option>';
  return `<option value="">Selecione uma sub-raça</option>${options.map(name=>`<option value="${esc(name)}" ${selected===name?'selected':''}>${esc(name)}</option>`).join('')}`;
}
function updateRaceOptions(race){
  const select=$('#subrace-select'),options=SUBRACES_BY_RACE[race]||[];
  if(select){select.innerHTML=subraceOptions(race);select.disabled=!options.length;select.required=options.length>0;}
  const extra=$('#flex-race-bonuses');if(extra)extra.innerHTML=raceBonusChoicesForm(race,[]);
  updateRaceBonusSummary();
}
function raceBonusChoicesForm(race,selected=[]){
  const active=race==='Meio-Elfo',required=active?'required':'';
  return `<div ${active?'':'hidden'}><p class="muted sm">A descrição da raça no SQL permite +1 em dois outros atributos, escolhidos pelo jogador; Carisma já recebe o bônus fixo.</p><label class="fl">Primeiro atributo +1</label><select class="inp form-select" name="race_bonus_1" ${required} onchange="updateRaceBonusSummary()">${bonusAbilityOptions(selected[0])}</select><label class="fl">Segundo atributo +1</label><select class="inp form-select" name="race_bonus_2" ${required} onchange="updateRaceBonusSummary()">${bonusAbilityOptions(selected[1])}</select></div>`;
}
function bonusAbilityOptions(selected=''){
  return `<option value="">Selecione</option>${ATTRS.filter(([key])=>key!=='car').map(([key,label])=>`<option value="${key}" ${selected===key?'selected':''}>${label}</option>`).join('')}`;
}
function ancestryBonusValues(race,subrace,flex=[]){
  const bonuses={};
  for(const source of [RACE_ATTRIBUTE_BONUSES[race]||{},SUBRACE_ATTRIBUTE_BONUSES[subrace]||{}])for(const [key,value] of Object.entries(source))bonuses[key]=(bonuses[key]||0)+value;
  if(race==='Meio-Elfo')for(const key of flex)if(key&&key!=='car')bonuses[key]=(bonuses[key]||0)+1;
  return bonuses;
}
function applyAncestryBonuses(base,race,subrace,flex=[]){
  const attrs=Object.assign({},base);
  for(const [key,value] of Object.entries(ancestryBonusValues(race,subrace,flex)))attrs[key]=Number(attrs[key])+value;
  return attrs;
}
function raceBonusSummary(race,subrace,flex=[],legacy=false){
  if(legacy)return 'Ficha antiga: os atributos atuais serão preservados sem reaplicar bônus de raça.';
  if(!race)return 'Os bônus de atributo cadastrados no SQL serão mostrados aqui.';
  const bonuses=ancestryBonusValues(race,subrace,flex),labels=Object.fromEntries(ATTRS.map(([key,label])=>[key,label]));
  const items=Object.entries(bonuses).map(([key,value])=>`+${value} ${labels[key]}`);
  return items.length?`Bônus aplicados aos atributos base: ${items.join(' · ')}`:'Nenhum bônus de atributo cadastrado para esta seleção.';
}
function updateRaceBonusSummary(){
  const form=$('#char-form');if(!form)return;
  const race=form.elements.race.value,subrace=form.elements.subrace.value||'';
  const choices=race==='Meio-Elfo'?[form.elements.race_bonus_1?.value||'',form.elements.race_bonus_2?.value||'']:[];
  const summary=$('#race-bonus-summary');if(summary)summary.textContent=raceBonusSummary(race,subrace,choices,form.elements.baseFromLegacy.value==='true');
  const description=$('#subrace-description');if(description)description.textContent=SUBRACE_DESCRIPTIONS[subrace]||'';
  updateDerivedStats();
}
