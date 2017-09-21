Period<#list table.headers() as header>,${messages.getString(header)!header}</#list>
<#list table.rows() as row>${periods[row?index]!'n/a'}<#list row as col>,${col!0}</#list>
</#list>
