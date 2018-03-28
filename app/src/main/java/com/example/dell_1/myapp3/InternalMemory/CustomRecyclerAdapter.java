package com.example.dell_1.myapp3.InternalMemory;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dell_1.myapp3.R;

import java.util.ArrayList;

import static com.example.dell_1.myapp3.InternalMemory.InternalStorage.selectallflag;

/**
 * Created by surya on 9/3/18.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private ArrayList<String> nameList, uriList;
    private Context context;

    public CustomRecyclerAdapter() {
    }

    public CustomRecyclerAdapter(ArrayList<String> nameList, ArrayList<String> uriList) {
        this.nameList = nameList;
        this.uriList = uriList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String animal = nameList.get(position);
        String animal2 = uriList.get(position);
        int THUMBSIZE = 150;
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(animal2),
                THUMBSIZE, THUMBSIZE);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(animal2, MediaStore.Video.Thumbnails.MINI_KIND);
        holder.myTextView.setText(animal + "");
        if(animal!= null && (animal.endsWith(".mp3") || animal.endsWith(".aac"))){
            holder.myImage.setImageResource(R.drawable.song);
        }
        else if(animal!= null && animal.endsWith(".pdf")){
            holder.myImage.setImageResource(R.drawable.pdficon2);
        }
        else
        if(animal!= null && (animal.endsWith(".jpeg") || animal.endsWith(".jpg") || animal.endsWith(".png")) && BitmapFactory.decodeFile(animal2)!=null ){
            holder.myImage.setImageBitmap(ThumbImage);
        }
        else
        if(animal!= null && animal.endsWith(".mp4")){
            holder.myImage.setImageBitmap(thumb);
        }
        else
        if(animal!= null && animal.endsWith(".zip")){
            holder.myImage.setImageResource(R.drawable.zip);
        }
        else
        if(animal!= null && animal.endsWith(".txt")){
            holder.myImage.setImageResource(R.drawable.text);
        }
        else if(animal!= null && animal.endsWith(".apk")){
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(animal2, PackageManager.GET_ACTIVITIES);
            if(packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                appInfo.sourceDir = animal2;
                appInfo.publicSourceDir = animal2;
                Drawable icon = appInfo.loadIcon(context.getPackageManager());
                Bitmap bmpIcon = ((BitmapDrawable) icon).getBitmap();
                holder.myImage.setImageBitmap(bmpIcon);
            }

        }
        else {
            holder.myImage.setImageResource(R.drawable.folder);
        }

        if(selectallflag){
            holder.itemView.setBackgroundColor(Color.MAGENTA);
        }
    }

    @Override
    public int getItemCount() {
        return nameList == null ? 0 : nameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageButton myImage;
        ViewHolder(View itemView) {
            super(itemView);
            myImage = (ImageButton) itemView.findViewById(R.id.buttonimage);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
//            myImage.setOnClickListener(this);
//            myImage.setOnLongClickListener(this);
        }

    }

}
