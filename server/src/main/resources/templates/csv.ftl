Period,<#list table.headers() as header>,${messages.getString(header)}</#list>
<#list table.rows() as row>${periods[row?index]},<#list row as col>,${col!0}</#list>
</#list>
