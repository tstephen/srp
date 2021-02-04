/*******************************************************************************
 * Copyright 2014-2021 Tim Stephenson and contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 ******************************************************************************/
describe("Sustainability Return API", function() {
  var tenantId = 'sdu';
  var DEFAULT_SERVER = 'http://localhost:8080';
  var $rh = new RestEntityHelper({
    server: window['$env'] == undefined ? DEFAULT_SERVER : $env.server,
    tenantId: tenantId
  });

  var originalTimeout;
  var accountsBefore = [];
  var returnsBefore = [];
  var currentReturn;
  var latestReturn;
  var deleteCurrentReturn = false;
  var returns = [];
  var currentYear = '2017-18';
  var account = {
      customFields: {
        orgCode: 'RPC'
      }
  };

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    //jasmine.DEFAULT_TIMEOUT_INTERVAL = 250000; even this not enough to run calcs
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 50000;
  });

  it("searches to take an initial baseline for the organisation", function(done) {
    var org = account.customFields.orgCode;
    $rh.getJSON('/returns/findByOrg/'+org, function(data, textStatus, jqXHR) {
      returnsBefore = data;
      window.returnsBefore = data;
      expect(jqXHR.status).toEqual(200);

      if (account.customFields.orgCode.indexOf('R')==1) {
        expect(returnsBefore.length).toEqual(6);

        expect(returnsBefore[0].name).toEqual('ERIC-2013-14-'+org);
        expect(returnsBefore[0].status).toEqual('Published');

        expect(returnsBefore[1].name).toEqual('ERIC-2014-15-'+org);
        expect(returnsBefore[1].status).toEqual('Published');

        expect(returnsBefore[2].name).toEqual('ERIC-2015-16-'+org);
        expect(returnsBefore[2].status).toEqual('Published');

        expect(returnsBefore[3].name).toEqual('ERIC-2016-17-'+org);
        expect(returnsBefore[3].status).toEqual('Published');
      }

      if (returnsBefore.length>0) {
        latestReturn = returnsBefore[returnsBefore.length-1];
        if (latestReturn.status == 'Published') deleteCurrentReturn = true;
        else {
          expect(latestReturn.status).toEqual('Draft');
          deleteCurrentReturn = false;
        }
      }

      done();
    });
  });

  it("fetches current year's ACTIVE SDU return(s) for the organisation, implicitly creating an empty one", function(done) {
    var org = account.customFields.orgCode;
    $rh.getJSON('/returns/findCurrentBySurveyNameAndOrg/SDU-'+currentYear+'/'+org, function(data, textStatus, jqXHR) {
      currentReturn = data;
      expect(jqXHR.status).toEqual(200);
      console.log('  return id: '+currentReturn.id);
      console.log('  return name: '+currentReturn.id);
      expect(currentReturn.status).toEqual('Draft');

      expect(currentReturn.answers).toBeDefined();
      console.log('  answer count: '+currentReturn.answers.length);

      var elec1314;
      var elec1415;
      var elec1516;
      var elec1617;
      var elec1718;
      for (idx in currentReturn.answers) {
        var a = currentReturn.answers[idx];
        if (a.question.name == 'ELEC_USED') {
          if (a.applicablePeriod == '2013-14') elec1314 = a;
          else if (a.applicablePeriod == '2014-15') elec1415 = a;
          else if (a.applicablePeriod == '2015-16') elec1516 = a;
          else if (a.applicablePeriod == '2016-17') elec1617 = a;
          else if (a.applicablePeriod == '2017-18') elec1718 = a;
          else console.warn('Unexpected elec answer for '+a.applicablePeriod);
        }
      }
      console.log('check expectation of ELEC_USED in 2013-14\n'+JSON.stringify(elec1314));
      expect(elec1314).toBeDefined(true);
      expect(parseFloat(elec1314.response)).toBeGreaterThan(0);
      expect(elec1314.revision).toEqual(currentReturn.revision);
      expect(elec1314.status).toEqual('Draft');
      expect(elec1314.derived).toEqual(false);
      console.log('check expectation of ELEC_USED in 2014-15\n'+JSON.stringify(elec1415));
      expect(elec1415).toBeDefined(true);
      expect(parseFloat(elec1415.response)).toBeGreaterThan(0);
      expect(elec1415.revision).toEqual(currentReturn.revision);
      expect(elec1415.status).toEqual('Draft');
      expect(elec1415.derived).toEqual(false);
      console.log('check expectation of ELEC_USED in 2015-16\n'+JSON.stringify(elec1516));
      expect(elec1516).toBeDefined(true);
      expect(parseFloat(elec1516.response)).toBeGreaterThan(0);
      expect(elec1516.revision).toEqual(currentReturn.revision);
      expect(elec1516.status).toEqual('Draft');
      expect(elec1516.derived).toEqual(false);
      console.log('check expectation of ELEC_USED in 2016-17\n'+JSON.stringify(elec1617));
  //    expect(elec1617).toBeDefined(true);
      console.log('check expectation of ELEC_USED in 2017-18\n'+JSON.stringify(elec1718));
      expect(elec1718.response).toBeNull();
      expect(elec1718.revision).toEqual(currentReturn.revision);
      expect(elec1718.status).toEqual('Draft');
      expect(elec1718.derived).toEqual(false);

      done();
    });
  });

  it("fetches energy use report", function(done) {
    var org = account.customFields.orgCode;
    $rh.ajax({
      url: '/reports/'+org+'/'+currentYear+'/energy.csv',
      type: 'GET',
      contentType: 'text/plain',
      success: function(data, textStatus, jqXHR) {
        window.energyReport = data;
        expect(jqXHR.status).toEqual(200);
        var lines = data.split(/\n/);
//        Period,Gas Consumed,Oil Consumed,Coal Consumed,Steam Consumed,Hot Water Consumed,Electricity Consumed,Green electricity
//        2014-15,4733165,0,0,0,0,3343905,0
//        2015-16,4480334,0,0,0,0,4084709,0
//        2016-17,0,0,0,0,0,0,0
//        2017-18,0,0,0,0,0,0,0
        expect(lines[0]).toEqual('Period,Gas Consumed,Oil Consumed,Coal Consumed,Steam Consumed,Hot Water Consumed,Electricity Consumed,Green electricity');
        expect(lines[4]).toEqual(currentYear+',0,0,0,0,0,0,0');

        expect(lines[1].split(/,/)[1]).toBeGreaterThan(0); // first year first column (gas)
        expect(lines[1].split(/,/)[6]).toBeGreaterThan(0); // first year sixth column (elec)

        done();
      }
    });
  });

