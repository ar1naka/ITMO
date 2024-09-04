<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="talks" type="ru.itmo.wp.model.domain.Talk[]" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#-- @ftlvariable name="text" type="java.lang.String" -->


<#-- @ftlvariable name="talk" type="java.lang.String" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="form-box" style="margin-bottom: 3rem; margin-right: 3rem; width: 90%">
        <div class="header">New message</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="send"/>
                <select name="select" style="margin: 0.3rem">
                    <option value="No user" selected> Select the recipient:</option>
                    <#list users as user>
                        <option value="${user.id}">${user.login}</option>
                    </#list>
                </select>
                <p>
                    <textarea rows="10" id="text" name="text" value="${text!}" style="width: 60%; margin-left: 4rem"></textarea>
                </p>
                <#if error??>
                    <div class="error">
                        ${error!}
                    </div>
                </#if>
                <div class="button-field">
                    <input type="submit" value="Send">
                </div>
            </form>
        </div>
    </div>


    <div class="datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>From</th>
                <th>To</th>
                <th>Text</th>
            </tr>
            </thead>
            <tbody>
            <#list talks as talk>
                <tr>
                    <td>${talk.id}</td>
                    <td>${talk.sourceUserId}</td>
                    <td>${talk.targetUserId}</td>
                    <td>${talk.text}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</@c.page>

