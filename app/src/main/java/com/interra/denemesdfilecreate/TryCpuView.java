package com.interra.denemesdfilecreate;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TryCpuView extends AppCompatActivity {
    final int DURATION = 600;
    ArrayList<Entry> yVals;
    long currentTime = 0;
    long savedTime = 0;
    private boolean isThreadRun = true;
    boolean isChart = false;
    LineChart mChart;
    static float avgCpuValue = 0;
    static float maxAvgCpuValue = 0;
    boolean keepReading = true;
    String pattern = "([0-9]{6,7})";
    Pattern pat = Pattern.compile(pattern);
    TextView txtCpuFrequency, txtPassingTime, txtMaxFreq, txtCores;
    long start;
    int getCores = 0;
    HashMap<Integer, String[]> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_cpu_view);

        // Get current time
        start = System.currentTimeMillis();

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor TempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(temperatureSensor, TempSensor, SensorManager.SENSOR_DELAY_NORMAL);
        init();

        hashMap = getCpuPath(getCores);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (keepReading) {
                        Thread.sleep(DURATION);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isChart) {
                                    initChart();
                                    return;
                                }
                                try {
                                    avgCpuValue = averageCpuData(getCPUFrequencyCurrent(getCores, hashMap));
                                    if(avgCpuValue>maxAvgCpuValue){
                                        maxAvgCpuValue = avgCpuValue;
                                        txtMaxFreq.setText("Max avg freq: " + (int)maxAvgCpuValue+ " Mhz");
                                    }
                                    updateData(avgCpuValue, 0);
                                    Log.d("authorize", "" + avgCpuValue);
                                    txtCpuFrequency.setText("Current avg freg: " + (int)avgCpuValue + " MHZ");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    private void init() {
        txtCpuFrequency = findViewById(R.id.txt_cpu_frequency);
        txtPassingTime = findViewById(R.id.txt_passing_time);
        txtCores = findViewById(R.id.txt_cores);
        txtMaxFreq = findViewById(R.id.txt_max_freq);
        getCores = getNumCores();
        txtCores.setText("Total cores: " + getCores);
        timeThread();
    }

    private void initChart() {
        if (mChart != null) {
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                savedTime++;
                isChart = true;
            }
        } else {
            currentTime = new Date().getTime();
            mChart = findViewById(R.id.chart1);
            mChart.setViewPortOffsets(50, 20, 5, 60);
            // no description text
            //mChart.setDescription("a");
            // enable touch gestures
            mChart.setTouchEnabled(true);
            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(false);
            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);
            //mChart.setDrawGridBackground(false);
            //mChart.setMaxHighlightDistance(400)
            mChart.setEnabled(false);
            mChart.setBackgroundColor(getResources().getColor(R.color.black));
            XAxis x = mChart.getXAxis();
            x.setLabelCount(8, false);
            x.setEnabled(true);
            //x.setTypeface(tf);
            x.setTextColor(getResources().getColor(R.color.needle2));
            x.setPosition(XAxis.XAxisPosition.BOTTOM);
            x.setDrawGridLines(true);
            x.setAxisLineColor(Color.GREEN);
            YAxis y = mChart.getAxisLeft();
            y.setLabelCount(6, false);
            y.setTextColor(getResources().getColor(R.color.needle2));
            //y.setTypeface(tf);
            y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            y.setDrawGridLines(false);
            y.setAxisLineColor(Color.GREEN);
            y.setAxisMinValue(500);
            //y.setAxisMaxValue(00);
            mChart.getAxisRight().setEnabled(true);
            yVals = new ArrayList<Entry>();
            yVals.add(new Entry(0, 0));
            LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
            //set1.setValueTypeface(tf);
            set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            set1.setCubicIntensity(0.02f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setCircleColor(Color.GREEN);
            //set1.setHighLightColor(Color.rgb(244, 117, 117));
            //set1.setColor(getResources().getColor(R.color.needle2));
            //set1.setFillColor(Color.GREEN);
            //set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            /*set1.setFillFormatter(new FillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });*/
            LineData data;
            if (mChart.getData() != null &&
                    mChart.getData().getDataSetCount() > 0) {
                data = mChart.getLineData();
                data.clearValues();
                data.removeDataSet(0);
                data.addDataSet(set1);
            } else {
                data = new LineData(set1);
            }

            data.setValueTextSize(9f);
            data.setDrawValues(false);
            mChart.setData(data);
            mChart.getLegend().setEnabled(false);
            mChart.animateXY(DURATION, DURATION);
            // dont forget to refresh the drawing
            mChart.invalidate();
            isChart = true;
        }

    }

    private void updateData(float val, long time) {
        if (mChart == null) {
            return;
        }
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            LineDataSet set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            Entry entry = new Entry(savedTime, val);
            set1.addEntry(entry);
            if (set1.getEntryCount() > 200) {
                set1.removeFirst();
                set1.setDrawFilled(false);
            }
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            savedTime++;
        }
    }

    private String ReadCPU0(String[] input) {
        ProcessBuilder pB;
        String result = "";

        try {
            //String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"};
            pB = new ProcessBuilder(input);
            pB.redirectErrorStream(false);
            Process process = pB.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) //default -1
            {
                //System.out.println(new String(re));
                result = new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private String formatCPUFreq(String cpuFreq) {

        String uwot = "";
        Matcher m = pat.matcher(cpuFreq);
        if (m.find()) {
            uwot = m.group(0);
            return uwot.substring(0, uwot.length() - 3);
        } else return "Error";
    }

    private SensorEventListener temperatureSensor = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("authorize", "on sensor changed called");
            float temp = event.values[0];
            Log.d("authorize", "Temperature sensor: " + temp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public int[] getCPUFrequencyCurrent(int cores, HashMap<Integer, String[]> hashMap) throws Exception {
        int[] output = new int[cores];
        for (int i = 0; i < cores; i++) {
            output[i] = Integer.parseInt(formatCPUFreq(ReadCPU0(hashMap.get(i))));
        }
        return output;
    }

    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
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
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    public float averageCpuData(int[] array) {
        int sum = 0;
        for (int j : array) {
            sum = sum + j;
        }
        return sum / array.length;
    }

    private HashMap<Integer, String[]> getCpuPath(int cores) {
        HashMap<Integer, String[]> hashMap = new HashMap<>();
        for (int i = 0; i < cores; i++) {
            hashMap.put(i, new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq"});
        }
        return hashMap;
    }

    //Bir cpu nun alabileceği max freq
    private void getMaxFreq() {
        final File maxcpufreqfile = new File("/sys/devices/system/cpu/cpu3/cpufreq/cpuinfo_max_freq");
        String[] maxFreq = {"/system/bin/cat", "/sys/devices/system/cpu/cpu3/cpufreq/cpuinfo_max_freq"};
        if (maxcpufreqfile.exists()) {
            txtMaxFreq.setText("Max freq: " + formatCPUFreq(ReadCPU0(maxFreq)) + " Mhz");
        } else txtMaxFreq.setText("N/A");
    }

    private void timeThread(){
        Thread timeThread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    while (keepReading){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countTimer();
                            }
                        });
                    }
                }catch (Exception e){}
            }
        };
        timeThread.start();
    }
    private void countTimer() {
        // Get elapsed time in milliseconds
        long elapsedTimeMillis = System.currentTimeMillis() - start;
        // Get elapsed time in seconds
        int elapsedTimeSec = ((int)(elapsedTimeMillis / 1000F))%60;
        // Get elapsed time in minutes
        int elapsedTimeMin = ((int)(elapsedTimeMillis / (60 * 1000F)))%60;
        // Get elapsed time in hours
        int elapsedTimeHour = ((int)(elapsedTimeMillis / (60 * 60 * 1000F)))%60;

        txtPassingTime.setText("Elapsed: "+timeValue(elapsedTimeHour)+":"+timeValue(elapsedTimeMin)+":"+timeValue(elapsedTimeSec));
    }
    private String timeValue(int time){
        return time<10?"0"+time:""+time;
    }
}