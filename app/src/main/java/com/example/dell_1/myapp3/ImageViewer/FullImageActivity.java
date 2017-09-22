package com.example.dell_1.myapp3.ImageViewer;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dell_1.myapp3.R;

import static com.example.dell_1.myapp3.ImageViewer.ImageGallery.al_images;

public class FullImageActivity extends AppCompatActivity {
    ImageView images;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        Intent i = getIntent();
        images = (ImageView) findViewById(R.id.fullImage);
        images.setVisibility(View.GONE);

        // Selected image id
        position = i.getExtras().getInt("id");
        Bundle extras = getIntent().getExtras();
        String value = extras.getString("abc");

        Glide.with(FullImageActivity.this)
                .load(value)
                .skipMemoryCache(false)
                .into(images);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TouchImageAdapter(this,al_images));
        mViewPager.setCurrentItem(0);
    }
}
