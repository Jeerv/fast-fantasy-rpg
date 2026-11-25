/* Navegação entre telas e renderização */
let stack=[{n:'welcome'}],lastRoute=null,gameSince=null;
const cur=()=>stack[stack.length-1];
function push(){try{history.pushState({},'');}catch(e){}}
function go(n,p){stack.push(Object.assign({n},p||{}));push();render();}
function back(){if(stack.length>1){stack.pop();render();}}
function tab(k){stack=[{n:'home'}];if(k!=='home')stack.push({n:k});push();render();}
window.addEventListener('popstate',()=>{if(stack.length>1){stack.pop();render();}});
function flushTime(){if(gameSince&&me()){me().stats.ms+=Date.now()-gameSince;save();}gameSince=null;}
window.addEventListener('pagehide',flushTime);
function render(){
  let r=cur();
  const pub=['welcome','login','register'];
  if(!me()&&!pub.includes(r.n)){stack=[{n:'welcome'}];r=cur();}
  const old=$('#screen .body');
  const keep=old&&lastRoute===r.n?old.scrollTop:0;
  const html=SCREENS[r.n](r);
  if(html===null){if(stack.length>1)stack.pop();else stack=[{n:'home'}];return render();}
  if(r.n==='game'){if(!gameSince)gameSince=Date.now();}else flushTime();
$('#app').classList.toggle('authenticated',!!me());
  $('#screen').innerHTML=html;
  if(me()&&!pub.includes(r.n)&&!$('#screen .nav')){
    const active=['char','charForm','charSpells','item'].includes(r.n)?'chars':['createTable','game'].includes(r.n)?'mesas':r.n;
    $('#screen').insertAdjacentHTML('beforeend',nav(active));
  }
  const b=$('#screen .body');if(b&&keep)b.scrollTop=keep;
  lastRoute=r.n;
}

const SCREENS = {}; // cada arquivo em js/screens/ registra suas telas aqui




