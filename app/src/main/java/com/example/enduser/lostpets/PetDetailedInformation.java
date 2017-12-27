package com.example.enduser.lostpets;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.events.OnBannerClickListener;
import ss.com.bannerslider.views.BannerSlider;

public class PetDetailedInformation extends AppCompatActivity {
    private String mPetNameFromIntent;
    private String mPetWeightFromIntent;
    private String mPetGenderFromIntent;
    private String mPetZipFromIntent;
    private String mPetBreedFromIntent;
    private String mPetDescriptionFromIntent;
    private String mPetMicrochipStatusFromIntent;
    private String mPetUrlOneFromIntent;
    private String mPetUrlTwoFromIntent;
    private String mPetUrlThreeFromIntent;
    private TextView mPetInfoDisplay;
    private TextView suggestClickTV;
    private ProgressBar mProgressBar;


    //image scroll
    private String[] mUrlArray = new String[3];
    private int mCurrentImage = 0;
    private ImageSwitcher mImageSwitcher;
    private ImageButton mRightScroll;
    private int totalImages =0;
    private RelativeLayout mShowMorePicuresLayout;
    //imageScroller
    private BannerSlider bannerSlider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailed_information);
        Intent intent = getIntent();
        getPetInformationFromIntent(intent);
        setupPetInformation();
        setUrlArray();
        countImages();
        //imageslider
        setupImageSlider();

    }
    //gets all the information from the passed intent extra so that we can display the pet's information in detail
    private void getPetInformationFromIntent(Intent intent){
        //I chose to hard code this because of pickles
        mPetNameFromIntent = intent.getStringExtra("PetName");
        mPetWeightFromIntent = intent.getStringExtra("PetWeight");
        mPetGenderFromIntent = intent.getStringExtra("PetGender");
        mPetZipFromIntent = intent.getStringExtra("PetZip");
        mPetBreedFromIntent = intent.getStringExtra("PetBreed");
        mPetDescriptionFromIntent = intent.getStringExtra("PetDescription");
        mPetMicrochipStatusFromIntent = intent.getStringExtra("PetMicrochip");
        mPetUrlOneFromIntent = intent.getStringExtra("PetUrlOne");
        mPetUrlTwoFromIntent = intent.getStringExtra("PetUrlTwo");
        mPetUrlThreeFromIntent = intent.getStringExtra("PetUrlThree");

    }
    private void setUrlArray(){
        mUrlArray[0] = mPetUrlOneFromIntent;
        mUrlArray[1] = mPetUrlTwoFromIntent;
        mUrlArray[2] = mPetUrlThreeFromIntent;
    }
    //change name to count images
    private void countImages(){
        for(int x =0 ; x< mUrlArray.length; x++){
            if(mUrlArray[x] != null) {
                if (!mUrlArray[x].equals("invalid")) {
                    totalImages++;
                }
            }
        }
    }
    // sets the pet information in the card view
    private void setupPetInformation(){
        mPetInfoDisplay = (TextView) findViewById(R.id.display_pet_info);
        mPetInfoDisplay.setText(mPetNameFromIntent + "'s breed is identified by its owner as \" "
                + mPetBreedFromIntent +"\", weighs "+ mPetWeightFromIntent+ ", is a "
                + mPetGenderFromIntent + " ,and is {microchip status goes here} "
                + mPetMicrochipStatusFromIntent + " . " + mPetNameFromIntent
                + " is described by its owners as \""
                + mPetDescriptionFromIntent + "\" ." );
    }
    private void setupImageSlider(){
        bannerSlider = (BannerSlider) findViewById(R.id.banner_slider);
        List<Banner> banners = new ArrayList<>();
        switch (totalImages){
            case 0:
                banners.add(new DrawableBanner(R.drawable.no_image));
                break;
            case 1:
                banners.add(new RemoteBanner(mPetUrlOneFromIntent));
                break;
            case 2:
                banners.add(new RemoteBanner(mPetUrlOneFromIntent));
                banners.add(new RemoteBanner(mPetUrlTwoFromIntent));
                break;
            case 3:
                banners.add(new RemoteBanner(mPetUrlOneFromIntent));
                banners.add(new RemoteBanner(mPetUrlTwoFromIntent));
                banners.add(new RemoteBanner(mPetUrlThreeFromIntent));
                break;
            default:
                break;
        }
        bannerSlider.setBanners(banners);
        bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                if(!mUrlArray[position].equals("invalid")) {
                    SharedPreferences preferences = getSharedPreferences("ImageUrls", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("UrlOne", mPetUrlOneFromIntent);
                    editor.putString("UrlTwo", mPetUrlTwoFromIntent);
                    editor.putString("UrlThree", mPetUrlThreeFromIntent);
                    editor.putInt("currentPicture", position);
                    editor.apply();
                    DialogFragment dialogFragment = new FullScreenDialog();
                    dialogFragment.show(getFragmentManager(), "Fragment");
                }
            }
        });
    }
}
