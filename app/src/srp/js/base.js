/*******************************************************************************
 * Copyright 2015, 2017 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
var EASING_DURATION = 500;
fadeOutMessages = true;
var newLineRegEx = /\n/g;


/**
 * Extends Ractive to handle offers some standard controls and a re-branding mechanism.
 */
var BaseRactive = Ractive.extend({
  CSRF_TOKEN: 'XSRF-TOKEN',
  addDataList: function(d, data) {
    $('datalist#'+d.name).remove();
    $('body').append('<datalist id="'+d.name+'">');
    if (data == null) {
      console.error('No data for datalist: '+d.name+', please fix configuration');
    } else {
      $.each(data, function (i,e) {
        $('datalist#'+d.name).append('<option value="'+e.name+'">'+e.name+'</option>');
      });
    }
  },
  applyAccessControl: function() {
    console.info('applyAccessControl');
    if (location.href.indexOf('public')==-1 && ractive.get('tenant.access.readonly')) {
      fadeOutMessages=false;
      ractive.showMessage('System is in read-only mode', 'alert-warning');
      $('input,select,textarea').attr('readonly','readonly').attr('disabled','disabled');
    }
  },
  applyBranding: function() {
    if (ractive.get('tenant')==undefined) return ;
    var tenant = ractive.get('tenant.id');
    if (tenant != undefined) {
      $('head').append('<link href="'+ractive.getServer()+'/css/'+tenant+'-1.0.0.css" rel="stylesheet">');
      if (ractive.get('tenant.theme.logoUrl')!=undefined) {
        $('.navbar-brand').empty().append('<img src="'+ractive.get('tenant.theme.logoUrl')+'" alt="logo"/>');
      }
      if (ractive.get('tenant.theme.iconUrl')!=undefined) {
          $('link[rel="icon"]').attr('href',ractive.get('tenant.theme.iconUrl'));
      }
      ractive.initContentEditable();// required here for the tenant switcher
      // tenant partial templates
      $.each(ractive.get('tenant').partials, function(i,d) {
        $.get(d.url, function(response){
//          console.log('partial '+d.url+' response: '+response);
          try {
          ractive.resetPartial(d.name,response);
          } catch (e) {
            console.error('Unable to reset partial '+d.name+': '+e);
          }
        });
      });
      ractive.applyAccessControl();
      if (ractive.brandingCallbacks!=undefined) ractive.brandingCallbacks.fire();
    }
  },
  entityName: function(entity) {
    console.info('entityName');
    var id = ractive.uri(entity);
    var lastSlash = id.lastIndexOf('/');
    return id.substring(id.lastIndexOf('/', lastSlash-1)+1, lastSlash);
  },
  fetchConfig: function() {
    console.info('fetchConfig');
    $.getJSON('/configuration', function(data) {
      ractive.set('server',data.clientContext);
    });
  },
  fetchDocs: function() {
    $.getJSON(ractive.uri(ractive.get('current'))+'/documents',  function( data ) {
      if (data['_embedded'] != undefined) {
        console.log('found docs '+data);
        ractive.merge('current.documents', data['_embedded'].documents);
        // sort most recent first
        ractive.get('current.documents').sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
      }
      ractive.set('saveObserver',true);
    });
  },
  fetchNotes: function() {
    $.getJSON(ractive.uri(ractive.get('current'))+'/notes',  function( data ) {
      if (data['_embedded'] != undefined) {
        console.log('found notes '+data);
        ractive.merge('current.notes', data['_embedded'].notes);
        // sort most recent first
        ractive.get('current.notes').sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
      }
    });
  },
  getCookie: function(name) {
    //console.log('getCookie: '+name)
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
  },
  getProfile: function() {
    if ($auth.loginInProgress) {
      console.info('skip fetch profile while logging in');
      return;
    }
    var username = $auth.getClaim('sub');
    console.log('getProfile: '+username);
    if (username) {
      $.getJSON(ractive.getServer()+'/users/'+username, function(profile) {
          ractive.set('saveObserver', false);
          ractive.set('profile',profile);
          $('.profile-img').empty().append('<img class="img-rounded" src="//www.gravatar.com/avatar/'+ractive.hash(ractive.get('profile.email'))+'?s=34" title="'+ractive.get('profile.email')+'"/>');
          if (ractive.hasRole('super_admin')) $('.super-admin').show();
          ractive.loadTenantConfig(ractive.get('profile.tenant'));
          ractive.set('saveObserver', true);
        });
      } else if (ractive.get('tenant') && $auth.isPublic(window.location.href)) {
        var tenant = ractive.get('tenant.id');
        console.warn('... page supplied default tenant:'+tenant);
        ractive.loadTenantConfig(ractive.get('tenant.id'));
      } else {
        $auth.showLogin();
      }
  },
  getServer: function() {
    return ractive.get('server')==undefined ? '' : ractive.get('server');
  },
  hash: function(email) {
    if (email==undefined) return;
    return hex_md5(email.trim().toLowerCase());
  },
  hasRole: function(role) {
    var ractive = this;
    if (this && this.get('profile')) {
      var hasRole;
      if (ractive.get('profile.groups')!= undefined) {
        hasRole = ractive.get('profile.groups').filter(function(g) {return g.id==role});
      }
      return hasRole!=undefined && hasRole.length>0;
    }
    return false;
  },
  hideLogin: function() {
    $('#loginSect').slideUp();
  },
  hideMessage: function() {
    $('#messages, .messages').hide();
  },
  hideUpload: function () {
    console.log('hideUpload...');
    $('#upload').slideUp();
  },
  id: function(entity) {
    console.log('id: '+entity);
    var id = ractive.uri(entity);
    return id.substring(id.lastIndexOf('/')+1);
  },
  initAbout: function() {
    $('.powered-by-icon').empty().append('<img src="/srp/images/icon/srp-icon-32x32.png" alt="Sustainable Resource Planning™">');
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
    $.each(ractive.get('tenant.typeaheadControls'), function(i,d) {
      //console.log('binding ' +d.url+' to typeahead control: '+d.selector);
      if (d.url==undefined) {
        ractive.addDataList(d,d.values);
      } else {
        var url = d.url;
        if (url.indexOf('//')==-1) url = ractive.getServer()+url;
        $.get(url, function(data){
          ractive.addDataList(d,data);
        },'json');
      }
    });
  },
  initAutoNumeric: function() {
    if ($('.autoNumeric')!=undefined && $('.autoNumeric').length>0) {
      $('.autoNumeric').autoNumeric('init', {});
    }
  },
  initContentEditable: function() {
    console.log('initContentEditable');
    $("[contenteditable]").focus(function() {
      console.log('click '+this.id);
      selectElementContents(this);
    });
  },
  initControls: function() {
    console.log('initControls');
    ractive.initAbout();
    ractive.initAutoComplete();
    ractive.initAutoNumeric();
    ractive.initDatepicker();
    ractive.initContentEditable();
    ractive.initHelp();
  },
  initDatepicker: function() {
    console.log('initDatepicker');
    if ($('.datepicker')!=undefined && $('.datepicker').length>0) {
      $('.datepicker').datepicker({
        format: "dd/mm/yyyy",
        autoclose: true,
        todayHighlight: true
      });
    }
  },
  initHelp: function() {
    $( "body" ).keypress(function( event ) {
      switch (event.which) { // ref http://keycode.info/
      case 47: // forward slash on my mac
      case 191: // forward slash (allegedly)
          $('.search').focus();
          event.preventDefault();
          break;
      case 63: // ?
         $('#helpModal').modal({});
         event.preventDefault();
         break;
      default:
        //console.log("No Handler for .keypress() called with: "+event.which);
      }
    });
  },
  initTags: function() {
    console.info('initTags');
    $('[data-bind]').each(function(i,d) {
      $(d).val(ractive.get($(d).data('bind'))).css('display','none');
    });

    if ($(".tag-ctrl").is(":ui-tagit")) $(".tag-ctrl").tagit('destroy');
    $(".tag-ctrl").tagit({
      allowSpaces: true,
      placeholderText: "Comma separated tags",
      showAutocompleteOnFocus: true,
      afterTagAdded: function(event, ui) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      },
      afterTagRemoved: function(event, ui) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      }
    });
  },
  loadStandardPartial: function(name,url) {
    //console.log('loading...: '+d.name)
      $.get(url, function(response){
        //console.log('... loaded: '+d.name)
        //console.log('response: '+response)
        if (ractive != undefined) {
          try {
            ractive.set('saveObserver',false);
            ractive.resetPartial(name,response);
            ractive.set('saveObserver',true);
          } catch (e) {
            console.warn('Unable to reset partial '+name+': '+e);
          }
        }
      });
    },
  loadStandardPartials: function(stdPartials) {
    console.info('loadStandardPartials');
    if (ractive == undefined || stdPartials == undefined) return;
    $.each(stdPartials, function(i,d) {
      ractive.loadStandardPartial(d.name, d.url);
    });
  },
  loadTenantConfig: function(tenant) {
    if ($auth.loginInProgress) {
      console.info('skip tenant load while logging in');
      return;
    }
    if (tenant==undefined || tenant=='undefined') $auth.showLogin();
    console.info('loadTenantConfig:'+tenant);
    $.getJSON(ractive.getServer()+'/tenants/'+tenant+'.json', function(response) {
      //console.log('... response: '+JSON.stringify(response));
      ractive.set('saveObserver', false);
      ractive.set('tenant', response);
      ractive.applyBranding();
      ractive.initAutoComplete();
      ractive.set('saveObserver', true);
      if (ractive.tenantCallbacks!=undefined) ractive.tenantCallbacks.fire();
    });
  },
  logout: function() {
    $auth.logout();
  },
  parseDate: function(timeString) {
    var d = new Date(timeString);
    // IE strikes again
    if (d == 'Invalid Date') d = parseDateIEPolyFill(timeString);
    return d;
  },
  saveDoc: function () {
    console.log('saveDoc '+JSON.stringify(ractive.get('current.doc'))+' ...');
    var n = ractive.get('current.doc');
    n.name = $('#docName').val();
    n.url = $('#doc').val();
    var url = ractive.uri(ractive.get('current'))+'/documents';
    url = url.replace(ractive.entityName(ractive.get('current')),ractive.get('tenant.id')+'/'+ractive.entityName(ractive.get('current')));
    if (n.url.trim().length > 0) {
      $('#docsTable tr:nth-child(1)').slideUp();
      $.ajax({
        /*url: '/documents',
        contentType: 'application/json',*/
        url: url,
        type: 'POST',
        data: n,
        success: completeHandler = function(data) {
          console.log('data: '+ data);
          ractive.showMessage('Document link saved successfully');
          ractive.fetchDocs();
          $('#doc').val(undefined);
        }
      });
    }
  },
  saveNote: function () {
    console.info('saveNote '+JSON.stringify(ractive.get('current.note'))+' ...');
    var n = ractive.get('current.note');
    n.content = $('#note').val();
    var url = ractive.uri(ractive.get('current'))+'/notes';
    url = url.replace(ractive.entityName(ractive.get('current')),ractive.get('tenant.id')+'/'+ractive.entityName(ractive.get('current')));
    console.log('  url:'+url);
    if (n.content.trim().length > 0) {
      $('#notesTable tr:nth-child(1)').slideUp();
      $.ajax({
        /*url: '/notes',
        contentType: 'application/json',*/
        url: url,
        type: 'POST',
        data: n,
        success: completeHandler = function(data) {
          console.log('response: '+ data);
          ractive.showMessage('Note saved successfully');
          ractive.fetchNotes();
          $('#note').val(undefined);
        }
      });
    }
  },
  showDisconnected: function(msg) {
    console.log('showDisconnected: '+msg);
    if ($('#connectivityMessages.alert-info').length>0) {
      ; // Due to ordering of methods, actually reconnected now
    } else {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-warning">'+msg+'</div>').show();
    }
  },
  showError: function(msg) {
    this.showMessage(msg, 'alert-danger');
  },
  showFormError: function(formId, msg) {
    this.showError(msg);
    var selector = formId==undefined || formId=='' ? ':invalid' : '#'+formId+' :invalid';
    $(selector).addClass('field-error');
    $(selector)[0].focus();
  },
  showHelp: function() {
    console.info('showHelp');
    $('iframe.helpContent')
        .attr('src',ractive.get('helpUrl'))
        .prop('height', window.innerHeight*0.8);
    $('#helpModal').modal({});
  },
  showLogin: function() {
    console.info('showLogin');
    $('#loginSect').slideDown();
  },
  showMessage: function(msg, additionalClass) {
    console.log('showMessage: '+msg);
    if (additionalClass == undefined) additionalClass = 'bg-info text-info';
    if (msg === undefined) msg = 'Working...';
    $('#messages').empty().append(msg).removeClass().addClass(additionalClass).show();
//    document.getElementById('messages').scrollIntoView();
    if (fadeOutMessages && additionalClass!='bg-danger text-danger') setTimeout(function() {
      $('#messages').fadeOut();
    }, EASING_DURATION*10);
    else $('#messages, .messages').append('<span class="text-danger pull-right glyphicon icon-btn kp-icon-remove" onclick="ractive.hideMessage()"></span>');
  },
  showReconnected: function() {
    console.log('showReconnected');
    $( "#ajax-loader" ).hide();
    if ($('#connectivityMessages:visible').length>0) {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-info">Reconnected</div>').show();
      setTimeout(function() {
        $('#connectivityMessages').fadeOut();
      }, EASING_DURATION*10);
    }
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  showWarning: function(msg) {
    this.showMessage(msg, 'alert-warning');
  },
  stripProjection: function(link) {
    if (link==undefined) return;
    var idx = link.indexOf('{projection');
    if (idx==-1) {
      idx = link.indexOf('{?projection');
      if (idx==-1) {
        return link;
      } else {
        return link.substring(0,idx);
      }
    } else {
      return link.substring(0,idx);
    }
  },
  switchToTenant: function() {
    var tenant = $('select.tenant').val();
    if (tenant==undefined || typeof tenant != 'string') {
      return false;
    }
    console.log('switchToTenant: '+tenant);
    $.ajax({
      method: 'PUT',
      url: ractive.getServer()+"/admin/tenant/"+$auth.getClaim('sub')+'/'+tenant,
      success: function() {
        window.location.reload();
      }
    })
  },
  tenantUri: function(entity) {
    console.log('tenantUri: '+entity);
    var uri = ractive.uri(entity);
    if (uri != undefined && uri.indexOf(ractive.get('tenant.id'))==-1) {
      uri = uri.replace(ractive.get('entityPath'),'/'+ractive.get('tenant.id')+ractive.get('entityPath'));
    }
    // if (uri != undefined && ractive.get('context')!=undefined) {
    //   uri = uri.replace('/'+ractive.get('tenant.id'),ractive.get('context')+'/'+ractive.get('tenant.id'));
    // }
    return uri;
  },
  toCsv: function(json, title, headings) {
    //If json is not an object then JSON.parse will parse the JSON string in an Object
    var arr = typeof json != 'object' ? JSON.parse(json) : json;

    var csv = '';

    // write title on first row
    csv += title + '\n\n';

    if (headings === undefined || headings == true) {
      // extract label from json fields in array idx 0
      var row = '';

      for (var idx in arr[0]) {
          row += idx + ',';
      }

      row = row.slice(0, -1); // strip trailing comma
      headings = row + '\n';
    }
    csv += headings + '\n';

    var propNames = headings.split(',');
    for (var i = 0; i < arr.length; i++) {
        var row = '';

        for (var j = 0 ; j < propNames.length ; j++) {
          try {
            var val = eval('arr['+i+'].'+propNames[j]);
            row += '"' + (val == undefined ? '' : val) + '",';
          } catch (err) {
            console.error('Fail to extract property '+propNames[j]+'from row '+i);
          }
        }

        row = row.slice(0, -1); // strip trailing comma
        csv += row + '\n';
    }

    if (csv == '') {
        alert("Invalid data");
        return;
    }

    //Generate a file name (<title with _ for spaces>-<timestamp>)
    var fileName = title.replace(/ /g,"_")+new Date().toISOString().substring(0,16).replace(/[T:]/g,'-');

    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(csv);

    // Now the little tricky part.
    // you can use either>> window.open(uri);
    // but this will not work in some browsers
    // or you will not get the correct file extension

    //this trick will generate a temp <a /> tag
    var link = document.createElement("a");
    link.href = uri;

    //set the visibility hidden so it will not effect on your web-layout
    link.style = "visibility:hidden";
    link.download = fileName + ".csv";

    //this part will append the anchor tag and remove it after automatic click
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  },
  toggleSection: function(sect) {
    console.info('toggleSection: '+$(sect).attr('id'));
    $('#'+$(sect).attr('id')+'>div').toggle();
    $('#'+$(sect).attr('id')+' .ol-collapse').toggleClass('kp-icon-caret-right').toggleClass('kp-icon-caret-down');
  },
  toggleSidebar: function() {
    console.info('toggleSidebar');
    $('.toolbar-left').toggle(EASING_DURATION);
  },
  upload: function(formId) {
    console.log('upload, id: '+formId);
    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: formElement.action,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
          console.log('successfully uploaded resource');
          ractive.fetch();
          ractive.hideUpload();
        }
    });
  },
  uri: function(entity) {
    // TODO switch to use modularized version
    //console.log('uri: '+entity);
    var saveObserver = ractive.get('saveObserver');
    ractive.set('saveObserver', false);
    var uri;
    if (entity['links']!=undefined) {
      $.each(entity.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (entity['_links']!=undefined) {
      uri = ractive.stripProjection(entity._links.self.href);
    } else if (entity['id']!=undefined) {
      uri = ractive.get('entityPath')+'/'+entity.id;
    }
    // work around for sub-dir running
    if (uri != undefined && uri.indexOf(ractive.getServer())==-1 && uri.indexOf('//')!=-1) {
      uri = ractive.getServer() + uri.substring(uri.indexOf('/', uri.indexOf('//')+2));
    } else if (uri != undefined && uri.indexOf('//')==-1) {
      uri = ractive.getServer()+uri;
    }

    ractive.set('saveObserver', saveObserver);
    return uri;
  }
});

