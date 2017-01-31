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
var TRANSITION_DURATION = 500;
// 4. We've got an element in the DOM, we've created a template, and we've
// loaded the library - now it's time to build our Hello World app.
var ractive = new Ractive({
  // The `el` option can be a node, an ID, or a CSS selector.
  el: 'container',
  
  // If two-way data binding is enabled, whether to only update data based on 
  // text inputs on change and blur events, rather than any event (such as key
  // events) that may result in new data.
  lazy: true,
  
  // We could pass in a string, but for the sake of convenience
  // we're passing the ID of the <script> tag above.
  template: '#template',

  // partial templates
  // partials: { question: question },

  charts: new Charts(),
  // Here, we're passing in some initial data
  data: {
    tenant: { 
      id: 'barts', 
    },
    breadcrumbs: ['all'],
    sortBy: function( array, column ) { 
      if (ractive == undefined) { 
        console.log('  not yet, still loading...');
      } else {
        return ractive.sortBy(array,column) 
      }
    },
    sortColumn: 'amount',
    sum: function(arr) { 
      return arr.reduce(function(previousValue, currentValue, idx, arr) {
        return parseInt(previousValue) + parseInt(currentValue.amount);
      }, 0); // need to init total to 0 or will try adding to zeroth object in array
    },
    report: 'byDefraDeccCategory',
    reports: ['byDefraDeccCategory','byDefraDeccCode','byEClass','bySupplier'],
    strings: $.getJSON('/js/strings.json', function(data) {
      ractive.set('strings',data);
      ractive.set('title', ractive.get('strings')["/financialTransactionsSummary/byDefraDeccCategory"]);
    }),
    title: 'Loading...',
    transactions: [],
    transactionSummaries: []
  },
  add: function () {
    console.log('add...');
    $('h2.edit-form').hide();
    $('.create-form').show();
    var transactionSummary = { author:localStorage['username'], organisation: {}, url: undefined };
    ractive.select( transactionSummary );
  },
  addDoc: function (transactionSummary) {
    console.log('addDoc '+transactionSummary+' ...');
    ractive.set('current.doc', { author:localStorage['username'], transactionSummary: transactionSummary, url: undefined});
    $('#docsTable tr:nth-child(1)').slideDown();
  },
  edit: function (transactionSummary) {
    console.log('edit'+transactionSummary+'...');
    $('h2.edit-form').show();
    $('.create-form').hide();
    ractive.select( transactionSummary );
  },
  getTransactionSummariesForPie: function(array, labelProp, valueProp) { 
    var data = []; 
    $.each(array, function(i,d) {
      data.push({ idx:i+1, label:d[labelProp], value:d[valueProp]});
    });
    /*var segmentsBeforeOther = 15;
    var otherAmount = 0;
    $.each(array, function(i,d) {
      if (i<segmentsBeforeOther) {
        data.push({ idx:i+1, label:d[labelProp], value:d[valueProp]});
      } else {
        otherAmount += d[valueProp];
      }
    });
    data.push({ idx:'Other', label:'Other', value:otherAmount});*/
    return data;
  },
  navigateDown: function(filter) {
    var report = ractive.get('reports')[ractive.get('reports').indexOf(ractive.get('report'))+1];
    if (report == undefined) { ractive.showWarning('No more detailed data is available at this time'); return; }
    ractive.set('report',report);
    ractive.push('breadcrumbs', filter);
    console.log('navigateDown: '+filter+", next report: "+ractive.get('report'));
    $('#messages').css('visibility','hidden');
    $('#navUp').fadeIn();
    ractive.fetchAndRenderChart(ractive.get('tenant.id')+'/financialTransactionsSummary/'+ractive.get('report')+'?filter='+filter);
  },
  navigateUp: function() { 
    var report = ractive.get('reports')[ractive.get('reports').indexOf(ractive.get('report'))-1];
    if (report == undefined) { ractive.showError('Something went wrong, please reload the application'); return; }
    ractive.set('report',report);
    console.log('navigateUp: next report: '+ractive.get('report'));
    $('#messages').css('visibility','hidden');
    ractive.pop('breadcrumbs').then( function ( results ) {
      ractive.fetchAndRenderChart(ractive.get('tenant.id')+"/financialTransactionsSummary/"+ractive.get('report')+'?filter='+ractive.get('breadcrumbs')[ractive.get('breadcrumbs').length-1]);
      //if (ractive.data.breadcrumbs.length == 1) $('#navUp').fadeOut();
    });
  },
  save: function () {
    console.log('save '+ractive.get('current')+' ...');
    var id = ractive.get('current')._links === undefined ? undefined : ractive.get('current')._links.self.href;
    if (document.getElementById('currentForm').checkValidity()) { 
      $.ajax({
        url: id === undefined ? '/financialTransactionSummaries' : id,
        type: id === undefined ? 'POST' : 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(ractive.get('current')),
        success: completeHandler = function(data, textStatus, jqXHR) {
          console.log('data: '+ data);
          var location = jqXHR.getResponseHeader('Location');
          if (location != undefined) ractive.set('current._links.self.href',location);
        },
        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
            alert("Bother: "+textStatus+':'+errorThrown);
        }
      });
    } else {
      console.warn('Cannot save yet as transactionSummary is invalid');
    }
  },
  saveDoc: function () {
    console.log('saveDoc '+ractive.get('current').doc.transactionSummary+' ...');
    var n = ractive.get('current').doc;
    n.url = $('#doc').val();
    if (n.url.trim().length > 0) { 
      $('#docsTable tr:nth-child(1)').slideUp();
      $.ajax({
        url: '/documents',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(n),
        success: completeHandler = function(data) {
          console.log('data: '+ data);
          ractive.select(ractive.get('current'));
        },
        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
            alert("Bother: "+textStatus+':'+errorThrown);
        }
      });
    } 
  },
  addNote: function (transactionSummary) {
    console.log('addNote '+transactionSummary+' ...');
    ractive.set('current.note', { author:localStorage['username'], transactionSummary: transactionSummary, content: undefined});
    $('#notesTable tr:nth-child(1)').slideDown();
  },
  saveNote: function () {
    console.log('saveNote '+ractive.get('current').note+' ...');
    var n = ractive.get('current').note;
    n.content = $('#note').val();
    if (n.content.trim().length > 0) { 
      $('#notesTable tr:nth-child(1)').slideUp();
      $.ajax({
        url: '/notes',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(n),
        success: completeHandler = function(data) {
          console.log('data: '+ data);
          ractive.select(ractive.get('current'));
        },
        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
            alert("Bother: "+textStatus+':'+errorThrown);
        }
      });
    }
  },
  sortBy: function ( array, column ) {
    console.info('sortBy: '+column);
    if (array==undefined || array.length==0) {
      console.log('  nothing to sort');
      return array;
    } else {
      console.log('  sorting '+array.length+' items');
      array = array.slice(); // clone, so we don't modify the underlying data
      return array.sort( function ( a, b ) {
        return a[ column ] < b[ column ] ? 1 : -1;
      });
    }
  },
  delete: function (url) {
    console.log('delete '+url+'...');
    $.ajax({
        url: url,
        type: 'DELETE',
        success: completeHandler = function(data) {
          ractive.fetch();
        },
        error: errorHandler = function(jqXHR, textStatus, errorThrown) {
            alert("Bother: "+textStatus+':'+errorThrown);
        }
    });
  },
  fetch: function () {
    console.log('fetch...');
    $.getJSON("/financialTransactionSummaries",  function( data ) {
      console.log('Found: '+JSON.stringify(data));
      if (data != undefined) 
        ractive.merge('transactionSummaries', data._embedded.financialTransactionSummaries);
	  });
  },
  fetchRaw: function () {
    console.log('fetch...');
    $.getJSON("/financialTransactions",  function( data ) {
      console.log('Found: '+JSON.stringify(data));
      if (data != undefined) 
        ractive.merge('transactions', data._embedded.financialTransactions);
    });
  },
  fetchAndRenderChart: function (url) {
    console.info('fetchAndRenderChart:'+url);
    var param = (url == undefined || url.indexOf('=') == -1 ? '' : url.substring(url.indexOf('=')+1)).toLabel();
    console.log('fetch: '+url+', param: '+param);
    var key = url.indexOf('?') == -1 ? url : url.substring(0,url.indexOf('?'));
    // strip tenant
    key = key.substring(key.indexOf('/'));
    console.log('setting title from key: '+key)
    ractive.set('title', ractive.get('strings')[key] + param);
    $.getJSON(url,  function( data ) {
      console.log('Found: '+JSON.stringify(data));
      if (data != undefined) {
        // TODO custom controller not currently returning HAL
        ractive.merge('transactionSummaries', data);
      }
      $("#chart").fadeOut(TRANSITION_DURATION, function() {
        $("#chart").empty();
        ractive.charts.pie('#chart', ractive.getTransactionSummariesForPie(ractive.sortBy(ractive.get('transactionSummaries'),'amount'), 'description', 'amount'));
        $("#chart").fadeIn();
      });
    });
  },
  import: function (formId) {
    console.log('import, form id: '+formId);

    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: ractive.get('tenant.id')+'/financialTransactionsSummary/import',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function(response) {
          console.log('successfully uploaded JSON');
        },
        error: function(jqXHR, textStatus, errorThrown) {
          console.log(textStatus+':'+errorThrown+','+jqXHR.responseText);
          alert("Bother: "+textStatus+':'+errorThrown);
        }
      });
  },
  /** Standard ractive event fired when ready to be rendered */
  oninit: function() {
	  //this.fetch();

	  // init auto complete values
    $.get('/defraDeccCategories', function(data){
      $("#curDefraDeccCategory").typeahead({ source:data });
	  },'json');
    $.get('/defraDeccCodes', function(data){
      $("#curDefraDeccCode").typeahead({ source:data });
    },'json');
    $.get('/eClassS2s', function(data){
      $("#curEClass").typeahead({ source:data });
    },'json');
    $.get('/financialYears', function(data){
      ractive.merge('financialYears', data._embedded.financialYears);
      $("#curFinancialYear").typeahead({ source:data });
    },'json');
  },
  select: function(transactionSummary) {
    console.log('select: '+JSON.stringify(transactionSummary));
    ractive.set('suspendSaveObserver',false);
	  ractive.set('current',transactionSummary);
	  if (transactionSummary._links != undefined) {
      $.getJSON(transactionSummary._links.self.href+'/notes',  function( data ) {
      	if (data._embedded != undefined) {
	        console.log('found notes '+data);
          ractive.merge('current.notes', data._embedded.notes);
	        // sort most recent first
  	      ractive.data.current.notes.sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
      	}
  	  });
      $.getJSON(transactionSummary._links.self.href+'/documents',  function( data ) {
        if (data._embedded != undefined) {
        	console.log('found docs '+data);
          ractive.merge('current.documents', data._embedded.documents);
          // sort most recent first
          ractive.get('current').documents.sort(function(a,b) { return new Date(b.created)-new Date(a.created); });
        }
    	});
    }
	  ractive.set('suspendSaveObserver',true);
	  $('#currentSect').slideDown();
  },
  showError: function(msg) {
    $('#messages').empty().append('<p class="bg-danger text-danger">'+msg).css('visibility','visible');
  },
  showMessage: function(msg) {
    $('#messages').empty().append('<p class="bg-info text-info">'+msg).css('visibility','visible');
  },
  showWarning: function(msg) {
    $('#messages').empty().append('<p class="bg-warning text-warning">'+msg).css('visibility','visible');
  }
});

// Save on model change
// done this way rather than with on-* attributes because autocomplete 
// controls done that way save the oldValue 
ractive.observe('current.*', function(newValue, oldValue, keypath) {
  ignored=['current.documents','current.doc','current.notes','current.note'];
  // console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
  if (ractive.get('suspendSaveObserver') && significantDifference(newValue,oldValue) && ignored.indexOf(keypath)==-1) {
    ractive.save();
  }
});

function significantDifference(newValue,oldValue) {
  if (newValue=='') { console.log('new value is empty');newValue = null;}
  if (oldValue=='') { console.log('oldvalue is empty');oldValue = null;}
  console.log('sig diff? '+newValue != oldValue);
  return newValue != oldValue;
}