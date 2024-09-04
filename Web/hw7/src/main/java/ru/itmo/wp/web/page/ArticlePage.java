package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.ArticleService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import ru.itmo.wp.web.exception.RedirectException;


public class ArticlePage {
    private final ArticleService articleService = new ArticleService();
    private void action(HttpServletRequest request, Map<String, Object> view) {
        if (request.getSession().getAttribute("user") == null) {
            request.getSession().setAttribute("message", "You need to register!");
            throw new RedirectException("/index");
        }
    }

    private void create(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        if (request.getSession().getAttribute("user") == null) {
            throw new RedirectException("/index");
        }
        Article article = new Article();
        article.setTitle(request.getParameter("title"));
        article.setText(request.getParameter("text"));
        User user = (User) request.getSession().getAttribute("user");
        article.setUserId(user.getId());
        article.setHidden(Article.Status.SHOW);


        articleService.validateArticle(article);
        articleService.create(article);
        throw new RedirectException("/article");
    }
}
