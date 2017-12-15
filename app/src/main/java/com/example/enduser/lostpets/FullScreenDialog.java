package com.example.enduser.lostpets;


import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by EndUser on 12/11/2017.
 */

public class FullScreenDialog extends DialogFragment {
    private String urlToDisplayOne;
    private String urlToDisplayTwo;
    private String urlToDisplayThree;
    private ImageView mImageOne;
    private int mImageCounter = 0, mImagePosition = 0;
    private ImageButton mRightScroll, mLeftScroll;
    private String[] urlArray = new String[3];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_size_image,container,false);
            mImageOne = (ImageView) view.findViewById(R.id.full_size_image_one);
            setUrls();
            imageCounter();
            mRightScroll = (ImageButton) view.findViewById(R.id.full_screen_right_scroll);
            mRightScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagePosition = mImagePosition +1;
                    if(mImagePosition < mImageCounter){
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).into(mImageOne );
                    }
                    else{
                        mImagePosition = mImageCounter -1;
                    }
                }
            });
            mLeftScroll = (ImageButton) view.findViewById(R.id.full_screen_left_scroll);
            mLeftScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagePosition = mImagePosition -1;
                    if(mImagePosition >= 0){
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).noFade().into(mImageOne );
                    }
                    else{
                        mImagePosition = 0;
                    }
                }
            });
        return view;
    }
    //sets the urls and puts them into an array
    private void setUrls(){
        SharedPreferences preferences = getActivity().getSharedPreferences("ImageUrls", Context.MODE_PRIVATE);
        urlToDisplayOne = preferences.getString("UrlOne","invalid");
        urlArray[0] = urlToDisplayOne;
        urlToDisplayTwo = preferences.getString("UrlTwo","invalid");
        urlArray[1] = urlToDisplayTwo;
        urlToDisplayThree = preferences.getString("UrlThree","invalid");
        urlArray[2] = urlToDisplayThree;

    }
    //counts the number of images and loads the first one
    private void imageCounter(){
        if(urlToDisplayOne != "invalid"){
            Picasso.with(getActivity()).load(urlToDisplayOne).into(mImageOne );
            mImageCounter++;
        }
        if(urlToDisplayTwo != "invalid"){
            mImageCounter++;

        }
        if(urlToDisplayThree != "invalid"){
            mImageCounter++;
        }
    }
}
