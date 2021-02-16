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
fadeOutInterventions = true;
var newLineRegEx = /\n/g;

var ractive = new BaseRactive({
  el: 'container',
  lazy: true,
  template: '#template',
  data: {
    discountRate:0.035,
    interventions: [],
    interventionTypes: [],
    organisationTypes: [],
    filter: undefined,
    //saveObserver:false,
    tenant: { id: 'sdu' },
    username: localStorage['username'],
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
//      for(idx in ractive.get('interventions')) {
//
//      }
      //console.log('  intvn: '+JSON.stringify(ractive.get('interventions.'+(i*j))));
      // NOTE: this only works because intvn types, org types and interventions all sorted by name
      return ractive.get('interventions.'+(i*j)+'.shareOfTotal');
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
    help: '<p>This page allows the management of a single list of interventions and their associated data requirements</p>\
      <h2>Key concepts</h2>\
      <ul>\
        <li>\
          <h3 id="">Concept 1</h3>\
          <p>Lorem ipsum.</p>\
        </li>\
        <li>\
          <h3 id="">Concept 2</h3>\
          <p>Lorem ipsum.</p>\
        </li>\
        <li>\
          <h3 id="">Concept 3</h3>\
          <p>Lorem ipsum.</p>\
        </li>\
        </ul>\
      </ul>',
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
        return obj.organisationType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.interventionType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.interventionType.status.toLowerCase().indexOf(searchTerm.toLowerCase())>=0
          || obj.organisationType.name.toLowerCase().indexOf(searchTerm.toLowerCase())>=0;
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
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
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
      for(idx in ractive.get('interventions')) {
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
      for(idx in ractive.get('interventions')) {
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
      for(idx in ractive.get('interventions')) {
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
        something = window.open("data:text/csv," + encodeURIComponent(data),"_blank");
        //something.focus();
      }
    });
  },
  edit: function (intervention) {
    console.log('edit'+intervention+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(intervention);
  },
  delete: function (obj) {
    ractive.srp.delete(ractive.get('current'))
    .then(response => {
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
    .then(response => response.json()) // catch (e) { console.error('unable to parse response as JSON') } })
    .then(data => {
        if (data['_embedded'] == undefined) {
          ractive.merge('interventions', data);
        }else{
          ractive.merge('interventions', data['_embedded'].interventions);
        }
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#interventionsTable tbody tr:visible').length);
        ractive.set('saveObserver', true);
    })
  },
  fetchInterventionTypes: function () {
    console.log('fetchInterventionTypes...');
    ractive.set('saveObserver', false);
    $.ajax({
      dataType: "json",
      url: ractive.getServer()+'/'+ractive.get('tenant.id')+'/intervention-types/',
      crossDomain: true,
      success: function( data ) {
        if (data['_embedded'] == undefined) {
          ractive.merge('interventionTypes', data);
        }else{
          ractive.merge('interventionTypes', data['_embedded'].interventionTypes);
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
        if (data['_embedded'] == undefined) {
          ractive.merge('organisationTypes', data);
        }else{
          ractive.merge('organisationTypes', data['_embedded'].organisationTypes);
        }
        ractive.initOrganisationTypes();
        ractive.set('saveObserver', true);
      }
    });
  },
  filter: function(filter) {
    console.log('filter: '+JSON.stringify(filter));
    ractive.set('filter',filter);
    $('.omny-dropdown.dropdown-menu li').removeClass('selected')
    $('.omny-dropdown.dropdown-menu li:nth-child('+filter.idx+')').addClass('selected')
    ractive.set('searchMatched',$('#interventionsTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  find: function(interventionId) {
    console.info('find: '+interventionId);
    var c;
    $.each(ractive.get('interventions'), function(i,d) {
      if (interventionId.endsWith(ractive.getId(d))) {
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
  getId: function(intervention) {
    console.log('getId: '+intervention);
    var uri;
    if (intervention['links']!=undefined) {
      $.each(intervention.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (intervention['_links']!=undefined) {
      uri = intervention._links.self.href.indexOf('?')==-1 ? intervention._links.self.href : intervention._links.self.href.substr(0,intervention._links.self.href.indexOf('?')-1);
    }
    return uri;
  },
  hideResults: function() {
    $('#interventionsTableToggle').addClass('kp-icon-caret-right').removeClass('kp-icon-caret-down');
    $('#interventionsTable').slideUp();
    $('#currentSect').slideDown({ queue: true });
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
      for (i=0;i<ractive.get('current.lifetime');i++) {
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
//      ractive.set('timeSeries',ractive.get(field+'TimeSeries'));
    }
  },
  oninit: function() {
    console.log('oninit');
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
      tmp.interventionType = ractive.getServer()+'/interventionTypes/'+ractive.getId(ractive.findInterventionType(tmp.interventionType.name));
      tmp.organisationType = ractive.getServer()+'/organisationTypes/'+ractive.getId(ractive.findOrganisationType(tmp.organisationType.name));
      tmp.tenantId = ractive.get('tenant.id');
      console.log('ready to save intervention'+JSON.stringify(tmp)+' ...');
      $.ajax({
        url: id === undefined ? ractive.getServer()+'/interventions' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(tmp),
        success: completeHandler = function(data, textStatus, jqXHR) {
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

    var url = ractive.tenantUri(intervention);
    console.log('loading detail for '+url);
    $.getJSON(url,  function( data ) {
      console.log('found intervention '+data);
      ractive.set('current', data);
      ractive.initControls();
      // who knows why this is needed, but it is, at least for first time rendering
      $('.autoNumeric').autoNumeric('update',{});
      ractive.toggleResults();
      $('#currentSect').slideDown();
      ractive.set('saveObserver',true);
    });
  },
  showActivityIndicator: function(msg, addClass) {
    document.body.style.cursor='progress';
    this.showMessage(msg, addClass);
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
        success: function(response) {
          console.log('successfully uploaded resource');
          ractive.fetch();
          ractive.hideUpload();
        }
    });
  }
});

ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#interventionsTable tbody tr').length);
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

function presentValue(futureValue,years) {
  console.info(' present value of: '+futureValue+', in '+years+' years');
  return Math.round(futureValue / Math.pow(1+ractive.get('discountRate'),years));
}
