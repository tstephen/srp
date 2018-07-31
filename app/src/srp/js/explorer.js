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
 ******************************************************************************/
var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    server: $env.server,
    tenant: { id: 'sdu' },
    criteria: [
      { field: "", operator: " = ", value: "" }
    ],
    // formatAnswer: function(qName) {
    //   if (qName==undefined || ractive.get('answers')==undefined) return '';
    //   return ractive.getAnswer(qName);
    // },
    formatDate: function(timeString) {
      return new Date(timeString).toLocaleDateString(navigator.languages).replace('Invalid Date','n/a').replace('01/01/1970','n/a');
    },
    formatSurveyName: function(surveyReturns) {
      return surveyReturns.map(function(rtn) {
        return rtn.name.indexOf('ERIC')==-1 ? 'SDU return' : 'ERIC';
      });
    },
    isCcg: function() {
      if (ractive.get('answers')==undefined) return '';
      if (ractive.getAnswer('ORG_TYPE')=='CCG') return true;
      else return false;
    },
    matchSearch: function(obj) {
      if (ractive.get('searchTerm')==undefined || ractive.get('searchTerm').length==0) {
        return true;
      } else {
        var search = ractive.get('searchTerm').split(' ');
        for (var idx = 0 ; idx < search.length ; idx++) {
          var searchTerm = search[idx].toLowerCase();
          var match = ( (obj.id.indexOf(searchTerm)>=0)
              || (obj.applicablePeriod.toLowerCase().indexOf(searchTerm.toLowerCase())>=0)
              || (obj.question.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0)
              || (searchTerm.startsWith('updated>') && new Date(obj.lastUpdated)>new Date(ractive.get('searchTerm').substring(8)))
              || (searchTerm.startsWith('created>') && new Date(obj.created)>new Date(ractive.get('searchTerm').substring(8)))
              || (searchTerm.startsWith('updated<') && new Date(obj.lastUpdated)<new Date(ractive.get('searchTerm').substring(8)))
              || (searchTerm.startsWith('created<') && new Date(obj.created)<new Date(ractive.get('searchTerm').substring(8)))
              || (searchTerm.startsWith('status:') && obj.status!=undefined && obj.status.toLowerCase().indexOf(ractive.get('searchTerm').substring(7))!=-1)
          );
          //no match is definitive but matches may fail other terms (AND logic)
          if (!match) return false;
        }
        return true;
      }
    },
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
      console.info('sorted');
      if (ractive.get('sortColumn') == column && ractive.get('sortAsc')) return 'sort-asc';
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      { "name": "loginSect", "url": $env.server+"/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "navbar", "url": "/srp/vsn/partials/answer-navbar.html"},
      { "name": "profileArea", "url": $env.server+"/partials/profile-area.html"},
      { "name": "sidebar", "url": $env.server+"/partials/sidebar.html"},
      { "name": "titleArea", "url": $env.server+"/partials/title-area.html"},
      { "name": "answerCriteriaSect", "url": "/srp/vsn/partials/answer-criteria-sect.html"},
      { "name": "answerListSect", "url": "/srp/vsn/partials/answer-list-sect.html"}
    ],
    title: "SDU Return Explorer"
  },
  partials: {
    'loginSect':'',
    'answerCriteriaSect':'',
    'answerListSect':'',
    'profileArea':'',
    'sidebar':'',
    'titleArea':''
  },
  addCriterion: function() {
    ractive.push('criteria', { field: "", operator: " = ", value: "" });
  },
  // defaultOrgCriterion: function() {
  //   for (var idx = 0 ; idx < ractive.push('criteria')
  //   ractive.push('criteria', { field: "org", operator: " = ", value: $auth.getClaim('org') });
  // },
  deleteCriterion: function(idx) {
    if (ractive.get('criteria').length <= 1) {
      return ractive.showError('You must specify one or more criteria');
    } else {
      ractive.splice('criteria', idx, 1);
    }
  },
  enter: function () {
    console.log('enter...');
    ractive.login();
  },
  export: function () {
    console.log('export...');
    var data = ractive.get('answers');
    if (data == undefined || data.length==0) {
      return ractive.showMessage('No results to export yet, please perform a search first');
    }
    ractive.toCsv(data, "SDU Return Data", 'id,source,applicablePeriod,question.categories,question.name,question.label,response,status,revision,submittedDate,submittedBy,created,createdBy,lastUpdated,updatedBy');
  },
  fetch: function() {
    console.info('fetch...');
    ractive.fetchOrgs();
    ractive.fetchOrgTypes();
  },
  fetchCategories: function() {
    console.info('fetchCategories...');
    $.getJSON(ractive.getServer()+'/survey-categories/', function(data) {
      ractive.set('categories', data);
      ractive.addDataList({ name: 'categories' }, data);
    });
  },
  fetchOrgs: function() {
    console.info('fetchOrgs...');
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/accounts/', function(data) {
      ractive.set('orgs', data);
      // doesn't fit the standard pattern of addDataList
      $('datalist#orgs').remove();
      $('body').append('<datalist id="orgs">');
      for (var idx = 0 ; idx < data.length ; idx++) {
        $('datalist#orgs').append('<option value="'+data[idx]['code']+'">'+data[idx].name+'</option>');
      };
    });
  },
  fetchOrgTypes: function() {
    console.info('fetchOrgTypes...');
    $.getJSON(ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-types/', function(data) {
      ractive.set('orgTypes', data);
      ractive.addDataList({ name: 'orgTypes' }, data);
    });
  },
  fetchQuestions: function() {
    console.info('fetchQuestions...');
    $.getJSON(ractive.getServer()+'/questions/', function(data) {
      ractive.set('questions', data);
      // doesn't fit the standard pattern of addDataList
      $('datalist#questions').remove();
      $('body').append('<datalist id="questions">');
      for (var idx = 0 ; idx < data.length ; idx++) {
        $('datalist#questions').append('<option value="'+data[idx].label+'">'+data[idx].label+'</option>');
      };
    });
  },
  search: function() {
    console.log('search');
    if (ractive.get('criteria').length==1
        && (ractive.get('criteria.0.field')=='organisation type'
            || ractive.get('criteria.0.field')=='region'
            || ractive.get('criteria.0.field')=='status')) {
      return ractive.showMessage('Please specify more criteria to narrow down your search');
    }
    $.ajax({
      dataType: "json",
      type: 'POST',
      contentType: 'application/json',
      url: ractive.getServer()+'/answers/findByCriteria',
      data: JSON.stringify(ractive.get('criteria')),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('saveObserver', false);
        for (var i = 0 ; i < data.length ; i++) {
          // associate category with answer
          data[i].category = function(q) {
            return 'Cat of '+q;//data[i]['question'].name
          }
          // flatten surveys this appears in
          data[i].source = data[i]['surveyReturns'].map(function(rtn) {
            return rtn.name.indexOf('ERIC')==-1 ? 'SDU return' : 'ERIC';
          });
        }
        ractive.set('answers', data);
        ractive.set('searchMatched', data.length);
        ractive.set('saveObserver', true);
      },
      error: function(jqXHR) {
        switch(jqXHR.status) {
        case 302:
          var noRows;
          try {
            noRows = JSON.parse(jqXHR.responseJSON).size;
          } catch (err) {
            noRows = 'too many';
          }
          ractive.showError('The criteria you specified result in '+noRows+' rows to display, please narrow your search');
          break;
        default:
          console.error();
        }
      }
    });
  },
  reset: function() {
    console.info('reset');
    if (document.getElementById('resetForm').checkValidity()) {
      $('#resetSect').slideUp();
      $('#loginSect').slideDown();
      var addr = $('#email').val();
      $.ajax({
        url: ractive.getServer()+'/msg/srp/omny.passwordResetRequest.json',
        type: 'POST',
        data: { json: JSON.stringify({ email: addr, tenantId: 'srp' }) },
        dataType: 'text',
        success: function(data) {
          ractive.showMessage('An reset link has been sent to '+addr);
        },
      });
    } else {
      ractive.showError('Please enter the email address you registered with');
    }
  },
  showReset: function() {
    $('#loginSect').slideUp();
    $('#resetSect').slideDown();
  }
});

