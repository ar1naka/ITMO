package ru.itmo.wp.model.repository;

import ru.itmo.wp.model.domain.Article;
import ru.itmo.wp.model.domain.User;

import java.util.List;

public interface ArticleRepository {
    void save(Article article);
    Article find(long id);

    List<Article> findAll();

    Long findUserId(Long articleId);

    void setVisibility(Article article, String status);

    List<Article> findAllUserArticles(User user);
}
