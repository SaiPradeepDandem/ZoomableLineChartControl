/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.model;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Time range data object.
 *
 * @author sai.dandem
 */
public class TimeRange {
    private final int number;

    private final ChronoUnit unit;

    public TimeRange(int number, ChronoUnit unit) {
        this.number = number;
        this.unit = unit;
    }

    public String getDesc() {
        switch (unit) {
            case HOURS:
                return "hrs";
            case DAYS:
                return "days";
            case WEEKS:
                return "week";
            case MONTHS:
                return "month";
            case YEARS:
                return "year";
            default:
                return "";
        }
    }

    /**
     * Returns the start date time value to the provided end time with respect to the range details of the class.
     *
     * @param endTime End date of the range.
     * @return Start date of the range.
     */
    public Date getRangeStartValue(Date endTime) {
        if (endTime != null) {
            switch (unit) {
                case HOURS:
                    return DateUtils.addHours(endTime, number * -1);
                case WEEKS:
                    return DateUtils.addWeeks(endTime, number * -1);
                case MONTHS:
                    return DateUtils.addMonths(endTime, number * -1);
                case YEARS:
                    return DateUtils.addYears(endTime, number * -1);
                default:
                    throw new AssertionError(unit.name());
            }
        }
        return null;
    }

    /**
     * Returns the start date time value to the provided end time with respect to the range details of the class.
     *
     * @param end   End date of the range.
     * @param range
     * @return Start date of the range.
     */
    public static Date getRangeStartDate(Date end, TimeRange range) {
        if (end != null) {
            switch (range.unit) {
                case HOURS:
                    return DateUtils.addHours(end, range.number * -1);
                case WEEKS:
                    return DateUtils.addWeeks(end, range.number * -1);
                case MONTHS:
                    return DateUtils.addMonths(end, range.number * -1);
                case YEARS:
                    return DateUtils.addYears(end, range.number * -1);
                default:
                    throw new AssertionError(range.unit.name());
            }
        }
        return null;
    }

    /**
     * Returns the end date time value to the provided start time with respect to the range details of the class.
     *
     * @param startTime Start date of the range.
     * @return End date of the range.
     */
    public Date getRangeEndValue(Date startTime) {
        if (startTime != null) {
            switch (unit) {
                case HOURS:
                    return DateUtils.addHours(startTime, number);
                case WEEKS:
                    return DateUtils.addWeeks(startTime, number);
                case MONTHS:
                    return DateUtils.addMonths(startTime, number);
                case YEARS:
                    return DateUtils.addYears(startTime, number);
                default:
                    throw new AssertionError(unit.name());
            }
        }
        return null;
    }

    public String getDisplayValue() {
        return number + " " + getDesc();
    }

    public int getNumber() {
        return number;
    }

    public ChronoUnit getUnit() {
        return unit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.number;
        hash = 89 * hash + Objects.hashCode(this.unit);
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
        final TimeRange other = (TimeRange)obj;
        if (this.number != other.number) {
            return false;
        }
        if (this.unit != other.unit) {
            return false;
        }
        return true;
    }

    

}
