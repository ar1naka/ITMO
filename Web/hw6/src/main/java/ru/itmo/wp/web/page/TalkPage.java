package ru.itmo.wp.web.page;

import com.google.common.base.Strings;
import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.exception.RedirectException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
@SuppressWarnings({"unused"})
public class TalkPage extends Page{
    protected void action(HttpServletRequest request, Map<String, Object> view){
        if (request.getSession().getAttribute("user") == null) {
            setMessage("Please, enter in your account!");
            throw new RedirectException("/index");
        }
        fill(view);
    }

    private void fill(Map<String, Object> view){
        view.put("users", userService.findAll());
        view.put("talks", talkService.findAll());
    }


    private void send(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        Talk talk = new Talk();
        view.put("text", request.getSession().getAttribute("text"));
        if (request.getParameter("text").isEmpty()) {
            fill(view);
            throw new ValidationException("Enter something");
        }
        if (request.getParameter("select").equals("No user")) {
            fill(view);
            throw new ValidationException("Choose recipient");
        }
        view.put("users", userService.findAll());
        User user = (User) request.getSession().getAttribute("user");
        talk.setText(request.getParameter("text"));
        talk.setSourceUserId(user.getId());
        talk.setTargetUserId(Long.parseLong(request.getParameter("select")));
        if (userService.find(Long.parseLong(request.getParameter("select"))) == null) {
            fill(view);
            request.getSession().setAttribute("text", talk.getText());
            throw new ValidationException("Don't have such recipient");
        }
        talkService.save(talk);
        request.getSession().setAttribute("text", "");
        throw new RedirectException("/talk");
    }
}
