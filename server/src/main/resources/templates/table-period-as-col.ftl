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
        <#list row as col>
          <th>${messages.getString(table.headers()[row?index])}</th>
          <td class="number">${col!"n/a"}</td>
        </#list>
      </tr>
    </#list>
  </tbody>
</table>