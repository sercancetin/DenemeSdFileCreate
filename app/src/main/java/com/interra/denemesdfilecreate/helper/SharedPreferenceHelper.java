package com.interra.denemesdfilecreate.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper  {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String FPS_SWİTCH = "switchkontrol";
    public static String FILE_SIZE = "file_size";

    private Context _context;
    private String etiket;

    public SharedPreferenceHelper(Context context,String etiket){
        _context = context;
        this.etiket = etiket;

        preferences = _context.getSharedPreferences(etiket,0);
        editor = preferences.edit();
    }
    public void setEditorBoolean(String adres,boolean deger){
        editor.putBoolean(adres,deger);
        editor.apply();
    }
    public boolean getEditorBoolean(String adres){
        return preferences.getBoolean(adres,false);
    }

    public void setEditorInt(String adress,int value){
        editor.putInt(adress,value);
        editor.apply();
    }
    public int getEditorInt(String adress){
        return preferences.getInt(adress,1024);
    }
}
