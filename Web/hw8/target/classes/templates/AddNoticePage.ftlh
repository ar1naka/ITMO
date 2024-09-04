<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <div class="add form-box">
            <div class="header">Notice</div>
            <div class="body">
                <form method="post" action="">
                    <div class="field">
                        <div class="text-name" style="margin-left: 2rem">
                          <label for="content">Content</label>
                        </div>
                        <p>
                            <textarea name="content" rows="10" style="margin-left: 2rem; width: 85%; resize:none">${notice.content!}</textarea>
                        </p>
                        <@c.error "notice.content"/>
                    </div>
                    <div class="button-field">
                        <input type="submit" value="Add">
                    </div>
                </form>
            </div>
        </div>
    <#else>
       Register!!!
    </#if>

</@c.page>