package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.service.EventService;
import ru.itmo.wp.model.service.TalkService;
import ru.itmo.wp.model.service.UserService;
import com.google.common.base.Strings;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Page {
    protected final EventService eventService = new EventService();
    protected final TalkService talkService = new TalkService();
    HttpServletRequest request;
    protected final UserService userService = new UserService();
    private void putUser(Map<String, Object> view) {
        User user = getUser();
        if (user != null) {
            view.put("user", user);
        }
    }
    protected void setUser(User user) {
        request.getSession().setAttribute("user", user);
    }

    protected User getUser() {
        return (User) request.getSession().getAttribute("user");
    }

    protected String getMessage() {
        return (String) request.getSession().getAttribute("message");
    }

    protected void putMessage(HttpServletRequest request, Map<String, Object> view) {
        String message = getMessage();
        if (!Strings.isNullOrEmpty(message)) {
            view.put("message", message);
            request.getSession().removeAttribute("message");
        }
    }

    void setMessage(String message){
        request.getSession().setAttribute("message", message);
    }

    void before(HttpServletRequest request, Map<String, Object> view) {
        view.put("userCount", Long.toString(userService.findCount()));
        this.request = request;
        putUser(view);
    }

    void after(HttpServletRequest request, Map<String, Object> view) {
        // No operations
    }

}
