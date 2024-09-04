package ru.itmo.wp.model.domain;

import java.util.Date;

public class Article {
    private long id;
    private long userId;
    private String title;
    private String text;
    private Date creationTime;
    private Status hidden;
    public enum Status {
        HIDE,
        SHOW
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getText(){
        return text;
    }
    public void setText(String text){
        this.text = text;
    }
    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }
    public void setHidden(Status hidden){
        this.hidden = hidden;
    }
    public Status getHidden(){
        return hidden;
    }
}
