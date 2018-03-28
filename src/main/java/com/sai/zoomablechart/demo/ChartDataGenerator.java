/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.demo;

import com.sai.zoomablechart.model.ChartZoomValue;
import com.sai.zoomablechart.model.VitalSign;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.time.DateUtils;

/**
 * Data generator for the chart demo.
 *
 * @author sai.dandem
 */
public class ChartDataGenerator {

    private final List<VitalSign> hearBeatDataStore = new ArrayList<>();

    private final List<VitalSign> temperatureDataStore = new ArrayList<>();

    public ChartDataGenerator() {
        Date endDate = new Date();
        Date startDate = DateUtils.addMonths(endDate, -3);
        hearBeatDataStore.addAll(buildHeartBeatData(startDate, endDate));
        System.out.println("Total heart beat data count :: " + hearBeatDataStore.size());

        Date tStartDate = DateUtils.addMonths(endDate, -6);
        temperatureDataStore.addAll(buildTemperatureData(tStartDate, endDate));
        System.out.println("Total temperature data count :: " + temperatureDataStore.size());
    }

    public List<ChartZoomValue> getHeartBeatZoomValues() {
        List<ChartZoomValue> list = new ArrayList<>();
        double minute = 60 * 1000;
        double hour = 60 * minute;
        list.add(new ChartZoomValue(1, ChronoUnit.HOURS, (10 * minute), 6));
        list.add(new ChartZoomValue(6, ChronoUnit.HOURS, (30 * minute), 12));
        list.add(new ChartZoomValue(12, ChronoUnit.HOURS, (1 * hour), 12));
        list.add(new ChartZoomValue(24, ChronoUnit.HOURS, (2 * hour), 12));
        list.add(new ChartZoomValue(72, ChronoUnit.HOURS, (6 * hour), 12));
        list.add(new ChartZoomValue(1, ChronoUnit.WEEKS, (24 * hour), 7));
        return list;
    }

    public List<ChartZoomValue> getTemperatureZoomValues() {
        List<ChartZoomValue> list = new ArrayList<>();
        double minute = 60 * 1000;
        double hour = 60 * minute;
        double day = 24 * hour;
        list.add(new ChartZoomValue(12, ChronoUnit.HOURS, (1 * hour), 12));
        list.add(new ChartZoomValue(24, ChronoUnit.HOURS, (2 * hour), 12));
        list.add(new ChartZoomValue(72, ChronoUnit.HOURS, (6 * hour), 12));
        list.add(new ChartZoomValue(1, ChronoUnit.WEEKS, (1 * day), 7));
        list.add(new ChartZoomValue(2, ChronoUnit.WEEKS, (2 * day), 7));
        list.add(new ChartZoomValue(1, ChronoUnit.MONTHS, (3 * day), 10));
        return list;
    }

    private List<VitalSign> buildHeartBeatData(Date strt, Date end) {
        List<VitalSign> list = new ArrayList<>();
        Date temp = null;
        while (temp == null || temp.after(strt)) {
            temp = (temp == null) ? end : DateUtils.addHours(temp, getRandomIntInRange(1, 6) * -1);
            list.add(new VitalSign(getRandomIntInRange(40, 100), temp));

        }
        return list;
    }

    private List<VitalSign> buildTemperatureData(Date strt, Date end) {
        List<VitalSign> list = new ArrayList<>();
        Date temp = null;
        while (temp == null || temp.after(strt)) {
            temp = (temp == null) ? end : DateUtils.addHours(temp, getRandomIntInRange(1, 12) * -1);
            list.add(new VitalSign(getRandomDoubleInRange(35, 43), temp));

        }
        return list;
    }

    private int getRandomIntInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static double getRandomDoubleInRange(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return Double.valueOf(new DecimalFormat("#.#").format(min + (r.nextDouble() * (max - min))));
    }

    public List<VitalSign> getHeartBeatData() {
        return hearBeatDataStore;
    }
    
    public List<VitalSign> getTemperatureData() {
        return temperatureDataStore;
    }
}
