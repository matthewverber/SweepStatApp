package com.example.sweepstatapp;

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
        if(view.getId() == R.id.goHome){
            this.finish();
        }
        else if(view.getId() == R.id.nextButton){
            setContentView(R.layout.guide_second_screen);
        }
    }

}
