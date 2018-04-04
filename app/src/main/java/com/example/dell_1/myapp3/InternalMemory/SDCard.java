package com.example.dell_1.myapp3.InternalMemory;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_1.myapp3.Events.ArrayEvent;
import com.example.dell_1.myapp3.R;
import com.example.dell_1.myapp3.Services.MethodOneTask;
import com.example.dell_1.myapp3.Utils.AppPrefrences;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SDCard extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    private ArrayList<String> results,myList,myList2,selected;
    MenuItem mSort, mSettings, mRename, mSelectAll, mProperties;
    private static final String TAG = "com.example.dell_1.myapp3.InternalMemory";
    public static boolean permissiongranted = true;
    public static boolean deletebuttonclicked =false;
    File file1,currentFile;
    String string1;
    Uri uri1;
    Uri treeUri;
    long result ;
    MyRecyclerViewAdapter adapter;
    int fileIndex = 0;
    RecyclerView recyclerView;
    String longSelectedPath;
boolean appChooserAppearsOnce=false;
    AppPrefrences appPrefrences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_storage);

        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        topToolBar.setTitle("");
        topToolBar.setSubtitle("");
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));
        appPrefrences=new AppPrefrences(this);

        ArrayList<String> x = getExternalStorageDirectories();

        String extPath = getExternalSdCardPath();

        if(!appPrefrences.getIsGranted()){

            access();


        }

//        if (extPath == null)
//            Toast.makeText(this, "No SDCARD Available", Toast.LENGTH_SHORT).show();
//        else{
//            File fi = new File(extPath);
//            method1(fi);
//            method2(fi);
//        }
//
//        Log.e(TAG, "onCreate: "+isExternalStorageReadable() );
        for(int i =0; i< x.size();i++){
            file1 = new File(x.get(i));
            Log.v(TAG,file1 + "");
            method1(file1);
            //method2(file1);

            //setAdapter();
        }

//        method1(file1);
//        method2(file1);

                ImageButton button2=(ImageButton)findViewById(R.id.button2);
                ImageButton button1=(ImageButton)findViewById(R.id.button1);
        final ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        final ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        final ImageButton buttonpaste = (ImageButton) findViewById(R.id.buttonpaste);
        buttonpaste.setVisibility(View.GONE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWriteStoragePermissionGranted()){

                    //put cut operation code here
                }

                else {
                    Toast.makeText(SDCard.this,"Please grant write permission",Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWriteStoragePermissionGranted()){


                    //put paste operation code here

                }

                else {
                    Toast.makeText(SDCard.this,"Please grant write permission",Toast.LENGTH_SHORT).show();
                }


            }
        });

        button3.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        if(isWriteStoragePermissionGranted()){

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SDCard.this);
                        builder1.setMessage("Are you sure you want to delete it ?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        selected = adapter.getList();
                                        if(permissiongranted){


                                            Log.v(TAG,"here");
                                            deletebuttonclicked=true;
                                            if(!appPrefrences.getIsGranted()){

                                                access();


                                            }

                                            else{

                                                File file = new File(longSelectedPath);
                                                deleteDirectory(file);

                                            }

                                        }

                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();



                    }
                    else {
                            Toast.makeText(SDCard.this,"Please grant write permission",Toast.LENGTH_SHORT).show();

                        }
                    }



                });

        button4.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        adapter.shareItem(view);
                    }
                }
        );

    }


    public void method1(File f) {

        currentFile=f;
        MethodOneTask methodOneTask=new MethodOneTask(getApplicationContext());
        methodOneTask.execute(f);
    }

