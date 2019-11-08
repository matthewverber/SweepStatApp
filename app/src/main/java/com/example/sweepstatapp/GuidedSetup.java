package com.example.sweepstatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GuidedSetup extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guided_setup);
        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }
    }

    public void onClick(View view){
        int selected = view.getId();
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
        else if(selected == R.id.microelectrodeButton){
            // Do something to indicate a selected microelectrode
            setContentView(R.layout.guide_fourth_screen);
        }
        else if(selected == R.id.macroelectrodeButton){
            // Do something to indicate a selected macroelectrode
            setContentView(R.layout.guide_fourth_screen);
        }
        else if(selected == R.id.nextButtonScreen4){
            setContentView(R.layout.guide_fifth_screen);
        }
        else if(selected == R.id.backButtonScreen5){
            setContentView(R.layout.guide_fourth_screen);
        }
        else if(selected == R.id.nextButtonScreen5){
            setContentView(R.layout.guide_sixth_screen);
        }
        else if(selected == R.id.nextButtonScreen6){
            setContentView(R.layout.guide_seventh_screen);
        }
        else if(selected == R.id.backButtonScreen6){
            setContentView(R.layout.guide_fifth_screen);
        }
        else if(selected == R.id.nextButtonScreen7){
            setContentView(R.layout.guide_eighth_screen);
        }
        else if(selected == R.id.backButtonScreen7){
            setContentView(R.layout.guide_sixth_screen);
        }
        else if(selected == R.id.nextButtonScreen8){
            setContentView(R.layout.guide_ninth_screen);
        }
        else if(selected == R.id.backButtonScreen8){
            setContentView(R.layout.guide_seventh_screen);
        }
        else if(selected == R.id.nextButtonScreen9){
            setContentView(R.layout.guide_tenth_screen);
        }
        else if(selected == R.id.backButtonScreen9){
            setContentView(R.layout.guide_eighth_screen);
        }
        else if(selected == R.id.nextButtonScreen10){
            setContentView(R.layout.guide_final_screen);
        }
        else if(selected == R.id.backButtonScreen10){
            setContentView(R.layout.guide_ninth_screen);
        }
        else if(selected == R.id.backButtonFinalScreen){
            setContentView(R.layout.guide_tenth_screen);
        }
        else if(selected == R.id.finalizeSimple){
            Intent goToRuntime = new Intent(this, ExperimentRuntime.class);
            startActivity(goToRuntime);
        }
    }

}
