package io.keikai.devref.jsp;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.json.JSONValue;
import io.keikai.jsp.*;
import org.zkoss.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * a servlet to handle ajax request and return the result
 * @author dennis
 *
 */
@WebServlet("/jsp/app4l")
public class ApplicationForLeaveServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final String ACTION = "action";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// parsing fetch API with request body data to JSON Map
		Map<String, String> bodyData = (Map) JSONValue.parse(req.getReader().lines().collect(Collectors.joining()));

		final String action = bodyData.get(ACTION);

		// add custom response message, it depends on your logic.
		final JSONObject appResponse = new JSONObject();
		appResponse.put(ACTION, action);

		final Map keikaiResponse = SmartUpdateBridge.Builder.create(req.getServletContext(), req, resp, bodyData).withBook(book -> {
			//implement business logic
			Sheet sheet = book.getSheetAt(0);
			if("reset".equals(action)){
				reset(sheet);
			}else if("submit".equals(action)){
				submit(sheet, appResponse);
			}
		})
		// build a keikai response for Book change which will be processed by kkjsp
		.build(appResponse);

		resp.getWriter().append(JSONObject.toJSONString(keikaiResponse));
	}
	

	//reset cells to default value
	private void reset(Sheet sheet) {
		//you can use a cell reference to get a range
		Range from = Ranges.range(sheet,"E5");//Ranges.range(sheet,"From");
		//or you can use a name to get a range (the named range has to be set in Excel);
		from.getCellData().setValue(DateUtil.tomorrowDate(0));
		
		Range to = Ranges.rangeByName(sheet,"To");
		to.getCellData().setValue(DateUtil.tomorrowDate(0));
		
		Range reason = Ranges.rangeByName(sheet,"Reason");
		reason.setCellEditText("");
		
		Range applicant = Ranges.rangeByName(sheet,"Applicant");
		applicant.setCellEditText("");
		
		Range requestDate = Ranges.rangeByName(sheet,"RequestDate");
		requestDate.getCellData().setValue(DateUtil.todayDate());
	}
	
	//validate cell data of user input and return a JSONObject
	private void submit(Sheet sheet, JSONObject response) {
		Date from = Ranges.rangeByName(sheet,"From").getCellData().getDateValue();
		Date to = Ranges.rangeByName(sheet,"To").getCellData().getDateValue();
		String reason = Ranges.rangeByName(sheet,"Reason").getCellData().getStringValue();
		Double total = Ranges.rangeByName(sheet,"Total").getCellData().getDoubleValue();
		String applicant = Ranges.rangeByName(sheet,"Applicant").getCellData().getStringValue();
		Date requestDate = Ranges.rangeByName(sheet,"RequestDate").getCellData().getDateValue();
		
		if(from == null){
			response.put("message", "FROM is not a correct date");
		}else if(to == null){
			response.put("message", "TO is not a correct date");
		}else if(total==null || total.intValue()<0){
			response.put("message", "TOTAL small than 1");
		}else if(reason == null){
			response.put("message", "REASON is empty");
		}else if(applicant == null){
			response.put("message", "APPLICANT is empty");
		}else if(requestDate == null){
			response.put("message", "REQUEST DATE is empty");
		}else{
			//Option 1:
			//You can handle your business logic here and return a final result for users directly
			//Or option 2: return necessary form data,
			//so client can process it by submitting that can be handled by Spring MVC or Struts
			response.put("valid", true);
			JSONObject form = new JSONObject();
			response.put("form", form);

			form.put("from", from.getTime());//can't pass as data, use long for time
			form.put("to", to.getTime());//can't pass as data, use long for time
			form.put("reason", reason);
			form.put("total", total.intValue());//we just need int
			form.put("applicant", applicant);
			form.put("requestDate", requestDate.getTime());
			
			//You can also store the book, and load it back later by export it to a file
			Exporter exporter = Exporters.getExporter();
			FileOutputStream fos = null;
			try {
				File temp = File.createTempFile("app4leave_", ".xlsx");
				fos = new FileOutputStream(temp); 
				exporter.export(sheet.getBook(), fos);
				System.out.println("file save at "+temp.getAbsolutePath());
				form.put("archive", temp.getName());
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if(fos!=null)
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}
}
