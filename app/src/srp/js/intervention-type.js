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
    interventionTypes: [],
    filter: undefined,
    //saveObserver:false,
    tenant: { id: 'sdu' },
    username: localStorage['username'],
    age: function(timeString) {
      return i18n.getAgeString(new Date(timeString))
    },
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
    help: '<p>This page allows the management of a single list of interventionTypes and their associated data requirements</p>\
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
      else if (ractive.get('sortColumn') == column && !ractive.get('sortAsc')) return 'sort-desc'
      else return 'hidden';
    },
    stdPartials: [
      { "name": "helpModal", "url": $env.server+"/partials/help-modal.html"},
      { "name": "navbar", "url": "./vsn/partials/intervention-type-navbar.html"},
      { "name": "profileArea", "url": $env.server+"/partials/profile-area.html"},
      { "name": "sidebar", "url": $env.server+"/partials/sidebar.html"},
      { "name": "toolbar", "url": $env.server+"/partials/toolbar.html"},
      { "name": "titleArea", "url": $env.server+"/partials/title-area.html"},
      { "name": "interventionTypeListSect", "url": "./vsn/partials/intervention-type-list-sect.html"},
      { "name": "interventionTypeCurrentSect", "url": "./vsn/partials/intervention-type-current-sect.html"}
    ],
    title: "Intervention Types"
  },
  partials: {
    'helpModal':'',
    'interventionTypeCurrentSect':'',
    'interventionTypeListSect':'',
    'loginSect':'',
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
        something = window.open("data:text/csv," + encodeURIComponent(data),"_blank");
        //something.focus();
      }
    });
  },
  edit: function (interventionType) {
    console.log('edit'+interventionType+'...');
    $('h2.edit-form,h2.edit-field').show();
    $('.create-form,create-field').hide();
    ractive.select(interventionType);
  },
  editField: function (selector, path) {
    console.log('editField '+path+'...');
    $(selector).css('border-width','1px').css('padding','5px 10px 5px 10px');
  },
  delete: function (obj) {
    console.log('delete '+obj+'...');
    var url = obj.links != undefined
        ? obj.links.filter(function(d) { console.log('this:'+d);if (d['rel']=='self') return d;})[0].href
        : obj._links.self.href;
    $.ajax({
        url: url,
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.fetch();
          ractive.toggleResults();
        }
    });
    return false; // cancel bubbling to prevent edit as well as delete
  },
  fetch: function () {
    console.log('fetch...');
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
        if (ractive.hasRole('admin')) $('.admin').show();
        if (ractive.fetchCallbacks!=null) ractive.fetchCallbacks.fire();
        ractive.set('searchMatched',$('#interventionTypesTable tbody tr:visible').length);
        ractive.set('saveObserver', true);
      }
    });
  },
  filter: function(filter) {
    console.log('filter: '+JSON.stringify(filter));
    ractive.set('filter',filter);
    $('.omny-dropdown.dropdown-menu li').removeClass('selected')
    $('.omny-dropdown.dropdown-menu li:nth-child('+filter.idx+')').addClass('selected')
    ractive.set('searchMatched',$('#interventionTypesTable tbody tr:visible').length);
    $('input[type="search"]').blur();
  },
  find: function(interventionTypeId) {
    console.log('find: '+interventionTypeId);
    var c;
    $.each(ractive.get('interventionTypes'), function(i,d) {
      if (interventionTypeId.endsWith(ractive.getId(d))) {
        c = d;
      }
    });
    return c;
  },
  getId: function(interventionType) {
    console.log('getId: '+interventionTypeType);
    var uri;
    if (interventionType['links']!=undefined) {
      $.each(interventionType.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (interventionType['_links']!=undefined) {
      uri = interventionType._links.self.href.indexOf('?')==-1 ? interventionType._links.self.href : interventionType._links.self.href.substr(0,interventionType._links.self.href.indexOf('?')-1);
    }
    return uri;
  },
  hideResults: function() {
    $('#interventionTypesTableToggle').addClass('kp-icon-caret-right').removeClass('kp-icon-caret-down');
    $('#interventionTypesTable').slideUp();
    $('#currentSect').slideDown({ queue: true });
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
      $.ajax({
        url: id === undefined ? ractive.getServer()+'/intervention-types' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(tmp),
        success: completeHandler = function(data, textStatus, jqXHR) {
          console.log(jqXHR.status+': '+JSON.stringify(data));
          var location = jqXHR.getResponseHeader('Location');
          ractive.set('saveObserver',false);
          if (location != undefined) ractive.set('current._links.self.href',location);
          if (jqXHR.status == 201) {
            ractive.set('currentIdx',ractive.get('interventionTypes').push(ractive.get('current'))-1);
          }
          if (jqXHR.status == 204) ractive.splice('interventionTypes',ractive.get('currentIdx'),1,ractive.get('current'));

          $('.autoNumeric').autoNumeric('update');
          ractive.showMessage('Intervention saved');
          ractive.set('saveObserver',true);
        }
      });
    } else {
      console.warn('Cannot save yet as interventionType is invalid');
      $('#currentForm :invalid').addClass('field-error');
      $('.autoNumeric').autoNumeric('update');
      ractive.showMessage('Cannot save yet as interventionType is incomplete');
    }
  },
  saveRef: function () {
    console.log('saveRef '+JSON.stringify(ractive.get('current.ref'))+' ...');
    var n = ractive.get('current.ref');
    n.url = $('#ref').val();
    var url = ractive.getId(ractive.get('current'))+'/references';
    url = url.replace('interventionTypes/',ractive.get('tenant.id')+'/intervention-types/');
    if (n.url.trim().length > 0) {
      $('#refsTable tr:nth-child(1)').slideUp();
      $.ajax({
        /*url: '/documents',
        contentType: 'application/json',*/
        url: url,
        type: 'POST',
        data: n,
        success: completeHandler = function(data) {
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
    var url = ractive.tenantUri(interventionType);
    console.log('loading detail for '+url);
    $.getJSON(url,  function( data ) {
      console.log('found interventionType '+data);
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
  showResults: function() {
    $('#interventionTypesTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#interventionTypesTable').slideDown({ queue: true });
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  sortInterventions: function() {
    ractive.get('interventionTypes').sort(function(a,b) { return new Date(b.lastUpdated)-new Date(a.lastUpdated); });
  },
  toggleResults: function() {
    console.info('toggleResults');
    $('#interventionTypesTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#interventionTypesTable').slideToggle();
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
        success: function(response) {
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
ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
  console.log('searchTerm changed');
  ractive.showResults();
  setTimeout(function() {
    ractive.set('searchMatched',$('#interventionTypesTable tbody tr').length);
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
    console.warn  ('Skipped interventionType save of '+keypath);
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

function presentValue(futureValue,years) {
  console.info(' present value of: '+futureValue+', in '+years+' years');
  return Math.round(futureValue / Math.pow(1+ractive.get('discountRate'),years));
}
