/* Mesa em jogo: jogadores, campo de batalha, dados e chat */
SCREENS.game=function(r){const t=tbl(r.id);if(!t)return null;t.members=Array.isArray(t.members)?t.members:[];t.tokens=Array.isArray(t.tokens)?t.tokens:[];t.chat=Array.isArray(t.chat)?t.chat:[];if(!isMember(t))return null;const gm=isGM(t),u=me();
  const diceLocked=!!t.initiative?.active&&!gm&&!isPlayerTurn(t);
  const map={};t.tokens.forEach(k=>map[k.x+','+k.y]=k);
  const fl=flankingState(t);
  let cells='';for(let y=0;y<GH;y++)for(let x=0;x<GW;x++){const k=map[x+','+y],moveLocked=!!t.initiative?.active&&!gm&&!isPlayerTurn(t)&&!k;
    const flanked=!!(k&&fl.targets[k.id]),flanker=!!(k&&fl.flankerIds.has(k.id));
    const label=(k?(k.kind==='npc'?(k.name||k.label||'Inimigo'):battleMemberName(t,k.owner)):`Casa ${x+1},${y+1}`)+(flanked?' (flanqueado)':'')+(flanker?' (flanqueando)':'');
    cells+=`<button class="cell${flanked?' flanked':''}${flanker?' flanker':''}${moveLocked?' turn-locked':''}" ${moveLocked?'disabled title="Aguarde o mestre liberar seu turno"':''} onclick="cellTap('${t.id}',${x},${y})" aria-label="${esc(label)}">${k?battleTokenMarkup(t,k):''}</button>`;}
  return hdr(t.name,{right:`<button class="ibtn" onclick="infoModal('${t.id}')" aria-label="Informações">ⓘ</button>`})+`<div class="body game-body">
  <div class="card"><div class="row between" style="margin-bottom:8px"><span>${IC_PEOPLE.replace('<svg','<svg width="20" height="20" style="vertical-align:-4px"')} Jogadores</span><span>${t.members.length}/${t.max}</span></div>
  ${t.members.length?t.members.map((m,index)=>{const mine=m.user===S.session,c=mine?getChar(m.charId):null,nm=c?c.name:(m.charName||m.name),hp=c?c.hp:m.hp,mx=c?c.maxHp:m.maxHp,tag=gm||mine&&c?'button':'div',handler=`hpSessionModal('${t.id}','m:${index}')`;
    return `<${tag}${gm||mine&&c?` onclick="${handler}"`:''} class="card item dark ${gm||mine&&c?'member-hp-control':''}" style="display:block;text-align:center;margin-bottom:8px;padding:8px"><div>${esc(nm)}${mine?' <small class="muted">(você)</small>':''}${gm||mine&&c?' <small class="muted">· ajustar PV</small>':''}</div><div class="hprow" style="padding:2px 0"><span class="sm">HP</span>${hpBar({hp,maxHp:mx})}<b>${hp}</b></div></${tag}>`;}).join('')
    :`<p class="muted center sm" style="margin:6px 0">Aguardando jogadores…<br>Código de acesso: <b style="color:var(--yellow)">${t.code}</b></p>`}${gm?gmTools(t):''}</div>
  <p class="sm center" style="margin:10px 0 0;color:var(--orange)">Mestre: ${esc(t.gm)}${gm?' (você)':''}</p>
  <h3 class="h2" style="margin-top:12px">Campo de Batalha</h3>
  <div class="bf">${cells}</div>
  <p class="muted sm center" style="margin:6px 0 0">${gm?'Escolha uma casa vazia para adicionar um inimigo; toque num token para gerenciá-lo e ajustar os PV.':'Toque numa casa vazia para mover seu token ou no seu token para ajustar seus PV.'}</p>
  ${flankingPanel(t,fl)}
  ${initiativePanel(t)}
  <div class="row" style="margin-top:14px"><button class="btn grow" ${diceLocked?'disabled title="Aguarde sua vez"':''} onclick="rollPrompt('${t.id}',20)">Rolar d20</button><button class="btn grow" onclick="chatModal('${t.id}')">Chat</button></div>
  </div>`;};

function battleMemberName(t,owner){const member=t.members.find(m=>m.user===owner||m.id===owner);if(!member)return'Personagem';return member.user===S.session?(getChar(member.charId)?.name||member.charName||member.name):(member.charName||member.name||'Personagem');}
function battleTokenMarkup(t,token){
  if(token.kind==='npc'){
    const name=token.name||token.label||'Inimigo',kind=monsterTokenKind(token.monsterType,name);
    return `<span class="tok npc monster-token monster-token-${kind}" title="${esc(name)}" aria-label="${esc(name)}"><span class="monster-token-portrait">${battlePortrait(monsterTokenPortraitIndex(kind))}</span><small>${esc(battleTokenLabel(name))}</small></span>`;
  }
  const member=t.members.find(m=>m.user===token.owner||m.id===token.owner),character=member?.user===S.session?getChar(member.charId):null;
  const tokenId=character?.tokenId??token.tokenId??member?.tokenId??0,name=character?.name||member?.charName||token.name||token.label||'Personagem';
  return `<span class="battle-character-token ${token.owner===S.session?'own':''}" title="${esc(name)}">${battlePortrait(tokenId)}</span>`;
}
// Usa retratos CC BY 3.0 já incluídos em img/fantasy_portraits_25.png.
function monsterTokenKind(type,name=''){
  const value=`${type||''} ${name}`.normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase();
  if(/drag|dracon|wyvern/.test(value))return'dragon';
  if(/morto-vivo|undead|esquele|zumbi|lich|vampir|fantasma|wraith/.test(value))return'undead';
  if(/fera|beast|animal|lobo|urso|aranha|inset|besta|crocodil|serpente|tigre|leao/.test(value))return'beast';
  if(/demon|diabo|fiend|infernal|aberr/.test(value))return'fiend';
  if(/element|fogo|agua|ar |terra|gelo|relamp|lava/.test(value))return'elemental';
  if(/planta|plant|fung|treant|veget/.test(value))return'plant';
  if(/gosma|ooze|limo|pudim|slime/.test(value))return'ooze';
  if(/humanoide|humanoid|gobl|orc|gigante|an[aã]o|elf|humano|kobold|troll/.test(value))return'humanoid';
  return'monster';
}
function monsterTokenPortraitIndex(kind){return({dragon:4,undead:7,beast:18,fiend:9,elemental:15,plant:16,ooze:17,humanoid:21,monster:20})[kind]??20;}
function battlePortrait(index){const n=Math.max(0,Math.min(TOKEN_COUNT-1,Number(index)||0)),x=n%6,y=Math.floor(n/6);return `<span class="battle-portrait" style="background-position:${x*20}% ${y*25}%"></span>`;}
function battleTokenLabel(name){const words=String(name).trim().split(/\s+/).filter(Boolean);return (words.length>1?words.slice(0,2).map(word=>word[0]).join(''):String(name).slice(0,2)).toUpperCase();}
function gmTools(t){return `<section class="gm-tools"><h3>Gerenciar mesa</h3><div class="gm-tool-actions"><button class="btn sm" onclick="tableManagerModal('${t.id}')">Jogadores</button><button class="btn sm" onclick="addEnemyModal('${t.id}')">＋ Adicionar inimigo</button><button class="btn sm" onclick="initiativeModal('${t.id}')">Iniciativa</button><button class="btn sm" onclick="toggleFlanking('${t.id}')" aria-pressed="${rulesOf(t).flanking}">Flanqueamento: ${rulesOf(t).flanking?'ligado':'desligado'}</button></div></section>`;}
function tableManagerModal(id){
  const t=tbl(id);if(!t||!isGM(t))return;
  const rows=t.members.length?t.members.map((m,index)=>`<div class="table-manager-row"><div><b>${esc(m.charName||m.name||'Jogador')}</b><small class="muted">PV ${Number(m.hp)||0}/${Number(m.maxHp)||0}</small></div><button class="btn sm" onclick="gmMemberHpModal('${id}',${index})">Ajustar PV</button><button class="btn sm red" onclick="removeTableMember('${id}',${index})">Remover</button></div>`).join(''):'<p class="muted sm">Nenhum jogador entrou na mesa ainda.</p>';
  modal(mh('Gerenciar mesa')+`<p class="muted sm">Código de convite: <b style="color:var(--yellow);letter-spacing:2px">${esc(t.code)}</b> <button class="btn sm" onclick="copyCode('${t.code}')">Copiar</button></p><h3 class="h2">Jogadores · ${t.members.length}/${t.max}</h3><div class="table-manager-list">${rows}</div><p class="muted sm" style="margin-top:12px">Clique em “Adicionar inimigo” ou numa casa vazia do mapa para colocar um combatente.</p>`);
}
function gmMemberHpModal(tid,index){hpSessionModal(tid,'m:'+index);}
function removeTableMember(tid,index){
  const t=tbl(tid);if(!t||!isGM(t))return;const member=t.members[index];if(!member)return;
  if(!confirm(`Remover ${member.charName||member.name||'este jogador'} da mesa?`))return;
  const owners=new Set([member.id,member.user].filter(Boolean));t.members.splice(index,1);t.tokens=t.tokens.filter(token=>!owners.has(token.owner));
  if(t.initiative?.scores)delete t.initiative.scores['player:'+member.id];save();tableManagerModal(tid);render();
}

