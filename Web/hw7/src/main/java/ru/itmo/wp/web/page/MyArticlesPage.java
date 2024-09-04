package ru.itmo.wp.web.page;


import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;
import ru.itmo.wp.web.exception.RedirectException;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;
/** @noinspection unused*/
public class MyArticlesPage {
    private final ArticleService articleService = new ArticleService();

    protected void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "You need to register!");
            throw new RedirectException("/index");
        }
        User user = (User) request.getSession().getAttribute("user");
        view.put("userArticles", articleService.findAllArticlesById(user));
    }

    private void setVision(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        Article article = articleService.find(Long.parseLong(request.getParameter("articleId")));
        User user = (User) request.getSession().getAttribute("user");
        Long articleId = Long.parseLong(request.getParameter("articleId"));
        articleService.validateVisibility(user, articleId);
        articleService.change(article, request.getParameter("status"));
    }
}