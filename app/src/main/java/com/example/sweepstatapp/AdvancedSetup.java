package com.example.sweepstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdvancedSetup extends AppCompatActivity {

    Button finish;
    Spinner sensitivity;
    EditText initialVoltage, highVoltage, lowVoltage, finalVoltage, scanRate, sweepSegs, sampleInterval, quietTime;
    CheckBox isAutoSens, isFinalE, isAuxRecording;
    Boolean polarity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_setup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        finish = findViewById(R.id.finishAdvanced);
        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.highVoltage);
        lowVoltage = findViewById(R.id.lowVoltage);
        finalVoltage = findViewById(R.id.finalVoltage);
        scanRate = findViewById(R.id.scanRate);
        sweepSegs = findViewById(R.id.sweepSegments);
        sampleInterval = findViewById(R.id.sampleInterval);
        quietTime = findViewById(R.id.quietTime);
        sensitivity = findViewById(R.id.sensitivity);
        isAutoSens = findViewById(R.id.isAutoSens);
        isFinalE = findViewById(R.id.isFinalE);
        isAuxRecording = findViewById(R.id.isAuxSignalRecording);
        polarity = true;

    }

    public void verifyEntries(View view){
        try{
            String initialV = (initialVoltage.getText().toString());
            String highV = (highVoltage.getText().toString());
            String lowV = (lowVoltage.getText().toString());
            String finalV = (finalVoltage.getText().toString());
            String polarity;
            if(initialVoltage.getText().toString().contains("-")){
                polarity = "Negative";
            }
            else{
                polarity = "Positive";
            }
            String scanrate = (scanRate.getText().toString());
            String segments = (sweepSegs.getText().toString());
            String interval = (sampleInterval.getText().toString());
            String quiettime = (quietTime.getText().toString());
            String sens = (sensitivity.getSelectedItem().toString());
            boolean isAuto = isAutoSens.isChecked();
            boolean isFinal = isFinalE.isChecked();
            boolean isAux = isAuxRecording.isChecked();

            if (initialV.equals("") || highV.equals("") || lowV.equals("") || finalV.equals("") || scanrate.equals("") || segments.equals("") || interval.equals("") || quiettime.equals("")){
                throw new Exception("User did not define all parameters");
            }
            SharedPreferences saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
            SharedPreferences.Editor saver = saved.edit();
            saver.putString(ExperimentRuntime.INITIAL_VOLTAGE, initialV);
            saver.putString("highVoltage", highV);
            saver.putString("lowVoltage", lowV);
            saver.putString("finalVoltage", finalV);
            saver.putString("polarity", polarity);
            saver.putString("scanRate", scanrate);
            saver.putString("scanSegments", segments);
            saver.putString("sampleInterval", interval);
            saver.putString("quietTime", quiettime);
            saver.putString("sensitivity", sens);
            saver.putBoolean("isAutoSens", isAuto);
            saver.putBoolean("isFinalE", isFinal);
            saver.putBoolean("isAuxRecording", isAux);
            saver.apply();


            Intent goToRuntime = new Intent(this, ExperimentRuntime.class);
            startActivity(goToRuntime);

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Please make entries for all parameters!", Toast.LENGTH_SHORT).show();
        }
    }

    public void close(View view){
        this.finish();
    }
}