// O banco poderá preencher window.FAST_FANTASY_MONSTERS pela futura rota do catálogo.
// Cada entrada precisa fornecer nome, pontos de vida e CA; aceitamos os nomes de coluna comuns do projeto.
let pendingEnemyPlacement=null,pendingEnemyMove=null;
let monsterCatalogState=Array.isArray(window.FAST_FANTASY_MONSTERS)?'ready':'idle';
function monsterCatalog(){return Array.isArray(window.FAST_FANTASY_MONSTERS)?window.FAST_FANTASY_MONSTERS:[];}
function monsterName(monster){return String(monster?.name??monster?.nome??monster?.nome_monstro??'').trim();}
function monsterHp(monster){const value=Number(monster?.hp??monster?.pv??monster?.pontos_vida??monster?.hp_maximo??monster?.HP_maximo);return Number.isInteger(value)&&value>0?value:null;}
function monsterAc(monster){const value=Number(monster?.ac??monster?.ca??monster?.classe_armadura??monster?.classe_de_armadura);return Number.isInteger(value)&&value>=0?value:null;}
function monsterSpeed(monster){return movementFeet(monster?.speed??monster?.deslocamento??monster?.velocidade,30);}
function movementFeet(value,fallback=30){
  if(typeof value==='number'&&Number.isFinite(value))return Math.max(0,value);
  const text=String(value??'').trim().toLowerCase(),match=text.match(/\d+(?:[.,]\d+)?/);
  if(!match)return fallback;
  const amount=Number(match[0].replace(',','.'));
  return Math.max(0,/\bm\b|metro/.test(text)&&!/ft|p[eé]s/.test(text)?Math.round(amount*3.28084):amount);
}
function findOpenBattleCell(t){for(let y=0;y<GH;y++)for(let x=0;x<GW;x++)if(!t.tokens.some(k=>k.x===x&&k.y===y))return{x,y};return null;}
function monsterCatalogRows(query=''){
  const search=String(query||'').trim().toLocaleLowerCase('pt-BR');
  return monsterCatalog().map((monster,index)=>({monster,index})).filter(({monster})=>monsterName(monster).toLocaleLowerCase('pt-BR').includes(search)).map(({monster,index})=>{
    const name=monsterName(monster),hp=monsterHp(monster),ac=monsterAc(monster);
    const ready=!!name&&hp!==null&&ac!==null;
    return `<button class="monster-choice" type="button" ${ready?'':'disabled'} onclick="addCatalogEnemy(${index})"><span><b>${esc(name||'Monstro sem nome')}</b><small class="muted">${hp===null?'PV não informado':`${hp} PV`} · ${ac===null?'CA não informada':`CA ${ac}`}</small></span><span aria-hidden="true">${ready?'＋':'—'}</span></button>`;
  }).join('')||'<p class="muted sm">Nenhum monstro encontrado no catálogo.</p>';
}
function monsterCatalogContent(){
  const catalog=monsterCatalog();
  if(monsterCatalogState==='loading')return '<p class="muted sm">Consultando o catálogo de monstros…</p>';
  if(monsterCatalogState==='unavailable')return '<p class="muted sm">O catálogo do banco ainda não está disponível. Você pode cadastrar manualmente os dados consultados no livro.</p>';
  if(!catalog.length)return '<p class="muted sm">Nenhum monstro cadastrado no catálogo ainda. Você pode cadastrar manualmente os dados consultados no livro.</p>';
  return `<label class="fl" for="monster-search">Buscar por nome</label><input class="inp form-control" id="monster-search" type="search" oninput="filterMonsterCatalog()" placeholder="Nome do monstro"><div id="monster-results" class="monster-results">${monsterCatalogRows()}</div>`;
}
async function loadMonsterCatalog(tid){
  if(Array.isArray(window.FAST_FANTASY_MONSTERS)){monsterCatalogState='ready';return;}
  if(monsterCatalogState!=='idle')return;
  monsterCatalogState='loading';const content=$('#monster-catalog-content');if(content)content.innerHTML=monsterCatalogContent();
  try{
    const payload=await apiRequest('monsters'),rows=Array.isArray(payload)?payload:payload.monsters;
    if(!Array.isArray(rows))throw new Error('Resposta inválida do catálogo de monstros');
    window.FAST_FANTASY_MONSTERS=rows;monsterCatalogState='ready';
  }catch(_){monsterCatalogState='unavailable';}
  const root=$('#monster-catalog-content');if(root&&pendingEnemyPlacement?.tableId===tid)root.innerHTML=monsterCatalogContent();
}
function filterMonsterCatalog(){const root=$('#monster-results'),input=$('#monster-search');if(root&&input)root.innerHTML=monsterCatalogRows(input.value);}
function addEnemyModal(tid,x=null,y=null){
  const t=tbl(tid);if(!t||!isGM(t))return;
  pendingEnemyPlacement={tableId:tid,x:Number.isInteger(x)?x:null,y:Number.isInteger(y)?y:null};
  const catalog=monsterCatalog(),position=Number.isInteger(x)&&Number.isInteger(y)?`Casa ${x+1}, ${y+1}`:'primeira casa livre';
  modal(mh('Adicionar inimigo')+`<p class="muted sm">Posição no mapa: ${position}.</p><section class="monster-catalog"><h3 class="h2">Catálogo de monstros</h3><div id="monster-catalog-content">${monsterCatalogContent()}</div></section><details class="manual-enemy" ${catalog.length?'':'open'}><summary>Adicionar dados manualmente</summary><form onsubmit="saveManualEnemy(event)"><label class="fl" for="manual-enemy-name">Nome do inimigo</label><input class="inp form-control" id="manual-enemy-name" name="name" maxlength="100" required><div class="g2"><div><label class="fl" for="manual-enemy-hp">Pontos de vida</label><input class="inp form-control" id="manual-enemy-hp" name="hp" type="number" min="1" step="1" required></div><div><label class="fl" for="manual-enemy-ac">Classe de Armadura</label><input class="inp form-control" id="manual-enemy-ac" name="ac" type="number" min="0" step="1" required></div></div><label class="fl" for="manual-enemy-speed">Deslocamento (ft)</label><input class="inp form-control" id="manual-enemy-speed" name="speed" type="number" min="0" max="1000" step="1" value="30" required><button class="btn big" style="margin-top:10px">Adicionar ao mapa</button></form></details>`);
  if(monsterCatalogState==='idle')loadMonsterCatalog(tid);
}
function addCatalogEnemy(index){
  const placement=pendingEnemyPlacement,monster=monsterCatalog()[index];if(!placement||!monster)return toast('Catálogo de monstros indisponível');
  const name=monsterName(monster),hp=monsterHp(monster),ac=monsterAc(monster);
  if(!name||hp===null||ac===null)return toast('O cadastro precisa conter nome, PV e CA');
  const id=monster.id_monstro??monster.id??null;
  placeEnemy(placement.tableId,{id:uid(),kind:'npc',owner:null,label:battleTokenLabel(name),name,monsterType:monster.tipo??monster.type??monster.tipo_monstro??'',monsterId:id,hp,maxHp:hp,ac,speed:monsterSpeed(monster),source:'catalog'},placement.x,placement.y);
}
function saveManualEnemy(event){
  event.preventDefault();const placement=pendingEnemyPlacement;if(!placement)return;
  const form=new FormData(event.target),name=String(form.get('name')||'').trim(),hp=Number(form.get('hp')),ac=Number(form.get('ac')),speed=Number(form.get('speed'));
  if(!name||!Number.isInteger(hp)||hp<1||!Number.isInteger(ac)||ac<0||!Number.isInteger(speed)||speed<0||speed>1000)return toast('Confira nome, PV, CA e deslocamento');
  placeEnemy(placement.tableId,{id:uid(),kind:'npc',owner:null,label:battleTokenLabel(name),name,monsterType:'',hp,maxHp:hp,ac,speed,source:'manual'},placement.x,placement.y);
}
function placeEnemy(tid,enemy,x=null,y=null){
  const t=tbl(tid);if(!t||!isGM(t))return;
  const position=Number.isInteger(x)&&Number.isInteger(y)?{x,y}:findOpenBattleCell(t);
  if(!position)return toast('O mapa está cheio');
  if(t.tokens.some(k=>k.x===position.x&&k.y===position.y))return toast('Essa casa já está ocupada');
  t.tokens.push(Object.assign(enemy,position));pendingEnemyPlacement=null;save();closeModal();render();toast(`${enemy.name} adicionado ao mapa`);
}
function manageEnemyModal(tid,enemyId){
  const t=tbl(tid),enemy=t?.tokens.find(token=>token.id===enemyId);if(!t||!enemy||!isGM(t))return;
  const hp=Number(enemy.hp)||0,max=Number(enemy.maxHp)||hp,ac=Number(enemy.ac);
  modal(mh('Gerenciar inimigo')+`<p class="muted">${esc(enemy.name||enemy.label||'Inimigo')}${enemy.monsterId!==null&&enemy.monsterId!==undefined?' · catálogo do banco':''}</p>${hpControlMarkup(t,hpTarget(t,'n:'+enemyId))}<form onsubmit="saveEnemyDetails(event,'${tid}','${enemyId}')"><label class="fl">Nome</label><input class="inp form-control" name="name" maxlength="100" value="${esc(enemy.name||enemy.label||'Inimigo')}" required><div class="g2"><div><label class="fl">PV máximos</label><input class="inp form-control" name="maxHp" type="number" min="1" step="1" value="${max||''}" required></div><div><label class="fl">CA</label><input class="inp form-control" name="ac" type="number" min="0" step="1" value="${Number.isInteger(ac)?ac:''}" required></div></div><label class="fl">Deslocamento (ft)</label><input class="inp form-control" name="speed" type="number" min="0" max="1000" step="1" value="${movementFeet(enemy.speed??enemy.deslocamento,30)}" required><button class="btn big" style="margin-top:10px">Salvar dados</button></form>${noFlankControl(t,enemy.id)}<div class="row gm-enemy-actions"><button class="btn grow" onclick="gmAttackSetup('${tid}','${enemyId}')">⚔ Atacar personagem</button><button class="btn grow" onclick="beginEnemyMove('${tid}','${enemyId}')">Mover no mapa</button><button class="btn red grow" onclick="removeEnemy('${tid}','${enemyId}')">Remover</button></div>`);
}
function beginEnemyMove(tid,enemyId){const t=tbl(tid),enemy=t?.tokens.find(token=>token.id===enemyId);if(!t||!enemy||!isGM(t))return;if(t.initiative?.active&&(t.initiative.currentKey!=='npc:'+enemyId||!t.initiative.turnReleased))return toast('O mestre precisa liberar a vez deste inimigo');pendingEnemyMove={tableId:tid,enemyId};closeModal();toast('Escolha uma casa vazia para mover o inimigo');}
function saveEnemyDetails(event,tid,enemyId){
  event.preventDefault();const t=tbl(tid),enemy=t?.tokens.find(token=>token.id===enemyId);if(!t||!enemy||!isGM(t))return;
  const form=new FormData(event.target),name=String(form.get('name')||'').trim(),max=Number(form.get('maxHp')),ac=Number(form.get('ac')),speed=Number(form.get('speed'));
  if(!name||!Number.isInteger(max)||max<1||!Number.isInteger(ac)||ac<0||!Number.isInteger(speed)||speed<0||speed>1000)return toast('Confira nome, PV, CA e deslocamento');
  enemy.name=name;enemy.label=battleTokenLabel(name);enemy.maxHp=max;enemy.hp=Math.min(Number(enemy.hp)||max,max);enemy.ac=ac;enemy.speed=speed;save();closeModal();render();toast('Dados do inimigo atualizados');
}
function removeEnemy(tid,enemyId){
  const t=tbl(tid);if(!t||!isGM(t))return;const enemy=t.tokens.find(token=>token.id===enemyId);if(!enemy)return;
  if(!confirm(`Remover ${enemy.name||'este inimigo'} do mapa?`))return;
  t.tokens=t.tokens.filter(token=>token.id!==enemyId);if(t.initiative?.scores)delete t.initiative.scores['npc:'+enemyId];if(pendingEnemyMove?.enemyId===enemyId)pendingEnemyMove=null;save();closeModal();render();toast('Inimigo removido');
}

