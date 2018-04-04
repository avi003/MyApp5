package com.example.dell_1.myapp3.InternalMemory;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell_1.myapp3.Bacon1;
import com.example.dell_1.myapp3.Events.ArrayEvent;
import com.example.dell_1.myapp3.R;
import com.example.dell_1.myapp3.Services.MethodOneTask;
import com.example.dell_1.myapp3.Utils.AppPrefrences;
import com.example.dell_1.myapp3.Utils.SdCardDirectoryManipulator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MirsMAC on 29/03/2018.
 */

public class SdCardFunctionality extends AppCompatActivity implements MyRecyclerViewAdapter_sd.ItemClickListener {
    MyRecyclerViewAdapter_sd adapter;
    MenuItem mSort, mSettings, mRename, mSelectAll, mProperties, mCreate;
    public ArrayList<String> myList, myList2, selected;
    String path;
    public static boolean selectallflag = false;
    public static boolean cutbuttonclicked = false;
    RecyclerView recyclerView;
    long result;
    String string1, string3, x;
    AlertDialog.Builder alert;
    EditText etRenameFile;
    int fileIndex = 0;
    boolean adapterFlag;
    boolean askedUserOnce = false;
    File destination1, f1, file2, dir, currentFile;

    boolean isCopy = false;

    private static final String TAG = "com.example.dell_1.myapp3.InternalMemory";
    File f;//converted string object to file//getting the list of files in string array

    //flag to check if any cut operation is performed in the previous screen
    private boolean isSelectedInPrev;
    public ArrayList<String> dirStack;
    //    private FetchFilesTask mFetchFilesTask;
    public ArrayList<String> copyTask = new ArrayList<>();
    ImageButton button3, button4, buttoncut, button2, buttonpaste;
    int mode = 0;

    Uri treeUri;
    SdCardDirectoryManipulator manipulator;

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

        mode = getIntent().getIntExtra("mode", 1);


        manipulator=new SdCardDirectoryManipulator(this,this);

        if (mode == 2) {
            path = getExternalSdCardPath();
            if(appPrefrences.getDefaultPath()==null){
                appPrefrences.setDefaultPath(path);
            }
        } else
            path = Environment.getExternalStorageDirectory().getAbsolutePath();



        if (path == null)
            finish();

        f = new File(path);

        dirStack = new ArrayList<>();
        dirStack.add(f.getAbsolutePath());

        pd = new ProgressDialog(SdCardFunctionality.this);


        if(!appPrefrences.getIsGranted()){

            access();


        }else{
            manipulator.InitializeCurrentDirAfterPermission();
        }

        currentpath = f.getAbsolutePath();
        method1(f);
        //method2(f);

        // set up the RecyclerView
        adapterFlag = true;
        //setAdapter(true);


        recyclerView = (RecyclerView) findViewById(R.id.rvNumbers);

        button3 = (ImageButton) findViewById(R.id.button3);
        button4 = (ImageButton) findViewById(R.id.button4);
        buttoncut = (ImageButton) findViewById(R.id.button1);
        //copy
        button2 = (ImageButton) findViewById(R.id.button2);
        buttonpaste = (ImageButton) findViewById(R.id.buttonpaste);

        buttonpaste.setVisibility(View.GONE);


        buttonpaste.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        moveItem(isCopy);

