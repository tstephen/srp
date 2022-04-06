/*******************************************************************************
 * Copyright 2015-2020 Tim Stephenson and contributors
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

/* Object extensions */

Array.prototype.clean = function(deleteValue) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] == deleteValue) {
      this.splice(i, 1);
      i--;
    }
  }
  return this;
};

/**
 * @return The first array element whose 'k' field equals 'v'.
 */
Array.prototype.findBy = function(k,v,arr) {
  for (var idx in arr) {
    if (arr[idx][k]==v) return arr[idx];
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return arr[idx];
  }
};

/**
 * @return All  array elements whose 'k' field equals 'v'.
 */
Array.prototype.findAll = function(k,v,arr) {
  var retArr = [];
  for (var idx in arr) {
    if (arr[idx][k]==v) retArr.push(arr[idx]);
    else if ('selfRef'==k && arr[idx][k] != undefined && arr[idx][k].endsWith(v)) return retArr.push(arr[idx]);
  }
  return retArr;
};
Array.prototype.uniq = function() {
  return this.sort().filter(function(el,i,a){if(i==a.indexOf(el))return 1;return 0;});
};
/******************************** Polyfills **********************************/
// ref https://developer.mozilla.org/en/docs/Web/JavaScript/Reference/Global_Objects/String/endsWith
if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(searchString, position) {
      var subjectString = this.toString();
      if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
        position = subjectString.length;
      }
      position -= searchString.length;
      var lastIdx = subjectString.indexOf(searchString, position);
      return lastIdx !== -1 && lastIdx === position;
  };
}

function parseDateIEPolyFill(timeString) {
  var start = timeString.substring(0,timeString.indexOf('.'));
  var offset;
  if (timeString.indexOf('-',timeString.indexOf('T'))!=-1) {
    offset = timeString.substr(timeString.indexOf('-',timeString.indexOf('T')),3)+':'+timeString.substr(timeString.indexOf('-',timeString.indexOf('T'))+3,2);
  } else if (timeString.indexOf('+')!=-1) {
    offset = timeString.substr(timeString.indexOf('+'),3)+':'+timeString.substr(timeString.indexOf('+')+3,2);
  }
  return new Date(Date.parse(start+offset));
}

/************************************************************
 * MD5 (Message-Digest Algorithm) http://www.webtoolkit.info/
 ***********************************************************/
