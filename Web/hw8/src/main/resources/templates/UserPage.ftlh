<#-- @ftlvariable name="userP" type="ru.itmo.wp.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="user-page">
        <#if userP??>
         <ul>
            <li>id: ${userP.id}</li>
            <li>handle: ${userP.login}</li>
            <li>registration date and time: ${userP.creationTime}</li>
        </ul>
        <#else>
            No such user
        </#if>
    </div>
</@c.page>