package com.caseyellow.server.central.configuration;

import org.apache.log4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static java.util.Objects.nonNull;

@Component
public class UserLoggingFilter implements Filter {

    private static final String USER_HEADER = "Case-Yellow-User";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        addUser(getUserHeader(request));
        filterChain.doFilter(request, response);
        removeUser();
    }


    @Override
    public void destroy() {}

    private void addUser(String userName) {
        if (nonNull(userName)) {
            MDC.put("correlation-id", userName);
        }
    }

    private void removeUser() {
        MDC.remove("correlation-id");
    }

    private String getUserHeader(ServletRequest request) {
        return ((HttpServletRequest)request).getHeader(USER_HEADER);
    }
}
