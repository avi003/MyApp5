package com.example.dell_1.myapp3.Services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.dell_1.myapp3.Models.ArrayListModel;
import com.example.dell_1.myapp3.Events.ArrayEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dragonfury on 16/03/18.
 */

public class MethodOneTask extends AsyncTask<File, Void, ArrayListModel> {
Context context;

    public MethodOneTask(Context context) {
        this.context=context;
    }

    @Override
    protected ArrayListModel doInBackground(File... files) {
        ArrayListModel arrayListModel=new ArrayListModel();
        File lists[] = files[0].listFiles();
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String>list2 = new ArrayList<>();
        if (files != null) {
            for (int i = 0; i < lists.length; i++) {
                list1.add(lists[i].getName());
                list2.add(lists[i].getPath());

            }
            arrayListModel.setList(list1,list2);
            return  arrayListModel;


        } else {
            Toast.makeText(context, "the folder is empty", Toast.LENGTH_SHORT)
                    .show();
            return arrayListModel;
        }



    }

    @Override
    protected void onPostExecute(ArrayListModel arrayListModel) {
        super.onPostExecute(arrayListModel);
        EventBus.getDefault().post(new ArrayEvent(arrayListModel));

    }


}