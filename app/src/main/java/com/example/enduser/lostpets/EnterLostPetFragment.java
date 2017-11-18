package com.example.enduser.lostpets;

import android.provider.Contacts;
import android.provider.Settings;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    //Edit text fields
    private String petID;
    private EditText petName, petWeight, petZip, petBreed,petDesc;
    private String petGender;
    private static final String PET_GENDER_MALE = "Male";
    private static final String PET_GENDER_FEMALE ="Female";
    private static final String PET_GENDER_UNKNOWN  ="Unknown";
    //default constructor
    public EnterLostPetFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View root_view = inflater.inflate(R.layout.enter_pet,container,false);
        setHasOptionsMenu(true);
        //
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();
        //gender spinner code
        mGenderSpinner = (Spinner) root_view.findViewById(R.id.gender_spinner);
        //Edit text assignement
        petName = (EditText) root_view.findViewById(R.id.enter_pet_name);
        petBreed = (EditText)root_view.findViewById(R.id.enter_pet_breed);
        petWeight = (EditText) root_view.findViewById(R.id.enter_pet_weight);
        petZip =(EditText) root_view.findViewById(R.id.enter_pet_zip);
        petDesc = (EditText) root_view.findViewById(R.id.enter_pet_desc);
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
            //storeData();
            //sets all fields to null
            clearTextFields();
            Toast.makeText(getContext(), "Pet has been added to database", Toast.LENGTH_SHORT).show();

        }
        return true;
    }

    public void storeData(String petNum, String[] petInfo){
        //stores data for each pet by first retrieving it from a passed in array //order matters
        petID = petNum;
        String name = petInfo[0];
        String weight = petInfo[1];;
        String breed = petInfo[2];;
        String zip = petInfo[3];;
        String desc = petInfo[4];;
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Pets");
        mCurrentUser = mAuth.getCurrentUser();

        mRef.child(petID).child("name").setValue(name);
        mRef.child(petID).child("breed").setValue(breed);
        mRef.child(petID).child("weight").setValue(weight);
        mRef.child(petID).child("zip").setValue(zip);
        mRef.child(petID).child("description").setValue(desc);

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
    }

    public void assignPetId(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference petId = database.getReference("PetId");
        final String[] petArray = new String[5];
        setPetInfo(petArray);

        petId.addListenerForSingleValueEvent(new ValueEventListener() {
            String petNum;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                petNum = dataSnapshot.getValue(String.class);
                //this code will get and unique value for pets
                int convertInt = Integer.parseInt(petNum);
                convertInt = convertInt +1;
                String convertedString = Integer.toString(convertInt);
                petId.setValue(convertedString);
                storeData(petNum, petArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //sets the pet's info for the array so that it can be passed to the storedata method
    public String[] setPetInfo(String[] petInfo){
        petInfo[0] = petName.getText().toString().trim();
        petInfo[1] = petWeight.getText().toString().trim();
        petInfo[2] = petBreed.getText().toString().trim();
        petInfo[3] = petZip.getText().toString().trim();
        petInfo[4] = petDesc.getText().toString().trim();

        return  petInfo;
    }
}