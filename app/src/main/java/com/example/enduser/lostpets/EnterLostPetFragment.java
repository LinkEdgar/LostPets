package com.example.enduser.lostpets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by EndUser on 10/22/2017.
 * The user can add pets into the database. A name and a zip code are the minimum requirements to add a pet to the database.
 * The user can choose up to three picture to upload and the pictures to be uploaded are determined by one of three Uri variables.
 * If user choose a picture to upload then the uri is not empty and thus the picture will be uploaded.
 */

public class EnterLostPetFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    private Spinner mGenderSpinner;
    //firebase varaibles
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser mCurrentUser;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    //Edit text fields
    private CheckBox mMicroCheckBox;
    private String petID;
    private EditText petName, petWeight, petZip, petBreed,petDesc;
    private String petGender;
    private String petPictureUrl;
    private String petPictureUrl2;
    private String petPictureUrl3;
    private static final String INVALID_URL = "invalid";
    private boolean isPetMicrochipped;
    private static final int ZIP_CODE_CHAR_LIMIT =5;
    private static final String PET_GENDER_MALE = "Male";
    private static final String PET_GENDER_FEMALE ="Female";
    private static final String PET_GENDER_UNKNOWN  ="Unknown";
    private CheckBox mMicroChipCheckBox;


    //new image selection
    private int SECOND_IMAGE_SELECTOR = 1002;

    private ImageView mCoverImage,mRightImage,mLeftImage;
    private ImageButton cancelFirstImage;
    private ImageButton mLeftImageSelector, mRightImageSelector;
    private TextView userHint;
    private Button imageSelectButton;
    //used to upload the photos via firebase
    private Uri[] uriArray = new Uri[3];
    private int imageCounter = 0;
    //used to decide which picture the user wants to take action on
    private int contextMenuImageToTakeAction;
    //bundle for images
    private final String STRING_URL_ONE = "string_url_one";
    private final String STRING_URL_TWO = "string_url_two";
    private final String STRING_URL_THREE = "string_url_three";
    private String[] stringUrlArray = {STRING_URL_ONE,STRING_URL_TWO,STRING_URL_THREE};


    private static final int IMAGE_UPLOAD_LIMIT = 3;


    //default constructor
    public EnterLostPetFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        final View root_view = inflater.inflate(R.layout.enter_pet,container,false);
        setHasOptionsMenu(true);
        //
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        //gender spinner code
        mGenderSpinner = (Spinner) root_view.findViewById(R.id.gender_spinner);
        //Edit text assignement
        petName = (EditText) root_view.findViewById(R.id.enter_pet_name);
        petBreed = (EditText)root_view.findViewById(R.id.enter_pet_breed);
        petWeight = (EditText) root_view.findViewById(R.id.enter_pet_weight);
        petZip =(EditText) root_view.findViewById(R.id.enter_pet_zip);
        petDesc = (EditText) root_view.findViewById(R.id.enter_pet_desc);
        mMicroCheckBox = (CheckBox) root_view.findViewById(R.id.enter_pet_microchip);
        mMicroCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                   isPetMicrochipped = true;
                }
                else{
                    isPetMicrochipped = false;
                }
            }
        });
        String[] genderValues = {"Unknown", "Male", "Female"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item, genderValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mGenderSpinner.setAdapter(adapter);
        mGenderSpinner.setOnItemSelectedListener(this);
        setUpImageSelect(root_view);
        return root_view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                petGender = PET_GENDER_UNKNOWN;
                break;
            case 1:
                petGender = PET_GENDER_MALE;
                break;
            case 2:
                petGender = PET_GENDER_FEMALE;
                break;
            default:
                petGender = PET_GENDER_UNKNOWN;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        petGender = PET_GENDER_UNKNOWN;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        //If the done icon is clicked the picture will and pet information will be uploaded
        //TODO add a confirmation context menu before submission
        if(itemId == R.id.add_pet_item){
            //This method calls assigns a unique ID for each pet added to the database
             //within this method store data is called which sets the pet info to the database
            initiatePictureUpload();
        }
        return true;
    }

    public boolean storeData(String petNum, String[] petInfo){
        //stores data for each pet by first retrieving it from a passed in array //order matters
        petID = petNum;
        String name = petInfo[0];
        String weight = petInfo[1];;
        String breed = petInfo[2];;
        String zip = petInfo[3];;
        String desc = petInfo[4];;
        String microChip = petInfo[5];
        if(validateData(name, zip)) {
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference("Pets");
            mCurrentUser = mAuth.getCurrentUser();
            mRef.child(petID).child("microchip").setValue(microChip);
            mRef.child(petID).child("name").setValue(name);
            mRef.child(petID).child("breed").setValue(breed);
            mRef.child(petID).child("weight").setValue(weight + " lbs");
            mRef.child(petID).child("zip").setValue(zip);
            mRef.child(petID).child("gender").setValue(petGender);
            mRef.child(petID).child("description").setValue(desc);
            if(petPictureUrl != null){
                mRef.child(petID).child("picture_url").setValue(petPictureUrl);
            }
            else{
                mRef.child(petID).child("picture_url").setValue(INVALID_URL);
            }

            if(petPictureUrl2 != null){
                mRef.child(petID).child("picture_url2").setValue(petPictureUrl2);
            }
            else{
                mRef.child(petID).child("picture_url2").setValue(INVALID_URL);
            }

            if(petPictureUrl3 != null){
                mRef.child(petID).child("picture_url3").setValue(petPictureUrl3);
            }
            else{
                mRef.child(petID).child("picture_url3").setValue(INVALID_URL);
            }


            Toast.makeText(getContext(), "Pet has been added to database", Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            //failure toast
            Toast.makeText(getContext(), "Invalid or empty text fields", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
    //called after succesfully adding a pet to the db. The textfields and imageviews are all cleared
    public void clearTextFields(){
        petBreed.getText().clear();
        petBreed.clearFocus();

        petName.getText().clear();
        petName.clearFocus();

        petWeight.getText().clear();
        petWeight.clearFocus();

        petDesc.getText().clear();
        petDesc.clearFocus();
        petZip.getText().clear();
        petZip.clearFocus();
        mMicroCheckBox.setChecked(false);
        mMicroCheckBox.clearFocus();

        //clear image select text fields and variables
        uriArray[0] = null;
        uriArray[1] = null;
        uriArray[2] = null;
        imageCounter = 0;
        mRightImage.setImageBitmap(null);
        mLeftImage.setImageBitmap(null);
        mRightImage.setVisibility(View.GONE);
        mLeftImage.setVisibility(View.GONE);
        mCoverImage.setImageBitmap(null);
        mCoverImage.setVisibility(View.GONE);
        cancelFirstImage.setVisibility(View.GONE);
        imageSelectButton.setVisibility(View.VISIBLE);
        Log.e("Uri array", uriArray[0]+ " "+ uriArray[1]+" " + uriArray[2]);

    }

    public void assignPetId(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference petId = database.getReference("PetId");
        final String[] petArray = new String[6];
        setPetInfo(petArray);

        petId.addListenerForSingleValueEvent(new ValueEventListener() {
            String petNum;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petNum = dataSnapshot.getValue(String.class);
                //this code will get and unique value for pets
                if(storeData(petNum,petArray)){
                    int convertInt = Integer.parseInt(petNum);
                    convertInt = convertInt +1;
                    String convertedString = Integer.toString(convertInt);
                    petId.setValue(convertedString);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //sets the pet's info for the array so that it can be passed to the storedata method
    private String[] setPetInfo(String[] petInfo){
        petInfo[0] = petName.getText().toString().trim();
        petInfo[1] = petWeight.getText().toString().trim();
        petInfo[2] = petBreed.getText().toString().trim();
        petInfo[3] = petZip.getText().toString().trim();
        petInfo[4] = petDesc.getText().toString().trim();
        petInfo[5] = Boolean.toString(isPetMicrochipped);

        return  petInfo;
    }
    private boolean validateData(String name, String zip){
        if(name == null){
            return false;
        }
        if(zip == null || zip.length() < ZIP_CODE_CHAR_LIMIT){
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == SECOND_IMAGE_SELECTOR && data != null){
                handleImageSelection(data);
            }
        }
        else {

        }



    }
    //TODO figure out how to determine if a photo is rotated to better display in the search
    //sets the url to our petPictureUrl variables based on whether or not they're null so that the right url is matched with the right picture
    private void setImageUrl(UploadTask.TaskSnapshot taskSnapshot){
        if(petPictureUrl == null) {
            petPictureUrl = taskSnapshot.getDownloadUrl().toString();
        }
        else if(petPictureUrl2 == null ){
            petPictureUrl2 = taskSnapshot.getDownloadUrl().toString();
        }
        else /*if(petPictureUriThree == null)*/{
            petPictureUrl3 = taskSnapshot.getDownloadUrl().toString();
        }
    }
    //TODO find out how to save image states in fragments
    //this method resets the urls for new pets being added into the database
    private void resetPetUrls(){
        petPictureUrl = null;
        petPictureUrl2 = null;
        petPictureUrl3 = null;
    }

    private void setUpImageSelect(View root){
        //TODO add context menu for imageviews
        //TODO delete or reimplement user hint

        imageSelectButton = (Button) root.findViewById(R.id.enter_pet_select_image_bt);
        mRightImage = (ImageView) root.findViewById(R.id.enter_pet_right_image_view);
        mCoverImage = (ImageView) root.findViewById(R.id.enter_pet_cover_image_iv);
        mLeftImage =(ImageView) root.findViewById(R.id.enter_pet_left_image);
        registerForContextMenu(mLeftImage);
        mLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextMenuImageToTakeAction = 2;
                mLeftImage.showContextMenu();
            }
        });
        registerForContextMenu(mRightImage);
        mRightImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextMenuImageToTakeAction = 1;
                mRightImage.showContextMenu();
            }
        });
        mRightImageSelector =(ImageButton) root.findViewById(R.id.enter_pet_right_image_select_ib);
        mRightImageSelector.setOnClickListener(this);
        mLeftImageSelector =(ImageButton) root.findViewById(R.id.enter_pet_left_image_select_ib);
        mLeftImageSelector.setOnClickListener(this);
        cancelFirstImage = (ImageButton) root.findViewById(R.id.enter_pet_cancel_selected_button);
        cancelFirstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCounter--;
                if(imageCounter == 0){
                    mCoverImage.setVisibility(View.GONE);
                    mCoverImage.setImageDrawable(null);
                    imageSelectButton.setVisibility(View.VISIBLE);
                    cancelFirstImage.setVisibility(View.GONE);
                    mRightImageSelector.setVisibility(View.GONE);
                    uriArray[imageCounter] = null;
                }
                else if(imageCounter == 1){
                    //swap pictures with main image
                    uriArray[imageCounter-1] = uriArray[imageCounter];
                    Glide.with(getContext()).load(uriArray[imageCounter-1]).into(mCoverImage);

                    mRightImage.setImageBitmap(null);
                    mRightImage.setVisibility(View.GONE);
                    mLeftImageSelector.setVisibility(View.GONE);
                    mRightImageSelector.setVisibility(View.VISIBLE);
                    uriArray[imageCounter] = null;
                }
                else if(imageCounter == 2){
                    //swap between right and center
                    uriArray[imageCounter-2] = uriArray[imageCounter-1];
                    Glide.with(getContext()).load(uriArray[imageCounter-2]).into(mCoverImage);

                    //swap pictures between left and right
                    uriArray[imageCounter-1] = uriArray[imageCounter];
                    Glide.with(getContext()).load(uriArray[imageCounter-1]).into(mRightImage);

                    mLeftImage.setImageBitmap(null);
                    mLeftImageSelector.setVisibility(View.VISIBLE);
                    mLeftImage.setVisibility(View.GONE);
                    uriArray[imageCounter] = null;
                }
            }
        });
        userHint  = (TextView) root.findViewById(R.id.enter_pet_user_select_hint);
        imageSelectButton.setOnClickListener(this);

    }
    private void handleImageSelection(Intent data){

        if(imageCounter == 0) {
            imageSelectButton.setVisibility(View.GONE);
            Bitmap bmp = null;
            Uri imageUri = data.getData();
            setUriAndBitMap(imageUri,mCoverImage);
            mCoverImage.setVisibility(View.VISIBLE);
            //userHint.setVisibility(View.VISIBLE);
            cancelFirstImage.setVisibility(View.VISIBLE);
            mRightImageSelector.setVisibility(View.VISIBLE);
        }
        else if(imageCounter == 1){
            Bitmap bmp = null;
            Uri imageUri = data.getData();
            setUriAndBitMap(imageUri, mRightImage);
            mRightImageSelector.setVisibility(View.GONE);
            mLeftImageSelector.setVisibility(View.VISIBLE);
            mRightImage.setVisibility(View.VISIBLE);
        }
        else if(imageCounter == 2){
            Uri imageUri = data.getData();
            setUriAndBitMap(imageUri, mLeftImage);
            mLeftImageSelector.setVisibility(View.GONE);
            mLeftImage.setVisibility(View.VISIBLE);
            //maybe set some textview indicating at capacity
        }
    }
    private void setUriAndBitMap(Uri uri, ImageView imageButton){
        Glide.with(getContext()).load(uri).into(imageButton);
        uriArray[imageCounter] = uri;
        imageCounter++;
    }
    //if the select images button is clicked previous urls are deleted. The url intent follows
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.enter_pet_select_image_bt){
            if(imageCounter == 0 && petPictureUrl != null){
                resetPetUrls();

            }
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, SECOND_IMAGE_SELECTOR);
    }
    private void  uploadSelectedPictures(){
        //TODO figure out how to determine if a photo is rotated to better display in the search
        Log.e("Uri Array ", " "+uriArray[0]+" "+uriArray[1]+" "+ uriArray[2]);
        Log.e("Uri Array String ", " "+uriArray[0].toString());

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        StorageReference ref = mStorageReference.child("Photos");

        for(int x = 0; x < uriArray.length; x++) {
            if (uriArray[x] != null) {
                if (x + 1 == imageCounter) {
                    progressDialog.show();
                    StorageReference photoRef = mStorageReference.child(uriArray[x].getLastPathSegment());
                    photoRef.putFile(uriArray[x]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            setImageUrl(taskSnapshot);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            assignPetId();
                            clearTextFields();
                        }
                    });
                } else {
                    progressDialog.show();
                    StorageReference photoRef = mStorageReference.child(uriArray[x].getLastPathSegment());
                    photoRef.putFile(uriArray[x]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            setImageUrl(taskSnapshot);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Image Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else{
                break;
            }
        }
    }
    //calls appropriate method to handle adding the pet into the database
    private void initiatePictureUpload(){
        if(imageCounter > 0){
            uploadSelectedPictures();
        }
        else{
            assignPetId();
            clearTextFields();
        }
    }
    //TODO fix the bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        for(int x = 0; x< imageCounter; x++){
            if(uriArray[x] != null){
                String stringUri = uriArray[x].toString();
                outState.putString(stringUrlArray[x],stringUri);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Modify Picture");
        menu.add(0,v.getId(),0,"delete image");
        menu.add(0,v.getId(),0,"make cover image");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       if(item.getTitle() == "delete image"){
           deleteCurrentImageFromSelection();
       }
       else{
           swapCurrentImageToCoverImage();
       }
        return super.onContextItemSelected(item);
    }
    //this method deletes the user picked image and properly arranges the rest
    private void deleteCurrentImageFromSelection(){
        switch(contextMenuImageToTakeAction){
            case 1:
                if(imageCounter > 2){
                    uriArray[1] = uriArray[2];
                    Glide.with(getContext()).load(uriArray[1]).into(mRightImage);
                    mLeftImage.setImageBitmap(null);
                    mLeftImageSelector.setVisibility(View.VISIBLE);
                    uriArray[2] = null;
                }
                else{
                    uriArray[1] = null;
                    mRightImage.setImageBitmap(null);
                    mRightImage.setVisibility(View.GONE);
                    mRightImageSelector.setVisibility(View.VISIBLE);
                    mLeftImageSelector.setVisibility(View.GONE);
                }
                imageCounter--;
                Log.e("uriArray", " " + uriArray[0]+ " " +uriArray[1] + " " +uriArray[2]);
                break;
            case 2:
                uriArray[2] = null;
                mLeftImage.setImageBitmap(null);
                mLeftImage.setVisibility(View.GONE);
                mLeftImageSelector.setVisibility(View.VISIBLE);
                imageCounter--;
                Log.e("ImageCounter", " " + imageCounter);
                Log.e("uriArray", " " + uriArray[0]+ " " +uriArray[1] + " " +uriArray[2]);
                break;
        }
    }
    private void swapCurrentImageToCoverImage(){
        switch (contextMenuImageToTakeAction){
            case 1:
                Uri temp = uriArray[0];
                uriArray[0] = uriArray[1];
                uriArray[1] = temp;
                Glide.with(getContext()).load(uriArray[1]).into(mRightImage);
                Glide.with(getContext()).load(uriArray[0]).into(mCoverImage);
                break;
            case 2:
                temp = uriArray[0];
                uriArray[0] = uriArray[2];
                uriArray[2] = temp;
                Glide.with(getContext()).load(uriArray[2]).into(mLeftImage);
                Glide.with(getContext()).load(uriArray[0]).into(mCoverImage);
                break;
        }
    }
}