function md5(string) {
 function RotateLeft(lValue, iShiftBits) {
 return (lValue<<iShiftBits) | (lValue>>>(32-iShiftBits));
 }

 function AddUnsigned(lX,lY) {
 var lX4,lY4,lX8,lY8,lResult;
 lX8 = (lX & 0x80000000);
 lY8 = (lY & 0x80000000);
 lX4 = (lX & 0x40000000);
 lY4 = (lY & 0x40000000);
 lResult = (lX & 0x3FFFFFFF)+(lY & 0x3FFFFFFF);
 if (lX4 & lY4) {
 return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
 }
 if (lX4 | lY4) {
 if (lResult & 0x40000000) {
 return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
 } else {
 return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
 }
 } else {
 return (lResult ^ lX8 ^ lY8);
 }
 }

 function F(x,y,z) { return (x & y) | ((~x) & z); }
 function G(x,y,z) { return (x & z) | (y & (~z)); }
 function H(x,y,z) { return (x ^ y ^ z); }
 function I(x,y,z) { return (y ^ (x | (~z))); }

 function FF(a,b,c,d,x,s,ac) {
 a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
 return AddUnsigned(RotateLeft(a, s), b);
 }

 function GG(a,b,c,d,x,s,ac) {
 a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
 return AddUnsigned(RotateLeft(a, s), b);
 }

 function HH(a,b,c,d,x,s,ac) {
 a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
 return AddUnsigned(RotateLeft(a, s), b);
 }

 function II(a,b,c,d,x,s,ac) {
 a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
 return AddUnsigned(RotateLeft(a, s), b);
 }

 function ConvertToWordArray(string) {
 var lWordCount;
 var lMessageLength = string.length;
 var lNumberOfWords_temp1=lMessageLength + 8;
 var lNumberOfWords_temp2=(lNumberOfWords_temp1-(lNumberOfWords_temp1 % 64))/64;
 var lNumberOfWords = (lNumberOfWords_temp2+1)*16;
 var lWordArray=Array(lNumberOfWords-1);
 var lBytePosition = 0;
 var lByteCount = 0;
 while ( lByteCount < lMessageLength ) {
 lWordCount = (lByteCount-(lByteCount % 4))/4;
 lBytePosition = (lByteCount % 4)*8;
 lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount)<<lBytePosition));
 lByteCount++;
 }
 lWordCount = (lByteCount-(lByteCount % 4))/4;
 lBytePosition = (lByteCount % 4)*8;
 lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80<<lBytePosition);
 lWordArray[lNumberOfWords-2] = lMessageLength<<3;
 lWordArray[lNumberOfWords-1] = lMessageLength>>>29;
 return lWordArray;
 }

 function WordToHex(lValue) {
 var WordToHexValue='',WordToHexValue_temp='',lByte,lCount;
 for (lCount = 0;lCount<=3;lCount++) {
 lByte = (lValue>>>(lCount*8)) & 255;
 WordToHexValue_temp = '0' + lByte.toString(16);
 WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length-2,2);
 }
 return WordToHexValue;
 }

 function Utf8Encode(string) {
 string = string.replace(/\r\n/g,'\n');
 var utftext = '';

 for (var n = 0; n < string.length; n++) {
 var c = string.charCodeAt(n);

 if (c < 128) {
 utftext += String.fromCharCode(c);
 }
 else if((c > 127) && (c < 2048)) {
 utftext += String.fromCharCode((c >> 6) | 192);
 utftext += String.fromCharCode((c & 63) | 128);
 }
 else {
 utftext += String.fromCharCode((c >> 12) | 224);
 utftext += String.fromCharCode(((c >> 6) & 63) | 128);
 utftext += String.fromCharCode((c & 63) | 128);
 }
 }

 return utftext;
 }

 var x=Array();
 var k,AA,BB,CC,DD,a,b,c,d;
 var S11=7, S12=12, S13=17, S14=22;
 var S21=5, S22=9 , S23=14, S24=20;
 var S31=4, S32=11, S33=16, S34=23;
 var S41=6, S42=10, S43=15, S44=21;

 string = Utf8Encode(string);

 x = ConvertToWordArray(string);

 a = 0x67452301; b = 0xEFCDAB89; c = 0x98BADCFE; d = 0x10325476;

 for (k=0;k<x.length;k+=16) {
 AA=a; BB=b; CC=c; DD=d;
 a=FF(a,b,c,d,x[k+0], S11,0xD76AA478);
 d=FF(d,a,b,c,x[k+1], S12,0xE8C7B756);
 c=FF(c,d,a,b,x[k+2], S13,0x242070DB);
 b=FF(b,c,d,a,x[k+3], S14,0xC1BDCEEE);
 a=FF(a,b,c,d,x[k+4], S11,0xF57C0FAF);
 d=FF(d,a,b,c,x[k+5], S12,0x4787C62A);
 c=FF(c,d,a,b,x[k+6], S13,0xA8304613);
 b=FF(b,c,d,a,x[k+7], S14,0xFD469501);
 a=FF(a,b,c,d,x[k+8], S11,0x698098D8);
 d=FF(d,a,b,c,x[k+9], S12,0x8B44F7AF);
 c=FF(c,d,a,b,x[k+10],S13,0xFFFF5BB1);
 b=FF(b,c,d,a,x[k+11],S14,0x895CD7BE);
 a=FF(a,b,c,d,x[k+12],S11,0x6B901122);
 d=FF(d,a,b,c,x[k+13],S12,0xFD987193);
 c=FF(c,d,a,b,x[k+14],S13,0xA679438E);
 b=FF(b,c,d,a,x[k+15],S14,0x49B40821);
 a=GG(a,b,c,d,x[k+1], S21,0xF61E2562);
 d=GG(d,a,b,c,x[k+6], S22,0xC040B340);
 c=GG(c,d,a,b,x[k+11],S23,0x265E5A51);
 b=GG(b,c,d,a,x[k+0], S24,0xE9B6C7AA);
 a=GG(a,b,c,d,x[k+5], S21,0xD62F105D);
 d=GG(d,a,b,c,x[k+10],S22,0x2441453);
 c=GG(c,d,a,b,x[k+15],S23,0xD8A1E681);
 b=GG(b,c,d,a,x[k+4], S24,0xE7D3FBC8);
 a=GG(a,b,c,d,x[k+9], S21,0x21E1CDE6);
 d=GG(d,a,b,c,x[k+14],S22,0xC33707D6);
 c=GG(c,d,a,b,x[k+3], S23,0xF4D50D87);
 b=GG(b,c,d,a,x[k+8], S24,0x455A14ED);
 a=GG(a,b,c,d,x[k+13],S21,0xA9E3E905);
 d=GG(d,a,b,c,x[k+2], S22,0xFCEFA3F8);
 c=GG(c,d,a,b,x[k+7], S23,0x676F02D9);
 b=GG(b,c,d,a,x[k+12],S24,0x8D2A4C8A);
 a=HH(a,b,c,d,x[k+5], S31,0xFFFA3942);
 d=HH(d,a,b,c,x[k+8], S32,0x8771F681);
 c=HH(c,d,a,b,x[k+11],S33,0x6D9D6122);
 b=HH(b,c,d,a,x[k+14],S34,0xFDE5380C);
 a=HH(a,b,c,d,x[k+1], S31,0xA4BEEA44);
 d=HH(d,a,b,c,x[k+4], S32,0x4BDECFA9);
 c=HH(c,d,a,b,x[k+7], S33,0xF6BB4B60);
 b=HH(b,c,d,a,x[k+10],S34,0xBEBFBC70);
 a=HH(a,b,c,d,x[k+13],S31,0x289B7EC6);
 d=HH(d,a,b,c,x[k+0], S32,0xEAA127FA);
 c=HH(c,d,a,b,x[k+3], S33,0xD4EF3085);
 b=HH(b,c,d,a,x[k+6], S34,0x4881D05);
 a=HH(a,b,c,d,x[k+9], S31,0xD9D4D039);
 d=HH(d,a,b,c,x[k+12],S32,0xE6DB99E5);
 c=HH(c,d,a,b,x[k+15],S33,0x1FA27CF8);
 b=HH(b,c,d,a,x[k+2], S34,0xC4AC5665);
 a=II(a,b,c,d,x[k+0], S41,0xF4292244);
 d=II(d,a,b,c,x[k+7], S42,0x432AFF97);
 c=II(c,d,a,b,x[k+14],S43,0xAB9423A7);
 b=II(b,c,d,a,x[k+5], S44,0xFC93A039);
 a=II(a,b,c,d,x[k+12],S41,0x655B59C3);
 d=II(d,a,b,c,x[k+3], S42,0x8F0CCC92);
 c=II(c,d,a,b,x[k+10],S43,0xFFEFF47D);
 b=II(b,c,d,a,x[k+1], S44,0x85845DD1);
 a=II(a,b,c,d,x[k+8], S41,0x6FA87E4F);
 d=II(d,a,b,c,x[k+15],S42,0xFE2CE6E0);
 c=II(c,d,a,b,x[k+6], S43,0xA3014314);
 b=II(b,c,d,a,x[k+13],S44,0x4E0811A1);
 a=II(a,b,c,d,x[k+4], S41,0xF7537E82);
 d=II(d,a,b,c,x[k+11],S42,0xBD3AF235);
 c=II(c,d,a,b,x[k+2], S43,0x2AD7D2BB);
 b=II(b,c,d,a,x[k+9], S44,0xEB86D391);
 a=AddUnsigned(a,AA);
 b=AddUnsigned(b,BB);
 c=AddUnsigned(c,CC);
 d=AddUnsigned(d,DD);
 }

 var temp = WordToHex(a)+WordToHex(b)+WordToHex(c)+WordToHex(d);

 return temp.toLowerCase();
}

