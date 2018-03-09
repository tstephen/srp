$(document).ready(function() {
  ractive.set('allowedOrgValues',function() {
    setTimeout(function() {
      $.each(ractive.get('current.info'), function(i,d) {
        if (d.key == 'allowedOrgs') return d.value.split(',');
      })
    }, 1000);
  });

  ractive.observe('current.info', function(newValue, oldValue, keypath) {
    console.log('info changed from '+oldValue+' to '+newValue);
    if (newValue == undefined) return;
    var allowedOrgs;
    $.each(newValue, function(i,d) {
      if (d.key == 'allowedOrgs') allowedOrgs = d.value.split(',');
    })
    console.log('allowed orgs: '+allowedOrgs);
    if (allowedOrgs == undefined) return;
    $('#curOrg').empty();
    $.each(allowedOrgs, function(i,d) {
      $('#curOrg').append('<option value="'+d+'" '
          +(d == $auth.getClaim('org') ? 'selected' : '')
          +'>'+d+'</option>');
    });
  });

  $('#curOrg').blur(function(ev) {
    updateOrg(ev.target.value);
  });
});

function updateOrg(org) {
  ractive.saveUserInfo({ key: 'org', value: org});
}
