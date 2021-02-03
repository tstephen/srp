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
fadeOutMessages = true;
var newLineRegEx = /\n/g;

var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    filter: { },
    server: $env.server,
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
    hash: function(email) {
      if (email == undefined) return '';
      //console.log('hash '+email+' = '+ractive.hash(email));
      return '<img class="img-rounded" src="//www.gravatar.com/avatar/'+ractive.hash(email)+'?s=36"/>'
    },
    display000s: true,
    commissioningOrganisationTypes: [],
    interventions: [],
    optModellingYear: 2010,
    organisationTypes: [],
    // TODO fetch dynamically
    parameters: { targetYear:2020 },
    modellingYear: [ { idx: 0, name: "2010" },{ idx: 1, name: "2015" } ],
    scaleTypes: [ { idx: 0, name: "Linear" },{ idx: 1, name: "Square Root" } ],
    formatForDomain: function(number) {
      if (ractive.get('display000s')) {
        return (number/1000).sigFigs(3);
      } else {
        return number.sigFigs(3);
      }
    },
    haveOverlaps: function(interventions) {
      console.info('haveOverlaps:'+interventions.length);
      var specificMsgs = '<ul>';
      $.each(interventions, function(i,d) {
        if (d.overlappingInterventionList!=undefined && d.overlappingInterventionList.length>0) {
          specificMsgs += '<li><b>'+d.name+':</b> ';
          specificMsgs += d.overlappingInterventionList;
        }
      });

      return specificMsgs;
    },
    shortId: function(uri) {
      return uri.substring(uri.lastIndexOf('/')+1);
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
    sum: function(column) {
      //console.log('sum of column: '+column);
      if (ractive.get('interventions').length==0) return;
      var interventions = ractive.get('interventions');
      var sum = 0;
      for (i = 0 ; i < interventions.length ; i++) {
        sum += interventions[i][column];
      }
      return sum;
    },
    stdPartials: [
      { "name": "maccIntroSect", "url": "../vsn/partials/macc-segment-intro-sect.html"},
      { "name": "maccOptionsSect", "url": "../vsn/partials/macc-options-sect.html"},
      { "name": "maccDisplaySect", "url": "../vsn/partials/macc-display-sect.html"},
      { "name": "macTableDisplaySect", "url": "../vsn/partials/mac-table-sect.html"}
    ],
    tenant: {id: 'sdu'},
    title: 'Healthy Returns by 2020',
    yNegLimit:-500,
    yPosLimit:500
  },
  partials: {
    'maccIntroSect':'',
    'maccDisplaySect':'',
    'maccOptionsSect':'',
    'macTableDisplaySect':'',
    'poweredBy':'',
    'titleArea':''
  },
  fetch: function () {
    console.info('fetch...');
    macc.options.targetYear = ractive.get('parameters.targetYear');
    var orgType = ractive.get('optOrgType');
    if (orgType) {
      $.ajax({
        dataType: "json",
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/interventions/status/green/'+orgType,
        crossDomain: true,
        success: function( data ) {
          $.each(data, function(i,d) {
            //console.log('costPerTonneCo2e: '+d.costPerTonneCo2e);
            d.slug  = d.name.toSlug();
          })
          ractive.merge('interventions',data.sort(ractive.sortByCostPerTonneCo2e));
          if (ractive.hasRole('admin')) $('.admin').show();
          if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
          ractive.replaceGraph();
        }
      });
    }
  },
  fetchOrganisationTypes: function () {
    console.log('fetchOrganisationTypes...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-types/status/green?projection=complete',
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
//        if (ractive.get('optScaleType')==undefined) ractive.set('optScaleType',ractive.get('scaleTypes.0.name'));
        if (ractive.get('optOrgType')==undefined) ractive.set('optOrgType',ractive.get('organisationTypes.0.name'));
      }
    });
  },
  oninit: function() {
    console.log('oninit');
  },
  replaceGraph: function() {
    console.info('replaceGraph: ');
    $('#result').fadeOut(1000, function() {
      macc.displayDataSet('#result',ractive.get('interventions'));
      $('#result').fadeIn(1000);
    });
  },
  select: function(id) {
    console.info('select: '+id);
    d3.select('#'+id).classed('selected',true);
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
    if (ractive.get('optOrgType')!=undefined) ractive.replaceGraph();
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
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
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
  }
});

ractive.observe('optOrgType', function(newValue, oldValue, keypath) {
  if (newValue!=undefined && newValue!='') {
    ractive.fetch();
  }
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

ractive.on( 'filterOrgType', function ( event, orgType ) {
  console.info('filterOrgType on '+JSON.stringify(event)+','+orgType);
  ractive.set('optOrgType',orgType);
  ractive.fetch();
});
ractive.on( 'sort', function ( event, column ) {
  console.info('sort on '+column);
  // if already sorted by this column reverse order
  if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
  this.set( 'sortColumn', column );
});

$(document).ready(function() {
  $('head').append('<link href="/sdu/css/sdu-1.0.0.css" rel="stylesheet">');

  ractive.fetchOrganisationTypes();
  $(window).bind('resize', function () {
    //console.log('resized to '+window.innerWidth);
    ractive.replaceGraph();
  });
})

String.prototype.toSlug = function() {
  if (this == undefined) return this;
  return this.replace(/[.,()'"&%\/]/g,'').toLeadingCaps().replace(/ /g,'');
}
