package com.example.sweepstatapp;

import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GraphView;
import java.util.ArrayList;


public class Graph {
    private LineGraphSeries<DataPoint> forwardSeries;
    private LineGraphSeries<DataPoint> backwardSeries;
    private int numberOfPoints = 100;
    private ArrayList<DataPoint> forwardData = new ArrayList<>();
    private ArrayList<DataPoint> backwardData = new ArrayList<>();
    private ArrayList<DataPoint> fullData = new ArrayList<>();
    private int x = 0;
    private double offset = 0;
    private long interval;
    private GraphView graph;
    private Viewport viewport;
    private DataPoint dataPoint = null;
    private int largestX;

    public Graph(GraphView graph, Viewport viewport, long interval){
        this.graph = graph;
        this.viewport = viewport;
        this.interval = interval;
        if (graph != null){
            forwardSeries = new LineGraphSeries<>();
            backwardSeries = new LineGraphSeries<>();
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Voltage");
            gridLabel.setVerticalAxisTitle("Current");
        }
    }

    public void drawOnFakeData (int numOfPoints){
            offset = Math.random();
            x = 0;
            numberOfPoints = numOfPoints;
            backwardData = new ArrayList<>();
            largestX = 0;
            if (graph != null){
                forwardSeries = new LineGraphSeries<>();
                backwardSeries = new LineGraphSeries<>();
                graph.removeAllSeries();
                graph.addSeries(forwardSeries);
                graph.addSeries(backwardSeries);
                viewport.setMaxX(numberOfPoints*0.1);
            }
            graphInRealTime();
    }

    protected DataPoint generateFakeDataPoint(int x) {
        return new DataPoint(0.1*x,Math.sin(0.1*x+offset));
    }

    protected DataPoint generateFakeDataPointReverse(int x){
        return new DataPoint(0.1*x,-1*Math.sin(0.1*x+offset));
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
                        addEntry(generateFakeDataPoint(x));
                        Thread.sleep(interval);
                    }
                    x = numberOfPoints - 2;
                    for (int i = x; i >= 0; i--, x--) {
                        if (i != x) {
                            return;
                        }
                        addEntry(generateFakeDataPointReverse(x));
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
        fullData.add(data);
        if(dataPoint.getX() < largestX) {
            backwardData.add(0,dataPoint);
            if (backwardSeries != null){
                backwardSeries.resetData(backwardData.toArray(new DataPoint[0]));
            }
        } else {
            largestX = x;
            forwardData.add(dataPoint);
            if (forwardSeries != null){
                forwardSeries.appendData(dataPoint, false, numberOfPoints);
            }
        }
    }

    public ArrayList<DataPoint> getForwardData(){
        return forwardData;
    }

    public ArrayList<DataPoint> getBackwardData(){
        ArrayList<DataPoint> reversedData = new ArrayList<>();
        for (int i = backwardData.size()-1; i >= 0; i++)
            reversedData.add(backwardData.get(i));
        return reversedData;
    }

    public ArrayList<DataPoint> getFullData(){
        return fullData;
    }

    public double getOffset(){
        return  offset;
    }
}