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
    private ArrayList<DataPoint> negatedBackwardData = new ArrayList<>();
    private ArrayList<DataPoint> fullData = new ArrayList<>();
//    private int x = 0;
    private double offset = 0;
    private long interval;
    private GraphView graph;
    private Viewport viewport;
    private DataPoint dataPoint = null;
    private double largestX, highVolt, lowVolt;
    private boolean drawing;
    private boolean reversed;

    public Graph(GraphView graph, Viewport viewport, long interval){
        this.graph = graph;
        this.viewport = viewport;
        this.interval = interval;
        drawing = false;
        reversed = false;
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
        this.highVolt = highVolt;
        this.lowVolt = lowVolt;
    }

    public void drawOnFakeData (){
            offset = Math.random();
            numberOfPoints = (int)((highVolt - lowVolt) / 0.01)+1;
            backwardData = new ArrayList<>();
            largestX = 0;
            if (graph != null){
                forwardSeries = new LineGraphSeries<>();
                backwardSeries = new LineGraphSeries<>();
                forwardData = new ArrayList<>();
                negatedForwardData = new ArrayList<>();
                negatedBackwardData = new ArrayList<>();
                backwardData = new ArrayList<>();
                graph.removeAllSeries();
                graph.addSeries(forwardSeries);
                graph.addSeries(backwardSeries);
            }
            graphInRealTime();
    }

    protected DataPoint generateFakeDataPoint(double x) {
        return new DataPoint(x,Math.sin(x+offset));
    }

    protected DataPoint generateFakeDataPointReverse(double x){
        return new DataPoint(x,-1*Math.sin(x+offset));
    }

    protected void graphInRealTime(){
        if (drawing)
            return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    drawing = true;
                    reversed = false;
                    for (double i = highVolt; i >= lowVolt; i -= 0.01) {
                        addEntry(generateFakeDataPoint(i));
                        Thread.sleep(interval);
                    }
                    for (double i = lowVolt; i <= highVolt; i += 0.01) {
                        addEntry(generateFakeDataPointReverse(i));
                        Thread.sleep(interval);
                    }
                    drawing = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    drawing = false;
                }
            }
        }).start();
    }

    public void addEntry(DataPoint data){
        this.dataPoint = data;
        fullData.add(data);

        if(dataPoint.getX() == largestX)
            reversed = true;
        if (reversed){
            backwardData.add(dataPoint);
            negatedBackwardData.add(0,new DataPoint(-1*dataPoint.getX(), dataPoint.getY()));
            if (backwardSeries != null) {
//                graph.removeSeries(backwardSeries);
//                backwardSeries = new LineGraphSeries<>(negatedBackwardData.toArray(new DataPoint[0]));
//                graph.addSeries(backwardSeries);
                backwardSeries.resetData(negatedForwardData.toArray(new DataPoint[0]));
            }
        } else {
            forwardData.add(dataPoint);
            negatedForwardData.add(new DataPoint(-1*dataPoint.getX(), dataPoint.getY()));
            if (forwardSeries != null) {
//                graph.removeSeries(forwardSeries);
//                forwardSeries = new LineGraphSeries<>(negatedForwardData.toArray(new DataPoint[0]));
//                graph.addSeries(forwardSeries);
                forwardSeries.appendData(new DataPoint(-1*dataPoint.getX(), dataPoint.getY()), false, numberOfPoints);
            }
        }
    }

    public ArrayList<DataPoint> getForwardData(){
        return forwardData;
    }

    public ArrayList<DataPoint> getBackwardData(){
//        ArrayList<DataPoint> reversedData = new ArrayList<>();
//        for (int i = backwardData.size()-1; i >= 0; i++)
//            reversedData.add(backwardData.get(i));
//        return reversedData;
        return backwardData;
    }

    public ArrayList<DataPoint> getFullData(){
        return fullData;
    }

    public double getOffset(){
        return  offset;
    }
}
