package com.zbjct.dajiujiu.socks.basics.config;

import com.zbjct.dajiujiu.socks.basics.query.processor.QueryLoadProcessor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询方法拦截器
 */
public class QueryInterceptor implements HandlerInterceptor {

    private final static String QUERY_PAGE_FLAG = "/query/(.*?)/(.*?)";

    public QueryInterceptor(String contextPath) {
        this.contextPath = contextPath;
    }

    private String contextPath;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (this.existPath(path)) {
            int i = path.lastIndexOf("/");
            String queryGroup = path.substring(i + 1);
            QueryLoadProcessor.put(queryGroup);
            path = path.substring(0, i);
            path = path.replaceAll(contextPath, "");
            request.getRequestDispatcher(path).forward(request, response);
            return false;
        }
        return true;
    }

    private boolean existPath(String path) {
        Pattern pattern = Pattern.compile(QUERY_PAGE_FLAG, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        QueryLoadProcessor.clear();
    }
}
