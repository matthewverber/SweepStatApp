package com.example.sweepstatapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.core.content.FileProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class AdvancedSetup extends AppCompatActivity {

    Button finish;
    Spinner sensitivity;
    EditText initialVoltage, highVoltage, lowVoltage, finalVoltage, scanRate, sweepSegs, sampleInterval;
    CheckBox isAutoSens, isFinalE, isAuxRecording;
    Boolean polarity;
    File CONFIGURATION_DIR = ExperimentRuntime.CONFIGURATION_DIR;
    File TEMP_DIR = new File(CONFIGURATION_DIR, "temp");
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
//      quietTime = findViewById(R.id.quietTime);
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
//          String quiettime = (quietTime.getText().toString());
            String sens = (sensitivity.getSelectedItem().toString());
            boolean isAuto = isAutoSens.isChecked();
            boolean isFinal = isFinalE.isChecked();
            boolean isAux = isAuxRecording.isChecked();

            if (initialV.equals("") || highV.equals("") || lowV.equals("") || finalV.equals("") || scanrate.equals("") || segments.equals("") || interval.equals("") ){
                throw new Exception("User did not define all parameters");
            }
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

        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Please make entries for all parameters!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveConfiguration(View view){
        if (!ExperimentRuntime.isExternalStorageAvailable() || ExperimentRuntime.isExternalStorageReadOnly())
            return;
        ExperimentRuntime.verifyStoragePermissions(this);
        CONFIGURATION_DIR.mkdirs();
        showEnterFileName(this, ExperimentRuntime.LOCAL);
    }

    private void showEnterFileName(final Context c, final int action) {
        final EditText fileNameEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter File Name")
                .setMessage("Please enter file name:")
                .setView(fileNameEditText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (action < ExperimentRuntime.LOCAL || action > ExperimentRuntime.EXPORT)
                            return;

                        String fileName = String.valueOf(fileNameEditText.getText());
                        File file = null;
                        if (action == ExperimentRuntime.LOCAL){
                            if (fileName == null || fileName.equals(""))
                                return;

                            file = new File(CONFIGURATION_DIR, fileName + ".xls");
                            if (file.exists()){
                                int i = 1;
                                do{
                                    file = new File(CONFIGURATION_DIR, fileName + '(' + i +").xls");
                                    i++;
                                } while (file.exists());
                            }
                        } else if (action == ExperimentRuntime.EXPORT){
                            if (TEMP_DIR.exists())
                                deleteDir(TEMP_DIR);
                            else
                                TEMP_DIR.mkdirs();

                            file = new File(TEMP_DIR,fileName+".xls");
                        }

                        createExcelFile(file, action);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    protected File createExcelFile(File file, int action){
        if (action < ExperimentRuntime.LOCAL || action > ExperimentRuntime.EXPORT)
            return null;

        File retVal = null;

        Workbook wb = new HSSFWorkbook();
        Cell c;
        int nextRow = 0;

        Sheet sheet = wb.createSheet("data");
        Row row;

        String[][] parameters = new String[2][13];

        parameters[0][0] = INITIAL_VOLTAGE;
        parameters[0][1] = HIGH_VOLTAGE;
        parameters[0][2] = LOW_VOLTAGE;
        parameters[0][3] = FINAL_VOLTAGE;
        parameters[0][4] = POLARITY_TOGGLE;
        parameters[0][5] = SCAN_RATE;
        parameters[0][6] = SWEEP_SEGS;
        parameters[0][7] = SAMPLE_INTEVAL;
        parameters[0][8] = QUIET_TIME;
        parameters[0][9] = SENSITIVITY;
        parameters[0][10] = IS_AUTOSENS;
        parameters[0][11] = IS_FINALE;
        parameters[0][12] = IS_AUX_RECORDING;

        parameters[1][0] = initialVoltage.getText().toString();
        parameters[1][1] = highVoltage.getText().toString();
        parameters[1][2] = lowVoltage.getText().toString();
        parameters[1][3] = finalVoltage.getText().toString();
        parameters[1][4] = polarity.toString();
        parameters[1][5] = scanRate.getText().toString();
        parameters[1][6] = sweepSegs.getText().toString();
        parameters[1][7] = sampleInterval.getText().toString();
        parameters[1][8] = quietTime.getText().toString();
        parameters[1][9] = sensitivity.getSelectedItem().toString();
        parameters[1][10] = isAutoSens.isChecked()+"";
        parameters[1][11] = isFinalE.isChecked()+"";
        parameters[1][12] = isAuxRecording.isChecked()+"";

        for (int i = 0; i < parameters[1].length; i++, nextRow++) {
            row = sheet.createRow(nextRow);
            c = row.createCell(0);
            c.setCellValue(parameters[0][i]);
            c = row.createCell(1);
            c.setCellValue(parameters[1][i]);
        }

        sheet.setColumnWidth(0, 20*256);
        sheet.setColumnWidth(1, 20*256);

        FileOutputStream os = null;
        try {
            if (!(action == ExperimentRuntime.EXPORT && file.exists())){
                file.createNewFile();
                os = new FileOutputStream(file);
                wb.write(os);
                os.flush();
                os.close();
                wb.close();
                retVal = file;
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (os != null){
                    os.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        if (action == ExperimentRuntime.EXPORT) {
            Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Choose where to export"));
        }
        return retVal;
    }

    private void deleteDir(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDir(file);
                file.delete();
            } else {
                file.delete();
            }
        }
    }

    protected void loadData(String filePath){
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(CONFIGURATION_DIR, filePath));
            Workbook wb = new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            Cell c;
            int rowIndex = 0;
            initialVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            highVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            lowVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            finalVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            String p = sheet.getRow(rowIndex++).getCell(1).getStringCellValue();
            if (p.equals("Positive"))
                polarity = false;
            else
                polarity = true;
            polarityToggle.setChecked(polarity);
            scanRate.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sweepSegs.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sampleInterval.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            quietTime.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            String s = sheet.getRow(rowIndex++).getCell(1).getStringCellValue();
            for(int i= 0; i < sensitivity.getAdapter().getCount(); i++)
                if(sensitivity.getAdapter().getItem(i).toString().equals(s))
                    sensitivity.setSelection(i);
            isAutoSens.setChecked(Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue()));
            isFinalE.setChecked(Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue()));
            isAuxRecording.setChecked(Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue()));
        } catch (IOException e){
            e.printStackTrace();
        } catch (IllegalStateException e){
            e.printStackTrace();
        } finally {
            if (is != null){
                try {
                    is.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void close(View view){
        this.finish();
    }
}
