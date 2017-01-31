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

var ractive = new Ractive({
  el: 'containerSect',
  template: '#template',
  data: {
    disclosure: function () {
      return ractive.toDisclosure(ractive.data.questionCategories);
    },
    questionCategories: [
      { 
        category: "products", questions: [ 
          { id: "eClassCode", label: "Please identify/confirm the eClass code for this declaration", placeholder: "eClass Code", type: "text" }
        ]
      },{ 
        category: "risk", questions: [
          { id: "codeOfConduct", label: "Have you signed a supplier code of conduct?", type: "yesno" },
          { id: "supplierCodeOfConduct", label: "Do you require your suppliers to sign a ethical code of conduct?", type: "yesno" },
          { id: "certified", label: "Do you carry any sustainability certifications (incl environmental management - ISO 14000 series, EMAS)?", type: "yesno" },
          { id: "productionCountry", label: "Please indicate the products' production country of origin", placeholder: "Country of origin", type: "text" }
        ]
      },{ 
        category: "materials", questions: [
          { id: "containsProscribedMaterials", label: "Do your products contain any of the following raw materials? Tin, Titanium, Tungsten", type: "yesno" },
          { id: "primaryMaterial", label: "Please indicate the products' primary input material", placeholder: "Primary input material", type: "text" },
          { id: "secondaryMaterial", label: "Please indicate the products' secondary input material", placeholder: "Secondary input material", type: "text" }
        ]
      },{ 
        category: "carbon", questions: [
          { id: "co2Account", label: "Do you currently account for the carbon emissions of your operation (in some form or another)", type: "yesno" },
          { id: "co2ProductFootprint", label: "Have you calculated the CO2 footprint for these products?", type: "yesno" },
          { id: "memberCdp", label: "Are you a supplier member of the CDP?", type: "yesno" },
          { id: "signatoryCdp", label: "Are you a signatory to the CDP?", type: "yesno" }
        ]
      },{ 
        category: "leadership", questions: [
          { id: "sustainabilityVisionStatement", label: "Do you have a company sustainability vision or value statement?", type: "yesno" },
          { id: "memberTradeAssoc", label: "Are you a member of your industry trade organisation / board?", type: "yesno" }
        ]
      },{ 
        category: "governance", questions: [
          { id: "useSpotContracts", label: "Do you make use of spot contracts for procurement of input materials and products?", type: "yesno" },
          { id: "useSubcontractors", label: "Do your suppliers use sub-contractors to meet requirements?", type: "yesnona" }
        ]
      },{ 
        category: "water", questions: [
          { id: "waterImpact", label: "Do you account for water impact and usage within your operation?", type: "yesno" }
        ]
      },{ 
        category: "waste", questions: [
          { id: "wasteManagementPolicy", label: "Do you have a waste management, recycling and reduction strategy?", type: "yesno" }
        ]
      }
    ]
  },
  revealDetails: function(id) {
    console.log('show details for: '+id);
    $('#'+id+'Details').slideDown(TRANSITION_DURATION).removeClass('hidden');
  },
  sendMessage: function() {
    if (document.forms['disclosureForm'].checkValidity()) {
      var disclosure = JSON.stringify(ractive.toDisclosure(ractive.data.questions));
      console.log('Sending message: '+disclosure);
      // $('html, body').css("cursor", "wait");
      return $.ajax({
        type: 'POST',
        url: ractive.getServer()+'/msg/trakeo.sustainabilityDisclosure.json',
        /* Uncomment to send as single JSON blob instead of form params
        contentType: 'application/json',*/
        data: {json:disclosure},
        dataType: 'text',
        timeout: 30000,
        success: function(response, textStatus, jqxhr) {
          console.log('successfully start instance by msg: '+jqxhr.getResponseHeader('Location'));
          console.log('  headers: '+JSON.stringify(jqxhr.getAllResponseHeaders()));
          console.log('  response: '+response);
          // $p.hideActivityIndicator();
        },
        error: function(jqXHR, textStatus, errorThrown) {
          var msg = 'Error saving: '+textStatus+' '+errorThrown;
          window.err = jqXHR;
          switch (jqXHR.status) {
            case 404:
              msg = "There is no workflow deployed to handle '"+msgName+"' messages. Please contact your administrator.";
            }
            console.error(msg);
            // $p.hideActivityIndicator(msg, 'error');
          },
          complete: function(data, textStatus, jqxhr) {
            //console.log('successfully start instance by msg: '+jqxhr.getResponseHeader('Location'));
            console.log('complete:'+textStatus+' data: '+JSON.stringify(data)+' jqxhr'+jqxhr);
          }
        });
    } else {
      console.log('Disclosure incomplete, returning to form');
    }
  },
  toDisclosure: function(cats) {
    var o = new Object();
    $.each(cats, function(i,d){
      $.each(d.questions, function(j,e){
        o[e.id] = e.response;
      });
    });
    return JSON.stringify(o);
  }
});
