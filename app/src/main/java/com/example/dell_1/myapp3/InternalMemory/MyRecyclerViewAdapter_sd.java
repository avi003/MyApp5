package com.example.dell_1.myapp3.InternalMemory;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dell_1.myapp3.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static com.example.dell_1.myapp3.InternalMemory.SdCardFunctionality.selectallflag;

public class MyRecyclerViewAdapter_sd extends RecyclerView.Adapter<MyRecyclerViewAdapter_sd.ViewHolder> {

    private boolean enableSelection;
    private ArrayList<String> mData;
    private ArrayList<String> mData2;
    private LayoutInflater mInflater;
    private int selected_position;
    private ItemClickListener mClickListener;
    ArrayList<Uri> files = new ArrayList<>();
    Uri uri;
    private static final String TAG = "com.example.dell_1.myapp3.InternalMemory";
    private Context context;
    SdCardFunctionality internal_activity=null;


    public MyRecyclerViewAdapter_sd(Context context, ArrayList<String> data, ArrayList<String> data2,
                                 boolean enableSelection, SdCardFunctionality activity_Internal) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mData2 = data2;
        this.context = context;
        this.enableSelection = enableSelection;
        internal_activity=activity_Internal;
    }

    public void setmData(ArrayList<String> mData) {
        this.mData = mData;
    }



    public void setmData2(ArrayList<String> mData2) {
        this.mData2 = mData2;
    }



    // total number of cells
    @Override
    public int getItemCount() {
        return mData2 == null ? 0 : mData2.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView myTextView;
        ImageButton myImage;

        ViewHolder(View itemView) {
            super(itemView);
            myImage = (ImageButton) itemView.findViewById(R.id.buttonimage);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            myImage.setOnClickListener(this);
            myImage.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            selected_position = getAdapterPosition();

            if(internal_activity.isMultiselected && internal_activity!=null){
                if(internal_activity.multiselect.contains(mData2.get(selected_position))){
                    itemView.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    itemView.setBackgroundColor(Color.LTGRAY);
                }

            }else{
                //single click handle

                if(prevView!=null){
                    itemView.setBackgroundColor(Color.LTGRAY);
                    prevView.setBackgroundColor(Color.TRANSPARENT);
                }
                prevView=itemView;
            }
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
        View prevView=null;
        @Override
        public boolean onLongClick(View view) {

            internal_activity.isMultiselected=true;
            if (internal_activity.isMultiselected) {

                selected_position = getAdapterPosition();
                if (internal_activity.multiselect.contains(mData2.get(selected_position))) {
                    // internal_activity.multiselect.remove(mData2.get(selected_position));
                    itemView.setBackgroundColor(Color.TRANSPARENT);// remove item from list;
                    // update view (v) state here
                    // eg: remove highlight
                } else {
                    // internal_activity.multiselect.add(mData2.get(selected_position));
                    itemView.setBackgroundColor(Color.LTGRAY);
                    // add item to list
                    // update view (v) state here
                    // eg: add highlight
                }
                if (mClickListener != null) mClickListener.onLongClick(view, getAdapterPosition());
            }
            Log.v(TAG, Integer.toString(internal_activity.multiselect.size()) + " this is size");
            return true;
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData2.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

        boolean onLongClick(View view, int position);
    }

    protected void deleteItem() {
        for (int i = 0; i < internal_activity.multiselect.size(); i++) {
            Log.v(TAG, internal_activity.multiselect.get(i));
//            Log.v(TAG, mData2internal_activity.multiselect.get(i));
            File file = new File(internal_activity.multiselect.get(i));
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int j = 0; j < children.length; j++) {
                    new File(file, children[i]).delete();
//                    mData2.remove(internal_activity.multiselect.get(i));
                }
            }
            Log.v(TAG, file.toString());
            file.delete();

            mData2.remove(internal_activity.multiselect.get(i));
            internal_activity.myList2.remove(internal_activity.multiselect.get(i));

            MediaScannerConnection.scanFile(context, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ZAA", "Scanned " + path + ":");
                    Log.i("ZAA", "-> uri=" + uri);
                }
            });
        }
        Toast.makeText(context,"Successfully Deleted", Toast.LENGTH_LONG).show();
        internal_activity.clearMultiSelect(1);
        internal_activity.method1(new File(internal_activity.currentpath));

        notifyDataSetChanged();


    }

    protected void shareItem(View v) {

        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.setType("*/*");
        for (int i = 0; i < internal_activity.multiselect.size(); i++) {
            Log.v(TAG, internal_activity.multiselect.get(i));
//            Log.v(TAG, mData2.get(Integer.parseInt(internal_activity.multiselect.get(i))));
            File file = new File(internal_activity.multiselect.get(i));
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        v.getContext().startActivity(Intent.createChooser(share, "Share file"));

    }

    protected void selectAll() {
        internal_activity.multiselect.addAll(mData2);
        Log.v(TAG, Integer.toString(internal_activity.multiselect.size()));
    }

    protected ArrayList<String> getList() {
        /*    for(String im : internal_activity.multiselect){
                Log.v(TAG, im + " print");
            }
            Log.v(TAG,Integer.toString(internal_activity.multiselect.size()) + "  final size");*/
        return internal_activity.multiselect;
    }

    protected Uri getUri() {
        for (int i = 0; i < internal_activity.multiselect.size(); i++) {
            uri = Uri.fromFile(new File(mData2.get(Integer.parseInt(internal_activity.multiselect.get(i)))));
            Log.v(TAG, uri.toString() + " uri");
        }
        return uri;
    }

    @Override
    public MyRecyclerViewAdapter_sd.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new MyRecyclerViewAdapter_sd.ViewHolder(view);
    }



    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter_sd.ViewHolder holder, int position) {
        String animal = mData.get(position);
//        int THUMBSIZE = 150;
//        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(animal2),
//                THUMBSIZE, THUMBSIZE);
//        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(animal2, MediaStore.Video.Thumbnails.MINI_KIND);
        String animal2 = mData2.get(position);
        holder.myTextView.setText(animal + "");
        if (animal != null && (animal.endsWith(".mp3") || animal.endsWith(".aac"))) {
            holder.myImage.setImageResource(R.drawable.song);
        } else if (animal != null && animal.endsWith(".pdf")) {
            holder.myImage.setImageResource(R.drawable.pdficon2);
        } else if (animal != null && (animal.endsWith(".jpeg") || animal.endsWith(".jpg") || animal.endsWith(".png"))) {//&& BitmapFactory.decodeFile(animal2)!=null ){
//                holder.myImage.setImageBitmap(ThumbImage);

            //Log.e(TAG, "onBindViewHolder: "+ mData2.get(position) );

            Glide.with(context).load(mData2.get(position)).override(100, 100).into(holder.myImage);

        } else if (animal != null && animal.endsWith(".mp4")) {
//                holder.myImage.setImageBitmap(thumb);

            Glide.with(context).load(mData2.get(position)).fitCenter().into(holder.myImage);

        } else if (animal != null && animal.endsWith(".zip")) {
            holder.myImage.setImageResource(R.drawable.zip);
        } else if (animal != null && animal.endsWith(".txt")) {
            holder.myImage.setImageResource(R.drawable.text);
        } else if (animal != null && animal.endsWith(".apk")) {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(animal2, PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                appInfo.sourceDir = animal2;
                appInfo.publicSourceDir = animal2;
                Drawable icon = appInfo.loadIcon(context.getPackageManager());
                Bitmap bmpIcon = ((BitmapDrawable) icon).getBitmap();
                holder.myImage.setImageBitmap(bmpIcon);
            }

        } else {
            holder.myImage.setImageResource(R.drawable.folder);
        }

        if (selectallflag) {
            holder.itemView.setBackgroundColor(Color.MAGENTA);
        }
    }




}
