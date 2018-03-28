/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.model;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

/**
 * Zoom value in the chart.
 *
 * @author sai.dandem
 */
public class ChartZoomValue {

    private TimeRange range;

    private double tickUnit;

    private int tickCount;

    public ChartZoomValue(int number, ChronoUnit unit, double tickUnit, int tickCount) {
        this.range = new TimeRange(number, unit);
        this.tickUnit = tickUnit;
        this.tickCount = tickCount;
    }

    /**
     * Returns the start date time value to the provided end time with respect to the range details of the class.
     *
     * @param endTime End date of the range.
     * @return Start date of the range.
     */
    public Date getRangeStartValue(Date endTime) {
        return range.getRangeStartValue(endTime);
    }

    /**
     * Returns the end date time value to the provided start time with respect to the range details of the class.
     *
     * @param startTime Start date of the range.
     * @return End date of the range.
     */
    public Date getRangeEndValue(Date startTime) {
        return range.getRangeEndValue(startTime);
    }

    public TimeRange getRange() {
        return range;
    }

    public String getDisplayValue() {
        return range.getDisplayValue();
    }

    public double getTickUnit() {
        return tickUnit;
    }

    public int getTickCount() {
        return tickCount;
    }

    public double getVisibleChartBound() {
        return getTickUnit() * getTickCount();
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.range);
        hash = 13 * hash + (int)(Double.doubleToLongBits(this.tickUnit) ^ (Double.doubleToLongBits(this.tickUnit) >>> 32));
        hash = 13 * hash + this.tickCount;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChartZoomValue other = (ChartZoomValue)obj;
        if (Double.doubleToLongBits(this.tickUnit) != Double.doubleToLongBits(other.tickUnit)) {
            return false;
        }
        if (this.tickCount != other.tickCount) {
            return false;
        }
        if (!Objects.equals(this.range, other.range)) {
            return false;
        }
        return true;
    }

}
