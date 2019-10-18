package com.example.sweepstatapp;

import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GraphView;
import android.view.View;
import java.util.ArrayList;


public class Graph {
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

    public Graph(GraphView graph, Viewport viewport){
        this.graph = graph;
        this.viewport = viewport;
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Voltage");
        gridLabel.setVerticalAxisTitle("Current");
    }

    public void drawOnFakeData (int numOfPoints){
            offset = Math.random();
            x = 0;
            numberOfPoints = numOfPoints;
            graph.removeAllSeries();
            forwardSeries = new LineGraphSeries<>();
            backwardData = new ArrayList<>();
            backwardSeries = new LineGraphSeries<>();
            graph.addSeries(forwardSeries);
            graph.addSeries(backwardSeries);
            viewport.setMaxX(numberOfPoints*0.1);
            graphInRealTime();
//        }
    }

    protected DataPoint generateFakeDataPoint() {
        return new DataPoint(0.1*x,Math.sin(0.1*x+offset));
    }

    protected DataPoint generateFakeDataPointReverse(){
        return new DataPoint(0.1*x,-1*Math.sin(0.1*x));
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