package com.example.enduser.lostpets;

/**
 * Created by EndUser on 12/27/2017.
 * This viewpager adapter is used an image scroller and takes in an activity, data set (arraylist), and a counter for the number of images.
 * Since we wish to display an image(default image) in the case a user didn't upload an image we add a condition to geCount.
 * This imageslider uses glide to handle the task of loading and compressing the images being loaded. Since we need full screen capabilities there is an on click listener
 * that creates a dialog fragment to once again loading the image through glide. This transaction shared information through the shared preferences.
 *
 */
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by EndUser on 12/27/2017.
 */

public class ImageSwitcherAdapter extends PagerAdapter implements RequestListener{

    private ArrayList<String> imageUrls;
    private LayoutInflater inflater;
    private Activity activity;
    private int imageCounter;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    // this constructor takes an activity because of the shared preferences method getFragmentManager that has to be called
    public ImageSwitcherAdapter(Activity activity, ArrayList<String> imageUrls, int imageCount){
        this.activity = activity;
        this.imageUrls = imageUrls;
        imageCounter = imageCount;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    //flag to display default image if user didn't upload an image
    @Override
    public int getCount() {
        if(imageCounter == 0){
            return 1;
        }
        else{
            return imageCounter;

        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Context context = activity.getApplicationContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = inflater.inflate(R.layout.image_slider,container,false);
        mImageView = (ImageView) item_view.findViewById(R.id.image_slider_iv);
        mProgressBar = (ProgressBar) item_view.findViewById(R.id.pet_detail_progressbar);
        //imageView.setImageResource(R.mipmap.ic_launcher);
        mProgressBar.setVisibility(View.VISIBLE);

        Glide.with(context).load(imageUrls.get(position)).
        listener(this).error(R.drawable.no_image).into(mImageView);
        //TODO fix the loading toolbar
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!imageUrls.get(position).equals("invalid")) {
                    SharedPreferences preferences = context.getSharedPreferences("ImageUrls", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("UrlOne", imageUrls.get(0));

                        editor.putString("UrlTwo", imageUrls.get(1));

                        editor.putString("UrlThree", imageUrls.get(2));

                    editor.putInt("currentPicture", position);
                    editor.apply();
                    DialogFragment dialogFragment = new FullScreenDialog();
                    dialogFragment.show(activity.getFragmentManager(), "Fragment");
                }
            }
        });
        container.addView(item_view);
        return item_view;
    }

    @Override
    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            mProgressBar.setVisibility(View.GONE);
        return false;
    }
}