package io.keikai.devref;

import io.keikai.devref.app.ReportBuilder;
import io.keikai.devref.app.exchange.CurrencyExchange;
import io.keikai.devref.performance.FillMillion;

import java.io.File;
import java.util.*;
import java.util.logging.*;

public class Configuration {
    public static final String DEFAULT_KEIKAI_SERVER = "http://localhost:8888";
    public static final String INTERNAL_BOOK_FOLDER = "/WEB-INF/book/";
    public static final String KEIKAI_JS = "keikaiJs";
    public static File DEFAULT_BOOK_FOLDER;

    /**
     * a mapping between path and corresponding use case.
     * http://localhost:8080/demo/case/[PATH]
     * PATH : MyClass.class
     */
    public static final Map<String, Class> pathCaseMap = new HashMap<String, Class>();

    static {
        pathCaseMap.put("million", FillMillion.class);
        pathCaseMap.put("language", UiLanguage.class);
        pathCaseMap.put("exchange", CurrencyExchange.class);
        pathCaseMap.put("report", ReportBuilder.class);
    }

    static public void enableSocketIOLog() {
        Logger log = java.util.logging.Logger.getLogger("");
        log.setLevel(Level.FINER);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);

        Iterator<String> pathIterator = pathCaseMap.keySet().iterator();
        while (pathIterator.hasNext()){
            String path = pathIterator.next();
        }

    }
}
