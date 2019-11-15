package com.example.sweepstatapp;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class Graph {
    private LineGraphSeries<DataPoint> forwardSeries;
    private LineGraphSeries<DataPoint> backwardSeries;
    private int numberOfPoints = 100;
    private ArrayList<DataPoint> forwardData = new ArrayList<>();
    private ArrayList<DataPoint> negatedForwardData = new ArrayList<>();
    private ArrayList<DataPoint> backwardData = new ArrayList<>();
    private ArrayList<DataPoint> fullData = new ArrayList<>();
    private int x = 0;
    private double offset = 0;
    private long interval;
    private GraphView graph;
    private Viewport viewport;
    private DataPoint dataPoint = null;
    private double largestX;

    public Graph(GraphView graph, Viewport viewport, long interval){
        this.graph = graph;
        this.viewport = viewport;
        this.interval = interval;

        if (graph != null){
            forwardSeries = new LineGraphSeries<>();
            backwardSeries = new LineGraphSeries<>();
            largestX = 0;
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Voltage");
            gridLabel.setVerticalAxisTitle("Current");
            gridLabel.setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        if (value == 0.0)
                            return super.formatLabel(value, isValueX);
                        return -1 * Double.parseDouble(super.formatLabel(value, isValueX)) + "";
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
        }
    }

    public Graph(GraphView graph, Viewport viewport, long interval, double lowVolt, double highVolt){
        this(graph, viewport, interval);
        viewport.setMaxX(-1*lowVolt);
        viewport.setMinX(-1*highVolt);
        largestX = lowVolt;

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
                forwardData = new ArrayList<>();
                negatedForwardData = new ArrayList<>();
                backwardData = new ArrayList<>();
                graph.removeAllSeries();
                graph.addSeries(forwardSeries);
                graph.addSeries(backwardSeries);
//                viewport.setMaxX(numberOfPoints*0.1);
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
//        if(dataPoint.getX() < largestX) {
//            backwardData.add(0, dataPoint);
//            if (backwardSeries != null) {
//                backwardSeries.resetData(backwardData.toArray(new DataPoint[0]));
//            }
//        } else {
//            largestX = dataPoint.getX();
//            forwardData.add(dataPoint);
//            if (forwardSeries != null) {
//                forwardSeries.appendData(dataPoint, false, numberOfPoints);
//            }
//        }
        if(-1*dataPoint.getX() < largestX) {
            backwardData.add(0, dataPoint);
            if (backwardSeries != null) {
                backwardSeries.appendData(new DataPoint(-1*dataPoint.getX(), dataPoint.getY()), false, numberOfPoints);
            }
        } else {
//            largestX = dataPoint.getX();
            forwardData.add(dataPoint);
            negatedForwardData.add(new DataPoint(-1*dataPoint.getX(), dataPoint.getY()));
            if (forwardSeries != null) {
                forwardSeries.resetData(negatedForwardData.toArray(new DataPoint[0]));
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
