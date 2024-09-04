package ru.itmo.wp.web;

import freemarker.template.*;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FrontServlet extends HttpServlet {
    private static final String BASE_PACKAGE = FrontServlet.class.getPackage().getName() + ".page";
    private static final String DEFAULT_ACTION = "action";

    private Configuration sourceConfiguration;
    private Configuration targetConfiguration;


    private Configuration newFreemarkerConfiguration(String templateDirName, boolean debug)
            throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir="
                    + templateDir + "]");
        }
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER :
                TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        return configuration;
    }

    @Override
    public void init() throws ServletException {
        sourceConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("/") + "../../src/main/webapp/WEB-INF/templates", true);
        targetConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("WEB-INF/templates"), false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Route route = Route.newRoute(request);
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException notFoundException) {
                throw new ServletException(notFoundException);
            }
        }
    }


    private Object[] getArguments(Method method, Map<String, Object> view, HttpServletRequest request) {
        Object[] args = new Object[method.getParameterTypes().length];
        for (int i = 0; i < method.getParameterTypes().length; i++) {
            Class<?> paramType = method.getParameterTypes()[i];
            if (paramType.equals(Map.class)) {
                args[i] = view;
            } else if (paramType.equals(HttpServletRequest.class)) {
                args[i] = request;
            }
        }
        return args;
    }

    private void process(Route route, HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, ServletException, IOException {
        Class<?> pageClass;
        try {
            pageClass = Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();
        }

        Method method = null;
        Map<String, Object> view = new HashMap<>();

        Map<Class<?>, Object> result = Map.of(
                Map.class, view,
                HttpServletRequest.class, request
        );
//
//        result.put(Map.class, view);
//        result.put(HttpServletRequest.class, request);

        for (Class<?> clazz = pageClass; method == null && clazz != null; clazz = clazz.getSuperclass()) {

            Method[] methods = clazz.getDeclaredMethods();

            method = Arrays.stream(methods)
                    .filter(m -> m.getName().equals(route.getAction()))
                    .filter(m -> Arrays.stream(m.getParameterTypes()).allMatch(result::containsKey))
                    .findFirst().orElse(null);

            if (method == null) {
                throw new RuntimeException("Method is empty");
            }
        }


//            for (Method m : methods) {
//                if (m.getName().equals(route.getAction())) {
//                    Class<?>[] parameterTypes = m.getParameterTypes();
//                    int c = 0;
//                    for (Class<?> type : parameterTypes) {
//                        if (result.containsKey(type)) {
//                            c++;
//                        }
//                    }
//                    if (c == parameterTypes.length) {
//                        method = m;
//                        break;
//                    }
//                }
//            }
//        }
//
//        if (method == null) {
//            throw new NotFoundException();
//        }

        Object page;
        try {
            page = pageClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                 | NoSuchMethodException | InvocationTargetException e) {
            throw new ServletException("Can't create page [pageClass=" + pageClass + "]");
        }

        method.setAccessible(true);

        List<Object> args = Arrays.stream(method.getParameterTypes())
                .map(result::get)
                .collect(Collectors.toList());

        try {
            method.invoke(page, args.toArray());
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't invoke action method [pageClass=" + pageClass + ", method=" + method + "]");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                RedirectException redirectException = (RedirectException) cause;
                response.sendRedirect(redirectException.getTarget());
                return;
            } else {
                throw new ServletException(
                        "Can't invoke action method [pageClass=" + pageClass + ", method=" + method + "]", cause);
            }
        }

        Template template = newTemplate(pageClass.getSimpleName() + ".ftlh");
        HttpSession session = request.getSession();

        if (request.getParameter("lang") != null){
            session.setAttribute("lang", request.getParameter("lang"));
            String lang = session.getAttribute("lang").toString();
            try {
                template = newTemplate(pageClass.getSimpleName() + "_" + lang + ".ftlh");
            } catch (Exception ignore) {
                template = newTemplate(pageClass.getSimpleName() + ".ftlh");
            }
        }

//        if (session.getAttribute("lang") != null) {
//            String lang = session.getAttribute("lang").toString();
//            try {
//                template = newTemplate(pageClass.getSimpleName() + "_" + lang + ".ftlh");
//            } catch (Exception ignore) {
//                template = newTemplate(pageClass.getSimpleName() + ".ftlh");
//            }
//        }

        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            template.process(view, response.getWriter());
        } catch (TemplateException e) {
            if (sourceConfiguration == null) {
                throw new ServletException(
                        "Can't render template [pageClass=" + pageClass + ", action=" + method + "]", e);
            }
        }
    }

    private Template newTemplate(String templateName) throws ServletException {
        Template template = null;

        if (sourceConfiguration != null) {
            try {
                template = sourceConfiguration.getTemplate(templateName, Locale.ENGLISH);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null && targetConfiguration != null) {
            try {
                template = targetConfiguration.getTemplate(templateName, Locale.ENGLISH);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null) {
            throw new ServletException("Can't find template [templateName=" + templateName + "]");
        }

        return template;
    }

    private static class Route {
        private final String className;
        private final String action;

        private Route(String className, String action) {
            this.className = className;
            this.action = action;
        }

        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newNotFoundRoute() {
            return new Route(NotFoundPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newIndexRoute() {
            return new Route(IndexPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();

            List<String> classNameParts = Arrays.stream(uri.split("/"))
                    .filter(part -> !part.isEmpty())
                    .collect(Collectors.toList());

            if (classNameParts.isEmpty()) {
                return newIndexRoute();
            }

            StringBuilder simpleClassName = new StringBuilder(classNameParts.get(classNameParts.size() - 1));
            int lastDotIndex = simpleClassName.lastIndexOf(".");
            simpleClassName.setCharAt(lastDotIndex + 1,
                    Character.toUpperCase(simpleClassName.charAt(lastDotIndex + 1)));
            classNameParts.set(classNameParts.size() - 1, simpleClassName.toString());

            String className = BASE_PACKAGE + "." + String.join(".", classNameParts) + "Page";

            String action = request.getParameter("action");
            if (action == null || action.isEmpty()) {
                action = DEFAULT_ACTION;
            }

            return new Route(className, action);
        }
    }
}
