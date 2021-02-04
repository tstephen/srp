<!DOCTYPE html>
<html lang="en">
<head>
  <title>Sustainable Resource Planning</title>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  
  <link href="/css/srp.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="images/icon/srp-icon-32x32.png" />
  <style>
    html,body,section#main {
      height:100%;
      text-align: center;
      width: 100%;
    }
    section#main {
      margin: auto;
      padding-top: 10%;
    }
    section#login {
      padding-top: 0;
      padding-right: 20px;
      position: absolute;
      top: 20px;
      right: 20px;
      width: 30%;
    }
    #container {
      left: auto;
      right: auto;
      top: 20px;
    }
    #container a {
      float: right;
    }
    .btn { 
      height: 40px;
    }
  </style>
<head>
<body>

  <section id="container">
    <h2>Oops! Something went wrong!</h2>
    <p>The server response was: ${error}</p>
  </section>

  <section id="main">
    <h1>Sustainability Resource Planning</h1>
    <img src="images/icon/srp-icon-256x256.png"/>
    <p>For more information see <a href="//srp.digital">srp.digital</a></p>
  </section>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</body>
</html>