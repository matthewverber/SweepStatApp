package com.example.sweepstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdvancedSetup extends AppCompatActivity {

    Button finish;
    Spinner sensitivity;
    EditText initialVoltage, highVoltage, lowVoltage, finalVoltage, scanRate, sweepSegs, sampleInterval, quietTime;
    ToggleButton polarityToggle;
    CheckBox isAutoSens, isFinalE, isAuxRecording;
    Boolean polarity;
    public static final String INITIAL_VOLTAGE = "initialVoltage";
    public static final String HIGH_VOLTAGE = "highVoltage";
    public static final String LOW_VOLTAGE = "lowVoltage";
    public static final String FINAL_VOLTAGE = "finalVoltage";
    public static final String POLARITY_TOGGLE = "polarityToggle";
    public static final String SCAN_RATE = "scanRate";
    public static final String SWEEP_SEGS = "sweepSegs";
    public static final String SAMPLE_INTEVAL = "sampleInterval";
    public static final String QUIET_TIME = "quietTime";
    public static final String SENSITIVITY = "sensitivity";
    public static final String IS_AUTOSENS = "isAutoSens";
    public static final String IS_FINALE = "isFinalE";
    public static final String IS_AUX_RECORDING = "isAuxRecording";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_setup);
        finish = findViewById(R.id.finishAdvanced);

        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.highVoltage);
        lowVoltage = findViewById(R.id.lowVoltage);
        finalVoltage = findViewById(R.id.finalVoltage);
        polarityToggle = findViewById(R.id.polarity);
        scanRate = findViewById(R.id.scanRate);
        sweepSegs = findViewById(R.id.sweepSegments);
        sampleInterval = findViewById(R.id.sampleInterval);
        quietTime = findViewById(R.id.quietTime);
        sensitivity = findViewById(R.id.sensitivity);
        isAutoSens = findViewById(R.id.isAutoSens);
        isFinalE = findViewById(R.id.isFinalE);
        isAuxRecording = findViewById(R.id.isAuxSignalRecording);
        polarity = true;

        polarityToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                polarity = !b;
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
    }

    public void verifyEntries(View view){
        try{
            String initialV = (initialVoltage.getText().toString());
            String highV = (highVoltage.getText().toString());
            String lowV = (lowVoltage.getText().toString());
            String finalV = (finalVoltage.getText().toString());
            String polarity = polarityToggle.getText().toString();
            String scanrate = (scanRate.getText().toString());
            String segments = (sweepSegs.getText().toString());
            String interval = (sampleInterval.getText().toString());
            String quiettime = (quietTime.getText().toString());
            String sens = (sensitivity.getSelectedItem().toString());
            boolean isAuto = isAutoSens.isChecked();
            boolean isFinal = isFinalE.isChecked();
            boolean isAux = isAuxRecording.isChecked();

            SharedPreferences saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
            SharedPreferences.Editor saver = saved.edit();
            saver.putString(INITIAL_VOLTAGE, initialV);
            saver.putString(HIGH_VOLTAGE, highV);
            saver.putString(LOW_VOLTAGE, lowV);
            saver.putString(FINAL_VOLTAGE, finalV);
            saver.putString(POLARITY_TOGGLE, polarity);
            saver.putString(SCAN_RATE, scanrate);
            saver.putString(SWEEP_SEGS, segments);
            saver.putString(SAMPLE_INTEVAL, interval);
            saver.putString(QUIET_TIME, quiettime);
            saver.putString(SENSITIVITY, sens);
            saver.putBoolean(IS_AUTOSENS, isAuto);
            saver.putBoolean(IS_FINALE, isFinal);
            saver.putBoolean(IS_AUX_RECORDING, isAux);
            saver.apply();
            Intent goToRuntime = new Intent(this, ExperimentRuntime.class);
            startActivity(goToRuntime);
//            this.finish();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Please make entries for all parameters!", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchToSimple(View view){
        this.finish();
        //For final version, code in the switching to and inclusion of the simple setup
    }

    public void close(View view){
        this.finish();
    }
}
