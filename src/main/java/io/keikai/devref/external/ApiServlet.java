package io.keikai.devref.external;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;

/**
 * RESTful API example.
 * a servlet to handle external API requests and access a cell of a Book.
 * API example: http://localhost:8080/dev-ref/api?r=6&c=5
 * r means row index, 0-based.
 * c means column index, 0-based.
 */
public class ApiServlet extends HttpServlet {

    private int rowIndex;
    private int columnIndex;
    private SpreadsheetService spreadsheetService;

    @Override
    public void init() throws ServletException {
        try {
            spreadsheetService = new SpreadsheetService("demo_sample.xlsx");
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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<String> colArg = Optional.ofNullable(request.getParameter("c"));
        if (colArg.isPresent()){
            columnIndex = Integer.parseInt(colArg.get());
        }else{
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
    }

}
