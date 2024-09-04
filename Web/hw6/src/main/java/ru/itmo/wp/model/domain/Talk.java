package ru.itmo.wp.model.domain;

import java.io.Serializable;
import java.util.Date;

public class Talk implements Serializable {
    private Long id;
    private long sourceUserId;
    private long targetUserId;
    private String text;
    private Date creationTime;



    public void setId(long id) {
        this.id = id;
    }

    public void setSourceUserId(long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public long getId() {
        return id;
    }

    public long getSourceUserId() {
        return sourceUserId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public String getText() {
        return text;
    }

    public Date getCreationTime() {
        return creationTime;
    }
}