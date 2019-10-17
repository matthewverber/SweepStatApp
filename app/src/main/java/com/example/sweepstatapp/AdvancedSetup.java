package com.example.sweepstatapp;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advanced_setup);
        finish = findViewById(R.id.finishAdvanced);

        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.initialVoltage);
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
                if (!b) {  // Polarity will be positive
                    polarity = true;
                }
                else {  // Polarity will be negative
                    polarity = false;
                }
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
    }

    public void verifyEntries(View view){
        try{
            double initialV = Double.parseDouble(initialVoltage.getText().toString());
            double highV = Double.parseDouble(highVoltage.getText().toString());
            double lowV = Double.parseDouble(lowVoltage.getText().toString());
            double finalV = Double.parseDouble(finalVoltage.getText().toString());
            double scanrate = Double.parseDouble(scanRate.getText().toString());
            double segments = Double.parseDouble(sweepSegs.getText().toString());
            double interval = Double.parseDouble(sweepSegs.getText().toString());
            double quiettime = Double.parseDouble(quietTime.getText().toString());
            double sens = Double.parseDouble(sensitivity.getSelectedItem().toString());
            boolean isAuto = isAutoSens.isChecked();
            boolean isFinal = isFinalE.isChecked();
            boolean isAux = isAuxRecording.isChecked();
            // save parameters back over to main activity OR runtime activity
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
