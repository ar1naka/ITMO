package ru.itmo.wp.form;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@SuppressWarnings("unused")
public class NoticeCredentials {
    @NotNull(message = "Content can't be null")
    @NotEmpty(message = "Content can't be empty")
    @Size(min = 2, max = 1000, message = "Wrong content length expected 2-1000")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