function ensureInitiative(t){
  if(!t.initiative||typeof t.initiative!=='object')t.initiative={active:false,round:1,currentKey:null,scores:{}};
  if(!t.initiative.scores||typeof t.initiative.scores!=='object')t.initiative.scores={};
  if(!Number.isInteger(t.initiative.round)||t.initiative.round<1)t.initiative.round=1;
  if(!Number.isFinite(t.initiative.movementUsed)||t.initiative.movementUsed<0)t.initiative.movementUsed=0;
  return t.initiative;
}
function initiativeEntries(t){
  const state=ensureInitiative(t);
  const players=t.members.map(m=>{
    const c=m.user===S.session?getChar(m.charId):null;
    return{key:'player:'+m.id,label:c?c.name:(m.charName||m.name),kind:'player',score:state.scores['player:'+m.id]};
  });
  const npcs=t.tokens.filter(k=>k.kind==='npc').map(k=>({key:'npc:'+k.id,label:k.name||k.label||'Inimigo',kind:'npc',score:state.scores['npc:'+k.id]}));
  return players.concat(npcs);
}
function sortedInitiative(t){
  const state=ensureInitiative(t);
  return initiativeEntries(t).filter(x=>Number.isInteger(x.score)).sort((a,b)=>b.score-a.score);
}
function currentInitiativeEntry(t){return sortedInitiative(t).find(entry=>entry.key===ensureInitiative(t).currentKey)||null;}
function playerInitiativeKey(t,user=S.session){const member=t.members.find(m=>m.user===user);return member?'player:'+member.id:null;}
function isPlayerTurn(t,user=S.session){return !!t?.initiative?.active&&t.initiative.turnReleased===true&&!!playerInitiativeKey(t,user)&&t.initiative.currentKey===playerInitiativeKey(t,user);}
function canRollInTable(t){return isGM(t)||!t.initiative?.active||isPlayerTurn(t);}
function canRollCharacterCheck(){
  const combats=S.tables.filter(t=>isMember(t)&&t.initiative?.active);
  return combats.length===0||combats.every(t=>isGM(t)||isPlayerTurn(t));
}
function tokenForInitiative(t,key){
  if(!key)return null;
  if(key.startsWith('npc:'))return t.tokens.find(token=>token.kind==='npc'&&token.id===key.slice(4))||null;
  if(key.startsWith('player:')){const member=t.members.find(item=>item.id===key.slice(7));return member?t.tokens.find(token=>token.kind!=='npc'&&(token.owner===member.user||token.owner===member.id))||null:null;}
  return null;
}
function tokenSpeedFeet(t,token){
  if(!token)return 30;
  if(token.kind==='npc')return movementFeet(token.speed??token.deslocamento,30);
  const member=t.members.find(item=>item.user===token.owner||item.id===token.owner),character=member?.user===S.session?getChar(member.charId):null;
  return movementFeet(character?.speed??member?.speed,30);
}
function movementCostFeet(fromX,fromY,toX,toY){return Math.max(Math.abs(toX-fromX),Math.abs(toY-fromY))*5;}
function currentMovementStatus(t){
  const state=ensureInitiative(t),entry=currentInitiativeEntry(t),token=tokenForInitiative(t,state.currentKey);
  if(!entry)return '';
  const limit=tokenSpeedFeet(t,token),used=Math.min(limit,state.movementUsed);
  return `<p class="initiative-movement sm">${esc(entry.label)} · deslocamento ${used}/${limit} ft (5 ft por casa)${used>=limit?' · limite atingido':''}</p>`;
}
function initiativePanel(t){
  const state=ensureInitiative(t),entries=initiativeEntries(t),order=sortedInitiative(t),gm=isGM(t);
  if(!state.active)return `<section class="card initiative-panel"><div class="row between"><div><h3>Iniciativa</h3><p class="muted sm">${gm?'Organize a ordem dos participantes durante o combate.':'Aguardando o mestre configurar a iniciativa.'}</p></div>${gm?`<button class="btn sm" onclick="initiativeModal('${t.id}')">Configurar</button>`:''}</div></section>`;
  const current=order.findIndex(x=>x.key===state.currentKey);
  if(current<0&&order.length)state.currentKey=order[0].key;
  const waiting=!gm&&(!state.turnReleased||!isPlayerTurn(t));
  const active=order.find(entry=>entry.key===state.currentKey),turnStatus=gm?(state.turnReleased?`Turno liberado: ${esc(active?.label||'combatente atual')}.`:`Turno bloqueado: clique em Liberar turno para autorizar ações.`):(isPlayerTurn(t)?'SUA VEZ está liberada. Você pode rolar e mover.':'TURNO BLOQUEADO. Aguarde o mestre liberar sua vez.');
  return `<section class="card initiative-panel"><div class="row between initiative-heading"><div><h3>Iniciativa</h3><p class="muted sm">Rodada ${state.round} · ${gm?'controle salvo neste dispositivo':'controlada pelo mestre'}</p></div>${gm?`<button class="btn sm" onclick="initiativeModal('${t.id}')">Editar ordem</button>`:''}</div>${order.length?`<p class="turn-status ${waiting?'locked':'released'}">${turnStatus}</p><ol class="initiative-list">${order.map((entry,index)=>`<li class="initiative-entry ${entry.key===state.currentKey?'active':''}"><span class="initiative-position">${index+1}</span><b>${esc(entry.label)}</b><span class="initiative-score">${fmt(entry.score)}</span></li>`).join('')}</ol>${currentMovementStatus(t)}${gm?`<div class="row initiative-actions"><button class="btn grow" onclick="advanceInitiative('${t.id}')">${state.turnReleased?'Passar turno':'Liberar turno'}</button><button class="btn sm" onclick="stopInitiative('${t.id}')">Finalizar combate</button></div>`:''}`:`<p class="muted sm">Não há combatentes nesta ordem. Configure participantes ou coloque inimigos no mapa.</p>`}</section>`;
}
function initiativeModal(id){
  const t=tbl(id);if(!t||!isGM(t))return;
  const entries=initiativeEntries(t);
  modal(mh('Ordem de iniciativa')+`<p class="muted sm">Informe o resultado final da iniciativa de cada combatente (d20 + modificador de Destreza). A ordem fica salva neste dispositivo.</p>${entries.length?`<form onsubmit="saveInitiative(event,'${id}')">${entries.map((entry,index)=>`<label class="fl" for="initiative-${index}">${esc(entry.label)}${entry.kind==='npc'?' · inimigo':''}</label><input class="inp form-control" id="initiative-${index}" name="score${index}" type="number" step="1" value="${Number.isInteger(entry.score)?entry.score:''}" required>`).join('')}<button class="btn big" style="margin-top:16px">Salvar e iniciar combate</button></form>`:'<p class="muted">Nenhum personagem está na mesa e não há inimigos no mapa.</p>'}`);
}
function saveInitiative(event,id){
  event.preventDefault();const t=tbl(id);if(!t||!isGM(t))return;
  const entries=initiativeEntries(t),form=new FormData(event.target),scores={};
  if(!entries.length)return toast('Adicione combatentes antes de iniciar');
  entries.forEach((entry,index)=>{const score=Number(form.get('score'+index));if(Number.isInteger(score))scores[entry.key]=score;});
  const order=entries.filter(entry=>Number.isInteger(scores[entry.key])).sort((a,b)=>scores[b.key]-scores[a.key]);
  if(!order.length)return toast('Informe ao menos uma iniciativa válida');
  t.initiative={active:true,round:1,currentKey:order[0].key,scores,movementUsed:0,turnReleased:false};save();closeModal();render();toast('Ordem salva. Libere a primeira vez quando estiver pronto.');
}
function advanceInitiative(id){
  const t=tbl(id);if(!t||!isGM(t))return;const state=ensureInitiative(t),order=sortedInitiative(t);if(!order.length)return toast('Configure a ordem primeiro');
  if(!state.turnReleased){state.turnReleased=true;state.movementUsed=0;const actor=initiativeEntries(t).find(entry=>entry.key===state.currentKey);addChat(t,{sys:true,text:`O mestre liberou a vez de ${actor?.label||'o primeiro combatente'}.`});render();return;}
  const index=order.findIndex(entry=>entry.key===state.currentKey),next=index<0?0:(index+1)%order.length;
  if(index===order.length-1||index<0)state.round++;
  state.currentKey=order[next].key;state.movementUsed=0;state.turnReleased=true;const actor=initiativeEntries(t).find(entry=>entry.key===state.currentKey);addChat(t,{sys:true,text:`O mestre passou a vez para ${actor?.label||'o próximo combatente'}${state.round>1&&next===0?` · rodada ${state.round}`:''}.`});render();
}
function stopInitiative(id){const t=tbl(id);if(!t||!isGM(t))return;t.initiative={active:false,round:1,currentKey:null,scores:{},movementUsed:0,turnReleased:false};save();render();toast('Combate finalizado');}

