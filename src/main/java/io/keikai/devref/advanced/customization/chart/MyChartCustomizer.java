package io.keikai.devref.advanced.customization.chart;

import io.keikai.model.SChart;
import io.keikaiex.ui.ZssCharts;
import io.keikaiex.util.ChartsCustomizer;
import org.zkoss.chart.Color;

import java.util.Arrays;

public class MyChartCustomizer implements ChartsCustomizer {
    @Override
    public void customize(ZssCharts chart, SChart chartInfo) {
        // Adjust plot options based on the chart type
        if (chartInfo.getType()== SChart.ChartType.COLUMN) {
            chart.setTitle("App-scope Customized Column Chart");
            chart.getPlotOptions().getColumn().setStacking("normal");
            // Customize series colors
            chart.setColors(Arrays.asList(
                    new Color("#FFFF00"),  // Yellow
                    new Color("#FFA500"),  // Orange
                    new Color("#800080")   // Purple
            ));
        }
    }
}