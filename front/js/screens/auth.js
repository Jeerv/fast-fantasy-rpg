/* Telas de boas-vindas, login e registro */
SCREENS.welcome=function(){return `<div class="body c"><div class="spacer"></div>${LOGO_BOOK}<h1 class="brand">Fast Fantasy RPG</h1><p class="tag">Jogue RPG de mesa online<br>rápida e simples.</p>
  <div class="stack" style="margin-top:34px"><button class="btn big" onclick="go('login')">Começar Aventura</button><button class="btn big" onclick="go('register')">Registrar</button></div>
  <div class="spacer"></div><p class="quote">"Cada rolar de dados escreve uma nova página da história..."</p></div>`;};

SCREENS.login=function(){return authScreen('login');};

SCREENS.register=function(){return authScreen('register');};

function authScreen(mode){const reg=mode==='register';
  return `<form class="body c" onsubmit="doAuth(event,'${mode}')"><div class="spacer"></div>${LOGO_BOOK}<h1 class="brand" style="font-size:30px">Fast Fantasy RPG</h1><p class="tag">Jogue RPG de mesa online<br>rápida e simples.</p>
  <h2 class="auth-title">${reg?'Registrar':'Entrar'}</h2>
  ${reg?'<input class="inp form-control" name="name" placeholder="Nome de usuário..." required maxlength="100" autocomplete="nickname">':''}
  <input class="inp form-control" type="email" name="email" placeholder="Email..." required maxlength="150" autocomplete="email">
  <input class="inp form-control" type="password" name="pw" placeholder="Senha..." required minlength="6" maxlength="256" autocomplete="${reg?'new-password':'current-password'}">
  ${reg?'<input class="inp form-control" type="password" name="pw2" placeholder="Confirmar senha..." required minlength="6" maxlength="256" autocomplete="new-password">':''}
  <p class="err" id="err"></p>
  <div class="row"><button class="btn">${reg?'Criar conta':'Entrar'}</button><button type="button" class="btn" onclick="back()">Voltar</button></div>
  <div class="spacer"></div><p class="quote">"Cada rolar de dados escreve uma nova página da história..."</p></form>`;}

async function doAuth(e,mode){e.preventDefault();const form=e.target,submit=form.querySelector('button[type="submit"],button:not([type])'),f=new FormData(form),email=String(f.get('email')).trim().toLowerCase(),pw=String(f.get('pw'));
  const err=m=>{$('#err').textContent=m;};
  if(!/^\S+@\S+\.\S+$/.test(email))return err('E-mail inválido.');
  if(pw.length<6)return err('A senha precisa ter ao menos 6 caracteres.');
  if(mode==='register'&&pw!==f.get('pw2'))return err('As senhas não conferem.');
  if(submit)submit.disabled=true;
  try{
    const result=await apiRequest(mode==='register'?'auth/register':'auth/login',{
      method:'POST',
      body:{name:String(f.get('name')||'').trim(),email,password:pw}
    });
    apiSetToken(result.token);
    const existing=S.users[email]||{};
    const account=Object.assign({},existing,{
      email:result.user.email,
      name:result.user.name,
      premium:result.user.premium,
      tokenId:existing.tokenId??0,
      characters:existing.characters||[],
      stats:existing.stats||{sessions:0,ms:0}
    });
    delete account.pass;
    S.users[email]=account;
    S.session=email;await restoreCloudState();save();stack=[{n:'home'}];push();render();
    toast(mode==='register'?'Conta criada! Bem-vindo.':'Bem-vindo de volta!');
  }catch(error){
    err(error.status===0?'Não foi possível conectar ao servidor. Confira a conexão e tente novamente.':error.message);
  }finally{
    if(submit)submit.disabled=false;
  }
}
function logout(){flushTime();stopCloudSync();apiForgetSession();S.session=null;save();stack=[{n:'welcome'}];render();}