function infoModal(id){const t=tbl(id),gm=isGM(t);modal(mh(esc(t.name))+`<p class="muted" style="font-family:var(--serif);font-size:18px">${esc(t.desc||'Sem descrição.')}</p><p style="color:var(--orange)">Mestre: ${esc(t.gm)}</p><p>Código de acesso: <b style="color:var(--yellow);letter-spacing:2px">${t.code}</b> <button class="btn sm" onclick="copyCode('${t.code}')">Copiar</button></p><button class="btn red big" style="font-size:16px;margin-top:10px" onclick="${gm?`closeTable('${id}')`:`leaveTable('${id}')`}">${gm?'Encerrar mesa':'Sair da mesa'}</button>`);}
async function leaveTable(id){if(S.settings.confirmLeave&&!confirm('Sair desta mesa?'))return;const t=tbl(id);if(!t)return;
  try{await apiRequest('campaign/leave',{method:'POST',body:{id}});}catch(error){return toast(error.status===0?'Não foi possível sair da mesa. Confira sua conexão.':error.message);}
  S.tables=S.tables.filter(table=>table.id!==id);save();closeModal();back();toast('Você saiu da mesa');}
async function closeTable(id){if(!confirm('Encerrar e apagar esta mesa?'))return;
  try{await apiRequest('campaign/delete',{method:'POST',body:{id}});}catch(error){return toast(error.status===0?'Não foi possível encerrar a mesa. Confira sua conexão.':error.message);}
  S.tables=S.tables.filter(t=>t.id!==id);save();closeModal();back();toast('Mesa encerrada');}
