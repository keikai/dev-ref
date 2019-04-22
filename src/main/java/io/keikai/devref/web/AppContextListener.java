package io.keikai.devref.web;

import io.keikai.devref.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Configuration.DEFAULT_BOOK_FOLDER = new File(servletContextEvent.getServletContext().getRealPath(Configuration.INTERNAL_BOOK_FOLDER));
    }


    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}