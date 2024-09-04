<#import "commons.ftlh" as c>
<#-- @ftlvariable name="title" type="java.lang.String" -->


<@c.page>
    <div class="article form-box" style=" margin-right: 3rem; width: 90%">
        <div class="header">Article</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Title</label>
                    </div>
                    <div class="value">
                        <input id="title" name="title" value="${title!}"/>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="new_text">Text</label>
                    </div>
                    <textarea id="new_text" name="new_text" style="width: 60%; white-space:pre-wrap" rows="10"></textarea>
                </div>
                <div class="button-field">
                    <input type="submit" value="Create">
                </div>
                <div class="error" style="color: red"></div>
            </form>
        </div>
    </div>
    <script>
        $(function () {
            $(".body form").submit(function () {
                const title = $(this).find("input[name='title']").val()
                const text = $(this).find("textarea[name='new_text']").val()
                const error = $(this).find(".error")
                foo({
                        action: "create",
                        title, text
                    },
                    function (response) {
                        if (response["error"]) {
                            error.text(response["error"]);
                        } else {
                            location.href = response["redirect"];
                        }
                    })
                return false
            })
        })
    </script>
</@c.page>