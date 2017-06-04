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
var os = new SrpSwagger();

function SrpSwagger() { 
  this.onSwaggerComplete = function() { 
    console.log('onSwaggerComplete');
    
    $('.info_title').empty().append('API Documentation');
    $('.info_description').remove();
    $('.info_name').empty().append('Created by Tim Stephenson (tim at knowprocess.com)');
    $('.info_license').empty().append('<a href="http://www.gnu.org/licenses/lgpl-3.0.en.html">LGPL license v3.0</a>');
    $('#header').css('background-color','white');
    $('#logo').remove();
    $('#header .swagger-ui-wrap .navbar-brand').remove();
  }
}
