window.notify = function (message, type='success') {
    $.notify(message, {
        position: "right bottom",
        className: type
    });
}
window.foo = function (data, success) {
    $.ajax({
        type: "POST",
        url: "",
        dataType: "json",
        data,
        success
    })
}

window.commonAjax = function (elem, action) {
    const login = elem.find("input[name='login']").val();
    const password = elem.find("input[name='password']").val();
    const error = elem.find(".error");
    foo({
            action,
            login,
            password
        },
        function (response) {
            if (response["error"]) {
                error.text(response["error"]);
            } else {
                location.href = response["redirect"];
            }
        })
}

