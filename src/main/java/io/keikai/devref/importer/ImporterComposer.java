package io.keikai.devref.importer;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;

import java.io.File;

/**
 * Demonstrate Importer.
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class ImporterComposer extends SelectorComposer<Component> {

	@Wire
	Spreadsheet ss;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);  //wire variables and event listeners
		//access components after calling super.doAfterCompose()
		//import and load the template excel file "/WEB-INF/books/demo_sample.xlsx"
		Importer importer = Importers.getImporter();
		Book book = importer.imports(getFile(), "sample");
		ss.setBook(book);
	}
	
	
	private File getFile() {
		//get a file 
		return new File(WebApps.getCurrent().getRealPath("/WEB-INF/books/demo_sample.xlsx"));
	}
}