var EASING_DURATION = 500;
var fadeOutMessages = true;

/**
 * Extends Ractive to handle offers some standard controls and a re-branding mechanism.
 */
var BaseRactive = Ractive.extend({ // jshint ignore:line
  CSRF_TOKEN: 'XSRF-TOKEN',
  srp: new SrpClient({
    server: $env.server,
  }),
  addDataList: function(d, data) {
    if (d.name == undefined) {
      console.warn('Data list with selector "'+d.ref+'" has no name, please add one to use as a datalist');
      return;
    }
    $('datalist#'+d.name).remove();
    $('body').append('<datalist id="'+d.name+'">');
    if (data == null) {
      console.error('No data for datalist: '+d.name+', please fix configuration');
    } else {
      ractive.set('saveObserver', false);
      ractive.set(d.name, data);
      ractive.set('saveObserver', true);
      $.each(data, function (i,e) {
        $('datalist#'+d.name).append('<option value="'+e.name+'">'+e.name+'</option>');
      });
    }
  },
  applyAccessControl: function() {
    console.info('applyAccessControl');
    if (location.href.indexOf('public')==-1 && ractive.get('tenant.access.readonly')) {
      fadeOutMessages=false;
      ractive.showMessage('System is in read-only mode', 'alert-warning');
      $('input,select,textarea').attr('readonly','readonly').attr('disabled','disabled');
    }
  },
  applyBranding: function() {
    if (ractive.get('tenant')==undefined) return ;
    var tenant = ractive.get('tenant.id');
    if (tenant != undefined) {
      $('head').append('<link href="/'+tenant+'/css/'+tenant+'-1.0.0.css" rel="stylesheet">');
      if (ractive.get('tenant.theme.logoUrl')!=undefined) {
        $('.navbar-brand').empty().append('<img src="'+ractive.get('tenant.theme.logoUrl')+'" alt="logo"/>');
      }
      if (ractive.get('tenant.theme.iconUrl')!=undefined) {
          $('link[rel="icon"]').attr('href',ractive.get('tenant.theme.iconUrl'));
      }
      ractive.initContentEditable();// required here for the tenant switcher
      // tenant partial templates
      $.each(ractive.get('tenant').partials, function(i,d) {
        $.get(d.url, function(response){
//          console.log('partial '+d.url+' response: '+response);
          try {
            ractive.set('saveObserver', false);
            ractive.resetPartial(d.name,response);
          } catch (e) {
            console.error('Unable to reset partial '+d.name+': '+e);
          }
          ractive.set('saveObserver', true);
        });
      });
      ractive.applyAccessControl();
      if (ractive.brandingCallbacks!=undefined) ractive.brandingCallbacks.fire();
    }
  },
  entityName: function(entity) {
    console.info('entityName');
    var id = ractive.uri(entity);
    var lastSlash = id.lastIndexOf('/');
    return id.substring(id.lastIndexOf('/', lastSlash-1)+1, lastSlash);
  },
  fetchConfig: function() {
    console.info('fetchConfig');
    $.getJSON('/configuration', function(data) {
      ractive.set('server',data.clientContext);
    });
  },
  filter: function(filter) {
    console.log('filter: '+JSON.stringify(filter));
    ractive.set('filter',filter);
    $('.omny-dropdown.dropdown-menu li').removeClass('selected');
    $('.omny-dropdown.dropdown-menu li:nth-child('+filter.idx+')').addClass('selected');
    ractive.showSearchMatched();
    $('input[type="search"]').blur();
  },
  getCookie: function(name) {
    //console.log('getCookie: '+name)
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length == 2) return parts.pop().split(";").shift();
  },
  getAttribute: function(attr) {
    return ractive.getProfile().attributes[attr];
  },
  getProfile: function() {
    var profile = localStorage.getItem('profile');
    if (profile == undefined) {
      alert('Unable to authenticate you at the moment, please try later');
      return;
    }
    profile = JSON.parse(profile);
    var username = profile.username;
    console.log('getProfile: '+username);
    if (username) {
      ractive.set('saveObserver', false);
      ractive.set('profile',profile);
      $('.profile-img').empty().append('<img class="img-rounded" src="//www.gravatar.com/avatar/'+ractive.hash(ractive.get('profile.email'))+'?s=34" title="'+ractive.get('profile.email')+'"/>');
      if (ractive.hasRole('super_admin')) $('.super-admin').show();
      ractive.loadTenantConfig(ractive.get('tenant.id'));
      ractive.set('saveObserver', true);
      /*} else if (ractive.get('tenant') && $auth.isPublic(window.location.href)) {
        var tenant = ractive.get('tenant.id');
        console.warn('... page supplied default tenant:'+tenant);
        ractive.loadTenantConfig(ractive.get('tenant.id'));
      */
      return profile;
    }
  },
  getServer: function() {
    return ractive.get('server')==undefined ? '' : ractive.get('server');
  },
  hash: function(email) {
    if (email==undefined) return;
    return md5(email.trim().toLowerCase());
  },
  hasRole: function(role) {
    //console.info('hasRole: ' + role);
    try {
      return (ractive.keycloak.hasRealmRole(role));
    } catch (e) {
      console.warn('No keycloak, using legacy authentication');
      return this.hasRoleLegacy(role);
    }
  },
  hasRoleLegacy: function(role) {
    var ractive = this;
    if (this && this.get('profile')) {
      var hasRole;
      if (ractive.get('profile.groups')!= undefined) {
        hasRole = ractive.get('profile.groups').filter(function(g) { return g.id==role; });
      }
      return hasRole!=undefined && hasRole.length>0;
    }
    return false;
  },
  hideLogin: function() {
    $('#loginSect').slideUp();
  },
  hideMessage: function() {
    $('#messages, .messages').hide();
  },
  hideResults: function() {
    var entityName = ractive.get('entityName');
    $('#'+entityName+'sTableToggle').addClass('kp-icon-caret-right').removeClass('kp-icon-caret-down');
    $('#'+entityName+'sTable').slideUp();
    $('#currentSect').slideDown({ queue: true });
  },
  hideUpload: function () {
    console.log('hideUpload...');
    $('#upload').slideUp();
  },
  id: function(entity) {
    console.log('id: '+entity);
    var id = ractive.uri(entity);
    return id.substring(id.lastIndexOf('/')+1);
  },
  initAbout: function() {
    $('.powered-by-icon').empty().append('<img src="/srp/images/icon/favicon.ico" alt="Sustainable Resource Planning™">');
  },
  initAutoComplete: function() {
    console.log('initAutoComplete');
    $.each(ractive.get('tenant.typeaheadControls'), function(i,d) {
      //console.log('binding ' +d.url+' to typeahead control: '+d.selector);
      if (d.url==undefined) {
        ractive.addDataList(d,d.values);
      } else {
        var url = d.url;
        if (url.indexOf('//')==-1) url = ractive.getServer()+url;
        $.get(url, function(data){
          ractive.addDataList(d,data);
        },'json');
      }
    });
  },
  initAutoNumeric: function() {
    console.log('initAutoNumeric');
    document.querySelectorAll('.autoNumeric').forEach(function(d) {
      $(d).autoNumeric('destroy');
      $(d).autoNumeric('init');
    });
  },
  initContentEditable: function() {
    console.log('initContentEditable');
    $("[contenteditable]").focus(function() {
      console.log('click '+this.id);
      selectElementContents(this);
    });
  },
  initControls: function() {
    console.log('initControls');
    ractive.initAbout();
    ractive.initAutoComplete();
    ractive.initAutoNumeric();
    ractive.initDatepicker();
    ractive.initContentEditable();
    ractive.initHelp();
    ractive.initShortKeys();
  },
  initDatepicker: function() {
    console.log('initDatepicker');
    if ($('.datepicker')!=undefined && $('.datepicker').length>0) {
      $('.datepicker').datepicker({
        format: "dd/mm/yyyy",
        autoclose: true,
        todayHighlight: true
      });
    }
  },
  initHelp: function() {
    $( "body" ).keypress(function( event ) {
      switch (event.which) { // ref http://keycode.info/
      case 47: // forward slash on my mac
      case 191: // forward slash (allegedly)
          $('.search').focus();
          event.preventDefault();
          break;
      case 63: // ?
         $('#helpModal').modal({});
         event.preventDefault();
         break;
      default:
        //console.log("No Handler for .keypress() called with: "+event.which);
      }
    });
  },
  initShortKeys: function() {
    $( "body" ).keypress(function( event ) {
      if (event.target.tagName.toLowerCase() == 'input' || event.target.tagName.toLowerCase() == 'textarea') return;
      switch (event.which) { // ref http://keycode.info/
      case 13: // enter
        $(event.target).blur().focus();
        break;
      case 47: // forward slash on both Mac and Linux
      case 191: // forward slash (allegedly)
        $('.search').focus();
        event.preventDefault();
        break;
      case 63: // ?
        $('#helpModal').modal({});
        event.preventDefault();
        break;
      default:
        //console.log("No Handler for .keypress() called with: "+event.which);
      }
    });
  },
  initTags: function() {
    console.info('initTags');
    $('[data-bind]').each(function(i,d) {
      $(d).val(ractive.get($(d).data('bind'))).css('display','none');
    });

    if ($(".tag-ctrl").is(":ui-tagit")) $(".tag-ctrl").tagit('destroy');
    $(".tag-ctrl").tagit({
      allowSpaces: true,
      placeholderText: "Comma separated tags",
      showAutocompleteOnFocus: true,
      afterTagAdded: function(event) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      },
      afterTagRemoved: function(event) {
        ractive.set($(event.target).data('bind'),$(event.target).val());
      }
    });
  },
  loadStandardPartial: function(name,url) {
    //console.log('loading...: '+d.name)
      $.get(url, function(response) {
        //console.log('... loaded: '+d.name)
        //console.log('response: '+response)
        if (ractive != undefined) {
          try {
            ractive.set('saveObserver', false);
            ractive.resetPartial(name,response);
            ractive.set('saveObserver', true);
          } catch (e) {
            console.warn('Unable to reset partial '+name+': '+e);
          }
        }
      });
    },
  loadStandardPartials: function(stdPartials) {
    console.info('loadStandardPartials');
    if (ractive == undefined || stdPartials == undefined) return;
    $.each(stdPartials, function(i,d) {
      ractive.loadStandardPartial(d.name, d.url);
    });
  },
  loadTenantConfig: function(tenant) {
    if ('$auth' in window && $auth.loginInProgress) {
      console.info('skip tenant load while logging in');
      return;
    }
    if (tenant==undefined || tenant=='undefined') $auth.showLogin();
    console.info('loadTenantConfig:'+tenant);
    $.getJSON('https://cloud.knowprocess.com/tenants/'+tenant+'/'+tenant+'.json', function(response) {
      //console.log('... response: '+JSON.stringify(response));
      ractive.set('saveObserver', false);
      ractive.set('tenant', response);
      ractive.applyBranding();
      ractive.initAutoComplete();
      ractive.set('saveObserver', true);
      if (ractive.tenantCallbacks!=undefined) ractive.tenantCallbacks.fire();
    });
  },
  logout: function() {
    try {
      ractive.keycloak.logout();
    } catch (e) {
      console.warn('Logging out of legacy authentication');
      $auth.logout();
    }
  },
  oninit: function() {
    this.initShortKeys();
  },
  parseDate: function(timeString) {
    var d = new Date(timeString);
    // IE strikes again
    if (d == 'Invalid Date') d = parseDateIEPolyFill(timeString);
    return d;
  },
  showDisconnected: function(msg) {
    console.log('showDisconnected: '+msg);
    if ($('#connectivityMessages').length>0) {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-warning">'+msg+'</div>').show();
    }
  },
  showError: function(msg) {
    this.showMessage(msg, 'alert-danger');
  },
  showFormError: function(formId, msg) {
    this.showError(msg);
    var selector = formId==undefined || formId=='' ? ':invalid' : '#'+formId+' :invalid';
    $(selector).addClass('field-error');
    $(selector)[0].focus();
  },
  showHelp: function() {
    console.info('showHelp');
    $('iframe.helpContent')
        .attr('src',ractive.get('helpUrl'))
        .prop('height', window.innerHeight*0.8);
    $('#helpModal').modal({});
  },
  showLogin: function() {
    console.info('showLogin');
    $('#loginSect').slideDown();
  },
  showMessage: function(msg, additionalClass) {
    console.log('showMessage: '+msg);
    if (additionalClass == undefined) additionalClass = 'bg-info text-info';
    if (msg === undefined) msg = 'Working...';
    $('#messages').empty().append(msg).removeClass().addClass(additionalClass).show();
//    document.getElementById('messages').scrollIntoView();
    if (fadeOutMessages && additionalClass!='bg-danger text-danger') setTimeout(function() {
      $('#messages').fadeOut();
    }, EASING_DURATION*10);
    else $('#messages, .messages').append('<span class="text-danger pull-right glyphicon icon-btn kp-icon-remove" onclick="ractive.hideMessage()"></span>');
  },
  showReconnected: function() {
    console.log('showReconnected');
    $( "#ajax-loader" ).hide();
    if ($('#connectivityMessages:visible').length>0) {
      $('#connectivityMessages').remove();
      $('body').append('<div id="connectivityMessages" class="alert-info">Reconnected</div>').show();
      setTimeout(function() {
        $('#connectivityMessages').fadeOut();
      }, EASING_DURATION*10);
    }
  },
  showResults: function() {
    var entityName = ractive.get('entityName');
    $('#'+entityName+'sTableToggle').addClass('kp-icon-caret-down').removeClass('kp-icon-caret-right');
    $('#currentSect').slideUp();
    $('#'+entityName+'sTable').slideDown({ queue: true });
  },
  showSearchMatched: function() {
    var entityName = ractive.get('entityName');
    ractive.set('searchMatched',$('#'+entityName+'sTable tbody tr').length);
    if ($('#'+entityName+'sTable tbody tr:visible').length==1) {
      var id = $('#'+entityName+'sTable tbody tr:visible').data('href');
      var q = Array.findBy('selfRef',id,ractive.get(entityName+'s'));
      ractive.select( q );
    }
  },
  showUpload: function () {
    console.log('showUpload...');
    $('#upload').slideDown();
  },
  showWarning: function(msg) {
    this.showMessage(msg, 'alert-warning');
  },
  sortRecords: function() {
    var entityName = ractive.get('entityName');
    ractive.get(entityName+'s').sort(function(a,b) {
      return new Date(b.lastUpdated)-new Date(a.lastUpdated);
    });
  },
  stripProjection: function(link) {
    if (link==undefined) return;
    var idx = link.indexOf('{projection');
    if (idx==-1) {
      idx = link.indexOf('{?projection');
      if (idx==-1) {
        return link;
      } else {
        return link.substring(0,idx);
      }
    } else {
      return link.substring(0,idx);
    }
  },
  switchToTenant: function() {
    var tenant = $('select.tenant').val();
    if (tenant==undefined || typeof tenant != 'string') {
      return false;
    }
    console.log('switchToTenant: '+tenant);
    var username = 'keycloak' in ractive ? ractive.getProfile().username : $auth.getClaim('sub');
    $.ajax({
      method: 'PUT',
      url: ractive.getServer()+"/admin/tenant/"+username+'/'+tenant,
      success: function() {
        window.location.reload();
      }
    });
  },
  tenantUri: function(entity) {
    console.log('tenantUri: '+entity);
    var uri = ractive.uri(entity);
    if (uri != undefined && uri.indexOf(ractive.get('tenant.id'))==-1) {
      uri = uri.replace(ractive.get('entityPath'),'/'+ractive.get('tenant.id')+ractive.get('entityPath'));
    }
    // if (uri != undefined && ractive.get('context')!=undefined) {
    //   uri = uri.replace('/'+ractive.get('tenant.id'),ractive.get('context')+'/'+ractive.get('tenant.id'));
    // }
    return uri;
  },
  toCsv: function(json, title, headings) {
    //If json is not an object then JSON.parse will parse the JSON string in an Object
    var arr = typeof json != 'object' ? JSON.parse(json) : json;

    var csv = '';

    // write title on first row
    csv += title + '\n\n';

    var row = '';
    if (headings === undefined || headings == true) {
      // extract label from json fields in array idx 0
      for (var idx in arr[0]) {
          row += idx + ',';
      }

      row = row.slice(0, -1); // strip trailing comma
      headings = row + '\n';
    }
    csv += headings + '\n';

    var propNames = headings.split(',');
    for (var i = 0; i < arr.length; i++) {
        row = '';

        for (var j = 0 ; j < propNames.length ; j++) {
          try {
            var getNestedObject = function(nestedObj, pathArr) {
              return pathArr.reduce(function(obj, key) {
                (obj && obj[key] !== 'undefined') ? obj[key] : undefined, nestedObj;// jshint ignore:line
              });
            };
            var val = getNestedObject(arr[i], propNames[j].split('.'));
            row += '"' + (val == undefined ? '' : val) + '",';
          } catch (err) {
            console.error('Fail to extract property '+propNames[j]+'from row '+i);
          }
        }

        row = row.slice(0, -1); // strip trailing comma
        csv += row + '\n';
    }

    if (csv == '') {
        alert("Invalid data");
        return;
    }

    //Generate a file name (<title with _ for spaces>-<timestamp>)
    var fileName = title.replace(/ /g,"_")+new Date().toISOString().substring(0,16).replace(/[T:]/g,'-');

    //Initialize file format you want csv or xls
    var uri = 'data:text/csv;charset=utf-8,' + escape(csv);

    // Now the little tricky part.
    // you can use either>> window.open(uri);
    // but this will not work in some browsers
    // or you will not get the correct file extension

    //this trick will generate a temp <a /> tag
    var link = document.createElement("a");
    link.href = uri;

    //set the visibility hidden so it will not effect on your web-layout
    link.style = "visibility:hidden";
    link.download = fileName + ".csv";

    //this part will append the anchor tag and remove it after automatic click
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  },
  toggleResults: function() {
    console.info('toggleResults');
    var entityName = ractive.get('entityName');
    $('#'+entityName+'sTableToggle').toggleClass('kp-icon-caret-down').toggleClass('kp-icon-caret-right');
    $('#'+entityName+'sTable').slideToggle();
  },
  toggleSection: function(sect) {
    console.info('toggleSection: '+$(sect).attr('id'));
    $('#'+$(sect).attr('id')+'>div').toggle();
    $('#'+$(sect).attr('id')+' .ol-collapse').toggleClass('kp-icon-caret-right').toggleClass('kp-icon-caret-down');
  },
  toggleSidebar: function() {
    console.info('toggleSidebar');
    $('.toolbar-left').toggle(EASING_DURATION);
  },
  toggleSubMenu: function(entry) {
    console.info('toggleSubMenu');
    $(entry.nextElementSibling).toggle(EASING_DURATION);
  },
  upload: function(formId) {
    console.log('upload, id: '+formId);
    var formElement = document.getElementById(formId);
    var formData = new FormData(formElement);
    return $.ajax({
        type: 'POST',
        url: formElement.action,
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function() {
          console.log('successfully uploaded resource');
          ractive.fetch();
          ractive.hideUpload();
        }
    });
  },
  uri: function(entity) {
    // TODO switch to use modularized version
    //console.log('uri: '+entity);
    var saveObserver = ractive.get('saveObserver');
    ractive.set('saveObserver', false);
    var uri;
    if ('links' in entity) {
      $.each(entity.links, function(i,d) {
        if (d.rel == 'self') {
          uri = d.href;
        }
      });
    } else if ('_links' in entity) {
      uri = ractive.stripProjection(entity._links.self.href);
    } else if ('id' in entity) {
      uri = ractive.get('entityPath')+'/'+entity.id;
    }
    // work around for sub-dir running
    if (uri != undefined && uri.indexOf(ractive.getServer())==-1 && uri.indexOf('//')!=-1) {
      uri = ractive.getServer() + uri.substring(uri.indexOf('/', uri.indexOf('//')+2));
    } else if (uri != undefined && uri.indexOf('//')==-1) {
      uri = ractive.getServer()+uri;
    }

    ractive.set('saveObserver', saveObserver);
    return uri;
  }
});

