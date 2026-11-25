/* Mesas: lista, criar mesa, entrar por código e Mesa Rápida */
SCREENS.mesas=function(){return hdr('Mesas')+`<div class="body tables-page">
  <div class="table-join"><div><label class="fl">ENTRAR COM CÓDIGO</label><input id="code" class="inp form-control" placeholder="Digite o código..." maxlength="8" autocapitalize="characters" autocomplete="off" style="text-transform:uppercase" onkeydown="if(event.key==='Enter')joinByCode()"></div><button class="btn" onclick="joinByCode()">Entrar na mesa</button></div>
  <div class="table-heading"><div><div class="eyebrow">SEU GRUPO</div><h2 class="hello">Mesas ativas</h2><p class="muted">Encontre uma campanha ou continue de onde parou.</p></div><button class="btn" onclick="openCreate()">＋ Criar Mesa</button></div>
  <div class="tables-list">${S.tables.filter(t=>!t.priv||isMember(t)).sort((a,b)=>b.created-a.created).map(t=>`<button class="card item table-card" onclick="openTable('${t.id}')"><div class="table-card-top"><span class="table-icon">${t.priv?'🔒':'⚄'}</span><span class="table-status">${t.status||'ativa'}</span></div><div class="table-card-name">${esc(t.name)}</div><div class="table-master">Mestre: ${esc(t.gm)}</div><div class="row between sm muted table-meta"><span>${IC_PEOPLE.replace('<svg','<svg width="16" height="16" style="vertical-align:-3px"')} ${t.members.length}/${t.max} jogadores</span><span>${ago(t.created)}</span></div>${isMember(t)?'<div class="sm table-joined">✔ Você está nesta mesa</div>':''}</button>`).join('')||'<p class="muted center">Nenhuma mesa ativa.</p>'}</div>
</div>`+nav('mesas');};
SCREENS.createTable=function(){return hdr('Criar Mesa')+`<form class="body form-page" onsubmit="saveTable(event)">
  <label class="lbl" style="margin-top:0">Nome da Mesa</label><input class="inp form-control" name="name" required maxlength="100" autocomplete="off">
  <label class="lbl">Descrição</label><textarea class="inp form-control" name="desc" maxlength="255" placeholder="Descreva a aventura para os jogadores..."></textarea>
  <label class="lbl">Máximo de Jogadores</label>
  <div class="stepper"><button type="button" class="btn sm" onclick="incMax(-1)">−</button><b id="ctmax">${ctMax}</b><button type="button" class="btn sm" onclick="incMax(1)">+</button></div>
  <label class="lbl">Mesa Privada</label>
  <div class="card row"><span style="font-size:24px">🔒</span><span class="grow sm">Apenas com código de acesso...</span><span id="ctpriv" class="sw ${ctPriv?'on':''}" role="switch" tabindex="0" onclick="togPriv()"><i></i></span></div>
  <button class="btn big" style="margin-top:22px">Criar Mesa</button></form>`;};

let ctMax=5,ctPriv=false;
function openCreate(){ctMax=5;ctPriv=false;go('createTable');}
function incMax(d){ctMax=Math.max(2,Math.min(10,ctMax+d));$('#ctmax').textContent=ctMax;}
function togPriv(){ctPriv=!ctPriv;$('#ctpriv').classList.toggle('on',ctPriv);}
async function saveTable(e){e.preventDefault();const f=new FormData(e.target),name=String(f.get('name')).trim();if(!name)return toast('Dê um nome à mesa');
  const u=me(),t={id:uid(),code:genCode(),name,desc:String(f.get('desc')).trim(),max:ctMax,priv:ctPriv,gm:u.name,gmUser:u.email,created:Date.now(),members:[],tokens:[],chat:[{sys:true,text:'Mesa criada por '+u.name+'.',t:Date.now()}]};
  try{const created=await apiRequest('campaign/create',{method:'POST',body:{table:JSON.stringify(t)}});Object.assign(t,created.table);t.serverVersion=Number(created.version)||1;}
  catch(error){return toast(error.status===0?'Não foi possível criar a mesa no servidor. Confira sua conexão.':error.message);}
  S.tables.unshift(t);u.stats.sessions++;save();stack[stack.length-1]={n:'game',id:t.id};render();
  modal(mh('Mesa criada!')+`<p class="muted">Compartilhe o código para seus jogadores entrarem:</p><div class="bigtxt" style="color:var(--yellow);letter-spacing:4px">${t.code}</div><button class="btn big" style="font-size:16px" onclick="copyCode('${t.code}')">Copiar código</button>`);}
function copyCode(c){try{navigator.clipboard.writeText(c).then(()=>toast('Código copiado'),()=>toast('Código: '+c));}catch(e){toast('Código: '+c);}}
const openingTableIds=new Set();
async function openTable(id){if(openingTableIds.has(id))return;openingTableIds.add(id);toast('Abrindo mesa…');
  try{return await openTableLoaded(id);}catch(error){console.error('Falha ao abrir a mesa:',error);const detail=String(error?.message||error||'Erro desconhecido').slice(0,300);modal(mh('Erro ao abrir a mesa')+`<p class="muted">Falha ao montar a tela da campanha. Envie esta mensagem para eu identificar a causa:</p><pre class="sm" style="white-space:pre-wrap;overflow-wrap:anywhere;color:var(--orange)">${esc(detail)}</pre><button class="btn big" onclick="closeModal()">Fechar</button>`);}
  finally{openingTableIds.delete(id);}}
