package com.example.sweepstatapp;

import android.util.Log;

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
    private double offset = 12;
    private GraphView graph;
    private DataPoint dataPoint = null;
    private double highVolt, lowVolt;
    private boolean drawing;
    private boolean reversed;
    private ArrayBlockingQueue<DataPoint> dataPoints;
    private int scaleFactor = -1000;
    private double maxY, minY, maxX, minX;
    private double lastX = -1000;

    public Graph(final GraphView graph){
        this.graph = graph;
        drawing = false;
        reversed = false;
        highVolt = -1000;
        lowVolt = -1000;
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
            gridLabel.setNumVerticalLabels(10);
            gridLabel.setNumHorizontalLabels(5);
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
        graphInRealTime();
    }

    public void setHighVolt(double highVolt){
        this.highVolt = highVolt;
    }

    public void setLowVolt(double lowVolt){
        this.lowVolt = lowVolt;
    }

    public boolean startDrawing(){
        if (drawing)
            return false;
        drawing = true;
        lastX = -1000;
        scaleFactor = -1000;
        maxX = -1000;
        minX = -1000;
        maxY = -1000;
        minY = -1000;
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
            return true;
        } else {
            return false;
        }
    }

    public void finishDrawing(){
        drawing = false;
    }

    public boolean getDrawing(){
        return drawing;
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
                        for (double i = lowVolt; i < highVolt; i += 0.01) {
                            putData(i, Math.sin(j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = highVolt-0.3; i > lowVolt; i -= 0.01) {
                            putData(i, Math.sin(10 * j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = lowVolt-0.1; i < highVolt; i += 0.01) {
                            putData(i, Math.sin(5 * j * i + offset) / Math.pow(10, 3));
                            Thread.sleep(5);
                        }
                        for (double i = highVolt-0.2; i > lowVolt; i -= 0.01) {
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
        Thread graphThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DataPoint dp = dataPoints.take();
                        if (dp != null){
                            Log.d("GRAPH", "Datapoint received: " + dp);
                            addEntry(dp);
                        }
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        graphThread.setName("GraphThread");
        graphThread.start();
    }

    protected void addEntry(DataPoint data){
        this.dataPoint = data;
        fullData.add(data);
        if (scaleFactor == -1000){
            scaleFactor = (int)Math.log(1/dataPoint.getY());
            if (scaleFactor > 12)
                scaleFactor = 12;
        }
        if (highVolt == -1000 || lowVolt == -1000) {
            if (maxX == -1000) {
                maxX = dataPoint.getX();
                minX = dataPoint.getX();
            }
            if (dataPoint.getX() > maxX) {
                maxX = dataPoint.getX();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(-1 * minX + 0.1);
                graph.getViewport().setMinX(-1 * maxX - 0.1);
            }
            if (dataPoint.getX() < minX) {
                minX = dataPoint.getX();
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMaxX(-1 * minX + 0.1);
                graph.getViewport().setMinX(-1 * maxX - 0.1);
            }
        }

        if(!reversed && dataPoint.getX() - lastX < 0) {
            reversed = true;
            backwardData = new ArrayList<>();
            negatedBackwardData = new ArrayList<>();
            backwardSeries = new LineGraphSeries<>();
        } else if (reversed && dataPoint.getX() - lastX > 0) {
            reversed = false;
            forwardData = new ArrayList<>();
            negatedForwardData = new ArrayList<>();
            forwardSeries = new LineGraphSeries<>();
        }
        lastX = dataPoint.getX();
        if (reversed){
            backwardData.add(dataPoint);
            dataPoint = new DataPoint(-1*dataPoint.getX(), dataPoint.getY()*Math.pow(10,scaleFactor));
            negatedBackwardData.add(dataPoint);
            if (dataPoint.getY() > maxY){
                maxY = dataPoint.getY();
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMaxY(maxY);
                graph.getViewport().setYAxisBoundsManual(false);
            }
            if (dataPoint.getY() < minY){
                minY = dataPoint.getY();
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(minY);
                graph.getViewport().setYAxisBoundsManual(false);
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
