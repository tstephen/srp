<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
<link href="//srp.digital/srp/2.3.0/css/srp.css" rel="stylesheet">
<link rel="icon" type="image/png" href="//srp.digital/srp/images/icon/srp-icon-32x32.png" />
<table class="table table-striped">
  <thead>
    <tr>
      <th>Period</th>
      <#list table.headers() as header>
        <th>${messages.getString(header)}</th>
      </#list>
    </tr>
  </thead>
  <tbody>
    <#list table.rows() as row>
      <tr>
        <td>${periods[row?index]}</td>
        <#list row as col>
          <td class="number">${col!"n/a"}</td>
        </#list>
      </tr>
    </#list>
  </tbody>
</table>
