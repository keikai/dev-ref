package org.zkoss.zss.essential;

import java.io.*;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
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
	@Wire("combobox")
	private Combobox combobox;
	
	Exporter exporter = Exporters.getExporter("pdf");
	
	
	@Listen("onClick = #exportPdf")
	public void doExport() throws IOException{
		Book book = ss.getBook();
		
		File file = File.createTempFile(Long.toString(System.currentTimeMillis()),"temp");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			exporter.export(book, file);
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
		Filedownload.save(new AMedia(book.getBookName()+".pdf", "pdf", "application/pdf", file, true));
	}

	@Listen("onSelect = combobox")
	public void switchFile(){
		String fileName = combobox.getSelectedItem().getLabel();
		ss.setSrc("/WEB-INF/books/"+fileName);
	}
	
}