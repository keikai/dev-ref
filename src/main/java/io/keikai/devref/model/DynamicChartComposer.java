package io.keikai.devref.model;

import io.keikai.api.Ranges;
import io.keikai.api.model.Chart;
import io.keikai.model.SName;
import io.keikai.ui.Spreadsheet;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

public class DynamicChartComposer extends SelectorComposer {

    @Wire
    private Spreadsheet ss;

    @Listen("onClick = button")
    public void click(){
        SName playerName = ss.getBook().getInternalBook().getNameByName("Player");
        playerName.setRefersToFormula("OFFSET('named range'!$B$2,0,0, COUNTA('named range'!$B$2:$B$100), 1)");
        Chart chart = ss.getBook().getSheetAt(1).getCharts().get(0);
        Ranges.range(ss.getSelectedSheet()).updateChart(chart); //just update a chart's position

    }
}