$(document).ready(function() {
  $('head').append('<link href="'+ractive.getServer()+'/css/sdu-1.0.0.css" rel="stylesheet">');

  if (Object.keys(getSearchParameters()).indexOf('error')!=-1) {
    ractive.showError('The username and password provided do not match a valid account');
  } else if (Object.keys(getSearchParameters()).indexOf('logout')!=-1) {
    ractive.showMessage('You have been successfully logged out');
  }
  ractive.fetch();
  // $auth.addLoginCallback(ractive.fetch);
  // $auth.addLoginCallback(ractive.defaultOrgCriterion);

  ractive.observe('criteria.*.field', function(newValue, oldValue, keypath) {
    var idx = parseInt(keypath.substring(keypath.indexOf('.')+1, keypath.lastIndexOf('.')));
    console.log('criteria '+ idx +' changed');
    switch(newValue) {
      case 'category':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'categories');
        if ($('datalist#categories').length==0) ractive.fetchCategories();
        break;
      case 'org':
      case 'organisation':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'orgs');
        if ($('datalist#orgs').length==0) ractive.fetchOrgs();
        break;
      case 'orgType':
      case 'organisation type':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'orgTypes');
        if ($('datalist#orgTypes').length==0) ractive.fetchOrgTypes();
        break;
      case 'period':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'periods');
        break;
      case 'question':
          $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'questions');
          if ($('datalist#questions').length==0) ractive.fetchQuestions();
          break;
      case 'region':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'regions');
        break;
      case 'status':
        $('.criteria:nth-child('+(idx+1)+') .criteria-value').attr('list', 'status');
        break;
      case '':
        break;
      default:
        console.info('no value list for '+newValue);
    }
  });

})
