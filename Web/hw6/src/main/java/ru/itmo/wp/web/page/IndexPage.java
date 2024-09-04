package ru.itmo.wp.web.page;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings({"unused"})
public class IndexPage extends Page {
    private void action(HttpServletRequest request, Map<String, Object> view) {

        putMessage(request, view);
    }

}
