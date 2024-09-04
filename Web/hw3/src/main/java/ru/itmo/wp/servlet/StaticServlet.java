package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String[] uris = uri.split("\\+");

        for (String u : uris) {
            File file = getFile(u);
            if (file.isFile()) {
                if (response.getContentType() == null) {
                    response.setContentType(getServletContext().getMimeType(file.getAbsolutePath()));
                }
                try (OutputStream outputStream = response.getOutputStream()) {
                    Files.copy(file.toPath(), outputStream);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
    private File getFile(String uri) {
        File file = new File("/Users/arinaka/Downloads/hw3/src/main/webapp/static", uri);
        if (!file.isFile()) {
            file = new File(getServletContext().getRealPath("/static"), uri);
        }
        return file;
    }
    /*protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        String[] uris = uri.split("\\+");
        String path = "/Users/arinaka/Downloads/hw3/src/main/webapp/static";
        File file = new File(path + uri);
        if (!file.isFile()) {
            file = new File(getServletContext().getRealPath("/static" + uri));
        }
        if (file.isFile()) {
            try (OutputStream outputStream = response.getOutputStream()) {
                Files.copy(file.toPath(), outputStream);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/
}


