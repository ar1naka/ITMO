package ru.itmo.wp.servlet;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

public class MessageServlet extends HttpServlet {
    private static class Message {
        private final String user;
        private final String text;

        private Message(String user, String text) {
            this.user = user;
            this.text = text;
        }
    }

    private ArrayList<Message> messages = new ArrayList <>();

    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        switch (uri) {
            case "/message/auth":
                auth(request, response);
                break;
            case "/message/findAll":
                findAll(response);
                break;
            case "/message/add":
                add(request);
                break;
        }
    }

    private void auth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");

        if (user != null) {
            request.getSession().setAttribute("user", user);
        }
        String json = new Gson().toJson(user);
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    private void findAll( HttpServletResponse response) throws IOException {
        String json = new Gson().toJson(messages);
        response.getWriter().print(json);
        response.getWriter().flush();
    }

    private void add(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String text = request.getParameter("text");
        String name = (String) session.getAttribute("user");
        messages.add(new Message(name, text));
    }
}