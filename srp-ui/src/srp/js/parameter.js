/*******************************************************************************
 * Copyright 2015-2021 Tim Stephenson and contributors
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
var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    parameters: [],
    filter: undefined,
    //saveObserver:false,
    tenant: { id: 'sdu' },
    username: localStorage.getItem('username'),
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
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc';
      else return 'hidden';
    },
    stdPartials: [
      { "name": "navbar", "url": "./vsn/partials/parameter-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "parameterListSect", "url": "./vsn/partials/parameter-list-sect.html"},
      { "name": "parameterCurrentSect", "url": "./vsn/partials/parameter-current-sect.html"}
    ],
    title: "Parameters"
  },
  partials: {
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
        window.open("data:text/csv," + encodeURIComponent(data),"_blank");
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
    ractive.srp.deleteParameter(obj)
      .then(function(response) {
        switch (response.status) {
        case 204:
          ractive.fetch();
          ractive.toggleResults();
          break;
        default:
          ractive.showMessage('Unable to delete the parameter ('+response.status+')');
        }
      });
    return false; // cancel bubbling to prevent edit as well as delete
  },
  fetch: function() {
    console.log('fetch...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchParameters(ractive.get('tenant.id'))
    .then(function(response) { return response.json(); }) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(function(data) {
        if ('_embedded' in data) {
          ractive.merge('parameters', data._embedded.parameters);
        } else {
          ractive.merge('parameters', data);
        }
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#parametersTable tbody tr:visible').length);
        ractive.set('saveObserver', true);
      }
    );
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
    if ('links' in parameter) {
      $.each(parameter.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if ('_links' in parameter) {
      uri = parameter._links.self.href.indexOf('?')==-1 ? parameter._links.self.href : parameter._links.self.href.substr(0,parameter._links.self.href.indexOf('?')-1);
    }
    return uri;
  },
  postSelect: function(parameter) {
    ractive.set('saveObserver', false);
    console.log('found parameter '+parameter);
    ractive.set('current', parameter);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.hideResults();
    $('#currentSect').slideDown();
    ractive.set('saveObserver',true);
  },
  save: function() {
    console.log('save parameter: '+ractive.get('current').name+'...');
    ractive.set('saveObserver',false);
    var id = ractive.uri(ractive.get('current'));
    console.log('  id: '+id+', so will '+(id === undefined ? 'POST' : 'PUT'));
    ractive.set('saveObserver',true);
    if (document.getElementById('currentForm')==undefined) { /* loading... ignore */ }
    else if(document.getElementById('currentForm').checkValidity()) {
      ractive.set('current.tenantId', ractive.get('tenant.id'));
      ractive.srp.saveParameter(ractive.get('current'), ractive.get('tenant.id'))
      .then(function(response) {
        switch(response.status) {
        case 201:
          return ractive.set('currentIdx',ractive.get('parameters').push(ractive.get('current'))-1);
        }
      })
      .then(function(data) {
        if (data !== undefined) ractive.set('current', data);
        ractive.splice('parameters',ractive.get('currentIdx'),1,ractive.get('current'));
        ractive.set('saveObserver', true);
        $('.autoNumeric').autoNumeric('update');
        ractive.showMessage('Parameter saved');
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
    var url = ractive.uri(parameter);
    if (url == undefined) {
      console.log('Skipping load as no uri.');
      ractive.postSelect(parameter);
    } else {
      console.log('loading detail for '+url);
      ractive.srp.fetchParameter(url)
      .then(function(response) { return response.json(); })
      .then(function(data) { ractive.postSelect(data); });
    }
  },
  sortparameters: function() {
    ractive.get('parameters').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  }
});

// Save on model change
// done this way rather than with on-* attributes because autocomplete
// controls done that way save the oldValue
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  var ignored=[];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save();
  } else {
    console.warn  ('Skipped save of '+keypath);
  }
});