function cellTap(tid,x,y){const t=tbl(tid);if(!t)return;const k=t.tokens.find(q=>q.x===x&&q.y===y);
  if(isGM(t)){
    if(pendingEnemyMove?.tableId===tid){
      if(k)return toast('Escolha uma casa vazia');
      const moving=t.tokens.find(token=>token.id===pendingEnemyMove.enemyId);if(!moving){pendingEnemyMove=null;return toast('O inimigo não está mais na mesa');}
      const state=ensureInitiative(t),speed=tokenSpeedFeet(t,moving),cost=movementCostFeet(moving.x,moving.y,x,y);
      if(t.initiative?.active&&(t.initiative.currentKey!=='npc:'+moving.id||!t.initiative.turnReleased)){pendingEnemyMove=null;return toast('O mestre precisa liberar a vez deste inimigo');}
      if(t.initiative?.active&&state.movementUsed+cost>speed)return toast(`Deslocamento excedido: ${Math.max(0,speed-state.movementUsed)} ft restantes`);
      moving.x=x;moving.y=y;if(t.initiative?.active)state.movementUsed+=cost;pendingEnemyMove=null;save();render();return toast(`Inimigo movido · ${cost} ft`);
    }
    if(k?.kind==='npc')return manageEnemyModal(tid,k.id);
    if(!k)return addEnemyModal(tid,x,y);
    const index=t.members.findIndex(m=>m.user===k.owner||m.id===k.owner);
    if(index>=0)return gmMemberHpModal(tid,index);
    return toast('Esse token não está vinculado a um jogador');
  }
  if(k){
    if(k.owner===S.session){const index=t.members.findIndex(m=>m.user===S.session);return index>=0?hpSessionModal(tid,'m:'+index):toast('Este é o seu token');}
    if(k.kind==='npc'||(t.members.some(m=>m.user===k.owner||m.id===k.owner)&&k.owner!==S.session))return attackModal(t,k);
    if(flankingState(t).targets[k.id])return toast(`${tokenName(t,k)} está flanqueado: vantagem nos ataques corpo a corpo de quem o flanqueia`);
    return toast('Casa ocupada');
  }
  const own=t.tokens.find(q=>q.owner===S.session),member=t.members.find(m=>m.user===S.session||m.id===S.session),character=member&&getChar(member.charId);
  if(t.initiative?.active&&!isPlayerTurn(t))return toast('Aguarde o mestre liberar sua vez para mover');
  if(own){
    if(t.initiative?.active){const state=ensureInitiative(t),speed=tokenSpeedFeet(t,own),cost=movementCostFeet(own.x,own.y,x,y);
      if(state.movementUsed+cost>speed)return toast(`Deslocamento excedido: ${Math.max(0,speed-state.movementUsed)} ft restantes`);
      state.movementUsed+=cost;
    }
    own.x=x;own.y=y;
  }else t.tokens.push({id:uid(),kind:'p',owner:S.session,label:character?battleTokenLabel(character.name):'?',tokenId:character?.tokenId??member?.tokenId??0});
  save();render();}

function gmAttackSetup(tid,enemyId){
  const t=tbl(tid),enemy=t?.tokens.find(token=>token.id===enemyId&&token.kind==='npc');if(!t||!enemy||!isGM(t))return;
  if(t.initiative?.active&&(t.initiative.currentKey!=='npc:'+enemyId||!t.initiative.turnReleased))return toast('O mestre precisa liberar a vez deste monstro antes de atacar');
  const targets=t.tokens.filter(token=>token.kind!=='npc'&&t.members.some(member=>member.user===token.owner||member.id===token.owner));
  if(!targets.length)return toast('Nenhum personagem jogador está no mapa');
  const options=targets.map(token=>({token,distance:attackDistanceFeet(enemy,token)})).filter(row=>tokenUp(t,row.token));
  enemy.attacks=Array.isArray(enemy.attacks)?enemy.attacks:[];const saved=enemy.attacks,initial=saved[0]||{name:'Ataque básico',bonus:4,damage:'1d6+2',range:5};
  modal(mh(`Ataque de ${esc(tokenName(t,enemy))}`)+`<p class="muted sm">Escolha um ataque salvo ou configure um novo. O mapa mede 5 ft por casa; personagens fora do alcance ficam desabilitados.</p><form oninput="filterMonsterAttackTargets(this)" onsubmit="resolveMonsterAttack(event,'${tid}','${enemyId}')"><label class="fl">1 · Ataque</label>${saved.length?`<select class="inp form-select" name="preset" onchange="selectMonsterAttack(this,'${tid}','${enemyId}')"><option value="">Novo ataque…</option>${saved.map((attack,index)=>`<option value="${index}" ${index===0?'selected':''}>${esc(attack.name)} · ${esc(attack.damage)} · ${attack.range} ft</option>`).join('')}</select>`:''}<div class="g2"><div><label class="fl">Nome</label><input class="inp form-control" name="name" maxlength="60" value="${esc(initial.name)}" required></div><div><label class="fl">Bônus para acertar</label><input class="inp form-control" name="bonus" type="number" min="-20" max="30" value="${Number(initial.bonus)}" required></div></div><div class="g2"><div><label class="fl">Dano (ex.: 1d6+2)</label><input class="inp form-control" name="damage" value="${esc(initial.damage)}" pattern="(?:\\d+d\\d+(?:[+-]\\d+)?|\\d+)" title="Exemplo: 1d6+2" required></div><div><label class="fl">Alcance em pés (ft)</label><input class="inp form-control" name="range" type="number" min="5" max="1000" step="5" value="${Number(initial.range)}" required></div></div><label class="fl">2 · Escolha o alvo</label><select class="inp form-select" name="target" required>${options.map(({token,distance})=>`<option value="${esc(token.id)}" data-distance="${distance}">${esc(tokenName(t,token))} · ${distance} ft${distance<=Number(initial.range)?' · ao alcance':' · fora do alcance'}</option>`).join('')}</select><label class="row sm" style="gap:8px;margin-top:10px"><input type="checkbox" name="saveAttack" checked><span>Salvar este ataque neste monstro para usar depois</span></label><button class="btn big" type="submit" style="margin-top:12px">⚔ Rolar contra o alvo</button></form>`);
  const attackForm=$('#sheet form[onsubmit*="resolveMonsterAttack"]');if(attackForm)filterMonsterAttackTargets(attackForm);
}
function selectMonsterAttack(select,tid,enemyId){
  const form=select.form,enemy=tbl(tid)?.tokens.find(token=>token.id===enemyId);if(!form)return;
  const attack=select.value===''?{name:'Ataque básico',bonus:4,damage:'1d6+2',range:5}:enemy?.attacks?.[Number(select.value)];if(!attack)return;
  form.elements.name.value=attack.name;form.elements.bonus.value=attack.bonus;form.elements.damage.value=attack.damage;form.elements.range.value=attack.range;filterMonsterAttackTargets(form);
}
function filterMonsterAttackTargets(form){
  const range=Number(form.elements.range?.value)||0,target=form.elements.target,button=form.querySelector('button[type="submit"]');if(!target)return;
  let available=0;
  Array.from(target.options).forEach(option=>{const inRange=Number(option.dataset.distance)<=range;option.disabled=!inRange;if(inRange)available++;option.textContent=option.textContent.replace(/ · (?:ao alcance|fora do alcance)$/,'')+(inRange?' · ao alcance':' · fora do alcance');});
  if(target.selectedOptions[0]?.disabled){const first=Array.from(target.options).find(option=>!option.disabled);if(first)first.selected=true;}
  if(button){button.disabled=!available;button.title=available?'':'Nenhum personagem está dentro do alcance deste ataque';}
}
async function resolveMonsterAttack(event,tid,enemyId){
  event.preventDefault();const t=tbl(tid),enemy=t?.tokens.find(token=>token.id===enemyId&&token.kind==='npc');if(!t||!enemy||!isGM(t))return;
  if(t.initiative?.active&&(t.initiative.currentKey!=='npc:'+enemyId||!t.initiative.turnReleased))return toast('A vez deste monstro não está liberada');
  const form=new FormData(event.target),name=String(form.get('name')||'Ataque do monstro').trim(),bonus=Number(form.get('bonus')),range=Number(form.get('range')),damageSpec=String(form.get('damage')||'').replace(/\s/g,'').toLowerCase(),targetId=String(form.get('target')||''),target=t.tokens.find(token=>token.id===targetId&&token.kind!=='npc'),member=target&&t.members.find(item=>item.user===target.owner||item.id===target.owner);
  const match=damageSpec.match(/^(?:(\d+)d(\d+)(?:([+-])(\d+))?|(\d+))$/);
  if(!target||!member||!Number.isInteger(bonus)||!Number.isInteger(range)||range<5||!match||Number(match[1]||0)>20||Number(match[2]||0)>100||Number(match[2]||0)===1)return toast('Confira alvo, bônus, dano e alcance');
  if(attackDistanceFeet(enemy,target)>range)return toast(`${tokenName(t,target)} está fora do alcance`);
  if(event.target.elements.saveAttack?.checked){enemy.attacks=Array.isArray(enemy.attacks)?enemy.attacks:[];const profile={name,bonus,damage:damageSpec,range},existing=enemy.attacks.findIndex(attack=>attack.name.toLocaleLowerCase()===name.toLocaleLowerCase());if(existing>=0)enemy.attacks[existing]=profile;else enemy.attacks.push(profile);save();}
  const attack=await rollDice({sides:20,bonus,label:`${enemy.name||'Monstro'}: ${name}`}),current=tbl(tid);if(!current||!isGM(current))return;
  if(current.initiative?.active&&(current.initiative.currentKey!=='npc:'+enemyId||!current.initiative.turnReleased))return toast('A vez deste monstro terminou; o ataque foi cancelado');
  const liveEnemy=current.tokens.find(token=>token.id===enemyId),liveTarget=current.tokens.find(token=>token.id===targetId);if(!liveEnemy||!liveTarget||attackDistanceFeet(liveEnemy,liveTarget)>range)return toast('O alvo saiu do alcance antes da resolução');
  const defense=Number(liveTarget.ac)||Number(member.ac)||10,hit=attack.total>=defense;let damageText='';
  if(hit){const diceCount=Number(match[1]||0),sides=Number(match[2]||0),flat=Number(match[5]||0)+(match[3]==='-'?-Number(match[4]||0):Number(match[4]||0)),rollCount=diceCount*(attack.crit?2:1),damageRoll=rollCount?await rollDice({sides,count:rollCount,label:`Dano: ${name}`}):{total:0,animated:false},amount=Math.max(0,damageRoll.total+flat),ref=hpTarget(current,'m:'+current.members.findIndex(item=>item.user===liveTarget.owner||item.id===liveTarget.owner));
    if(ref){const old=ref.hp,absorbed=Math.min(ref.temp,amount),next=Math.max(0,old-(amount-absorbed));ref.commit(next,ref.temp-absorbed);damageText=` · ${amount} dano${absorbed?` (${absorbed} absorvido)`:''}; PV ${old} → ${next}`;}}
  const result=`${tokenName(current,liveEnemy)} usou ${name} contra ${tokenName(current,liveTarget)}: ${attack.total} contra CA ${defense} — ${hit?'acertou':'errou'}${hit?(attack.crit?' (crítico)':'')+damageText:''}.`;
  addChat(current,{who:`${me().name} (Mestre)`,dice:true,text:result});closeModal();render();if(!attack.animated)vib();
}

