package com.sai.zoomablechart.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.ValueAxis;

/**
 * Customized Time axis for the line chart.
 *
 * @author sai.dandem
 */
public class TimeAxis extends ValueAxis<Number> {
    private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm");

    private final SimpleObjectProperty<Double> tickUnitProperty = new SimpleObjectProperty<>();

    public TimeAxis(String axisLabel, double lowerBound, double upperBound, double tickUnit) {
        super(lowerBound, upperBound);
        setTickUnit(tickUnit);
        setLabel(axisLabel);
    }

    @Override
    protected List<Number> calculateMinorTickMarks() {
        List<Number> ticks = new ArrayList<>();
        double tickUnit = tickUnitProperty.get() / getMinorTickCount();
        double start = Math.floor(getLowerBound() / tickUnit) * tickUnit;
        for (double value = start; value < getUpperBound(); value += tickUnit) {
            ticks.add(value);
        }
        return ticks;
    }

    @Override
    protected List<Number> calculateTickValues(double arg0, Object arg1) {
        List<Number> ticks = new ArrayList<>();
        double tickUnit = tickUnitProperty.get();
        double start = Math.floor(getLowerBound() / tickUnit) * tickUnit;
        for (double value = start; value < getUpperBound(); value += tickUnit) {
            ticks.add(value);
        }
        return ticks;
    }

    @Override
    protected String getTickMarkLabel(Number label) {
        return format.format(new Date(label.longValue()));
    }

    public SimpleObjectProperty<Double> getTickUnitProperty() {
        return tickUnitProperty;
    }

    public void setTickUnit(double tickUnit) {
        tickUnitProperty.set(tickUnit);
    }

    @Override
    protected void setRange(Object range, boolean animate) {

    }

    @Override
    protected Object getRange() {
        return null;
    }

}
