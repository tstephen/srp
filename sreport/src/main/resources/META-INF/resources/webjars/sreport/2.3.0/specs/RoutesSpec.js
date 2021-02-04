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
describe("Sustainability Routes", function() {
  var tenantId = 'sdu';
  var $rh = new RestEntityHelper({
    server: window['$env'] == undefined ? 'http://localhost:8080' : $env.server,
    tenantId: tenantId
  });

  var webServer = 'https://srp.digital';
  var originalTimeout;
  var accountsBefore = [];
  var returnsBefore = [];
  var returns = [];

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 500;
  });

  it("checks we have a return page", function(done) {
    $rh.ajax({
      url: webServer+'/srp/return.html',
      type: 'GET',
      contentType: 'text/html',
      success: function(data, textStatus, jqXHR) {
        expect(jqXHR.status).toEqual(200);
        done();
      }
    });
  });
  it("checks we have a report page", function(done) {
    $rh.ajax({
      url: webServer+'/srp/report.html',
      type: 'GET',
      contentType: 'text/html',
      success: function(data, textStatus, jqXHR) {
        expect(jqXHR.status).toEqual(200);
        done();
      }
    });
  });
  it("checks we have a treasury report page", function(done) {
    $rh.ajax({
      url: webServer+'/srp/treasury.html',
      type: 'GET',
      contentType: 'text/html',
      success: function(data, textStatus, jqXHR) {
        expect(jqXHR.status).toEqual(200);
        done();
      }
    });
  });
  it("checks we have a profile page", function(done) {
    $rh.ajax({
      url: webServer+'/me.html',
      type: 'GET',
      contentType: 'text/html',
      success: function(data, textStatus, jqXHR) {
        expect(jqXHR.status).toEqual(200);
        done();
      }
    });
  });

  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });
});
