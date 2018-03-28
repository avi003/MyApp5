package com.example.dell_1.myapp3.Events;

import com.example.dell_1.myapp3.Models.ArrayListModel;

/**
 * Created by dragonfury on 16/03/18.
 */

public class ArrayEvent {
    ArrayListModel arrayListModel=new ArrayListModel();

    public ArrayEvent(ArrayListModel arrayListModel) {
        this.arrayListModel=arrayListModel;
    }

    public ArrayListModel getArrayListModel(){
        return arrayListModel;
    }
}
