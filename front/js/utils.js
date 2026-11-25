/* Funções utilitárias (texto, números, tempo, hash) */
const $ = s => document.querySelector(s);
const esc = s => String(s ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const uid = () => Math.random().toString(36).slice(2, 9);
const mod = v => Math.floor((v - 10) / 2);
const fmt = n => (n >= 0 ? '+' : '') + n;
const prof = l => 2 + Math.floor((l - 1) / 4);
// Hash simples, só para a demonstração local (não há servidor). Não use isto em produção.
function hash(s){let h1=0xdeadbeef,h2=0x41c6ce57;for(let i=0;i<s.length;i++){const c=s.charCodeAt(i);h1=Math.imul(h1^c,2654435761);h2=Math.imul(h2^c,1597334677);}h1=Math.imul(h1^(h1>>>16),2246822507)^Math.imul(h2^(h2>>>13),3266489909);h2=Math.imul(h2^(h2>>>16),2246822507)^Math.imul(h1^(h1>>>13),3266489909);return(4294967296*(2097151&h2)+(h1>>>0)).toString(36);}
function ago(ts){const m=Math.floor((Date.now()-ts)/60000);if(m<1)return 'agora mesmo';if(m<60)return m+' min atrás';const h=Math.floor(m/60);if(h<24)return h+(h>1?' horas':' hora')+' atrás';const d=Math.floor(h/24);return d+(d>1?' dias':' dia')+' atrás';}
function fmtTime(ms){const m=Math.floor(ms/60000);return m<60?m+'min':Math.floor(m/60)+'h';}
function genCode(){const a='ABCDEFGHJKLMNPQRSTUVWXYZ23456789';let c='';for(let i=0;i<6;i++)c+=a[Math.floor(Math.random()*a.length)];return c;}
