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
var sc = new Charts();
var sb = new StackedBar();

ractive.observe('current.account', function(newValue, oldValue, keypath) {
  console.info('observe account change')
  ignored=[];
//  if (ractive.get('saveObserver') && ignored.indexOf(keypath)==-1) {
    console.log('current prop change: '+newValue +','+oldValue+' '+keypath);
    sc.bar('#carbonIntensityBar',[
        {"barName":"Intensity","barValue":ractive.get('current.account.customFields.carbonIntensity')},
        {"barName":"Sector average","barValue":ractive.get('current.account.customFields.sectorCarbonIntensity')}
      ],
      { 
    	margin: {top: 20, right: 20, bottom: 30, left: 60},
    	yAxisLabel: 'Carbon Intensity'
      }
    );
    sc.donut('#summaryDonut', [
        {"label":"Environment","value":ractive.get('current.account.customFields.environmentScore')},
        {"label":"Social","value":ractive.get('current.account.customFields.socialScore')},
        {"label":"Governance","value":ractive.get('current.account.customFields.governanceScore')},
        {"value":ractive.get('current.account.customFields.remainingScore')}
      ]
    );
    $($('#summaryDonut text')[2]).addClass('inverse');
    
    sb.displayDataSet("#carbonFootprintCorporateStackedBar", [
        {
        	"Category":"Carbon Emissions",
        	"Scope1":ractive.get('current.account.customFields.scope1tCO2e'),
        	"Scope2":ractive.get('current.account.customFields.scope2tCO2e'),
        	"Scope3":ractive.get('current.account.customFields.scope3tCO2e')
        }
      ],
      { minHeight:300, outerWidth:400, outerHeight:300, yAxisLabel:"tCO2e" }
    );
    
//  } else { 
//    console.warn  ('Skipped account graphs on change to '+keypath);
//  }
});