/* ================= ATAQUES NO MAPA ================= */
function attackDistanceFeet(a,b){return Math.max(Math.abs(Number(a.x)-Number(b.x)),Math.abs(Number(a.y)-Number(b.y)))*5;}
function attackRangeFeet(item){
  const props=String(item?.props||''),match=props.match(/(?:muni(?:ç|c)[aã]o|arremesso)\s*\((\d+)\s*\/\s*(\d+)\s*m\)/i);
  if(match)return Math.round(Number(match[1])*3.28084);
  const reach=props.match(/alcance\s*\((\d+)\s*ft\)/i);if(reach)return Number(reach[1]);
  return 5;
}
function attackOptions(character,distance){
  const options=[{key:'unarmed',name:'Ataque desarmado',line:'1 dano contundente',props:'',range:5,damage:'1',unarmed:true}];
  for(const key of Object.values(character?.equip||{})){
    const item=ITEMS[key];if(!item||item.slot!=='arma')continue;
    const range=attackRangeFeet(item),ranged=/muni(?:ç|c)[aã]o|arremesso/i.test(item.props||'');
    if(distance<=(ranged?range:Math.max(5,range)))options.push({key,name:item.name,line:item.line,props:item.props||'',range,damage:(String(item.line||'').match(/\d+d\d+/i)||[])[0]||'1',ranged});
  }
  return options.filter(option=>distance<=option.range||(!option.ranged&&distance<=5));
}
function attackModal(t,target){
  if(!canRollInTable(t))return toast('Aguarde o mestre liberar sua vez para atacar');
  const member=t.members.find(m=>m.user===S.session),character=member&&getChar(member.charId),own=t.tokens.find(token=>token.owner===S.session);
  if(!own||!character)return toast('Coloque seu token no mapa e vincule um personagem para atacar');
  const distance=attackDistanceFeet(own,target),options=attackOptions(character,distance);
  if(!options.length)return toast(`${tokenName(t,target)} está a ${distance} ft: fora do alcance das suas armas`);
  const targetRef=target.kind==='npc'?'n:'+target.id:'m:'+t.members.findIndex(m=>m.user===target.owner||m.id===target.owner);
  modal(mh('Atacar '+esc(tokenName(t,target)))+`<p class="muted sm">Distância: ${distance} ft · CA ${Number(target.ac)||10}. Escolha uma forma de ataque; a rolagem inclui proficiência e o modificador do atributo.</p>${options.map((option,index)=>`<button class="card item" style="width:100%;text-align:left;margin:6px 0" onclick="resolveAttack('${t.id}','${target.id}','${option.key}',${index===0?'true':'false'})"><b>${esc(option.name)}</b><br><small class="muted">${esc(option.line)} · alcance ${option.range} ft</small></button>`).join('')}`);
}
async function resolveAttack(tid,targetId,itemKey,unarmed){
  const t=tbl(tid),target=t?.tokens.find(token=>token.id===targetId),member=t?.members.find(m=>m.user===S.session),character=member&&getChar(member.charId),attacker=t?.tokens.find(token=>token.owner===S.session);
  if(!t||!target||!character||!attacker)return;
  if(!canRollInTable(t))return toast('Aguarde o mestre liberar sua vez para atacar');
  const distance=attackDistanceFeet(attacker,target),item=unarmed?null:ITEMS[itemKey],option=(attackOptions(character,distance)).find(row=>unarmed?row.unarmed:row.key===itemKey);
  if(!option)return toast('Alvo fora do alcance deste ataque');
  const ability=/acuidade/i.test(option.props)?Math.max(mod(character.attrs.for),mod(character.attrs.des)):option.ranged?mod(character.attrs.des):mod(character.attrs.for);
  const bonus=ability+prof(character.lvl),flanked=!!flankingState(t).targets[target.id],who=myName(t),hitRoll=await rollDice({sides:20,mode:flanked?'adv':'normal',bonus,label:`Ataque com ${option.name}`});
  const current=tbl(tid);if(!current||!canRollInTable(current))return toast('Sua vez terminou; o ataque foi cancelado');
  const defense=Number(target.ac)||10,hit=hitRoll.total>=defense,critical=hitRoll.crit;
  let damageText='';
  if(hit){
    const dice=String(option.damage||'1').match(/^(\d+)d(\d+)$/i),count=dice?(Number(dice[1])*(critical?2:1)):0,sides=dice?Number(dice[2]):0;
    const damageRoll=count?await rollDice({sides,count,label:`Dano: ${option.name}`}):{total:Number(option.damage)||1,animated:false},damage={...damageRoll,total:damageRoll.total+ability};
    const latest=tbl(tid),liveTarget=latest?.tokens.find(token=>token.id===targetId);if(!latest||!liveTarget)return;
    if(liveTarget.kind==='npc'){
      const old=Number(liveTarget.hp)||0,amount=Math.max(0,damage.total),absorbed=Math.min(Number(liveTarget.tempHp)||0,amount);liveTarget.tempHp=(Number(liveTarget.tempHp)||0)-absorbed;liveTarget.hp=Math.max(0,old-(amount-absorbed));damageText=` · ${amount} dano${absorbed?` (${absorbed} absorvido)`:''}; PV ${old} → ${liveTarget.hp}`;
    }else{
      const index=latest.members.findIndex(m=>m.user===liveTarget.owner||m.id===liveTarget.owner),ref=hpTarget(latest,'m:'+index);
      if(ref){const old=ref.hp,amount=Math.max(0,damage.total),absorbed=Math.min(ref.temp,amount);ref.commit(Math.max(0,old-(amount-absorbed)),ref.temp-absorbed);damageText=` · ${amount} dano${absorbed?` (${absorbed} absorvido)`:''}; PV ${old} → ${Math.max(0,old-(amount-absorbed))}`;}
    }
  }
  const result=`${who} atacou ${tokenName(t,target)} com ${option.name}: ${hitRoll.total} contra CA ${defense} — ${hit?'acertou':'errou'}${hit?(critical?' (crítico)':'')+damageText:''}.`;
  addChat(tbl(tid),{who,dice:true,text:result});closeModal();render();if(!hitRoll.animated)vib();
}

