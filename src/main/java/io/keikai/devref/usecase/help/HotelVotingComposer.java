package io.keikai.devref.usecase.help;

import io.keikai.api.*;
import io.keikai.devref.util.RangeHelper;
import io.keikai.model.impl.ColorImpl;
import io.keikai.ui.Spreadsheet;
import io.keikai.ui.event.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zkmax.zul.Drawer;
import org.zkoss.zuti.zul.ShadowTemplate;

public class HotelVotingComposer extends SelectorComposer {

    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Drawer helpDrawer;
    private ShadowTemplate moreInfo = new ShadowTemplate(true);

    private Range moreInfoRange;
    private static final String CHECKMARK = "√";
    private Range voteRange; //clicked vote range
    private Range clickedCell; //clicked cell
    private static final String MY_VOTE_COLOR = "#F77228";

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        moreInfoRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "More");
        moreInfo.apply(helpDrawer);
    }

    @Listen(Events.ON_CELL_CLICK + " = spreadsheet")
    public void onCellClicked(CellMouseEvent e){
        if (RangeHelper.isRangeClicked(e, moreInfoRange)) {
            helpDrawer.open();
            clickedCell = RangeHelper.getTargetRange(e);
            showMoreInfo(clickedCell);
        }else if (isVotingRangeClicked(e)){
            updateVote();
        }
    }

    /**
     * find the corresponding cell in Vote1 and update the vote count.
     */
    @Listen("onVote = #helpDrawer")
    public void onVote(){
        voteRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "Vote1");
        clickedCell = voteRange.toCellRange(clickedCell.getRow() - voteRange.getRow(), 0);
        updateVote();
    }

    @Listen(org.zkoss.zk.ui.event.Events.ON_OPEN + "= drawer")
    public void focusSpreadsheet(OpenEvent e){
        if (!e.isOpen()) { //more user friendly: avoid clicking to focus spreadsheet after closing more info
            spreadsheet.focus();
        }
    }

    private void showMoreInfo(Range cell) {
        String hotelName = moreInfoRange.toCellRange(cell.getRow()-moreInfoRange.getRow(), 0).getCellData().getStringValue();
        moreInfo.setTemplateURI(hotelName + ".zul");
        moreInfo.apply(helpDrawer);
    }

    /**
     * update the checkmark position and the corresponding vote count according to the clicked cell.
     */
    private void updateVote() {
        //update checkmark
        voteRange.clearContents();
        clickedCell.setCellValue(CHECKMARK);
        //update vote count
        Range voteCount = voteRange.toShiftedRange(0, 5);
        int row = voteCount.getRow();
        for (int offset = 0 ; offset < voteCount.getRowCount() ; offset++){
            Range eachCount = voteCount.toCellRange(offset, 0);
            if (eachCount.getCellStyle().getFont().getColor().getHtmlColor().equalsIgnoreCase(MY_VOTE_COLOR)){
                eachCount.setCellValue(eachCount.getCellData().getDoubleValue().intValue()-1);
                CellOperationUtil.applyFontColor(eachCount, ColorImpl.BLACK.getHtmlColor());
                break;
            }
        }
        Range myVote = clickedCell.toShiftedRange(0, 5);
        myVote.setCellValue(myVote.getCellData().getDoubleValue().intValue() + 1);
        CellOperationUtil.applyFontColor(myVote, MY_VOTE_COLOR);
    }

    /**
     * determine whether a user clicking is inside those voting ranges or not.
     * @return true means a user clicks a cell inside one of the voting ranges
     */
    private boolean isVotingRangeClicked(CellMouseEvent e) {
        int n = 1;
        do{
            Range eachVoteRange = Ranges.rangeByName(spreadsheet.getSelectedSheet(), "Vote"+n);
            if (RangeHelper.isRangeClicked(e, eachVoteRange)){
                voteRange = eachVoteRange;
                clickedCell = RangeHelper.getTargetRange(e);
                return true;
            }
            n++;
        }while(n<=4);
        voteRange = null;
        return false;
    }
}
