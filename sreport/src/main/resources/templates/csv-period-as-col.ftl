classification,percentage
<#list table.rows() as row><#if row?index < periods?size>${messages.getString(periods[row?index])!header}<#list row as col>,${col!0}</#list></#if>
</#list>
