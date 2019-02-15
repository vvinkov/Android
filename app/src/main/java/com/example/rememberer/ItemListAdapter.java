package com.example.rememberer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemListAdapter extends BaseAdapter {

    private Context context;
    protected static ArrayList<Item> items;

    public ItemListAdapter(Context ctx, ArrayList<Item> items){
        this.context = ctx;
        this.items = items;
    }

    public void setData(ArrayList<Item> newItemHistory) {
        items = newItemHistory;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        protected TextView nameTxt;
        protected RatingBar rating;
        protected ImageView image;


    }

    @Override
    public int getCount(){
        return items.size();
    }

    @Override
    public int getViewTypeCount(){
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public Item getItem(int position){
        return items.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final ViewHolder holder;

      //  if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_list_items, null, true);

            holder.nameTxt = convertView.findViewById(R.id.txt_item_name);
            holder.image = convertView.findViewById(R.id.item_image);
            holder.rating = convertView.findViewById(R.id.image_rating);
//        }
//        else{
//            holder = (ViewHolder) convertView.getTag();
//        }

        Item item = items.get(position);
        holder.nameTxt.setText(item.getName());
        holder.rating.setNumStars(5);
        holder.rating.setRating(item.getRating());
        holder.rating.setStepSize(1);
        holder.rating.setIsIndicator(true);
        holder.rating.setScaleX(0.5F);
        holder.rating.setScaleY(0.5F);

        String mCurrentPhotoPath = item.getImg_name();
        if(mCurrentPhotoPath != null &&
            new File(mCurrentPhotoPath).exists() ){

            // Get the dimensions of the View
            int targetW = holder.image.getWidth();
            int targetH = holder.image.getHeight();

            if(targetH == 0)
                targetH = 100;
            if(targetW == 0)
                targetW = 100;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            holder.image.setImageBitmap(bitmap);
        }

        else{
            holder.image.setVisibility(View.INVISIBLE);
        }

        return convertView;

    }

}
