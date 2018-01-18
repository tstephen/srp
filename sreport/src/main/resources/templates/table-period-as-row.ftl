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
