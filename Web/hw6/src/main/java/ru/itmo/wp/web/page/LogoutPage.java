package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class LogoutPage extends Page {
    protected void action(HttpServletRequest request, Map<String, Object> view) {
        Event event = new Event();
        User user = (User) request.getSession().getAttribute("user");

        event.setUserId(user.getId());
        event.setType(Event.Type.LOGOUT);
        eventService.register(event);
        request.getSession().removeAttribute("user");

        request.getSession().setAttribute("message", "Good bye. Hope to see you soon!");
        throw new RedirectException("/index");
    }
}
