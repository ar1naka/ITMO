<#import "./common.ftlh" as c>

<@c.page>
   <#assign user_posts = posts?filter(p -> p.user_id == user_id)?reverse>
        <#if user_posts?size == 0>
            User don't have posts
        <#else>
            <#list user_posts as p>
                <@c.post p true/>
            </#list>
        </#if>

</@c.page>