package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.app.Activity;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Export extends AppCompatActivity {

    double[] voltage, current;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);
        Bundle data = getIntent().getExtras();
        voltage = data.getDoubleArray("voltage");
        current = data.getDoubleArray("current");
    }

    public void onClick(View view){
        if (view.getId() == R.id.local){
            createExcelFile();
        } else if (view.getId() == R.id.googleDrive){

        }
    }

    protected boolean createExcelFile(){
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()){
            return false;
        }

        boolean success = false;

        Workbook wb = new HSSFWorkbook();
        Cell c = null;
        int nextRow = 0;

        Sheet sheet = wb.createSheet("data");
        SharedPreferences saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        String[][] parameters = new String[2][13];
        String loadFailed = "Load failed!";
        Row row;

        if(saved != null){
            parameters[0][0] = AdvancedSetup.INITIAL_VOLTAGE;
            parameters[0][1] = AdvancedSetup.HIGH_VOLTAGE;
            parameters[0][2] = AdvancedSetup.LOW_VOLTAGE;
            parameters[0][3] = AdvancedSetup.FINAL_VOLTAGE;
            parameters[0][4] = AdvancedSetup.POLARITY_TOGGLE;
            parameters[0][5] = AdvancedSetup.SCAN_RATE;
            parameters[0][6] = AdvancedSetup.SWEEP_SEGS;
            parameters[0][7] = AdvancedSetup.SAMPLE_INTEVAL;
            parameters[0][8] = AdvancedSetup.QUIET_TIME;
            parameters[0][9] = AdvancedSetup.SENSITIVITY;
            parameters[0][10] = AdvancedSetup.IS_AUTOSENS;
            parameters[0][11] = AdvancedSetup.IS_FINALE;
            parameters[0][12] = AdvancedSetup.IS_AUX_RECORDING;

            parameters[1][0] = saved.getString(AdvancedSetup.INITIAL_VOLTAGE, loadFailed);
            parameters[1][1] = saved.getString(AdvancedSetup.HIGH_VOLTAGE, loadFailed);
            parameters[1][2] = saved.getString(AdvancedSetup.LOW_VOLTAGE, loadFailed);
            parameters[1][3] = saved.getString(AdvancedSetup.FINAL_VOLTAGE, loadFailed);
            parameters[1][4] = saved.getString(AdvancedSetup.POLARITY_TOGGLE, loadFailed);
            parameters[1][5] = saved.getString(AdvancedSetup.SCAN_RATE, loadFailed);
            parameters[1][6] = saved.getString(AdvancedSetup.SWEEP_SEGS, loadFailed);
            parameters[1][7] = saved.getString(AdvancedSetup.SAMPLE_INTEVAL, loadFailed);
            parameters[1][8] = saved.getString(AdvancedSetup.QUIET_TIME, loadFailed);
            parameters[1][9] = saved.getString(AdvancedSetup.SENSITIVITY, loadFailed);
            parameters[1][10] = saved.getBoolean(AdvancedSetup.IS_AUTOSENS, false)+"";
            parameters[1][11] = saved.getBoolean(AdvancedSetup.IS_FINALE, true)+"";
            parameters[1][12] = saved.getBoolean(AdvancedSetup.IS_AUX_RECORDING, false)+"";

            for (int i = 0; i < parameters[1].length; i++, nextRow++){
                row = sheet.createRow(nextRow);
                c = row.createCell(0);
                c.setCellValue(parameters[0][i]);
                c = row.createCell(1);
                c.setCellValue(parameters[1][i]);
            }
        }

        row = sheet.createRow(nextRow);
        c = row.createCell(0);
        c.setCellValue("Voltage");
        c = row.createCell(1);
        c.setCellValue("Current");
        nextRow++;
        sheet.createRow(nextRow);
        nextRow++;

        for (int i = 0; i < voltage.length; i++){
            row = sheet.createRow(i+nextRow);
            c = row.createCell(0);
            c.setCellValue(voltage[i]);
            c = row.createCell(1);
            c.setCellValue(current[i]);
        }

        sheet.setColumnWidth(0, 20*256);
        sheet.setColumnWidth(1, 20*256);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SavedData.xls");

        verifyStoragePermissions(Export.this);

        if (file.exists()){
            file.delete();
        }
        FileOutputStream os = null;
        try {
            file.createNewFile();
            os = new FileOutputStream(file);
            wb.write(os);
            os.flush();
            success = true;
            os.close();
            wb.close();
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
        return success;
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static boolean isExternalStorageReadOnly(){
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable(){
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}
