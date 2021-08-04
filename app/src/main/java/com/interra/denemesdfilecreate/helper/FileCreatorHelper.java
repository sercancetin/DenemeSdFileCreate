package com.interra.denemesdfilecreate.helper;

/**
 * The FileCreator class creates 1 MB files with random blocks written. Based on the amount of
 * free space available on the SD card, a varying number of files will be created to take up 10%
 * of the free space on the card.
 */

import android.content.Context;

import com.interra.denemesdfilecreate.interfaces.GetFinishFileStatus;

import java.io.File;
import java.io.FileWriter;

import static com.interra.denemesdfilecreate.Constants.fileData10KB;
import static com.interra.denemesdfilecreate.Constants.fileData1KB;
import static com.interra.denemesdfilecreate.Constants.fileData5KB;


public class FileCreatorHelper implements Runnable
{
    //Data member for keeping track of whether or not the file creation has finished
    public boolean run_stop = false;
    long radioValue;
    long numFiles;
    Context context;
    GetFinishFileStatus getFinishFileStatus;

    /**
     * The FileCreator constructor calls the createFiles method and passes it the number of files to
     * create
     * @param numFiles the number of files to create
     */
    public FileCreatorHelper(long numFiles, GetFinishFileStatus getFinishFileStatus, Context context)
    {
        this.numFiles = numFiles;
        this.getFinishFileStatus = getFinishFileStatus;
        this.context = context;
    }

    /**
     * The createFiles method creates a set number of files with 1 MB of random data written to them into
     * the temporary directory /sdcard/stresstest/
     * @para numberOfFiles the number of files to be generated
     */
    public void createFiles()
    {
        long fileNumber = numFiles;

        try
        {
            String file_name = "";

            for(long x = 1; x <= fileNumber; x++)
            {
                file_name = "TestFile_" + (long) (x) + ".txt";
                //System.out.println(file_name+"--"+radioValue);

                long a = radioValue;
                if(a==1024){
                    writeFileOnInternalStorage(context,file_name, fileData1KB);
                } else if(a == 1024*5){
                    writeFileOnInternalStorage(context,file_name, fileData5KB);
                } else if(a == 1024*10){ //10 kb
                    writeFileOnInternalStorage(context,file_name, fileData10KB);
                }

                if(x == fileNumber)
                {
                    getFinishFileStatus.finishFileCreater(true);
                }
                getFinishFileStatus.fileCreatedCount(x);
                if(run_stop){
                    return;
                }
            }
        }
        catch(Exception io)
        {
            System.out.println(io);
        }
    }

    /**
     * The getStatus method checks whether or not the files have been created
     * @return the status of the test; true if finished, false if not
     */
    public void setStop()
    {
       run_stop = true ;
    }
    public void setRadioValue(long radioValue){
        this.radioValue = radioValue;
    }

    //thread runnable
    @Override
    public void run() {
        createFiles();
    }


    public void writeFileOnInternalStorage(Context context, String sFileName, String sBody){
        File dir = new File(context.getFilesDir(), "StressTestFiles");
        if(!dir.exists()){
            dir.mkdir();
        }

        try {
            File gpxfile = new File(dir, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
