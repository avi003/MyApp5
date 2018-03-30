package com.example.dell_1.myapp3.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * Created by dragonfury on 20/03/18.
 */

public class AppPrefrences {

    Context mContext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String PREF_NAME="MAIN_PREFS";
    private String IS_Granted="is_granted";
    private String URI_Save="uri_save";

    private String SavedDir="save_dir";

    public  AppPrefrences(Context context){
        this.mContext=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        editor=sharedPreferences.edit();


    }


    public void sdPermissionGranted(boolean status){
        editor.putBoolean(IS_Granted,status);
        editor.commit();

    }

    public boolean getIsGranted(){

        return sharedPreferences.getBoolean(IS_Granted,false);
    }


    public void setUri(Uri uri){

        editor.putString(URI_Save,uri.toString());
        editor.commit();
    }
public String getUri(){

        return sharedPreferences.getString(URI_Save,null);
}

    public void setDefaultPath(String uri){

        editor.putString(SavedDir,uri);
        editor.commit();
    }
    public String getDefaultPath(){

        return sharedPreferences.getString(SavedDir,null);
    }
}
