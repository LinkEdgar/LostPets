package com.example.enduser.lostpets;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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


    //image scroll
    private String[] mUrlArray = new String[3];
    private int mCurrentImage = 0;
    private MyImageSwitcher mImageSwitcher;
    private ImageButton mRightScroll;
    private int totalImages =0;
    private RelativeLayout mShowMorePicuresLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailed_information);
        Intent intent = getIntent();
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
         mImageSwitcher = (MyImageSwitcher) findViewById(R.id.detail_image_switcher);
         mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
             @Override
             public View makeView() {
                 ImageView image = new ImageView(PetDetailedInformation.this);
                 image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                 image.setLayoutParams(
                         new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                 );
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
    }
    //must be called after setMorePicturesLayout()
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
                    suggestClickTV.setText("Click on picture for full screen");
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
            mImageSwitcher.setImageUrl(mUrlArray[mCurrentImage]);
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
