package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.os.Bundle;
import com.jjoe64.graphview.GraphView;
import android.view.View;
import java.util.ArrayList;


public class graph extends AppCompatActivity {
    private LineGraphSeries<DataPoint> forwardSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> backwardSeries = new LineGraphSeries<>();
    private int numberOfPoints = 100;
    private ArrayList<DataPoint> backwardData = new ArrayList<>();
    private int x = 0;
    private double offset = 0;
    private long interval = 50;
    private GraphView graph = null;
    private Viewport viewport = null;
    private DataPoint dataPoint = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = findViewById(R.id.graph);
        viewport = graph.getViewport();
//        viewport.setScalable(true);
//        viewport.setScrollable(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(-1);
        viewport.setMaxY(1);
        viewport.setMinX(0);
    }

    public void onClick (View view){
        if (view.getId() == R.id.run){
//            range = 10;
//            numberOfPoints = 500;
            offset = Math.random();
            x = 0;
            viewport.setMaxX(numberOfPoints*0.1);
            graph.removeAllSeries();
            generateFakeData();
            graph.addSeries(forwardSeries);
        }
        if (view.getId() == R.id.realTime){
            offset = Math.random();
            x = 0;
//            numberOfPoints = 500;
//            interval = 100;
            graph.removeAllSeries();
            forwardSeries = new LineGraphSeries<>();
            backwardData = new ArrayList<>();
            backwardSeries = new LineGraphSeries<>();
            graph.addSeries(forwardSeries);
            graph.addSeries(backwardSeries);
            viewport.setMaxX(numberOfPoints*0.1);
            graphInRealTime();
        }
    }

    protected DataPoint generateFakeDataPoint() {
        return new DataPoint(0.1*x,Math.sin(0.1*x+offset));
    }

    protected DataPoint generateFakeDataPointReverse(){
        return new DataPoint(0.1*x,-1*Math.sin(0.1*x));
    }

    protected void generateFakeData(){
        DataPoint[] fakeData = new DataPoint[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++, x++){
            fakeData[i] = generateFakeDataPoint();
        }
        forwardSeries = new LineGraphSeries<>(fakeData);
    }

    protected void graphInRealTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    x = 0;
                    Thread.sleep(100);
                    x = 0;
                    for (int i = 0; i < numberOfPoints; i++, x++) {
                        if (i != x) {
                            return;
                        }
                        addEntry(generateFakeDataPoint());
                        Thread.sleep(interval);
                    }
                    x = numberOfPoints - 2;
                    for (int i = x; i >= 0; i--, x--) {
                        if (i != x) {
                            return;
                        }
                        addEntry(generateFakeDataPointReverse());
                        Thread.sleep(interval);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void addEntry(DataPoint data){
        this.dataPoint = data;
        if(dataPoint.getX() < forwardSeries.getHighestValueX()) {
            backwardData.add(0,dataPoint);
            backwardSeries.resetData(backwardData.toArray(new DataPoint[0]));
        } else {
            forwardSeries.appendData(dataPoint, false, numberOfPoints);
        }
    }
}