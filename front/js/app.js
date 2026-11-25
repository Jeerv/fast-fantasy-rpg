/* Inicialização do app */
'use strict';
async function startApp(){
  const token=apiGetToken();
  if(token){
    try{
      const result=await apiRequest('auth/me');
      const user=result.user,email=user.email,existing=S.users[email]||{};
      const account=Object.assign({},existing,{
        email:user.email,
        name:user.name,
        premium:user.premium,
        tokenId:existing.tokenId??0,
        characters:existing.characters||[],
        stats:existing.stats||{sessions:0,ms:0}
      });
      delete account.pass;
      S.users[email]=account;
      S.session=email;
      await restoreCloudState();
    }catch(error){
      if(error.status===401){apiSetToken(null);S.session=null;save();}
    }
  }
  if(me())stack=[{n:'home'}];
  render();
}
startApp();
