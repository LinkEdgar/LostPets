package com.example.enduser.lostpets;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detailed_information);
        mPetInfoDisplay = (TextView) findViewById(R.id.display_pet_info);
        mPetImageOne = (ImageView) findViewById(R.id.detail_picture_one);
        mPetImageTwo= (ImageView) findViewById(R.id.detail_picture_two);
        mPetImageThree= (ImageView) findViewById(R.id.detail_picture_three);

        Intent intent = getIntent();
        getPetInformationFromIntent(intent);

        mPetInfoDisplay.setText(mPetNameFromIntent + "'s breed is identified by its owner as \" "+ mPetBreedFromIntent +"\", weighs "+ mPetWeightFromIntent+ ", is a " + mPetGenderFromIntent + " ,and is {microchip status goes here} "+ mPetMicrochipStatusFromIntent + " . " + mPetNameFromIntent + " is described by its owners as \"" + mPetDescriptionFromIntent + "\" ." );

        loadPetimages(mPetUrlOneFromIntent, mPetUrlTwoFromIntent,mPetUrlThreeFromIntent);

        mPetImageOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO used shared preference to share urls for the urls which are cashed into memory and load quickly
                if(mPetUrlOneFromIntent != "invalid") {
                    SharedPreferences preferences = getSharedPreferences("ImageUrls", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("UrlOne", mPetUrlOneFromIntent);
                    editor.putString("UrlTwo", mPetUrlTwoFromIntent);
                    editor.putString("UrlThree", mPetUrlThreeFromIntent);
                    editor.apply();
                    DialogFragment dialogFragment = new FullScreenDialog();
                    dialogFragment.show(getFragmentManager(), "Fragment");
                }
            }
        });
    }
    /* TODO figure out a more efficient way to layout these pictures*/
    private void loadPetimages(String url1, String url2, String url3){
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
}