//$( document ).ajaxSuccess(function( event, request, settings ) {
//  if (settings['retryIn']!=undefined) ractive.showReconnected();
//});

$( document ).bind('keypress', function(e) {
  switch (e.keyCode) {
  case 13: // Enter key
    if ('ractive' in window && 'enter' in ractive) ractive.enter();
    break;
  case 63:   // ? key
    console.log('help requested');
    $('#helpModal').modal({});
    break;
  }
});

$(document).ready(function() {

  try {
    ractive.keycloak = Keycloak('/keycloak.json');
    ractive.keycloak.init({ onLoad: 'login-required' })
    .then(function(authenticated) {
      console.info(authenticated ? 'authenticated' : 'not authenticated');
      if ('srp' in ractive) ractive.srp.options.token = ractive.keycloak.token;
      ractive.keycloak.loadUserProfile().then(function(profile) {
        localStorage.setItem('profile', JSON.stringify(profile));
        ractive.getProfile();
        ractive.fetch();
      }).catch(function(e) {
        console.error('Failed to load user profile: '+e);
      });
    }).catch(function(e) {
      console.error('failed to initialize: '+e);
    });
  } catch (e) {
    console.warn('no Keycloak, use legacy authentication');
  }

  ractive.set('saveObserver', false);
  if (document.location.href.indexOf('https://srp.digital/srp')!=-1) {
    (function() {
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){// jshint ignore:line
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),// jshint ignore:line
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)// jshint ignore:line
      })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');// jshint ignore:line

      ga('create', 'UA-39960834-3', 'auto');
      ga('send', 'pageview');
    })();
  }

  // ajax loader
  if ($('#ajax-loader').length==0) $('body').append('<div id="ajax-loader"><img class="ajax-loader" src="/images/ajax-loader.svg" alt="Loading..."/></div>');
  $( document ).ajaxStart(function() {
    $( "#ajax-loader" ).show();
  });
  $( document ).ajaxStop(function() {
    $( "#ajax-loader" ).hide();
  });

  ractive.loadStandardPartials(ractive.get('stdPartials'));

  $( document ).ajaxComplete(function(event, jqXHR) {
    if (jqXHR.status > 0 && jqXHR < 400) ractive.showReconnected();
  });

  ractive.observe('tenant', function(newValue, oldValue, keypath) {
    console.log("'"+keypath+"' changing from '"+oldValue+"' to '"+newValue+"'");
    if ((oldValue == undefined || oldValue.id == '') && newValue != undefined && newValue.id != '' && 'fetch' in ractive) {
      ractive.fetch();
    }
  });

  ractive.on( 'filter', function ( event, filter ) {
    console.info('filter on '+JSON.stringify(event)+','+filter.idx);
    ractive.filter(filter);
  });

  ractive.on( 'sort', function ( event, column ) {
    console.info('sort on '+column);
    // if already sorted by this column reverse order
    if (this.get('sortColumn')==column) this.set('sortAsc', !this.get('sortAsc'));
    this.set( 'sortColumn', column );
  });

  ractive.observe('title', function(newValue, oldValue, keypath) {
    console.log("'"+keypath+"' changing from '"+oldValue+"' to '"+newValue+"'");
    if (newValue!=undefined && newValue!='') {
      $('title').empty().append(newValue);
    }
  });

  ractive.observe('searchTerm', function(newValue, oldValue, keypath) {
    console.log("'"+keypath+"' changed from '"+oldValue+"' to '"+newValue+"'");
    if ('showResults' in ractive && typeof ractive.showResults === 'function') ractive.showResults();
    setTimeout(ractive.showSearchMatched, 1000);
  });

  var params = getSearchParameters();
  if ('searchTerm' in params) {
    ractive.set('searchTerm',decodeURIComponent(params.searchTerm));
  } else if ('q' in params) {
    ractive.set('searchTerm',decodeURIComponent(params.q));
  }

  ractive.set('saveObserver', true);
});

function selectElementContents(el) {
  var range = document.createRange();
  range.selectNodeContents(el);
  var sel = window.getSelection();
  sel.removeAllRanges();
  sel.addRange(range);
}

function getSearchParameter(k) { // jshint ignore:line
  var params = getSearchParameters();
  return k in params ? params[k] : undefined;
}

function getSearchParameters() {
  var prmstr = window.location.search.substr(1);
  return prmstr != null && prmstr != "" ? transformToAssocArray(prmstr) : {};
}

function transformToAssocArray( prmstr ) {
  var params = {};
  var prmarr = prmstr.split("&");
  for ( var i = 0; i < prmarr.length; i++) {
      var tmparr = prmarr[i].split("=");
      params[tmparr[0]] = tmparr[1];
  }
  return params;
}
