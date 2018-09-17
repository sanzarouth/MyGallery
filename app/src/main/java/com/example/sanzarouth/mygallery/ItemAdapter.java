package com.example.sanzarouth.mygallery;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends BaseAdapter {

    private LayoutInflater mInflator;
    private List<Drawable> images;
    protected static final String SELECTED = "selected";
    protected static final String NOT_SELECTED = "not_selected";
    protected static Drawable SELECTED_IMG;
    protected static boolean AT_LEAST_ONE = false;
    private ArrayList<ImageView> allImages = new ArrayList<ImageView>();

    public ItemAdapter(Context c, List<Drawable> images) {
        this.images = images;
        mInflator = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = mInflator.inflate(R.layout.list_item, null);

        final ImageView img = (ImageView) v.findViewById(R.id.landscape);

        img.setImageDrawable(images.get(i));

        if(AT_LEAST_ONE) {
            if(img.getDrawable() == SELECTED_IMG) {
                img.setAlpha(1f);
                setSize(img, 800);
            } else {
                img.setAlpha(0.25f);
                setSize(img, (int) img.getResources().getDimension(R.dimen.imgHeight));
            }
        } else {
            img.setAlpha(1f);
        }

        allImages.add(img);

        return v;
    }

    public ArrayList<ImageView> getAllImages() {
        return allImages;
    }

    public void setSize(ImageView img, int height){
        ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
        layoutParams.height = height;
    }

}
