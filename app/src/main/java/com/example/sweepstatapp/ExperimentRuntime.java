package com.example.sweepstatapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ExperimentRuntime extends AppCompatActivity {
    TextView initialVoltage, highVoltage, lowVoltage, finalVoltage, polarity, scanRate,
    sampleInterval, quietTime, sensitivity, sweepSegments;
    Boolean autoSens, finalE, auxRecord;
    String loadFailed = "Load failed!";
    static final String INITIAL_VOLTAGE = "INITIAL VOLTAGE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_runtime);
        SharedPreferences saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
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
        if(saved != null){
            initialVoltage.setText(saved.getString(INITIAL_VOLTAGE, loadFailed));
            highVoltage.setText(saved.getString("highVoltage", loadFailed));
            lowVoltage.setText(saved.getString("lowVoltage", loadFailed));
            finalVoltage.setText(saved.getString("finalVoltage", loadFailed));
            polarity.setText(saved.getString("polarity", loadFailed));
            scanRate.setText(saved.getString("scanRate", loadFailed));
            sweepSegments.setText(saved.getString("scanSegments", loadFailed));
            sampleInterval.setText(saved.getString("sampleInterval", loadFailed));
            quietTime.setText(saved.getString("quietTime", loadFailed));
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

    public void back(View view){
        this.finish();
    }
}
