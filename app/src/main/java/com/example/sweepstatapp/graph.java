package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.os.Bundle;
import com.jjoe64.graphview.GraphView;
import android.view.View;

import java.util.Random;

public class graph extends AppCompatActivity {
    private LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private int x = 0;
    private int numberOfPoints = 500;
    private double offset = 0;
    private long interval = 50;
    private GraphView graph = null;
    private Viewport viewport = null;
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
            graph.addSeries(series);
        }
        if (view.getId() == R.id.realTime){
            offset = Math.random();
            x = 0;
//            numberOfPoints = 500;
//            interval = 100;
            graph.removeAllSeries();
            series = new LineGraphSeries<>();
            graph.addSeries(series);
            viewport.setMaxX(numberOfPoints*0.1);
            graphInRealTime();
        }
    }

    protected DataPoint generateFakeDataPoint() {
        return new DataPoint(0.1*x,Math.sin(0.1*x+offset));
    }

    protected void generateFakeData(){
        DataPoint[] fakeData = new DataPoint[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++, x++){
            fakeData[i] = generateFakeDataPoint();
        }
        series = new LineGraphSeries<>(fakeData);
    }

    protected void graphInRealTime(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                x = 0;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
               x = 0;
                for (int i = 0; i < numberOfPoints; i++, x++){
                    if (i != x){
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry(generateFakeDataPoint());
                        }
                    });
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    protected void addEntry(DataPoint dataPoint){
        series.appendData(dataPoint, false, numberOfPoints);
    }
}
