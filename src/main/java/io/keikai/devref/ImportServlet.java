package io.keikai.devref;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.model.sys.formula.*;
import org.zkoss.util.resource.ClassLocator;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.TaglibMapper;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;

public class ImportServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		importFile(req);
		resp.getWriter().print("imported");
	}

	private void importFile(HttpServletRequest req) throws IOException {
		FunctionResolver fr = FunctionResolverFactory.createFunctionResolver();
		TaglibMapper mapper = (TaglibMapper)fr.getFunctionMapper();
		//load the taglib declared in config.xml
		mapper.load(new Taglib("zss", "http://www.zkoss.org/zss/essentials/custom"), new ClassLocator());
		//load the tld directly in the classpath
//		mapper.load(new Taglib("zss", "/web/tld/function.tld"), new ClassLocator());
		//load the tld under WEB-INF
//		mapper.load(new Taglib("zss", "/WEB-INF/tld/function.tld"), new ServletContextLocator(getServletContext())); 
		
		Importer importer = Importers.getImporter();
		Book book = importer.imports(new File(req.getSession().getServletContext().getRealPath("/WEB-INF/books/customFunction.xlsx")), "sample");
		Range formulaCell = Ranges.range(book.getSheetAt(0), 4, 2);
		System.out.println("c5 is " + formulaCell.getCellEditText()+", "+ formulaCell.getCellValue());
	}

}
