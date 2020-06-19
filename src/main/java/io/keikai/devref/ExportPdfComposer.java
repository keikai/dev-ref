package io.keikai.devref;

import java.io.*;

import io.keikai.api.model.Book;
import io.keikai.model.SPrintSetup;
import io.keikai.model.impl.pdf.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;


/**
 * This class demonstrate pdf exporter API
 * @author Hawk
 *
 */
public class ExportPdfComposer extends SelectorComposer<Component> {

	@Wire
	private Spreadsheet ss;
	@Wire
	private Listbox fileBox;

	@Wire
	private Window printDialog;

	@Wire("#printDialog #centerH")
	private Checkbox centerH;
	@Wire("#printDialog #centerV")
	private Checkbox centerV;
	@Wire("#printDialog #printGridlines")
	private Checkbox printGridlines;

//	Exporter exporter = Exporters.getExporter("pdf"); //another way
	PdfExporter pdfExporter = new PdfExporter();
	
	@Listen("onClick = #printDialog #exportPdf")
	public void doExport(Event event) throws IOException{
		printDialog.setVisible(false);
		loadSetup();

		File file = File.createTempFile(Long.toString(System.currentTimeMillis()),"temp");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			pdfExporter.export(ss.getBook().getInternalBook(), file);
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
		Filedownload.save(new AMedia(ss.getBook().getBookName()+".pdf", "pdf", "application/pdf", file, true));
	}

	@Listen("onSelect = listbox")
	public void loadFile(){
		String fileName = fileBox.getSelectedItem().getLabel();
		ss.setSrc("/WEB-INF/books/"+fileName);
	}

	public void loadSetup(){
		SPrintSetup setup = pdfExporter.getPrintSetup();
		setup.setHCenter(centerH.isChecked());
		setup.setVCenter(centerV.isChecked());
		setup.setPrintGridlines(printGridlines.isChecked());
	}
}