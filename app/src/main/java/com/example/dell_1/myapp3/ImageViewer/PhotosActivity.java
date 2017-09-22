package com.example.dell_1.myapp3.ImageViewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.dell_1.myapp3.R;

import static com.example.dell_1.myapp3.ImageViewer.ImageGallery.al_images;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);
        gridView = (GridView)findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, al_images,int_position);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String abc = "file://" + al_images.get(int_position).getAl_imagepath().get(position);

                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                i.putExtra("id", position);
                i.putExtra("abc",abc);
                startActivity(i);
            }
        });
    }
}