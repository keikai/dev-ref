package io.keikai.devref.advanced.customization;

import io.keikai.api.Exporters;
import io.keikai.api.model.*;
import io.keikai.ui.*;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;

import java.io.*;

/**
 * Save a book model as an Excel file.
 * @author Hawk
 *
 */
public class SaveBookHandler implements UserActionHandler {
	
	@Override
	public boolean isEnabled(Book book, Sheet sheet) {
		return book!=null;
	}

	@Override
	public boolean process(UserActionContext ctx){
		try{
			Book book = ctx.getBook();
			save(book);
			Clients.showNotification("saved "+book.getBookName(),"info",null,null,2000,true);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public void save(Book book) throws IOException{
		String savingPath = WebApps.getCurrent().getRealPath("/WEB-INF/books/")+File.separator;
		File targetFile = new File(savingPath
				+ book.getBookName());
		FileOutputStream fos = null;
		try{
			//write to temporary file first to avoid write error damage original file 
			File temp = File.createTempFile("temp", targetFile.getName());
			fos = new FileOutputStream(temp);
			Exporters.getExporter().export(book, fos);
			
			fos.close();
			fos = null;
			
			copy(temp,targetFile);
			temp.delete();
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos!=null)
				fos.close();
		}
	}
	
	public void copy(File src, File dest) throws IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		byte[] buff = new byte[1024];
		try{
			fis = new FileInputStream(src);
			fos = new FileOutputStream(dest);
			int r;
			while( (r=fis.read(buff))>-1){
				fos.write(buff,0,r);
			}
		}finally{
			if(fis!=null){
				try{
					fis.close();
				}catch(Exception x){}
			}
			if(fos!=null){
				try{
					fos.close();
				}catch(Exception x){}
			}
		}
	}
}
