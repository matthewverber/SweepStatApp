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
    private ArrayList<DataPoint> forwardData = new ArrayList<>();
    private ArrayList<DataPoint> negatedForwardData = new ArrayList<>();
    private ArrayList<DataPoint> backwardData = new ArrayList<>();
    private ArrayList<DataPoint> negatedBackwardData = new ArrayList<>();
    private ArrayList<DataPoint> fullData = new ArrayList<>();
    private double offset = 0;
    private GraphView graph;
    private DataPoint dataPoint = null;
    private double highVolt, lowVolt;
    private boolean drawing;
    private boolean reversed;
    private ArrayBlockingQueue<DataPoint> dataPoints;
    private int scaleFactor = 12;
    private double maxY, minY;
    final static double THRESHOLD = 0.0001;

    public Graph(final GraphView graph){
        this.graph = graph;
        drawing = false;
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
            dataPoints = new ArrayBlockingQueue<>(10);
            GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitle("Voltage");
            gridLabel.setVerticalAxisTitle("Current");
            gridLabel.setNumVerticalLabels(6);
            gridLabel.setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        if (value == 0.0)
                            return super.formatLabel(value, isValueX);
                        return -1 * Double.parseDouble(super.formatLabel(value, isValueX)) + "";
                    } else {
                        if (value == 0.0)
                            return super.formatLabel(value, isValueX);
                        return String.format("%3.2E", value / Math.pow(10,scaleFactor));
                    }
                }
            });
        }
    }

    public Graph(GraphView graph, double lowVolt, double highVolt){
        this(graph);
        graph.getViewport().setMaxX(-1*lowVolt);
        graph.getViewport().setMinX(-1*highVolt);
        this.highVolt = highVolt;
        this.lowVolt = lowVolt;
        graphInRealTime();
    }

    public void drawOnFakeData (){
        if (drawing)
            return;
        drawing = true;
        offset = Math.random();
        backwardData = new ArrayList<>();
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
            dataPoints.clear();
        } else {
            return;
        }
        generateFakeData();
    }

    protected void generateFakeData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (double j = 1; j < 1.5; j+=0.1) {
                        for (double i = lowVolt; i <= highVolt; i += 0.01) {
                            putData(i, Math.sin(j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = highVolt; i >= lowVolt; i -= 0.01) {
                            putData(i, Math.sin(10 * j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = lowVolt; i <= highVolt; i += 0.01) {
                            putData(i, Math.sin(5 * j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = highVolt; i >= lowVolt; i -= 0.01) {
                            putData(i, Math.sin(10 * j * i + 1 + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                    }
                    drawing = false;
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    drawing = false;
                }

//                for (double i = highVolt; i >= lowVolt; i -= 0.01) {
//                    putData(i,Math.sin(i+offset));
//                }
//                for (double i = lowVolt; i <= highVolt; i += 0.01) {
//                    putData(i,Math.sin(10*i+offset));
//                }
//                drawing = false;
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
                    }
                }
            }
        }).start();
    }

    protected void addEntry(DataPoint data){
//        if (!drawing)
//            return;
        this.dataPoint = data;
        fullData.add(data);

        if(Math.abs(dataPoint.getX() - highVolt) < THRESHOLD) {
            reversed = true;
            backwardData = new ArrayList<>();
            negatedBackwardData = new ArrayList<>();
            backwardSeries = new LineGraphSeries<>();
        }
        if (Math.abs(dataPoint.getX() - lowVolt) < THRESHOLD) {
            reversed = false;
            forwardData = new ArrayList<>();
            negatedForwardData = new ArrayList<>();
            forwardSeries = new LineGraphSeries<>();
        }
        if (reversed){
            backwardData.add(dataPoint);
            dataPoint = new DataPoint(-1*dataPoint.getX(), dataPoint.getY()*Math.pow(10,scaleFactor));
            negatedBackwardData.add(dataPoint);
            if (dataPoint.getY() > maxY){
                maxY = dataPoint.getY();
                graph.getViewport().setMaxY(maxY);
            }
            if (dataPoint.getY() < minY){
                minY = dataPoint.getY();
                graph.getViewport().setMinY(minY);
            }
            if (backwardSeries != null) {
                graph.post(new Runnable(){
                    @Override
                    public void run(){
                        graph.removeSeries(backwardSeries);
                        try {
                            backwardSeries = new LineGraphSeries<>(negatedBackwardData.toArray(new DataPoint[0]).clone());
                        }catch (IllegalArgumentException e){}
                        graph.addSeries(backwardSeries);
                    }
                });
            }
        } else {
            forwardData.add(dataPoint);
            dataPoint = new DataPoint(-1*dataPoint.getX(), dataPoint.getY()*Math.pow(10,scaleFactor));
            negatedForwardData.add(0, dataPoint);
            if (dataPoint.getY() > maxY){
                maxY = dataPoint.getY();
                graph.getViewport().setMaxY(maxY);
            }
            if (dataPoint.getY() < minY){
                minY = dataPoint.getY();
                graph.getViewport().setMinY(minY);
            }
            if (forwardSeries != null) {
                graph.post(new Runnable(){
                    @Override
                    public void run(){
                        graph.removeSeries(forwardSeries);
                        try {
                            forwardSeries = new LineGraphSeries<>(negatedForwardData.toArray(new DataPoint[0]).clone());
                        } catch (IllegalArgumentException e){}
                        graph.addSeries(forwardSeries);
                    }
                });
            }
        }
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
