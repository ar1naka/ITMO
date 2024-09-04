package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.itmo.wp.domain.User;

import javax.servlet.http.HttpSession;

@Controller
public class IndexPage extends Page {
    @GetMapping({"", "/"})
    public String index(HttpSession httpSession) {
        User user = getUser(httpSession);
        if (user != null && user.isDisabled()) {
            unsetUser(httpSession);
            setMessage(httpSession, "Your account was blocked.");
            return "redirect:";
        }
        return "IndexPage";
    }



    @GetMapping("/logout")
    public String logout(HttpSession httpSession) {
        unsetUser(httpSession);
        setMessage(httpSession, "Good bye!");
        return "redirect:";
    }
}
