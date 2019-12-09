package org.zkoss.zss.essential.advanced.customization;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.ui.*;
import org.zkoss.zk.ui.WebApps;
import java.io.*;

/**
 * Load a blank Excel file to spreadsheet.
 * @author Hawk
 *
 */
public class NewBookHandler implements UserActionHandler {

	@Override
	public boolean isEnabled(Book book, Sheet sheet) {
		return true;
	}

	@Override
	public boolean process(UserActionContext context) {
		Importer importer = Importers.getImporter();
		
		try {
			Book loadedBook = importer.imports(new File(WebApps.getCurrent()
							.getRealPath("/WEB-INF/books/blank.xlsx")), "blank.xlsx");
			context.getSpreadsheet().setBook(loadedBook);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
