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
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

/**
 * Created by EndUser on 12/11/2017.
 */

public class FullScreenDialog extends DialogFragment {
    //urls
    private String urlToDisplayOne;
    private String urlToDisplayTwo;
    private String urlToDisplayThree;
    private ImageView mImageOne;
    private int mImageCounter = 0, mImagePosition = 0;
    private ImageButton mRightScroll, mLeftScroll;
    //the image the user picked to load from petDetailActitivty
    private int userPickedPicturePosition;
    private String[] urlArray = new String[3];
    //progress bar
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_size_image,container,false);
            mImageOne = (ImageView) view.findViewById(R.id.full_size_image_one);
            mProgressBar = (ProgressBar) view.findViewById(R.id.full_screen_progress);

            setUrls();
            imageCounter();
            setUserClickedImage();
            setRightAndLeftScrolling(view);
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
        userPickedPicturePosition = preferences.getInt("currentPicture",0);

    }
    //counts the number of images and sets the UI accordingly
    private void imageCounter(){
        if(!urlToDisplayOne.equals("invalid")){
            mImageCounter++;
        }
        if(!urlToDisplayTwo.equals("invalid")){
            mImageCounter++;

        }
        if(!urlToDisplayThree.equals("invalid")){
            mImageCounter++;
        }
    }
    //this method set the picture the user chose to display and then sets our imageCounter equal to the user picked position
    //the default value is zero in case the shared preferences didn't work
    private void setUserClickedImage(){
        mProgressBar.setVisibility(View.VISIBLE);
        mImageOne.setVisibility(View.GONE);
        Picasso.with(getActivity()).load(urlArray[userPickedPicturePosition]).into(mImageOne,
                new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(View.GONE);
                        mImageOne.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        mImagePosition = userPickedPicturePosition;
    }

    private void setRightAndLeftScrolling(View view){
        mRightScroll = (ImageButton) view.findViewById(R.id.full_screen_right_scroll);
        mLeftScroll = (ImageButton) view.findViewById(R.id.full_screen_left_scroll);
        if(mImageCounter > 1) {
            mRightScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagePosition = mImagePosition + 1;
                    if (mImagePosition < mImageCounter) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mImageOne.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).into(mImageOne,
                                new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {
                                        mProgressBar.setVisibility(View.GONE);
                                        mImageOne.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    } else {
                        mImagePosition = 0;
                        mProgressBar.setVisibility(View.VISIBLE);
                        mImageOne.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).into(mImageOne,
                                new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {
                                        mProgressBar.setVisibility(View.GONE);
                                        mImageOne.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                }
            });
            mLeftScroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImagePosition = mImagePosition - 1;
                    if (mImagePosition >= 0) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        mImageOne.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).noFade().into(mImageOne,
                                new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {
                                        mProgressBar.setVisibility(View.GONE);
                                        mImageOne.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    } else {
                        mImagePosition = mImageCounter - 1;
                        mProgressBar.setVisibility(View.VISIBLE);
                        mImageOne.setVisibility(View.GONE);
                        Picasso.with(getActivity()).load(urlArray[mImagePosition]).into(mImageOne,
                                new com.squareup.picasso.Callback() {

                                    @Override
                                    public void onSuccess() {
                                        mProgressBar.setVisibility(View.GONE);
                                        mImageOne.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                    }
                }
            });
        }
        else{
            mRightScroll.setVisibility(View.GONE);
            mLeftScroll.setVisibility(View.GONE);
        }
    }
}
