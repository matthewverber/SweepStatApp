package com.example.sweepstatapp;

import com.jjoe64.graphview.series.DataPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class GraphTest {
    Graph graph;
    int x;
    int numOfPoints;
    double offset;
    double delta;

    @Before
    public void createGraph(){
        x = 0;
        numOfPoints = 500;
        delta = 0.00000000001;
//        graph = new Graph(0);
    }

    @Test
    public void generateFakeDataPoint_isCorrect(){
//        DataPoint dp = graph.generateFakeDataPoint(x);
//        offset = graph.getOffset();
//        assertEquals(0.1*x,dp.getX(), delta);
//        assertEquals(Math.sin(0.1*x+offset), dp.getY(), delta);
    }

    @Test
    public void generateFakeDataReverse_isCorrect(){
//        DataPoint dp = graph.generateFakeDataPoint(x);
//        offset = graph.getOffset();
//        assertEquals(0.1*x,dp.getX(), delta);
//        assertEquals(-1*Math.sin(0.1*x+offset), dp.getY(), delta);
    }

    @Test
    public void drawOnFakeData_isCorrect(){
//        graph.drawOnFakeData(numOfPoints);
        try {
            Thread.sleep(105);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        offset = graph.getOffset();
        ArrayList<DataPoint> data = graph.getFullData();
        for (int i = 0; i < 2*numOfPoints-1; i++, x++) {
            if(i < numOfPoints){
                DataPoint dp = data.get(i);
                assertEquals(0.1*i,dp.getX(),delta);
                assertEquals(Math.sin(0.1*x+offset), dp.getY(),delta);
            } else if (i >= numOfPoints){
                int index = numOfPoints - (i+1-numOfPoints) -1;
                DataPoint dp = data.get(i);
                assertEquals(0.1*index,dp.getX(),delta);
                assertEquals(-1*Math.sin(0.1*index+offset), dp.getY(),delta);
            }
        }
    }
}
