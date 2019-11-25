package com.example.sweepstatapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
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

public class ExperimentRuntime extends AppCompatActivity {
    TextView initialVoltage, highVoltage, lowVoltage, finalVoltage, polarity, scanRate,
    sampleInterval, sensitivity, sweepSegments;
    Boolean autoSens, finalE, auxRecord;
    String loadFailed = "Not currently enabled";
    double lowVolt, highVolt;
    boolean drawing = false;
    String[][] parameters;
    private Graph graph = null;
    private int numOfPoints = 10;
    private long interval = 20;
    SharedPreferences saved;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    double[] voltage, current;
    final static String SWEEPSTAT = "SweepStat";
    final static File DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SWEEPSTAT);
    final static File DATA_DIR = new File(DIR, "Data");
    final static File CONFIGURATION_DIR = new File(DIR, "Configuration");
    final static File TEMP_DIR = new File(DATA_DIR,"temp");
    static final int LOCAL = 0;
    static final int EXPORT = 1;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeConnectionService.GATT_WRITE_MESSAGE.equals(action) && graph != null) {
                String[] data = intent.getStringExtra(BluetoothLeConnectionService.EXTRA_DATA).split(",");
                graph.putData(Double.parseDouble(data[1].substring(data[1].indexOf(":")+1)), Double.parseDouble(data[2].substring(data[2].indexOf(":")+1, data[2].length()-1)));
            } else if(BluetoothLeConnectionService.FINISH_DRAWING.equals(action) && graph != null){
                drawing = false;
            }
        }
    };
    private final IntentFilter filter = new IntentFilter();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.experiment_runtime);
        saved = this.getSharedPreferences("com.example.sweepstatapp", Context.MODE_PRIVATE);
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        drawing = false;
        filter.addAction(BluetoothLeConnectionService.ACTION_DATA_AVAILABLE);
        registerReceiver(receiver, filter);

        GraphView graphView = findViewById(R.id.graph);
        Viewport viewport = graphView.getViewport();
        viewport.setScrollable(true);
        viewport.setScrollableY(true);
        viewport.setScalable(true);
        viewport.setScalableY(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinX(-1);
        viewport.setMaxX(1);

        initialVoltage = findViewById(R.id.initialVoltage);
        highVoltage = findViewById(R.id.highVoltage);
        lowVoltage = findViewById(R.id.lowVoltage);
        finalVoltage = findViewById(R.id.finalVoltage);
        polarity = findViewById(R.id.polarity);
        scanRate = findViewById(R.id.scanRate);
        sweepSegments = findViewById(R.id.sweepSegments);
        sampleInterval = findViewById(R.id.sampleInterval);
//      quietTime = findViewById(R.id.quietTime);
        sensitivity = findViewById(R.id.sensitivity);
        autoSens = false;
        finalE = false;
        auxRecord = false;

        Bundle data = getIntent().getExtras();
        if (data != null && data.getString("loadFile") != null) {
            String filePath = data.getString("loadFile");
            loadData(filePath);
        } else {
            if (saved != null) {
                initialVoltage.setText(saved.getString(AdvancedSetup.INITIAL_VOLTAGE, loadFailed));

                String highV = saved.getString(AdvancedSetup.HIGH_VOLTAGE, loadFailed);
                String lowV = saved.getString(AdvancedSetup.LOW_VOLTAGE, loadFailed);
                highVoltage.setText(highV);
                lowVoltage.setText(lowV);
                if (!highV.equals("") && !lowV.equals("")) {
                    highVolt = Double.parseDouble(highV);
                    lowVolt = Double.parseDouble(lowV);
                    graph = new Graph(graphView, lowVolt, highVolt);
                } else
                    graph = new Graph(graphView);

                finalVoltage.setText(saved.getString(AdvancedSetup.FINAL_VOLTAGE, loadFailed));
                polarity.setText(saved.getString(AdvancedSetup.POLARITY_TOGGLE, loadFailed));
                scanRate.setText(saved.getString(AdvancedSetup.SCAN_RATE, loadFailed));
                sweepSegments.setText(saved.getString(AdvancedSetup.SWEEP_SEGS, loadFailed));
                sampleInterval.setText(saved.getString(AdvancedSetup.SAMPLE_INTEVAL, loadFailed));
                quietTime.setText(saved.getString(AdvancedSetup.QUIET_TIME, loadFailed));
                sensitivity.setText(saved.getString(AdvancedSetup.SENSITIVITY, loadFailed));
                autoSens = saved.getBoolean(AdvancedSetup.IS_AUTOSENS, false);
                finalE = saved.getBoolean(AdvancedSetup.IS_FINALE, true);
                auxRecord = saved.getBoolean(AdvancedSetup.IS_AUX_RECORDING, false);
                if (autoSens)
                    findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
                if (finalE)
                    findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
                if (auxRecord)
                    findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);
                setParameters();
            } else {
                Toast.makeText(this, "Failed to load saved inputs!", Toast.LENGTH_SHORT).show();
                graph = new Graph(graphView);
            }
        }
    }

    public void onClick(View view){
        if (view.getId() == R.id.runExperiment){
//           graph.drawOnFakeData();
            if (drawing)
                return;
            drawing = true;
            Intent intent = new Intent();
            intent.setAction(BluetoothLeConnectionService.GATT_WRITE_MESSAGE);
            intent.setClass(getApplicationContext(), BluetoothLeConnectionService.class);
            intent.putExtra("message", ".");
            startService(intent);
        } else if (view.getId() == R.id.local){
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
                return;
            verifyStoragePermissions(this);
            DATA_DIR.mkdirs();
            showEnterFileName(this, LOCAL);
        } else if (view.getId() == R.id.export){
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
                return;
            verifyStoragePermissions(this);
            DATA_DIR.mkdirs();
            showEnterFileName(this, EXPORT);
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

    protected void loadData(String filePath){
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(DATA_DIR, filePath));
            Workbook wb = new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            Cell c;
            int rowIndex = 0;
            initialVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            highVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            lowVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            finalVoltage.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            polarity.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            scanRate.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sweepSegments.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sampleInterval.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            quietTime.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            sensitivity.setText(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            autoSens = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            finalE = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            auxRecord = Boolean.parseBoolean(sheet.getRow(rowIndex++).getCell(1).getStringCellValue());
            if(autoSens)
                findViewById(R.id.autoSensEnabled).setVisibility(View.VISIBLE);
            if(finalE)
                findViewById(R.id.finalEEnabled).setVisibility(View.VISIBLE);
            if(auxRecord)
                findViewById(R.id.auxRecordingEnabled).setVisibility(View.VISIBLE);

            rowIndex += 2;
            Row row = sheet.getRow(rowIndex);
            GraphView graphView = findViewById(R.id.graph);
            graph = new Graph(graphView, Double.parseDouble(lowVoltage.getText()+""), Double.parseDouble(highVoltage.getText()+""));
//            while(row != null && !row.getCell(1).getStringCellValue().equals("")){
            while(row != null){
                double x = row.getCell(0).getNumericCellValue();
//                double y = Double.parseDouble(row.getCell(1).getStringCellValue());
                double y = row.getCell(1).getNumericCellValue();
                graph.putData(x,y);
                rowIndex++;
                row = sheet.getRow(rowIndex);
            }
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

    private String[][] setParameters(){
        parameters = new String[2][13];
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

        parameters[1][0] = initialVoltage.getText()+"";
        parameters[1][1] = highVoltage.getText()+"";
        parameters[1][2] = lowVoltage.getText()+"";
        parameters[1][3] = finalVoltage.getText()+"";
        parameters[1][4] = polarity.getText()+"";
        parameters[1][5] = scanRate.getText()+"";
        parameters[1][6] = sweepSegments.getText()+"";
        parameters[1][7] = sampleInterval.getText()+"";
        parameters[1][8] = quietTime.getText()+"";
        parameters[1][9] = sensitivity.getText()+"";
        parameters[1][10] = autoSens+"";
        parameters[1][11] = finalE+"";
        parameters[1][12] = auxRecord+"";
        return parameters;
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

                            file = new File(DATA_DIR, fileName + ".xls");
                            if (file.exists()){
                                int i = 1;
                                do{
                                    file = new File(DATA_DIR, fileName + '(' + i +").xls");
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

                        createExcelFile(file, action);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    protected File createExcelFile(File file, int action){
        if (action < LOCAL || action > EXPORT)
            return null;

        File retVal = null;

        Workbook wb = new HSSFWorkbook();
        Cell c;
        int nextRow = 0;

        Sheet sheet = wb.createSheet("data");
        Row row;

        if (parameters == null)
            setParameters();
        for (int i = 0; i < parameters[1].length; i++, nextRow++) {
            row = sheet.createRow(nextRow);
            c = row.createCell(0);
            c.setCellValue(parameters[0][i]);
            c = row.createCell(1);
            c.setCellValue(parameters[1][i]);
        }

        sheet.createRow(nextRow);
        nextRow++;
        row = sheet.createRow(nextRow);
        c = row.createCell(0);
        c.setCellValue("Voltage");
        c = row.createCell(1);
        c.setCellValue("Current");
        nextRow++;

        DataPoint[] dataPoint = graph.getFullData().toArray(new DataPoint[0]);
        voltage = new double[dataPoint.length];
        current = new double[dataPoint.length];
        for (int i = 0; i < dataPoint.length; i++){
            voltage[i] = dataPoint[i].getX();
            current[i] = dataPoint[i].getY();
        }
        for (int i = 0; i < voltage.length; i++){
            row = sheet.createRow(i+nextRow);
            c = row.createCell(0);
            c.setCellValue(voltage[i]);
            c = row.createCell(1);
            c.setCellValue(current[i]);
//            c.setCellValue(String.format("%3.2E", current[i]));
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

    public void back(View view){
        this.finish();
    }

    public void finish(){
        unregisterReceiver(receiver);
        super.finish();
    }
}
