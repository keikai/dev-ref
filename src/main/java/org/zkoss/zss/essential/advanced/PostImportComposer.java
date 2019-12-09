package org.zkoss.zss.essential.advanced;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;

import java.io.*;

/**
 * This class demonstrate @PostImport which can reduce the importing time for the massive formulas
 * @author Hawk
 *
 */
@SuppressWarnings("serial")
public class PostImportComposer extends SelectorComposer<Component> implements PostImport {

	@Wire
	private Spreadsheet ss;
	@Wire("checkbox")
	private Checkbox postImportingBox;
	private String src = "/WEB-INF/books/blank.xlsx";
	private final File FILE = new File(WebApps.getCurrent().getRealPath(src));
	private Importer importer = Importers.getImporter();
	private String POST_IMPORT_KEY = "post-import";

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		long start = System.currentTimeMillis();
		if (isPostImported()){
			loadWithPostImporting();
		}else{
			loadDirectly();
		}
		long end = System.currentTimeMillis();
		postImportingBox.setChecked(isPostImported());
		Clients.showNotification("consumed (ms):"+(end-start));
	}

	@Override
	public void process(Book book) {
		initializeMassiveFormulas(book.getSheetAt(0));
	}

	/**
	 * Increase row and column here, you will see bigger time difference between post importing and non-post importing. 
	 * @param sheet
	 */
	private void initializeMassiveFormulas(Sheet sheet){
		for (int row = 0 ; row < 1500 ; row++){
			for (int col = 0 ; col < 50 ; col++){
				String editText; 
				if (row > 0 ){
					editText = "=" +Ranges.getCellRefString(row-1, col);
				}
				else{
					editText = ""+(row+col);
				}
				Ranges.range(sheet, row, col).setCellEditText(editText);
			}
		}
	}
	
	private void loadWithPostImporting() throws IOException{
		Book book = importer.imports(FILE, "blank", this);
		ss.setBook(book);
	}
	
	private void loadDirectly() throws IOException{
		Book book = importer.imports(FILE, "blank");
		ss.setBook(book);
		initializeMassiveFormulas(ss.getSelectedSheet());
	}

	private boolean isPostImported() {
		return Sessions.getCurrent().getAttribute(POST_IMPORT_KEY) != null;
	}
	
	@Listen("onCheck = checkbox")
	public void togglePostImporting(){
		if (isPostImported()){
			Sessions.getCurrent().removeAttribute(POST_IMPORT_KEY);
		}else{
			Sessions.getCurrent().setAttribute(POST_IMPORT_KEY, "true");
		}
	}
}



