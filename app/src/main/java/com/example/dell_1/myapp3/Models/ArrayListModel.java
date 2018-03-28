package com.example.dell_1.myapp3.Models;

import java.util.ArrayList;

/**
 * Created by dragonfury on 15/03/18.
 */

public class ArrayListModel {
    ArrayList<String> nameList=new ArrayList<>();
    ArrayList<String> pathList=new ArrayList<>();

    public void setList(ArrayList<String> a,ArrayList<String> b){
nameList=a;
pathList=b;

    }
public ArrayList<String> getNameList(){

        return nameList;
}

public ArrayList<String> getPathList(){
    return pathList;
}
}
