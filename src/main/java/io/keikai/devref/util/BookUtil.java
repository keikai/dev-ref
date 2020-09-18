package io.keikai.devref.util;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.api.model.Book.BookType;
import io.keikai.range.*;
import org.zkoss.lang.SystemException;
import org.zkoss.zk.ui.WebApps;

import java.io.*;
import java.util.function.Supplier;


public class BookUtil {
	public static String DEFAULT_BOOK_FOLDER = "/WEB-INF/books/";
	/**
	 * Create a {@link Book} named "blank.xlsx" with one sheet "Sheet1".
	 */
	static public Book newBook() {
		return newBook(null);
	}

	static public Book newBook(String bookName){
		if (bookName == null){
			bookName = "blank.xlsx";
		}
		Book book = Books.createBook(bookName);
		Ranges.range(book).createSheet("Sheet1");
		return book;
	}

	static public Book newBook(String bookName, BookType type) {
		try {
			return loadBlankBook(bookName, type);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	static private Book loadBlankBook(String bookName, BookType type)
			throws IOException {
		
		Importer importer = Importers.getImporter();
		if(importer==null){
			throw new RuntimeException("importer for excel not found");
		}
		InputStream is = null;
		switch (type) {
		case XLS:
			is = WebApps.getCurrent().getResourceAsStream("/WEB-INF/books/blank.xls");
			break;
		case XLSX:
			is = WebApps.getCurrent().getResourceAsStream("/WEB-INF/books/blank.xlsx");
			break;
		default :
			throw new IllegalArgumentException("Unknow book type" + type);
		}
		return importer.imports(is,bookName);
	}

	static File workingFolder;

	static public File getWorkingFolder() {
		if (workingFolder == null) {
			synchronized (BookUtil.class) {
				if (workingFolder == null) {
					workingFolder = new File(
							System.getProperty("java.io.tmpdir"), "kktmp");
					if (!workingFolder.exists()) {
						if (!workingFolder.mkdirs()) {
							throw new SystemException(
									"Can't get working folder:"
											+ workingFolder.getAbsolutePath());
						}
					}
				}
			}
		}
		return workingFolder;
	}

	
	static public String suggestName(Book book) {
		String bookName = book.getBookName();
		BookType type = book.getType();
		
		String ext = type==BookType.XLS?".xls":".xlsx";
		int i = bookName.lastIndexOf('.');
		if(i==0){
			bookName = "book";
		}else if(i>0){
			bookName = bookName.substring(0,i);
		}
		return bookName+ext;
	}

	static public File saveBookToTemp(Book book) throws IOException{
		Exporter exporter = Exporters.getExporter("excel");
		String bookName = book.getBookName();
		String ext = book.getType()==BookType.XLS?".xls":".xlsx";
		int i = bookName.lastIndexOf('.');
		if(i==0){
			bookName = "book";
		}else if(i>0){
			bookName = bookName.substring(0,i);
		}
		
		File tempFile = File.createTempFile(Long.toString(System.currentTimeMillis()),ext,getWorkingFolder());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(tempFile);
			exporter.export(book, fos);
		}finally{
			if(fos!=null){
				fos.close();
			}
		}
		return tempFile;
	}
	
	static public Book copySheetToNewBook(String bookName, Sheet sheet){
		Book newBook = newBook(bookName);
		Ranges.range(newBook).cloneSheetFrom(sheet.getSheetName(), sheet);
		Ranges.range(newBook.getSheetAt(0)).deleteSheet();
		return newBook;
	}

	/**
	 * get a file from {@link #DEFAULT_BOOK_FOLDER}
	 */
	static public File getFile(String fileName) {
		return new File(WebApps.getCurrent().getRealPath(DEFAULT_BOOK_FOLDER + fileName));
	}

	/**
	 * override the default importer by "excel", all spreadsheets will import with this custom importer
	 */
	static public void overrideDefaultImporter(Supplier<SImporter> importerSupplier) {
		registerCustomImporter("excel", importerSupplier);
	}
	/**
	 * register an extra importer with any preferred type then retrieve it back by {@link Importers#getImporter(String)}
	 * @param type any preferred type except "excel" reserved for default
	 * @param importerSupplier suggest to return a new instance of {@link SImporter}
	 */
	static public void registerCustomImporter(String type, Supplier<SImporter> importerSupplier) {
		SImporters.register(type, new SImporterFactory() {
			@Override
			public SImporter createImporter() {
				return importerSupplier.get();
			}
		});
	}
}
