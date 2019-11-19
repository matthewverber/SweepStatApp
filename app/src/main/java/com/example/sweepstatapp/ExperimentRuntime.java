package com.example.sweepstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ExperimentRuntime extends AppCompatActivity {
    TextView initialVoltage, highVoltage, lowVoltage, finalVoltage, polarity, scanRate,
    sampleInterval, quietTime, sensitivity, sweepSegments;
    Boolean autoSens, finalE, auxRecord;
    String loadFailed = "Load failed!";
    double lowVolt, highVolt;
    private Graph graph = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_runtime);
        SharedPreferences saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        GraphView graphView = findViewById(R.id.graph);
        Viewport viewport = graphView.getViewport();
        viewport.setScrollable(true);
        viewport.setScrollableY(true);
        viewport.setScalable(true);
        viewport.setScalableY(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinX(-1);
        viewport.setMaxX(1);

        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.highVoltage);
        lowVoltage = findViewById(R.id.lowVoltage);
        finalVoltage = findViewById(R.id.finalVoltage);
        polarity = findViewById(R.id.polarity);
        scanRate = findViewById(R.id.scanRate);
        sweepSegments = findViewById(R.id.sweepSegments);
        sampleInterval = findViewById(R.id.sampleInterval);
        quietTime = findViewById(R.id.quietTime);
        sensitivity = findViewById(R.id.sensitivity);
        autoSens = false;
        finalE = false;
        auxRecord = false;

        Bundle data = getIntent().getExtras();
        if (data != null && data.getString("loadFile") != null) {
            String filePath = data.getString("loadFile");
            loadData(filePath);
        } else {
            if (saved != null) {
                initialVoltage.setText(saved.getString(AdvancedSetup.INITIAL_VOLTAGE, loadFailed));

                String highV = saved.getString(AdvancedSetup.HIGH_VOLTAGE, loadFailed);
                String lowV = saved.getString(AdvancedSetup.LOW_VOLTAGE, loadFailed);
                highVoltage.setText(highV);
                lowVoltage.setText(lowV);
                if (!highV.equals("") && !lowV.equals("")) {
                    highVolt = Double.parseDouble(highV);
                    lowVolt = Double.parseDouble(lowV);
                    graph = new Graph(graphView, lowVolt, highVolt);
                } else
                    graph = new Graph(graphView);

                finalVoltage.setText(saved.getString(AdvancedSetup.FINAL_VOLTAGE, loadFailed));
                polarity.setText(saved.getString(AdvancedSetup.POLARITY_TOGGLE, loadFailed));
                scanRate.setText(saved.getString(AdvancedSetup.SCAN_RATE, loadFailed));
                sweepSegments.setText(saved.getString(AdvancedSetup.SWEEP_SEGS, loadFailed));
                sampleInterval.setText(saved.getString(AdvancedSetup.SAMPLE_INTEVAL, loadFailed));
                quietTime.setText(saved.getString(AdvancedSetup.QUIET_TIME, loadFailed));
                sensitivity.setText(saved.getString(AdvancedSetup.SENSITIVITY, loadFailed));
                autoSens = saved.getBoolean(AdvancedSetup.IS_AUTOSENS, false);
                finalE = saved.getBoolean(AdvancedSetup.IS_FINALE, true);
                auxRecord = saved.getBoolean(AdvancedSetup.IS_AUX_RECORDING, false);
                if (autoSens)
                    findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
                if (finalE)
                    findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
                if (auxRecord)
                    findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to load saved inputs!", Toast.LENGTH_SHORT).show();
                graph = new Graph(graphView);
            }
        }
    }

    public void onClick(View view){
        if (view.getId() == R.id.runExperiment){
           graph.drawOnFakeData();
        } else if (view.getId() == R.id.exportRes) {
            DataPoint[] dataPoint = graph.getFullData().toArray(new DataPoint[0]);
            double[] voltage = new double[dataPoint.length];
            double[] current = new double[dataPoint.length];
            for (int i = 0; i < dataPoint.length; i++){
                voltage[i] = dataPoint[i].getX();
                current[i] = dataPoint[i].getY();
            }
            Intent export = new Intent(this, Export.class);
            export.putExtra("voltage", voltage);
            export.putExtra("current", current);
            startActivity(export);
        }
    }

    protected void loadData(String filePath){
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(Export.DIR, filePath));
            Workbook wb = new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            Cell c;
            int rowIndex = 0;
            initialVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            highVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            lowVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            finalVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            polarity.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            scanRate.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sweepSegments.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sampleInterval.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            quietTime.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sensitivity.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            autoSens = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            finalE = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            auxRecord = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            if(autoSens)
                findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
            if(finalE)
                findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
            if(auxRecord)
                findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);

            rowIndex += 2;
            Row row = sheet.getRow(rowIndex);
            GraphView graphView = findViewById(R.id.graph);
            graph = new Graph(graphView, Double.parseDouble(lowVoltage.getText()+""), Double.parseDouble(highVoltage.getText()+""));
            while(row != null && !row.getCell(1).getStringCellValue().equals("")){
                double x = row.getCell(0).getNumericCellValue();
                double y = Double.parseDouble(row.getCell(1).getStringCellValue());
                //                double y = row.getCell(1).getNumericCellValue();
                graph.putData(x,y);
                rowIndex++;
                row = sheet.getRow(rowIndex);
            }
        } catch (IOException e){
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void back(View view){
        this.finish();
    }
}
