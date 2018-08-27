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
describe("Sustainability Management API", function() {
  var tenantId = 'sdu';
  var $rh = new RestEntityHelper({
    server: window['$env'] == undefined ? 'http://localhost:8080' : $env.server,
    tenantId: tenantId
  });

  var originalTimeout;
  var accountsBefore = [];
  var returnsBefore = [];
  var returns = [];

  beforeEach(function() {
    originalTimeout = jasmine.DEFAULT_TIMEOUT_INTERVAL;
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 50000;
  });

  it("ensures questions initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/questions', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  it("ensures ERIC-2013-14 data set initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/answers/ERIC-2013-14', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  it("ensures ERIC-2014-15 data set initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/answers/ERIC-2014-15', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  it("ensures ERIC-2015-16 data set initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/answers/ERIC-2015-16', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  it("ensures ERIC-2016-17 data set initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/answers/ERIC-2016-17', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  it("ensures surveys initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/surveys', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      expect(data.surveys).toEqual(6);
      done();
    });
  });
  it("ensures all data sets initialised", function(done) {
    $rh.getJSON('/admin/data-mgmt/', function(data, textStatus, jqXHR) {
      returnsBefore = data;
      expect(jqXHR.status).toEqual(200);
      done();
    });
  });
  afterEach(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = originalTimeout;
  });
});
