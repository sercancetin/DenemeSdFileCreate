package com.interra.denemesdfilecreate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
    private TextView sdStatus;
    private Button startTest;
    private Button resetTest;
    private Button info;
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

    RadioGroup rd_gr_files;
    RadioButton radio_1kb,radio_1mb,radio_10mb;
    int radioBtn;

    SharedPreferenceHelper sharedPreferenceHelper;

    ProgressDialog progressDialogFileCreated;

    MemoryInfoHelper memoryInfoHelper;

    //spinner
    private Spinner spinner1;
    private String[] spinnerString={"%5","%10","%20","%40","%50","%60","%75","%90"};
    private ArrayAdapter<String> dataAdapterForSpinner;

    CpuTempDetail cpuTempDetail;
    /**
     * The onCreate method instantiates the UI elements as well as starts the test once the button is pressed
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        displayHelpMessage();
        resetHandler();

        if(checkForMemory())
        {
            sdStatus.setText("Memory Detected. Press run test to start. Before starting for the first time, make sure you view the readme by pressing Help/Info.");
            startTest.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v) {

                    if(fileNumber>0){
                        progressDialogFileCreated.setMessage("Files are being created. Please wait. "+0+"/"+fileNumber);
                        progressDialogFileCreated.show();

                        sdStatus.setText("Test started");
                        createStart = System.currentTimeMillis();

                        FileCreatorHelper fc = new FileCreatorHelper(fileNumber,MainActivity.this,getApplicationContext());
                        fc.setRadioValue((long)sharedPreferenceHelper.getEditorInt(FILE_SIZE));
                        Thread t1 = new Thread(fc);
                        t1.start();

                    }else {
                        Toast.makeText(getApplicationContext(),"0 Files",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else
        {
            sdStatus.setText("No Memory found");
        }
    }

    private void init(){
        startTest = (Button)findViewById(R.id.startTest);
        resetTest = (Button)findViewById(R.id.resetTest);
        info = (Button)findViewById(R.id.infoButton);
        sdStatus = (TextView)findViewById(R.id.sdStatus);
        rd_gr_files = findViewById(R.id.rd_gr_files);
        radio_1mb = findViewById(R.id.radio_1mb);
        radio_1kb = findViewById(R.id.radio_1kb);
        radio_10mb = findViewById(R.id.radio_10mb);
        spinner1 = findViewById(R.id.spinner1);

        //CpuDetail
        cpuTempDetail = new CpuTempDetail(this);
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
                System.out.println(" >> total extarnal memory>>"+ memoryInfoHelper.formatFileSize(getAvailableInternalMemorySize())+" number of files to be created "+fileNumber);
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
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                createHelpMessage();
            }
        });
    }

    /**
     * The createHelpMessage method creates and sets the text for the pop up box for the Info/Help viewer
     */
    public void createHelpMessage()
    {
        AlertDialog help = new AlertDialog.Builder(MainActivity.this).create();
        help.setTitle("Info");
        //help.setMessage("Running the test will simulate wear by generating files that take up 10% of the available RAM space, time it, delete the files, time the deletion, and finally list the results.
        // This can simulate heavy wear if used multiple times.
        // It is normal for the UI to freeze during the test due to the RAM used to write random blocks to the files.");
        help.setMessage("");
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
                deleteFiles();
                sdStatus.setText("Files deleted (if any). Press Run Stress Test to test again.");
            }
        });
    }

    /**
     * The deleteFiles method sequentially deletes the files and times how long it takes to delete them by
     * updating the delete timing variables using the current system time
     */
    public void deleteFiles()
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
                    setText(sdStatus,"Final results: "+fileNumber+" files took "+ getFileCreationTime()+
                            " to create, "+ getFileReadTime()+ " seconds to read, and "+getFileDeletionTime()+" seconds to delete.");
                }
            }

        }
        else
        {
            sdStatus.setText("Memory not inserted/mounted, so there are no files to be removed");
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
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        return (availableBlocks * blockSize);
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

            ReadFileHelper readFileHelper = new ReadFileHelper(this, this);
            Thread thread = new Thread(readFileHelper);
            thread.start();

        }
    }
    // Oluşturulan dosya sayısını veren interface
    @Override
    public void fileCreatedCount(long count) {

        setProgressDialogMessage("Files are being created. Please wait. "+count+"/"+fileNumber);
        /*if(count==fileNumber){
            //Bittiğinde
        }*/
    }

    private void setText(final TextView text,final String value){
        runOnUiThread(() -> text.setText(value));
    }

    //Ram available show ui
    private void timerRamUptade() {

        TextView txt_ram_used,totalram;
        txt_ram_used = findViewById(R.id.txt_ram_used);

        totalram = findViewById(R.id.totalram);
        totalram.setText("Total Ram\n"+memoryInfoHelper.getTotalRAM());


        Timer timerRamUpdate = new Timer();
        timerRamUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_ram_used.setText("Free Ram: "+memoryInfoHelper.formatFileSize(memoryInfoHelper.AvailableRAM(getApplicationContext()))+" / Total Ram: "+memoryInfoHelper.getTotalRAM());
                            totalram.setText("Free Stroge: "+memoryInfoHelper.formatFileSize(getAvailableInternalMemorySize())+" / Total Stroge: "+memoryInfoHelper.getTotalInternalMemorySize());
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
            deleteFiles();
        }
    }

    @Override
    public void fileReaderCount(long count) {
        setProgressDialogMessage("Files are read and stored in memory. Please wait.\nRead Files: "+count);
    }
}