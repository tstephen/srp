/*******************************************************************************
 * Copyright 2015-2020 Tim Stephenson and contributors
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
    wfactors: [],
    entityPath: '/wfactors',
    filter: undefined,
    saveObserver:false,
    tenant: { id: 'sdu' },
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
    help: '<p>This page allows the management of a list of weighting factors used to apportion Carbon to different organisations across the NHS</p>',
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
        var search = ractive.get('searchTerm').split(' ');
        for (var idx = 0 ; idx < search.length ; idx++) {
          var searchTerm = search[idx].toLowerCase();
          var match = ( (obj.category.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.applicablePeriod!=undefined && obj.applicablePeriod.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.orgType!=undefined && obj.orgType.toLowerCase().indexOf(searchTerm)>=0)
            || (searchTerm.startsWith('updated>') && new Date(obj.lastUpdated)>new Date(searchTerm.substring(8)))
            || (searchTerm.startsWith('created>') && new Date(obj.firstContact)>new Date(searchTerm.substring(8)))
            || (searchTerm.startsWith('updated<') && new Date(obj.lastUpdated)<new Date(searchTerm.substring(8)))
            || (searchTerm.startsWith('created<') && new Date(obj.firstContact)<new Date(searchTerm.substring(8)))
          );
          // no match is definitive but match now may fail other terms (AND logic)
          if (!match) return false;
        }
        return true;
      }
    },
    saveObserver: false,
    searchTerm: '2017-18',
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
      { "name": "helpModal", "url": "/partials/help-modal.html"},
      { "name": "navbar", "url": "./vsn/partials/wfactor-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "wfactorListSect", "url": "./vsn/partials/wfactor-list-sect.html"},
      { "name": "wfactorCurrentSect", "url": "./vsn/partials/wfactor-current-sect.html"}
    ],
    title: "Weighting Factors"
  },
  partials: {
    'helpModal': '',
    'navbar': '',
    'profileArea': '',
    'wfactorListSect': '',
    'wfactorCurrentSect': '',
    'sidebar': '',
    'toolbar': '',
    'titleArea': ''
  },
  addRecord: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var wfactor = { applicablePeriod: new Date().getFullYear(), tenantId: ractive.get('tenant.id') };
    ractive.select(wfactor);
  },
  delete: function (obj) {
    ractive.srp.deleteWeightingFactor(ractive.get('current'))
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
  edit: function (wfactor) {
    console.log('edit'+wfactor+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(wfactor);
  },
  fetch: function () {
    console.log('fetch...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchWeightingFactors()
    .then(response => response.json())
    .then(data => {
      ractive.set('saveObserver', false);
      ractive.set('wfactors', data);
      if (ractive.hasRole('admin')) $('.admin').show();
      if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
      ractive.showResults();
      ractive.showSearchMatched();
      ractive.set('saveObserver', true);
    })
  },
  filter: function(filter) {
    console.log('filter: '+JSON.stringify(filter));
    ractive.set('filter',filter);
    $('.omny-dropdown.dropdown-menu li').removeClass('selected')
    $('.omny-dropdown.dropdown-menu li:nth-child('+filter.idx+')').addClass('selected')
    ractive.set('searchMatched',$('#wfactorsTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  hideResults: function() {
    $('#wfactorsTableToggle').addClass('kp-icon-caret-right').removeClass('kp-icon-caret-down');
    $('#wfactorsTable').slideUp();
    $('#currentSect').slideDown({ queue: true });
  },
  save: function () {
    console.log('save wfactor: '+ractive.get('current').name+'...');
    ractive.set('saveObserver',false);
    var id = ractive.uri(ractive.get('current'));
    console.log('  id: '+id+', so will '+(id === undefined ? 'POST' : 'PUT'));

    ractive.set('saveObserver',true);
    if (document.getElementById('currentForm')==undefined) {
      // loading... ignore
    } else if(document.getElementById('currentForm').checkValidity()) {
      var tmp = JSON.parse(JSON.stringify(ractive.get('current')));
      tmp.tenantId = ractive.get('tenant.id');
      if (tmp.optionNames == undefined) tmp.optionNames = [];
      else if (typeof tmp.optionNames == 'string') tmp.optionNames = tmp.optionNames.split(',');
      ractive.srp.saveWeightingFactor(tmp, ractive.get('tenant.id'))
      .then(response => {
        switch(response.status) {
        case 201:
          ractive.set('currentIdx',ractive.get('wfactors').push(ractive.get('current'))-1);
          return response.json();
        }
      })
      .then(data => {
        if (data !== undefined) ractive.set('current', data);
        ractive.splice('wfactors',ractive.get('currentIdx'),1,ractive.get('current'));
        ractive.set('saveObserver', true);
        $('.autoNumeric').autoNumeric('update');
        ractive.showMessage('Weighting Factor saved');
      });
    } else {
      console.warn('Cannot save yet as wfactor is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as record is incomplete');
    }
  },
  select: function(wfactor) {
    console.log('select: '+JSON.stringify(wfactor));
    ractive.set('saveObserver',false);
    var url = ractive.uri(wfactor);
    if (url == undefined) {
      console.log('Skipping load as no uri.');
      ractive.set('current', wfactor);
      ractive.set('saveObserver',true);
    } else {
      console.log('loading detail for '+url);
      ractive.srp.fetchWeightingFactor(url)
      .then(response => response.json())
      .then(data => {
        console.log('found wfactor '+data);
        ractive.set('current', data);
        ractive.initControls();
        // who knows why this is needed, but it is, at least for first time rendering
        $('.autoNumeric').autoNumeric('update',{});
        ractive.hideResults();
        $('#currentSect').slideDown();
        ractive.set('saveObserver',true);
      })
    }
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
  },
  showResults: function() {
    $('#wfactorsTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#wfactorsTable').slideDown({ queue: true });
  },
  showSearchMatched: function() {
    ractive.set('searchMatched',$('#wfactorsTable tbody tr').length);
    if ($('#wfactorsTable tbody tr:visible').length==1) {
      var wfactorId = $('#wfactorsTable tbody tr:visible').data('href')
      var q = Array.findBy('selfRef',wfactorId,ractive.get('wfactors'))
      ractive.select( q );
    }
  },
  sortRecords: function() {
    ractive.get('wfactors').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  toggleResults: function() {
    console.info('toggleResults');
    $('#wfactorsTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#wfactorsTable').slideToggle();
  }
});

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#wfactorsTable tbody tr').length);
  }, 500);
});

// Save on model change
// done this way rather than with on-* attributes because autocomplete
// controls done that way save the oldValue
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  ignored=['current.references'];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save();
  } else {
    console.info('Skipped wfactor save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});
ractive.on( 'filter', function ( event, filter ) {
  console.info('filter on '+JSON.stringify(event)+','+filter.idx);
  ractive.filter(filter);
});
ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});
