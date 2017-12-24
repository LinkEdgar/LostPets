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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailed_information);
        Intent intent = getIntent();
        mProgressBar = (ProgressBar) findViewById(R.id.pet_detail_progress_bar);
        getPetInformationFromIntent(intent);
        setupPetInformation();
        setUrlArray();
        setUpMorePicturesLayout();
        initializeImageSwitcher();
        setImageScrollListener();
        setInitialImage();
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
    private void initializeImageSwitcher(){
         mImageSwitcher = (ImageSwitcher) findViewById(R.id.detail_image_switcher);
         mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
             @Override
             public View makeView() {
                 ImageView image = new ImageView(PetDetailedInformation.this);
                 return image;
             }
         });
        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mUrlArray[mCurrentImage].equals("invalid")) {
                    SharedPreferences preferences = getSharedPreferences("ImageUrls", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("UrlOne", mPetUrlOneFromIntent);
                    editor.putString("UrlTwo", mPetUrlTwoFromIntent);
                    editor.putString("UrlThree", mPetUrlThreeFromIntent);
                    editor.putInt("currentPicture", mCurrentImage);
                    editor.apply();
                    DialogFragment dialogFragment = new FullScreenDialog();
                    dialogFragment.show(getFragmentManager(), "Fragment");
                }
            }
        });
        //TODO finish this
        mImageSwitcher.setOnTouchListener(null);
    }

    //must be called after setMore
    private void setImageScrollListener(){
        mRightScroll =(ImageButton) findViewById(R.id.detail_right_scroll);
        mRightScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentImage++;
                if(mCurrentImage == totalImages){
                    mCurrentImage =0;
                }
                setCurrentImage();
                if(mCurrentImage %2 != 0){
                    suggestClickTV.setText("Click on picture to expand");
                }
                else{
                    suggestClickTV.setText("See more pictures");
                }
            }
        });
    }
    private void setInitialImage(){
        setCurrentImage();
    }
    private void setCurrentImage() {
        if (mCurrentImage > 0 && mUrlArray[mCurrentImage].equals("invalid")) {

        }
        else {
            mProgressBar.setVisibility(View.VISIBLE);
            Glide.with(PetDetailedInformation.this)
                    .load(mUrlArray[mCurrentImage])
                    .asBitmap()
                    .error(R.drawable.no_image)
                    .centerCrop()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mImageSwitcher.setImageDrawable(new BitmapDrawable(getResources(),resource));
                            return true;
                        }
                    }).into((ImageView) mImageSwitcher.getCurrentView());

        }

    }
    private void setUrlArray(){
        mUrlArray[0] = mPetUrlOneFromIntent;
        mUrlArray[1] = mPetUrlTwoFromIntent;
        mUrlArray[2] = mPetUrlThreeFromIntent;
    }
    //This method sets the view that suggests more images to the user by first going through the array and checking if at least two urls aren't invalid
    private void setUpMorePicturesLayout(){
        mShowMorePicuresLayout = (RelativeLayout) findViewById(R.id.more_picture_layout);
        suggestClickTV = (TextView) findViewById(R.id.enter_pet_detail_switch_click_suggestion);
        for(int x =0 ; x< mUrlArray.length; x++){
            if(!mUrlArray[x].equals("invalid")){
                totalImages++;
            }
        }
        if(totalImages > 1){
            mShowMorePicuresLayout.setVisibility(View.VISIBLE);
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
}
