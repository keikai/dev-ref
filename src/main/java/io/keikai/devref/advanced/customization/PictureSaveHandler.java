package io.keikai.devref.advanced.customization;

import io.keikai.ui.UserActionContext;
import io.keikaiex.ui.impl.ua.InsertPictureHandler;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.WebApps;

import java.io.*;

/**
 * save the inserted picture as a file.
 */
public class PictureSaveHandler extends InsertPictureHandler {

    private String targetFolder;

    @Override
    protected boolean doInsertPicture(UserActionContext ctx, Media media) {
        targetFolder = WebApps.getCurrent().getRealPath("/WEB-INF/");
        // get picture insertion cell address
        System.out.println("insert at " + ctx.getSheet().getSheetName() + ctx.getSelection());
        byte[] bytes = media.getByteData();
        try {
            File file = new File(targetFolder,media.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.doInsertPicture(ctx, media);
    }
}
