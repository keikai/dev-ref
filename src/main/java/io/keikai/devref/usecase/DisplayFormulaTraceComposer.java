package io.keikai.devref.usecase;

import io.keikai.api.*;
import io.keikai.api.model.Sheet;
import io.keikai.ui.Spreadsheet;
import org.zkoss.chart.*;
import org.zkoss.chart.model.DefaultFromToModel;
import org.zkoss.chart.plotOptions.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;
import org.zkoss.zul.*;

import java.util.Set;

/**
 * @author Hawk
 */
public class DisplayFormulaTraceComposer extends SelectorComposer<Component> {

    @Wire
    private Spreadsheet spreadsheet;
    @Wire
    private Label cellAddress;

    @Wire("window .precedentBox")
    private Listbox precedentBox;
    @Wire("window .dependentBox")
    private Listbox dependentBox;
    @Wire("window charts")
    private Charts charts;
    private ListModelList<Range> precedentList = new ListModelList<>();
    private ListModelList<Range> dependentList = new ListModelList<>();
    private DefaultFromToModel fromToModel = new DefaultFromToModel();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        precedentBox.setModel(precedentList);
        dependentBox.setModel(dependentList);

        charts.setType(Charts.NETWOKRGRAPH);
        charts.setModel(fromToModel);
        NetworkGraphLayoutAlgorithm algorithm = charts.getPlotOptions().getNetworkGraph().getLayoutAlgorithm();
        algorithm.setLinkLength(40);
        algorithm.setEnableSimulation(true);
        charts.getPlotOptions().getSeries().getDataLabels().setEnabled(true);
        charts.getPlotOptions().getSeries().getDataLabels().setLinkFormat(""); //make link text empty
    }

    @Listen(Events.ON_CLICK + " = #trace")
    public void trace() {
        cellAddress.setValue(spreadsheet.getCellFocus().asString());
        precedentList.clear();
        dependentList.clear();
        Set<Range> precedents = Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()).getDirectPrecedents();
        Set<Range> dependents = Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()).getDirectDependents();
        precedentList.addAll(precedents);
        dependentList.addAll(dependents);
        renderChart();
    }

    @Listen("onFocusCell = #rangeWin")
    public void focus(ForwardEvent e){
        Range range = (Range)((Listitem)e.getOrigin().getTarget()).getValue();
        if (!isInCurrentSheet(range)){
            spreadsheet.setSelectedSheet(range.getSheet().getSheetName());
        }
        spreadsheet.focusTo(range.getRow(), range.getColumn());
    }

    private boolean isInCurrentSheet(Range range) {
        return range.getSheet().getSheetName().equals(spreadsheet.getSelectedSheetName());
    }

    private void renderChart(){
        for (int n=0 ; n <charts.getSeriesSize(); n++){
            charts.getSeries(n).remove();
        }
        addDependent();
        addPrecedent();
    }

    private static final String PRECEDENT_COLOR = "green";
    private static final String DEPENDENT_COLOR = "orange";

    /**
     * precedent is "from", dependent is "to".
     */
    private void addDependent() {
        String selectedCellRef = spreadsheet.getCellFocus().asString();
        charts.getSeries().setName(selectedCellRef);
        charts.getSeries().setColor(DEPENDENT_COLOR);
        dependentList.stream().forEach(range -> {
            charts.getSeries().addPoint(selectedCellRef, range.toString());
        });
    }

    private void addPrecedent() {
        String selectedCellRef = spreadsheet.getCellFocus().asString();
        charts.getSeries(1).setName(selectedCellRef);
        charts.getSeries(1).setColor(PRECEDENT_COLOR);
        precedentList.stream().forEach(range -> {
            charts.getSeries(1).addPoint(range.toString(), selectedCellRef);
        });
    }

}
