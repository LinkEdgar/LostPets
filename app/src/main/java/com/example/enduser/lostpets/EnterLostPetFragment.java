package com.example.enduser.lostpets;

import android.provider.Contacts;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.RemoteInput;
import android.support.v4.view.ViewPager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        //TODO Add spinner logic for the gender
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
        //TODO--> add logic for selected gender
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
            storeData();
            //sets all fields to null
            clearTextFields();
            Toast.makeText(getContext(), "Pet has been added to database", Toast.LENGTH_SHORT).show();

        }
        return true;
    }

    public void storeData(){
        String name = petName.getText().toString().trim();
        String weight = petWeight.getText().toString().trim();
        String breed = petBreed.getText().toString().trim();
        String zip = petZip.getText().toString().trim();
        String desc = petDesc.getText().toString().trim();
        //TODO --> findout how to better structure pets when adding
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Pets");
        mCurrentUser = mAuth.getCurrentUser();
        //use UID to uniquely identify
        String Uid = mCurrentUser.getUid().toString();
        mRef.child(Uid).child("name").setValue(name);
        mRef.child(Uid).child("breed").setValue(breed);
        mRef.child(Uid).child("gender").setValue(petGender);
        mRef.child(Uid).child("weight").setValue(weight);
        mRef.child(Uid).child("zip").setValue(zip);
        mRef.child(Uid).child("description").setValue(desc);




    }
    public void clearTextFields(){
        petBreed.setText(null);
        petName.setText(null);
        petWeight.setText(null);
        petDesc.setText(null);
        petZip.setText(null);
    }
    //TODO make a unique pet id by putting the number 1 in the realtime database. retrieve that number convert it to an long. Then add one to it each time a new pet is added.
    // afterwards convert it to string and update it in the realtime database thus we will always have a a unique pet id to identify each pet.
    
}