<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->

<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if user?? && user.admin>
                    <th>Admin</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user_login"></td>
                    <#if user?? && user.admin>
                        <td class="user_admin"></td>
                        <td class="button"><input type="button" ></td>
                    </#if>

                </tr>
            </template>
            <tr class="noData">
                <td colspan="3">
                    No data
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");
            $.ajax({
                dataType: "json",
                data: {
                    action: "findAll"
                },
                success: function(response) {
                    response["users"].forEach(function (user) {
                        $noData.hide();
                        const $tr = $($(".users template").prop("content")).clone();
                        $tr.find(".user-id").text(user["id"]);
                        $tr.find(".user_login").text(user["login"]);
                        $tr.find("tr").attr("data-userId", user["id"]);
                        <#if user?? && user.admin>
                        $tr.find(".user_admin").text(user["admin"]);
                        $tr.find('td input').val(user['admin'] ? 'disable' : 'enable');
                        </#if>

                        $(".users tbody").append($tr);
                    });


                    $(".users tr td input").click(function () {
                        const $error = $(".users").find(".error");
                        const $tmp = $(this);
                        const $state = $(this).closest("tr").find(".user_admin");
                        const $userId = $(this).closest("tr").find(".user-id").text();

                        function func(response) {
                            if (response["error"]) {
                                $error.val(response["error"]);
                            } else {
                                if ($tmp.val() === "enable") {
                                    $tmp.val("disable");
                                    $state.text("true");
                                } else {
                                    $tmp.val("enable");
                                    $state.text("false");
                                }
                                notify("Success");
                            }
                        }
                        const data = {action: "change",
                            userId : $userId,
                            status : $tmp.val()};
                        foo(data, func);
                        return false;
                    });
                /*$(".users tr").click(function () {
                    const data = {
                        action: "findUser",
                        userId: $(this).attr("data-userId")
                    }
                    const success = function (response) {
                        notify(response["user"].creationTime);
                    }
                    foo(data, success)
                });*/
                },
                type: "POST"
            });
        })
    </script>

</@c.page>
