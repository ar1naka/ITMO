<#import "commons.ftlh" as c>

<@c.page>
    <div class="articles">
        <template>
            <article class="article">
                <div class="title"></div>
                <div class="body"><p></p></div>
                <ul class="attachment">
                    <li>Announcement of <a href="#">Codeforces Round #510 (Div. 1)</a></li>
                    <li>Announcement of <a href="#">Codeforces Round #510 (Div. 2)</a></li>
                </ul>
                <div class="footer">
                    <div class="left">
                        <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                        <span class="positive-score">+173</span>
                        <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                    </div>
                    <div class="right">
                        <img src="img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                        2 days ago
                        <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                        <a href="#">68</a>
                    </div>
                </div>
            </article>
        </template>
        <div class="noData">
            <p>No data</p>
        </div>
    </div>

</@c.page>

<script>
    $(function () {
        const $noData = $(".articles .noData");
        $noData.find("p").text("Loading...");
        foo({
                action: "findAll"
            },
            function (response) {
                response["articles"].forEach(function (article) {
                    $noData.hide()
                    const $tmp = $($(".articles template").prop("content")).clone();
                    $tmp.find(".title").text(article["title"]);
                    $tmp.find(".body p").html(article["text"].replaceAll('\n', '<br />'));
                    $(".articles").append($tmp);
                })
            })
    })
</script>