//$( document ).ajaxSuccess(function( event, request, settings ) {
//  if (settings['retryIn']!=undefined) ractive.showReconnected();
//});

$( document ).bind('keypress', function(e) {
  switch (e.keyCode) {
  case 13: // Enter key
    if (window['ractive'] && ractive['enter']) ractive['enter']();
    break;
  case 63:   // ? key
    console.log('help requested');
    $('#helpModal').modal({});
    break;
  }
});

$(document).ready(function() {
  ractive.set('saveObserver', false);
  //ractive.set('context','/srp');
  if (document.location.href.indexOf('https://srp.digital/srp')!=-1) {
    (function() {
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-39960834-3', 'auto');
      ga('send', 'pageview');
    })();
  }

  // ajax loader
  if ($('#ajax-loader').length==0) $('body').append('<div id="ajax-loader"><img class="ajax-loader" src="'+ractive.getServer()+'/images/ajax-loader.svg" alt="Loading..."/></div>');
  $( document ).ajaxStart(function() {
    $( "#ajax-loader" ).show();
  });
  $( document ).ajaxStop(function() {
    $( "#ajax-loader" ).hide();
  });

  ractive.loadStandardPartials(ractive.get('stdPartials'));
  if (window['$auth'] != undefined) $auth.addLoginCallback(ractive.getProfile);

  $( document ).ajaxComplete(function( event, jqXHR, ajaxOptions ) {
    if (jqXHR.status > 0) ractive.showReconnected();
  });

  ractive.observe('tenant', function(newValue, oldValue, keypath) {
    console.log('tenant changed');
    if ((oldValue == undefined || oldValue.id == '') && newValue != undefined && newValue.id != '' && ractive['fetch'] != undefined) {
      ractive.fetch();
    }
  });

  ractive.on( 'sort', function ( event, column ) {
    console.info('sort on '+column);
    // if already sorted by this column reverse order
    if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
    this.set( 'sortColumn', column );
  });

  ractive.observe('title', function(newValue, oldValue, keypath) {
    console.log('title changing from '+oldValue+' to '+newValue);
    if (newValue!=undefined && newValue!='') {
      $('title').empty().append(newValue);
    }
  });

  ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
    console.log('searchTerm changed');
    if (typeof ractive['showResults'] == 'function') ractive.showResults();
    setTimeout(ractive.showSearchMatched, 1000);
  });

  var params = getSearchParameters();
  if (params['searchTerm']!=undefined) {
    ractive.set('searchTerm',decodeURIComponent(params['searchTerm']));
  } else if (params['q']!=undefined) {
    ractive.set('searchTerm',decodeURIComponent(params['q']));
  }
  var i18n = new I18nController($env.server+'/workmgmt/3.0.0');

  ractive.set('saveObserver', true);
});

