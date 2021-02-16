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
    narrativeCtxtPrompt: 'Please insert a commentary on contextual information e.g. projects, initiatives etc. related to this area.',
    narrativePerfPrompt: 'Please click to insert a commentary on your performance in this area.',
    orgAnswerNames: ['PROVIDER1_COMMISSIONED','PROVIDER2_COMMISSIONED','PROVIDER3_COMMISSIONED',
        'PROVIDER4_COMMISSIONED','PROVIDER5_COMMISSIONED','PROVIDER6_COMMISSIONED',
        'PROVIDER7_COMMISSIONED','PROVIDER8_COMMISSIONED','CCG1_SERVED',
        'CCG2_SERVED','CCG3_SERVED','CCG4_SERVED','CCG5_SERVED','CCG6_SERVED','CCG7_SERVED','CCG8_SERVED'],
    requiredAnswers: ['ORG_CODE', 'ORG_NAME', 'ORG_TYPE', 'SDMP_CRMP', 'HEALTHY_TRANSPORT_PLAN', 'PROMOTE_HEALTHY_TRAVEL'],
    period: '2020-21',
    server: $env.server,
    survey: 'SDU-2020-21',
    tenant: { id: 'sdu' },
    username: localStorage['username'],
    formatAbsAnswer: function(qName, period) {
      try {
        var answer = ractive.getAnswer(qName, period);
        return answer == undefined ? '' : Math.abs(answer);
      } catch (e) {
        return '';
      }
    },
    formatAnswer: function(qName, period) {
      if (qName==undefined || ractive.get('surveyReturn')==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, period);
          return answer == undefined ? '' : answer;
        } catch (e) {
          return '';
        }
      }
    },
    formatCommissionerAnswer: function(idx,qName) {
      if (qName==undefined || ractive.get('surveyReturn.commissioners.'+idx)==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, ractive.get('period'), ractive.get('surveyReturn.commissioners.'+idx+'.answers'));
          return answer;
        } catch (e) {
          return '';
        }
      }
    },
    formatDateTime: function(timeString) {
      // console.log('formatDate: '+timeString);
      if (timeString==undefined) return 'n/a';
      return new Date(timeString).toLocaleString(navigator.languages);
    },
    formatDecimalAnswer: function(qName, period, decimalPlaces) {
      if (qName==undefined || ractive.get('surveyReturn')==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, period);
          return answer == undefined ? '' : parseFloat(answer).formatDecimal(decimalPlaces);
        } catch (e) {
          return '';
        }
      }
    },
    formatHint: function(qName) {
      for (i in ractive.get('q.categories')) {
        for (j in ractive.get('q.categories.'+i+'.questions')) {
          if (ractive.get('q.categories.'+i+'.questions.'+j+'.name')==qName) return 'This is based on your response to '+ractive.get('q.categories.'+i+'.name')+' question '+(parseInt(j)+1);
        }
      }
      if (ractive.get('labels.'+qName)!=undefined) return ractive.get('labels.'+qName);
      else return qName;
    },
    formatProviderAnswer: function(idx,qName) {
      if (qName==undefined || ractive.get('surveyReturn.providers.'+idx)==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName, ractive.get('period'), ractive.get('surveyReturn.providers.'+idx+'.answers'));
          return answer;
        } catch (e) {
          return '';
        }
      }
    },
    haveAnswer: function(qName) {
      try {
        var a = ractive.getAnswer(qName);
        return (a == undefined || a == '' || a == ractive.get('narrativeCtxtPrompt') || a == ractive.get('narrativePerfPrompt'))
            ? false
            : true;
      } catch (e) {
        return false;
      }
    },
    isCcg: function() {
      return ractive.isCcg();
    },
    isEClassUser: function() {
      return ractive.isEClassUser();
    },
    matchRole: function(role) {
      console.info('matchRole: '+role)
      if (role==undefined || ractive.hasRole(role)) {
        $('.'+role).show();
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
        return obj.question.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.applicablePeriod.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.status.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || (searchTerm.startsWith('revision:') && obj.revision==searchTerm.substring(9))
          || (searchTerm.startsWith('updated>') && new Date(obj.lastUpdated)>new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('created>') && new Date(obj.created)>new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('updated<') && new Date(obj.lastUpdated)<new Date(searchTerm.substring(8)))
          || (searchTerm.startsWith('created<') && new Date(obj.created)<new Date(searchTerm.substring(8)));
      }
    },
    sort: function (array, column, asc) {
      console.info('sort '+(asc ? 'ascending' : 'descending')+' on: '+column);
      array = array.slice(); // clone, so we don't modify the underlying data

      return array.sort( function ( a, b ) {
        console.log('sort '+a+','+b);
        var aVal = eval('a.'+column);
        var bVal = eval('b.'+column);
        if (bVal==undefined || bVal==null || bVal=='') {
          return (aVal==undefined || aVal==null || aVal=='') ? 0 : -1;
        } else if (asc) {
          return aVal < bVal ? -1 : 1;
        } else {
          return aVal > bVal ? -1 : 1;
        }
      });
    },
    sortAsc: true,
    sortColumn: 'id',
    sorted: function(column) {
//      console.info('sorted:'+column);
      return 'hidden'; // hide as sorting is disabled due to perf
      if (ractive.get('sortColumn') == column && ractive.get('sortAsc')) return 'sort-asc';
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "nhsCarbonProfileSect", "url": "/srp/vsn/partials/nhs-carbon-profile.html"},
      { "name": "statusSect", "url": "/srp/vsn/partials/status-sect.html"}
    ],
  },
  partials: {
    'sidebarSect': '',
    'toolbarSect': '',
    'nhsCarbonProfileSect': '',
    'shareCtrl': '<div class="controls pull-right" style="display:none">'
                +'  <span class="glyphicon icon-btn kp-icon-share"></span>'
                +'  <!--span class="glyphicon icon-btn kp-icon-link"></span-->'
                +'  <!--span class="glyphicon icon-btn kp-icon-copy"></span-->'
                +'</div>',
    'statusSect': ''
  },
  calculate: function () {
    console.info('calculate...');
    $('.btn-calc,.btn-refresh,.btn-submit').attr('disabled','disabled');
    $('#ajax-loader').show();
    $.ajax({
      dataType: 'json',
      type: 'POST',
      url: ractive.getServer()+'/calculations/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  copyLink: function(ev) {
    if (navigator['clipboard'] == undefined) {
      alert('Please upgrade your browser to be able to copy links');
    } else {
      navigator.clipboard.writeText(ev.target.getAttribute('data-share'));
      console.info('copied link');
      var toast = new iqwerty.toast.Toast('Copied!', { style: { main: {
	  background: '#0078c1',
	  color: 'white',
	  'box-shadow': '0 0 50px rgba(0, 120, 193, .7)'
	}}});
    }
    return false;
  },
  enter: function () {
    console.info('enter...');
  },
  fetch: function() {
    console.info('fetch...');
    ractive.fetchMessages();
    if (getSearchParameters()['id']==undefined || !ractive.hasRole('analyst')) ractive.fetchImplicit();
    else ractive.fetchExplicit();
  },
  fetchExplicit: function() {
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/returns/'+getSearchParameters()['id'],
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  fetchImplicit: function() {
    if (ractive.getProfile() == undefined) return; // still loading
    ractive.set('org', ractive.getAttribute('org'));
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/returns/findCurrentBySurveyNameAndOrg/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  fetchCsv: function(ctrl, callback) {
    var url = $(ctrl)
        .data('src').indexOf('//')==-1
            ? ractive.getServer()+$(ctrl).data('src')
            : $(ctrl).data('src').replace(/host:port/,window.location.host)
        .attr('width', '1024')
        .attr('width', '400')
    $.ajax({
      dataType: "text",
      url: url,
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('saveObserver', false);
        //ractive.set(keypath,data);
        var options = {};
        if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
        if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
        if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
        if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
        if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

        $(ctrl)
          .on('mouseover', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').show();
          })
          .on('mouseout', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').hide();
          });
        $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
        callback('#'+ctrl.id, data, options);
        ractive.set('saveObserver', true);
      }
    });
  },
  fetchMessages: function() {
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/messages_'+navigator.language+'.json',
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('labels', data);
      }
    });
  },
  fetchOrgData: function() {
    console.info('fetchOrgData');
    $( "#ajax-loader" ).show();
    ractive.set('saveObserver', false);
    ractive.set('surveyReturn.orgs', []);
    var orgs = ractive.get('orgAnswerNames').filter(function(el, idx) {
      return (ractive.isCcg() && el.startsWith('PROVIDER')) || (!ractive.isCcg() && el.startsWith('CCG'));
    });
    for (var idx = 0 ; idx < orgs.length ; idx++) {
      try {
        var org = ractive.getAnswer(orgs[idx]);
        if (org == undefined || org.trim().length==0) continue;
        $.ajax({
          dataType: "json",
          url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/accounts/findByName/'+encodeURIComponent(org),
          crossDomain: true,
          headers: {
            "X-Requested-With": "XMLHttpRequest",
            "X-Authorization": "Bearer "+ractive.keycloak.token,
            "Authorization": "Bearer "+ractive.keycloak.token,
            "Cache-Control": "no-cache"
          },
          success: function( data ) {
            ractive.set('saveObserver', false);
            for (j = 0 ; j < ractive.get('surveyReturn.orgs').length ; j++) {
              if (ractive.get('surveyReturn.orgs.'+j+'.name') == data.name) {
                ractive.splice('surveyReturn.orgs',j,1);
              }
            }
            ractive.push('surveyReturn.orgs', data);
            $( "#ajax-loader" ).hide();
            ractive.set('saveObserver', true);
          },
          error: function(jqXHR, textStatus, errorThrown ) {
            var org = this.url.substring(this.url.lastIndexOf('/')+1);
            console.warn('Unable to fetch data for '+org+'.'+jqXHR.status+':'+textStatus+','+errorThrown);
            $( "#ajax-loader" ).hide();
            ractive.set('saveObserver', true);
          }
        });
      } catch (e) {
        console.info('Assume no org at idx: '+idx);
      }
    }
  },
  fetchSuccessHandler: function( data ) {
    ractive.set('saveObserver', false);
    if (Array.isArray(data)) ractive.set('surveyReturn', data[0]);
    else ractive.set('surveyReturn', data);
    if (ractive.isCcg()) ractive.fetchOrgData();
    else ractive.fetchOrgData();
    //if (ractive.hasRole('admin')) $('.admin').show();
    //if (ractive.hasRole('power-user')) $('.power-user').show();
    if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
    //ractive.set('searchMatched',$('#contactsTable tbody tr:visible').length);
    $('.rpt.pie').each(function(i,d) {
      ractive.fetchCsv(d, renderPie);
    });
    $('.rpt.pie2').each(function(i,d) {
      ractive.renderCsvForPie(d, renderPie);
    });
    $('.rpt.stacked,.rpt.stacked2').each(function(i,d) {
      switch (true) {
      case (window.innerWidth < 480):
        $(d).attr('width',440).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 768):
        $(d).attr('width',720).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 980):
        $(d).attr('width',720).attr('height', window.innerHeight* .4);
        break;
      case (window.innerWidth < 1200):
        $(d).attr('width',window.innerWidth* .8).attr('height', window.innerHeight* .4);
        break;
      default:
        $(d).attr('width',1140).attr('height', window.innerHeight* .4);
      }
    });
    $('.rpt.stacked').each(function(i,d) {
      ractive.fetchCsv(d, renderStacked);
    });
    $('.rpt.stacked2').each(function(i,d) {
      ractive.renderCsv(d, renderStacked);
    });
    $('.rpt.table').each(function(i,d) {
      ractive.fetchTable(d);
    });
    $('.rpt.table2').each(function(i,d) {
      ractive.renderTable(d);
    });
    if (data.status == 'Draft') {
      $('.btn-calc').removeAttr('disabled');
      $('.btn-refresh').removeAttr('disabled');
      $('.btn-submit').removeAttr('disabled');
      $('.btn-restate').attr('disabled','disabled');
    }
    if (data.status == 'Submitted') {
      $('.btn-calc').attr('disabled','disabled');
      $('.btn-refresh').attr('disabled','disabled');
      $('.btn-submit').attr('disabled','disabled');
      $('.btn-restate').removeAttr('disabled');
    }
    var qs=0;
    var coreQs=0;
    var answeredCoreQs=0;
    var answeredQs=0;
    var periods = [];
    for (idx = 0 ; idx < ractive.get('surveyReturn.answers').length ; idx++) {
      qs++;
      if (ractive.get('surveyReturn.answers.'+idx+'.question.required')==true) coreQs++;
      if (ractive.get('surveyReturn.answers.'+idx+'.response')!=undefined
          && ractive.get('surveyReturn.answers.'+idx+'.response')!=''
          && ractive.get('surveyReturn.answers.'+idx+'.derived')==false
          && ractive.get('surveyReturn.answers.'+idx+'.status')!='Superseded') {
        answeredQs++;
        if (ractive.get('surveyReturn.answers.'+idx+'.question.required')==true) answeredCoreQs++;
        periods.push(ractive.get('surveyReturn.answers.'+idx+'.applicablePeriod'));
      }
    }
    ractive.set('surveyReturn.completeness.qs',qs);
    ractive.set('surveyReturn.completeness.coreQs',coreQs);
    ractive.set('surveyReturn.completeness.answeredCoreQs',answeredCoreQs);
    ractive.set('surveyReturn.completeness.answeredQs',answeredQs);
    ractive.set('surveyReturn.completeness.periods',periods.uniq());
    var requiredMissing = [];
    for (idx = 0 ; idx < ractive.get('requiredAnswers').length ; idx++) {
      if (ractive.getAnswer(ractive.get('requiredAnswers.'+idx)) != undefined) {
        requiredMissing.push(ractive.get('requiredAnswers.'+idx));
      }
    }
    ractive.set('surveyReturn.completeness.missingrequired',requiredMissing);
    ractive.initNarrative();
    $('title').empty().append('Sustainability Report &ndash; '+ ractive.get('surveyReturn.applicablePeriod') +' &ndash; '+ ractive.get('surveyReturn.org'));
    ractive.fetchSurvey();
    ractive.set('saveObserver', true);
  },
  fetchSurvey: function() {
    console.info('fetchSurvey');
    ractive.srp.fetchSurvey(ractive.get('survey'), function(data) {
      console.log('success:'+data);
      ractive.set('q', data);
    });
  },
  fetchTable: function(ctrl) {
    $.ajax({
      dataType: "html",
      url: ractive.getServer()+$(ctrl).data('src'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+ractive.keycloak.token,
        "Authorization": "Bearer "+ractive.keycloak.token,
        "Cache-Control": "no-cache"
      },
      success: function( data ) {
        ractive.set('saveObserver', false);
        $(ctrl).empty().append(data)
          .on('mouseover', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').show();
          })
          .on('mouseout', function(ev) {
            $('#'+ev.currentTarget.id+' .controls').hide();
          });
        $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
        ractive.set('saveObserver', true);
      }
    });
  },
  formatNumber: function() {
    $('.number').each(function(i,d) {
      var a = d.innerText.substring(0,d.innerText.indexOf('.'));
      d.innerText = a.replace(new RegExp("^(\\d{" + (a.length%3?a.length%3:0) + "})(\\d{3})", "g"), "$1,$2").replace(/(\d{3})+?/gi, "$1,").replace(/^,/,'').slice(0,-1);
    });
  },
  getAnswer: function(qName, period) {
    if (period == undefined) period = ractive.get('surveyReturn.applicablePeriod');
    var answers = ractive.get('surveyReturn.answers');
    return ractive.getAnswerFromArray(qName, period, answers);
  },
  getAnswerFromArray: function(qName, period, answers, unformatted) {
    for (var idx = 0 ; idx < answers.length ; idx++) {
      var a = answers[idx];
      if (a.question.name == qName && a.applicablePeriod == period && a.response=='true') {
        return true;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.response=='false') {
        return false;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number' && unformatted == true) {
        var x = a.response == undefined ? 0 : parseFloat(a.response);
        if (isNaN(x)) return 0;
        else return x;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number' && parseFloat(a.response)<100) {
        return parseFloat(a.response).sigFigs(3);
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number') {
        return parseFloat(a.response).formatDecimal(0);
      } else if (a.question.name == qName && a.applicablePeriod == period) {
        return a.response;
      }
    }
    return a.question.type == 'number' ? 0 : undefined;
  },
  getAttribute: function(attr) {
    return ractive.getProfile().attributes[attr];
  },
  getPeriod: function(offset) {
    var currentYear = parseInt(ractive.get('period').substring(0,4));
    var yearEnd = (currentYear-1999+offset);
    var period = (currentYear+offset)+'-'+(yearEnd < 10 ? '0'+yearEnd : yearEnd);
    return period;
  },
  getProfile: function() {
    var profile = localStorage['profile'];
    if (profile == undefined) {
      alert('Unable to authenticate you at the moment, please try later');
      return;
    } else {
      return JSON.parse(profile);
    }
  },
  initNarrative: function() {
    if (ractive.get('surveyReturn.status')!='Draft') {
      console.info('Skipping initNarrative because status is '+ractive.get('surveyReturn.status'));
      $('.narrative').removeAttr('contenteditable');
      return;
    }
    $('.narrative')
        .attr('contenteditable', 'true')
        .click(function(ev) {
          if ($(ev.target).hasClass('narrative')) {
            console.info('Edit... '+$(ev.target).data('q'));
          } else {
            console.info('Edit: '+ev.target.tagName+$(ev.target).closest('contenteditable').data('q'));
          }
        })
        .blur(function(ev) {
          if ($(ev.target).hasClass('narrative')) {
            ractive.saveNarrative($(ev.target).data('q'), ev.target.innerText);
          } else {
            ractive.saveNarrative($(ev.target).closest('contenteditable').data('q'),
                ev.target.innerText);
          }
        });
    $('.narrative.context:empty')
        .html(ractive.get('narrativeCtxtPrompt'));
    $('.narrative.performance:empty')
        .html(ractive.get('narrativePerfPrompt'));
  },
  isCcg: function() {
    if (ractive.get('surveyReturn')==undefined) return false;
    if (ractive.getAnswer('ORG_TYPE')=='Clinical Commissioning Group') return true;
    else return false;
  },
  isEClassUser: function() {
    // unclear what has changed but this used to report 0-E-CLASS but now reports true
    // cater for both
    var eClass = ractive.getAnswer('ECLASS_USER');
    if (eClass || (eClass!=undefined && eClass.charAt(0)==0)) return true;
    else return false;
  },
  oninit: function() {
  },
  renderLabel: function(qName) {
    if (ractive.get('labels.'+qName)!=undefined) return ractive.get('labels.'+qName);
    else return qName.toLabel();
  },
  renderCsv: function(ctrl, callback) {
    console.log('renderCsv: '+ctrl.id);
    var periods = parseInt(ctrl.getAttribute('data-periods'));
    var qs = ctrl.getAttribute('data-qs').split(',');
    var csv = 'Period,';
    for (var i = 0 ; i < qs.length ; i++) {
      csv += ractive.renderLabel(qs[i]);
      if (i+1 < qs.length) csv += ',';
      else csv += '\n';
    }
    var answers = ractive.get('surveyReturn.answers');
    for (var idx = 0 ; idx < periods ; idx++) {
      var period = ractive.getPeriod(idx-periods+1);
      csv += period;
      csv += ',';
      for (var i = 0 ; i < qs.length ; i++) {
        csv += ractive.getAnswerFromArray(qs[i], period, answers, true);
        if (i+1 < qs.length) csv += ',';
        else csv += '\n';
      }
    }

    var options = {};
    if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
    if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
    if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
    if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
    if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

    $(ctrl)
      .on('mouseover', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').show();
      })
      .on('mouseout', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').hide();
      });
    $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
    callback('#'+ctrl.id, csv, options);
  },
  renderCsvForPie: function(ctrl, callback) {
    console.log('renderCsvForPie: '+ctrl.id);
    var period = ractive.getPeriod(parseInt(1-ctrl.getAttribute('data-periods')));
    var qs = ctrl.getAttribute('data-qs').split(',');
    var answers = ractive.get('surveyReturn.answers');

    var csv = 'classification,percentage\n';
    for (var i = 0 ; i < qs.length ; i++) {
      csv += ractive.renderLabel(qs[i]);
      csv += ',';
      csv += ractive.getAnswerFromArray(qs[i], period, answers, true);
      csv += '\n';
    }

    var options = {};
    if ($(ctrl).data('colors') != undefined) options.colors = $(ctrl).data('colors').split(',');
    if ($(ctrl).data('labels') != undefined) options.labels = $(ctrl).data('labels');
    if ($(ctrl).data('other') != undefined) options.other = parseFloat($(ctrl).data('other'));
    if ($(ctrl).data('x-axis-label') != undefined) options.xAxisLabel = $(ctrl).data('x-axis-label');
    if ($(ctrl).data('y-axis-label') != undefined) options.yAxisLabel = $(ctrl).data('y-axis-label');

    $(ctrl)
      .on('mouseover', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').show();
      })
      .on('mouseout', function(ev) {
        $('#'+ev.currentTarget.id+' .controls').hide();
      });
    $('#'+ctrl.id+' .controls .kp-icon-new-tab').wrap('<a href="'+ractive.getServer()+$(ctrl).data('src')+'" target="_blank"></a>');
    callback('#'+ctrl.id, csv, options);
  },
  renderTable: function(d) {
    console.log('renderTable: '+d.id);
    var periods = parseInt(d.getAttribute('data-periods'));
    var qs = d.getAttribute('data-qs').split(',');
    var table = '<table class="table table-striped"><thead><tr><th>&nbsp;</th><th>&nbsp;</th>';
    for (var i = 1 ; i <= periods ; i++) {
      table += '<th class="number">'+ractive.getPeriod(i-periods)+'</th>';
    }
    table += '</tr><tbody>';
    for (var idx = 0 ; idx < qs.length ; idx++) {
      table += '<th>'+ractive.renderLabel(qs[idx])+'</th>';
      table += '<th class="legend '+qs[idx].toLowerCase()+'">&nbsp;</th>';
      for (var i = 1 ; i <= periods ; i++) {
        var ans = ractive.getAnswer(qs[idx], ractive.getPeriod(i-periods));
        table += '<td class="number">';
        table += ans == undefined ? 'n/a' : isNaN(ans) ? ans : parseFloat(ans).sigFigs(3);
        table += '</td>';
      }
      table += '</tr>';
    }

    table += '</tbody><tfoot>';
    if (d.getAttribute('data-total')=='true') {
      table += '<th>Total</th><th>&nbsp;</th>';
      for (var i = 1 ; i <= periods ; i++) {
        table += '<td class="number">'+ractive.total(qs, ractive.getPeriod(i-periods))+'</td>';
      }
    }
    if (d.hasAttribute('data-share')) {
      table += '<tr><td colspan="'+(periods+2)+'">'
            +'<a href="#" title="Copy link to this table" data-share="'+d.getAttribute('data-share')
            +'" onclick="return ractive.copyLink(event);" '
            +'class="pull-right no-print"><span class="glyphicon glyphicon-copy"></span>Copy</a>'
            +'<a href="'+d.getAttribute('data-share')+'" target="_blank" title="Open the table in a new window" '
            +'class="pull-right no-print"><span class="glyphicon glyphicon-share-alt"></span>Share</a>'
            +'</td></tr>';
    }
    table += '</tfoot></table>';
    $(d).empty().append(table);
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
          ractive.showMessage('A reset link has been sent to '+addr);
        },
      });
    } else {
      ractive.showError('Please enter the email address you registered with');
    }
  },
  restate: function () {
    console.info('restate...');
    $('#ajax-loader').show();
    var instanceToStart = {
      businessKey: 'Sustainability report '+ractive.get('surveyReturn.revision')+' for '+ractive.get('surveyReturn.org')+' '+ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'RestateSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: ractive.getProfile('firstName'),
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Restating your return, this may take a little while...');
    $.ajax({
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(instanceToStart),
      success: function(data, textStatus, jqXHR) {
        console.log('response: '+ jqXHR.status+", Location: "+jqXHR.getResponseHeader('Location'));
        ractive.showMessage('Created a new, draft revision of your return');
        $('#ajax-loader').hide();
        ractive.fetch();
      }
    });
  },
  saveNarrative: function(q, val) {
    console.info('saveNarrative of '+q+' as '+val);
    if (q!=undefined && val!=undefined && val.trim()!=ractive.get('narrativePrompt')) {
      $('.save-indicator').show();
      $.ajax({
        url: ractive.getServer()+'/returns/'+ractive.get('surveyReturn.id')+'/answers/'+q,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(val),
        success: function(data) {
          console.info('...saved');
          $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
          setTimeout(function() {
            $('.save-indicator').fadeOut(function() {
              $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
            });
          }, 3000);
        },
      });
    }
  },
  showReset: function() {
    $('#loginSect').slideUp();
    $('#resetSect').slideDown();
  },
  submit: function () {
    console.info('submit...');
    $('#ajax-loader').show();
    var instanceToStart = {
      businessKey: 'Sustainability report '+ractive.get('surveyReturn.revision')+' for '+ractive.get('surveyReturn.org')+' '+ractive.get('surveyReturn.applicablePeriod'),
      processDefinitionKey: 'SubmitSustainabilityReturn',
      processVariables: {
        applicablePeriod: ractive.get('surveyReturn.applicablePeriod'),
        initiator: ractive.getProfile().username,
        org: ractive.get('surveyReturn.org'),
        returnId: ractive.get('surveyReturn.id'),
        tenantId: ractive.get('tenant.id')
      }
    };
    ractive.showMessage('Submitting your return...');
    $.ajax({
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/process-instances/',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(instanceToStart),
      success: function(data, textStatus, jqXHR) {
        console.log('response: '+ jqXHR.status+", Location: "+jqXHR.getResponseHeader('Location'));
        ractive.showMessage('Your return has been received');
        $('#ajax-loader').hide();
        ractive.fetch();
      }
    });
  },
  toggleFieldHint: function(id) {
    console.log('toggleFieldHint');
    if ($('#'+id+'Hint:visible').length == 0) {
      $('#'+id+'Hint').slideDown(ractive.get('easingDuration')).removeClass('hidden');
    } else {
      $('#'+id+'Hint').slideUp(ractive.get('easingDuration'));
    }
  },
  total: function(qs, period) {
    var total = 0;
    var answers = ractive.get('surveyReturn.answers');
    for (i = 0 ; i < qs.length ; i++) {
      var val = ractive.getAnswerFromArray(qs[i], period, answers, true);
      if (!isNaN(val)) total += parseFloat(val);
    }
    if (total < 100) return total.sigFigs(3);
    else return total.formatDecimal(0);
  }
});

$(document).ready(function() {
  $('head').append('<link href="/sdu/css/sdu-1.0.0.css" rel="stylesheet">');
  $('.menu-burger, .toolbar').addClass('no-print');
})

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  //ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#answersTable tbody tr').length);
  }, 500);
});
ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});
