package com.example.enduser.lostpets;

import android.accessibilityservice.GestureDescription;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.support.v4.view.ViewPager;
import android.util.Log;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import java.util.UUID;

/**
 * Created by EndUser on 10/22/2017.
 * The user can add pets into the database. A name and a zip code are the minimum requirements to add a pet to the database.
 * The user can choose up to three picture to upload and the pictures to be uploaded are determined by one of three Uri variables.
 * If user choose a picture to upload then the uri is not empty and thus the picture will be uploaded.
 */

public class EnterLostPetFragment extends Fragment implements AdapterView.OnItemSelectedListener{
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
    private Uri petPictureUriOne;
    private Uri petPictureUriTwo;
    private Uri petPictureUriThree;
    private static final String INVALID_URL = "invalid";
    private boolean isPetMicrochipped;
    private static final int ZIP_CODE_CHAR_LIMIT =5;
    private static final String PET_GENDER_MALE = "Male";
    private static final String PET_GENDER_FEMALE ="Female";
    private static final String PET_GENDER_UNKNOWN  ="Unknown";
    private CheckBox mMicroChipCheckBox;

    private Button mUploadPictureButton;
    private ImageView mImageToUploadOne;
    private ImageView mImageToUploadTwo;
    private ImageView mImageToUploadThree;
    private ImageButton mImageOneCancel;
    private ImageButton mImageTwoCancel;
    private ImageButton mImageThreeCancel;
    private boolean isDoneUploadingImages =false;
    private int mImageUploadCounter = 0;

