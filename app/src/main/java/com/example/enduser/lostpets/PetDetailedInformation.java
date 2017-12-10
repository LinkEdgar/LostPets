package com.example.enduser.lostpets;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
        mPetImageTwo= (ImageView) findViewById(R.id.detail_picture_three);

        Intent intent = getIntent();
        //I chose to hard code this because of pickles
        mPetNameFromIntent = intent.getStringExtra("PetName");
        mPetWeightFromIntent = intent.getStringExtra("PetWeight");
        mPetGenderFromIntent = intent.getStringExtra("PetGender");
        mPetZipFromIntent = intent.getStringExtra("PetZip");
        mPetBreedFromIntent = intent.getStringExtra("PetBreed");
        mPetDescriptionFromIntent = intent.getStringExtra("PetDescription");
        mPetMicrochipStatusFromIntent = intent.getStringExtra("PetMicrochip");
        mPetUrlOneFromIntent = intent.getStringExtra("PetUrlOne");

        mPetInfoDisplay.setText(mPetNameFromIntent + "'s breed is identified by its owner as \" "+ mPetBreedFromIntent +"\", weighs "+ mPetWeightFromIntent+ ", is a " + mPetGenderFromIntent + " ,and is {microchip status goes here} "+ mPetMicrochipStatusFromIntent + " . " + mPetNameFromIntent + " is described by its owners as \"" + mPetDescriptionFromIntent + "\" ." );
        /*
        mPetInfoDisplay.append(mPetNameFromIntent + "\n");
        mPetInfoDisplay.append(mPetWeightFromIntent + "\n");
        mPetInfoDisplay.append(mPetGenderFromIntent + "\n");
        mPetInfoDisplay.append(mPetZipFromIntent + "\n");
        mPetInfoDisplay.append(mPetBreedFromIntent + "\n");
        mPetInfoDisplay.append(mPetDescriptionFromIntent + "\n");
        mPetInfoDisplay.append(mPetMicrochipStatusFromIntent + "\n");
        mPetInfoDisplay.append(mPetUrlOneFromIntent + "\n");
        */
        loadPetimages(mPetUrlOneFromIntent);

    }
    private void loadPetimages(String url1){
        if(url1 != "invalid"){
            Picasso.with(this).load(url1).into(mPetImageOne);
        }
    }
}