async function openTableLoaded(id){let t=tbl(id);if(!t)return toast('Mesa não encontrada nesta sessão. Atualize a lista e tente novamente.');
  if(apiGetToken()&&t.code){
    try{
      const response=await apiRequest('campaign',{query:{search:t.code}}),remote=response.table;
      if(remote){remote.serverVersion=Number(response.version)||Number(remote.serverVersion)||0;
        const index=S.tables.findIndex(table=>table.id===remote.id||table.code===remote.code);
        if(index<0)S.tables.push(remote);else S.tables[index]=remote;
        if(remote.id!==id)S.tables=S.tables.filter(table=>table.id!==id||table.id===remote.id);
        save({fromCloud:true});t=tbl(remote.id);
      }
    }catch(error){
      if(error.status===404&&!isMember(t)){S.tables=S.tables.filter(table=>table.id!==id);save({fromCloud:true});return toast('Esta mesa não existe no servidor. Confira se está usando o código ou a conta corretos.');}
      if(error.status!==404&&!isMember(t))return toast(error.status===0?'Não foi possível atualizar a mesa. Confira a conexão.':error.message);
      if(error.status===404)toast('A mesa só existe nesta cópia local; abrindo sem sincronização.');
    }
  }
  if(!t)return toast('Mesa não encontrada nesta sessão. Atualize a lista e tente novamente.');if(isMember(t))return go('game',{id:t.id});const full=t.members.length>=t.max;
  modal(mh(esc(t.name))+`<p class="muted" style="font-family:var(--serif);font-size:18px">${esc(t.desc||'Sem descrição.')}</p><p style="color:var(--orange)">Mestre: ${esc(t.gm)}</p><p class="sm muted">👥 ${t.members.length}/${t.max} jogadores · ${ago(t.created)}</p><button class="btn big" ${full?'disabled':''} onclick="pickChar('${t.id}')">${full?'Mesa lotada':'Entrar na mesa'}</button>`);}
function pickChar(tid){const u=me(),t=tbl(tid);
  if(!u.characters.length){modal(mh('Sem personagens')+`<p class="muted">Crie um personagem para entrar numa mesa.</p><button class="btn big" style="font-size:17px" onclick="closeModal();go('charForm')">Criar personagem</button><button class="btn big" style="font-size:15px;margin-top:10px" onclick="addExamples();pickChar('${tid}')">Usar personagens de exemplo</button>`);return;}
  modal(mh('Escolha seu personagem')+`<p class="muted sm">${esc(t.name)}</p>${u.characters.map(c=>`<button class="card item" onclick="joinTable('${tid}','${c.id}')"><span class="cicon token-wrap">${tokenAvatar(c.tokenId??0,'character-token')}</span><span class="grow"><b>${esc(c.name)}</b><br><small class="muted">${esc(c.cls)} Nv.${c.lvl} · HP ${c.hp}/${c.maxHp}</small></span></button>`).join('')}`);}
async function joinTable(tid,cid){const t=tbl(tid),u=me(),c=getChar(cid);if(!t||!c)return;if(t.members.length>=t.max)return toast('Mesa lotada');
  const member={id:u.email,user:u.email,name:u.name,charId:c.id,charName:c.name,cls:c.cls,hp:c.hp,maxHp:c.maxHp,tokenId:Number(c.tokenId)||0};
  const cell=freeCell(t),token={id:uid(),kind:'p',owner:u.email,label:c.name[0].toUpperCase(),x:cell.x,y:cell.y,name:c.name,tokenId:member.tokenId};
  try{const joined=await apiRequest('campaign/join',{method:'POST',body:{code:t.code,member:JSON.stringify(member),token:JSON.stringify(token)}});Object.assign(t,joined.table);t.serverVersion=Number(joined.version)||t.serverVersion;}
  catch(error){return toast(error.status===0?'Não foi possível entrar na mesa. Confira sua conexão.':error.message);}
  u.stats.sessions++;save();closeModal();go('game',{id:tid});toast('Você entrou na mesa!');}
async function joinByCode(){const code=($('#code').value||'').trim().toUpperCase();if(!code)return toast('Digite o código');
  try{const result=await apiRequest('campaign',{query:{search:code}}),remote=result.table;if(!remote)return toast('Código não encontrado');
    const index=S.tables.findIndex(table=>table.id===remote.id);remote.serverVersion=Number(result.version)||0;
    if(index<0)S.tables.push(remote);else if(Number(S.tables[index].serverVersion||0)<=remote.serverVersion)S.tables[index]=remote;
    const table=tbl(remote.id);if(isMember(table))return go('game',{id:table.id});if(table.members.length>=table.max)return toast('Mesa lotada');pickChar(table.id);
  }catch(error){toast(error.status===0?'Não foi possível buscar a mesa. Confira sua conexão.':error.message);}}
function quickTable(){const o=S.tables.filter(t=>!t.priv&&!isMember(t)&&t.members.length<t.max);if(!o.length)return toast('Nenhuma mesa com vaga agora');pickChar(o[Math.floor(Math.random()*o.length)].id);}


