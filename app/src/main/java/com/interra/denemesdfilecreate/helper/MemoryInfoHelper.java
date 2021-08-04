package com.interra.denemesdfilecreate.helper;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.ACTIVITY_SERVICE;

public class MemoryInfoHelper {
    private static final int MB = 1024 * 1024;
    private static MemoryInfoHelper sInstance;


    private ActivityManager.MemoryInfo mMemoryInfo = new ActivityManager.MemoryInfo();
    private ActivityManager mActivityManager;

    public MemoryInfoHelper(Context context) {
        mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        update();
    }

    public static MemoryInfoHelper get(Context context) {
        if (sInstance == null) {
            sInstance = new MemoryInfoHelper(context);
        }
        return sInstance;
    }

    public void update() {
        mActivityManager.getMemoryInfo(mMemoryInfo);
    }

    public float getAvailableMemory() {
        return mMemoryInfo.availMem / MB;
    }

    public boolean isLowMemory() {
        return mMemoryInfo.lowMemory;
    }

    public long getMemoryThreshold() {
        return mMemoryInfo.threshold / MB;
    }

    public float getRamSize() {
        long memory;
        if (Build.VERSION.SDK_INT >= 16) {
            memory = mMemoryInfo.totalMem;
        } else {
            memory = readRamSizeFromSystem();
        }
        return memory / MB;
    }

    public float getFreeMemoryPercentage() {
        return (getAvailableMemory() / getRamSize()) * 100;
    }

    private static long readRamSizeFromSystem() {
        try {
            RandomAccessFile memFile = new RandomAccessFile("/proc/meminfo", "r");
            Pattern pattern = Pattern.compile("[0-9]+");

            memFile.close();
            String memory = pattern.matcher(memFile.readLine()).group();
            return Long.parseLong(memory) * 1024;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    @Override
    public String toString() {
        return "MemoryUtils{" +
                "availMem=" + mMemoryInfo.availMem +
                ", lowMemory=" + mMemoryInfo.lowMemory +
                ", threshold=" + mMemoryInfo.threshold +
                ", totalMem=" + getRamSize() +
                '}';
    }

    public static boolean isMemoryAvailable() {
        float freeMemoryPercent = 100 - (Runtime.getRuntime().totalMemory() / (float) Runtime.getRuntime().maxMemory()) * 100;
        if (freeMemoryPercent > 10) {
            return true;
        }
        return false;
    }
    public long getUsedMemorySize(Activity activity) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) Objects.requireNonNull(activity).getSystemService(ACTIVITY_SERVICE);
        assert activityManager != null;
        activityManager.getMemoryInfo(mi);
        return mi.availMem / 1048576L;
    }
    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            try {
                reader = new RandomAccessFile("/proc/meminfo", "r");
                load = reader.readLine();
            } catch (Exception e) {

            }

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);
                // System.out.println("Ram : " + value);
            }
            try {
                reader.close();
            } catch (Exception e) {

            }

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Streams.close(reader);
        }

        return lastValue;
    }
    public long AvailableRAM(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        return mi.availMem;
    }

    public long calculateTotalRAM() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;

        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");

            //total Memory
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }
    public String formatFileSize(long size) {
        String hrSize;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat("TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat("GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat("MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat("KB");
        } else {
            hrSize = dec.format(b).concat("Bytes");
        }

        return hrSize;
    }
}
