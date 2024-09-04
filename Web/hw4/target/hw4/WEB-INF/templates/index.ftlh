<#import "./common.ftlh" as c>
<@c.page>
    <#list posts?reverse as p>
        <@c.post p false/>
    </#list>
</@c.page>