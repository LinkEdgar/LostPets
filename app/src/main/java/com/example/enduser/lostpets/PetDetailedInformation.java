package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.ArrayList;
import me.relex.circleindicator.CircleIndicator;

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


    //image scroll
    private int totalImages =0;
    private ArrayList<String> urlArrayList = new ArrayList<>();
    ImageSwitcherAdapter adapter;
    ViewPager viewPager;



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
        urlArrayList.add(mPetUrlOneFromIntent);
        urlArrayList.add(mPetUrlTwoFromIntent);
        urlArrayList.add(mPetUrlThreeFromIntent);
    }
    //change name to count images
    private void countImages(){
        for(int x =0 ; x< urlArrayList.size(); x++){
            if(urlArrayList.get(x) != null) {
                if (!urlArrayList.get(x).equals("invalid")) {
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
    // set the variables related to the imageslider up
    private void setupImageSlider(){
        viewPager = (ViewPager) findViewById(R.id.pet_detail_pager);
        adapter = new ImageSwitcherAdapter(PetDetailedInformation.this, urlArrayList, totalImages );
        viewPager.setAdapter(adapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.enter_pet_indicator);
        indicator.setViewPager(viewPager);

    }
}
