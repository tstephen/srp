/*******************************************************************************
 * Copyright 2011-2018 Tim Stephenson and contributors
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
function I18nController(baseUrl) { // jshint ignore:line
  var me = this;
  this.BASE_URL = baseUrl == undefined ? '' : baseUrl;
  /**
   * @deprecated Use getStrings.
   */
  this.localize = function(locale) {
    this.getStrings(locale);
  };
  this.getAgeString = function(millis) {
    return this.getDurationString(new Date()-millis)+' ago';
  };
  this.getDeadlineString = function(millis) {
    var millisToDeadline = millis-new Date();
    if (millisToDeadline <= 0) {
      return 'n/a';
    } else {
      return 'In '+this.getDurationString(millisToDeadline);
    }
  };
  this.getDurationString = function(millis) {
      if (isNaN(millis)) return 'n/a';
      var secs = millis / 1000;
      var mins = secs / 60;
      var hours = mins / 60;
      var days = hours / 24;
      var weeks = days / 7;
      var years = days / 365;
      if (secs < 1) {
          return 'less than a second';
      } else if (secs < 2) {
          return 'about a second';
      } else if (mins < 1) {
          return 'less than a minute';
      } else if (mins < 2) {
          return 'about a minute';
      } else if (mins < 60) {
          return 'about '+Math.floor(mins) + ' minutes';
      } else if (hours < 2) {
          return 'about an hour';
      } else if (days < 1) {
          return 'about '+Math.floor(hours) + ' hours';
      } else if (days < 2) {
          return 'about a day';
      } else if (years > 1) {
          return 'about ' + Math.floor(years) + ' years';
      } else if (weeks > 2) {
        return 'about ' + Math.floor(weeks) + ' weeks';
//      } else if (weeks > 1) {
//          return 'about ' + Math.floor(weeks) + ' weeks';
      } else {
          return 'about ' + Math.floor(days) + ' days';
      }
  };
  this.getStrings = function(locale) {
    if (locale===undefined) locale = 'en_GB';
    console.log('localising to: '+locale+'...');
    var jqxhr = $.ajax({
      type: 'GET',
      url: this.BASE_URL+'/js/i18n_'+locale+'.json',
      contentType: 'application/json',
      dataType: 'json',
      success: function(s) {
        console.log('strings returned: '+ s.length);
        me.strings = s;
        switch (jqxhr.status) {
        case 200:
          me.l10n();
          break;
        default:
          console.error('  failed to load i18n strings: '+jqxhr.status);
        }
      }
    });
  };
  this.l10n = function() {
    $('[data-i18n]').each(function(i,d){
      var code = $(d).data('i18n');
      console.log('... '+code+' = '+me.strings[code]);
      if (me.strings[code]!==undefined) $(d).empty().append(me.strings[code]);
    });
  };
  this.getStrings();
}
