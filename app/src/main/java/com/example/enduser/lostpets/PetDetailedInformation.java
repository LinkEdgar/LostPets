package com.example.enduser.lostpets;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class PetDetailedInformation extends AppCompatActivity{
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
    //static map
    private double Latitude;
    private double Longitude;
    private TextView staticMapCity;
    private final static String GOOGLE_STATIC_MAP_BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private final static String GOOGLE_API_KEY ="AIzaSyD5fotiQ4E6IDK56KG5LGwtrkew8v_VIvI";
    private final static String GOOGLE_STATIC_MAP_ZOOM = "&zoom=12";
    //firebase
    private String currentUserUid;
    private String otherUserUid;
    private FirebaseAuth mAuth;
    private Button mFoundButton;

    private ImageView mStaticMap;



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
        getStaticMapParamaters();
        setupMapPicture();


        //found button
        mAuth =FirebaseAuth.getInstance();
        currentUserUid = mAuth.getUid().toString();
        Log.e("detail currentuser", " "+ currentUserUid);
        mFoundButton = (Button) findViewById(R.id.pet_detail_found_pet_button);
        mFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentUserUid.equals(otherUserUid)) {
                    Intent switchActivityIntent = new Intent(PetDetailedInformation.this, MessengerActivity.class);
                    switchActivityIntent.putExtra("userOneId", currentUserUid);
                    switchActivityIntent.putExtra("userTwoId", otherUserUid);
                    switchActivityIntent.putExtra("jointChatId", currentUserUid+otherUserUid);
                    startActivity(switchActivityIntent);
                }
                else{
                    Toast.makeText(PetDetailedInformation.this, "You are the owner of this pet", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
        otherUserUid = intent.getStringExtra("PetOwnerUid");

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
    //sets the static map into the imageview must be called after getStaticMapParamaters
    private void setupMapPicture(){
        mStaticMap = (ImageView) findViewById(R.id.enter_pet_map);
        String staticMapUrl = GOOGLE_STATIC_MAP_BASE_URL+"center="+Latitude+","+Longitude+GOOGLE_STATIC_MAP_ZOOM+"&size=600x300&maptype=rpadmap&key="+GOOGLE_API_KEY;
        Glide.with(PetDetailedInformation.this).load(staticMapUrl).error(R.drawable.no_image).into(mStaticMap);
    }
    // this method gets city, lat,long and other location based info from the zip code using geocode. Used zip code from variable mPetZipFromIntent
    //this method also sets the city textview
    private void getStaticMapParamaters(){
        staticMapCity = (TextView) findViewById(R.id.pet_detail_city_display);
        final Geocoder geocoder = new Geocoder(this);
        try{
            List<Address> addresses = geocoder.getFromLocationName(mPetZipFromIntent,1);
            if(addresses != null && !addresses.isEmpty()){
                Address address = addresses.get(0);
                Latitude = address.getLatitude();
                Longitude = address.getLongitude();
                String city = address.getLocality();
                staticMapCity.setText(city);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
