package com.example.sweepstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GuidedSetup extends AppCompatActivity {

    /*
     * This class handles what goes on as the user runs the guided setup, known as the
     * SweepStat Experimental Assistant. The onCreate method is standard to all Android
     * activity classes, and initiated the guided setup on the first screen.
     * All buttons are set to reference the onClick method when pressed. This method checks which
     * button was pressed and either changes the view to the corresponding layout, or takes the
     * user-defined settings for the runtime and saves them to the SharedPreferences.
     * Optimization of this class will involve separating the onClick method into per-screen methods
     * or entirely individual methods so that there are less if and else if statements to check,
     * increasing readability. This will only negligibly improve running speeds.
     */

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    SharedPreferences saved;
    SharedPreferences.Editor saver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guided_setup);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        saver = saved.edit();
    }

    public void onClick(View view){

        // Store the button's id so as to not fetch it for each comparison
        int selected = view.getId();


        //Begin comparison to find which button was pressed:
        if(selected == R.id.goHome){
            this.finish();
        }

        else if(selected == R.id.nextButton || selected == R.id.backToScreen2){
            setContentView(R.layout.guide_second_screen);
        }

        else if(selected == R.id.nextButton2){
            setContentView(R.layout.guide_third_screen);
        }

        else if(selected == R.id.backButton2){
            setContentView(R.layout.guided_setup);
        }

        else if(selected == R.id.microelectrodeButton || selected == R.id.macroelectrodeButton || selected == R.id.backButtonScreen5){
            if(selected == R.id.microelectrodeButton){
                saver.putString("Selected_Electrode", "Microelectrode");
                saver.putString("Scale", "nano");
                saver.apply();
            }
            else if(selected == R.id.macroelectrodeButton){
                saver.putString("Selected_Electrode", "Macroelectrode");
                saver.putString("Scale", "micro");
                saver.apply();
            }
            setContentView(R.layout.guide_fourth_screen);
        }

        else if(selected == R.id.nextButtonScreen4 || selected == R.id.backButtonScreen6){
            setContentView(R.layout.guide_fifth_screen);
        }

        else if(selected == R.id.nextButtonScreen5 || selected == R.id.backButtonScreen7){
            setContentView(R.layout.guide_sixth_screen);
        }

        else if(selected == R.id.nextButtonScreen6 || selected == R.id.backButtonScreen8){
            setContentView(R.layout.guide_seventh_screen);
        }

        else if(selected == R.id.nextButtonScreen7){
            radioGroup = findViewById(R.id.referenceGroup);

            try{
                int referenceElectrode = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(referenceElectrode);
                saver.putString("Reference_Electrode", radioButton.getText().toString());
                saver.apply();
            } catch(NullPointerException e){
                Toast.makeText(this, "Please make a selection!", Toast.LENGTH_SHORT).show();
                return;
            }
            setContentView(R.layout.guide_eighth_screen);
        }

        else if(selected == R.id.nextButtonScreen8 || selected == R.id.backButtonScreen10){
            setContentView(R.layout.guide_ninth_screen);
        }

        else if(selected == R.id.nextButtonScreen9){
            EditText input_initial_potential = findViewById(R.id.initial_Potential);
            EditText input_vertex_potential = findViewById(R.id.vertex_Potential);
            String defined_initial_potential = input_initial_potential.getText().toString();
            String defined_vertex_potential = input_vertex_potential.getText().toString();
            if(defined_initial_potential.equals("") || defined_vertex_potential.equals("")){
                Toast.makeText(this, "Please make entries for both potentials!", Toast.LENGTH_SHORT).show();
                return;
            } else if (Double.parseDouble(defined_initial_potential) >= Double.parseDouble(defined_vertex_potential)){
                Toast.makeText(this, "Initial potential must be lower than vertex potential", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                saver.putString("initialVoltage", defined_initial_potential);
                saver.putString("highVoltage", defined_vertex_potential);
                saver.putString("lowVoltage", defined_initial_potential);
                saver.putString("finalVoltage", defined_initial_potential);
                if(defined_initial_potential.contains("-")){
                    saver.putString("polarityToggle", "Negative");
                }
                else{
                    saver.putString("polarityToggle", "Positive");
                }
                saver.apply();
            }
            setContentView(R.layout.guide_tenth_screen);
        }

        else if(selected == R.id.backButtonScreen9){
            setContentView(R.layout.guide_eighth_screen);
        }

        else if(selected == R.id.nextButtonScreen10){
            EditText input_scan_rate = findViewById(R.id.scan_rate);
            String user_input = input_scan_rate.getText().toString();
            if(user_input.equals("")){
                Toast.makeText(this, "Please enter a scan rate!", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                saver.putString("scanRate", user_input);
                saver.apply();
            }
            setContentView(R.layout.guide_final_screen);
        }

        else if(selected == R.id.backButtonFinalScreen){
            setContentView(R.layout.guide_tenth_screen);
        }

        else if(selected == R.id.finalizeSimple){
            saver.putString("sweepSegs", "2");
            saver.putString("quietTime", "Not Enabled");
            saver.putString("sampleInterval", "0.001");
            saver.apply();
            Intent goToRuntime = new Intent(this, ExperimentRuntime.class);
            startActivity(goToRuntime);
        }
    }

}
