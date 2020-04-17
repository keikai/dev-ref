package io.keikai.devref.advanced.customization;

import io.keikai.api.*;
import io.keikai.api.model.*;
import io.keikai.ui.*;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Filedownload;

import java.io.*;

/**
 * export the whole book to a PDF file and let users to download with a browser.
 */
public class ExportPdfHandler implements UserActionHandler {
    private Exporter exporter = Exporters.getExporter("pdf");

    @Override
    public boolean isEnabled(Book book, Sheet sheet) {
        return true;
    }

    @Override
    public boolean process(UserActionContext userActionContext) {
        Book book = userActionContext.getBook();
        File file = null;
        try {
            file = File.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                exporter.export(book, file);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
            Filedownload.save(new AMedia(book.getBookName() + ".pdf", "pdf", "application/pdf", file, true));
        } catch (IOException e) {
            Notification.show(e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
}
