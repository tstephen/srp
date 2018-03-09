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
fadeOutparameters = true;
var newLineRegEx = /\n/g;

var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    parameters: [],
    filter: undefined,
    //saveObserver:false,
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    chars: function(string) {
      console.info('chars: '+string);
      var len = string == undefined ? 0 : string.length;
      console.log('  returning: '+len);
      return len;
    },
    customField: function(obj, name) {
      if (obj['customFields']==undefined) {
        return undefined;
      } else if (!Array.isArray(obj['customFields'])) {
        return obj.customFields[name];
      } else {
        //console.error('customField 30');
        var val;
        $.each(obj['customFields'], function(i,d) {
          if (d.name == name) val = d.value;
        });
        return val;
      }
    },
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages).replace('Invalid Date','n/a').replace('01/01/1970','n/a');
    },
    formatJson: function(json) {
      console.log('formatJson: '+json);
      try {
        var obj = JSON.parse(json);
        var html = '';
        $.each(Object.keys(obj), function(i,d) {
          html += (typeof obj[d] == 'object' ? '' : '<b>'+d+'</b>: '+obj[d]+'<br/>');
        });
        return html;
      } catch (e) {
        // So it wasn't JSON
        return json;
      }
    },
    hash: function(email) {
      if (email == undefined) return '';
      console.log('hash '+email+' = '+ractive.hash(email));
      return '<img class="img-rounded" src="//www.gravatar.com/avatar/'+ractive.hash(email)+'?s=36"/>'
    },
    matchFilter: function(obj) {
      if (ractive.get('filter')==undefined) return true;
      else return ractive.get('filter').value.toLowerCase()==obj[ractive.get('filter').field].toLowerCase();
    },
    matchRole: function(role) {
      // console.info('matchRole: ' + role)
      if (role == undefined || ractive.hasRole(role)) {
        $('.' + role).show();
        return true;
      } else {
        return false;
      }
    },
    matchSearch: function(obj) {
      var searchTerm = ractive.get('searchTerm');
      //console.info('matchSearch: '+searchTerm);
      if (searchTerm==undefined || searchTerm.length==0) {
        return true;
      } else {
        return (obj.name.toLowerCase().indexOf(searchTerm.toLowerCase()))>=0;
      }
    },
    saveObserver: false,
    server: $env.server,
    sort: function (array, column, asc) {
      console.info('sort '+(asc ? 'ascending' : 'descending')+' on: '+column);
      array = array.slice(); // clone, so we don't modify the underlying data

      return array.sort( function ( a, b ) {
        if (b[column]==undefined || b[column]==null || b[column]=='') {
          return (a[column]==undefined || a[column]==null || a[column]=='') ? 0 : -1;
        } else if (asc) {
          return a[ column ] < b[ column ] ? -1 : 1;
        } else {
          return a[ column ] > b[ column ] ? -1 : 1;
        }
      });
    },
    sortAsc: true,
    sortColumn: 'name',
    sorted: function(column) {
//      console.info('sorted:'+column);
      if (ractive.get('sortColumn') == column && ractive.get('sortAsc')) return 'sort-asc';
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      { "name": "loginSect", "url": $env.server+"/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "navbar", "url": "./vsn/partials/parameter-navbar.html"},
      { "name": "profileArea", "url": "./vsn/partials/profile-area.html"},
      { "name": "sidebar", "url": "./vsn/partials/sidebar.html"},
      { "name": "titleArea", "url": "./vsn/partials/title-area.html"},
      { "name": "parameterListSect", "url": "./vsn/partials/parameter-list-sect.html"},
      { "name": "parameterCurrentSect", "url": "./vsn/partials/parameter-current-sect.html"}
    ],
    title: "Parameters"
  },
  partials: {
    'loginSect':'',
    'parameterCurrentSect':'',
    'parameterListSect':'',
    'profileArea':'',
    'sidebar':'',
    'titleArea':''
  },
  add: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var parameterOrgType = { status:'Green', tenantId: ractive.get('tenant.id') };
    ractive.select(parameterOrgType);
  },
  download: function() {
    console.info('download');
    $.ajax({
      headers: {
        "Accept": "text/csv; charset=utf-8",
        "Content-Type": "text/csv; charset=utf-8"
      },
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/parameters/',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        something = window.open("data:text/csv," + encodeURIComponent(data),"_blank");
        //something.focus();
      }
    });
  },
  edit: function (parameter) {
    console.log('edit'+parameter+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(parameter);
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    var url = obj.links != undefined
        ? obj.links.filter(function(d) { console.log('this:'+d);if (d['rel']=='self') return d;})[0].href
        : obj._links.self.href;
    $.ajax({
        url: url,
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.fetch();
          ractive.toggleResults();
        }
    });
    return false; // cancel bubbling to prevent edit as well as delete
  },
  fetch: function () {
    console.log('fetch...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/parameters/',
      crossDomain: true,
      success: function( data ) {
        if (data['_embedded'] == undefined) {
          ractive.merge('parameters', data);
        }else{
          ractive.merge('parameters', data['_embedded'].parameters);
        }
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#parametersTable tbody tr:visible').length);
        ractive.set('saveObserver', true);
      }
    });
  },
  filter: function(field,value) {
    console.log('filter: field '+field+' = '+value);
    if (value==undefined) value = ractive.get('tenant.stagesInActive');
    if (field==undefined) ractive.set('filter',undefined);
    else ractive.set('filter',{field: field,value: value});
    ractive.set('searchMatched',$('#parametersTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  find: function(parameterId) {
    console.log('find: '+parameterId);
    var c;
    $.each(ractive.get('parameters'), function(i,d) {
      if (parameterId.endsWith(ractive.getId(d))) {
        c = d;
      }
    });
    return c;
  },
  findUri: function(arr,name) {
    $.each(arr, function(i,d) {
      if (d.name == name) return ractive.getId(d);
    });
    return null;
  },
  getId: function(parameter) {
    console.log('getId: '+parameter);
    var uri;
    if (parameter['links']!=undefined) {
      $.each(parameter.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (parameter['_links']!=undefined) {
      uri = parameter._links.self.href.indexOf('?')==-1 ? parameter._links.self.href : parameter._links.self.href.substr(0,parameter._links.self.href.indexOf('?')-1);
    }
    return uri;
  },
  save: function () {
    console.log('save parameter: '+ractive.get('current').name+'...');
    ractive.set('saveObserver',false);
    var id = ractive.uri(ractive.get('current'));
    console.log('  id: '+id+', so will '+(id === undefined ? 'POST' : 'PUT'));
    ractive.set('saveObserver',true);
    if (document.getElementById('currentForm')==undefined) {
      // loading... ignore
    } else if(document.getElementById('currentForm').checkValidity()) {
      ractive.set('current.tenantId', ractive.get('tenant.id'));
      $.ajax({
        url: id === undefined ? ractive.getServer()+'/parameters/' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(ractive.get('current')),
        success: completeHandler = function(data, textStatus, jqXHR) {
          console.log(jqXHR.status+': '+JSON.stringify(data));
          var location = jqXHR.getResponseHeader('Location');
          ractive.set('saveObserver',false);
          if (location != undefined) ractive.set('current._links.self.href',location);
          if (jqXHR.status == 201) {
            ractive.set('currentIdx',ractive.get('parameters').push(ractive.get('current'))-1);
          }
          if (jqXHR.status == 204) ractive.splice('parameters',ractive.get('currentIdx'),1,ractive.get('current'));

          ractive.showMessage('Parameter saved');
          ractive.set('saveObserver',true);
        }
      });
    } else {
      console.warn('Cannot save yet as parameter is invalid');
      $('#currentForm :invalid').addClass('field-error');
      ractive.showMessage('Cannot save yet as parameter is incomplete');
    }
  },
  select: function(parameter) {
    console.log('select: '+JSON.stringify(parameter));
    ractive.set('saveObserver',false);
	  // adapt between Spring Hateos and Spring Data Rest
	  if (parameter['_links'] == undefined && parameter.links != undefined) {
	    parameter._links = parameter.links;
	    $.each(parameter.links, function(i,d) {
        if (d.rel == 'self') parameter._links.self = { href:d.href };
      });
	  }
	  if (parameter._links != undefined) {
	    var url = parameter._links.self.href.indexOf('?')==-1 ? parameter._links.self.href : parameter._links.self.href.substr(0,parameter._links.self.href.indexOf('?')-1);
	    console.log('loading detail for '+url);
	    $.getJSON(ractive.getServer()+url,  function( data ) {
        console.log('found parameter '+data);
        ractive.set('current', data);
        ractive.initControls();
        // who knows why this is needed, but it is, at least for first time rendering
        $('.autoNumeric').autoNumeric('update',{});
        ractive.set('saveObserver',true);
      });
    } else {
      console.log('Skipping load as no _links.'+parameter.name);
      ractive.set('current', parameter);
      ractive.set('saveObserver',true);
    }
	  ractive.toggleResults();
	  $('#currentSect').slideDown();
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
  },
  showResults: function() {
    $('#parametersTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#parametersTable').slideDown({ queue: true });
  },
  sortparameters: function() {
    ractive.get('parameters').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  toggleResults: function() {
    console.log('toggleResults');
    $('#parametersTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#parametersTable').slideToggle();
  }
});

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#parametersTable tbody tr').length);
  }, 500);
});

$(document).ready(function() {
  console.info('ready handler')
//  if (ractive.fetchCallbacks==null) ractive.fetchCallbacks = $.Callbacks();
});

// Save on model change
// done this way rather than with on-* attributes because autocomplete
// controls done that way save the oldValue
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  ignored=[];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save();
  } else {
    console.warn  ('Skipped save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});
ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});