                        copyTask.clear();
                    }
                }
        );

        //copy
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTask = (ArrayList<String>) multiselect.clone();
                clearMultiSelect(1);
                OnClick(v);
                isCopy = true;
                manipulator.copyFilesLoc();
                method1(new File(currentpath));

                //moveItem();
            }
        });

        //cut
        buttoncut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTask = (ArrayList<String>) multiselect.clone();
                clearMultiSelect(1);
                OnClick(v);
                isCopy = false;
                manipulator.copyFilesLoc();
                method1(new File(currentpath));

                //moveItem();
            }
        });

        button3.setOnClickListener(


                new View.OnClickListener() {
                    public void onClick(View view) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(SdCardFunctionality.this);
                        builder1.setMessage("Are you sure you want to delete it ?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //adapter.deleteItem();
                                        //method1(currentFile);
                                        Log.v(TAG,"here");
                                        deletebuttonclicked=true;
                                        if(!appPrefrences.getIsGranted()){

                                            if(appPrefrences.getUri()==null) {
                                                access();
                                            }


                                        }

                                        else{

                                            deleteDirectory();

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


                });

        button4.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        adapter.shareItem(view);
                    }
                }
        );
    }

    public void createAlertRename(){
        alert = new AlertDialog.Builder(this);

        etRenameFile = new EditText(getApplicationContext());
        etRenameFile.setText(myList.get(fileIndex));
        etRenameFile.setTextColor(getResources().getColor(R.color.colorBlack));
        etRenameFile.selectAll();

        alert.setTitle("Do you want to rename the file?");
        alert.setMessage(" ");
        alert.setView(etRenameFile);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                //renameFileAlert();
                String renameFile = etRenameFile.getText().toString();
                manipulator.renameFolder(renameFile);
                clearMultiSelect(1);
                method1(new File(currentpath));
                dialog.cancel();
//                adapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
// what ever you want to do with No option.
            }
        });

        alert.show();
    }


    public ArrayList<String> getExternalStorageDirectories() {

        ArrayList<String> results = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //Method 1 for KitKat & above
            File[] externalDirs = getExternalFilesDirs(null);

            for (File file : externalDirs) {
                String path = file.getPath().split("/Android")[0];

                boolean addPath = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addPath = Environment.isExternalStorageRemovable(file);
                } else {
                    addPath = Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(file));
                }

                if (addPath) {
                    results.add(path);
                }
            }
        }

        if (results.isEmpty()) { //Method 2 for all versions
            // better variation of: http://stackoverflow.com/a/40123073/5002496
            String output = "";
            try {
                final Process process = new ProcessBuilder().command("mount | grep /dev/block/vold")
                        .redirectErrorStream(true).start();
                Log.v(TAG, "HI");
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
            if (!output.trim().isEmpty()) {
                String devicePoints[] = output.split("\n");
                for (String voldPoint : devicePoints) {
                    results.add(voldPoint.split(" ")[2]);
                }
            }
        }

        //Below few lines is to remove paths which may not be external memory card, like OTG (feel free to comment them out)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().matches(".*[0-9a-f]{4}[-][0-9a-f]{4}")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        } else {
            for (int i = 0; i < results.size(); i++) {
                if (!results.get(i).toLowerCase().contains("ext") && !results.get(i).toLowerCase().contains("sdcard")) {
                    Log.d(TAG, results.get(i) + " might not be extSDcard");
                    results.remove(i--);
                }
            }
        }
        return results;
    }

    public String getExternalSdCardPath() {
        String path = null;
        ArrayList<String> x = getExternalStorageDirectories();
        for (int i = 0; i < x.size(); i++) {
            // file1 = new File(x.get(i));
            // Log.v(TAG,file1 + "");
            //method1(file1);
            //method2(file1);
            path = x.get(i);
            //setAdapter();
        }
        return path;
    }


    public void OnClick(View view) {
        cutbuttonclicked = true;

        button3.setVisibility(View.GONE);
        button4.setVisibility(View.GONE);
        buttoncut.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);
        buttonpaste.setVisibility(View.VISIBLE);
    }

    String currentpath = "";
    String currentUri="";

    @Override
    public void onItemClick(View view, int position) {
        string1 = adapter.getItem(position);
        if (!isMultiselected) {
            File directory = new File(string1);
            Log.e(TAG, "onItemClick: " + directory.isDirectory());
            if (directory.isDirectory()) {


                if (!dirStack.contains(directory.getAbsolutePath())) {
                    dirStack.add(directory.getAbsolutePath());
                    currentpath = directory.getAbsolutePath();
                    //saving the URI
                    manipulator.saveIndex(myList2.indexOf(string1));
                    //currentUri
                }

                adapter = null;
                method1(directory);

                // adapterFlag = !isSelectedInPrev;

//                if (isSelectedInPrev)
//                    adapter.setmSelected(temp);

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
        } else {
            //multiselect is on
            if (!multiselect.contains(myList2.get(position)))
                multiselect.add(myList2.get(position));
            else
                multiselect.remove(myList2.get(position));
        }
    }

    public void clearMultiSelect(int a) {
        isMultiselected = false;
        multiselect.clear();
        fileIndex = 0;
        //hideMenuItem();

        if (a == 0)
            Toast.makeText(SdCardFunctionality.this, "Multi Select Cleared", Toast.LENGTH_SHORT).show();
    }

    boolean isMultiselected = false;
    public ArrayList<String> multiselect = new ArrayList<>();

    @Override
    public boolean onLongClick(View view, int position) {
        isMultiselected = true;

        if (!multiselect.contains(myList2.get(position)))
            multiselect.add(myList2.get(position));
        else
            multiselect.remove(myList2.get(position));

        fileIndex = position;
        hideMenuItem();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionmenu, menu);
        mSort = menu.findItem(R.id.action_sort);
        mSettings = menu.findItem(R.id.action_settings);
        mCreate = menu.findItem(R.id.action_newFolder);
        mRename = menu.findItem(R.id.action_rename);
        mRename.setVisible(false);
        mSelectAll = menu.findItem(R.id.action_selectAll);
        mSelectAll.setVisible(false);
        mProperties = menu.findItem(R.id.action_properties);
        mProperties.setVisible(false);
        return true;
    }

    boolean newFolderClick=false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_settings:
                // search action
                return true;

            case R.id.action_newFolder:
                String foldername = "New Folder2";
                String paths=appPrefrences.getDefaultPath();
                newFolderClick=true;
                if(appPrefrences.getUri()==null){
                    access();
                }else {
                    if (string1 == null) {
//                        Uri u = Uri.parse(appPrefrences.getUri());
//                        String directory = currentpath.replaceFirst(paths, "");
                            manipulator.createNewFolder();
//                        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(appPrefrences.getUri()));
//                        pickedDir.createDirectory(foldername);
//                        Log.v(TAG, "Directory is created");
//                        Toast.makeText(SdCardFunctionality.this, "New Folder created with the name:"
//                                + foldername, Toast.LENGTH_LONG).show();

                    } else {
//                        Uri u = Uri.parse(appPrefrences.getUri());
//                        String directory = currentpath.replaceFirst(paths, "");
                            manipulator.createNewFolder();
//                        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(appPrefrences.getUri()));
//                        pickedDir.createDirectory(foldername);
//
//                        Log.v(TAG, "Directory is created");
//                        Toast.makeText(SdCardFunctionality.this, "New Folder created with the name:"
//                                + foldername, Toast.LENGTH_LONG).show();

                        // dir = new File(string1, foldername);

//                    try {
//                        if (dir.mkdir()) {
//                            Toast.makeText(SdCardFunctionality.this, "New Folder created with the name:"
//                                    + foldername, Toast.LENGTH_LONG).show();
//                        } else {
//                            Toast.makeText(SdCardFunctionality.this,
//                                    "Directory is not created", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    }

                    clearMultiSelect(1);
                    //notifyMediaStoreScanner(dir);
                    //adapter.notifyDataSetChanged();
                    method1(new File(currentpath));
                    // search action
                }
                return true;

            case R.id.action_sort:
                showRadioButtonDialog();
                clearMultiSelect(1);
                // location found
                return true;

            case R.id.action_rename:

                createAlertRename();
                Log.i("ZAA", "Image Gallery action rename");
                // location found
                return true;

            case R.id.action_selectAll:
                adapter.selectAll();
                adapter.notifyDataSetChanged();
                selectallflag = true;
                // location found
                Log.i("ZAA", "Image Gallery action action_selectAll");
                return true;

            case R.id.action_properties:
                properties();
                // location found
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////// ******* MULTI CLICK HIDE ********** ////////////
    private void hideMenuItem() {
        mSort.setVisible(false);
        mSettings.setVisible(false);
        mRename.setVisible(true);
        mSelectAll.setVisible(true);
        mProperties.setVisible(true);
        selected = adapter.getList();
        Log.v(TAG, Integer.toString(selected.size()) + " is the size");
        if (selected.size() > 1) {
            mProperties.setVisible(false);
            mRename.setVisible(false);
        }
    }


    @Override
    public void onBackPressed() {

        if (!selectallflag) {
            if (!isMultiselected) {
                String currpath = null;

                if (dirStack.size() == 0 || dirStack.size() == 1)
                    finish();
                else {
                    dirStack.remove(currentpath);
                    currpath = dirStack.get(dirStack.size() - 1);
                    currentpath = currpath;

                    manipulator.backPressed();
                    method1(new File(currpath));

                    Log.e(TAG, "onBackPressed: " + dirStack.size() + "**" + currpath);
                    adapterFlag = true;
                }
            } else {
                clearMultiSelect(0);
                method1(new File(currentpath));
            }
        } else {
            selectallflag = false;
            clearMultiSelect(0);
            method1(new File(currentpath));
        }

    }


    private void setAdapter(boolean enableSelection) {
        int numberOfColumns = 4;
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        adapter = new MyRecyclerViewAdapter_sd(this, myList, myList2,
                enableSelection, SdCardFunctionality.this);
        adapter.setClickListener( this);

        recyclerView.setAdapter(adapter);
    }


    /////////////*************** MOVING AND PASTING ****************************************** ///////////////////
    private void moveItem(boolean isCopy) {
        //selected = adapter.getList();
        Log.v(TAG, "moveitem" + Integer.toString(copyTask.size()));
//        for (int i = 0; i < copyTask.size(); i++) {
//            File source1 = new File(copyTask.get(i));
//            destination1 = new File(currentpath + File.separator + source1.getName());
//            try {
//                moveFile(source1, destination1, isCopy);
//                notifyMediaStoreScanner(destination1);
//                myList.add(destination1.getName());
//                myList2.add(destination1.getAbsolutePath());
//                adapter.notifyDataSetChanged();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        manipulator.copyFiles(isCopy);

        button3.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        buttoncut.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
        buttonpaste.setVisibility(View.GONE);

        method1(new File(currentpath));
    }

    private void moveFile(File file_Source, File file_Destination, boolean isCopy) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        if (!file_Destination.exists()) {
            file_Destination.createNewFile();
        }

        try {
            source = new FileInputStream(file_Source).getChannel();
            destination = new FileOutputStream(file_Destination).getChannel();

            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size) ;
            if (!isCopy) {
                file_Source.delete();
                MediaScannerConnection.scanFile(this,
                        new String[]{file_Source.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });
            }

        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
/////////////*************** MOVING AND PASTING ****************************************** ///////////////////


    public final void notifyMediaStoreScanner(File file) {
//        try {
//            MediaStore.Images.Media.insertImage(getBaseContext().getContentResolver(),
//                    file.getAbsolutePath(), file.getName(), null);
        getBaseContext().sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        getBaseContext().sendBroadcast(new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    private void renameFileAlert() {

        String renameFile = etRenameFile.getText().toString();
        String filename = myList.get(fileIndex);

        File oldFilePath = new File(myList2.get(fileIndex));
// Log.d("OLDFILEPATH", oldFilePath.toString());

        x = myList2.get(fileIndex);


        File renamedFile = new File(x.replace(filename, renameFile));
// Log.d("NEWFILE", renamedFile.toString());

        boolean isSuccess = oldFilePath.renameTo(renamedFile);

        //check if the file was renamed
        if (isSuccess) {
            myList2.set(fileIndex, renamedFile.getAbsolutePath());
            myList.set(fileIndex, renamedFile.getName());
//            adapter.notifyDataSetChanged();
//            notify the adapter that the file is renamed
            adapter.notifyItemChanged(fileIndex);
            mRename.setVisible(false);
        } else
            Toast.makeText(this, "There was an error in renaming the file", Toast.LENGTH_SHORT).show();

        Log.e(TAG, "renameFileAlert: " + renamedFile.getPath());

        notifyMediaStoreScanner(renamedFile);

    }

    //SORT BY NAME DATE ETC THE FILE SYSTEM
    private void showRadioButtonDialog() {

        final CharSequence[] items = {" Name ", " Date taken", " Size ", " last modified "};
        final CharSequence[] items2 = {" Only for this folder"};
        final ArrayList seletedItems = new ArrayList();
// custom dialog
        final AlertDialog.Builder builder2 = new AlertDialog.Builder(SdCardFunctionality.this);
        builder2.setTitle("SORT BY");

        builder2.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
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

    ProgressDialog pd;

    //Load the contents of the directory
    public void method1(final File f) {

        pd.setMessage("Please Wait...");
        pd.setCancelable(true);
        pd.show();
        currentFile = f;
        MethodOneTask methodOneTask = new MethodOneTask(getApplicationContext());
        methodOneTask.execute(f);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayEvent event) {
        myList = event.getArrayListModel().getNameList();
        myList2 = event.getArrayListModel().getPathList();
        setAdapter(adapterFlag);
        pd.dismiss();

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    ///////////// ************* PROPERTIES ************ //////////////////////////
    private void properties() {
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
        TextView displaypath = (TextView) dialogView.findViewById(R.id.displaypath);
        for (int i = 0; i < multiselect.size(); i++) {
            Log.v(TAG, Integer.toString(selected.size()) + " is the final size");
            if (multiselect.size() > 0) {
                Log.v(TAG, Integer.toString(multiselect.size()));
                File file = new File(multiselect.get(i));
                String strFileName = file.getName();
                displayname.setText(strFileName);
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDateString = formatter.format(lastModified);
                displaylastmodified.setText(formattedDateString);
                displaydatetaken.setText(formattedDateString);
                String path = multiselect.get(i);//myList2.get(Integer.parseInt(selected.get(i)));
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
                } else if (file.isDirectory()) {
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

    /////////// SD Card Components ******** //////////////////
    AppPrefrences appPrefrences;
    public void access(){

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        // it would be "*/*".
        //intent.setType("*/*");
        startActivityForResult(intent, 42);
    }

    boolean deletebuttonclicked=false;
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == 42) {
            treeUri = resultData.getData();
            appPrefrences.setUri(treeUri);
            //save it
            manipulator.saveCurrentUri(treeUri);

            //appChooserAppearsOnce=true;
            appPrefrences.sdPermissionGranted(true);
            manipulator.InitializeCurrentDirAfterPermission();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getContentResolver().takePersistableUriPermission(treeUri,Intent.FLAG_GRANT_READ_URI_PERMISSION & Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
            }
            //DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

            // Create a new file and write into it
            // DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
            if(deletebuttonclicked){
                deleteDirectory();
            }else if(newFolderClick){
                String foldername = "New Folder2";
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, Uri.parse(appPrefrences.getUri()));
                pickedDir.createDirectory(foldername);
                Log.v(TAG, "Directory is created");
                Toast.makeText(SdCardFunctionality.this, "New Folder created with the name:"
                        + foldername, Toast.LENGTH_LONG).show();
            }
            //permissiongranted = true;
            makeToast("Access Granted");
            Log.v(TAG,"reached");
        }

    }

    public void deleteDirectory() {

        if(appPrefrences.getUri()!=null){

            //File file2=new File(Environment.getExternalStorageDirectory(),longSelectedPath);

            //Uri uu=Uri.fromFile(new File(multiselect.get(0)));
            //String st=appPrefrences.getUri();
//            DocumentFile pickedDir= DocumentFile.fromTreeUri(this, Uri.parse(appPrefrences.getUri()));
//
//            DocumentFile[] documentFiles = pickedDir.listFiles() ;
//            boolean delete=false;
//            for(int i=0; i<multiselect.size();i++){
//                int a=myList2.indexOf(multiselect.get(i));
//                DocumentFile documentFile = documentFiles[myList2.indexOf(multiselect.get(i))];
//
//                if(documentFile.delete()){
//                    //adapter.notifyItemRemoved(fileIndex);
//                   delete=true;
//                }
//            }
            boolean delete=false;
            delete=manipulator.deleteFiles();
            if(delete){
                makeToast("Deleted Files Successfully");
                //clearMultiSelect(1);
            }
            method1(new File(currentpath));


            //  delete(new File(longSelectedPath));

        }




    }

    public void makeToast(String str){
        Toast.makeText(this, str,Toast.LENGTH_LONG).show();
    }



}
