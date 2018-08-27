{ 
  "questions": <#if questions??>${questions?size}<#else>0</#if>,
  "surveys": <#if surveys??>${surveys?size}<#else>0</#if>,
  "answers": <#if answers??>${answers?size?replace(",", "")}<#else>0</#if>,
  "returns": <#if returns??>${returns?size?replace(",", "")}<#else>0</#if>,
  "orgs": <#if orgs??>${orgs?size}<#else>0</#if>
}