<#import "commons.ftlh" as c>
<#-- @ftlvariable name="userArticles" type="ru.itmo.wp.model.domain.Article[]" -->


<@c.page>
    <div class="myArticles datatable">
        <div class="caption">My articles</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Hide/Show</th>
            </tr>
            </thead>
            <tbody>
            <#list userArticles as article>
                <tr>
                    <td class="article_id">${article.id}</td>
                    <td class="article_title">${article.title}</td>
                    <td>
                        <#assign x = "HIDE">
                        <#if article.hidden == "HIDE">
                            <#assign x = "SHOW">
                        </#if>
                        <input type="button" name="${article.id}" class="article_hidden" value="${x}"/>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
        <div class="error" style="color: red"></div>
    </div>
    <script>
        $(function () {
            const $error =  $(".myArticle").find(".error");
            $(".myArticles .article_hidden").click(function() {
                const $tmp = $(this);
                function success(response) {
                    if (response["error"]) {
                        $error.text(response["error"]);
                        notify("It's not your article", 'error');
                    } else {
                        if ($tmp.val() === "SHOW") {
                            $tmp.val("HIDE");
                        } else {
                            $tmp.val("SHOW");
                        }
                        notify("Success");
                    }
                }
                const data = {
                    "action": "setVision",
                    "articleId": $(this).attr("name"),
                    "status" : $tmp.val()
                };
                foo(data, success);
            });
        });
    </script>


</@c.page>