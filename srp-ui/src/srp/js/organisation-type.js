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
    //server: 'http://api.knowprocess.com:8082',
    organisationTypes: [],
    filter: undefined,
    //saveObserver:false,
    tenant: { id: 'sdu' },
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
        something = window.open("data:text/csv," + encodeURIComponent(data),"_blank");
        //something.focus();
      }
    });
  },
  edit: function (organisationType) {
    console.log('edit'+organisationType+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(organisationType);
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    ractive.srp.deleteOrgType(ractive.get('current'))
        .then(response => {
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
    .then(response => response.json()) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(data => {
      if (data['_embedded'] == undefined) {
        ractive.merge('organisationTypes', data);
      } else {
        ractive.merge('organisationTypes', data['_embedded'].organisationTypes);
      }
      if (ractive.hasRole('admin')) $('.admin').show();
      if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
      ractive.set('searchMatched',$('#organisationTypesTable tbody tr:visible').length);
      ractive.set('saveObserver', true);
    })
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
      if (organisationTypeId.endsWith(ractive.getId(d))) {
        c = d;
      }
    });
    return c;
  },
  getId: function(organisationType) {
    console.log('getId: '+organisationType);
    var uri;
    if (organisationType['links']!=undefined) {
      $.each(organisationType.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (organisationType['_links']!=undefined) {
      uri = organisationType._links.self.href.indexOf('?')==-1 ? organisationType._links.self.href : organisationType._links.self.href.substr(0,organisationType._links.self.href.indexOf('?')-1);
    }
    return uri;
  },
  oninit: function() {
    console.log('oninit');
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
          .then(response => {
            switch(response.status) {
            case 201:
              ractive.set('currentIdx',ractive.get('organisationTypes').push(ractive.get('current'))-1);
              return response.json();
            }
          })
          .then(data => {
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
    // TODO obsolete?
	  if (organisationType._links != undefined) {
	    var url = ractive.tenantUri(organisationType);
	    console.log('loading detail for '+url);
	    $.getJSON(ractive.getServer()+url,  function( data ) {
        console.log('found organisationType '+data);
        ractive.set('current', data);
        ractive.initControls();
        // who knows why this is needed, but it is, at least for first time rendering
        $('.autoNumeric').autoNumeric('update',{});
        ractive.set('saveObserver',true);
      });
    } else {
      console.log('Skipping load as no _links.'+organisationType.name);
      ractive.set('current', organisationType);
      ractive.set('currentIdx', ractive.get('organisationTypes').indexOf(organisationType));
      ractive.set('saveObserver',true);
    }
	  ractive.toggleResults();
	  $('#currentSect').slideDown();
  },
  showResults: function() {
    $('#organisationTypesTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#organisationTypesTable').slideDown({ queue: true });
  },
  sortOrganisationTypes: function() {
    ractive.get('organisationTypes').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  toggleResults: function() {
    console.log('toggleResults');
    $('#organisationTypesTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#organisationTypesTable').slideToggle();
  }
});

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#organisationTypesTable tbody tr').length);
  }, 500);
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
    console.warn  ('Skipped organisationType save of '+keypath);
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
