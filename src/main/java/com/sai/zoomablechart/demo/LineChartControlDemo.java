/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sai.zoomablechart.demo;

import com.sai.zoomablechart.control.ZoomableChartControl;
import com.sai.zoomablechart.model.ChartZoomValue;
import com.sai.zoomablechart.model.VitalSign;
import java.time.temporal.ChronoUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Demo application for the line chart control.
 *
 * @author sai.dandem
 */
public class LineChartControlDemo extends Application {
    private ChartDataGenerator data;

    private ZoomableChartControl<VitalSign> heartBeatChart;

    private ZoomableChartControl<VitalSign> temperatureChart;

    @Override
    public void start(Stage primaryStage) throws Exception {
        data = new ChartDataGenerator();

        VBox root = new VBox();

        StackPane headingPane = new StackPane();
        headingPane.setMinWidth(1200);
        headingPane.setStyle("-fx-background-color:#3B6D99;");
        headingPane.setPadding(new Insets(10));
        Label heading = new Label("Zoomable Line Chart Demo");
        heading.setStyle("-fx-font-size:32px;-fx-text-fill:#F5A833;-fx-font-weight:bold;-fx-font-family:Arial;");
        headingPane.getChildren().add(heading);

        heartBeatChart = new ZoomableChartControl<>();
        heartBeatChart.setPrefHeight(350);
        heartBeatChart.setZoomValues(data.getHeartBeatZoomValues());
        heartBeatChart.setChartYAxisLabel("per min");
        heartBeatChart.setLegend("Heart Beat");
        heartBeatChart.setValueFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return object.intValue()+"";
            }

            @Override
            public Number fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        VBox.setMargin(heartBeatChart, new Insets(10));

        temperatureChart = new ZoomableChartControl<>(34, 44);
        temperatureChart.setPrefHeight(350);
        temperatureChart.setZoomValues(data.getTemperatureZoomValues());
        temperatureChart.setChartYAxisLabel("°C");
        temperatureChart.setLegend("Temperature");
        VBox.setMargin(temperatureChart, new Insets(10));

        Label title1 = new Label("Sample Chart #1 (Patient HeartBeat Chart)");
        title1.setStyle("-fx-font-size:16px;");
        title1.setPadding(new Insets(20, 0, 0, 10));

        Label title2 = new Label("Sample Chart #2 (Patient Temperature Chart)");
        title2.setStyle("-fx-font-size:16px;");
        title2.setPadding(new Insets(30, 0, 0, 10));

        VBox content = new VBox();
        content.getChildren().addAll(title1, heartBeatChart, title2, temperatureChart);

        ScrollPane scroll = new ScrollPane();
        scroll.setFocusTraversable(false);
        scroll.setContent(content);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        root.getChildren().addAll(headingPane, scroll);
        Scene scene = new Scene(root, Color.valueOf("#F7F8FA"));
        scene.getStylesheets().add(LineChartControlDemo.class.getResource("/linechart.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Line Chart Demo");
        primaryStage.setMaximized(true);
        primaryStage.show();

        heartBeatChart.setCurrentZoom(new ChartZoomValue(6, ChronoUnit.HOURS, (30 * 60 * 1000), 12));
        temperatureChart.setCurrentZoom(new ChartZoomValue(24, ChronoUnit.HOURS, (60 * 60 * 1000), 12));
        generateData();
    }

    private void generateData() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {

            }
            Platform.runLater(() -> heartBeatChart.setData(data.getHeartBeatData()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {

            }
            Platform.runLater(() -> temperatureChart.setData(data.getTemperatureData()));
        });
        t.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
