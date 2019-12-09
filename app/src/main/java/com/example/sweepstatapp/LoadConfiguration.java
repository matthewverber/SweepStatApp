package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

public class LoadConfiguration extends AppCompatActivity {
    File CONFIGURATION_DIR = ExperimentRuntime.CONFIGURATION_DIR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_configuration);

        if (!ExperimentRuntime.isExternalStorageAvailable() || ExperimentRuntime.isExternalStorageReadOnly())
            return;
        ExperimentRuntime.verifyStoragePermissions(this);

        LinearLayout buttonLayout = findViewById(R.id.configurationButtons);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (CONFIGURATION_DIR.exists() && CONFIGURATION_DIR.listFiles() != null) {
            for (File dataSheet : CONFIGURATION_DIR.listFiles()) {
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
                            Intent setup = new Intent(LoadConfiguration.this, AdvancedSetup.class);
                            setup.putExtra("loadFile", fileName + ".xls");
                            startActivity(setup);
                            LoadConfiguration.this.finish();
                        }
                    });
                    buttonLayout.addView(button, p);
                }
            }
        } else {
//            buttonLayout.onFinishInflate();
            View v = findViewById(R.id.noSavedConfiguration);
            findViewById(R.id.noSavedConfiguration).setVisibility(View.VISIBLE);
        }
    }
}
