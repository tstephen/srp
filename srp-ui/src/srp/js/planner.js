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
var EASING_DURATION = 500;
var fadeOutMessages = true;
var ractive = new BaseRactive({
  el: 'container',
  template: '#template',
  data: {
    server: $env.server,
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    commissioningOrganisationTypes: [],
    display000s: false,
    healthWarning:'This resource is intended to provide an overview of carbon and cost reduction opportunities, as well as a framework within which users can develop their own analysis. The figures are derived from specific case studies and as such will not be equally applicable for every organisation. Developing local business cases will require local technical and economic assessments. This tool identifies the potential opportunities, interventions to investigate and scale of savings and is not a substitute for the usual financial analysis required to assemble a case for investment.',
    organisationTypes: [],
    // TODO fetch dynamically
    parameters: { targetYear:2020 },
    scaleTypes: [ { idx: 0, name: "Linear" },{ idx: 1, name: "Square Root" } ],
    haveOverlaps: function(orgIntvns) {
      console.info('haveOverlaps:'+orgIntvns.length);
      var haveOverlaps = 0;
      var baseMsg = '<p>There are overlapping interventions shown in the \
        curve, further detailed analysis would be needed to understand the \
        magnitude of the overlap. Below follows a list of the interventions \
        affected and the interventions that overlaps with each.</p>';
      var applicableIntvnNames = orgIntvns.map(function(obj) {
        return obj.name;
     });
      var specificMsgs = '<ul>';
      $.each(orgIntvns, function(i,d) {
        if (d.overlappingInterventionList!=undefined && d.overlappingInterventionList.length>0) {
          haveOverlaps++;
          specificMsgs += '<li><b>'+d.name+':</b> ';
          specificMsgs += d.overlappingInterventionList.reduce(function(previousValue, currentValue, currentIndex, array) {
            if (applicableIntvnNames.indexOf(currentValue)!=-1)
              return previousValue += (', '+currentValue);
            else
              return previousValue;
          });
        }
      });

      ractive.set('overlapCount',haveOverlaps);
      return haveOverlaps > 0 ? baseMsg+specificMsgs : '';
    },
    formatForDomain: function(number) {
      if (ractive.get('display000s')) {
        return (number/1000).sigFigs(3);
      } else {
        return number.sigFigs(3);
      }
    },
    showAddUserIntervention: true,
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
      console.info('sorted');
      if (ractive.get('sortColumn') == column && ractive.get('sortAsc')) return 'sort-asc';
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      { "name": "sustainabilityInterventionPlanner", "url": "../vsn/partials/planner.html"},
      { "name": "maccOptionsSect", "url": "../vsn/partials/macc-options-sect.html"},
      { "name": "maccDisplaySect", "url": "../vsn/partials/macc-display-sect.html"},
      { "name": "macTableDisplaySect", "url": "../vsn/partials/mac-table-sect.html"},
      { "name": "userInterventionModal", "url": "../vsn/partials/planner-user-intervention-modal.html"}
    ],
    stepIndex: 0,
    sum: function(column) {
      console.log('sum of column: '+column);
      if (ractive.get('interventions').length==0) return;
      var interventions = ractive.get('interventions');
      var sum = 0;
      for (i = 0 ; i < interventions.length ; i++) {
        sum += interventions[i][column];
      }
      return sum;
    },
    tenant: {id: 'sdu'},
    title: 'Healthy Returns by 2020',
    username: localStorage['username'],
    yNegLimit:-100,
    yPosLimit:500
  },
  addUserIntervention: function() {
    console.info('addUserIntervention');

    ractive.set('currentUserIntervention.lifetime',parseInt(ractive.get('currentUserIntervention.lifetime')));
    ractive.set('currentUserIntervention.cashOutflowsUpFront',parseInt(ractive.get('currentUserIntervention.cashOutflowsUpFront')));
    ractive.set('currentUserIntervention.annualCashOutflowsTargetYear',parseInt(ractive.get('currentUserIntervention.annualCashOutflowsTargetYear')));
    ractive.set('currentUserIntervention.annualCashInflowsTargetYear',parseInt(ractive.get('currentUserIntervention.annualCashInflowsTargetYear')));
    ractive.set('currentUserIntervention.costPerTonneCo2e',parseInt(ractive.get('currentUserIntervention.costPerTonneCo2e')));
    ractive.set('currentUserIntervention.tonnesCo2eSavedTargetYear',parseInt(ractive.get('currentUserIntervention.tonnesCo2eSavedTargetYear')));

    ractive.get('interventions').push(ractive.get('currentUserIntervention'));

    ractive.recordPlan();

    $('#userInterventionModal').modal('hide');
  },
  existingInterventions: function() {
    console.info('existingInterventions');
  },
  fetch: function() {
    console.info('fetch');
    macc.options.targetYear = ractive.get('parameters.targetYear');
    ractive.fetchOrganisationTypes();
    if (ractive.hasRole('admin')) $('.admin').show();
    if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
  },
  fetchPlan: function() {
    console.info('fetchPlan...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer() + '/' + ractive.get('tenant.id') + '/accounts/'+ractive.getOrg(),
      crossDomain: true,
      success: function(data) {
        ractive.set('account', data);
        ractive.set('current.abatementPlanBy',ractive.getUsername());
        // ractive.set('current.existingInterventions',data.customFields.existingInterventions.split());
        // var fieldNames = Object.keys(data.customFields);
        // for (var idx in fieldNames) {
        //   ractive.set('current.characteristics.'+idx+'.unit',fieldNames[idx]);
        //   ractive.set('current.characteristics.'+idx+'.unitCount',data.customFields[fieldNames[idx]]);
        // }
        ractive.set('saveObserver', true);
      }
    });
  },
  fetchOrganisationInterventions: function() {
    console.info('fetchOrganisationInterventions');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-interventions/plan/'+ractive.get('current.orgTypeName'),
      crossDomain: true,
      success: function( data ) {
        if (data['_embedded'] == undefined) {
          ractive.set('interventions', data.sort(ractive.sortByName));
        }else{
          ractive.set('interventions', data['_embedded'].interventions.sort(ractive.sortByName));
        }
        ractive.set('current.existingInterventions',[]);
        ractive.set('current.characteristics',ractive.uniqUnits());

        if (ractive.get('account')!=undefined) {
          var existing = ractive.get('current.customFields.existingInterventions');
          if (existing != undefined) ractive.set('current.existingInterventions',existing.split());
          var fields = ractive.get('current.characteristics');
          for (var idx in fields) {
            var val = ractive.get('account.customFields')[ractive.get('current.characteristics.'+idx+'.unit')];
            if (val!=undefined) ractive.set('current.characteristics.'+idx+'.unitCount',val);
          }
        }
        // take a copy of what we showed...
        // TODO except it gets updated too...
//        ractive.set('current.defaultInterventions', ractive.get('interventions').slice());
//        ractive.set('current.defaultCharacteristics',ractive.get('current.characteristics').slice());

        ractive.set('saveObserver', true);
      }
    });
  },
  fetchOrganisationTypes: function() {
    console.info('fetchOrganisationTypes');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-types/status/green',
      crossDomain: true,
      success: function( data ) {
        if (data['_embedded'] == undefined) {
          ractive.merge('organisationTypes', data.map(function(d) {
            return d.commissioner==false ? d : undefined;
          }).clean());
          ractive.merge('commissioningOrganisationTypes', data.map(function(d) {
            return d.commissioner==true ? d : undefined;
          }).clean());
        }else{
          ractive.merge('organisationTypes', data['_embedded'].organisationTypes.map(function(d) {
            return d.commissioner==false ? d : undefined;
          }).clean());
          ractive.merge('commissioningOrganisationTypes', data['_embedded'].organisationTypes.map(function(d) {
            return d.commissioner==true ? d : undefined;
          }).clean());
        }
        ractive.set('saveObserver', true);
      }
    });
  },
  getOrg: function() {
    return ractive.keycloak.profile.attributes['org'];
  },
  getUsername: function() {
    return ractive.keycloak.profile.username;
  },
  initControls: function() {
    console.info('initControls');
//    ractive.initAutoComplete();
//    ractive.initAutoNumeric();
//    ractive.initDatepicker();
  },
  replaceGraph: function() {
    console.info('replaceGraph: ');
    $('#result').fadeOut(1000, function() {
      macc.displayDataSet('#result',ractive.get('interventions'));
      $('#result').fadeIn(1000);
    });
  },
  recordPlan: function() {
    console.info('recordPlan');
    if (ractive.getUsername()!=undefined) {
      var tmp = JSON.parse(JSON.stringify(ractive.get('current')));
      tmp.abatementPlanBy = ractive.getUsername();
      tmp.tenantId = ractive.get('tenant.id');
      tmp.orgCode = ractive.getOrg();
      tmp.existingInterventions = (tmp.existingInterventions == undefined ? '' : tmp.existingInterventions.join());
      for (var idx in tmp.characteristics) {
        tmp[tmp.characteristics[idx].unit] = tmp.characteristics[idx].unitCount;
      }
      delete tmp.characteristics;
      $.ajax({
        url: ractive.getServer()+'/msg/'+ractive.get('tenant.id')+'/srp.abatementplan.json',
        type: 'POST',
        data: { json: JSON.stringify(tmp) },
        success: completeHandler = function(data) {
          console.log('submitted plan, response was: '+ data);
        }
      });
    }
  },
  review: function() {
    console.info('review');

    for (var i in ractive.get('interventions')) {
      for (var j in ractive.get('current.characteristics')) {
        if (ractive.get('current.characteristics.'+j+'.unit')==ractive.get('interventions.'+i+'.unit')) {
          var tmp = (ractive.get('interventions.'+i+'.annualCashInflowsTargetYear')*ractive.get('current.characteristics.'+j+'.unitCount'))/ractive.get('interventions.'+i+'.unitCount');
          ractive.set('interventions.'+i+'.annualCashInflowsTargetYear', tmp);
          tmp = (ractive.get('interventions.'+i+'.tonnesCo2eSavedTargetYear')*ractive.get('current.characteristics.'+j+'.unitCount'))/ractive.get('interventions.'+i+'.unitCount');
          ractive.set('interventions.'+i+'.tonnesCo2eSavedTargetYear', tmp);
          ractive.set('interventions.'+i+'.unitCount', ractive.get('current.characteristics.'+j+'.unitCount'));
        }
      }

      for (var j in ractive.get('current.existingInterventions')) {
        if (ractive.get('current.existingInterventions.'+j)==ractive.get('interventions.'+i+'.name')) {
          ractive.splice('interventions',i,1);
        }
      }
    }

    ractive.recordPlan();

    // re-sort for MAC order
    ractive.set('interventions', ractive.get('interventions').sort(ractive.sortByCostPerTonneCo2e));

    $('#result').fadeOut(1000, function() {
      macc.displayDataSet('#result',ractive.get('interventions'));
      $('#result').fadeIn(1000);
    });
  },
  showUserInterventionModal: function() {
    console.info('showUserInterventionModal');
    $('#userInterventionModal').modal('show');
  },
  setScaleFunc: function(idx) {
    console.info('setScaleFunc');
    switch(idx) {
    case 1:
      macc.options.scaleFunc = d3.scale.sqrt;
      break;
    case 2:
      macc.options.scaleFunc = d3.scale.log;
      break;
    default:
      macc.options.scaleFunc = d3.scale.linear;
      break;
    }
    ractive.replaceGraph();
  },
  setYNegLimit: function(limit) {
    console.info('setYNegLimit:'+limit);
    macc.options.yNegLimit = limit;
    ractive.replaceGraph();
  },
  setYPosLimit: function(limit) {
    console.info('setYPosLimit:'+limit);
    macc.options.yPosLimit = limit;
    ractive.replaceGraph();
  },
  select: function(obj) {
    console.info('select: '+obj.selfRef);
    $('.tr-large-icon-selected').removeClass('tr-large-icon-selected');
    $('[data-id="'+obj.selfRef+'"]').addClass('tr-large-icon-selected');
    ractive.set('current',{ organisationType: obj.selfRef, orgTypeName: obj.name });
    ractive.showStep('tailor');
  },
  showStep: function(name) {
    console.info('showStep');
    ractive.set('step',name);
    $('.wizard-step a[href="#'+name+'"]').click();
    if (typeof ractive[name]=='function') ractive[name]();
  },
  sortByCostPerTonneCo2e: function (a, b) {
    if (a.costPerTonneCo2e > b.costPerTonneCo2e) {
      return 1;
    }
    if (a.costPerTonneCo2e < b.costPerTonneCo2e) {
      return -1;
    }
    // a must be equal to b
    return 0;
  },
  sortByName: function (a, b) {
    if (a.name > b.name) {
      return 1;
    }
    if (a.name < b.name) {
      return -1;
    }
    // a must be equal to b
    return 0;
  },
  tailor: function() {
    console.info('tailor');
    if (ractive.get('current')==undefined) {
      ractive.showError('Please select the organisation type most similar to your own.');
      ractive.showStep('start');
    } else {
      ractive.fetchOrganisationInterventions();
    }
  },
  uniqUnits: function() {
    console.info('uniqUnits');

    var seen = {};
    var units = [];
    for (var i=0 ; i<ractive.get('interventions').length ; i++) {
      var item = ractive.get('interventions')[i]['unit'];
      if(seen[item] !== 1) {
        seen[item] = 1;
        units.push({
          unit: ractive.get('interventions')[i]['unit'],
          unitCount: ractive.get('interventions')[i]['unitCount'],
          unitDescription: ractive.get('interventions')[i]['unitDescription']
        });
      }
    }

    return units;
  },
  updateExistingInterventions: function(interventionName) {
    console.info('updateExistingInterventions');
    ractive.get('current.existingInterventions').push(interventionName);
    ractive.updatePlan('existingInterventions');
  },
  updatePlan: function(step) {
    console.log('updatePlan');
    ractive.set('step',step);
  }
});

ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});
ractive.observe('yNegLimit', function(newValue, oldValue, keypath) {
  if (newValue!=undefined && newValue!='') {
    ractive.setYNegLimit(newValue);
  }
});
ractive.observe('yPosLimit', function(newValue, oldValue, keypath) {
  if (newValue!=undefined && newValue!='') {
    ractive.setYPosLimit(newValue);
  }
});

$(document).ready(function() {
  $('head').append('<link href="/sdu/css/sdu-1.0.0.css" rel="stylesheet">');

  ractive.fetchOrganisationTypes();
})
