package com.example.sanzarouth.mygallery;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Resources resources;
    private List<Drawable> images;
    private ItemAdapter itemAdapter;
    private ListView lv;

    private Animator fade, unfade;
    private ValueAnimator expand, collapse;
    private LayoutAnimationController animation;

    private boolean alreadyOpen;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ArrayList<Drawable>();
        resources = getApplicationContext().getResources();

        images.add(resources.getDrawable(R.drawable.img1));
        images.add(resources.getDrawable(R.drawable.img2));
        images.add(resources.getDrawable(R.drawable.img9));
        images.add(resources.getDrawable(R.drawable.img7));
        images.add(resources.getDrawable(R.drawable.img4));
        images.add(resources.getDrawable(R.drawable.img5));
        images.add(resources.getDrawable(R.drawable.img6));
        images.add(resources.getDrawable(R.drawable.img8));
        images.add(resources.getDrawable(R.drawable.img10));

        itemAdapter = new ItemAdapter(this, images);

        lv = (ListView) findViewById(R.id.lv);

        animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        lv.setLayoutAnimation(animation);

        lv.setAdapter(itemAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                ImageView img = view.findViewById(R.id.landscape);

                if(img.getTag() == ItemAdapter.SELECTED) {
                    alreadyOpen = true;
                } else {
                    alreadyOpen = false;
                }

                //if image already selected
                if(alreadyOpen) {
                    //collapse all and unfade all
                    ItemAdapter.AT_LEAST_ONE = false;
                    for (int i = 0; i < itemAdapter.getAllImages().size(); ++i) {
                        ImageView image = itemAdapter.getAllImages().get(i);
                        image.setTag(ItemAdapter.NOT_SELECTED);
                        unfade = ObjectAnimator.ofFloat(image, "alpha", 1f);
                        collapse = addAnimation((ImageView) image, (int) img.getResources().getDimension(R.dimen.imgHeight));
                        unfade.start();
                        collapse.start();
                    }
                } else {
                    //collapse and fade all, expand only this one
                    ItemAdapter.AT_LEAST_ONE = true;
                    img.setTag(ItemAdapter.SELECTED);
                    ItemAdapter.SELECTED_IMG = img.getDrawable();
                    unselectAllButOne(itemAdapter.getAllImages(), img);

                    for(int i = 0; i < itemAdapter.getAllImages().size(); i++) {
                        ImageView image = itemAdapter.getAllImages().get(i);
                        if(image.getTag() == ItemAdapter.NOT_SELECTED) {
                            fade = ObjectAnimator.ofFloat(image, "alpha", 0.25f);
                            collapse = addAnimation((ImageView) image, (int) img.getResources().getDimension(R.dimen.imgHeight));
                            fade.start();
                            collapse.start();
                        } else {
                            expand = addAnimation((ImageView) image, (int) img.getResources().getDimension(R.dimen.imgHeight) * 2);
                            unfade = ObjectAnimator.ofFloat(image, "alpha", 1f);
                            expand.start();
                            unfade.start();
                        }
                    }
                }
            }

        });

        ImageButton addImg = findViewById(R.id.button);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View b) {
                Animation a = AnimationUtils.loadAnimation(b.getContext(), R.anim.rotation);
                b.startAnimation(a);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

    }

    public ValueAnimator addAnimation(final ImageView img, int heightValue) {
        ValueAnimator anim = ValueAnimator.ofInt(img.getMeasuredHeight(), heightValue);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
                layoutParams.height = val;
                img.setLayoutParams(layoutParams);
            }
        });
        return anim;
    }

    public void unselectAllButOne(ArrayList<ImageView> images, ImageView selected){
        for (int i = 0; i < images.size(); i++) {
            if(!images.get(i).equals(selected)) {
                images.get(i).setTag(ItemAdapter.NOT_SELECTED);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                Drawable chosenImage = new BitmapDrawable(getResources(), selectedImage);

                images.add(0, chosenImage);
                for (int i = 0; i < itemAdapter.getAllImages().size(); i++) {
                    itemAdapter.getAllImages().get(i).setTag(ItemAdapter.NOT_SELECTED);
                }
                ItemAdapter.AT_LEAST_ONE = false;
                itemAdapter.notifyDataSetChanged();
                lv.scheduleLayoutAnimation();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
