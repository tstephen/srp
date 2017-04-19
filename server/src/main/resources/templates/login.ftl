<!DOCTYPE html>
<html lang="en">
<head>
  <title>Sustainable Resource Planning</title>
  <link href="/webjars/bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
  
  <link href="/2.0.0/css/srp.css" rel="stylesheet">
  <link rel="icon" type="image/png" href="/images/icon/srp-icon-32x32.png" />
    <style>
    html,body,section#main {
      height:100%;
      text-align: center;
      width: 100%;
    }
    a,a:visited {
      color: #949699;
    }
    a:hover,a:active {
      color: #ec9300;
    }
    input {
      border-style: solid;
      border-width: 1px;
    }
    input#email {
      margin-right:2px;
      width: 400px;
    }
    section#main {
      margin: auto;
      padding-top: 45%;
    }
    @media (min-width: 480px) {
      section#main {
        padding-top: 30%;
      }
    }
    @media (min-width: 768px) {
      section#main {
        padding-top: 20%;
      }
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
      position: fixed;
      top: 5px;
      right: 20px;
    }
    #container div {
      position: relative;
      margin-top: 5px;
      }
      #container form {
      text-align: right;
    }
    #container #messages {
      margin: 0 auto;
      width: 100%;
      /*width: 531px;*/
      top: -5px;
    }
    .btn {
      height: 45px;
    }

    @media (max-width: 480px) {
      section#main {
        padding-top: 45%;
      }
    }
    @media (min-width: 481px) {
      section#main {
        padding-top: 30%;
      }
    }
    @media (min-width: 769px) {
      section#main {
        padding-top: 10%;
      }
    }
  </style>
<head>
<body>

  <section id="container" style="width:100%"></section>

  <section id="main">
    <h1>Sustainability Resource Planning</h1>
    <img src="images/icon/srp-icon-256x256.png"/>
    <p>For more information see <a href="//srp.digital">srp.digital</a></p>
  </section>


  <script id='template' type='text/ractive'>
    <div class="col-md-offset-1 col-md-10">
      <div id="messages" style="display:none;width:100%"></div>
    </div>
    <div class="col-md-offset-6 col-md-6 col-sm-12" id="loginSect">
      <form class="form-inline" id="loginForm" name="loginForm" action="{{context}}/auth/login" method="POST">
        <fieldset>
          <input class="form-control input-lg" type="text" id="username" name="username" placeholder="Username" required/>
          <input class="form-control input-lg" type="password" id="password" name="password" placeholder="Password" required/>
          <input type="hidden" id="_csrf" name="_csrf" value="{{csrfToken}}" />
          <input type="hidden" id="redirect" name="redirect" value="{{context}}/index.html" />
          <input type="button" id="login" onclick="ractive.login()" value="Login" class="btn btn-primary" />
        </fieldset>
      </form>
    </div>
    <div id="resetSect" class="col-md-offset-6 col-md-6 col-sm-12" style="display:none">
      <form id="resetForm" class="form-inline" name="resetForm" action="/reset" method="POST">
        <fieldset>
          <input class="form-control input-lg" id="email" name="email" placeholder="Registered email address" required/>
          <input type="hidden" id="_csrf" name="_csrf" value="{{csrfToken}}" />
          <input type="hidden" id="redirect" name="redirect" value="index.html" />
          <input class="btn" type="button" id="reset" on-click="reset()" value="Reset Password"/>
        </fieldset>
      </form>
    </div>
    <div class="pull-right" style="margin-right:20px">
      <a href="#" on-click="showReset()">Forgot your password?</a>
      <a href="http://trakeo.com/register/">Or click here to register a new account</a>
    </div>
  </script>

  <script src="/webjars/jquery/1.11.1/jquery.min.js"></script>
  <script src="/webjars/bootstrap/3.3.5/js/bootstrap.min.js"></script>
  <script src="/webjars/Bootstrap-3-Typeahead/3.1.1/bootstrap3-typeahead.js"></script>
  <script src="/webjars/ractive/0.7.3/ractive.min.js"></script>
  
  <script src="/js/md5.min.js"></script>
  <script src="/2.0.0/js/base.js"></script>
  <script src="/2.0.0/js/login-ui.js"></script>
  <script src="/js/omny-1.0.0.js"></script>
</body>

</html>
