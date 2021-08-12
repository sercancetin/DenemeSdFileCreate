package com.interra.denemesdfilecreate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.interra.denemesdfilecreate.helper.FileCreatorHelper;
import com.interra.denemesdfilecreate.helper.MemoryInfoHelper;
import com.interra.denemesdfilecreate.helper.ReadFileHelper;
import com.interra.denemesdfilecreate.helper.SharedPreferenceHelper;
import com.interra.denemesdfilecreate.interfaces.GetFinishFileReaderInterface;
import com.interra.denemesdfilecreate.interfaces.GetFinishFileStatus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.interra.denemesdfilecreate.helper.SharedPreferenceHelper.FILE_SIZE;

public class MainActivity extends AppCompatActivity implements GetFinishFileStatus, GetFinishFileReaderInterface {
    //UI element Data members
    private TextView txtFileStatus;
    private LinearLayout lytSettingsWrapper;
    private Button startTest;
    private Button resetTest;
    private Button btnStop;
    private ProgressBar progressBarRam;
    public static long fileNumber;
    private boolean done = false;
    private boolean x = false;
    //Test timing data members
    private long createStart;
    private long createEnd;
    private long deleteStart;
    private long deleteEnd;
    private long readStart;
    private long readEnd;

    RWStatus rwStatus;
    CpuGraphDetail cpuGraphDetail;
    FileCreatorHelper fileCreatorHelper;
    ReadFileHelper readFileHelper;

    RadioGroup rd_gr_files;
    RadioButton radio_1kb,radio_1mb,radio_10mb;

    SharedPreferenceHelper sharedPreferenceHelper;

    ProgressDialog progressDialogFileCreated;

    MemoryInfoHelper memoryInfoHelper;

