package io.keikai.devref.model;

import java.io.*;

import io.keikai.api.*;
import io.keikai.model.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;


/**
 * This class demonstrate pdf exporter API
 *
 * @author Hawk
 */
public class ExportPdfComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet ss;
    @Wire
    private Listbox fileBox;

    @Wire
    private Window printDialog;

    @Wire("#printDialog #centerH")
    private Checkbox centerH;
    @Wire("#printDialog #centerV")
    private Checkbox centerV;
    @Wire("#printDialog #printGridlines")
    private Checkbox printGridlines;
    @Wire("#printDialog selectbox")
    private Selectbox scopeBox;

    enum PrintingScope {BOOK, SHEET, SELECTION}

    private ListModelList<PrintingScope> printingScopeModel = new ListModelList(PrintingScope.values());

    private Exporter exporter = Exporters.getExporter("pdf");

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        scopeBox.setModel(printingScopeModel);
        printingScopeModel.addToSelection(PrintingScope.BOOK);
    }

    @Listen("onClick = #printDialog #exportPdf")
    public void doExport(Event event) throws IOException {
        printDialog.setVisible(false);
        loadSetup();

        File file = File.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            switch (printingScopeModel.getSelection().iterator().next()) {
                case BOOK:
                    exporter.export(ss.getBook(), fos);
                    break;
                case SHEET:
                case SELECTION:
                    // Since Keikai 7.0, PdfExporter only exports a whole book;
                    // sheet- and selection-scoped export methods were removed.
                    Messagebox.show("Exporting a single sheet or selection is not supported since Keikai 7.0");
                    return;
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        Filedownload.save(new AMedia(ss.getBook().getBookName() + ".pdf", "pdf", "application/pdf", file, true));
    }

    @Listen("onSelect = listbox")
    public void loadFile() {
        String fileName = fileBox.getSelectedItem().getLabel();
        ss.setSrc("/WEB-INF/books/" + fileName);
    }

    public void loadSetup() {
        // Since Keikai 7.0, the exporter reads print settings from the workbook
        // itself; configure each sheet's SPrintSetup instead of the exporter.
        for (SSheet sheet : ss.getBook().getInternalBook().getSheets()) {
            SPrintSetup setup = sheet.getPrintSetup();
            setup.setHCenter(centerH.isChecked());
            setup.setVCenter(centerV.isChecked());
            setup.setPrintGridlines(printGridlines.isChecked());
        }
    }
}