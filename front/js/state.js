/* Estado do app: armazenamento local, dados iniciais e acesso ao usuário/mesas/personagens */
const KEY = 'fastFantasy.v2';
function freeCell(t){for(let y=1;y<GH;y++)for(let x=1;x<GW;x++)if(!t.tokens.some(k=>k.x===x&&k.y===y))return{x,y};return{x:0,y:0};}
function placeToken(t,owner,label,kind){const p=freeCell(t);t.tokens.push({id:uid(),kind:kind||'p',owner,label,x:p.x,y:p.y});}
function seed(){
  const now=Date.now(),H=36e5;
  const mk=(id,code,name,desc,gm,max,priv,age,list)=>{
    const t={id,code,name,desc,gm,gmUser:null,max,priv,created:now-age,members:[],tokens:[],chat:[{sys:true,text:'Mesa criada por '+gm+'.',t:now-age}]};
    list.forEach((m,i)=>{const mid='n'+id+i;t.members.push({id:mid,user:null,name:m[0],charName:m[1],cls:m[2],hp:m[3],maxHp:m[4]});placeToken(t,mid,m[1][0].toUpperCase());});
    return t;
  };
  return{users:{},session:null,settings:{vibe:true,confirmLeave:true,diceAnim:true},tables:[
    mk('t1','DRAGAO','O Despertar dos Dragões Ancestrais','Uma profecia esquecida desperta os dragões adormecidos. Só os mais bravos podem impedir o fim dos reinos.','Vikell',6,false,2*H,[['Brann','Brann Forjaferro','Clérigo',24,28],['Selene','Selene Brisalua','Patrulheiro',19,22],['Ozz','Ozzrik','Feiticeiro',12,16],['Mira','Mira Tecelã','Bardo',20,20]]),
    mk('t2','TORRE1','A Torre do Mago Louco','Dentro da torre, cada andar guarda uma magia mais perigosa que a anterior.','Merlin',5,false,26*H,[['Dara','Dara Pedralume','Paladino',27,30],['Kip','Kip Ligeiro','Ladino',14,18],['Ulf','Ulf Duro','Bárbaro',33,40]]),
    mk('t3','CORVO7','Taverna do Corvo','Mistério numa taverna à beira da estrada. Apenas convidados.','Edda',4,true,5*H,[['Tom','Tomás Ferrão','Ladino',15,18]])
  ]};
}
function load(){try{const r=JSON.parse(localStorage.getItem(KEY));if(r&&r.users&&r.tables){
  const details={
    'Thorim Pedraforte':{race:'Anão',subrace:'Anão da Montanha',subclass:'Campeão',background:'Soldado',backstory:'Criado entre os clãs das montanhas, Thorim serviu como escudeiro e depois como defensor de uma fortaleza anã. Após perder seu posto numa emboscada, jurou proteger os povos da superfície e provar que sua coragem vale mais que qualquer título.'},
    'Aria Lunergent':{race:'Elfo',subrace:'Alto Elfo',subclass:'Escola de Evocação',background:'Sábia',backstory:'Aria passou anos estudando fenômenos arcanos numa torre afastada. Quando uma estrela caiu perto de sua cidade natal, ela encontrou sinais de uma magia antiga e partiu para descobrir quem a despertou — antes que alguém a use para ferir inocentes.'},
    'Kael Sombravento':{race:'Halfling',subrace:'Pés Leves',subclass:'Ladrão',background:'Órfão',backstory:'Kael cresceu nos becos de uma cidade portuária, vivendo de pequenos golpes e fugas por telhados. Um mapa roubado revelou a localização de um tesouro — e também o símbolo de uma guilda que agora o persegue. Ele pretende chegar primeiro e descobrir por que aquele mapa importa tanto.'}
  };
  Object.values(r.users).forEach(user=>(user.characters||[]).forEach(character=>{const detail=details[character.name];if(detail){for(const [key,value] of Object.entries(detail))if(!character[key])character[key]=value;if(character.name==='Thorim Pedraforte'&&character.equip)character.equip.armadura='cota_malha';}}));
  r.settings=Object.assign({vibe:true,confirmLeave:true,diceAnim:true},r.settings);return r;}}catch(e){}return seed();}
