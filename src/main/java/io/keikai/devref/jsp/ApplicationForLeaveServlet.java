package io.keikai.devref.jsp;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.jsp.JsonUpdateBridge;
import io.keikai.ui.Spreadsheet;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Desktop;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.Date;

/**
 * a servlet to handle ajax request and return the result
 * @author dennis
 *
 */
@WebServlet("/jsp/app4l")
public class ApplicationForLeaveServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//set encoding
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		//json content type
		resp.setContentType("application/json");
		
		//parameter from ajax request, you have to pass it in AJAX request
		//necessary parameter to get ZK server side desktop
		final String desktopId = req.getParameter("desktopId");
		//necessary parameter to get ZK server side spreadsheet
		final String zssUuid = req.getParameter("zssUuid");
		
		final String action = req.getParameter("action");
		
		// prepare a json result object, it can contain your ajax result and
		// also the necessary zk component update result
		final JSONObject result = new JSONObject();
		// set back for client to check action result, it depends on your logic.
		result.put("action", action);
		
		// use utility class to wrap zk in servlet request and
		// get access and response result
		JsonUpdateBridge bridge = new JsonUpdateBridge(getServletContext(), req, resp,
				desktopId) {
			@Override
			protected void process(Desktop desktop) {
				Spreadsheet ss = (Spreadsheet)desktop.getComponentByUuidIfAny(zssUuid);
				Book book = ss.getBook();
				Sheet sheet = book.getSheetAt(0);
				
				if("reset".equals(action)){
					handleReset(sheet,result);
				}else if("check".equals(action)){
					handleCheck(sheet,result);
				}
			}
		};
		
		/*
		 * Generate ZK update result in given JSON object. An AJAX response
		 * handler at client side, zssjsp, will 'eval' this result to update ZK
		 * components.
		 */
		bridge.process(result);

		Writer w = resp.getWriter();
		w.append(result.toJSONString());	
	}
	

	//reset cells to default value
	private void handleReset(Sheet sheet, JSONObject result) {
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
	private void handleCheck(Sheet sheet, JSONObject result) {
		Date from = Ranges.rangeByName(sheet,"From").getCellData().getDateValue();
		Date to = Ranges.rangeByName(sheet,"To").getCellData().getDateValue();
		String reason = Ranges.rangeByName(sheet,"Reason").getCellData().getStringValue();
		Double total = Ranges.rangeByName(sheet,"Total").getCellData().getDoubleValue();
		String applicant = Ranges.rangeByName(sheet,"Applicant").getCellData().getStringValue();
		Date requestDate = Ranges.rangeByName(sheet,"RequestDate").getCellData().getDateValue();
		
		if(from == null){
			result.put("message", "FROM is not a correct date");
		}else if(to == null){
			result.put("message", "TO is not a correct date");
		}else if(total==null || total.intValue()<0){
			result.put("message", "TOTAL small than 1");
		}else if(reason == null){
			result.put("message", "REASON is empty");
		}else if(applicant == null){
			result.put("message", "APPLICANT is empty");
		}else if(requestDate == null){
			result.put("message", "REQUEST DATE is empty");
		}else{
			//Option 1:
			//You can handle your business logic here and return a final result for user directly
			
			
			
			//Or option 2: return necessary form data, 
			//so client can process it by submitting that can be handled by Spring MVC or Struts
			result.put("valid", true);
			JSONObject form = new JSONObject();
			result.put("form", form);

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
						//handle the exception
					}
			}
		}
	}
}