/*    public ArrayList<String> method2(File f) {
        File list2[] = f.listFiles();
        myList2 = new ArrayList<>();
        if (list2 != null) {
            for (int i = 0; i < list2.length; i++) {
                myList2.add(list2[i].getPath());
            }
        } else Toast.makeText(this, "the folder is empty", Toast.LENGTH_SHORT)
                .show();
        return myList2;
    }*/

    private void setAdapter(){
        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
       // adapter = new MyRecyclerViewAdapter(this, myList, myList2, true);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        string1 = adapter.getItem(position);
        File directory = new File(string1);
        if (directory.isDirectory()) {
            method1(directory);
            //method2(directory);
            //setAdapter();
        } else if (string1.endsWith(".mp3")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "audio/mpeg");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".zip")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/zip");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".mp4")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "video/mp4");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".jpeg")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "image/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".png")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "image/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".pdf")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/pdf");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".apk")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "application/vnd.android.package-archive");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else if (string1.endsWith(".txt")) {
            Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
            viewIntent1.setDataAndType(Uri.fromFile(directory), "text/*");
            startActivity(Intent.createChooser(viewIntent1, null));
        } else Toast.makeText(this, "unsupported format", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean onLongClick(View view, int position) {
        fileIndex =position;
        longSelectedPath=myList2.get(position).toString();
        hideMenuItem();
        return true;
    }

    public ArrayList<String> getExternalStorageDirectories() {

        results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                }
                else{
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if(addPath){
                    results.add(path);
                }
            }
        }

        if(results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                Log.v(TAG,"HI");
                process.waitFor();
                final InputStream is = process.getInputStream();
                final byte[] buffer = new byte[1024];
                while (is.read(buffer) != -1) {
                    output = output + new String(buffer);

                    Log.v(TAG, output);
                }
                is.close();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            if(!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for(String voldPoint: devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(TAG, results.get(i)+" might not be extSDcard");
                    results.remove(i--);
                }
            }
        }
        return results;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        mSort = menu.findItem(R.id.action_sort);
        mSettings = menu.findItem(R.id.action_settings);
        mRename = menu.findItem(R.id.action_rename);
        mRename.setVisible(false);
        mSelectAll = menu.findItem(R.id.action_selectAll);
        mSelectAll.setVisible(false);
        mProperties = menu.findItem(R.id.action_properties);
        mProperties.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                // search action
                return true;

            case R.id.action_sort:
                showRadioButtonDialog();
                // location found
                return true;

            case R.id.action_rename:
                Log.i("ZAA","Image Gallery action rename");
                // location found
                return true;

            case R.id.action_selectAll:
                // location found
                Log.i("ZAA","Image Gallery action action_selectAll");
                return true;

            case R.id.action_properties:
                properties();
                // location found
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideMenuItem(){
        mSort.setVisible(false);
        mSettings.setVisible(false);
        mRename.setVisible(true);
        mSelectAll.setVisible(true);
        mProperties.setVisible(true);
        selected = adapter.getList();
        Log.v(TAG, Integer.toString(selected.size())+ " is the size");
        if(selected.size()>0){
            mProperties.setVisible(false);
            mRename.setVisible(false);
        }
    }

    private void properties(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setMessage("Properties");
        builder.setView(R.layout.properties);
        View dialogView = inflater.inflate(R.layout.properties, null);
        builder.setView(dialogView);
        TextView displayname = (TextView) dialogView.findViewById(R.id.displayname);
        TextView displaysize = (TextView) dialogView.findViewById(R.id.displaysize);
        TextView displaylastmodified = (TextView) dialogView.findViewById(R.id.displaylastmodified);
        TextView displaydatetaken = (TextView) dialogView.findViewById(R.id.displaydatetaken);
        TextView displaypath  = (TextView) dialogView.findViewById(R.id.displaypath);
        for(int i=0;i<selected.size();i++){
            Log.v(TAG, Integer.toString(selected.size())+ " is the final size");
            if(selected.size()>0) {
                Log.v(TAG, Integer.toString(selected.size()));
                File file = new File(myList2.get(Integer.parseInt(selected.get(i))));
                String strFileName = file.getName();
                displayname.setText(strFileName);
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDateString = formatter.format(lastModified);
                displaylastmodified.setText(formattedDateString);
                displaydatetaken.setText(formattedDateString);
                String path = myList2.get(Integer.parseInt(selected.get(i)));
                displaypath.setText(path);
                if (file.isFile()) {
                    float fileSizeInBytes = file.length();
                    String calString = Float.toString(fileSizeInBytes);
                    displaysize.setText(calString + " bytes");
                    Log.v(TAG, Long.toString(file.length()));
                    if (fileSizeInBytes > 1024) {
                        float fileSizeInKB = fileSizeInBytes / 1024;
                        String calString2 = Float.toString(fileSizeInKB);
                        displaysize.setText(calString2 + " KB");
                        if (fileSizeInKB > 1024) {
                            float fileSizeInMB = fileSizeInKB / 1024;
                            String calString3 = Float.toString(fileSizeInMB);
                            displaysize.setText(calString3 + " MB");
                        }
                    }
                }
                else if(file.isDirectory()){
                    result = 0;
                    final List<File> dirs = new LinkedList<>();
                    dirs.add(file);
                    while (!dirs.isEmpty()) {
                        final File dir = dirs.remove(0);
                        if (!dir.exists())
                            continue;
                        final File[] listFiles = dir.listFiles();
                        if (listFiles == null || listFiles.length == 0)
                            continue;
                        for (final File child : listFiles) {
                            result += child.length();
                            if (child.isDirectory())
                                dirs.add(child);
                        }
                    }
                    float fileSizeInBytes = result;
                    String calString = Float.toString(fileSizeInBytes);
                    displaysize.setText(calString + " bytes");
                    if (fileSizeInBytes > 1024) {
                        float fileSizeInKB = fileSizeInBytes / 1024;
                        String calString2 = Float.toString(fileSizeInKB);
                        displaysize.setText(calString2 + " KB");
                        if (fileSizeInKB > 1024) {
                            float fileSizeInMB = fileSizeInKB / 1024;
                            String calString3 = Float.toString(fileSizeInMB);
                            displaysize.setText(calString3 + " MB");
                        }
                    }
                }
            }
        }
        AlertDialog alert12 = builder.create();
        alert12.show();
    }

    private void access(){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, 42);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            treeUri = resultData.getData();
            appPrefrences.setUri(treeUri);
            appChooserAppearsOnce=true;
            appPrefrences.sdPermissionGranted(true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getContentResolver().takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_READ_URI_PERMISSION & Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
            }
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            // Create a new file and write into it
           // DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
            if(deletebuttonclicked){
              DocumentFile[] documentFiles = pickedDir.listFiles();
                DocumentFile documentFile = documentFiles[fileIndex];
                if(documentFile.delete()){
                    method1(currentFile);
                    //adapter.notifyItemRemoved(fileIndex);
                }


            }
          /*  try {
                OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
                out.write("A long time ago...".getBytes());
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }*/
            permissiongranted = true;
            Log.v(TAG,"reached");
        }
    }

    private void showRadioButtonDialog() {

        final CharSequence[] items = {" Name "," Date taken"," Size "," last modified "};
        final CharSequence[] items2 = {" Only for this folder"};
        final ArrayList seletedItems=new ArrayList();
        // custom dialog
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(SDCard.this);
        builder2.setTitle("SORT BY");

        builder2.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        // Your code when first option seletced
                        break;
                    case 1:
                        // Your code when 2nd  option seletced

                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        break;
                    case 3:
                        // Your code when 4th  option seletced
                        break;

                }
                dialog.dismiss();
            }

        });


        AlertDialog alert12 = builder2.create();
        alert12.show();

    }

    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;
        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");

        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                }
                else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
        }
        else {
            sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        return sdCardFile.getAbsolutePath();
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayEvent event) {
        myList=event.getArrayListModel().getNameList();
        myList2=event.getArrayListModel().getPathList();
        setAdapter();

    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        method1(currentFile);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }



    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }


    public void deleteDirectory(File file) {

        if(treeUri!=null){
            DocumentFile pickedDir= DocumentFile.fromTreeUri(this, treeUri);
            DocumentFile[] documentFiles = pickedDir.listFiles();
            DocumentFile documentFile = documentFiles[fileIndex];
            if(documentFile.delete()){
                method1(currentFile);}
        }

        else if(appPrefrences.getUri()!=null){

            //File file2=new File(Environment.getExternalStorageDirectory(),longSelectedPath);

            DocumentFile pickedDir= DocumentFile.fromTreeUri(this, Uri.parse(appPrefrences.getUri()));
            DocumentFile[] documentFiles = pickedDir.listFiles() ;
                DocumentFile documentFile = documentFiles[fileIndex];
            if(documentFile.delete()){
                //adapter.notifyItemRemoved(fileIndex);
                method1(currentFile);
                }

        //  delete(new File(longSelectedPath));

        }




    }




    public boolean delete(final File file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[] {
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri(longSelectedPath);

        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {

            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }



}
