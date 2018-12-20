{ 
  <#if questions??>"questions": ${questions},</#if>
  <#if surveys??>"surveys": ${surveys},</#if>
  <#if answers??>"answers": ${answers?replace(",", "")},</#if>
  <#if returns??>"returns": ${returns?replace(",", "")},</#if>
  <#if orgs??>"orgs": ${orgs},</#if>
  "created": "${now}"
}