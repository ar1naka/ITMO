package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.ChangingCredential;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class UsersPage extends Page {
    private final UserService userService;

    public UsersPage(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/all")
    public String users(Model model, HttpSession httpSession) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("disableForm", new UserCredentials());
        User user = getUser(httpSession);
        if (user != null && user.isDisabled()) {
            unsetUser(httpSession);
            setMessage(httpSession, "Your account was blocked.");
            return "redirect:/users/all";
        }
        return "UsersPage";
    }



    @PostMapping( "/users/all")
    public String set(@Valid @ModelAttribute("disableForm") ChangingCredential disableForm,
                      BindingResult bindingResult,
                            HttpSession httpSession) {

        if (bindingResult.hasErrors()) {
            return "UsersPage";
        }

        User loggedInUser = getUser(httpSession);
        if (loggedInUser == null) {
            setMessage(httpSession, "You are not logged!");
            return "redirect:/users/all";
        }
        Long userIdToDisable = disableForm.getUserId();
        if (userIdToDisable.equals(loggedInUser.getId()) && disableForm.getStatus()) {
            setMessage(httpSession, "You cannot disable your own account");
            return "redirect:/users/all";
        }
        if (loggedInUser.isDisabled()) {
            unsetUser(httpSession);
            setMessage(httpSession, "Your account was blocked.");
            return "redirect:/users/all";
        }

        userService.changeStatus(userIdToDisable, disableForm.getStatus());
        return "redirect:/users/all";
    }
}