// TODO remove the redundancy of having this in base Ractive and here
function getCookie(name) {
  //console.log('getCookie: '+name)
  var value = "; " + document.cookie;
  var parts = value.split("; " + name + "=");
  if (parts.length == 2) return parts.pop().split(";").shift();
}

function selectElementContents(el) {
  var range = document.createRange();
  range.selectNodeContents(el);
  var sel = window.getSelection();
  sel.removeAllRanges();
  sel.addRange(range);
}

function getSearchParameters() {
  var prmstr = window.location.search.substr(1);
  return prmstr != null && prmstr != "" ? transformToAssocArray(prmstr) : {};
}

function transformToAssocArray( prmstr ) {
  var params = {};
  var prmarr = prmstr.split("&");
  for ( var i = 0; i < prmarr.length; i++) {
      var tmparr = prmarr[i].split("=");
      params[tmparr[0]] = tmparr[1];
  }
  return params;
}

/* Object extensions */

Array.prototype.clean = function(deleteValue) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == deleteValue) {
      this.splice(i, 1);
      i--;
    }
  }
  return this;
};

/**
 * @return The first array element whose 'k' field equals 'v'.
 */
Array.findBy = function(k,v,arr) {
  for (idx in arr) {
    if (arr[idx][k]==v) return arr[idx];
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return arr[idx];
  }
}
/**
 * @return All  array elements whose 'k' field equals 'v'.
 */
