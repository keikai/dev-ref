package io.keikai.devref.advanced.customization.chart;

import io.keikai.model.SChart;
import io.keikaiex.ui.ZssCharts;
import io.keikaiex.util.ChartsCustomizer;

public class MyComponentChartCustomizer implements ChartsCustomizer {

    @Override
    public void customize(ZssCharts chart, SChart chartInfo) {
        // Adjust plot options based on the chart type
        if (chart.getType().equals("line")) {
            chart.setTitle("Component-scope Customized Line Chart");
        }
    }
}