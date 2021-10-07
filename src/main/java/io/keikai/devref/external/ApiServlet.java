package io.keikai.devref.external;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

/**
 * a servlet to handle external API request and access a cell of a Book.
 */
public class ApiServlet extends HttpServlet {

    private int rowIndex;
    private int columnIndex;
    private String result = "";
    private SpreadsheetService spreadsheetService;

    @Override
    public void init() throws ServletException {
        try {
            spreadsheetService = new SpreadsheetService();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        extractParameters(req, resp);
        if (resp.isCommitted()){
            return;
        }
        String result = doSpreadsheetRequest(req);
        writeOutResult(result, resp);
    }

    private void writeOutResult(String result, HttpServletResponse resp) throws IOException {
        resp.getWriter().println(result);
    }

    private String doSpreadsheetRequest(HttpServletRequest req) {
        return spreadsheetService.getCellValue(rowIndex, columnIndex);
    }

    private void extractParameters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> rowArg = Optional.ofNullable(request.getParameter("r"));
        if (rowArg.isPresent()){
            rowIndex = Integer.parseInt(rowArg.get());
        }else{
            failureResponse(response);
            return;
        }

        Optional<String> colArg = Optional.ofNullable(request.getParameter("c"));
        if (colArg.isPresent()){
            columnIndex = Integer.parseInt(colArg.get());
        }else{
            failureResponse(response);
            return;
        }
    }

    private void failureResponse(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println("parameter r is required");
    }
}