Array.findAll = function(k,v,arr) {
  var retArr = [];
  for (idx in arr) {
    if (arr[idx][k]==v) retArr.push(arr[idx]);
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return retArr.push(arr[idx]);
  }
  return retArr;
}
Array.uniq = function(fieldName, arr) {
  // console.info('uniq');
  list = '';
  for (idx in arr) {
    if (idx(arr[idx],fieldName) != undefined
        && list.indexOf(idx(arr[idx],fieldName)) == -1) {
      if (list != '')
        list += ','
      list += idx(arr[idx],fieldName);
    }
  }
  return list;
}
Array.prototype.uniq = function() {
  return this.sort().filter(function(el,i,a){if(i==a.indexOf(el))return 1;return 0});
}
/******************************** Polyfills **********************************/
// ref https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/String/endsWith
if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(searchString, position) {
      var subjectString = this.toString();
      if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
        position = subjectString.length;
      }
      position -= searchString.length;
      var lastIdx = subjectString.indexOf(searchString, position);
      return lastIdx !== -1 && lastIdx === position;
  };
}

function parseDateIEPolyFill(timeString) {
  var start = timeString.substring(0,timeString.indexOf('.'));
  var offset;
  if (timeString.indexOf('-',timeString.indexOf('T'))!=-1) {
    offset = timeString.substr(timeString.indexOf('-',timeString.indexOf('T')),3)+':'+timeString.substr(timeString.indexOf('-',timeString.indexOf('T'))+3,2);
  } else if (timeString.indexOf('+')!=-1) {
    offset = timeString.substr(timeString.indexOf('+'),3)+':'+timeString.substr(timeString.indexOf('+')+3,2);
  }
  return new Date(Date.parse(start+offset));
}
