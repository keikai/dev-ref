package org.zkoss.zss.essential;

import io.keikai.api.*;
import io.keikai.api.model.Picture;
import io.keikai.api.model.Picture.*;
import io.keikai.ui.Spreadsheet;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.io.*;

/**
 * Demonstrate picture related API usage.
 * @author Hawk
 * 
 */
@SuppressWarnings("serial")
public class PictureComposer extends SelectorComposer<Component> {

	@Wire
	private Intbox toRowBox;
	@Wire
	private Intbox toColumnBox;
	@Wire
	private Spreadsheet ss;
	@Wire
	private Listbox pictureListbox;
	
	private ListModelList<Picture> pictureList = new ListModelList<Picture>();


	public void add() {
		try{ 
			AImage image = new AImage(WebApps.getCurrent().getResource("/zklogo.png"));
			SheetAnchor anchor = SheetOperationUtil.toFilledAnchor(ss.getSelectedSheet(),
					ss.getSelection().getRow(), ss.getSelection().getColumn(), 
					image.getWidth(), image.getHeight());
			Ranges.range(ss.getSelectedSheet())
				.addPicture(anchor, image.getByteData(), Format.PNG);
			refreshPictureList();
		}catch(IOException e){
			System.out.println("cannot add a picture for "+ e);
		}
	}
	
	

	public void delete() {
		if (pictureListbox.getSelectedItem() != null){
			Ranges.range(ss.getSelectedSheet()).deletePicture(
					(Picture)pictureListbox.getSelectedItem().getValue());
			refreshPictureList();
		}
	}
	
	
	public void move() {
		if (pictureListbox.getSelectedItem() != null){
			//calculate destination anchor
			SheetAnchor fromAnchor = ((Picture) pictureListbox.getSelectedItem()
					.getValue()).getAnchor();
			int rowOffset = fromAnchor.getLastRow() - fromAnchor.getRow();
			int columnOffset = fromAnchor.getLastColumn() - fromAnchor.getColumn();
			SheetAnchor toAnchor = new SheetAnchor(toRowBox.getValue(), 
					toColumnBox.getValue(),
					fromAnchor.getXOffset(), fromAnchor.getYOffset(),
					toRowBox.getValue()+rowOffset, toColumnBox.getValue()+columnOffset,
					fromAnchor.getLastXOffset(), fromAnchor.getLastYOffset());
			
			Ranges.range(ss.getSelectedSheet())
				.movePicture(toAnchor, (Picture)pictureListbox.getSelectedItem().getValue());
			refreshPictureList();
		}
	}
	
	private void refreshPictureList(){
		pictureList.clear();
		pictureList.addAll(ss.getSelectedSheet().getPictures());
		pictureListbox.setModel(pictureList);
	}
	
	@Listen("onClick = #addButton")
	public void addByUtil() {
		Range selection = Ranges.range(ss.getSelectedSheet(), ss.getSelection());
		try{
			SheetOperationUtil.addPicture(selection,
				new AImage(WebApps.getCurrent().getResource("/zklogo.png")));
			refreshPictureList();
		}catch(IOException e){
			System.out.println("cannot add a picture for "+ e);
		}
	}
	
	@Listen("onClick = #deleteButton")
	public void deleteByUtil() {
		if (pictureListbox.getSelectedItem() != null){
			SheetOperationUtil.deletePicture(Ranges.range(ss.getSelectedSheet()),
					(Picture)pictureListbox.getSelectedItem().getValue());
			refreshPictureList();
		}
	}
	
	@Listen("onClick = #moveButton")
	public void moveByUtil(){
		if (pictureListbox.getSelectedItem() != null){
			SheetOperationUtil.movePicture(Ranges.range(ss.getSelectedSheet()),
					(Picture)pictureListbox.getSelectedItem().getValue(),
					toRowBox.getValue(), toColumnBox.getValue());
			refreshPictureList();
		}
	}
	
	@Listen("onClick = #exportButton")
	public void export() throws IOException {
		Exporter excelExporter = Exporters.getExporter("excel");
		File file = new File("exported.xlsx");
		FileOutputStream fos = new FileOutputStream(file);
		excelExporter.export(ss.getBook(), fos);
		Filedownload.save(file, "application/excel");
	}
}
