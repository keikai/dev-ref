package io.keikai.devref.jsp;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.jsp.BookProvider;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;

import java.net.URL;

public class DemoBookProvider implements BookProvider {
	
	public Book loadBook(ServletContext servletContext, HttpServletRequest request, HttpServletResponse res) {
		
		Book book;
		try {
			URL bookUrl = servletContext.getResource("/WEB-INF/books/application_for_leave.xlsx");
			book = Importers.getImporter().imports(bookUrl, "app4leave");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//initialize the book model
		Sheet sheet = book.getSheetAt(0);
		//reset sample data
		//you can use a cell reference to get a range
		Range from = Ranges.range(sheet,"E5");//Ranges.range(sheet,"From");
		//or you can use a name to get a range (the named rnage has to be set in book);
		Range to = Ranges.rangeByName(sheet,"To");
		Range reason = Ranges.rangeByName(sheet,"Reason");
		Range applicant = Ranges.rangeByName(sheet,"Applicant");
		Range requestDate = Ranges.rangeByName(sheet,"RequestDate");
		
		//use range api to set the cell data
		from.getCellData().setValue(DateUtil.tomorrowDate(0));
		to.getCellData().setValue(DateUtil.tomorrowDate(0));
		reason.setCellEditText("");
		applicant.setCellEditText("");
		requestDate.getCellData().setValue(DateUtil.todayDate());
		
		return book;
	}

}
