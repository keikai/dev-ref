package io.keikai.devref.usecase;

import io.keikai.api.*;
import io.keikai.api.model.Picture;
import io.keikai.devref.util.RangeHelper;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.Events;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkex.zul.Colorbox;
import org.zkoss.zkmax.zul.Signature;
import org.zkoss.zul.*;

public class ScoresheetComposer extends SelectorComposer<Component>{

    private static final String SCORESHEET = "scoresheet";
    private Range teamRange;
    private Range pointRangeA;
    private Range pointRangeB;
    private Range pointRangeC;
    private Range weightRange;
    private Range dateRange;
    private Range signRange;
    private Range teamA;
    private Range teamB;
    private Range teamC;
    private Range teamAtotal;
    private Range teamBtotal;
    private Range teamCtotal;
    private Picture signature;
	@Wire("spreadsheet")
    private Spreadsheet spreadsheet;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		teamRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamrange");
		pointRangeA = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "pointrangeA");
		pointRangeB = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "pointrangeB");
		pointRangeC = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "pointrangeC");
		weightRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "weightrange");
		dateRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "daterange");
		signRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "signrange");
		teamA = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamA");
		teamB = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamB");
		teamC = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamC");
		teamAtotal = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamAtotal");
		teamBtotal = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamBtotal");
		teamCtotal = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "teamCtotal");
	}

	@Listen(Events.ON_CELL_CLICK + "=spreadsheet")
    public void onCellClick(CellMouseEvent e) {
    	if (RangeHelper.isRangeClicked(e, teamRange))
    		contextPopupTeamColor(e);
    	if (RangeHelper.isRangeClicked(e, pointRangeA))
    		contextPopupPointSpinner(e);
    	if (RangeHelper.isRangeClicked(e, pointRangeB))
    		contextPopupPointSpinner(e);
    	if (RangeHelper.isRangeClicked(e, pointRangeC))
    		contextPopupPointSpinner(e);
    	if (RangeHelper.isRangeClicked(e, weightRange))
    		contextPopupWeightSlider(e);
    	if (RangeHelper.isRangeClicked(e, dateRange))
    		contextPopupDatePicker(e);
    	if (RangeHelper.isRangeClicked(e, signRange))
    		contextPopupSignature(e);
    }

	private void contextPopupSignature(CellMouseEvent e) {
		Popup pop = new Popup();
		pop.setPage(spreadsheet.getPage());
		Signature sign = new Signature();
		sign.addEventListener("onSave", (UploadEvent evt) -> {
			byte[] byteData = evt.getMedia().getByteData();
			if(signature != null)
				signRange.deletePicture(signature);
			signature = signRange.addPicture(new SheetAnchor(e.getRow(), e.getColumn()+2, 4, 4, e.getRow()+3, e.getColumn()+4, -4, -4), byteData, Picture.Format.PNG );
			pop.close();
			spreadsheet.focus();
		});
		sign.setWidth("250px");
		sign.setHeight("250px");
		pop.appendChild(sign);
		pop.open(e.getClientx(), e.getClienty());
		
	}

	private void contextPopupDatePicker(CellMouseEvent e) {
		Popup pop = new Popup();
		pop.setPage(spreadsheet.getPage());
		Calendar calendar = new Calendar();
		Range targetCell = Ranges.range(spreadsheet.getSelectedSheet(), e.getRow(), e.getColumn()).toCellRange(0, 2);
		calendar.addEventListener(org.zkoss.zk.ui.event.Events.ON_CHANGE, (Event evt) -> {
			targetCell.setCellValue(calendar.getValue());
			pop.close();
			spreadsheet.focus();
		});
		pop.appendChild(calendar);
		pop.open(e.getClientx(), e.getClienty());
		
	}


	private void contextPopupWeightSlider(CellMouseEvent e) {
		Popup pop = new Popup();
		pop.setPage(spreadsheet.getPage());
		Slider slider = new Slider();
		Range targetCell = Ranges.range(spreadsheet.getSelectedSheet(), e.getRow(), e.getColumn()).toCellRange(0, 0);
		slider.setCurpos(((Double)targetCell.getCellValue()) * 100);
		slider.addEventListener(org.zkoss.zk.ui.event.Events.ON_SCROLL, (Event evt) -> {
			targetCell.setCellValue(new Double(slider.getCurpos())/100);
			pop.close();
			spreadsheet.focus();
		});
		slider.setWidth("300px");
		slider.setStep(1);
		slider.setMinpos(0);
		slider.setMaxpos(100);
		pop.appendChild(slider);
		pop.open(e.getClientx(), e.getClienty());
		
	}

	private void contextPopupPointSpinner(CellMouseEvent e) {
		Popup pop = new Popup();
		pop.setPage(spreadsheet.getPage());
		Spinner spinner = new Spinner();
		Range targetCell = Ranges.range(spreadsheet.getSelectedSheet(), e.getRow(), e.getColumn()).toCellRange(0, 0);
		spinner.setValue(((Number)targetCell.getCellValue()).intValue());
		spinner.addEventListener(org.zkoss.zk.ui.event.Events.ON_CHANGE, (Event evt) -> {
			targetCell.setCellValue(spinner.getValue());
			pop.close();
			spreadsheet.focus();
		});
		spinner.setWidth("100px");
		spinner.setStep(1);
		spinner.setConstraint("no empty,min 0 max 100");
		pop.appendChild(spinner);
		pop.open(e.getClientx(), e.getClienty());
		
	}

	private void contextPopupTeamColor(CellMouseEvent e) {
		Popup pop = new Popup();
		pop.setPage(spreadsheet.getPage());
		Colorbox colorbox = new Colorbox();
		Range targetCell = Ranges.range(spreadsheet.getSelectedSheet(), e.getRow(), e.getColumn()).toCellRange(0, 0);
		colorbox.setValue(targetCell.getCellStyle().getBackColor().getHtmlColor());
		colorbox.addEventListener(org.zkoss.zk.ui.event.Events.ON_CHANGE, (Event evt) -> {
			Range teamRange = getTeamRangeFromClick(e);
			Range teamTotalRange = getTeamTotalRangeFromClick(e);
			CellOperationUtil.applyBackColor(teamRange, colorbox.getColor());
			CellOperationUtil.applyBackColor(teamTotalRange, colorbox.getColor());
			pop.close();
			spreadsheet.focus();
		});
		pop.appendChild(colorbox);
		pop.open(e.getClientx(), e.getClienty());
	}

	private Range getTeamRangeFromClick(CellMouseEvent e) {
		if (RangeHelper.isRangeClicked(e, teamA))
			return teamA;
		if (RangeHelper.isRangeClicked(e, teamB))
			return teamB;
		if (RangeHelper.isRangeClicked(e, teamC))
			return teamC;
		return null;
	}
	private Range getTeamTotalRangeFromClick(CellMouseEvent e) {
		if (RangeHelper.isRangeClicked(e, teamA))
			return teamAtotal;
		if (RangeHelper.isRangeClicked(e, teamB))
			return teamBtotal;
		if (RangeHelper.isRangeClicked(e, teamC))
			return teamCtotal;
		return null;
	}
    
}