// TOO SLOW at present
//  it("runs calculations for current year's ACTIVE SDU return(s) for the organisation", function(done) {
//    $rh.ajax({
//      url: '/calculations/'+currentReturn.survey.name+'/'+currentReturn.org,
//      type: 'POST',
//      success: function(data, textStatus, jqXHR) {
//        expect(jqXHR.status).toEqual(200);
//        done();
//      }
//    });
//  });

  it("fetches energy emissions report", function(done) {
    var org = account.customFields.orgCode;
    $rh.ajax({
      url: '/reports/'+org+'/'+currentYear+'/energy-co2e.csv',
      type: 'GET',
      contentType: 'text/plain',
      success: function(data, textStatus, jqXHR) {
        window.energyEmissionsReport = data;
        expect(jqXHR.status).toEqual(200);
        var lines = data.split(/\n/);
        expect(lines[0]).toEqual('Period,Electricity,Gas,Oil,Coal,Steam,Hot water,Green electricity,Exported thermal');
        expect(lines[4]).toEqual(currentYear+',0,0,0,0,0,0,0,0');

//        expect(lines[1].split(/,/)[1]).toBeGreaterThan(0); // first year first column (elec)

        done();
      }
    });
  });

  it("deletes the added return, if indeed one was added", function(done) {
    if (deleteCurrentReturn) {
      $rh.ajax({
        url: '/returns/'+currentReturn.id,
        type: 'DELETE',
        success: function(data, textStatus, jqXHR) {
          expect(jqXHR.status).toEqual(204);
          done();
        }
      });
    } else {
      expect(latestReturn.status).toEqual('Draft');
      done();
    }
  });

  it("checks the organisation's returns are the same as the baseline", function(done) {
    var org = account.customFields.orgCode;
    $rh.getJSON('/returns/findByOrg/'+org, function(data, textStatus, jqXHR) {
      returns = data;
      expect(jqXHR.status).toEqual(200);
      expect(returns.length).toEqual(returnsBefore.length);

      done();
    });
  });

  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });
});
