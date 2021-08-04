package com.interra.denemesdfilecreate;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileLister {
    private String path;
    public FileLister(String path){
        this.path = path;
    }
    public void lister(){
        //String path = Environment.getExternalStorageDirectory().toString()+"/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
    }
}
