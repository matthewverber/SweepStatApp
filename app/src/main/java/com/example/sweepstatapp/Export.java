package com.example.sweepstatapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

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
    final static String SWEEPSTAT = "SweepStat";
    final File DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SWEEPSTAT);
    final File TEMP_DIR = new File(DIR,"temp");
    int action = -1;
    private static final int LOCAL = 0;
    private static final int EXPORT = 1;
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
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
            return;
        verifyStoragePermissions(this);
        DIR.mkdirs();
        if (view.getId() == R.id.local){
            action = LOCAL;
            showEnterFileName(Export.this, action);
        } else if (view.getId() == R.id.export){
            action = EXPORT;
            showEnterFileName(Export.this, action);
        }
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
                        if (action < LOCAL || action > EXPORT)
                            return;

                        String fileName = String.valueOf(fileNameEditText.getText());
                        File file = null;
                        if (action == LOCAL){
                            if (fileName == null || fileName.equals(""))
                                return;

                            file = new File(DIR, fileName + ".xls");
                            if (file.exists()){
                                int i = 1;
                                do{
                                    file = new File(DIR, fileName + '(' + i +").xls");
                                    i++;
                                } while (file.exists());
                            }
                        } else if (action == EXPORT){
                            if (TEMP_DIR.exists())
                                deleteDir(TEMP_DIR);
                            else
                                TEMP_DIR.mkdirs();

                            file = new File(TEMP_DIR,fileName+".xls");
                        }

                        createExcelFile(file);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    protected File createExcelFile(File file){
        if (action < LOCAL || action > EXPORT)
            return null;

        File retVal = null;

        Workbook wb = new HSSFWorkbook();
        Cell c;
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
            parameters[1][11] = saved.getBoolean(AdvancedSetup.IS_FINALE, false)+"";
            parameters[1][12] = saved.getBoolean(AdvancedSetup.IS_AUX_RECORDING, false)+"";

            for (int i = 0; i < parameters[1].length; i++, nextRow++){
                row = sheet.createRow(nextRow);
                c = row.createCell(0);
                c.setCellValue(parameters[0][i]);
                c = row.createCell(1);
                c.setCellValue(parameters[1][i]);
            }
        }

        sheet.createRow(nextRow);
        nextRow++;
        row = sheet.createRow(nextRow);
        c = row.createCell(0);
        c.setCellValue("Voltage");
        c = row.createCell(1);
        c.setCellValue("Current");
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

        FileOutputStream os = null;
        try {
            if (!(action == EXPORT && file.exists())){
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
        if (action == EXPORT) {
            Uri path = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Choose where to export"));
        }
        return retVal;
    }

    private void deleteDir(File dir){
        for (File file: dir.listFiles()) {
            if (file.isDirectory()){
                deleteDir(file);
                file.delete();
            } else {
                file.delete();
            }
        }
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

    public static boolean isExternalStorageReadOnly(){
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable(){
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}
