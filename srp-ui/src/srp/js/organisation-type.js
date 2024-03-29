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
    entityName: 'organisationType',
    organisationTypes: [],
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
      { "name": "navbar", "url": "./vsn/partials/organisation-type-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "organisationTypeListSect", "url": "./vsn/partials/organisation-type-list-sect.html"},
      { "name": "organisationTypeCurrentSect", "url": "./vsn/partials/organisation-type-current-sect.html"}
    ],
    title: "Organisation Types"
  },
  partials: {
    'organisationTypeCurrentSect':'',
    'organisationTypeListSect':'',
    'profileArea':'',
    'sidebar':'',
    'titleArea':''
  }, 
  add: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var organisationType = { status:'Green', tenantId: ractive.get('tenant.id') };
    ractive.select(organisationType);
  },
  download: function() {
    console.info('download');
    $.ajax({
      headers: {
        "Accept": "text/csv; charset=utf-8",
        "Content-Type": "text/csv; charset=utf-8"
      },
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-types/',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        window.open("data:text/csv," + encodeURIComponent(data),"_blank");
      }
    });
  },
  edit: function (organisationType) {
    console.log('edit'+organisationType+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.srp.fetchQuestion(ractive.tenantUri(organisationType))
      .then(function(response) { return response.json(); })
      .then(ractive.select);
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    ractive.srp.deleteOrgType(ractive.get('current'))
        .then(function(response) {
          switch (response.status) {
          case 204:
            ractive.fetch();
            ractive.toggleResults();
            break;
          default:
            ractive.showMessage('Unable to delete the organisation type ('+response.status+')');
          }
        });
    return false; // cancel bubbling to prevent edit as well as delete
  },
  fetch: function () {
    console.log('fetch...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchOrgTypes(ractive.get('tenant.id'))
    .then(function(response) { return response.json(); }) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(function(data) {
      if ('_embedded' in data) {
        ractive.merge('organisationTypes', data._embedded.organisationTypes);
      } else {
        ractive.merge('organisationTypes', data);
      }
      if (ractive.hasRole('admin')) $('.admin').show();
      if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
      ractive.set('searchMatched',$('#organisationTypesTable tbody tr:visible').length);
      ractive.set('saveObserver', true);
    });
  },
  filter: function(field,value) {
    console.log('filter: field '+field+' = '+value);
    if (value==undefined) value = ractive.get('tenant.stagesInActive');
    if (field==undefined) ractive.set('filter',undefined);
    else ractive.set('filter',{field: field,value: value});
    ractive.set('searchMatched',$('#organisationTypesTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  find: function(organisationTypeId) {
    console.log('find: '+organisationTypeId);
    var c;
    $.each(ractive.get('organisationTypes'), function(i,d) {
      if (organisationTypeId.endsWith(ractive.id(d))) {
        c = d;
      }
    });
    return c;
  },
  save: function () {
    console.log('save organisationType: '+ractive.get('current.name')+'...');
    ractive.set('saveObserver',false);
    if (document.getElementById('currentForm')==undefined) {
      // loading... ignore
      ractive.set('saveObserver',true);
    } else if (document.getElementById('currentForm').checkValidity()) {
      // cannot save organisationType and account in one (grrhh), this will clone...
      var tmp = JSON.parse(JSON.stringify(ractive.get('current')));
      tmp.notes = undefined;
      tmp.documents = undefined;
      tmp.tenantId = ractive.get('tenant.id');
//      console.log('ready to save organisationType'+JSON.stringify(tmp)+' ...');
      ractive.srp.saveOrgType(tmp, ractive.get('tenant.id'))
          .then(function(response) {
            switch(response.status) {
            case 201:
              ractive.set('currentIdx',ractive.get('organisationTypes').push(ractive.get('current'))-1);
              return response.json();
            }
          })
          .then(function(data) {
            if (data !== undefined) ractive.set('current', data);
            ractive.splice('organisationTypes',ractive.get('currentIdx'),1,ractive.get('current'));
            ractive.set('saveObserver', true);
            ractive.showMessage('Organisation Type saved');
          });
    } else {
      console.warn('Cannot save yet as Organisation Type is invalid');
      $('#currentForm :invalid').addClass('field-error');
      ractive.showMessage('Cannot save yet as Organisation Type is incomplete');
      ractive.set('saveObserver',true);
    }
  },
  select: function(organisationType) {
    console.log('select: '+JSON.stringify(organisationType));
    ractive.set('saveObserver',false);
    ractive.set('current', organisationType);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.set('saveObserver',true);
    ractive.set('currentIdx', ractive.get('organisationTypes').indexOf(organisationType));
    ractive.set('saveObserver',true);
    ractive.hideResults();
    $('#currentSect').slideDown();
  },
  sortOrganisationTypes: function() {
    ractive.get('organisationTypes').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
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
    console.warn  ('Skipped organisationType save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});
