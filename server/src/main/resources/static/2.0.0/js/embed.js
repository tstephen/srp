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
var $tr = new EmbedOptions();
$tr.applyHeaderAndFooter();

function EmbedOptions() {
  this.options = { 
    header: '/partials/header.html',
    footer: '/partials/footer.html',
  }
  this.applyHeaderAndFooter = function() {
    $.get(this.options.header, function(response){
      //console.log('response: '+response)
      ractive.resetPartial('header',response);
      $('body').prepend(ractive.partials.header);
    });
    $.get(this.options.footer, function(response){
      //console.log('response: '+response)
      ractive.resetPartial('footer',response);
      $('body').append(ractive.partials.footer);
    });
    onElementHeightChange(document.body, function(){
      if ($('body').height()<window.innerHeight) {
        $('.site-footer .navbar').removeClass('navbar-static-bottom').addClass('navbar-fixed-bottom');
      } else {
        $('.site-footer .navbar').removeClass('navbar-fixed-bottom').addClass('navbar-static-bottom');
      }
    });
  };
}

function onElementHeightChange(elm, callback){
  var lastHeight = elm.clientHeight, newHeight;
  (function run() {
    newHeight = elm.clientHeight;
    if( lastHeight != newHeight )
        callback();
    lastHeight = newHeight;

    if( elm.onElementHeightChangeTimer )
        clearTimeout(elm.onElementHeightChangeTimer);

    elm.onElementHeightChangeTimer = setTimeout(run, 200);
  })();
}

