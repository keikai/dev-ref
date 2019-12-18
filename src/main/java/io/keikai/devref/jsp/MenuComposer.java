package io.keikai.devref.jsp;

import io.keikai.api.*;
import io.keikai.api.model.Book;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.io.*;
import java.util.Arrays;

public class MenuComposer extends SelectorComposer {

    @Wire
    private Spreadsheet ss;
    @Wire
    private Listbox filelistBox;

    static String DEFAULT_FILE_FOLDER = "/WEB-INF/books/";
    final private File BOOK_FOLDER = new File(getPage().getDesktop().getWebApp().getRealPath(DEFAULT_FILE_FOLDER));
    private ListModelList fileListModel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        fileListModel = new ListModelList();
        filelistBox.setModel(fileListModel);
    }

    @Listen("onUpload = #upload")
    public void importUploadedFile(UploadEvent e){
        ByteArrayInputStream uploadedFile = new ByteArrayInputStream(e.getMedia().getByteData());

        Importer importer = Importers.getImporter();
        try {
            Book book = importer.imports(uploadedFile, e.getName());
            ss.setBook(book);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Listen("onClick = #load")
    public void load(){
        fileListModel.clear();
        String[] xlsxFiles = generateBookList();
        Arrays.sort(xlsxFiles);
        fileListModel.addAll(Arrays.asList(xlsxFiles));
    }

    private String[] generateBookList() {
        return BOOK_FOLDER.list((dir, name) -> {
            return name.endsWith("xlsx");
        });
    }
}
