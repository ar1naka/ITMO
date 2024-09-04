package ru.itmo.wp.model.service;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.ArticleRepository;
import ru.itmo.wp.model.repository.impl.ArticleRepositoryImpl;

import java.util.List;

public class ArticleService {
    private final ArticleRepository articleRepository = new ArticleRepositoryImpl();
    public void validateArticle(Article article) throws ValidationException {
        if (Strings.isNullOrEmpty(article.getTitle())) {
            throw new ValidationException("The title is required");
        }
        if (Strings.isNullOrEmpty(article.getText())) {
            throw new ValidationException("The text is required");
        }

        if (article.getTitle().length() > 150) {
            throw new ValidationException("Title can't be longer than 200 letters");
        }

        if (article.getText().length() > 500) {
            throw new ValidationException("Text can't be longer than 500 letters");
        }

    }

    public void create(Article article) {
        articleRepository.save(article);
    }

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findAllArticlesById(User user) {
        return articleRepository.findAllUserArticles(user);
    }

    public void change(Article article, String status) {
        articleRepository.setVisibility(article, status);
    }

    public Article find(long id) {
        return articleRepository.find(id);
    }
    public void validateVisibility(User user, Long articleId) throws ValidationException {
        Long articleAuthorId = articleRepository.findUserId(articleId);
        if (user.getId() != articleAuthorId) {
            throw new ValidationException("It's not your article");
        }
    }
}
