package com.example.dell_1.myapp3.ImageViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

class TouchImageAdapter extends PagerAdapter {
    Context context;
    String filename;
    ArrayList<Model_images> al_menu = new ArrayList<>();
    int position,int_position;

    public TouchImageAdapter(Context context,ArrayList<Model_images> al_menu){
        this.al_menu = al_menu;
        this.context = context;
    }

    @Override
    public int getCount() {
        return al_menu.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, int position) {

        ImageView img = new ImageView(container.getContext());
        ViewGroup.LayoutParams lp= new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img.setLayoutParams(lp);
        img.setImageDrawable(getImageFromSdCard(filename,position));
        container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return img;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    public Drawable getImageFromSdCard(String imageName,int position) {
        Drawable d = null;
        try {
            String path = al_menu.get(int_position).getAl_imagepath().get(position)
                    + "/";
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            d = new BitmapDrawable(context.getResources(),bitmap);
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
        }

        return d;


    }
}