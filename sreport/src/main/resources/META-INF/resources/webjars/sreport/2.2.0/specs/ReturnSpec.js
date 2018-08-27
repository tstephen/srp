/*******************************************************************************
 * Copyright 2015-2018 Tim Stephenson and contributors
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
  var returns = [];
  var currentYear = '2017-18';
  var account = {
      customFields: {
        orgCode: 'RDR'
      }
  };

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 50000;
  });

  it("searches to take an initial baseline for the organisation", function(done) {
    var org = account.customFields.orgCode;
    $rh.getJSON('/returns/findByOrg/'+org, function(data, textStatus, jqXHR) {
      returnsBefore = data;
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

      done();
    });
  });

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

  it("deletes the added return", function(done) {
    $rh.ajax({
      url: '/returns/'+currentReturn.id,
      type: 'DELETE',
      success: function(data, textStatus, jqXHR) {
        expect(jqXHR.status).toEqual(204);
        done();
      }
    });
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
