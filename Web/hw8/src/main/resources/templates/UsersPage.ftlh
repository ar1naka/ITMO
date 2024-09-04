<#import "commons.ftlh" as c>


<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <#if user??>
                    <th>Status</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <#if users?size==0>
                <tr class="noData">
                    <td colspan="4">
                        No data
                    </td>
                </tr>
            <#else>
               <#list users as userC>
                    <tr>
                        <td class="id">${userC.id}</td>
                        <td class="login">${userC.login}</td>
                        <td class="creationTime">${userC.creationTime}</td>
                        <#if user??>
                        <td class>
                                <form method="post" action="">
                                    <input type="hidden" name="userId" value="${userC.id}"/>
                                    <#if userC.isDisabled()>
                                        <button type="submit" name="status" value="false">Enable</button>
                                    <#else>
                                        <button type="submit" name="status" value="true">Disable</button>
                                    </#if>
                                </form>
                        </td>
                        </#if>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</@c.page>