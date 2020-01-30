package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.model.sys.formula.*;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.TaglibMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

/**
 * An example to access an Excel file without {@link io.keikai.ui.Spreadsheet}.
 * Just import an xlsx file and access cells via {@link Range} API.
 */
@WebServlet("/import")
public class ImportServlet extends HttpServlet {

	private Book book;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		importFile(req);
		readCell(resp);
	}

	private void importFile(HttpServletRequest req) throws IOException {
		Importer importer = Importers.getImporter();
		book = importer.imports(new File(req.getSession().getServletContext().getRealPath("/WEB-INF/books/data_format.xlsx")), "sample");
	}

	private void readCell(HttpServletResponse response) throws IOException {
		String cell = "I2";
		Range formulaCell = Ranges.range(book.getSheetAt(0), cell);
		response.getWriter().print(cell + " is " + formulaCell.getCellEditText()+", "+ formulaCell.getCellValue());
	}

}
