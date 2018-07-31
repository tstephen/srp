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
        'CCG2_SERVED','CCG3_SERVED','CCG4_SERVED','CCG5_SERVED','CCG6_SERVED'],
    requiredAnswers: ['ORG_CODE', 'ORG_NAME', 'ORG_TYPE', 'SDMP_CRMP', 'HEALTHY_TRANSPORT_PLAN', 'PROMOTE_HEALTHY_TRAVEL'],
    period: '2017-18',
    server: $env.server,
    survey: 'SDU-2017-18',
    tenant: { id: 'sdu' },
    username: localStorage['username'],
    formatAnswer: function(qName) {
      if (qName==undefined || ractive.get('surveyReturn')==undefined) return '';
      else {
        try {
          var answer = ractive.getAnswer(qName);
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
    isCcg: function() {
      return ractive.isCcg();
    },
    isEClassUser: function() {
      return ractive.isEClassUser();
    },
    stdPartials: [
      { "name": "loginSect", "url": $env.server+"/webjars/auth/1.1.0/partials/login-sect.html"},
      { "name": "statusSect", "url": "/srp/vsn/partials/status-sect.html"}
    ],
  },
  partials: {
    'loginSect': '',
    'shareCtrl': '<div class="controls pull-right" style="display:none">'
                +'  <span class="glyphicon icon-btn kp-icon-share"></span>'
                +'  <!--span class="glyphicon icon-btn kp-icon-link"></span-->'
                +'  <!--span class="glyphicon icon-btn kp-icon-copy"></span-->'
                +'</div>'
  },
  calculate: function () {
    console.info('calculate...');
    $('.btn-calc,.btn-refresh,.btn-submit').attr('disabled','disabled');
    $('#ajax-loader').show();
    $.ajax({
      dataType: "json",
      type: 'POST',
      url: ractive.getServer()+'/calculations/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
        "Cache-Control": "no-cache"
      },
      success: ractive.fetchSuccessHandler
    });
  },
  enter: function () {
    console.info('enter...');
  },
  fetch: function() {
    console.info('fetch...');
    if ($auth.getClaim('org') == undefined) return;
    ractive.set('org',$auth.getClaim('org'));
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/returns/findCurrentBySurveyNameAndOrg/'+ractive.get('survey')+'/'+ractive.get('org'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
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
        "X-Authorization": "Bearer "+localStorage['token'],
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
  fetchOrgData: function() {
    console.info('fetchOrgData');
    $( "#ajax-loader" ).show();
    ractive.set('saveObserver', false);
    ractive.set('surveyReturn.orgs', []);
    for (var idx = 0 ; idx < ractive.get('orgAnswerNames').length ; idx++) {
      try {
        var org = ractive.getAnswer(ractive.get('orgAnswerNames')[idx]);
        if (org == undefined || org.trim().length==0) continue;
        $.ajax({
          dataType: "json",
          url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/accounts/findByName/'+encodeURIComponent(org),
          crossDomain: true,
          headers: {
            "X-Requested-With": "XMLHttpRequest",
            "X-Authorization": "Bearer "+localStorage['token'],
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
    $('.rpt.stacked').each(function(i,d) {
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
      ractive.fetchCsv(d, renderStacked);
    });
    $('.rpt.table').each(function(i,d) {
      ractive.fetchTable(d);
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
    ractive.set('saveObserver', true);
  },
  fetchTable: function(ctrl) {
    $.ajax({
      dataType: "html",
      url: ractive.getServer()+$(ctrl).data('src'),
      crossDomain: true,
      headers: {
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+localStorage['token'],
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
    if (period == undefined) period = ractive.get('period');
    var answers = ractive.get('surveyReturn.answers');
    return ractive.getAnswerFromArray(qName, period, answers);
  },
  getAnswerFromArray: function(qName, period, answers) {
    for (var idx = 0 ; idx < answers.length ; idx++) {
      var a = answers[idx];
      if (a.question.name == qName && a.applicablePeriod == period && a.response=='true') {
        return true;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.response=='false') {
        return false;
      } else if (a.question.name == qName && a.applicablePeriod == period && a.question.type == 'number') {
        return parseFloat(a.response).sigFigs(3);
      } else if (a.question.name == qName && a.applicablePeriod == period) {
        return a.response;
      }
    }
    return undefined;
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
    if (ractive.getAnswer('ECLASS_USER')!=undefined && ractive.getAnswer('ECLASS_USER').charAt(0)==0) return true;
    else return false;
  },
  oninit: function() {
    /* TODO this works but is too late to affect data load
    $.getJSON($env.server+'/sdu/parameters/REPORTING_PERIOD', function(data) {
      ractive.set('period', data.value);
      ractive.set('survey', 'SDU-'+data.value);
    });*/
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
        initiator: $auth.getClaim('sub'),
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
        initiator: $auth.getClaim('sub'),
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
  }
});

$(document).ready(function() {
  $('head').append('<link href="'+ractive.getServer()+'/css/sdu-1.0.0.css" rel="stylesheet">');

  if (Object.keys(getSearchParameters()).indexOf('error')!=-1) {
    ractive.showError('The username and password provided do not match a valid account');
  } else if (Object.keys(getSearchParameters()).indexOf('logout')!=-1) {
    ractive.showMessage('You have been successfully logged out');
  }
  $auth.addLoginCallback(ractive.fetch);
  $auth.addLoginCallback(ractive.getProfile);
})
