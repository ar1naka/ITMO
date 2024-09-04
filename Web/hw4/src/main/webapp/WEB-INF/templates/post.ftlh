<#import "./common.ftlh" as c>

<@c.page>
    <#if post_id??>
            <#assign p = c.findBy(posts, "id", post_id)!/>
    </#if>

            <#if p?has_content>
                <@c.post p true/>
            <#else>
                No such post
            </#if>

</@c.page>