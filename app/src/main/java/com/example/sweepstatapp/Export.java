package com.example.sweepstatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.app.Activity;
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

        Sheet sheet = wb.createSheet("data");
        Row row = sheet.createRow(0);
        c = row.createCell(0);
        c.setCellValue("Voltage");
        c = row.createCell(1);
        c.setCellValue("Current");

        for (int i = 0; i < voltage.length; i++){
            row = sheet.createRow(i+1);
            c = row.createCell(0);
            c.setCellValue(voltage[i]);
            c = row.createCell(1);
            c.setCellValue(current[i]);
        }

        sheet.setColumnWidth(0, 10*256);
        sheet.setColumnWidth(1, 10*256);

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
