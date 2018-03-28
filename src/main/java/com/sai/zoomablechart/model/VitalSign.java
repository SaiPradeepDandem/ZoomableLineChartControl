/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.model;

import java.util.Date;
import java.util.Objects;

/**
 * Data object for vital sign.
 *
 * @author sai.dandem
 */
public class VitalSign implements ChartData<Number> {

    private double value;

    private Date time;

    public VitalSign(double value, Date time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public Date getXValue() {
        return time;
    }

    @Override
    public Number getYValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + (int)(Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        hash = 17 * hash + Objects.hashCode(this.time);
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
        final VitalSign other = (VitalSign)obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (!Objects.equals(this.time, other.time)) {
            return false;
        }
        return true;
    }

}
