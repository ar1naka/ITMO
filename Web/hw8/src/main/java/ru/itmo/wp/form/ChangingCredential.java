package ru.itmo.wp.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@SuppressWarnings("unused")
public class ChangingCredential {

    private boolean disabled;
    private long userId;

    public void setStatus(boolean type) {
        this.disabled = type;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean getStatus() {
        return disabled;
    }

    public long getUserId() {
        return userId;
    }
}

