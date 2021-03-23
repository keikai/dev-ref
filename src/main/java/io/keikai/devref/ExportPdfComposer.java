package io.keikai.devref;

import java.io.*;

import io.keikai.model.*;
import io.keikai.model.impl.pdf.*;
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

    //the default way, it uses the file's printing setup
//	private Exporter exporter = Exporters.getExporter("pdf");
    // use this if you need to change print setup
    private PdfExporter pdfExporter = new PdfExporter();

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
                    pdfExporter.export(ss.getBook().getInternalBook(), file);
                    break;
                case SHEET:
                    pdfExporter.export(ss.getSelectedSSheet(), fos);
                    break;
                case SELECTION:
                    pdfExporter.export(new SheetRegion(ss.getSelectedSSheet(), new CellRegion(ss.getSelection().asString())), fos);
                    break;
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
        SPrintSetup setup = pdfExporter.getPrintSetup();
        setup.setHCenter(centerH.isChecked());
        setup.setVCenter(centerV.isChecked());
        setup.setPrintGridlines(printGridlines.isChecked());
    }
}