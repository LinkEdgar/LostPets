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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    //imageviews
    private ImageView mPetImageOne;
    private ImageView mPetImageTwo;
    private ImageView mPetImageThree;

    //image scroll
    private String[] mUrlArray = new String[3];
    private int mCurrentImage = 0;
    private MyImageSwitcher mImageSwitcher;
    private ImageButton mRightScroll;
    private ImageView THEIMAGEVIEW;
    private int totalImages =0;
    private boolean petHasMoreThanOnePicture =false;
    private RelativeLayout mShowMorePicuresLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailed_information);
        mPetInfoDisplay = (TextView) findViewById(R.id.display_pet_info);
        //mPetImageOne = (ImageView) findViewById(R.id.detail_picture_one);
        //mPetImageTwo= (ImageView) findViewById(R.id.detail_picture_two);
        //mPetImageThree= (ImageView) findViewById(R.id.detail_picture_three);

        Intent intent = getIntent();
        getPetInformationFromIntent(intent);

        mPetInfoDisplay.setText(mPetNameFromIntent + "'s breed is identified by its owner as \" "+ mPetBreedFromIntent +"\", weighs "+ mPetWeightFromIntent+ ", is a " + mPetGenderFromIntent + " ,and is {microchip status goes here} "+ mPetMicrochipStatusFromIntent + " . " + mPetNameFromIntent + " is described by its owners as \"" + mPetDescriptionFromIntent + "\" ." );

        //loadPetImages(mPetUrlOneFromIntent, mPetUrlTwoFromIntent,mPetUrlThreeFromIntent);
    //TODO --add this to new Imageview

        setUrlArray();
        initializeImageSwitcher();
        setImageScrollListener();
        setInitialImage();
        setUpMorePictureLayout();
        //TODO log this to see why it's not working for all the pictures and globalize shared preferences
        mImageSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Imageview", "clicked");
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
    /* TODO figure out a more efficient way to layout these pictures*/
    private void loadPetImages(String url1, String url2, String url3){
        if(!url1.equals("invalid")){
            Picasso.with(this).load(url1).into(mPetImageOne);
        }
        else{
            mPetImageOne.setImageResource(R.drawable.no_image);
        }
        if(!url2.equals("invalid")){
            Picasso.with(this).load(url2).memoryPolicy(MemoryPolicy.NO_STORE).memoryPolicy(MemoryPolicy.NO_CACHE).into(mPetImageTwo);
        }
        else{
            mPetImageTwo.setVisibility(View.GONE);
        }
        if(!url3.equals("invalid")){
            Picasso.with(this).load(url3).memoryPolicy(MemoryPolicy.NO_STORE).memoryPolicy(MemoryPolicy.NO_CACHE).into(mPetImageThree);
        }
        else{
            mPetImageThree.setVisibility(View.GONE);
        }

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
                 THEIMAGEVIEW = new ImageView(PetDetailedInformation.this);
                 THEIMAGEVIEW.setScaleType(ImageView.ScaleType.CENTER_CROP);
                 THEIMAGEVIEW.setLayoutParams(new ImageSwitcher.LayoutParams(mImageSwitcher.getLayoutParams()));
                 return THEIMAGEVIEW;
             }
         });
        mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
    }
    private void setImageScrollListener(){
        mRightScroll =(ImageButton) findViewById(R.id.detail_right_scroll);
        mRightScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentImage++;
                if(mCurrentImage == 3){
                    mCurrentImage =0;
                }
                setCurrentImage();
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

    private void setUpMorePictureLayout(){
        mShowMorePicuresLayout = (RelativeLayout) findViewById(R.id.more_picture_layout);
        for(int x =0 ; x< mUrlArray.length; x++){
            if(!mUrlArray[x].equals("invalid")){
                totalImages++;
            }
        }
        if(totalImages > 1){
            mShowMorePicuresLayout.setVisibility(View.VISIBLE);
        }
    }
}
