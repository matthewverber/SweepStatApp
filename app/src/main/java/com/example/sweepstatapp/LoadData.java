package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.File;
import java.util.ArrayList;

public class LoadData extends AppCompatActivity {

//    File DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), ExperimentRuntime.SWEEPSTAT);
    File DATA_DIR = ExperimentRuntime.DATA_DIR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        if (!ExperimentRuntime.isExternalStorageAvailable() || ExperimentRuntime.isExternalStorageReadOnly())
            return;
        ExperimentRuntime.verifyStoragePermissions(this);

        LinearLayout buttonLayout = findViewById(R.id.dataButtons);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (DATA_DIR.exists() && DATA_DIR.listFiles() != null) {
            for (File dataSheet : DATA_DIR.listFiles()) {
                if (dataSheet.isDirectory())
                    continue;
                if (dataSheet.getName().endsWith(".xls")) {
                    final String fileName = dataSheet.getName().substring(0, dataSheet.getName().length() - 4);
                    Button button = new Button(this);
                    button.setText(fileName);
                    button.setTag(fileName);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent graph = new Intent(LoadData.this, ExperimentRuntime.class);
                            graph.putExtra("loadFile", fileName + ".xls");
                            startActivity(graph);
                        }
                    });
                    buttonLayout.addView(button, p);
                }
            }
        } else {
            findViewById(R.id.noSavedData).setVisibility(View.VISIBLE);
        }
    }
}
