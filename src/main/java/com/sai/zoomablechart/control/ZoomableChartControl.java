/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.control;

import com.sai.zoomablechart.model.ChartData;
import com.sai.zoomablechart.model.ChartZoomValue;
import com.sai.zoomablechart.model.TimeRange;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Customized LineChart control.
 *
 * @author sai.dandem
 * @param <T>
 */
public class ZoomableChartControl<T extends ChartData> extends StackPane {

    private TimeAxis xAxis;

    private NumberAxis yAxis;

    private StackPane mask;

    private LineChart<Number, Number> lineChart;

    private XYChart.Series chartSeries;

    private ScrollBar scroll;

    private final ObservableList<ChartZoomValue> zoomValues = FXCollections.observableArrayList();

    private ChartZoomValue currentZoomValue;

    private ToggleGroup zoomGroup;

    private Date dataUpperBound;

    private Date dataLowerBound;

    private double pressedX;

    private double pressedLowerBound;

    private double pressedUpperBound;

    private double boundPerPx;

    private StringConverter<Number> valueFormatter;

    public ZoomableChartControl() {
        init(null, null);
    }

    public ZoomableChartControl(Number yAxisLower, Number yAxisUpper) {
        init(yAxisLower, yAxisUpper);
    }

    private void init(Number yAxisLower, Number yAxisUpper) {
        getStyleClass().add("line-chart-control");
        mask = new StackPane();
        mask.getStyleClass().add("chart-mask");
        StackPane maskBg = new StackPane();
        maskBg.getStyleClass().add("chart-mask-background");
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setMaxSize(100, 100);
        mask.getChildren().addAll(maskBg, indicator);

        HBox zoomPane = new HBox();
        zoomPane.setPadding(new Insets(5, 10, 5, 10));
        zoomPane.setSpacing(5);
        zoomPane.setAlignment(Pos.CENTER_LEFT);

        zoomGroup = new ToggleGroup();
        zoomGroup.selectedToggleProperty().addListener((obs, old, toggle) -> {
            if (toggle == null && old != null) {
                zoomGroup.selectToggle(old);
            }
        });
        zoomValues.addListener((Change<? extends ChartZoomValue> c) -> {
            zoomPane.getChildren().clear();
            if (!zoomValues.isEmpty()) {
                zoomValues.forEach(v -> {
                    ToggleButton tb = new ToggleButton(v.getDisplayValue());
                    tb.setFocusTraversable(false);
                    tb.setToggleGroup(zoomGroup);
                    tb.setOnAction(e -> updateChart(v));
                    zoomPane.getChildren().add(tb);
                });
            }
        });

        // Defaulting xAxis for 1 week range and 7 sections.
        Date today = new Date();
        Date oneWeekBefore = DateUtils.addDays(today, -7);
        xAxis = new TimeAxis("", oneWeekBefore.getTime(), today.getTime(),
                (today.getTime() - oneWeekBefore.getTime()) / 7);

        if (yAxisLower == null || yAxisUpper == null) {
            yAxis = new NumberAxis();
        } else {
            yAxis = new NumberAxis("", yAxisLower.doubleValue(), yAxisUpper.doubleValue(), 1);
        }

        this.chartSeries = new XYChart.Series();
        this.lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().addAll(chartSeries);
        lineChart.setOnScroll((ev) -> {
            if (!isDataEmptyOrWithinAxis()) {
                if (ev.isDirect()) {
                    // Scroll based on touch.
                    double diff = ev.getDeltaX(); // +ve to right and -ve to left.
                    dragChart(diff, xAxis.getLowerBound(), xAxis.getUpperBound());
                } else {
                    // Scroll from mouse.
                    scrollChart(ev.getDeltaY());
                }
            }
            ev.consume();
        });
        lineChart.setOnTouchPressed(e -> {
            onPressed(e.getTouchPoint().getX());
            e.consume();
        });
        lineChart.setOnMousePressed(e -> {
            if (!e.isSynthesized() && !isDataEmptyOrWithinAxis()) {
                onPressed(e.getX());
            }
            e.consume();
        });
        lineChart.setOnTouchMoved(e -> {
            e.consume();
        });
        lineChart.setOnMouseDragged(e -> {
            if (!e.isSynthesized() && !isDataEmptyOrWithinAxis()) {
                dragChart((e.getX() - pressedX), pressedLowerBound, pressedUpperBound);
            }
            e.consume();
        });

        StackPane chartPane = new StackPane();
        chartPane.setAlignment(Pos.CENTER_LEFT);
        chartPane.setPadding(new Insets(0, 25, 0, 0));
        VBox.setVgrow(chartPane, Priority.ALWAYS);
        chartPane.getChildren().add(lineChart);

        scroll = new ScrollBar();
        scroll.getStyleClass().add("chart-scroll-bar");
        scroll.valueProperty().addListener((obs, old, nxtLb) -> {
            double diff = xAxis.getUpperBound() - xAxis.getLowerBound();
            xAxis.setLowerBound(nxtLb.doubleValue());
            xAxis.setUpperBound(nxtLb.doubleValue() + diff);
        });

        VBox box = new VBox();
        box.getChildren().addAll(zoomPane, chartPane, scroll);

        getChildren().addAll(box, mask);

        setOnMouseClicked(e -> requestFocus());
        setFocusTraversable(true);
        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.LEFT) {
                scrollChart((e.getCode() == KeyCode.RIGHT) ? 1 : -1);
            }
        });
    }

    private void onPressed(double x) {
        if (isDataEmptyOrWithinAxis()) {
            return;
        }
        this.pressedX = x;
        this.pressedLowerBound = xAxis.getLowerBound();
        this.pressedUpperBound = xAxis.getUpperBound();

        double xMin = xAxis.getDisplayPosition(xAxis.getLowerBound());
        double xMax = xAxis.getDisplayPosition(xAxis.getUpperBound());
        double plotWidth = xMax - xMin;
        this.boundPerPx = (xAxis.getUpperBound() - xAxis.getLowerBound()) / plotWidth;
    }

    private void dragChart(double diff, double iniLowerBound, double iniUpperBound) {
        double nxtUb;
        double nxtLb;
        if (diff > 0) { // Dragged to right
            nxtLb = iniLowerBound - (diff * boundPerPx);
            if (nxtLb < dataLowerBound.getTime()) {
                nxtLb = dataLowerBound.getTime();
            }
            nxtUb = nxtLb + currentZoomValue.getVisibleChartBound();

        } else if (diff < 0) { // Dragged to left
            nxtUb = iniUpperBound + (-1 * diff * boundPerPx);
            if (nxtUb > dataUpperBound.getTime()) {
                nxtUb = dataUpperBound.getTime();
            }
            nxtLb = nxtUb - currentZoomValue.getVisibleChartBound();

        } else {
            nxtLb = iniLowerBound;
            nxtUb = iniUpperBound;
        }
        xAxis.setUpperBound(nxtUb);
        xAxis.setLowerBound(nxtLb);
        scroll.setValue(nxtLb);
    }

    private void scrollChart(double delta) {
        double nxtUb;
        double nxtLb;
        if (delta > 0) {
            nxtUb = xAxis.getUpperBound() + (currentZoomValue.getTickUnit() / 2);
            if (nxtUb > dataUpperBound.getTime()) {
                nxtUb = dataUpperBound.getTime();
            }
            nxtLb = nxtUb - currentZoomValue.getVisibleChartBound();
        } else {
            nxtLb = xAxis.getLowerBound() - (currentZoomValue.getTickUnit() / 2);
            if (nxtLb < dataLowerBound.getTime()) {
                nxtLb = dataLowerBound.getTime();
            }
            nxtUb = nxtLb + currentZoomValue.getVisibleChartBound();
        }
        xAxis.setUpperBound(nxtUb);
        xAxis.setLowerBound(nxtLb);
        scroll.setValue(nxtLb);
    }

    private void updateChart(ChartZoomValue chartZoomValue) {
        if (!chartZoomValue.equals(this.currentZoomValue)) {
            this.currentZoomValue = chartZoomValue;
            if (isDataEmpty()) {
                // Default Axis setting.
                Date nxtUb = new Date();
                Date nxtLb = new Date((long)(nxtUb.getTime() - currentZoomValue.getVisibleChartBound()));
                xAxis.setLowerBound(nxtLb.getTime());
                xAxis.setUpperBound(nxtUb.getTime());
            } else {
                Date xAxisUpper = new Date((long)xAxis.getUpperBound());
                Date xAxisLower = TimeRange.getRangeStartDate(xAxisUpper, chartZoomValue.getRange());
                boolean isDataWithInNewAxisBound = ((dataLowerBound.getTime() >= xAxisLower.getTime())
                        && (dataUpperBound.getTime() <= xAxisUpper.getTime()));

                if (isDataWithInNewAxisBound) {
                    xAxis.setLowerBound(dataUpperBound.getTime() - currentZoomValue.getVisibleChartBound());
                    xAxis.setUpperBound(dataUpperBound.getTime());
                } else if (xAxisLower.after(dataLowerBound)) {
                    xAxis.setLowerBound(xAxisLower.getTime());
                } else { // Re adjust the x axis bounds from start.
                    xAxis.setLowerBound(dataLowerBound.getTime());
                    xAxis.setUpperBound(dataLowerBound.getTime() + currentZoomValue.getVisibleChartBound());
                }
            }
            xAxis.setTickUnit(currentZoomValue.getTickUnit());
            updateScrollBar();
        }
    }

    private void updateScrollBar() {
        if (isDataEmptyOrWithinAxis()) {
            scroll.setVisible(false);
        } else {
            double visibleBound = xAxis.getUpperBound() - xAxis.getLowerBound();
            scroll.setMin(this.dataLowerBound.getTime());
            scroll.setMax(this.dataUpperBound.getTime() - visibleBound);
            scroll.setVisibleAmount(visibleBound);
            scroll.setValue(xAxis.getLowerBound());
            //System.out.println("Scroll diff :: " + (scroll.getMax() - scroll.getMin()));
            scroll.setVisible((scroll.getMax() - scroll.getMin()) > 0.0);
        }
    }

    private boolean isDataEmptyOrWithinAxis() {
        return isDataEmpty() || ((dataLowerBound.getTime() >= xAxis.getLowerBound())
                && (dataUpperBound.getTime() <= xAxis.getUpperBound()));
    }

    private boolean isDataEmpty() {
        return dataUpperBound == null;
    }

    /* *** PUBLIC API METHODS *** */
    /**
     * Sets the possible zoom values to chart control.
     *
     * @param zoomValues List of zoom values that need to displayed.
     */
    public void setZoomValues(List<ChartZoomValue> zoomValues) {
        this.zoomValues.clear();
        this.zoomValues.addAll(zoomValues);
    }

    /**
     * Sets the current zoom value to the chart control and update the data accordingly.
     *
     * @param value ChartZoomValue object.
     */
    public void setCurrentZoom(ChartZoomValue value) {
        zoomGroup.getToggles().stream()
                .map(t -> (ToggleButton)t)
                .filter(tb -> tb.getText().equals(value.getDisplayValue()))
                .findFirst().ifPresent(tb -> tb.setSelected(true));
        updateChart(value);
    }

    /**
     * Sets the legend to the chart.
     *
     * @param title Title of the chart.
     */
    public void setLegend(String title) {
        if (this.chartSeries != null) {
            this.chartSeries.setName(title);
        }
    }

    /**
     * Sets the label to the y-axis of the chart.
     *
     * @param label Label of the y-axis.
     */
    public void setChartYAxisLabel(String label) {
        if (lineChart != null) {
            lineChart.getYAxis().setLabel(label);
        }
    }

    /**
     * Sets/adds data to the chart.
     *
     * @param data Data object.
     */
    public void setData(List<T> data) {
        this.chartSeries.getData().clear();
        if (!data.isEmpty()) {
            List<T> sortedData = data.stream().sorted(Comparator.comparing(ChartData::getXValue)).collect(
                    Collectors.toList());
            dataUpperBound = sortedData.get(sortedData.size() - 1).getXValue();
            dataLowerBound = sortedData.get(0).getXValue();
            final List<XYChart.Data> seriesData = sortedData.stream()
                    .map(t -> new XYChart.Data(t.getXValue().getTime(), t.getYValue()))
                    .collect(Collectors.toList());
            this.chartSeries.getData().addAll(seriesData);
        }

        Date nxtUb = dataUpperBound;
        if (nxtUb == null) {
            nxtUb = new Date();
        }
        xAxis.setUpperBound(nxtUb.getTime());
        xAxis.setLowerBound(nxtUb.getTime() - currentZoomValue.getVisibleChartBound());
        xAxis.setTickUnit(currentZoomValue.getTickUnit());
        updateScrollBar();
        mask.setVisible(false);

        Platform.runLater(() -> {
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            final String toolTip = "Value : %s " + lineChart.getYAxis().getLabel() + "\nTime : %s Hrs\nDate : %s";
            lineChart.getData().stream().flatMap(s -> s.getData().stream())
                    .forEach(d -> {
                        Date date = new Date((long)d.getXValue());
                        String value = valueFormatter != null ? valueFormatter.toString(d.getYValue()) : d.getYValue() + "";
                        Tooltip.install(d.getNode(), new Tooltip(String.format(toolTip, value, timeFormat.format(date),
                                dateFormat.format(date))));
                        d.getNode().setOnMouseEntered(e -> d.getNode().getStyleClass().add("chartnode-hover"));
                        d.getNode().setOnMouseExited(e -> d.getNode().getStyleClass().remove("chartnode-hover"));
                    });
        });
    }

    public NumberAxis getyAxis() {
        return yAxis;
    }

    public void setValueFormatter(StringConverter<Number> valueFormatter) {
        this.valueFormatter = valueFormatter;
    }

}
