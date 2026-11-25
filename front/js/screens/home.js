/* Início: painel com o mesmo clima visual do protótipo, distribuído em colunas no desktop. */
SCREENS.home=function(){const u=me(),chars=u.characters||[],tables=S.tables.filter(t=>!t.priv||isMember(t)).sort((a,b)=>b.created-a.created);
  return hdr('Início',{sub:'Campanhas, fichas e ferramentas'})+`<div class="body home-body">
  <section class="home-dashboard">
    <div class="home-hero"><div class="home-hero-copy"><div class="eyebrow">SEU PRÓXIMO CAPÍTULO</div><h2>Uma nova aventura espera por você.</h2><p>Prepare seus personagens, encontre uma mesa e reúna seu grupo para começar a jogar.</p><div class="home-hero-actions"><button class="btn big" onclick="tab('mesas')">Explorar mesas</button><button class="btn" onclick="openCreate()">Criar mesa</button></div></div></div>
    <aside class="home-side-panel"><div class="home-welcome"><h2 class="hello">Bem-vindo, ${esc(u.name)}</h2><p class="muted">Seu espaço de jogo está pronto.</p></div><div class="home-stat-grid"><div class="home-stat"><span>Personagens</span><b>${chars.length}</b></div><div class="home-stat"><span>Mesas ativas</span><b>${S.tables.filter(t=>t.status==='ativa').length}</b></div><div class="home-stat"><span>Sessões</span><b>${u.stats.sessions}</b></div><div class="home-stat"><span>Itens equipados</span><b>${chars.reduce((n,c)=>n+Object.values(c.equip||{}).filter(Boolean).length,0)}</b></div></div></aside>
  </section>
  <section class="home-lower">
    <div class="home-section"><div class="home-section-head"><h3 class="h2">Seus personagens</h3><button class="btn sm" onclick="tab('chars')">Ver todos</button></div>
      <div class="home-character-list">${chars.slice(0,4).map(c=>`<button class="card item dark" onclick="go('char',{id:'${c.id}'})"><span class="cicon token-wrap">${tokenAvatar(c.tokenId??0,'character-token')}</span><span class="grow"><b>${esc(c.name)}</b><br><small class="muted">${esc(c.cls)} · Nível ${c.lvl}</small></span><span>›</span></button>`).join('')||'<p class="muted">Você ainda não criou personagens.</p>'}</div>
      <button class="btn big" style="margin-top:12px" onclick="go('charForm')">＋ Novo personagem</button>
    </div>
    <div class="home-section"><div class="home-section-head"><h3 class="h2">Mesas disponíveis</h3><button class="btn sm" onclick="tab('mesas')">Ver mesas</button></div>
      <div class="home-table-list">${tables.slice(0,3).map(t=>`<button class="card item dark" onclick="openTable('${t.id}')"><div class="row between"><b>${t.priv?'🔒 ':''}${esc(t.name)}</b><span class="sm" style="color:var(--green)">● ${esc(t.status||'ativa')}</span></div><div class="row between sm muted" style="margin-top:8px"><span>Mestre: ${esc(t.gm)}</span><span>${t.members.length}/${t.max} jogadores</span></div></button>`).join('')||'<p class="muted">Nenhuma mesa disponível agora.</p>'}</div>
      <button class="btn big" style="margin-top:12px" onclick="quickTable()">Encontrar mesa rápida</button>
    </div>
  </section></div>`+nav('home');};


