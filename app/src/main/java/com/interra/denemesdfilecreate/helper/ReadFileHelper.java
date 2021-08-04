package com.interra.denemesdfilecreate.helper;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.interra.denemesdfilecreate.interfaces.GetFinishFileReaderInterface;
import com.interra.denemesdfilecreate.model.FileData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadFileHelper implements Runnable{
    ArrayList<FileData> fileDataList;
    HashMap<FileReader,FileData > hashMap = new HashMap<>();
    String[] tempDataString = null;
    Context context;
    File dir;
    File gpxfile;
    boolean read_stop=false;
    GetFinishFileReaderInterface finishFileReaderInterface;
    public ReadFileHelper(Context context, GetFinishFileReaderInterface finishFileReaderInterface) {
        this.context = context;
        fileDataList = new ArrayList<>();
        dir = new File(context.getFilesDir(), "StressTestFiles");//Klasör yolu, Klasör adı
        this.finishFileReaderInterface =finishFileReaderInterface;
    }

    public void readFileOnInternalStroge(String rFileName){
        try{
            if(!dir.exists()){
               return;
            }
            gpxfile = new File(dir, rFileName);
            FileReader fr=new FileReader(gpxfile);


            BufferedReader bufferedReader = new BufferedReader(fr);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            int s = 0;
            while (line!=null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
                tempDataString[++s]=line;
            }
            /*StringBuilder content = new StringBuilder();
            int i;
            while((i=fr.read())!=-1){
                content.append((char)i);
            }
            FileData fileData = new FileData(content.toString());
            fileDataList.add(fileData);*/
            FileData fileData = new FileData(line);
            fileDataList.add(fileData);
            //hashMap.put(fr,fileData);
            //Log.d("KKK", "x of map : "+hashMap.size()+ " Key model hash :"+fr.hashCode());

            fr.close();
        }catch (Exception e){

        }
    }

    @Override
    public void run() {
        File dir = new File(context.getFilesDir(),"/StressTestFiles/");
        List<File>  files = getListFiles(dir);
        try {
            tempDataString = new String[files.size()*1024];
        }catch (Exception e){
            tempDataString = new String[files.size()*100];
        }

        long readFileCount=0;
        for (File file: files) {
            readFileOnInternalStroge(file.getName());
            finishFileReaderInterface.fileReaderCount(++readFileCount);
            if(readFileCount==files.size()){
                finishFileReaderInterface.finishFileReader(true);
            }
            if(read_stop){
                return;
            }
        }
    }
    public void setReadStop(){
        read_stop = true;
    }
    //Dizindeki dosyaları listeler
    List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    /*
    Uygulada ve yerel yığın tarafından kullanılan geçerli kullanılan
    belleğin çıktısını almak için uygulamamda bu kodu kullanıyorum:
     */
    public static void logHeap(Class clazz) {
        String APP ="authorize";
        Double allocated = (double) Debug.getNativeHeapAllocatedSize() /new Double((1048576));
        Double available = (double) Debug.getNativeHeapSize() /1048576.0;
        Double free = (double) Debug.getNativeHeapFreeSize() /1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d(APP, "debug. =================================");
        Log.d(APP, "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free) in [" + clazz.getName().replaceAll("com.myapp.android.","") + "]");
        Log.d(APP, "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
        System.gc();
        System.gc();
    }

    public void readFileLine(String fileName){
        StringBuilder sb = new StringBuilder();
        try{
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
        } catch(OutOfMemoryError om){
            om.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        System.out.println(sb.toString());
    }
}
