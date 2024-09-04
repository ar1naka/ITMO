package ru.itmo.wp.form.validator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itmo.wp.domain.User;
import ru.itmo.wp.form.NoticeCredentials;
import ru.itmo.wp.form.UserCredentials;
import ru.itmo.wp.service.UserService;

@Component
public class NoticeCredentialValidator implements Validator {
    public boolean supports(Class<?> clazz) {
        return NoticeCredentials.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        if (!errors.hasErrors()) {
            NoticeCredentials notice = (NoticeCredentials) target;
            if (notice.getContent().trim().isEmpty()) {
                errors.rejectValue(
                        "content", "content.error", "Empty Input");
            }
        }
    }
}
