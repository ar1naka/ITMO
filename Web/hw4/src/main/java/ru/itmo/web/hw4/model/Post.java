
package ru.itmo.web.hw4.model;

import java.net.FileNameMap;

public class Post {
    private final long id;
    private final String title;
    private final String text;
    private final long user_id;
    public final int MAX_TEXT_LENGTH = 350;


    public Post(long id, String title, String text, long user_id) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.user_id = user_id;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getShortText() {
        if (text.length() < MAX_TEXT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_TEXT_LENGTH) + "...";
    }

    public long getUser_id() {
        return user_id;
    }
}
