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

public class ExperimentRuntime extends AppCompatActivity {
    TextView initialVoltage, highVoltage, lowVoltage, finalVoltage, polarity, scanRate,
    sampleInterval, sensitivity, sweepSegments;
    Boolean autoSens, finalE, auxRecord;
    String loadFailed = "Not currently enabled";
    static final String INITIAL_VOLTAGE = "INITIAL VOLTAGE";
    private Graph graph = null;
    private int numOfPoints = 100;
    SharedPreferences saved;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_runtime);
        saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        GraphView graphView = findViewById(R.id.graph);
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinY(-1);
        viewport.setMaxY(1);
        viewport.setMinX(0);
        graph = new Graph(graphView, viewport);

        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.highVoltage);
        lowVoltage = findViewById(R.id.lowVoltage);
        finalVoltage = findViewById(R.id.finalVoltage);
        polarity = findViewById(R.id.polarity);
        scanRate = findViewById(R.id.scanRate);
        sweepSegments = findViewById(R.id.sweepSegments);
        sampleInterval = findViewById(R.id.sampleInterval);
//      quietTime = findViewById(R.id.quietTime);
        sensitivity = findViewById(R.id.sensitivity);
        autoSens = false;
        finalE = false;
        auxRecord = false;


        if(saved != null){
            initialVoltage.setText(saved.getString(INITIAL_VOLTAGE, loadFailed));
            highVoltage.setText(saved.getString("highVoltage", loadFailed));
            lowVoltage.setText(saved.getString("lowVoltage", loadFailed));
            finalVoltage.setText(saved.getString("finalVoltage", loadFailed));
            polarity.setText(saved.getString("polarity", loadFailed));
            scanRate.setText(saved.getString("scanRate", loadFailed));
            sweepSegments.setText(saved.getString("scanSegments", loadFailed));
            sampleInterval.setText(saved.getString("sampleInterval", loadFailed));
//          quietTime.setText(saved.getString("quietTime", loadFailed));
            sensitivity.setText(saved.getString("sensitivity", loadFailed) + "A/V");
            autoSens = saved.getBoolean("isAutoSens", false);
            finalE = saved.getBoolean("isFinalE", true);
            auxRecord = saved.getBoolean("isAuxRecording", false);
            if(autoSens)
                findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
            if(finalE)
                findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
            if(auxRecord)
                findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Failed to load saved inputs!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(saved != null){
            initialVoltage.setText(saved.getString(INITIAL_VOLTAGE, loadFailed));
            highVoltage.setText(saved.getString("highVoltage", loadFailed));
            lowVoltage.setText(saved.getString("lowVoltage", loadFailed));
            finalVoltage.setText(saved.getString("finalVoltage", loadFailed));
            polarity.setText(saved.getString("polarity", loadFailed));
            scanRate.setText(saved.getString("scanRate", loadFailed));
            sweepSegments.setText(saved.getString("scanSegments", loadFailed));
            sampleInterval.setText(saved.getString("sampleInterval", loadFailed));
//          quietTime.setText(saved.getString("quietTime", loadFailed));
            sensitivity.setText(saved.getString("sensitivity", loadFailed));
            autoSens = saved.getBoolean("isAutoSens", false);
            finalE = saved.getBoolean("isFinalE", true);
            auxRecord = saved.getBoolean("isAuxRecording", false);
            if(autoSens)
                findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
            if(finalE)
                findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
            if(auxRecord)
                findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "Failed to load saved inputs!", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClick(View view){
        if (view.getId() == R.id.runExperiment){
           graph.drawOnFakeData(numOfPoints);
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

    public void back(View view){
        this.finish();
    }
}
