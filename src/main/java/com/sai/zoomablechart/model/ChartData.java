package com.sai.zoomablechart.model;

import java.util.Date;

/**
 * Data interface for the chart data.
 *
 * @author sai.dandem
 */
public interface ChartData<Y> {
    public Date getXValue();

    public Y getYValue();
}
