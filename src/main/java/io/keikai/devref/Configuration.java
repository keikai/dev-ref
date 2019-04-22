package io.keikai.devref;

import java.io.File;
import java.util.*;
import java.util.logging.*;

public class Configuration {
    public static final String DEFAULT_KEIKAI_SERVER = "http://localhost:8888";
    public static final String INTERNAL_BOOK_FOLDER = "/WEB-INF/book/";
    public static final String KEIKAI_JS = "keikaiJs";
    public static File DEFAULT_BOOK_FOLDER;

    /**
     * a mapping between file and corresponding application class
     */
    public static final Map<String, Class> fileAppMap = new HashMap<String, Class>();

    static {
        fileAppMap.put("million", io.keikai.devref.performance.FillMillion.class);
    }

    static public void enableSocketIOLog() {
        Logger log = java.util.logging.Logger.getLogger("");
        log.setLevel(Level.FINER);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        log.addHandler(handler);
    }
}
