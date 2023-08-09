package io.keikai.devref.jsf;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.api.model.Sheet;
import io.keikai.jsf.Action;
import io.keikai.jsf.ActionBridge;
import jakarta.annotation.ManagedBean;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.bean.RequestScoped;
import jakarta.faces.context.FacesContext;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


@ManagedBean
@RequestScoped
public class ApplicationForLeave {

	/*
	 * the book of spreadsheet
	 */
	private Book book;
	
	/*
	 * the bridge to execute action in ZK context
	 */
	private ActionBridge actionBridge;
	private Range fromCell;
	private Range toCell;
	private Range reasonCell;
	private Range applicantCell;
	private Range requestDateCell;
	private Range totalCell;

	public Book getBook() {
		if (book != null) {
			return book;
		}
		try {
			URL bookUrl = FacesContext.getCurrentInstance()
					.getExternalContext()
					.getResource("/WEB-INF/books/application_for_leave.xlsx");
			book = Importers.getImporter().imports(bookUrl, "app4leave");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		initRanges();
		resetFields();
		return book;
	}

	private void resetFields() {
		// use range API to set the cell data
		fromCell.getCellData().setValue(getDate(LocalDate.now().plusDays(1))); //tomorrow
		toCell.getCellData().setValue(getDate(LocalDate.now().plusDays(1)));
		reasonCell.setCellEditText("");
		applicantCell.setCellEditText("");
		requestDateCell.getCellData().setValue(getDate(LocalDate.now()));
	}

	private Date getDate(LocalDate date){
		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	private void initRanges() {
		Sheet sheet = book.getSheetAt(0);
		fromCell = Ranges.rangeByName(sheet, "From"); // equivalent to Ranges.range(sheet, "E5");
		toCell = Ranges.rangeByName(sheet, "To");
		reasonCell = Ranges.rangeByName(sheet, "Reason");
		applicantCell = Ranges.rangeByName(sheet, "Applicant");
		requestDateCell = Ranges.rangeByName(sheet, "RequestDate");
		totalCell = Ranges.rangeByName(sheet, "Total");
	}

	public void setBook(Book book) {
		this.book = book;
	}
	
	public ActionBridge getActionBridge() {
		return actionBridge;
	}

	public void setActionBridge(ActionBridge actionBridge) {
		this.actionBridge = actionBridge;
	}

	public void doReset() {
		//use actionBridge to execute the action inside ZK context
		//so the spreadsheet can get the update of book automatically
		actionBridge.execute(new Action() {
			public void execute() {
				initRanges();
				resetFields();
			}
		});
		addMessage("Reset book");
	}
	
	private void addMessage(String message){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
	}

	public void doOk() {
		actionBridge.execute(new Action() {
			public void execute() {
				initRanges();
				Date from = fromCell.getCellData().getDateValue();
				Date to = toCell.getCellData().getDateValue();
				String reason = reasonCell.getCellData().getStringValue();
				Double total = totalCell.getCellData().getDoubleValue();
				String applicant = applicantCell.getCellData().getStringValue();
				Date requestDate = requestDateCell.getCellData().getDateValue();

				if (!validateInput(from, to, reason, total, applicant, requestDate)){
					return;
				}

				//Handle your business logic here
				addMessage("Your request are sent, following is your data");
				addMessage("From :" + from);
				addMessage("To :" + to);
				addMessage("Reason :" + reason);
				addMessage("Total :" + total.intValue());//we only need int
				addMessage("Applicant :" + applicant);
				addMessage("RequestDate :" + requestDate.getTime());

				exportXlsx();
			}
		});
	}

	private void exportXlsx() {
		//You can also store the book, and load it back later by exporting it to a file
		Exporter exporter = Exporters.getExporter();
		FileOutputStream fos = null;
		try {
			File temp = File.createTempFile("app4leave_", ".xlsx");
			fos = new FileOutputStream(temp);
			exporter.export(book, fos);
			System.out.println("file save at " + temp.getAbsolutePath());

			addMessage("Archive " + temp.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @return true for pass, false for failure
	 */
	private boolean validateInput(Date from, Date to, String reason, Double total, String applicant, Date requestDate) {
		if(from == null){
			addMessage("FROM cannot be empty");
		}
		if(to == null){
			addMessage("TO cannot be empty");
		}
		if(total==null || total.intValue()<0){
			addMessage("TOTAL small than 1");
		}
		if(reason == null){
			addMessage("REASON cannot be empty");
		}
		if(applicant == null){
			addMessage("APPLICANT cannot be empty");
		}
		if(requestDate == null){
			addMessage("REQUEST DATE cannot be empty");
		}
		return !FacesContext.getCurrentInstance().getMessages().hasNext();
	}
}
