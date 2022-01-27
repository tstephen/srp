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
    discountRate:0.035,
    entityName: 'intervention',
    entityPath: '/interventions',
    interventions: [],
    interventionTypes: [],
    organisationTypes: [],
    filter: undefined,
    //saveObserver:false,
    tenant: { id: 'sdu' },
    username: localStorage.getItem('username'),
    findShareOfCarbon: function(i,j) {
      console.info('findShareOfCarbon: '+i+','+j);
      // NOTE: this only works because intvn types, org types and interventions all sorted by name
      return ractive.get('interventions.'+(i*j)+'.tonnesCo2eSavedTargetYear');
    },
    findShareOfFinancial: function(i,j) {
      console.info('findShareOfFinancial: '+i+','+j);
      // NOTE: this only works because intvn types, org types and interventions all sorted by name
      return ractive.get('interventions.'+(i*j)+'.targetYearSavings')/1000;
    },
    findShareOfTotal: function(i,j) {
      console.info('findShareOfTotal: '+i+','+j);
      console.log('  intervention type: '+ractive.get('interventionTypes')[i].name);
      console.log('  org type: '+ractive.get('organisationTypes')[j].name);
      // NOTE: this only works because intvn types, org types and interventions all sorted by name
      return ractive.get('interventions.'+(i*j)+'.shareOfTotal');
    },
    help: '<p>This page allows the management of a single list of interventions and their associated data requirements</p>',
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
        return obj.organisationType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0 ||
          obj.interventionType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0 ||
          obj.interventionType.status.toLowerCase().indexOf(searchTerm.toLowerCase())>=0 ||
          obj.organisationType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0;
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
      { "name": "navbar", "url": "./vsn/partials/intervention-navbar.html"},
      { "name": "profileArea", "url": "/partials/profile-area.html"},
      { "name": "sidebar", "url": "/partials/sidebar.html"},
      { "name": "toolbar", "url": "/partials/toolbar.html"},
      { "name": "titleArea", "url": "/partials/title-area.html"},
      { "name": "interventionGridSect", "url": "./vsn/partials/intervention-grid-sect.html"},
      { "name": "interventionListSect", "url": "./vsn/partials/intervention-list-sect.html"},
      { "name": "interventionCurrentSect", "url": "./vsn/partials/intervention-current-sect.html"},
      { "name": "interventionTimeModal", "url": "./vsn/partials/intervention-time-modal.html"}
    ],
    sumShare: function(i) {
      console.info('sumShare');
      var itName = ractive.get('interventionTypes.'+i+'.name');
      console.log('  intervention type: '+itName);
      var total=0;
      for(var idx in ractive.get('interventions')) {
        if (itName==ractive.get('interventions.'+idx+'.name')) {
          total+=ractive.get('interventions.'+idx+'.shareOfTotal');
        }
      }
      return total;
    },
    sumShareCarbon: function(i) {
      console.info('sumShareCarbon');
      var itName = ractive.get('interventionTypes.'+i+'.name');
      console.log('  intervention type: '+itName);
      var total=0;
      for(var idx in ractive.get('interventions')) {
        if (itName==ractive.get('interventions.'+idx+'.name')) {
          total+=ractive.get('interventions.'+idx+'.tonnesCo2eSavedTargetYear');
        }
      }
    },
    sumShareFinancial: function(i) {
      console.info('sumShareFinancial');
      var itName = ractive.get('interventionTypes.'+i+'.name');
      console.log('  intervention type: '+itName);
      var total=0;
      for(var idx in ractive.get('interventions')) {
        if (itName==ractive.get('interventions.'+idx+'.name')) {
          total+=ractive.get('interventions.'+idx+'.targetYearSavings');
        }
      }
    },
    title: "Interventions"
  },
  partials: {
    profileArea: '',
    titleArea: '',
    interventionListSect: '',
    interventionCurrentSect: '',
    interventionTimeModal: '',
    helpModal: '',
    sidebar: ''
  },
  add: function () {
    console.log('add...');
    $('h2.edit-form,h2.edit-field').hide();
    $('.create-form,create-field').show();
    var intervention = { tenantId: ractive.get('tenant.id') };
    ractive.select(intervention);
  },
  addResource: function () {
    console.log('addResource...');
    //$('#upload fieldset').append($('#resourceControl').html());
    $("#file").click();
  },
  download: function() {
    console.info('download');
    $.ajax({
      headers: {
        "Accept": "text/csv",
        "Content-Type": "text/csv"
      },
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/interventions/?projection=complete',
      crossDomain: true,
      success: function( data ) {
        console.warn('response;'+data);
        window.open("data:text/csv," + encodeURIComponent(data),"_blank");
      }
    });
  },
  edit: function (intervention) {
    console.log('edit'+intervention+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.srp.fetchIntervention(ractive.tenantUri(intervention))
      .then(function(response) { return response.json(); })
      .then(ractive.select);
  },
  delete: function (obj) {
    ractive.srp.delete(obj)
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
  fetch: function () {
    console.log('fetch...');
    ractive.fetchInterventionTypes();
    ractive.fetchOrganisationTypes();
    ractive.fetchInterventions();
  },
  fetchInterventions: function() {
    console.log('fetchInterventions...');
    if (ractive.keycloak.token === undefined) return;
    ractive.set('saveObserver', false);
    ractive.srp.fetchInterventions(ractive.get('tenant.id'))
    .then(function(response) { return response.json(); }) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(function(data) {
        if ('_embedded' in data) {
          ractive.merge('interventions', data._embedded.interventions);
        }else{
          ractive.merge('interventions', data);
        }
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#interventionsTable tbody tr:visible').length);
        ractive.set('saveObserver', true);
    });
  },
  fetchInterventionTypes: function () {
    console.log('fetchInterventionTypes...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/intervention-types/',
      crossDomain: true,
      success: function( data ) {
        if ('_embedded' in data) {
          ractive.merge('interventionTypes', data._embedded.interventionTypes);
        }else{
          ractive.merge('interventionTypes', data);
        }
        ractive.initInterventionTypes();
        ractive.set('saveObserver', true);
      }
    });
  },
  fetchOrganisationTypes: function () {
    console.log('fetchOrganisationTypes...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/organisation-types/?projection=complete',
      crossDomain: true,
      success: function( data ) {
        if ('_embedded' in data) {
          ractive.merge('organisationTypes', data._embedded.organisationTypes);
        }else{
          ractive.merge('organisationTypes', data);
        }
        ractive.initOrganisationTypes();
        ractive.set('saveObserver', true);
      }
    });
  },
  find: function(interventionId) {
    console.info('find: '+interventionId);
    var c;
    $.each(ractive.get('interventions'), function(i,d) {
      if (interventionId.endsWith(ractive.id(d))) {
        c = d;
      }
    });
    return c;
  },
  findInterventionType: function(interventionTypeName) {
    console.info('findInterventionType: '+interventionTypeName);
    var c;
    $.each(ractive.get('interventionTypes'), function(i,d) {
      if (d.name==interventionTypeName) {
        c = d;
      }
    });
    return c;
  },
  findOrganisationType: function(organisationTypeName) {
    console.info('findOrganisationType: '+organisationTypeName);
    var c;
    $.each(ractive.get('organisationTypes'), function(i,d) {
      if (d.name==organisationTypeName) {
        c = d;
      }
    });
    return c;
  },
  hideUpload: function () {
    console.log('hideUpload...');
    $('#upload').slideUp();
  },
  initInterventionTypes: function() {
    console.log('initInterventionTypes');
    var data = ractive.get('interventionTypes').map(function(obj) {
      return { id: obj.selfRef, name: obj.name };
    });
    ractive.set('dataInterventionTypes',data);
    ractive.addDataList({ name: 'interventionTypes'}, data);
  },
  initOrganisationTypes: function() {
    console.log('initOrganisationTypes');
    var data = ractive.get('organisationTypes').map(function(obj) {
      return { id: obj.selfRef, name: obj.name };
    });
    ractive.set('dataOrganisationTypes',data);
    ractive.addDataList({ name: 'organisationTypes'}, data);
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
    console.log('save intervention: '+ractive.get('current.interventionType.name')+'...');
    ractive.set('saveObserver',false);
    var id = ractive.uri(ractive.get('current'));
    console.log('  id: '+id+', so will '+(id === undefined ? 'POST' : 'PUT'));
    ractive.set('saveObserver',true);

    if (document.getElementById('currentForm')==undefined) {
      // loading... ignore
    } else if(document.getElementById('currentForm').checkValidity()) {
      var tmp = JSON.parse(JSON.stringify(ractive.get('current')));
      tmp.interventionType = ractive.getServer()+'/interventionTypes/'+ractive.id(ractive.findInterventionType(tmp.interventionType.name));
      tmp.organisationType = ractive.getServer()+'/organisationTypes/'+ractive.id(ractive.findOrganisationType(tmp.organisationType.name));
      tmp.tenantId = ractive.get('tenant.id');
      console.log('ready to save intervention'+JSON.stringify(tmp)+' ...');
      $.ajax({
        url: id === undefined ? ractive.getServer()+'/interventions' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(tmp),
        success: function(data, textStatus, jqXHR) {
          console.log(jqXHR.status+': '+JSON.stringify(data));
          var location = jqXHR.getResponseHeader('Location');
          ractive.set('saveObserver',false);
          if (location != undefined) ractive.set('current._links.self.href',location);
          if (jqXHR.status == 201) {
            ractive.set('currentIdx',ractive.get('interventions').push(ractive.get('current'))-1);
          }
          if (jqXHR.status == 204) ractive.splice('interventions',ractive.get('currentIdx'),1,ractive.get('current'));

          $('.autoNumeric').autoNumeric('update');
          ractive.showMessage('Intervention saved');
          ractive.set('saveObserver',true);
        }
      });
    } else {
      console.warn('Cannot save yet as intervention is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as intervention is incomplete');
    }
  },
  select: function(intervention) {
    console.log('select: '+JSON.stringify(intervention));
    ractive.set('saveObserver',false);

    ractive.set('current', intervention);
    ractive.initControls();
    // who knows why this is needed, but it is, at least for first time rendering
    $('.autoNumeric').autoNumeric('update',{});
    ractive.toggleResults();
    $('#currentSect').slideDown();
    ractive.set('saveObserver',true);
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  showResults: function() {
    $('#interventionsTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#interventionsTable').slideDown({ queue: true });
  },
  toggleResults: function() {
    console.info('toggleResults');
    $('#interventionsTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#interventionsTable').slideToggle();
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
  upload: function(formId) {
    console.log('upload, id: '+formId);
    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/interventions/uploadcsv',
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

// Save on model change
// done this way rather than with on-* attributes because autocomplete
// controls done that way save the oldValue
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  var ignored=['current.references'];
  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    ractive.save();
  } else {
    console.warn  ('Skipped intervention save of '+keypath);
    //console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    //console.log('  saveObserver: '+ractive.get('saveObserver'));
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

function presentValue(futureValue,years) {
  console.info(' present value of: '+futureValue+', in '+years+' years');
  return Math.round(futureValue / Math.pow(1+ractive.get('discountRate'),years));
}
