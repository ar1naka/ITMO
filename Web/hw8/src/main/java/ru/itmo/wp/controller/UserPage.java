package ru.itmo.wp.controller;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.service.UserService;

import javax.validation.constraints.Pattern;

@Controller
public class UserPage extends Page{
    private final UserService userService;

    public UserPage(UserService userService) {
        this.userService = userService;
    }
    /*@GetMapping("/user/{id}")
    public String get(@Pattern(regexp = "^[0-9]") @PathVariable("id") @NumberFormat String id, Model model) {
        User user = null;
        try {
            user = userService.findById(Long.parseLong(id));
        } catch (NumberFormatException ignored) {}
        model.addAttribute("userP", user);
        return "UserPage";
    }*/
    @GetMapping("/user/")
    public String empty(Model model) {
        model.addAttribute("user", null);
        return "UserPage";
    }
    @GetMapping("/user/{id}" )
    public String get (@PathVariable("id") String id, Model model) {
        long userId;
        try {
            userId = Long.parseLong(id);
        } catch (NumberFormatException error) {
            return "UserPage";
        }
        User user = userService.findById(userId);
        model.addAttribute("userP", user);
        return "UserPage";
    }
}