    private int REQUEST_IMAGE_GET = 1001;

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
        setupPictureVariables(root_view);

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
        if(itemId == R.id.add_pet_item){
            //This method calls assigns a unique ID for each pet added to the database
             //within this method store data is called which sets the pet info to the database
            sortThroughUserSelectedPictures();

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

        petPictureUriOne = null;
        mImageOneCancel.setVisibility(View.INVISIBLE);
        mImageToUploadOne.setImageDrawable(null);
        petPictureUriTwo = null;
        mImageTwoCancel.setVisibility(View.INVISIBLE);
        mImageToUploadTwo.setImageDrawable(null);
        mImageThreeCancel.setVisibility(View.INVISIBLE);
        petPictureUriThree = null;
        mImageToUploadThree.setImageDrawable(null);
        mUploadPictureButton.setText("Upload Pictures");
        mImageUploadCounter = 0;
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
    private void selectImage(){
        if(petPictureUriThree == null || petPictureUriTwo == null || petPictureUriOne == null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
        else{
            Toast.makeText(getContext(), "There's a limit of three pictures per pet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            Log.e("display counter","after intent call "+mImageUploadCounter);
            if(requestCode == REQUEST_IMAGE_GET && data != null){
                imageSelection(data);
            }

        }
        else
        {
            mImageUploadCounter= mImageUploadCounter -1;
            Log.e("display counter","if intent is null "+mImageUploadCounter);
        }



    }
    //this method only runs if the petPictureUri is not null meaning the user has chosen to upload a picture of their pet.
    //after the picture is successfully finished uploading it will clear the text fields
    private void uploadImage(final Uri filePath){
        //TODO figure out how to determine if a photo is rotated to better display in the search
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = mStorageReference.child("Photos");
            StorageReference photoRef = mStorageReference.child(filePath.getLastPathSegment());
            photoRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

    }

    private void uploadTwoImages(Uri filePath, Uri filePath2){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = mStorageReference.child("Photos");
        StorageReference photoRef = mStorageReference.child(filePath.getLastPathSegment());
        photoRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setImageUrl(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image One Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.setTitle("Image One Done... ");

            }
        });


        photoRef = mStorageReference.child(filePath2.getLastPathSegment());
        photoRef.putFile(filePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setImageUrl(taskSnapshot);
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image Two Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                assignPetId();
                clearTextFields();

            }
        });
    }
    private void uploadThreeImages(Uri filePath, Uri filePath2, Uri filePath3){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = mStorageReference.child("Photos");
        StorageReference photoRef = mStorageReference.child(filePath.getLastPathSegment());
        photoRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setImageUrl(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image One Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.setTitle("Image One Done...");

            }
        });


        photoRef = mStorageReference.child(filePath2.getLastPathSegment());
        photoRef.putFile(filePath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                setImageUrl(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image Two Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                progressDialog.setTitle("Image Two Done...");

            }
        });


        photoRef = mStorageReference.child(filePath3.getLastPathSegment());
        photoRef.putFile(filePath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                setImageUrl(taskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image Three Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                assignPetId();
                clearTextFields();
            }
        });
    }

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
    //this method handles all requests to add a new pet image by checking if the associated uris are null
    //this all includes the freedom of choice for the user to pick and choose which photos they want to upload
    private void imageSelection(Intent data){
        //logic tp check if the first picture was chosen
        //TODO find out how to save image states in fragments
        Bitmap bmp = null;

        if(petPictureUriThree != null && petPictureUriTwo == null){
            try {
                Uri imageUri = data.getData();
                InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(image);
                mImageToUploadTwo.setImageBitmap(bmp);
                petPictureUriTwo = imageUri;
                mImageTwoCancel.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("SetImageOneFromGallery", "Failed to load image ");

            }
        }
        else if(petPictureUriThree != null && petPictureUriOne == null){
            try {
                Uri imageUri = data.getData();
                InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(image);
                mImageToUploadOne.setImageBitmap(bmp);
                petPictureUriOne = imageUri;
                Log.e("display counter"," "+mImageUploadCounter);
                mImageOneCancel.setVisibility(View.VISIBLE);
                //TODO add string for this
                mUploadPictureButton.setText("Click to add more");
            } catch (IOException e) {
                Log.e("SetImageOneFromGallery", "Failed to load image ");

            }
        }

        else if( petPictureUriOne == null) {
            try {
                Uri imageUri = data.getData();
                InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(image);
                mImageToUploadOne.setImageBitmap(bmp);
                petPictureUriOne = imageUri;
                        Log.e("display counter"," "+mImageUploadCounter);
                mImageOneCancel.setVisibility(View.VISIBLE);
                //TODO add string for this
                mUploadPictureButton.setText("Click to add more");
            } catch (IOException e) {
                Log.e("SetImageOneFromGallery", "Failed to load image ");

            }
        }
        else if(petPictureUriTwo == null){
            try {
                Uri imageUri = data.getData();
                InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(image);
                mImageToUploadTwo.setImageBitmap(bmp);
                petPictureUriTwo = imageUri;
                mImageTwoCancel.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("SetImageOneFromGallery", "Failed to load image ");

            }
        }
        else if(petPictureUriThree == null){
            try {
                Uri imageUri = data.getData();
                InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                bmp = BitmapFactory.decodeStream(image);
                mImageToUploadThree.setImageBitmap(bmp);
                petPictureUriThree = imageUri;
                mUploadPictureButton.setText("At Picture Capacity");
                mImageThreeCancel.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.e("SetImageOneFromGallery", "Failed to load image ");

            }

        }
    }
    //this method instantiates all variables related to uploading a picture including finding the variables by id through the fragmetn view
    private void setupPictureVariables( View root_view){

        mImageToUploadOne =(ImageView) root_view.findViewById(R.id.enter_pet_upload_pic_one);
        mImageToUploadTwo = (ImageView) root_view.findViewById(R.id.enter_pet_pic_two);
        mImageToUploadThree = (ImageView) root_view.findViewById(R.id.enter_pet_pic_three);

        mUploadPictureButton = (Button) root_view.findViewById(R.id.enter_pet_pic_upload_bt);
        mUploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUploadCounter == 0 && petPictureUrl != null){
                    resetPetUrls();
                }
                if(mImageUploadCounter <= 3) {
                    mImageUploadCounter = mImageUploadCounter +1;
                    Log.e("display counter"," after onclick"+mImageUploadCounter);

                    selectImage();
                }
            }
        });


        mImageOneCancel = (ImageButton) root_view.findViewById(R.id.enter_pet_delete_pic_one);
        mImageOneCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUploadCounter >= 0) {
                    mImageUploadCounter = mImageUploadCounter -1;
                }
                mImageToUploadOne.setImageDrawable(null);
                petPictureUriOne = null;
                mUploadPictureButton.setText("Select more pictures");
            }
        });
        mImageTwoCancel = (ImageButton) root_view.findViewById(R.id.enter_pet_delete_pic_two);
        mImageTwoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUploadCounter >= 0) {
                    mImageUploadCounter = mImageUploadCounter -1;
                }
                mImageToUploadTwo.setImageDrawable(null);
                petPictureUriTwo = null;
                mUploadPictureButton.setText("Select more pictures");
            }
        });
        mImageThreeCancel = (ImageButton) root_view.findViewById(R.id.enter_pet_delete_pic_three);
        mImageThreeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageUploadCounter >= 0) {
                    mImageUploadCounter = mImageUploadCounter -1;
                }
                mImageToUploadThree.setImageDrawable(null);
                petPictureUriThree = null;
                mUploadPictureButton.setText("Select more pictures");
            }
        });
    }
    //this method resets the urls for new pets being added into the database
    private void resetPetUrls(){
        petPictureUrl = null;
        petPictureUrl2 = null;
        petPictureUrl3 = null;
    }
    //handles all cases for user uploaded pictures
    private void sortThroughUserSelectedPictures(){
        if(mImageUploadCounter == 3){
            uploadThreeImages(petPictureUriOne, petPictureUriTwo, petPictureUriThree);
        }
        else if(mImageUploadCounter == 2){
            if(petPictureUriOne != null && petPictureUriTwo != null){
                uploadTwoImages(petPictureUriOne,petPictureUriTwo);
            }
            else if(petPictureUriOne != null && petPictureUriThree != null){
                uploadTwoImages(petPictureUriOne,petPictureUriThree);
            }
            else{
                uploadTwoImages(petPictureUriTwo,petPictureUriThree);
            }
        }
        else if(mImageUploadCounter == 1) {
            if(petPictureUriOne != null){
                uploadImage(petPictureUriOne);
            }
            else if(petPictureUriTwo != null){
                uploadImage(petPictureUriTwo);
            }
            else{
                uploadImage(petPictureUriThree);
            }
        }
        else {
            assignPetId();
            clearTextFields();
        }
    }
}