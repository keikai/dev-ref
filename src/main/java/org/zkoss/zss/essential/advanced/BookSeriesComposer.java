package org.zkoss.zss.essential.advanced;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.io.File;

/**
 * This class demonstrate usage of BookSeriesBuilder
 * @author dennis, Hawk
 *
 */
@SuppressWarnings("serial")
public class BookSeriesComposer extends SelectorComposer<Component> {
	
	@Wire
	Spreadsheet profile;
	
	@Wire
	Spreadsheet resume;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		Importer importer = Importers.getImporter();
		Book book1 = importer.imports(getFile("/WEB-INF/books/resume.xlsx"),"resume.xlsx");
		Book book2 = importer.imports(getFile("/WEB-INF/books/profile.xlsx"),"profile.xlsx");
		//can load more books...
		
		resume.setBook(book1);
		profile.setBook(book2);
		
		BookSeriesBuilder.getInstance().buildBookSeries(new Book[]{book1,book2});
	}
	
	private File getFile(String path){
		return new File(WebApps.getCurrent().getRealPath(path));
	}
}



