package com.example.sweepstatapp;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GraphTest {
    Graph graph;
    int x;
    int numOfPoints;
    double offset;

    @Before
    public void createGraph(){
        x = 0;
        numOfPoints = 500;
        GraphView graphView = new GraphView(null);
        graph = new Graph(graphView, graphView.getViewport());
        offset = graph.getOffset();
    }

    @Test
    public void generateFakeDataPoint_isCorrect(){
        DataPoint dp = graph.generateFakeDataPoint(x);
        assertEquals(0.1*x,dp.getX());
        assertEquals(Math.sin(0.1*x+offset), dp.getY());
    }

    @Test
    public void generateFakeDataReverse_isCorrect(){
        DataPoint dp = graph.generateFakeDataPoint(x);
        assertEquals(0.1*x,dp.getX());
        assertEquals(-1*Math.sin(0.1*x+offset), dp.getY());
    }

    @Test
    public void drawOnFakeData_isCorrect(){
        graph.drawOnFakeData(numOfPoints);
        ArrayList<DataPoint> data = graph.getFullData();
        for (int i = 0; i < 2*numOfPoints-1; i++, x++) {
            if(i < numOfPoints){
                DataPoint dp = data.get(i);
                assertEquals(0.1*i,dp.getX());
                assertEquals(Math.sin(0.1*x+offset), dp.getY());
            } else if (i >= numOfPoints){
                int index = numOfPoints - (i-numOfPoints) -1;
                DataPoint dp = data.get(i);
                assertEquals(0.1*index,dp.getX());
                assertEquals(Math.sin(0.1*index+offset), dp.getY());
            }
        }
    }
}
