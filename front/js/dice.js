/* Rolagem de dados com animação: sorteia o resultado antes, anima e só então avisa quem chamou */
const DICE_SHAPES={
  4:{poly:'50,5 97,91 3,91',facet:'M50 5V63M3 91L50 63L97 91'},
  6:{poly:'14,6 86,6 94,14 94,86 86,94 14,94 6,86 6,14',facet:'M22 22H78V78H22Z'},
  8:{poly:'50,3 95,50 50,97 5,50',facet:'M5 50H95M50 3L30 50L50 97M50 3L70 50L50 97'},
  10:{poly:'50,3 96,38 50,97 4,38',facet:'M4 38L50 56L96 38M50 3V56M50 56V97'},
  12:{poly:'50,3 96,37 79,92 21,92 4,37',facet:'M28 42L50 30L72 42L64 70H36ZM50 3V30M96 37L72 42M4 37L28 42M79 92L64 70M21 92L36 70'},
  20:{poly:'50,3 93,27 93,73 50,97 7,73 7,27',facet:'M50 3L28 40H72ZM7 27L28 40M93 27L72 40M7 73L28 40M93 73L72 40M28 40L50 97L72 40M7 73L50 97L93 73'}
};
let diceRun=null;
const diceRand=n=>1+Math.floor(Math.random()*n);
function dieSvg(sides){
  const s=DICE_SHAPES[sides]||DICE_SHAPES[sides===100?10:20];
  return `<svg viewBox="0 0 100 100" aria-hidden="true"><polygon class="body" points="${s.poly}"/><path class="facet" d="${s.facet}"/></svg>`;
}
/* spec: {sides, count, mode:'normal'|'adv'|'dis', bonus, label, who}
   devolve Promise com {values, kept, result, bonus, total, crit, fumble, animated} no momento em que o dado "para" */
function rollDice(spec){
  const sides=spec.sides||20,mode=spec.mode||'normal',count=mode==='normal'?(spec.count||1):2,bonus=spec.bonus||0;
  const values=Array.from({length:count},()=>diceRand(sides));
  let kept=null;
  if(mode==='adv')kept=values[0]>=values[1]?0:1;
  if(mode==='dis')kept=values[0]<=values[1]?0:1;
  const result=kept===null?values.reduce((a,b)=>a+b,0):values[kept];
  const single=sides===20&&(kept!==null||count===1),face=kept===null?values[0]:values[kept];
  const res={sides,mode,values,kept,result,bonus,total:result+bonus,crit:single&&face===20,fumble:single&&face===1,animated:false};
  if(S.settings.diceAnim===false||!document.getElementById('app'))return Promise.resolve(res);
  return new Promise(resolve=>playDice(spec,res,resolve));
}
function playDice(spec,res,resolve){
  if(diceRun)diceRun.close(true);
  const app=document.getElementById('app'),sides=res.sides,calm=window.matchMedia&&matchMedia('(prefers-reduced-motion: reduce)').matches;
  const layer=document.createElement('div');layer.className='dice-layer'+(calm?' calm':'');
  layer.setAttribute('role','status');layer.setAttribute('aria-live','polite');
  const dice=res.values.map((v,i)=>`<div class="die rolling" style="--s:${i%2?-1:1};--d:${i*0.07}s"><div class="die-body">${dieSvg(sides)}</div><span class="die-n${sides===100?' wide':''}">?</span></div>`).join('');
  layer.innerHTML=`<div class="dice-stage"><div class="dice-who">${esc(spec.who?spec.who+' rola…':'Rolando…')}</div><div class="dice-row">${dice}</div><div class="dice-banner"></div><div class="dice-hint">toque para fechar</div></div>`;
  app.appendChild(layer);
  const els=[...layer.querySelectorAll('.die')],nums=els.map(d=>d.querySelector('.die-n'));
  const DURATION=calm?250:1050,STAY=1500;let landed=false,closed=false,tickT=null,landT=null,stayT=null;
  const show=()=>nums.forEach(n=>n.textContent=diceRand(sides));
  const tick=delay=>{show();tickT=setTimeout(()=>tick(Math.min(delay*1.13,170)),delay);};
  const land=()=>{
    if(landed)return;landed=true;clearTimeout(tickT);clearTimeout(landT);
    nums.forEach((n,i)=>n.textContent=res.values[i]);
    els.forEach((d,i)=>{d.classList.remove('rolling');d.classList.add('landed');if(res.kept!==null)d.classList.add(i===res.kept?'kept':'dropped');});
    layer.classList.add('landed');if(res.crit)layer.classList.add('crit');if(res.fumble)layer.classList.add('fumble');
    const head=spec.label?`<div class="dice-label">${esc(spec.label)}</div>`:'';
    const mod=res.bonus?` <span class="dice-mod">${res.bonus>0?'+':'−'} ${Math.abs(res.bonus)}</span> = `:'';
    const raw=res.bonus?`<span class="dice-raw">${res.result}</span>${mod}<span class="dice-total">${res.total}</span>`:`<span class="dice-total">${res.result}</span>`;
    const note=res.crit?'✨ Crítico!':res.fumble?'💀 Falha crítica':res.kept!==null?`${res.mode==='adv'?'Vantagem':'Desvantagem'}: ${res.values[0]} e ${res.values[1]}`:res.values.length>1?`d${sides}: ${res.values.join(' + ')}`:`d${sides}`;
    layer.querySelector('.dice-banner').innerHTML=`${head}<div class="dice-line">${raw}</div><div class="dice-note">${note}</div>`;
    if(typeof vib==='function')vib();
    res.animated=true;resolve(res);
    stayT=setTimeout(()=>close(false),STAY);
  };
  const close=instant=>{
    if(closed)return;closed=true;clearTimeout(tickT);clearTimeout(landT);clearTimeout(stayT);
    if(!landed){landed=true;res.animated=true;resolve(res);}
    if(diceRun&&diceRun.layer===layer)diceRun=null;
    if(instant){layer.remove();return;}
    layer.classList.add('out');setTimeout(()=>layer.remove(),200);
  };
  layer.addEventListener('click',()=>{if(!landed)land();else close(false);});
  diceRun={layer,close};
  tick(45);landT=setTimeout(land,DURATION);
}
