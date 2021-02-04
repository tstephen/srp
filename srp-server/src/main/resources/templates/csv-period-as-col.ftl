classification,percentage
<#list 0..table.headers()?size-1 as idx>${messages.getString(table.headers()[idx])!table.headers()[idx]}<#list table.rows()[idx] as col>,${col!0}</#list>
</#list>
