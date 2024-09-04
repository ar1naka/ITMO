package ru.itmo.wp.servlet;

import ru.itmo.wp.util.ImageUtils;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String random_number = Integer.toString((new Random()).nextInt(900) + 100);
        HttpSession session = request.getSession();
        if (request.getMethod().equals("GET") && session.getAttribute("Captcha") == null) {
            String form = getCaptchaForm(Integer.valueOf(random_number));
            response.getWriter().print(form);
            response.getWriter().flush();
            String answer = request.getParameter("answer");
            String number = (String) session.getAttribute("number");
            if (number != null && number.equals(answer)) {
                session.setAttribute("Captcha", "complete");
                response.sendRedirect(request.getRequestURI());
            } else {
                session.setAttribute("number", random_number);
                session.setAttribute("captcha_png", Base64.getEncoder().encodeToString(ImageUtils.toPng(random_number)));
                response.setContentType("text/html");
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    private String getCaptchaForm(Integer code) {
        String encodedImage = Base64.getEncoder().encodeToString(ImageUtils.toPng(code.toString()));
        String htmlFirstPart = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "\t<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "\t<img src=\"data:image/png;base64, ";
        String htmlSecondPart = "\"/>\n" +
                "\t<form method=\"get\" action=\"\">\n" +
                "\t<label for=\"captcha\">Enter: </label>\n" +
                "\t<input name=\"captcha\" id=\"captcha\"/>\n" +
                "\t</form>\n" +
                "</body>\n" +
                "</html>";
        return htmlFirstPart + encodedImage + htmlSecondPart;
    }
}
