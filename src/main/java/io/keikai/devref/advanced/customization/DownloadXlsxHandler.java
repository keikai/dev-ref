package io.keikai.devref.advanced.customization;

import io.keikai.api.Exporters;
import io.keikai.api.model.*;
import io.keikai.ui.*;
import io.keikai.ui.impl.DefaultUserActionManagerCtrl;
import org.zkoss.zul.Filedownload;

import java.io.*;

/**
 * Download a book model as an Excel file.
 * @author Hawk
 *
 */
public class DownloadXlsxHandler implements UserActionHandler {
	
	@Override
	public boolean isEnabled(Book book, Sheet sheet) {
		return book!=null;
	}

	@Override
	public boolean process(UserActionContext ctx){
		try{
            downloadBook(ctx.getBook());
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public void downloadBook(Book book) throws IOException{
		FileOutputStream fos = null;
		File temp = File.createTempFile("", book.getBookName());
		temp.deleteOnExit();
		try{
			//write to a temporary file first to avoid write error damage original file
			fos = new FileOutputStream(temp);
			Exporters.getExporter().export(book, fos);
			Filedownload.save(temp, "application/excel");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos!=null)
				fos.close();
		}
	}

	/**
	 * register this handler to the spreadsheet.
	 */
	public static void register(Spreadsheet ss) {
		UserActionManager actionManager = ss.getUserActionManager();
		actionManager.registerHandler(
				DefaultUserActionManagerCtrl.Category.AUXACTION.getName(),
				AuxAction.SAVE_BOOK.getAction(), new DownloadXlsxHandler());
	}
}