/* ================= PV DURANTE A SESSÃO ================= */
// ref: 'm:<índice do jogador>' ou 'n:<id do token do inimigo>'. Mestre altera qualquer um; jogador só o próprio personagem.
function hpTarget(t,ref){
  const [kind,key]=String(ref).split(':');
  if(kind==='m'){
    const m=t.members[Number(key)];if(!m)return null;
    const c=m.user===S.session?getChar(m.charId):null,max=Number(c?c.maxHp:m.maxHp)||1;
    const tok=t.tokens.find(k=>k.kind!=='npc'&&(k.owner===m.user||k.owner===m.id));
    return{ref,name:c?.name||m.charName||m.name||'Jogador',hp:Number(c?c.hp:m.hp)||0,max,temp:Number(m.tempHp)||0,tokenId:tok?.id||null,canEdit:isGM(t)||(m.user===S.session&&!!c),
      commit(hp,temp){if(c)c.hp=hp;m.hp=hp;m.maxHp=max;m.tempHp=temp;}};
  }
  const e=t.tokens.find(k=>k.id===key&&k.kind==='npc');if(!e)return null;
  const hp=Number(e.hp)||0,max=Number(e.maxHp)||hp||1;
  return{ref,name:e.name||e.label||'Inimigo',hp,max,temp:Number(e.tempHp)||0,tokenId:e.id,canEdit:isGM(t),
    commit(h,temp){e.hp=h;e.maxHp=max;e.tempHp=temp;}};
}
function hpControlMarkup(t,g){
  if(!g)return '';
  const q=d=>`<button type="button" class="btn sm" onclick="hpApply('${t.id}','${g.ref}','${d<0?'dmg':'heal'}',${Math.abs(d)})">${d>0?'＋':'−'}${Math.abs(d)}</button>`;
  return `<div class="bigtxt" id="hp-readout">PV ${g.hp}/${g.max}${g.temp?` <small class="muted">+${g.temp} temp.</small>`:''}</div>${hpBar({hp:g.hp,maxHp:g.max})}${g.hp===0?'<p class="center sm" style="color:var(--orange);margin:6px 0 0">0 PV — fora de combate</p>':''}
  <div class="gm-hp-actions">${[-5,-1,1,5].map(q).join('')}</div>
  <form onsubmit="event.preventDefault()"><label class="fl" for="hp-amount">Valor</label><input class="inp form-control" id="hp-amount" type="number" inputmode="numeric" min="0" step="1" value="1">
  <div class="hp-actions"><button type="button" class="btn red" onclick="hpApplyInput('${t.id}','${g.ref}','dmg')">Causar dano</button><button type="button" class="btn" onclick="hpApplyInput('${t.id}','${g.ref}','heal')">Curar</button><button type="button" class="btn" onclick="hpApplyInput('${t.id}','${g.ref}','set')">Definir PV</button><button type="button" class="btn" onclick="hpApplyInput('${t.id}','${g.ref}','temp')">PV temporários</button></div></form>`;
}
function hpSessionModal(tid,ref){
  const t=tbl(tid),g=t&&hpTarget(t,ref);if(!g)return;
  if(!g.canEdit)return toast('Você só pode alterar os PV do seu personagem');
  modal(mh('Pontos de vida')+`<p class="muted">${esc(g.name)}</p>${hpControlMarkup(t,g)}${isGM(t)&&g.tokenId?noFlankControl(t,g.tokenId):''}`);
}
function hpApplyInput(tid,ref,mode){
  const n=Number($('#hp-amount')?.value);
  if(!Number.isInteger(n)||n<0||(mode!=='set'&&mode!=='temp'&&n<1))return toast(mode==='set'||mode==='temp'?'Informe um número inteiro a partir de 0':'Informe um número inteiro maior que 0');
  hpApply(tid,ref,mode,n);
}
function hpApply(tid,ref,mode,n){
  const t=tbl(tid),g=t&&hpTarget(t,ref);if(!g||!g.canEdit)return;
  let hp=g.hp,temp=g.temp,txt;
  if(mode==='dmg'){const absorbed=Math.min(temp,n);temp-=absorbed;hp=Math.max(0,hp-(n-absorbed));txt=`${g.name} sofreu ${n} de dano${absorbed?` (${absorbed} absorvido pelos PV temporários)`:''} · PV ${g.hp} → ${hp}`;}
  else if(mode==='heal'){hp=Math.min(g.max,hp+n);txt=`${g.name} recuperou ${hp-g.hp} PV · PV ${g.hp} → ${hp}`;}
  else if(mode==='set'){hp=Math.min(g.max,n);txt=`PV de ${g.name} ajustados: ${g.hp} → ${hp}`;}
  else{temp=n;txt=`${g.name} agora tem ${n} PV temporários`;}
  if(hp===0&&g.hp>0)txt+=' · caiu a 0 PV';
  g.commit(hp,temp);vib();addChat(t,{sys:true,text:txt});render();hpSessionModalOrEnemy(t,ref);
}
function hpSessionModalOrEnemy(t,ref){if(String(ref).startsWith('n:'))manageEnemyModal(t.id,ref.slice(2));else hpSessionModal(t.id,ref);}

