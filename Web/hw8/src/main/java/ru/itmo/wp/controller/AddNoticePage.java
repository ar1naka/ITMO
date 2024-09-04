package ru.itmo.wp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.BindingResult;

import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.NoticeCredentials;
import ru.itmo.wp.form.validator.NoticeCredentialValidator;
import ru.itmo.wp.service.NoticeService;
import ru.itmo.wp.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Objects;

@Controller
public class AddNoticePage extends Page{
    private final NoticeService noticeService;
    private final NoticeCredentialValidator noticeCredentialValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (noticeCredentialValidator.supports(Objects.requireNonNull(binder.getTarget())
                .getClass())) {
            binder.addValidators(noticeCredentialValidator);
        }
    }

    public AddNoticePage(NoticeService noticeService, NoticeCredentialValidator noticeCredentialValidator) {
        this.noticeService = noticeService;
        this.noticeCredentialValidator = noticeCredentialValidator;
    }
    @GetMapping(path = "/addNotice")
    public String addNoticeGet(Model model, HttpSession httpSession) {
        model.addAttribute("notice", new NoticeCredentials());
        User user = getUser(httpSession);
        if (user != null && user.isDisabled()) {
            unsetUser(httpSession);
            setMessage(httpSession, "Your account was blocked.");
            return "redirect:/addNotice";
        }
        return "AddNoticePage";
    }

    @PostMapping(path = "/addNotice")
    public String addNoticePost(@Valid @ModelAttribute("notice") NoticeCredentials notice,
                                BindingResult bindingResult,
                                HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return "AddNoticePage";
        }
        User user = getUser(httpSession);
        if (user.isDisabled()){
            setMessage(httpSession, "you cannot write notices because you were blocked");
            return "redirect:/";
        }
        noticeService.add(notice);
        setMessage(httpSession, "Your post has been added!");
        return "redirect:/";
    }
}
