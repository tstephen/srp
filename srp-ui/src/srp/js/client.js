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
function SrpClient(o) {
  var me = {
    options: o
  };

  function commonHeaders() {
    return {
      "Accept": "application/json, text/javascript",
      "X-Requested-With": "XMLHttpRequest",
      "Authorization": "Bearer "+me.options.token
    }
  }

  me.deleteCarbonFactor = function(cfactor) {
    return deleteEntity(cfactor, 'cfactors');
  }

  function deleteEntity(entity, entityPath) {
    return fetch(uriFromEntity(entity, entityPath), {
      "headers": commonHeaders(),
      "body": JSON.stringify(entity),
      "method": "DELETE",
      "mode": "cors"
    });
  }

  me.deleteIntervention = function(intervention) {
    return deleteEntity(intervention, 'interventions');
  }

  me.deleteInterventionType = function(interventionType) {
    return deleteEntity(interventionType, 'intervention-types');
  }

  me.deleteOrgType = function(orgType) {
    return deleteEntity(orgType, 'organisation-types');
  }

  me.deleteParameter = function(param) {
    return deleteEntity(param, 'parameters');
  }

  me.deleteQuestion = function(question) {
    return deleteEntity(question, 'question');
  }

  me.deleteWeightingFactor = function(wfactor) {
    return deleteEntity(wfactor, 'wfactors');
  }

  me.fetchCarbonFactor = function(uri) {
    return fetchEntity(uri);
  }

  me.fetchCarbonFactors = function() {
    return fetchEntities('cfactors');
  }

  function fetchEntities(entityPath, tenantId, params) {
    return fetch(me.options.server+'/' + (tenantId === undefined ? '' : tenantId + '/') + entityPath +'/'+(params === undefined ? '' : params), {
      "headers": commonHeaders(),
      "method": "GET",
      "mode": "cors"
    })
  }

  function fetchEntity(uri) {
    return fetch(uri, {
      "headers": commonHeaders(),
      "method": "GET",
      "mode": "cors"
    })
  }

  me.fetchInterventions = function(tenantId) {
    return fetchEntities('interventions', tenantId);
  }

  me.fetchInterventionTypes = function(tenantId) {
    return fetchEntities('intervention-types', tenantId);
  }

  me.fetchOrgTypes = function(tenantId) {
    return fetchEntities('organisation-types', tenantId);
  }

  me.fetchOrgTypesForReporting = function(tenantId) {
    return fetchEntities('organisation-types', tenantId, '?filter=reportingType');
  }

  me.fetchParameter = function(uri) {
    return fetchEntity(uri);
  }

  me.fetchParameters = function(tenantId) {
    return fetchEntities('parameters', tenantId);
  }

  me.fetchQuestion = function(uri) {
    return fetchEntity(uri);
  }

  me.fetchQuestions = function(tenantId) {
    return fetchEntities('questions', tenantId);
  }

  me.fetchReturn = function(surveyName, org) {
    return fetch(me.options.server+'/returns/findCurrentBySurveyNameAndOrg/'+surveyName+'/'+org, {
      "headers": {
        "Accept": "application/json, text/javascript",
        "X-Requested-With": "XMLHttpRequest",
        "X-Authorization": "Bearer "+me.options.token,
        "Authorization": "Bearer "+me.options.token,
        "Cache-Control": "no-cache",
      },
      "method": "GET",
      "mode": "cors"
    });
  }

  me.fetchSurvey = function(surveyName, successHandler) {
    fetch(me.options.server+'/surveys/findByName/'+surveyName, {
      "headers": commonHeaders(),
      "method": "GET",
      "mode": "cors"
    })
    .then(response => response.json())
    .then(data => successHandler(data));
  }

  me.fetchWeightingFactor = function(uri) {
    return fetchEntity(uri);
  }

  me.fetchWeightingFactors = function() {
    return fetchEntities('wfactors');
  }

  me.saveAnswer = function(rtn, answer, successHandler) {
    fetch(me.options.server+'/returns/'+rtn.id+'/answers/'+answer.question.name+'/'+answer.applicablePeriod, {
      "headers": {
        "Accept": "application/json",
        "X-Requested-With": "XMLHttpRequest",
        "Authorization": "Bearer "+me.options.token,
        "Content-Type": "application/json"
      },
      "body": answer.response,
      "method": "POST",
      "mode": "cors"
    })
    .then(() => successHandler());
  }

  me.saveCarbonFactor = function(cFactor, tenantId) {
    return saveEntity(cFactor, 'cfactors', tenantId);
  }

  function saveEntity(entity, entityPath, tenantId) {
    var headers = commonHeaders();
    headers["Content-Type"] = "application/json";
    let uri = uriFromEntity(entity, entityPath);
    return fetch(uri === undefined ? me.options.server + '/' + tenantId + '/'+ entityPath +'/' : uri, {
      "headers": headers,
      "body": JSON.stringify(entity),
      "method": uri === undefined ? 'POST' : 'PUT',
      "mode": "cors"
    });
  }

  me.saveIntervention = function(intervention, tenantId) {
    return saveEntity(intervention, 'intervention', tenantId);
  }

  me.saveInterventionType = function(interventionType, tenantId) {
    return saveEntity(interventionType, 'intervention-types', tenantId);
  }

  me.saveOrgType = function(orgType, tenantId) {
    return saveEntity(orgType, 'organisation-types', tenantId);
  }

  me.saveParameter = function(param, tenantId) {
    return saveEntity(param, 'parameters', tenantId);
  }

  me.saveQuestion = function(question, tenantId) {
    return saveEntity(question, 'questions', tenantId);
  }

  me.saveWeightingFactor = function(wFactor, tenantId) {
    return saveEntity(wFactor, 'wfactors', tenantId);
  }

  function uriFromEntity(entity, entityPath) {
    var uri;
    if (entity['links']!=undefined) {
      $.each(entity.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if (entity['_links']!=undefined) {
      uri = entity._links.self.href;
    } else if (entity['id']!=undefined) {
      uri = entityPath+'/'+entity.id;
    }

    return uri;
  }

  return me;
}
