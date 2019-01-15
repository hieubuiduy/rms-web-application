package com.example.springbootmvcsecurity.web;

import com.example.springbootmvcsecurity.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            LOGGER.info(auth.getName()
                    + " was trying to access protected resource: "
                    + httpServletRequest.getRequestURI());
        }

        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/access-denied");
    }
}
