package com.example.dell_1.myapp3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell_1.myapp3.APK.AllAppsActivity;
import com.example.dell_1.myapp3.ImageViewer.ImageGallery;
import com.example.dell_1.myapp3.MusicPlayer.PlayListActivity;
import com.example.dell_1.myapp3.PDF.MainActivity1;
import com.example.dell_1.myapp3.VideoPlayer.SDVideos;

import java.io.File;


public class Bacon1 extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bacon1);


        Button button4 = (Button) findViewById(R.id.button4);

        button4.setOnClickListener(
                new Button.OnClickListener()

                {
                    public void onClick(View v) {
                        buttonClicked(v);

                        Intent p = new Intent(Bacon1.this, PlayListActivity.class);
                        startActivity(p);

                    }
                });
    }

    private void buttonClicked(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            Snackbar.make(view, "Permission not Granted, Requesting permission.", Snackbar.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Snackbar.make(view, "We need permission to internal storage for displaying songs", Snackbar.LENGTH_LONG).show();

            } else {

                Snackbar.make(view, "Allow myapp3 to access this device's internal storage", Snackbar.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }


    public void onClick2(View view) {

        buttonClicked(view);
        Intent viewIntent2 = new Intent(this, ImageGallery.class);
        startActivity(viewIntent2);
    }


    public void onClick4(View view) {

        buttonClicked(view);
        Intent viewIntent1 = new Intent(this, SDVideos.class);
        startActivity(viewIntent1);
    }

    public void onClick5(View view) {

        Intent viewIntent1 = new Intent(Intent.ACTION_VIEW);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.getExternalStorageDirectory() + "/myapp") + "/" + "downloadedfile.zip");
        viewIntent1.setDataAndType(Uri.fromFile(file), "application/zip");
        startActivity(Intent.createChooser(viewIntent1, null));
    }

    public void onClick6(View view) {

        buttonClicked(view);
        Intent viewIntent1 = new Intent(this, MainActivity1.class);
        startActivity(viewIntent1);

    }

    public void onClick7(View view) {

        Intent viewIntent1 = new Intent(this,AllAppsActivity.class);
        startActivity(viewIntent1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Bacon1.this, "WRITE_CONTACTS granted", Toast.LENGTH_SHORT)
                            .show();


                } else {

                    Toast.makeText(Bacon1.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}








