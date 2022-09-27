package io.keikai.devref.importer;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.model.sys.formula.*;
import org.zkoss.lang.Library;
import org.zkoss.util.resource.*;
import org.zkoss.xel.taglib.Taglib;
import org.zkoss.xel.util.TaglibMapper;

import java.io.*;
import java.net.*;

/**
 * demonstrate how to import a file without web environment.
 * Run this class with Maven: 
 * <code>mvn compile exec:java -Dexec.mainClass="io.keikai.devref.importer.ImportTest"</code>
 */
public class ImportTest {
    final static String TAGLIB_KEY = "http://www.zkoss.org/zss/functions";

    public static void main(String[] args) throws IOException {
        setupDefaultProperties();
        loadTaglib();
        Book book = importFile(new File("src/main/webapp/WEB-INF/books/customFunction.xlsx"));
        validate(book);
    }

    /**
     * If you run Keikai in a web application, it will set these properties by config.xml automatically.
     * Since this usage runs Keikai without web environment, we need to set them manually.
     */
    protected static void setupDefaultProperties() {
        Library.setProperty("io.keikai.model.FormulaEngine.class", "io.keikaiex.formula.FormulaEngineEx");
        Library.setProperty("io.keikai.model.FunctionResolver.class", "io.keikaiex.formula.FunctionResolverEx");
        Library.setProperty(TAGLIB_KEY, "financial,engineering,math,statistical,text,info,datetime,logical");
    }

    protected static Book importFile(File file) throws IOException {
        Importer importer = Importers.getImporter();
        return importer.imports(file, "sample");
    }

    /**
     * If your sheet contains a custom function in a formula,  you need to load your tld to make keikai evaluate it correctly.
     * Here it shows 3 ways to load a taglib.
     */
    protected static void loadTaglib() {
        FunctionResolver resolver = FunctionResolverFactory.createFunctionResolver();
        TaglibMapper mapper = (TaglibMapper)resolver.getFunctionMapper();
        // 1. load the taglib declared in config.xml
//		mapper.load(new Taglib("keikai", "http://www.zkoss.org/zss/essentials/custom"), new ClassLocator());
        // 2. load the tld directly in the classpath
//        mapper.load(new Taglib("keikai", "/web/tld/function.tld"), new ClassLocator());
        // 3. load the tld under WEB-INF
		mapper.load(new Taglib("keikai", "/WEB-INF/tld/function.tld"), new FileLocator());
    }

    static void validate(Book book) {
        Range formulaCell = Ranges.range(book.getSheetAt(0), "C6");
        System.out.println(formulaCell.getCellEditText());
        System.out.println(formulaCell.getCellFormatText().equals("156.5")? "Pass" : "Failed");
    }

    static class FileLocator implements Locator{

        @Override
        public String getDirectory() {
            return null;
        }

        @Override
        public URL getResource(String name) {
            try {
                return new File("src/main/webapp" + name).toURI().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return null;
        }
    }
}
