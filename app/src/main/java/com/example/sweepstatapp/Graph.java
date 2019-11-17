package com.example.sweepstatapp;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;


public class Graph {
    private LineGraphSeries<DataPoint> forwardSeries;
    private LineGraphSeries<DataPoint> backwardSeries;
    private int numberOfPoints = 100;
    private ArrayList<DataPoint> forwardData = new ArrayList<>();
    private ArrayList<DataPoint> negatedForwardData = new ArrayList<>();
    private ArrayList<DataPoint> backwardData = new ArrayList<>();
    private ArrayList<DataPoint> negatedBackwardData = new ArrayList<>();
    private ArrayList<DataPoint> fullData = new ArrayList<>();
    private double offset = 0;
    private GraphView graph;
    private DataPoint dataPoint = null;
    private double largestX, highVolt, lowVolt;
    private boolean drawing;
    private boolean reversed;
    private ArrayBlockingQueue<DataPoint> dataPoints;
//    private final long TIMEOUT = 5;
//    private boolean endOfInput = false;

    public Graph(GraphView graph){
        this.graph = graph;

//        drawing = false;
        reversed = false;
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
            dataPoints = new ArrayBlockingQueue<>(1024);
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

    public Graph(GraphView graph, double lowVolt, double highVolt){
        this(graph);
        graph.getViewport().setMaxX(-1*lowVolt);
        graph.getViewport().setMinX(-1*highVolt);
        largestX = lowVolt;
        this.highVolt = highVolt;
        this.lowVolt = lowVolt;
//        graphInRealTime();
    }

    public void drawOnFakeData (){
        if (drawing)
            return;
        drawing = true;
        offset = Math.random();
        numberOfPoints = (int)((highVolt - lowVolt) / 0.01)+1;
        backwardData = new ArrayList<>();
        largestX = 0;
        reversed = false;
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
            dataPoints = new ArrayBlockingQueue<>(1024);
        } else {
            return;
        }
        generateFakeData();
        graphInRealTime();
    }

    protected void generateFakeData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (double i = highVolt; i >= lowVolt; i -= 0.01) {
                        putData(i,Math.sin(i+offset));
                        Thread.sleep(20);
                    }
                    for (double i = lowVolt; i <= highVolt; i += 0.01) {
                        putData(i,Math.sin(10*i+offset));
                        Thread.sleep(20);
                    }
                    drawing = false;
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    drawing = false;
                }

            }
        }).start();
    }

    protected void graphInRealTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    while(true){
                        try {
                            DataPoint dp = dataPoints.take();
                            if (dp != null){
                                addEntry(dp);
                            }
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        } finally {
                            continue;
                        }
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
                graph.post(new Runnable(){
                    @Override
                    public void run(){
                        graph.removeSeries(backwardSeries);
                        backwardSeries = new LineGraphSeries<>(negatedBackwardData.toArray(new DataPoint[0]).clone());
                        graph.addSeries(backwardSeries);
                    }
                });
            }
        } else {
            forwardData.add(dataPoint);
            negatedForwardData.add(new DataPoint(-1*dataPoint.getX(), dataPoint.getY()));
            if (forwardSeries != null) {
                graph.post(new Runnable(){
                    @Override
                    public void run(){
                        graph.removeSeries(forwardSeries);
                        forwardSeries = new LineGraphSeries<>(negatedForwardData.toArray(new DataPoint[0]).clone());
                        graph.addSeries(forwardSeries);
                    }
                });
            }
        }
    }

    public ArrayList<DataPoint> getForwardData(){
        return forwardData;
    }

    public ArrayList<DataPoint> getBackwardData(){
        return backwardData;
    }

    public ArrayList<DataPoint> getFullData(){
        return fullData;
    }

    public double getOffset(){
        return  offset;
    }

    public void putData(double x, double y){
        try {
            dataPoints.put(new DataPoint(x,y));
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
