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
    entityName: 'question',
    questions: [],
    filter: undefined,
    saveObserver: false,
    tenant: { id: 'sdu' },
    username: localStorage.getItem('username'),
    help: '<p>This page allows the management of a single list of questions and their associated data requirements</p></ul>',
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
          var term = search[idx].toLowerCase();
          var match = ( (obj.name.toLowerCase().indexOf(term)>=0) ||
            (obj.label!=undefined && obj.label.toLowerCase().indexOf(term)>=0) ||
            (obj.hint!=undefined && obj.hint.toLowerCase().indexOf(term)>=0) ||
            (obj.placeholder!=undefined && obj.placeholder.toLowerCase().indexOf(term)>=0) ||
            (obj.source!=undefined && obj.source.toLowerCase().indexOf(term)>=0) ||
            (obj.type!=undefined && obj.type.toLowerCase().indexOf(term)>=0) ||
            (term.startsWith('updated>') && new Date(obj.lastUpdated)>new Date(term.substring(8))) ||
            (term.startsWith('created>') && new Date(obj.firstContact)>new Date(term.substring(8))) ||
            (term.startsWith('updated<') && new Date(obj.lastUpdated)<new Date(term.substring(8))) ||
            (term.startsWith('created<') && new Date(obj.firstContact)<new Date(term.substring(8))) ||
            (term.startsWith('required') && obj.required) ||
            (term.startsWith('!required') && (obj.required==undefined || !obj.required))
          );
          // no match is definitive but match now may fail other terms (AND logic)
          if (!match) return false;
        }
        return true;
      }
    },
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
      { "name": "helpModal", "url": "/partials/help-modal.html"},
      { "name": "navbar", "url": "./vsn/partials/question-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "questionListSect", "url": "./vsn/partials/question-list-sect.html"},
      { "name": "questionCurrentSect", "url": "./vsn/partials/question-current-sect.html"}
    ],
    title: "Survey Questions"
  },
  partials: {
    'helpModal': '',
    'profileArea': '',
    'questionListSect': '',
    'questionCurrentSect': '',
    'sidebar': '',
    'titleArea': ''
  },
  add: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var question = { modellingYear:new Date().getFullYear(), tenantId: ractive.get('tenant.id') };
    ractive.select(question);
  },
  delete: function (obj) {
    ractive.srp.deleteQuestion(obj)
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
  edit: function (question) {
    console.log('edit'+question+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.srp.fetchQuestion(ractive.tenantUri(question))
      .then(function(response) { return response.json(); })
      .then(ractive.select);
  },
  fetch: function () {
    console.log('fetch...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchQuestions(ractive.get('tenant.id'))
      .then(function(response) { return response.json(); })
      .then(function(data) {
        ractive.set('saveObserver', false);
        ractive.set('questions', data);
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.showSearchMatched();
        ractive.set('saveObserver', true);
      });
  },
  save: function () {
    console.log('save question: '+ractive.get('current').name+'...');
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
      ractive.srp.saveQuestion(tmp, ractive.get('tenant.id'))
          .then(function(response) {
            switch(response.status) {
            case 201:
              ractive.set('currentIdx',ractive.get('questions').push(ractive.get('current'))-1);
              return response.json();
            }
          })
          .then(function(data) {
            ractive.set('saveObserver',false);
            if (data !== undefined) ractive.set('current', data);
            ractive.splice('questions',ractive.get('currentIdx'),1,ractive.get('current'));
            ractive.set('saveObserver', true);
            $('.autoNumeric').autoNumeric('update');
            ractive.showMessage('Question saved');
          });
    } else {
      console.warn('Cannot save yet as question is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as question is incomplete');
    }
  },
  select: function(question) {
    console.log('select: '+JSON.stringify(question));
    ractive.set('saveObserver',false);
    ractive.set('current', question);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.hideResults();
    $('#currentSect').slideDown();
    ractive.set('saveObserver',true);
  }
});

// Save on model change
// done this way rather than with on-* attributes because autocomplete
// controls done that way save the oldValue
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  var ignored=['current.references'];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save();
  } else {
    console.info('Skipped question save of '+keypath);
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
