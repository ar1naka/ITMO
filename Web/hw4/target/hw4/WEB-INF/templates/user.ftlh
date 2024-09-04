<#-- @ftlvariable name="handle" type="java.lang.String" -->
<#-- @ftlvariable name="user_id" type="java.lang.String" -->
<#-- @ftlvariable name="logged_user_id" type="java.lang.String" -->


<#import "./common.ftlh" as c>

<@c.page>
    <#if handle??>
        <#assign u = c.findBy(users, "handle", handle)!/>
    <#elseif user_id??>
        <#assign u = c.findBy(users, "id", user_id)!/>
    <#elseif logged_user_id??>
        <#assign u = c.findBy(users, "id", logged_user_id)!/>
    </#if>
    <#if u?has_content>
            <#assign uNext = c.next(users, "handle", u.handle)!/>
            <#assign uPrev = c.prev(users, "handle", u.handle)!/>
            <div class = "user-table">
                <div class = "user-name">
                    <#if u.logged_user_id??>
                        <div style="color: ${u.color}">${u.name}</div>
                    <#else>
                        <u>${u.name}</u>
                    </#if>
                </div>

                <img src="/img/no-title.jpg" alt="user_img">
                <ul>
                    <li>id: ${u.id}</li>
                    <li>handle: ${u.handle}</li>
                    <li>name: ${u.name}</li>
                    <li>posts: <a href="/posts?user_id=${u.id}">${c.size(posts, "user_id", u.id)}</a></li>
                </ul>
                <div class="arrows">
                    <#if uPrev?has_content>
                        <a href="/user?handle=${uPrev.handle}" style="text-decoration: none">←</a>
                    <#else>
                        <a href="/user?handle=${u}" style="text-decoration: none; pointer-events: none;color: rgb(185, 185, 185);">←</a>
                    </#if>

                    <#if uNext?has_content>
                        <a href="/user?handle=${uNext.handle}" style="text-decoration: none">→</a>
                    <#else>
                        <a href="/user?handle=${u}" style="text-decoration: none; pointer-events: none;color: rgb(185, 185, 185);">→</a>
                    </#if>
                <div>
            <#else>
                No such user
            </#if>
    </div>
</@c.page>
