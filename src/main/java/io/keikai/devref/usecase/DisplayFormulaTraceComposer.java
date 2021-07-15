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

import java.util.*;
import java.util.function.Function;

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
        algorithm.setLinkLength(20);
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
        clearSeries();
        int maxLevel = Integer.parseInt(Optional.ofNullable(charts.getAttribute("maxLevel", true)).orElse("1").toString());
        addDependent(maxLevel);
        addPrecedent(maxLevel);
    }

    private void clearSeries() {
        int size = charts.getSeriesSize();
        for (int n=0 ; n < size; n++){
            charts.getSeries().remove();
        }
    }

    private static final String PRECEDENT_COLOR = "green";
    private static final String DEPENDENT_COLOR = "orange";

    private void addDependent(int maxLevel) {
        List<Point> points = traceCells(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection())
                , maxLevel, DisplayFormulaTraceComposer::getDirectDependents);

        String selectedCellRef = spreadsheet.getCellFocus().asString();
        charts.getSeries().setName(selectedCellRef);
        charts.getSeries().setColor(DEPENDENT_COLOR);
        points.stream().forEach(p -> { charts.getSeries().addPoint(p); });
        makeLargeCenter(points, charts.getSeries());
    }

    private void makeLargeCenter(List<Point> points, Series series) {
        Node node = series.getNodes();
        node.setId(points.get(0).getFrom());
        node.getMarker().setRadius(10);
    }

    private void addPrecedent(int maxLevel) {
        List<Point> points = traceCells(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection())
                , maxLevel, DisplayFormulaTraceComposer::getDirectPrecedents);

        String selectedCellRef = spreadsheet.getCellFocus().asString();
        charts.getSeries(1).setName(selectedCellRef);
        charts.getSeries(1).setColor(PRECEDENT_COLOR);
        points.stream().forEach(p -> { charts.getSeries(1).addPoint(p); });
        makeLargeCenter(points, charts.getSeries(1));
    }

    /**
     * @param origin the starting cell to trace
     * @param maxLevel the max level to trace. A1 -> B1 -> C1 is 2 levels.
     * @return a list of from-to points
     */
    private List<Point> traceCells(Range origin, int maxLevel, Function<Range, Set<Range>> getNextLevelCell) {
        List<Range> cellsToTrace = new LinkedList<>();
        cellsToTrace.add(origin);

        List<Point> points = new LinkedList<>();
        for (int pointLevel = 1 ; pointLevel <= maxLevel ; pointLevel++){
            List<Range> referencedCells = new LinkedList<>();
            while (!cellsToTrace.isEmpty()){
                Range cell = cellsToTrace.remove(0);
                // get direct referenced cells (precedents/dependents) of current level
                // turn cells into points
                Set<Range> directReferencedCells = getNextLevelCell.apply(cell);
                for (Range range : directReferencedCells){
                    Point p = new Point(cell.toString(), range.toString());
                    points.add(p);
                    if (pointLevel == 1){
                        p.setWidth(5);
                    }
                };
                // if doesn't reach the max level, collect the next level cells
                if (pointLevel < maxLevel){
                    referencedCells.addAll(directReferencedCells);
                }
            }
            cellsToTrace.addAll(referencedCells);
        }
        return points;
    }

    static Set<Range> getDirectDependents(Range range){
        Set<Range> dependents = new HashSet<>();
        range.zOrderStream().forEach(cell ->  {
            dependents.addAll(cell.getDirectDependents());
        });
        return dependents;
    }
    static Set<Range> getDirectPrecedents(Range range){
        Set<Range> precedents = new HashSet<>();
        range.zOrderStream().forEach(cell ->  {
            precedents.addAll(cell.getDirectPrecedents());
        });
        return precedents;
    }
}
