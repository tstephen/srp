var EASING_DURATION = 500;
fadeOutMessages = true;
var newLineRegEx = /\n/g;

var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    server: $env.server,
    title: $env.appName,
    tagLine: $env.tagLine,
    username: localStorage['username'],
    featureEnabled: function(feature) {
      console.log('featureEnabled: '+feature);
      if (feature==undefined || feature.length==0) return true;
      else return ractive.get('tenant.show.'+feature);
    },
    gravatar: function(email) {
      if (email == undefined) return '';
      return '<img class="img-rounded" src="//www.gravatar.com/avatar/'+ractive.hash(email)+'?s=36&d=https%3A%2F%2Fapi.knowprocess.com%2F'+ractive.get('tenant.id')+'%2Fgravatars%2F'+ractive.hash(email)+'.png"/>'
    },
    hash: function(email) {
      if (email == undefined) return '';
      return ractive.hash(email);
    },
    helpUrl: '//omny.link/user-help/#the_title',
    matchRole: function(role) {
      console.info('matchRole: '+role)
      if (role==undefined || ractive.hasRole(role)) {
        $('.'+role).show();
        return true;
      } else {
        return false;
      }
    },
    stdPartials: [
      { "name": "loginSect", "url": $env.server+"/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "profileArea", "url": $env.server+"/partials/profile-area.html"},
      { "name": "sidebar", "url": $env.server+"/partials/sidebar.html"},
      { "name": "toolbar", "url": $env.server+"/partials/toolbar.html"},
      { "name": "supportBar", "url": $env.server+"/webjars/supportservices/3.0.0/partials/support-bar.html"},
      { "name": "titleArea", "url": $env.server+"/partials/title-area.html"},
    ],
  },
  partials: {
    'helpModal': '',
    'loginSect': '',
    'profileArea': '',
    'sidebar': '',
    'supportBar': '',
    'titleArea': ''
  },
  fetch: function () {
    console.info('fetch...');
  },
  oninit: function() {
    console.log('oninit');
    this.loadStandardPartials(this.get('stdPartials'));
  },
  sendMessage: function(msg) {
    console.log('sendMessage: '+msg.name);
    var type = (msg['pattern'] == 'inOut' || msg['pattern'] == 'outOnly') ? 'GET' : 'POST';
    var d = (msg['pattern'] == 'inOut') ? {query:msg['body']} : {json:msg['body']};
    console.log('d: '+d);
    //var d['businessDescription']=ractive.get('message.bizKey');
    return $.ajax({
      url: '/msg/'+ractive.get('tenant.id')+'/'+msg.name+'/',
      type: type,
      data: d,
      dataType: 'text',
      success: completeHandler = function(data) {
        console.log('Message received:'+data);
        if (msg['callback']!=undefined) msg.callback(data);
      },
    });
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
  },
});

$(document).ready(function() {
  var statusCode = parseInt(getSearchParameters()['statusCode']);
  var msg = decodeURIComponent(getSearchParameters()['msg']);
  if (statusCode!=undefined && !isNaN(statusCode)) {
    switch (statusCode) {
    case 401:
      msg = "You're not authorised to see that page";
      break;
    case 404:
      msg = "We can't find that page";
      break;
    case 500:
      msg = '';
      break;
    default:
      console.warn('statusCode: '+statusCode);
    }
    ractive.set('tagLine', 'Ooops! Something went wrong... '+msg);
    ractive.set('intro', 'Please continue by clicking one of the icons below:');
  }
});
