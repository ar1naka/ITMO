<#import "./common.ftlh" as c>

<@c.page>
    <div class="datatable">
        <div class="caption">Submissions</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Handle</th>
                <th>Name</th>
            </tr>
            </thead>
            <tbody>
            <#list users as u>
                <tr>
                    <#if user?? && user.id == u.id>*</#if>
                    <td>${u.id}</td>
                    <td><@c.userlink u false/></td>
                    <td>${u.name}</td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
    <ul>
</@c.page>