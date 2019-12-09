package org.zkoss.zss.essential;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zss.essential.util.BookUtil;
import org.zkoss.zul.Filedownload;

/**
 * This class shows exporter API
 * @author dennis
 *
 */
public class ExportComposer extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;
	
	@Wire
	private Spreadsheet ss;
	
	
	@Listen("onClick = #exportExcel; onExport=#ss")
	public void doExport() throws IOException{
		Exporter exporter = Exporters.getExporter();
		Book book = ss.getBook();
		File file = File.createTempFile(Long.toString(System.currentTimeMillis()),"temp");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			exporter.export(book, fos);
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
		//generate file name upon book type (2007,2003)
		String dlname = BookUtil.suggestName(book);
		Filedownload.save(new AMedia(dlname, "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file, true));
	}

}



