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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;

/**
 * Created by EndUser on 10/22/2017.
 */

public class EnterLostPetFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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
    private boolean isPetMicrochipped;
    private static final int ZIP_CODE_CHAR_LIMIT =5;
    private static final String PET_GENDER_MALE = "Male";
    private static final String PET_GENDER_FEMALE ="Female";
    private static final String PET_GENDER_UNKNOWN  ="Unknown";
    private CheckBox mMicroChipCheckBoc;
    private Button mUploadPictureButton;
    private ImageView mImageToUploadOne;
    private int REQUEST_IMAGE_GET = 1001;

    //Bundle textfields
    //TODO find out how to bundle image
    private final static String IMAGE_ONE_BUNDLE = "image_one";
    private final static String PET_NAME = "pet_name";
    private final static String PET_BREED = "pet_breed";
    private final static String PET_ZIP = "pet_zip";
    private final static String PET_WEIGHT = "pet_weight";
    private final static String PET_DESCRIPTION = "pet_desc";
    private final static String PET_MIRCOCHIP = "pet_micro";
    private final static String PET_GENDER = "pet_gender";

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
        mImageToUploadOne =(ImageView) root_view.findViewById(R.id.enter_pet_upload_pic_one);
        mUploadPictureButton = (Button) root_view.findViewById(R.id.enter_pet_pic_upload_bt);
        mUploadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        //bundle
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(PET_NAME)){
                petName.setText(savedInstanceState.getString(PET_NAME));
            }
            if(savedInstanceState.containsKey(PET_BREED)){
                petBreed.setText(savedInstanceState.getString(PET_BREED));
            }
            if(savedInstanceState.containsKey(PET_ZIP)){
                petZip.setText(savedInstanceState.getString(PET_ZIP));
            }
            if(savedInstanceState.containsKey(PET_WEIGHT)){
                petWeight.setText(savedInstanceState.getString(PET_WEIGHT));
            }
            if(savedInstanceState.containsKey(PET_DESCRIPTION)){
                petDesc.setText(savedInstanceState.getString(PET_DESCRIPTION));
            }
            if(savedInstanceState.containsKey(PET_GENDER)){
                petGender = savedInstanceState.getString(PET_GENDER);
            }
            if(savedInstanceState.containsKey(PET_MIRCOCHIP)){
                isPetMicrochipped = savedInstanceState.getBoolean(PET_MIRCOCHIP);
            }
        }
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
                assignPetId();
                clearTextFields();

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
            Toast.makeText(getContext(), "Pet has been added to database", Toast.LENGTH_SHORT).show();
            return true;
        }
        else{
            //failure toast
            Toast.makeText(getContext(), "Invalid or empty text fields", Toast.LENGTH_SHORT).show();
            return false;
        }

    }
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
        if(zip == null){
            return false;
        }
        else if(zip.length() < ZIP_CODE_CHAR_LIMIT){
            return false;
        }
        return true;
    }
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_GET);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmp = null;
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_IMAGE_GET && data != null){
                try {
                    Uri imageUri = data.getData();
                    InputStream image = getActivity().getContentResolver().openInputStream(imageUri);
                    bmp = BitmapFactory.decodeStream(image);
                    mImageToUploadOne.setImageBitmap(bmp);
                    uploadImage(imageUri);
                }
                catch(IOException e){
                    Log.e("SetImageOneFromGallery", "Failed to load image ");

                }
            }

            else{

            }
        }

    }
    //bundle fields and images if choosen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(PET_NAME, petName.getText().toString().trim());
        outState.putString(PET_BREED, petBreed.getText().toString().trim());
        outState.putString(PET_ZIP,petZip.getText().toString().trim());
        outState.putString(PET_WEIGHT, petWeight.getText().toString().trim());
        outState.putString(PET_DESCRIPTION, petDesc.getText().toString().trim());
        outState.putString(PET_GENDER, petGender);
        outState.putBoolean(PET_MIRCOCHIP,isPetMicrochipped);
        super.onSaveInstanceState(outState);
    }
    private void uploadImage(final Uri filePath){
        //TODO add elegance to this method! If the image is rotated then unrotate it!
        if(filePath != null){
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
                    Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
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
    private void setImageUrl(UploadTask.TaskSnapshot taskSnapshot){
        petPictureUrl = taskSnapshot.getDownloadUrl().toString();
    }
}