    //spinner
    private Spinner spinner1;
    private String[] spinnerString={"%5","%10","%20","%40","%50","%60","%75","%90"};
    private ArrayAdapter<String> dataAdapterForSpinner;
    /**
     * The onCreate method instantiates the UI elements as well as starts the test once the button is pressed
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fullScreenActivity();
        setContentView(R.layout.activity_main);
        init();
        displayHelpMessage();
        resetHandler();

        if(checkForMemory())
        {
            startTest.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {

                    if(fileNumber>0){
                        txtFileStatus.setVisibility(View.VISIBLE);
                        txtFileStatus.setText("Files are being created. Please wait. \n"+0+"/"+fileNumber);
                        lytSettingsWrapper.setVisibility(View.GONE);
                        btnStop.setVisibility(View.VISIBLE);
                        cpuGraphDetail.timeStart();
                        /*progressDialogFileCreated.setMessage("Files are being created. Please wait. "+0+"/"+fileNumber);
                        progressDialogFileCreated.show();*/
                        createStart = System.currentTimeMillis();
                        fileCreatorHelper = new FileCreatorHelper(fileNumber,MainActivity.this,getApplicationContext());
                        fileCreatorHelper.setRadioValue((long)sharedPreferenceHelper.getEditorInt(FILE_SIZE));
                        Thread t1 = new Thread(fileCreatorHelper);
                        t1.start();
                        rwStatus = RWStatus.WRITE;
                    }else {
                        Toast.makeText(getApplicationContext(),"0 Files",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else
        {
            createDialogMessage("No Memory found");
        }
    }

    private void init(){
        startTest = (Button)findViewById(R.id.startTest);
        txtFileStatus = findViewById(R.id.txt_file_status);
        lytSettingsWrapper = findViewById(R.id.lyt_settings_wrapper);
        resetTest = (Button)findViewById(R.id.resetTest);
        btnStop = (Button)findViewById(R.id.btn_stop);
        progressBarRam = findViewById(R.id.progress_bar_ram);
        rd_gr_files = findViewById(R.id.rd_gr_files);
        radio_1mb = findViewById(R.id.radio_1mb);
        radio_1kb = findViewById(R.id.radio_1kb);
        radio_10mb = findViewById(R.id.radio_10mb);
        spinner1 = findViewById(R.id.spinner1);
        //CpuDetail
        cpuGraphDetail = new CpuGraphDetail(this);
        //progressDialog
        progressDialogFileCreated = new ProgressDialog(MainActivity.this);
        progressDialogFileCreated.setCancelable(false);

        //Ram Usage
        memoryInfoHelper = new MemoryInfoHelper(this);
        timerRamUptade();

        sharedPreferenceHelper = new SharedPreferenceHelper(this,FILE_SIZE);
        rd_gr_files.setOrientation(LinearLayout.HORIZONTAL);


        dataAdapterForSpinner = new ArrayAdapter<String>(this,R.layout.spinner_item,spinnerString);
        spinner1.setAdapter(dataAdapterForSpinner);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getSelectedItem().toString().equals(spinnerString[0])){
                    fileNumber = calculateNumFiles(0.00000005,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[0]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[1])){
                    fileNumber = calculateNumFiles(0.00000010,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[1]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[2])){
                    fileNumber = calculateNumFiles(0.00000020,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[2]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[3])){
                    fileNumber = calculateNumFiles(0.00000040,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[3]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[4])){
                    fileNumber = calculateNumFiles(0.00000050,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[4]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[5])){
                    fileNumber = calculateNumFiles(0.00000060,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[5]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[6])){
                    fileNumber = calculateNumFiles(0.00000075,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[6]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }else if(parent.getSelectedItem().toString().equals(spinnerString[7])){
                    fileNumber = calculateNumFiles(0.00000090,radiBtnMb());
                    Toast.makeText(getApplicationContext(),spinnerString[7]+" "+fileNumber+" files",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rd_gr_files.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.radio_1kb) {
                    sharedPreferenceHelper.setEditorInt(FILE_SIZE,1024);
                    Toast.makeText(getApplicationContext(), "Choice: 1KB",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_1mb) {
                    sharedPreferenceHelper.setEditorInt(FILE_SIZE,1024*5);
                    Toast.makeText(getApplicationContext(), "Choice: 5KB",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_10mb) {
                    sharedPreferenceHelper.setEditorInt(FILE_SIZE,1024*10);
                    Toast.makeText(getApplicationContext(), "Choice: 10KB",
                            Toast.LENGTH_SHORT).show();
                }
                fileNumber = calculateNumFiles(radioBtnSpinnerChoose(),radiBtnMb());
                System.out.println("filenumberradio>>"+fileNumber);
            }
        });
        getRadioBtnEnabled();

    }

    private void getRadioBtnEnabled(){
        int a = sharedPreferenceHelper.getEditorInt(FILE_SIZE);
        int b = 1024*10;
        if(a==1024){
            radio_1kb.setChecked(true);
        } else if(a == 1024*5){
            radio_1mb.setChecked(true);
        } else if(a == b){
            radio_10mb.setChecked(true);
        }
    }


    private double radiBtnMb(){
        if(sharedPreferenceHelper.getEditorInt(FILE_SIZE)==1024){
            return 0.001;
        } else if(sharedPreferenceHelper.getEditorInt(FILE_SIZE)==1024*5){
            return 0.01;
        } else if(sharedPreferenceHelper.getEditorInt(FILE_SIZE)==1024*10){
            return 0.005;
        } else {
            return 0.001;
        }
    }
    private double radioBtnSpinnerChoose(){
        if(spinner1.getSelectedItem().toString().equals(spinnerString[0])){
            return 0.00000005;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[1])){
            return 0.00000010;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[2])){
            return 0.00000020;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[3])){
            return 0.00000040;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[4])){
            return 0.00000050;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[5])){
            return 0.00000060;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[6])){
            return 0.00000075;
        }else if(spinner1.getSelectedItem().toString().equals(spinnerString[7])){
            return 0.00000090;
        } else{
            return 0;
        }
    }
    /**
     * The displayHelpMessage helper method creates a new onClicker listener for the Info/Help button
     */
    public void displayHelpMessage()
    {
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                stopRWThread();
                deleteFiles(true);
            }
        });
    }

    /**
     * The createHelpMessage method creates and sets the text for the pop up box for the Info/Help viewer
     */
    public void createDialogMessage(String message)
    {
        AlertDialog help = new AlertDialog.Builder(this).create();
        help.setTitle("Info");
        //help.setMessage("Running the test will simulate wear by generating files that take up 10% of the available RAM space, time it, delete the files, time the deletion, and finally list the results.
        // This can simulate heavy wear if used multiple times.
        // It is normal for the UI to freeze during the test due to the RAM used to write random blocks to the files.");
        help.setMessage(message);
        help.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        help.show();
    }

    /**
     * The resetHandler method creates a new onClick listener for the Reset button which deletes any temporary test files (if any are found)
     */
    public void resetHandler()
    {
        resetTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFiles(true);
                createDialogMessage("Files deleted (if any). Press Run Stress Test to test again.");
            }
        });
    }

    /**
     * The deleteFiles method sequentially deletes the files and times how long it takes to delete them by
     * updating the delete timing variables using the current system time
     */
    public void deleteFiles(boolean cl)//message don't show: true
    {
        deleteStart = System.currentTimeMillis();
        File dir = new File(getFilesDir(),"/StressTestFiles/");
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
                if(i==children.length-1){
                    deleteEnd = System.currentTimeMillis();
                    if(!cl){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createDialogMessage("Final results: "+fileNumber+" files took "+ getFileCreationTime()+
                                        " to create, "+ getFileReadTime()+ " seconds to read, and "+getFileDeletionTime()+" seconds to delete.");
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtFileStatus.setVisibility(View.GONE);
                            lytSettingsWrapper.setVisibility(View.VISIBLE);
                            btnStop.setVisibility(View.GONE);
                        }
                    });
                }
            }

        }
        else
        {
            createDialogMessage("Memory not inserted/mounted, so there are no files to be removed");
        }
    }

    /**
     * The checkForSD method checks to see if there is a SD card inserted and mounted
     * @return true if there is an SD card detected
     * @return false if there is not an SD card detected
     */
    /*public boolean checkForSD()
    {
        *//*if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && Environment.isExternalStorageRemovable())
        {
            return true;
            return true;
        }
        else
        {
            return false;
        }*//*

    }*/
    public boolean checkForMemory()
    {
        String extStrogeState = Environment.getExternalStorageState();
        return extStrogeState.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * The getAvailableSDSpace method calculates how much free space is available on the card in MB
     * @return the number of MB available on the SD card
     */
    public long getAvailableSDSpace()
    {
        StatFs sdstat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return (long) sdstat.getBlockSize() * (long) sdstat.getAvailableBlocks();

    }
    public long getAvailableUsbMemory(){
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        return statFs.getAvailableBlocks() * statFs.getBlockSize();
    }


    /**
     * The calculateNumFiles method calculates the number of files to be created by taking the MB of the available
     * SD card space and multiplying it by .10 to get 10% of it
     * @return
     */
    /*public long calculateNumFiles()
    {
        //long spaceToUse = (long) (getAvailableSDSpace() * .0000001);
        long spaceToUse = (long) (getAvailableInternalMemorySize() * .00001);
        return spaceToUse;
    }*/
    public long calculateNumFiles(double percentile,double mb)
    {
        long spaceToUse = (long) ((long) (memoryInfoHelper.AvailableRAM(getApplicationContext()) * percentile)/mb);
        return spaceToUse;
    }

    /**
     * The getFileCreation time gets the time it took to create the files in number of minutes
     * @return the number of minutes it took to get the files created
     */
    public double getFileCreationTime()
    {
        return (createEnd - createStart) / 1000;
    }

    /**
     * The getFileDeletionTime gets the time it took to delete the files in number of seconds
     * @return the number of seconds it took to get the files deleted
     */
    public double getFileDeletionTime()
    {
        return (deleteEnd - deleteStart) / 1000;
    }

    /*
     *getFileReadTime() takes seconds to read files
     * @returns the number of seconds it takes for files to be read
     */
    public double getFileReadTime(){
        return (readEnd - readStart) / 1000;
    }

    //Dosya oluşturma işlemi bitmesini haber veren interface
    @Override
    public void finishFileCreater(boolean a) {
        if(a){
            createEnd = System.currentTimeMillis();
            readStart = System.currentTimeMillis();

            readFileHelper = new ReadFileHelper(this, this);
            Thread thread = new Thread(readFileHelper);
            thread.start();
            rwStatus = RWStatus.READ;
        }
    }
    // Oluşturulan dosya sayısını veren interface
    @Override
    public void fileCreatedCount(long count) {

        setText(txtFileStatus,"Files are being created. Please wait. "+count+"/"+fileNumber);
        //setProgressDialogMessage("Files are being created. Please wait. "+count+"/"+fileNumber);
        /*if(count==fileNumber){
            //Bittiğinde
        }*/
    }

    private void setText(final TextView text,final String value){
        runOnUiThread(() -> text.setText(value));
    }

    //Ram available show ui
    private void timerRamUptade() {

        TextView txt_total_stroge,txt_stroge_use,txt_total_ram,txt_ram_used;
        txt_total_stroge = findViewById(R.id.txt_total_stroge);
        txt_stroge_use = findViewById(R.id.txt_stroge_use);
        txt_total_ram = findViewById(R.id.txt_total_ram);
        txt_ram_used = findViewById(R.id.txt_ram_used);

        txt_total_stroge.setText("Total: "+memoryInfoHelper.getTotalInternalMemorySize());
        txt_total_ram.setText("Total: "+memoryInfoHelper.getTotalRAM());

        Timer timerRamUpdate = new Timer();
        timerRamUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_ram_used.setText("Free: "+memoryInfoHelper.formatFileSize(memoryInfoHelper.AvailableRAM(getApplicationContext())));
                            txt_stroge_use.setText("Free: "+memoryInfoHelper.formatFileSize(memoryInfoHelper.getAvailableInternalMemorySize()));
                        }
                    });
                } catch (Exception e) {

                }
            }
        }, 900, 2000);
    }

    public void setProgressDialogMessage(String message) {
        if (progressDialogFileCreated != null && progressDialogFileCreated.isShowing()) {
            ((Activity) this).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialogFileCreated.setMessage(message);
                }
            });
        }
    }

    @Override
    public void finishFileReader(boolean a) {
        if(a){
            progressDialogFileCreated.dismiss();
            readEnd = System.currentTimeMillis();
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    cpuGraphDetail.timeReset();
                }
            });
            rwStatus = RWStatus.NONE;
            deleteFiles(false);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        cpuGraphDetail.graphStatus(false);
    }
    private void stopRWThread(){
        if(rwStatus==RWStatus.WRITE){
            fileCreatorHelper.setStop();
        }else if(rwStatus == RWStatus.READ) {
            readFileHelper.setReadStop();
        }
    }
    @Override
    public void fileReaderCount(long count) {
        setText(txtFileStatus,"Files are read and stored in memory. Please wait.\nRead Files: "+count+"/"+fileNumber);
        //setProgressDialogMessage("Files are read and stored in memory. Please wait.\nRead Files: "+count);
    }
    public void fullScreenActivity(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
    }
    public enum RWStatus{
        READ,WRITE,NONE;
    }
}