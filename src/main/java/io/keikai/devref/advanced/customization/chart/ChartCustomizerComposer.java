package io.keikai.devref.advanced.customization.chart;

import io.keikai.devref.advanced.customization.DownloadXlsxHandler;
import io.keikai.ui.Spreadsheet;
import io.keikaiex.ui.ZssCharts;
import io.keikaiex.util.ChartsHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.*;

public class ChartCustomizerComposer extends SelectorComposer<Component> {

    @Wire("#ss")
    private Spreadsheet spreadsheet;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        // keikai calls global customizer first, then component-specific customizer
        ChartsHelper.setCustomizer(spreadsheet, new MyComponentChartCustomizer());
        DownloadXlsxHandler.register(spreadsheet);
    }

    @Listen("onClick = #customize")
    public void customizeChart() {
        ZssCharts areaChart = ChartsHelper.getChartsByName(spreadsheet, "Chart 2");
        if (areaChart != null) {
            areaChart.setTitle("Programmatically Customized Chart");
        }
    }
}