let S=load();
let cloudVersion=0,cloudSaveTimer=null,cloudPollTimer=null,cloudSaving=false,lastLocalChange=0;
const DEMO_CAMPAIGN_IDS=new Set(['t1','t2','t3']);
function save(options={}){
  try{localStorage.setItem(KEY,JSON.stringify(S));}catch(e){}
  if(!options.fromCloud&&apiGetToken()&&me()){
    lastLocalChange=Date.now();
    clearTimeout(cloudSaveTimer);
    cloudSaveTimer=setTimeout(pushCloudChanges,700);
  }
}
function cloudSnapshot(){
  const user=me();if(!user)return null;
  const email=user.email;
  const account=JSON.parse(JSON.stringify(user));delete account.pass;
  const tables=S.tables.filter(t=>t.gmUser===email||(t.members||[]).some(m=>m.user===email));
  return{user:account,tables:JSON.parse(JSON.stringify(tables)),settings:JSON.parse(JSON.stringify(S.settings||{}))};
}
async function pushCloudState(){
  if(!apiGetToken())return;
  const state=cloudSnapshot();if(!state)return;
  try{const result=await apiRequest('state',{method:'PUT',json:state});cloudVersion=Number(result.version)||cloudVersion;}
  catch(error){if(error.status===401)apiSetToken(null);}
}
async function pushCloudCampaigns(){
  if(!apiGetToken())return;
  for(const table of S.tables){
    if(!table.serverVersion||!(table.gmUser===S.session||(table.members||[]).some(member=>member.user===S.session)))continue;
    try{
      const result=await apiRequest('campaign/save',{method:'PUT',json:{table}});
      if(result.table){Object.assign(table,result.table);table.serverVersion=Number(result.version)||table.serverVersion;}
    }catch(error){if(error.status===401){apiSetToken(null);return;}}
  }
}
async function pushCloudChanges(){
  if(!apiGetToken())return;
  if(cloudSaving){clearTimeout(cloudSaveTimer);cloudSaveTimer=setTimeout(pushCloudChanges,500);return;}
  const savedChangeAt=lastLocalChange;
  cloudSaving=true;
  try{await pushCloudState();await pushCloudCampaigns();}
  finally{cloudSaving=false;if(lastLocalChange>savedChangeAt){clearTimeout(cloudSaveTimer);cloudSaveTimer=setTimeout(pushCloudChanges,150);}}
}
function renderAfterCloudUpdate(){
  if(!me()||!cur())return;
  const modalOpen=$('#modal')?.classList.contains('on');
  if(cur().n==='game'){
    if(!modalOpen)render();
    else if($('#chatlog')){render();if(typeof refreshOpenChat==='function')refreshOpenChat();}
  }else if(!modalOpen&&['home','mesas','chars','char'].includes(cur().n))render();
}
function applyCloudState(data){
  const state=data?.state,email=S.session;
  if(!state||!email)return;
  const existing=S.users[email]||{};
  const user=Object.assign({},existing,state.user||{});delete user.pass;
  S.users[email]=user;
  const belongs=t=>t.gmUser===email||(t.members||[]).some(m=>m.user===email);
  S.tables=[...S.tables.filter(t=>!belongs(t)),...(Array.isArray(state.tables)?state.tables:[])];
  if(state.settings)S.settings=Object.assign({},S.settings,state.settings);
  cloudVersion=Number(data.version)||0;
  save({fromCloud:true});
  renderAfterCloudUpdate();
}
async function restoreCloudState(){
  if(!apiGetToken()||!me())return;
  try{
    const remote=await apiRequest('state');
    if(remote.state?.user)applyCloudState(remote);
    else{cloudVersion=Number(remote.version)||0;await pushCloudState();}
    await refreshCloudCampaigns();
    if(!cloudPollTimer)cloudPollTimer=setInterval(()=>{refreshCloudState();refreshCloudCampaigns();},3000);
  }catch(error){if(error.status===401)apiSetToken(null);}
}
async function refreshCloudCampaigns(){
  if(!apiGetToken()||!me()||cloudSaving||Date.now()-lastLocalChange<1500)return;
  const localChangeAtStart=lastLocalChange;
  try{
    const response=await apiRequest('campaigns');
    if(lastLocalChange!==localChangeAtStart)return;
    let changed=false;const remoteIds=new Set();
    (response.campaigns||[]).forEach(entry=>{
      const remote=entry.table;if(!remote)return;
      remote.serverVersion=Number(entry.version)||Number(remote.serverVersion)||0;
      remoteIds.add(remote.id);
      const index=S.tables.findIndex(table=>table.id===remote.id);
      if(index<0){S.tables.push(remote);changed=true;}
      else if(Number(S.tables[index].serverVersion||0)!==remote.serverVersion){S.tables[index]=remote;changed=true;}
    });
    // As mesas de exemplo são apenas dados locais para demonstração; não existem na API,
    // então seus códigos não funcionam para entrar numa sessão online.
    const withoutDemoTables=S.tables.filter(table=>!DEMO_CAMPAIGN_IDS.has(table.id)||remoteIds.has(table.id));
    if(withoutDemoTables.length!==S.tables.length){S.tables=withoutDemoTables;changed=true;}
    const visible=S.tables.filter(table=>!table.serverVersion||!isMember(table)||remoteIds.has(table.id));
    if(visible.length!==S.tables.length){S.tables=visible;changed=true;}
    if(changed){save({fromCloud:true});renderAfterCloudUpdate();}
  }catch(error){if(error.status===401)apiSetToken(null);}
}
async function refreshCloudState(){
  if(!apiGetToken()||!me()||cloudSaving||Date.now()-lastLocalChange<4000)return;
  const localChangeAtStart=lastLocalChange;
  try{const remote=await apiRequest('state');if(lastLocalChange!==localChangeAtStart)return;if(Number(remote.version)>cloudVersion)applyCloudState(remote);}
  catch(error){if(error.status===401){apiSetToken(null);clearInterval(cloudPollTimer);cloudPollTimer=null;}}
}
function stopCloudSync(){clearTimeout(cloudSaveTimer);clearInterval(cloudPollTimer);cloudSaveTimer=null;cloudPollTimer=null;cloudVersion=0;lastLocalChange=0;}
const me=()=>S.session?S.users[S.session]:null;
const tbl=id=>S.tables.find(t=>t.id===id);
const getChar=id=>{const u=me();return u?u.characters.find(c=>c.id===id):null;};
const isGM=t=>!!S.session&&t.gmUser===S.session;
const isMember=t=>isGM(t)||t.members.some(m=>m.user===S.session);
const examples=()=>[
  {id:uid(),name:'Thorim Pedraforte',race:'Anão',subrace:'Anão da Montanha',cls:'Guerreiro',subclass:'Campeão',background:'Soldado',backstory:'Criado entre os clãs das montanhas, Thorim serviu como escudeiro e depois como defensor de uma fortaleza anã. Após perder seu posto numa emboscada, jurou proteger os povos da superfície e provar que sua coragem vale mais que qualquer título.',lvl:3,hp:31,maxHp:31,ac:18,speed:30,attrs:{for:18,des:12,con:16,int:8,sab:10,car:12},skills:['atletismo','intimidacao','percepcao'],equip:{arma:'espada_longa',escudo:'escudo',armadura:'cota_malha',kit:'kit_aventureiro'}},
  {id:uid(),name:'Aria Lunergent',race:'Elfo',subrace:'Alto Elfo',cls:'Mago',subclass:'Escola de Evocação',background:'Sábia',backstory:'Aria passou anos estudando fenômenos arcanos numa torre afastada. Quando uma estrela caiu perto de sua cidade natal, ela encontrou sinais de uma magia antiga e partiu para descobrir quem a despertou — antes que alguém a use para ferir inocentes.',lvl:3,hp:20,maxHp:20,ac:12,speed:30,attrs:{for:8,des:14,con:14,int:16,sab:12,car:10},skills:['arcanismo','historia','investigacao'],equip:{arma:'cajado',escudo:'',armadura:'',kit:'kit_estudioso'}},
  {id:uid(),name:'Kael Sombravento',race:'Halfling',subrace:'Pés Leves',cls:'Ladino',subclass:'Ladrão',background:'Órfão',backstory:'Kael cresceu nos becos de uma cidade portuária, vivendo de pequenos golpes e fugas por telhados. Um mapa roubado revelou a localização de um tesouro — e também o símbolo de uma guilda que agora o persegue. Ele pretende chegar primeiro e descobrir por que aquele mapa importa tanto.',lvl:3,hp:21,maxHp:21,ac:14,speed:30,attrs:{for:10,des:16,con:12,int:12,sab:13,car:12},skills:['furtividade','prestidigitacao','percepcao','acrobacia'],equip:{arma:'adaga',escudo:'',armadura:'couro',kit:'kit_masmorra'}}
];
