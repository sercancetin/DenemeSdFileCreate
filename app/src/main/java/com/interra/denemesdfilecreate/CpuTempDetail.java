package com.interra.denemesdfilecreate;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.interra.denemesdfilecreate.helper.CpuMaxFreq;
import com.interra.denemesdfilecreate.helper.CpuStat;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

public class CpuTempDetail {
    Context context;
    CpuMaxFreq cpuMaxFreq;
    CpuStat cpuStat;
    TextView txtCpuFrequency,txtPassingTime,txtCpuTemp,txtCores;

    public CpuTempDetail(Context context){
        this.context = context;
        init();
    }
    private void init(){
        txtCpuFrequency = ((Activity)context).findViewById(R.id.txt_cpu_frequency);
        txtPassingTime = ((Activity)context).findViewById(R.id.txt_passing_time);
        txtCores = ((Activity)context).findViewById(R.id.txt_cores);
        cpuMaxFreq = new CpuMaxFreq();
        cpuStat = new CpuStat();

        txtCores.setText(String.valueOf("cores: "+getNumCores()));
        try {
            txtCpuFrequency.setText(String.valueOf(getReadCPUinfo()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpuUpdateValue();
    }


    public void cpuUpdateValue(){
        Timer timerRamUpdate = new Timer();
        timerRamUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                txtCpuTemp.setText(""+ cpuStat.getTotalCpuUsage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {

                }
            }
        }, 900, 2000);
    }
    public static int[] getCPUFrequencyCurrent() throws Exception {
        int[] output = new int[getNumCores()];
        for(int i=0;i<getNumCores();i++) {
            output[i] = CpuMaxFreq.readSystemFileAsInt("/sys/devices/system/cpu/cpu"+String.valueOf(i)+"/cpufreq/scaling_cur_freq");
        }
        return output;
    }
    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }


    //get Cpu Info
    private String getReadCPUinfo()
    {
        ProcessBuilder cmd;
        String result="";

        try{
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while(in.read(re) != -1){
                System.out.println(new String(re));
                result = result + new String(re);
            }
            in.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        return result;
    }

}
