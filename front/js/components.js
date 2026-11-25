/* Componentes de interface: toast, modal, cabeçalho, menu inferior e barra de HP */
let toastT;
function toast(m){const t=$('#toast');t.textContent=m;t.classList.add('on');clearTimeout(toastT);toastT=setTimeout(()=>t.classList.remove('on'),2400);}
function vib(){if(S.settings.vibe&&navigator.vibrate)navigator.vibrate(30);}
function modal(h){$('#sheet').innerHTML=h;$('#modal').classList.add('on');}
function closeModal(){$('#modal').classList.remove('on');}
document.addEventListener('keydown',e=>{if(e.key==='Escape')closeModal();});
const mh=t=>`<div class="mh"><b>${t}</b><button class="ibtn" onclick="closeModal()" aria-label="Fechar">✕</button></div>`;
const TOKEN_COUNT=25;
function tokenAvatar(index=0,extra=''){
  const n=Math.max(0,Math.min(TOKEN_COUNT-1,Number(index)||0)),x=n%6,y=Math.floor(n/6);
  return `<span class="token-avatar ${extra}" style="background-position:-${x*40}px -${y*40}px" role="img" aria-label="Token ${n+1}"></span>`;
}
function tokenPicker(name,selected=0){
  const current=Math.max(0,Math.min(TOKEN_COUNT-1,Number(selected)||0));
  return `<div class="token-picker" role="radiogroup" aria-label="Escolha um token">${Array.from({length:TOKEN_COUNT},(_,n)=>`<label class="token-choice" title="Token ${n+1}"><input type="radio" name="${esc(name)}" value="${n}" ${n===current?'checked':''}><span>${tokenAvatar(n,'pick-token')}</span></label>`).join('')}</div>`;
}
function hdr(title,o){o=o||{};return `<header class="hdr"><button class="ibtn" onclick="back()" aria-label="Voltar">←</button><h1>${esc(title)}</h1>${o.sub?`<span class="sub">${esc(o.sub)}</span>`:''}${o.right||''}</header>`;}
function nav(a){const it=[['home','Início',IC_BOOK],['chars','Personagens',IC_PEOPLE],['mesas','Mesas',IC_D20],['account','Conta',IC_BOOK]];
  return `<nav class="nav">${it.map(([k,l,i])=>{const icon=k==='account'?tokenAvatar(me()?.tokenId??0,'nav-token'):i;return `<button class="nv ${a===k?'on':''}" onclick="tab('${k}')"><i>${icon}</i><span>${l}</span></button>`;}).join('')}</nav>`;}
function hpBar(c){const p=Math.max(0,Math.min(100,c.maxHp?c.hp/c.maxHp*100:0));const col=p>50?'#3fe05a':p>25?'#f2c14e':'#e5533d';return `<span class="hpbar"><i style="width:${p}%;background:${col}"></i></span>`;}





