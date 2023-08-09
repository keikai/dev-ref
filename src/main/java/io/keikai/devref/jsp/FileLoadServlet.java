package io.keikai.devref.jsp;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class FileLoadServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //pre-process, e.g. file upload
        //store some information into a request, pass (forward) to a page with ZSS
        request.getRequestDispatcher("/index.jsp").forward(request,response);
    }
}
