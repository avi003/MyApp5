package com.example.dell_1.myapp3.Utils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.provider.DocumentFile;
import android.widget.Toast;

import com.example.dell_1.myapp3.InternalMemory.SdCardFunctionality;
import com.github.barteksc.pdfviewer.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by MirsMAC on 01/04/2018.
 */

public class SdCardDirectoryManipulator {

    /////////////*********** DIRECTORY HANDLER ***********////////////
    String currentTreeDir="";
    DocumentFile pickedDir=null;
    boolean firstTime=true;
    AppPrefrences appPrefrences;
    Context context;
    SdCardFunctionality act;

    public Uri currentUri;

//    Uri u = Uri.parse(appPrefrences.getUri());

    //this will maintain all the directory we have moved
    //at first its emplty if we get in a directory it will save the index of the dir we clicked
    ArrayList<Integer> indexMovement=new ArrayList<>();
    //index movement and index Uri are correspondant
    //ArrayList<Uri> indexURi=new ArrayList<>();

    public SdCardDirectoryManipulator(Context ctx, SdCardFunctionality acitvity){
        appPrefrences=new AppPrefrences(ctx);
        context=ctx;
        act=acitvity;

        if(appPrefrences.getUri()!=null)
        currentUri=Uri.parse(appPrefrences.getUri());
    }

    public void saveCurrentUri(Uri uri){
        currentUri=uri;
    }

    DocumentFile currentDir;
    public void saveIndex(int index){
        indexMovement.add(index);
        currentDir=generateCurrentURi();
        //indexURi.add(uri);
    }

    public void clearIndexes(){
        indexMovement.clear();
    }

    //back press treatment
    public void backPressed(){
    int size=indexMovement.size();
    if(size>0){
    indexMovement.remove(indexMovement.get(size-1));
        currentDir=generateCurrentURi();
    }
    }

    public void InitializeCurrentDirAfterPermission(){
        currentDir=generateCurrentURi();
    }

    //it will generate the current uri
    public DocumentFile generateCurrentURi(){
        DocumentFile doc;
        DocumentFile pickedDirBase= DocumentFile.fromTreeUri(context, Uri.parse(appPrefrences.getUri()));
        //index movement carries the document tree file positions like 0,1,2 that means
        // in the root 0=first folder, 1= root>1st folder> 2nd Foleder, 2= root>1st folder> 2nd Foleder>3rd Folder>,
        // The documentFile[ ] has all the files/folders of a specific directory. like 0,1,2... so with the
        //indexMovement arraylist I can easily go throught he directory
//        for(int i=0;i<indexMovement.size();i++){
//            DocumentFile[] documentFiles = pickedDirBase.listFiles() ;
//            DocumentFile documentFile = documentFiles[indexMovement.get(i)];
//
//            DocumentFile[] documentFiles2=documentFile.listFiles();
//
//            //documentFiles2[0].createDirectory("Mir");
//
//            Uri u=documentFile.getUri();
//            DocumentFile[] documentFile2=documentFile.listFiles();
//
//            pickedDirBase= DocumentFile.fromTreeUri(context, Uri.parse(documentFile.getUri().toString()));
//
//        }
//        uri=pickedDirBase.getUri();
//        currentUri=uri;
//        return uri;


        doc=giveMeCurrentDocumentDirectory(0,pickedDirBase);
        return doc;

    }

    //call d with the root
    public DocumentFile giveMeCurrentDocumentDirectory(int pos,DocumentFile d){
        DocumentFile d1=null;
        if(indexMovement.size()==0)
            return d;
        if(pos<indexMovement.size()){
            int fileindex= indexMovement.get(pos);
            d1=d.listFiles()[fileindex];

        }
        if(pos+1==indexMovement.size()){
            return d1;
        }else
            pos++;

        return giveMeCurrentDocumentDirectory(pos,d1);

    }


    //////////////////////////////// FUNCTIONS //////////////////////
    public void createNewFolder(){
        String foldername = "New Folder2";
        if(currentDir!=null) {
            currentDir.createDirectory(foldername);
            currentDir=generateCurrentURi();
        }
    }

    public void renameFolder(String name){
        if(!act.multiselect.isEmpty() && currentDir!=null){
            int index=act.myList2.indexOf(act.multiselect.get(0));
            DocumentFile f=currentDir.listFiles()[index];
            f.renameTo(name);
            //upgrade current directory otherwise new files wont be added
            currentDir=generateCurrentURi();
        }
    }

    //delete
    public boolean deleteFiles(){
        boolean deleted=false;
        if(!act.multiselect.isEmpty())
        for(int i=0;i<act.multiselect.size();i++){
            int index=act.myList2.indexOf(act.multiselect.get(i));
            DocumentFile f=currentDir.listFiles()[index];

            f.delete();
            deleted=true;
        }

        if(deleted) {
            currentDir = generateCurrentURi();
        }
        act.clearMultiSelect(1);

        return deleted;
    }

    DocumentFile oldCopyDir=null;
    ArrayList<DocumentFile> files=new ArrayList<>();

    public void copyFilesLoc(){
    oldCopyDir=currentDir;
        for(int i=0; i<act.copyTask.size();i++) {
            int index = act.myList2.indexOf(act.copyTask.get(i));
            DocumentFile file = oldCopyDir.listFiles()[index];
            files.add(file);
        }
    }


    public void copyFiles(boolean isCopy){

        for(int i=0; i<files.size();i++) {
            DocumentFile file=files.get(i);
            InputStream inStream = null;
            OutputStream outStream = null;
            boolean isDirectory = file.isDirectory();
            if (isDirectory) {
                currentDir.createDirectory(file.getName());
            } else
                currentDir.createFile("", file.getName());
            //currentDir=generateCurrentURi();
            DocumentFile d = currentDir.findFile(file.getName());


            //currentDir= generateCurrentURi();
            //DocumentFile d=currentDir.
            try {

                //String st=readFileContent(file.getUri());
                if (!isDirectory) {
                    inStream =
                            context.getContentResolver().openInputStream(file.getUri());
                    outStream =
                            (FileOutputStream) context.getContentResolver().openOutputStream(d.getUri());
                    byte[] buffer = new byte[16384];
                    int bytesRead;
                    while ((bytesRead = inStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (!isDirectory) {
                        inStream.close();

                        outStream.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        //the cut treatment
        if(!isCopy){
            for(DocumentFile d: files){
                d.delete();
            }
        }

        files.clear();
    }


    public void makeToast(String str){
        Toast.makeText(context, str,Toast.LENGTH_LONG).show();
    }

}
