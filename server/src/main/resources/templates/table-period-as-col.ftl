<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
<link href="//srp.digital/srp/2.3.0/css/srp.css" rel="stylesheet">
<link rel="icon" type="image/png" href="//srp.digital/srp/images/icon/srp-icon-32x32.png" />
<table class="table table-striped">
  <thead>
    <tr>
      <th>&nbsp;</th>
      <th>&nbsp;</th>
      <#list periods as period>
        <th class="number">${period}</th>
      </#list>
    </tr>
  </thead>
  <tbody>
    <#list table.rows() as row>
      <tr>
        <th>${messages.getString(table.headers()[row?index])!table.headers()[row?index]}</th>
        <th class="legend ${table.headers()[row?index]?lower_case}">&nbsp;</th>
        <#list row as col>
          <#if col?index < periods?size>
            <td class="number">${col!"0"}</td>
          </#if>
        </#list>
      </tr>
    </#list>
  </tbody>
</table>
