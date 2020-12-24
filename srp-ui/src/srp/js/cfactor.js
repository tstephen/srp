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
    cfactors: [],
    entityPath: '/cfactors',
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
    help: '<p>This page allows the management of a list of Carbon intensity factors used in the Carbon accounting calculations</p>',
    matchRole: function(role) {
      console.info('matchRole: ' + role);
      $('.' + role).show();
      return ractive.hasRole(role);
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
          var match = ( (obj.name.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.applicablePeriod!=undefined && obj.applicablePeriod.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.scope!=undefined && obj.scope.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.source!=undefined && obj.source.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.unit!=undefined && obj.unit.toLowerCase().indexOf(searchTerm)>=0)
            || (obj.value!=undefined && (''+obj.value).indexOf(searchTerm)>=0)
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
      { "name": "helpModal", "url": $env.server+"/partials/help-modal.html"},
      { "name": "navbar", "url": "/srp/vsn/partials/cfactor-navbar.html"},
      { "name": "profileArea", "url": $env.server+"/partials/profile-area.html"},
      { "name": "sidebar", "url": $env.server+"/partials/sidebar.html"},
      { "name": "toolbar", "url": $env.server+"/partials/toolbar.html"},
      { "name": "titleArea", "url": $env.server+"/partials/title-area.html"},
      { "name": "cfactorListSect", "url": "/srp/vsn/partials/cfactor-list-sect.html"},
      { "name": "cfactorCurrentSect", "url": "/srp/vsn/partials/cfactor-current-sect.html"}
    ],
    title: "Carbon Factors"
  },
  partials: {
    'helpModal': '',
    'profileArea': '',
    'cfactorListSect': '',
    'cfactorCurrentSect': '',
    'sidebar': '',
    'titleArea': ''
  },
  addRecord: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var year = new Date().getFullYear();
    var cfactor = {
      applicablePeriod: year+'-'+(year-1999),
      tenantId: ractive.get('tenant.id')
    };
    ractive.select(cfactor);
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
  edit: function (cfactor) {
    console.log('edit'+cfactor+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(cfactor);
  },
  fetch: function () {
    console.log('fetch...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/cfactors/',
      crossDomain: true,
      success: function( data ) {
        ractive.set('saveObserver', false);
        ractive.set('cfactors', data);
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.showResults();
        ractive.showSearchMatched();
        ractive.set('saveObserver', true);
      }
    });
  },
  filter: function(filter) {
    console.log('filter: '+JSON.stringify(filter));
    ractive.set('filter',filter);
    $('.omny-dropdown.dropdown-menu li').removeClass('selected')
    $('.omny-dropdown.dropdown-menu li:nth-child('+filter.idx+')').addClass('selected')
    ractive.set('searchMatched',$('#cfactorsTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  hideResults: function() {
    $('#cfactorsTableToggle').addClass('kp-icon-caret-right').removeClass('kp-icon-caret-down');
    $('#cfactorsTable').slideUp();
    $('#currentSect').slideDown({ queue: true });
  },
  postSelect: function(cfactor) {
    ractive.set('current', cfactor);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.hideResults();
    $('#currentSect').slideDown();
    ractive.set('saveObserver',true);
  },
  save: function () {
    console.log('save cfactor: '+ractive.get('current').name+'...');
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
//      console.log('ready to save cfactor'+JSON.stringify(tmp)+' ...');
      $.ajax({
        url: id === undefined ? ractive.getServer()+'/cfactors/' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(tmp),
        success: completeHandler = function(data, textStatus, jqXHR) {
          console.log(jqXHR.status+': '+JSON.stringify(data));
          var location = jqXHR.getResponseHeader('Location');
          ractive.set('saveObserver',false);
          if (location != undefined) ractive.set('current._links.self.href',location);
          if (jqXHR.status == 201) {
            ractive.set('currentIdx',ractive.get('cfactors').push(ractive.get('current'))-1);
          }
          if (jqXHR.status == 204) ractive.splice('cfactors',ractive.get('currentIdx'),1,ractive.get('current'));

          $('.autoNumeric').autoNumeric('update');
          ractive.showMessage('Record saved');
          ractive.set('saveObserver',true);
        }
      });
    } else {
      console.warn('Cannot save yet as cfactor is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as record is incomplete');
    }
  },
  select: function(cfactor) {
    console.log('select: '+JSON.stringify(cfactor));
    ractive.set('saveObserver',false);
    var url = ractive.uri(cfactor);
    if (url == undefined) {
      console.log('Skipping load as no uri.');
      ractive.postSelect(cfactor);
    } else {
      console.log('loading detail for '+url);
      $.getJSON(url,  function( data ) {
        console.log('found cfactor '+data);
        ractive.set('saveObserver',false);
        ractive.postSelect(data);
      });
    }
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
  },
  showResults: function() {
    $('#cfactorsTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#cfactorsTable').slideDown({ queue: true });
  },
  showSearchMatched: function() {
    ractive.set('searchMatched',$('#cfactorsTable tbody tr').length);
    if ($('#cfactorsTable tbody tr:visible').length==1) {
      var cfactorId = $('#cfactorsTable tbody tr:visible').data('href')
      var q = Array.findBy('selfRef',cfactorId,ractive.get('cfactors'))
      ractive.select( q );
    }
  },
  sortRecords: function() {
    ractive.get('cfactors').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  toggleResults: function() {
    console.info('toggleResults');
    $('#cfactorsTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#cfactorsTable').slideToggle();
  }
});

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#cfactorsTable tbody tr').length);
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
    console.info('Skipped cfactor save of '+keypath);
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