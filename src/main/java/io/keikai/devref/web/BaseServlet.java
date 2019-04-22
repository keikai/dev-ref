package io.keikai.devref.web;

import io.keikai.devref.Configuration;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

/**
 * accept "server" parameter e.g. http://localhost:8080?server=10.1.1.1:8888
 */
public class BaseServlet extends HttpServlet {
    protected String keikaiServerAddress = Configuration.DEFAULT_KEIKAI_SERVER;
    protected File defaultFileFolder;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        determineServerAddress(req);
    }

    /**
     * determine Keikai server address according to the query string
     * @param request
     */
    protected void determineServerAddress(ServletRequest request) {
        String serverAddress = request.getParameter("server");
        if (serverAddress != null){
            keikaiServerAddress = "http://" + serverAddress;
        }
    }
}
