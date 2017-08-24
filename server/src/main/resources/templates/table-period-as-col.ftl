<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
<link href="//srp.digital/srp/2.0.0/css/srp.css" rel="stylesheet">
<link rel="icon" type="image/png" href="//srp.digital/srp/2.0.0/images/icon/srp-icon-32x32.png" />
<div class="controls pull-right" style="display:none">
  <span class="glyphicon glyphicon-btn glyphicon-share"></span>
  <!--span class="glyphicon glyphicon-btn glyphicon-link"></span-->
  <!--span class="glyphicon glyphicon-btn glyphicon-copy"></span-->
</div>
<table class="table table-striped">
  <thead>
    <tr>
      <th>&nbsp;</th>
      <#list periods as period>
        <th class="number">${period}</th>
      </#list>
    </tr>
  </thead>
  <tbody>
    <#list table.rows() as row>
        <tr>
        <th>${messages.getString(table.headers()[row?index])}</th>
          <#list row as col>
            <#if col?index < periods?size>
              <td class="number">${col!"n/a"}</td>
            </#if>
          </#list>
        </tr>
    </#list>
  </tbody>
</table>