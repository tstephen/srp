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
 ******************************************************************************/
var $r = (function ($, ractive) {
  var me = {
    dirty: false,
    srp: new SrpClient({
      server: $env.server,
    }),
    rtn: undefined
  };
  var _isCcg = false;
  var _isIE = false;
  var _orgType;
  var _server = $env.server;
  var _survey;
  var _surveyPeriod = $env.period;
  var _now = new Date();
  var _params = getSearchParameters();
  var _period = 'p' in _params ? _params.p : _surveyPeriod;
  var _surveyName = 's' in _params ? _params.s : 'SDU-'+_period;

  function _bindLists() {
    if ($('#ORG_NAME')!=undefined && $('#ORG_NAME[list]').length!=0) $('#ORG_NAME').attr('list','orgs');
    if ($('#ORG_TYPE option')!=undefined && $('#ORG_TYPE option').length==0 && ractive.get('orgTypes')!=undefined) {
      ractive.addSelectOptions('#ORG_TYPE', ractive.get('orgTypes'), _orgType);
    }
    $('#CCG1_SERVED,#CCG2_SERVED,#CCG3_SERVED,#CCG4_SERVED,#CCG5_SERVED,#CCG6_SERVED,#CCG7_SERVED,#CCG8_SERVED').attr('list','orgs');
    $('#PROVIDER1_COMMISSIONED,#PROVIDER2_COMMISSIONED,#PROVIDER3_COMMISSIONED,#PROVIDER4_COMMISSIONED,#PROVIDER5_COMMISSIONED,#PROVIDER6_COMMISSIONED,#PROVIDER7_COMMISSIONED,#PROVIDER8_COMMISSIONED').attr('list','orgs');
  }

  function _disableHeadSections() {
    console.info('disableHeadSections');
    if ($('#Organisation h3 .title').length==0) {
      $('#Organisation h3').wrapInner('<span class="title"></span>').append(' - <em>Please enter these details only for the current year</em>');
      $('#Policy h3').wrapInner('<span class="title"></span>').append(' - <em>Please enter these details only for the current year</em>');
      $('#Performance h3').wrapInner('<span class="title"></span>').append(' - <em>Please enter these details only for the current year</em>');
      //$('#Organisation input,#Organisation select,#Policy input,#Policy select,#Performance input,#Performance select').removeAttr('readonly').removeAttr('disabled');
      $('#Organisation ol,#Policy ol,#Performance ol').hide();
    }
  }

  function _enableHeadSections() {
    console.info('enableHeadSections');
    $('#Organisation h3').empty().append('Organisation');
    $('#Policy h3').empty().append('Policy');
    $('#Performance h3').empty().append('Performance');
    //$('#Organisation input,#Organisation select,#Policy input,#Policy select,#Performance input,#Performance select').attr('readonly','readonly').attr('disabled','disabled');
    $('#Organisation ol,#Policy ol,#Performance ol').show();
  }

  function _fetchLists() {
    if ($r.getProfile() == undefined) {
      console.info('skip fetch lists while logging in');
      return;
    }
    $.getJSON('https://crm.knowprocess.com/sdu/accounts/', function(data) {
      ractive.set('orgs', data);
      ractive.addDataList({ name: 'orgs' },data);
      // if (_survey != undefined) $('#ORG_NAME').attr('list','orgs');
      _bindLists();
    });
    me.srp.fetchOrgTypes('sdu')
    .then(function(response) { return response.json(); })
    .then(function(data) {
      ractive.set('orgTypes', data);
      _bindLists();
    });
  }

  // load return (fetching blank if needed)
  function _fetchReturn() {
    if ($r.getProfile() == undefined) {
      console.info('skip fetch return while logging in');
      return;
    } else {
      // #262 WORKAROUND for login sect visibility being driven off this ractive data
      ractive.set('username', $r.getProfile().username);
    }

    me.srp.fetchReturn(_surveyName, $r.getProfileAttribute('org'))
        .then(function(response) { return response.json(); })
        .then(function(data) {
          me.rtn = data;
          if (_survey!=undefined) _fill(_survey);
        });
  }

  function _fill(survey) {
    _survey = survey;
    if ($r.rtn.status != 'Draft') {
      ractive.showError('This return has been submitted and cannot be changed. If you detect a problem you may create a new version from the report pages');
    }
    survey.categories.sort(function(a, b) {
      return a.id - b.id;
    });
    for(var i in survey.categories) {

      for(var j in survey.categories[i].questions) {
        console.log('  fill: '+survey.categories[i].questions[j].name);

        // reset question
        ractive.set('q.categories.'+i+'.questions.'+j+'.response', undefined);
        $('#'+survey.categories[i].questions[j].name).removeAttr('readonly').removeAttr('disabled');

        // fill answer
        for (var k in me.rtn.answers) {
          if (_period != me.rtn.answers[k].applicablePeriod) continue;
          if (me.rtn.answers[k].question.name==survey.categories[i].questions[j].name) {
            if (me.rtn.answers[k].question.type=='checkbox' && typeof me.rtn.answers[k].response == 'string') {
              me.rtn.answers[k].response = me.rtn.answers[k].response.split(',');
            }
            var idx = 0;
            switch (me.rtn.answers[k].question.name) {
            // special handling for organisation ...
            case 'ECLASS_USER':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              $('[data-id="ECLASS_USER"][value="'+me.rtn.answers[k].response+'"]').attr('checked','checked');
              break;
            case 'ORG_CODE':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', $r.getProfileAttribute('org'));
              $('#ORG_CODE').attr('readonly','readonly').attr('disabled','disabled');
              break;
            case 'ORG_NAME':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              if (me.rtn.answers[k].response != undefined) {
                $('#ORG_NAME').attr('readonly','readonly').attr('disabled','disabled');
              }
              break;
            case 'ORG_TYPE':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              if (me.rtn.answers[k].response == 'Clinical Commissioning Group') {
                _isCcg = true;
                console.warn('Detected org is a CCG: '+_isCcg);
                _hideNotApplicable();
              }
              $('#ORG_TYPE').attr('list','orgTypes');
              _orgType = me.rtn.answers[k].response;
              if ('Submitted'==me.rtn.answers[k].status || 'Published'==me.rtn.answers[k].status ||
		      me.rtn.answers[k].response != undefined) {
                $('#'+me.rtn.answers[k].question.name).attr('readonly','readonly').attr('disabled','disabled');
              }
              ractive.addSelectOptions('#ORG_TYPE', ractive.get('orgTypes'), _orgType);
              break;
            case 'PROC_SUPPLIER_SUSTAINABILITY':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              if (me.rtn.answers[k].response == undefined) break;
              for (idx = 0 ; idx < me.rtn.answers[k].response.length ; idx++) {
                $('[data-id="PROC_SUPPLIER_SUSTAINABILITY"][value="'+me.rtn.answers[k].response[idx]+'"]').attr('checked','checked');
              } 
              break;
            case 'SDG_CLEAR':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              if (me.rtn.answers[k].response == undefined) break;
              for (idx = 0 ; idx < me.rtn.answers[k].response.length ; idx++) {
                $('[data-id="SDG_CLEAR"][value="'+me.rtn.answers[k].response[idx]+'"]').attr('checked','checked');
              } 
              break;
            case 'SDG_STARTING':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              if (me.rtn.answers[k].response == undefined) break;
              for (idx = 0 ; idx < me.rtn.answers[k].response.length ; idx++) {
                $('[data-id="SDG_STARTING"][value="'+me.rtn.answers[k].response[idx]+'"]').attr('checked','checked');
              } 
              break;
            case 'SDMP_CRMP':
              ractive.set('q.categories.'+i+'.questions.'+j+'.response', me.rtn.answers[k].response);
              $('[data-id="SDMP_CRMP"][value="'+me.rtn.answers[k].response+'"]').attr('checked','checked');
              _toggleSdmp();
              break;
            default:
              if ('Submitted'==me.rtn.answers[k].status || 'Published'==me.rtn.answers[k].status) {
                $('#'+me.rtn.answers[k].question.name).attr('readonly','readonly').attr('disabled','disabled');
              } else {
                $('#'+me.rtn.answers[k].question.name).removeAttr('readonly').removeAttr('disabled');
              }
              // update ractive model with current value or default
              if (me.rtn.answers[k].response!=undefined && me.rtn.answers[k].response!='') {
                ractive.set('q.categories.'+i+'.questions.'+j+'.response', $r.getAnswer(me.rtn.answers[k].question.name, _period).response);
              } else if (ractive.get('q.categories.'+i+'.questions.'+j+'.defaultValue')!=undefined) {
                ractive.set('q.categories.'+i+'.questions.'+j+'.response', ractive.get('q.categories.'+i+'.questions.'+j+'.defaultValue'));
              } else {
                ractive.set('q.categories.'+i+'.questions.'+j+'.response', '');
              }
            }
            // Set status for all questions based on return status
            if ($r.rtn.status != 'Draft') {
              $('#questionnaireForm input').attr('readonly','readonly').attr('disabled','disabled');
            }

            // store answer that needs to receive updates
            //ractive.set('q.categories.'+i+'.questions.'+j+'.answer', me.rtn.answers[k]);
            break;
          }
        }
      }
    }
    _bindLists();

    $('#questionnaireForm input, #questionnaireForm select, #questionnaireForm textarea')
        .off().on('blur', function(ev) {
          if (!me.validate(ev.target)) {
            ractive.showError('Please select one of the available values for '+ev.target.id.toLabel());
            return false;
          }
          $r.dirty = true;
          var id = $(ev.target).data('id');
          if (id == undefined) id = ev.target.id;
          var answer = $r.getAnswer(id, _period);
          // #261 16 Mar 18: lazy initialisation of questions
          if (answer == undefined) {
            $.getJSON(_server+'/answers/findByReturnPeriodAndQ/'+$r.rtn.id+'/'+_period+'/'+id, function(answer) {
              $r.rtn.answers.push(answer);
              answer.response = ev.target.value;
              me.saveAnswer(answer);
            });
          } else {
            answer.response = ev.target.value;
            me.saveAnswer(answer);
          }
    });
    // Set questionnaire details specific to SDU return
    ractive.set('q.about.title', 'SDU return '+_period);
    ractive.set('q.about.options.previous', '$r.movePrevious()');
    ractive.set('q.about.options.next', '$r.moveNext()');
    ractive.set('q.about.options.finalSubmitButton', true);
    $('[data-id="ECLASS_USER"]').off().on('blur', function(ev) {
      var answer = $r.getAnswer('ECLASS_USER', _period);
      if (answer.response == null) answer.response = [];
      answer.response = ev.target.value;
      $r.dirty = true;
      $r.saveAnswer(answer);
    });
    $('[data-id="PROC_SUPPLIER_SUSTAINABILITY"]').off().on('change', function(ev) {
      var answer = $r.getAnswer('PROC_SUPPLIER_SUSTAINABILITY', _period);
      if (answer == undefined) answer = {
        revision: 1,
        question: { id: 340, name: 'PROC_SUPPLIER_SUSTAINABILITY' },
        applicablePeriod: _period,
        status: 'Draft'
      };
      if (answer.response == null) answer.response = [];
      if (ev.target.checked) answer.response.push(ev.target.value);
      else answer.response.splice(answer.response.indexOf(ev.target.value), 1);
      $r.dirty = true;
      $r.saveAnswer(answer);
    });
    $('[data-id="SDG_CLEAR"]').off().on('change', function(ev) {
      var answer = $r.getAnswer('SDG_CLEAR', _period);
      if (answer.response == null) answer.response = [];
      if (ev.target.checked) answer.response.push(ev.target.value);
      else answer.response.splice(answer.response.indexOf(ev.target.value), 1);
      $r.dirty = true;
      $r.saveAnswer(answer);
    });
    $('[data-id="SDG_STARTING"]').off().on('change', function(ev) {
      var answer = $r.getAnswer('SDG_STARTING', _period);
      if (answer.response == null) answer.response = [];
      if (ev.target.checked) answer.response.push(ev.target.value);
      else answer.response.splice(answer.response.indexOf(ev.target.value), 1);
      $r.dirty = true;
      $r.saveAnswer(answer);
    });
    $('[data-id="SDMP_CRMP"]').off().on('blur', function(ev) {
      var answer = $r.getAnswer('SDMP_CRMP', _period);
      answer.response = ev.target.value;
      $r.dirty = true;
      $r.saveAnswer(answer);
      _toggleSdmp();
    });
  }

  /**
   * Last categories are calculations that need to be hidden.
   */
  function _hideCalcs() {
    var no2Hide = 1;
    ractive.splice('q.categories', ractive.get('q.categories').length-no2Hide, no2Hide);
  }

  /**
   * Adapt questions according to whether CCG or provider.
   */
  function _hideNotApplicable() {
    console.info('hideNotApplicable'+_isCcg);
    if (_isCcg) {
      $('#CCG1_SERVED,#CCG2_SERVED,#CCG3_SERVED,#CCG4_SERVED,#CCG5_SERVED,#CCG6_SERVED,#CCG7_SERVED,#CCG8_SERVED,#NO_PATIENT_CONTACTS,#PATIENT_CONTACT_MEASURE,#DESFLURANE,#ISOFLURANE,#SEVOFLURANE,#NITROUS_OXIDE,#PORTABLE_NITROUS_OXIDE_MIX,#PORTABLE_NITROUS_OXIDE_MIX_MATERNITY,#CHP_ELECTRICAL_OUTPUT,#EXPORTED_THERMAL_ENERGY,#WOOD_LOGS_OWNED_RENEWABLE_CONSUMPTION,#WOOD_CHIPS_OWNED_RENEWABLE_CONSUMPTION,#WOOD_PELLETS_OWNED_RENEWABLE_CONSUMPTION,#ELEC_OWNED_RENEWABLE_CONSUMPTION,#OCCUPIED_BEDS,#NO_BEDS,#NO_PATIENT_CONTACTS,#PATIENT_CONTACT_MEASURE').parent().parent().hide();
      for (var idx in ractive.get('q.categories')) {
        if (ractive.get('q.categories.'+idx+'.name')=='Gases') {
          ractive.splice('q.categories', idx, 1);
        }
      }
    } else {
      $('#PROVIDER1_COMMISSIONED,#PROVIDER2_COMMISSIONED,#PROVIDER3_COMMISSIONED,#PROVIDER4_COMMISSIONED,#PROVIDER5_COMMISSIONED,#PROVIDER6_COMMISSIONED,#PROVIDER7_COMMISSIONED,#PROVIDER8_COMMISSIONED').parent().parent().hide();
    }
    if (_isIE) {
      $('.help-block').removeClass('hidden');
      $('.previous').empty().append('&lt; Previous').addClass('btn');
      $('.next').empty().append('Next &gt;').addClass('btn pull-right');
    } else {
      console.info('No need for IE workarounds');
    }
  }

  function _notifyParent() {
    console.info('notify parent to resize');
    /* jshint -W069 */
    if (parent!=undefined && parent['notifyClick']!=undefined) {
      parent.notifyClick();
      // allow time for notify click to scroll to top and for iframe to calc new height
      setTimeout(function() {
        //parent.alert(''+$('#containerSect').height());
        parent.notifyIFrameSize($('#containerSect').height());
      },400);
    }
    /* jshint +W069 */
  }

  function _toggleSdmp() {
    var answer = $r.getAnswer('SDMP_CRMP', _period);
    if (answer.response=='true') {
      $('section#Policy.category li:hidden').slideDown();
    } else {
      $('section#Policy.category li:gt(0):lt(3)').slideUp();
    }
  }

  me.complete = function() {
    ractive.showMessage("Redirecting to your report... Don't forget to submit it when you're done. <br/>Unsubmitted reports will be submited automatically on 1st October after year end.");
    setTimeout(function() {
      window.location.href = '/srp/report-2020-21.html';
    },5000);
  };

  me.loginSuccessful = function() {
    ractive.fetch(); // delegate to questionnaire to fetch survey
    if (me.rtn == undefined) _fetchReturn();
    if (ractive.get('orgTypes') == undefined) _fetchLists();
    _showQuestionnaire();
  };

  function _showQuestionnaire() {
    $('section.questionnaire').slideDown();
    ractive.set('q.activeCategory',0);
  }

  me.fill = function(survey) {
    console.info('fill survey');
    // Don't know if we'll get survey or return first
    if (me.rtn==undefined) _survey = survey;
    else _fill(survey);

  };

  me.getAnswer = function(qName,period) {
    if ($r.rtn!=undefined) {
      for (var idx in $r.rtn.answers) {
        if ($r.rtn.answers[idx].question.name == qName && $r.rtn.answers[idx].applicablePeriod == period) {
          return $r.rtn.answers[idx];
        }
      }
    }
  };

  me.getProfile = function() {
    var profile = localStorage.getItem('profile');
    if (profile == undefined) {
      alert('Unable to authenticate you at the moment, please try later');
      return;
    } else {
      return JSON.parse(profile);
    }
  };

  me.getProfileAttribute = function(attr) {
    if (Array.isArray(me.getProfile().attributes[attr])) {
      return me.getProfile().attributes[attr][0];
    } else {
      return me.getProfile().attributes[attr];
    }
  };

  me.getSurveyName = function() {
    return _surveyName;
  };

  me.moveNext = function() {
    console.info('_moveNext');
    var currentYear = parseInt(_period.substring(0,4));
    if (currentYear >= _now.getFullYear()) {
      ractive.showError('You cannot enter data for the future');
      $('.next').hide();
      return;
    }
    var yearEnd = (currentYear+2-2000);
    _period = (currentYear+1)+'-'+(yearEnd < 10 ? '0'+yearEnd : yearEnd);
    ractive.set('q.about.title', 'SDU return '+_period);
    _fill(_survey);
    if (_period == _surveyPeriod) _enableHeadSections();
    else _disableHeadSections();
  };

  me.movePrevious = function() {
    console.info('_movePrevious');
    $('.next').show();
    var currentYear = _period.substring(0,4);
    var yearEnd = (currentYear-2000);
    _period = (currentYear-1)+'-'+(yearEnd < 10 ? '0'+yearEnd : yearEnd);
    ractive.set('q.about.title', 'SDU return '+_period);
    _fill(_survey);
    if (_period == _surveyPeriod) _enableHeadSections();
    else _disableHeadSections();
  };

  me.saveAnswer = function(answer) {
    if ($r.dirty == false || $r.rtn.status != 'Draft' || $r.getProfile() == undefined) {
      console.info('skip save, dirty: '+$r.dirty+', profile: '+$r.getProfile());
      return;
    } else if (me.rtn == undefined) {
      ractive.fetch();
      _fetchReturn();
    }

    var response = answer.response;
    if (answer.question.type == 'radio' && Array.isArray(answer.response)) {
      response = answer.response.join();
    }

    $('.save-indicator').show();
    me.srp.saveAnswer(me.rtn, answer, function() {
      console.log('updated ok');
      $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
      setTimeout(function() {
        $('.save-indicator').fadeOut(function() {
          $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
        });
      }, 3000);
      $r.dirty = false;
    });
  };

  me.submit = function() {
    if ($r.dirty == false || $r.rtn.status != 'Draft' || $r.getProfile() == undefined) {
      console.info('skip save, dirty: '+$r.dirty+', profile: '+$r.getProfile());
      return;
    } else if (me.rtn == undefined) {
      ractive.fetch();
      _fetchReturn();
    }
    // _fill(_survey);
    $.each(me.rtn.links, function (i,d) {
      if (d.rel=='self') me.rtn.selfRef = d.href;
    });
    if (_server.indexOf('v3.srp.digital')!=-1) {
      me.rtn.selfRef = me.rtn.selfRef.replace(/localhost/, 'v3.srp.digital');
    }
    // handle checkbox options
    for (var idx = 0 ; idx< me.rtn.answers.length ; idx++) {
      // console.warn(idx);
      if (Array.isArray(me.rtn.answers[idx].response)) me.rtn.answers[idx].response = me.rtn.answers[idx].response.join();
      if ('optionNames' in me.rtn.answers[idx].question) {
        delete me.rtn.answers[idx].question.optionNames;
      }
    }
    $('.save-indicator').show();
    return $.ajax({
        url: me.rtn.selfRef,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(me.rtn),
        dataType:'text',
        success: function(data, textStatus) {
          console.log('updated '+ textStatus +', data: '+ data);
          $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
          setTimeout(function() {
            $('.save-indicator').fadeOut(function() {
              $('.save-indicator span').toggleClass('save-indicator-animation glyphicon-save glyphicon-saved');
            });
          }, 3000);
          $r.dirty = false;
        }
      });
  };

  me.validate = function(ctrl) {
    console.info('validate '+ctrl.id);
    if (ctrl.getAttribute('list')!=undefined && ctrl.value != undefined && ctrl.value != '') {
      var org = ractive.get(ctrl.getAttribute('list')).find(function(element) {
        return element.name == ctrl.value;
      });
      if (org == undefined) {
        console.error('selected illegal value for '+ctrl.id);
        ctrl.setCustomValidity("Please select from the existing values");
      } else {
        ctrl.setCustomValidity("");
      }
    }
    console.log('ctrl is valid: '+$(ctrl).is(':valid'));
    return $(ctrl).is(':valid');
  };

  ractive.observe('q.activeCategory', function (newValue, oldValue, keypath) {
    console.log("'"+keypath+"' changing from '"+oldValue+"' to '"+newValue+"'");
    if (newValue!=oldValue) {
      _hideNotApplicable();
      _notifyParent();
    }
  });

  ractive.observe('q.categories.*.questions.*.response', function(newValue, oldValue, keypath) {
    if (newValue === oldValue) return;
    console.log("'"+keypath+"' changing from '"+oldValue+"' to '"+newValue+"'");
    ractive.get(keypath.substring(0, keypath.indexOf('.response')));
    // after #226 not sure if this is still needed
    if ($r.rtn!=undefined && oldValue!=undefined && oldValue!='') {
      //var found = false;
      /*for (idx in $r.rtn.answers) {
        //if (found) break;
        if ($r.rtn.answers[idx].question.name == q.name && $r.rtn.answers[idx].applicablePeriod == _period) {
          $r.dirty = true;
          $r.rtn.answers[idx].response = newValue;
          $r.saveAnswer($r.rtn.answers[idx]);
          break;
          //found = true;
        }
      }*/
          $r.dirty = true;
    }
  });

  // Correct std partial paths
  ractive.set('stdPartials', [
      { "name": "questionnaire", "url": "/questionnaire/partials/questionnaire.html"},
      { "name": "questionnaireContact", "url": "/questionnaire/partials/questionnaire-contact.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"}
    ]);
  ractive.loadStandardPartials(ractive.get('stdPartials'));

  $('head').append('<link href="/sdu/css/sdu-1.0.0.css" rel="stylesheet">');
  $('head').append('<link rel="icon" type="image/png" href="/srp/images/icon/sdu-icon-16x16.png">');

  if (!('fetchCallbacks' in ractive)) ractive.fetchCallbacks = $.Callbacks();
  ractive.fetchCallbacks.add(me.fill);
  ractive.fetchCallbacks.add(_hideCalcs);
  ractive.fetchCallbacks.add(_showQuestionnaire);
  ractive.toggleSidebar = function() {
    console.info('toggleSidebar');
    $('.toolbar-left').toggle(ractive.get('easingDuration'));
  };
  ractive.toggleSubMenu = function(entry) {
    console.info('toggleSubMenu');
    $(entry.nextElementSibling).toggle(ractive.get('easingDuration'));
  };

  try { _isIE = navigator.userAgent.match(/trident/i)!=null; } catch (e) {
    document.write('<script src="ie-polyfill.js"><\/script>'); // jshint ignore:line
  }

  return me;
}($, ractive));

$(document).ready(function() {
  ractive.sendMessage = $r.complete;

  $r.keycloak = Keycloak('/keycloak.json');
  $r.keycloak.init({ onLoad: 'login-required' }).then(function(authenticated) {
    console.info(authenticated ? 'authenticated' : 'not authenticated');
    if ('srp' in $r) $r.srp.options.token = $r.keycloak.token;
    // now safe to set and load questionnaire
    ractive.set('questionnaireDef',$env.server+'/surveys/findByName/'+$r.getSurveyName());
    localStorage.setItem('token', $r.keycloak.token); // used by questionnaire

    $r.keycloak.loadUserProfile().then(function(profile) {
      localStorage.setItem('profile', JSON.stringify(profile));
      $r.loginSuccessful();
    }).catch(function(e) {
      alert('Failed to load user profile: '+e);
    });
  }).catch(function(e) {
    console.error('failed to initialize: '+e);
  });

});