/* ================= FLANQUEAMENTO (opcional, DMG 5e 2014, cap. 8) ================= */
// Criaturas em lados ou cantos opostos do inimigo (ambas adjacentes) têm vantagem nos ataques corpo a corpo contra ele.
// Não flanqueia quem está incapacitado (0 PV ou marcado pelo mestre) nem quem não enxerga o alvo (marcação do mestre).
function rulesOf(t){if(!t.rules||typeof t.rules!=='object')t.rules={};if(typeof t.rules.flanking!=='boolean')t.rules.flanking=true;return t.rules;}
function tokenSide(k){return k.kind==='npc'?'npc':'pc';}
function tokenName(t,k){return k.kind==='npc'?(k.name||k.label||'Inimigo'):battleMemberName(t,k.owner);}
function tokenUp(t,k){
  if(k.kind==='npc'){const hp=Number(k.hp);return Number.isNaN(hp)||hp>0;}
  const m=t.members.find(q=>q.user===k.owner||q.id===k.owner);if(!m)return true;
  const c=m.user===S.session?getChar(m.charId):null,hp=Number(c?c.hp:m.hp);return Number.isNaN(hp)||hp>0;
}
function flankingState(t){
  const st={on:rulesOf(t).flanking,targets:{},flankerIds:new Set()};if(!st.on)return st;
  const at={};t.tokens.forEach(k=>at[k.x+','+k.y]=k);
  const can=k=>!!k&&!k.noFlank&&tokenUp(t,k);
  t.tokens.forEach(T=>{
    if(!tokenUp(t,T))return;
    const pairs=[];
    for(let dy=-1;dy<=1;dy++)for(let dx=-1;dx<=1;dx++){
      if(dx<0||(dx===0&&dy<=0))continue;
      const A=at[(T.x+dx)+','+(T.y+dy)],B=at[(T.x-dx)+','+(T.y-dy)];
      if(can(A)&&can(B)&&tokenSide(A)!==tokenSide(T)&&tokenSide(B)!==tokenSide(T))pairs.push([A,B]);
    }
    if(pairs.length){const f=[];pairs.forEach(p=>p.forEach(k=>{if(!f.includes(k.id))f.push(k.id);st.flankerIds.add(k.id);}));st.targets[T.id]={target:T,pairs,flankers:f};}
  });
  return st;
}
function flankingPanel(t,fl){
  if(!fl.on)return '';
  const rows=Object.values(fl.targets);
  const canRoll=r=>r.flankers.some(id=>{const k=t.tokens.find(q=>q.id===id);return k&&(isGM(t)?k.kind==='npc':k.owner===S.session);});
  return `<section class="card flank-panel"><h3>Flanqueamento</h3><p class="muted sm">Regra opcional do Livro do Mestre: quem flanqueia um inimigo, em lados ou cantos opostos, tem vantagem nos ataques corpo a corpo contra ele. Criaturas com 0 PV ou marcadas pelo mestre não flanqueiam.</p>${rows.length?`<ul class="flank-list">${rows.map(r=>`<li class="flank-entry"><div><b>${esc(tokenName(t,r.target))}</b><br><span class="muted sm">flanqueado por ${r.flankers.map(id=>esc(tokenName(t,t.tokens.find(q=>q.id===id)))).join(' + ')}</span></div>${canRoll(r)?`<button class="btn sm" onclick="rollAdv('${t.id}','adv','${r.target.id}')">Atacar com vantagem</button>`:''}</li>`).join('')}</ul>`:'<p class="muted sm" style="margin:8px 0 0">Ninguém está flanqueado agora.</p>'}</section>`;
}
function toggleFlanking(tid){
  const t=tbl(tid);if(!t||!isGM(t))return;const r=rulesOf(t);r.flanking=!r.flanking;
  addChat(t,{sys:true,text:`O mestre ${r.flanking?'ativou':'desativou'} a regra de flanqueamento.`});render();
}
function noFlankControl(t,tokenId){
  const k=t.tokens.find(q=>q.id===tokenId);if(!k||!isGM(t))return '';
  return `<label class="noflank-row"><input type="checkbox" ${k.noFlank?'checked':''} onchange="toggleNoFlank('${t.id}','${tokenId}')"><span>Não pode flanquear (incapacitado ou não enxerga o alvo)</span></label>`;
}
function toggleNoFlank(tid,tokenId){const t=tbl(tid),k=t?.tokens.find(q=>q.id===tokenId);if(!t||!k||!isGM(t))return;k.noFlank=!k.noFlank;save();render();}
async function rollAdv(id,mode,targetId){
  const t0=tbl(id);if(!t0)return;if(!canRollInTable(t0))return toast('Aguarde o mestre liberar sua vez para rolar dados');
  const bonusValue=prompt('Bônus ou penalidade do ataque (negativo para penalidade)','0');if(bonusValue===null)return;const bonus=Number(bonusValue);if(!Number.isInteger(bonus))return toast('Informe um bônus inteiro');
  if(!canRollInTable(tbl(id)))return toast('Sua vez terminou; aguarde o mestre liberar novamente');
  const who=myName(t0),tg0=targetId?t0.tokens.find(k=>k.id===targetId):null,tgName=tg0?tokenName(t0,tg0):'';
  const r=await rollDice({sides:20,mode,bonus,who,label:tg0?'Ataque contra '+tgName:''}),t=tbl(id);if(!t)return;
  const [a,b]=r.values,mod=bonus?` ${bonus>0?'+':'−'} ${Math.abs(bonus)}`:'';
  addChat(t,{who,dice:true,text:`rolou d20 com ${mode==='dis'?'desvantagem':'vantagem'}${tg0?' contra '+tgName+(mode==='adv'?' (flanqueado)':''):''}: [${a}, ${b}]${mod} = ${r.total}${r.crit?' ✨ crítico!':r.fumble?' 💀 falha crítica':''}`});
  if(!r.animated){vib();toast(`🎲 ${who}: ${r.total}`);}
}
/* ================= DADOS E CHAT ================= */
function myName(t){const u=me();if(isGM(t))return u.name+' (Mestre)';const m=t.members.find(q=>q.user===S.session),c=m&&getChar(m.charId);return c?c.name:(m?m.charName:u.name);}
function chatLine(m){return m.sys?`<p class="sys">${esc(m.text)}</p>`:`<p class="${m.dice?'dice':''}"><b>${esc(m.who)}:</b> ${esc(m.text)}</p>`;}
function addChat(t,m){m.t=Date.now();t.chat.push(m);t.chat=t.chat.slice(-100);save();const l=$('#chatlog');if(l){l.insertAdjacentHTML('beforeend',chatLine(m));l.scrollTop=l.scrollHeight;}}
async function rollPrompt(id,n){const t=tbl(id);if(!t)return;if(!canRollInTable(t))return toast('Aguarde o mestre liberar sua vez para rolar dados');const countValue=prompt('Quantidade de dados (1 a 20)','1');if(countValue===null)return;const count=Number(countValue);if(!Number.isInteger(count)||count<1||count>20)return toast('Informe de 1 a 20 dados');const bonusValue=prompt('Bônus ou penalidade (negativo para penalidade)','0');if(bonusValue===null)return;const bonus=Number(bonusValue);if(!Number.isInteger(bonus))return toast('Informe um bônus inteiro');if(!canRollInTable(tbl(id)))return toast('Sua vez terminou; aguarde o mestre liberar novamente');rollTable(id,n,count,bonus);}
async function rollTable(id,n,count=1,bonus=0){
  const t0=tbl(id);if(!t0)return;if(!canRollInTable(t0))return toast('Aguarde o mestre liberar sua vez para rolar dados');const who=myName(t0),r=await rollDice({sides:n,count,bonus,who}),t=tbl(id);if(!t)return;if(!canRollInTable(t))return toast('Sua vez terminou; a rolagem foi cancelada');
  const notation=`${count}d${n}${bonus?` ${bonus>0?'+':'−'} ${Math.abs(bonus)}`:''}`;
  addChat(t,{who,text:`rolou ${notation}: [${r.values.join(', ')}]${bonus?` ${bonus>0?'+':'−'} ${Math.abs(bonus)}`:''} = ${r.total}${n===20&&count===1&&bonus===0&&r.result===20?' ✨ crítico!':n===20&&count===1&&bonus===0&&r.result===1?' 💀 falha crítica':''}`,dice:true});
  if(!r.animated){vib();toast(`🎲 ${who}: ${r.total}`);}
}function chatModal(id){const t=tbl(id);if(!t)return;
  const locked=!canRollInTable(t);
  modal(mh('💬 Chat')+`<div id="chatlog" class="chatlog">${t.chat.map(chatLine).join('')}</div><div class="dicerow">${[4,6,8,10,12,20,100].map(n=>`<button class="btn sm" ${locked?'disabled title="Aguarde sua vez"':''} onclick="rollPrompt('${id}',${n})">d${n}</button>`).join('')}</div><form class="row" onsubmit="chatSend(event,'${id}')"><input class="inp form-control" name="m" placeholder="Mensagem..." maxlength="200" autocomplete="off"><button class="btn">Enviar</button></form>`);
  const l=$('#chatlog');l.scrollTop=l.scrollHeight;}
function refreshOpenChat(){
  const t=tbl(cur()?.id),log=$('#chatlog');if(!t||!log)return;
  const atBottom=log.scrollHeight-log.scrollTop-log.clientHeight<48;
  log.innerHTML=t.chat.map(chatLine).join('');
  if(atBottom)log.scrollTop=log.scrollHeight;
  const locked=!canRollInTable(t);
  document.querySelectorAll('#sheet .dicerow button').forEach(button=>{button.disabled=locked;button.title=locked?'Aguarde sua vez':'';});
}
function chatSend(e,id){e.preventDefault();const i=e.target.elements.m,txt=i.value.trim();if(!txt)return;addChat(tbl(id),{who:myName(tbl(id)),text:txt});i.value='';}

