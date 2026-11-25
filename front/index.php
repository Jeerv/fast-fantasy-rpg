<!DOCTYPE html>
<html lang="pt-BR">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover">
<meta name="theme-color" content="#2a1409">
<title>Fast Fantasy RPG</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-yEPZ7jP9S0QKiUQ0m3jjsO4ProIW7wvR9gXkI7JOGa2XVsw53Yke2bsdkBbjmmiX" crossorigin="anonymous">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Cormorant+Garamond:ital,wght@500;600;700&display=swap" rel="stylesheet">
<link rel="stylesheet" href="css/base.css">
<link rel="stylesheet" href="css/components.css">
<link rel="stylesheet" href="css/screens.css?v=turn-control-1">
<link rel="stylesheet" href="css/responsive.css">
<link rel="stylesheet" href="css/dice.css">
<link rel="icon" type="image/x-icon" href="img/favicon.png">
</head>
<body>
<div id="app" class="d-flex flex-column">
  <div id="screen" class="d-flex flex-column"></div>
  <div id="toast"></div>
  <div id="modal" onclick="if(event.target===this)closeModal()"><div id="sheet"></div></div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.9/dist/js/bootstrap.bundle.min.js" integrity="sha384-WyOxWvTyr1QaMPfiPJSv76iReVthorl5uWlFtZPyK0IEB0hGzo9y5Bp9mYanr8Bu" crossorigin="anonymous"></script>
<!-- A ordem importa: cada arquivo usa o que os anteriores definem -->
<script src="js/utils.js"></script>
<script src="js/data.js"></script>
<script src="js/rpg-catalog.js"></script>
<script src="js/db-contract.js"></script>
<script src="js/icons.js"></script>
<script src="js/state.js?v=realtime-sync-7"></script>
<script src="js/components.js"></script>
<script src="js/dice.js"></script>
<script src="js/router.js"></script>
<script src="js/api-config.js"></script>
<script src="js/api.js"></script>
<script src="js/screens/auth.js"></script>
<script src="js/screens/home.js?v=table-refresh-3"></script>
<script src="js/screens/characters.js?v=free-character-rolls-1"></script>
<script src="js/screens/tables.js?v=table-open-5"></script>
<script src="js/screens/game.js?v=gm-monster-attacks-1"></script>
<script src="js/screens/account.js"></script>
<script src="js/app.js"></script>
</body>
</html>





