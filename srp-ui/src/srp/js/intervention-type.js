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
    discountRate:0.035,
    entityName: 'interventionType',
    interventionTypes: [],
    filter: undefined,
    tenant: { id: 'sdu' },
    username: localStorage.getItem('username'),
    help: '<p>This page allows the management of a single list of interventionTypes and their associated data requirements</p>',
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
    pv: function(futureValue, years) {
      return presentValue(futureValue,years);
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
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc';
      else return 'hidden';
    },
    stdPartials: [
      { "name": "helpModal", "url": "/partials/help-modal.html"},
      { "name": "navbar", "url": "./vsn/partials/intervention-type-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "interventionTypeListSect", "url": "./vsn/partials/intervention-type-list-sect.html"},
      { "name": "interventionTypeCurrentSect", "url": "./vsn/partials/intervention-type-current-sect.html"}
    ],
    title: "Intervention Types"
  },
  partials: {
    'helpModal':'',
    'interventionTypeCurrentSect':'',
    'interventionTypeListSect':'',
    'profileArea':'',
    'sidebar':'',
    'titleArea':''
  },
  add: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var interventionType = { modellingYear:new Date().getFullYear(), tenantId: ractive.get('tenant.id') };
    ractive.select(interventionType);
  },
  addResource: function () {
    console.log('addResource...');
    //$('#upload fieldset').append($('#resourceControl').html());
    $(".uploadForm .file").click();
  },
  download: function() {
    console.info('download');
    $.ajax({
      headers: {
        "Accept": "text/csv; charset=utf-8",
        "Content-Type": "text/csv; charset=utf-8"
      },
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/intervention-types/',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        window.open("data:text/csv," + encodeURIComponent(data),"_blank");
      }
    });
  },
  edit: function (interventionType) {
    console.log('edit'+interventionType+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.srp.fetchQuestion(ractive.tenantUri(interventionType))
      .then(function(response) { return response.json(); })
      .then(ractive.select);
  },
  editField: function (selector, path) {
    console.log('editField '+path+'...');
    $(selector).css('border-width','1px').css('padding','5px 10px 5px 10px');
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    ractive.srp.deleteInterventionType(ractive.get('current'))
    .then(function(response) {
      switch (response.status) {
      case 204:
        ractive.fetch();
        ractive.toggleResults();
        break;
      default:
        ractive.showMessage('Unable to delete the intervention type ('+response.status+')');
      }
    });
    return false; // cancel bubbling to prevent edit as well as delete
  },
  fetch: function () {
    console.log('fetch...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchInterventionTypes(ractive.get('tenant.id'))
    .then(function(response) { return response.json(); }) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(function(data) {
      if ('_embedded' in data) {
        ractive.merge('interventionTypes', data._embedded.interventionTypes);
      } else {
        ractive.merge('interventionTypes', data);
      }
      if (ractive.hasRole('admin')) $('.admin').show();
      if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
      ractive.set('searchMatched',$('#interventionTypesTable tbody tr:visible').length);
      ractive.set('saveObserver', true);
    });
  },
  find: function(interventionTypeId) {
    console.log('find: '+interventionTypeId);
    var c;
    $.each(ractive.get('interventionTypes'), function(i,d) {
      if (interventionTypeId.endsWith(ractive.id(d))) {
        c = d;
      }
    });
    return c;
  },
  hideUpload: function () {
    console.log('hideUpload...');
    $('#upload').slideUp();
  },
  initTimeSeries: function(field,loadingStrategy) {
    console.info('initTimeSeries for: '+field);
    ractive.set('timeSeriesField',field);
    if (ractive.get('current.'+field+'TimeSeries')==undefined) ractive.set('current.'+field+'TimeSeries',[]);
    if (ractive.get('current.'+field+'TimeSeries').length<ractive.get('current.lifetime')) {
      var val;
      for (var i=0;i<ractive.get('current.lifetime');i++) {
        switch (loadingStrategy) {
        case 'average':
          val = ractive.get('current.'+field);
          break;
        case 'upFront':
          if (i==0) val = ractive.get('current.'+field);
          else val = 0;
          break;
        default:
          console.warn('Do not know how to initialise time series according to strategy: '+loadingStrategy);
        }
        ractive.get('current.'+field+'TimeSeries').push(val);
      }
    }
  },
  save: function () {
    console.log('save interventionType: '+ractive.get('current').name+'...');
    ractive.set('saveObserver',false);
    var id = ractive.uri(ractive.get('current'));
    console.log('  id: '+id+', so will '+(id === undefined ? 'POST' : 'PUT'));
    // Workaround for incompatibility between autoNumeric and server side number fields
    try { ractive.set('current.costPerTonneCo2e',$('#curCostPerTonneCo2e').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.tonnesCo2eSavedTargetYear',$('#curTonnesCo2eSavedInTargetYear').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.cashOutflowsUpFront',$('#curCashOutflowsUpFront').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.annualCashOutflows',$('#curAnnualCashOutflows').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.annualCashInflows',$('#curAnnualCashInflows').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.annualTonnesCo2eSaved',$('#curAnnualTonnesCo2eSaved').autoNumeric('get')); } catch (e) {}
    try { ractive.set('current.unitCount',$('#curUnitCount').autoNumeric('get')); } catch (e) {}

    ractive.set('saveObserver',true);
    if (document.getElementById('currentForm')==undefined) {
      // loading... ignore
    } else if(document.getElementById('currentForm').checkValidity()) {
      var tmp = JSON.parse(JSON.stringify(ractive.get('current')));
      delete tmp.gasCIntensity;
      delete tmp.elecCIntensity;
      delete tmp.elecPrice;
      delete tmp.gasPrice;
      delete tmp.targetYear;
      delete tmp.interventionType;
      tmp.tenantId = ractive.get('tenant.id');
//      console.log('ready to save interventionType'+JSON.stringify(tmp)+' ...');
      ractive.srp.saveInterventionType(tmp, ractive.get('tenant.id'))
          .then(function(response) {
            ractive.set('saveObserver',false);
            switch(response.status) {
            case 201:
              ractive.set('currentIdx',ractive.get('interventionTypes').push(ractive.get('current'))-1);
              return response.json();
            }
          })
          .then(function(data) {
            if (data !== undefined) ractive.set('current', data);
            ractive.splice('interventionTypes',ractive.get('currentIdx'),1,ractive.get('current'));
            $('.autoNumeric').autoNumeric('update');
            ractive.showMessage('Intervention saved');
            ractive.set('saveObserver',true);
          });
    } else {
      console.warn('Cannot save yet as interventionType is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as Intervention Type is incomplete');
    }
  },
  saveRef: function () {
    console.log('saveRef '+JSON.stringify(ractive.get('current.ref'))+' ...');
    var n = ractive.get('current.ref');
    n.url = $('#ref').val();
    var url = ractive.id(ractive.get('current'))+'/references';
    url = url.replace('interventionTypes/',ractive.get('tenant.id')+'/intervention-types/');
    if (n.url.trim().length > 0) {
      $('#refsTable tr:nth-child(1)').slideUp();
      $.ajax({
        /*url: '/documents',
        contentType: 'application/json',*/
        url: url,
        type: 'POST',
        data: n,
        success: function(data) {
          console.log('data: '+ data);
          ractive.showMessage('Reference link saved successfully');
          ractive.fetchRefs();
          $('#ref').val(undefined);
        }
      });
    }
  },
  select: function(interventionType) {
    console.log('select: '+JSON.stringify(interventionType));
    ractive.set('saveObserver',false);
    ractive.set('saveObserver', false);
    console.log('found interventionType '+interventionType);
    ractive.set('current', interventionType);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.hideResults();
    $('#currentSect').slideDown();
    ractive.set('saveObserver',true);
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  sortInterventions: function() {
    ractive.get('interventionTypes').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  updateTimeSeriesSummary: function() {
    console.info('updateTimeSeriesSummary');
    var ts = ractive.get('current.'+ractive.get('timeSeriesField')+'TimeSeries');
    var sum = ts.reduce(function(prev, curr) { return prev + curr; } );
    // data binding seems to fail in this setting hence update both model and ui
    var ctrl = '#cur'+ractive.get('timeSeriesField')[0].toUpperCase()+ractive.get('timeSeriesField').substring(1);
    $(ctrl).autoNumeric('set',sum);
    ractive.set('current.'+ractive.get('timeSeriesField'),sum);

    $('#timeSeriesModal').modal('hide');
  },
  upload: function(formSelector) {
    console.log('upload, selector: '+formSelector);
    var formElement = document.querySelector(formSelector);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/intervention-types/uploadcsv',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function() {
          console.log('successfully uploaded resource');
          ractive.fetch();
          ractive.hideUpload();
        }
    });
  }
});

ractive.observe('resourceFiles', function(newValue, oldValue, keypath) {
  if (newValue!=undefined) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    var tmp='';
    $.each(newValue, function(i,d) {
      tmp+=d.name;
      tmp+=',';
    });
    ractive.set('resourceName',newValue==undefined || newValue.length==0 ? undefined : newValue.length+' selected');
  } else {
    console.warn  ('Nothing to do: '+keypath+': '+newValue);
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
    console.warn  ('Skipped interventionType save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
  }
});
ractive.on( 'filter', function ( event, filter ) {
  console.info('filter on '+JSON.stringify(event)+','+filter.idx);
  ractive.filter(filter);
});

function presentValue(futureValue,years) {
  console.info(' present value of: '+futureValue+', in '+years+' years');
  return Math.round(futureValue / Math.pow(1+ractive.get('discountRate'),years));
}
