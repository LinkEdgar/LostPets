package com.example.enduser.lostpets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ZenithPC on 10/23/2017.
 */

public class PetQueryFragment extends Fragment implements PetAdapter.OnItemClicked{
    //firebase related variables
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    //Recyclerview related elements
    private RecyclerView mRecyclerView;
    private PetAdapter mAdapter;
    private RecyclerView.LayoutManager mLayouManager;
    private ArrayList<Pet> petArrayList;
    //TODO progress bar not working properly
    private ProgressBar mloadingBar;
    public PetQueryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.activity_recycler_view,container,false);
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);
        mLayouManager = new LinearLayoutManager(root_view.getContext());
        mRecyclerView.setLayoutManager(mLayouManager);

        mloadingBar = (ProgressBar) root_view.findViewById(R.id.pet_query_progressbar);

        petArrayList = new ArrayList<>();
        mAdapter = new PetAdapter(petArrayList);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClick(this);

        //this method queries all pets and calls getAllPetInfo
        queryAllPets();
        return root_view;
    }
    //performs a general query for all pets refreshing every pet in the database and is performed once on start and again anytime a pet is added to the db
    private void queryAllPets(){
        FirebaseDatabase fullQueryDB = FirebaseDatabase.getInstance();
        DatabaseReference mFullRef = fullQueryDB.getReference("PetId");
        mFullRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String limitString = dataSnapshot.getValue(String.class);
                int limitInt= Integer.parseInt(limitString);
                getAllPetInfo(limitInt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getAllPetInfo(final int maxQueryCount){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Pets");
        mloadingBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addPetsToArrayList(dataSnapshot,maxQueryCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addPetsToArrayList(DataSnapshot dataSnapshot, int maxQueryCount){
        //Test code
        petArrayList.clear();
        int count = 1;
        while(count < maxQueryCount){
            String petBreed = dataSnapshot.child(""+count).child("breed").getValue().toString();
            String petName = dataSnapshot.child(""+count).child("name").getValue().toString();
            String petWeight = dataSnapshot.child(""+count).child(("weight")).getValue().toString();
            String petZip = dataSnapshot.child(""+count).child(("zip")).getValue().toString();
            String petGender = dataSnapshot.child(""+count).child("gender").getValue().toString();
            String petMicro = dataSnapshot.child(""+count).child("microchip").getValue().toString();
            String petDesciption = dataSnapshot.child(""+count).child("description").getValue().toString();
            String petUrl = dataSnapshot.child(""+count).child("picture_url").getValue().toString();

            petArrayList.add(new Pet(petName, petWeight,petGender,petZip, petBreed, petMicro, petDesciption, petUrl));
            count++;
        }
        mloadingBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    //used for recycler view onclick handling
    //investigate if it's worth it to pass the entire object or just the data
    @Override
    public void onItemClick(int position) {
        //use this method to pass the pet to a new activity or a popup menu with detailed information
        Pet pet = mAdapter.getPet(position);
        Intent intent = new Intent(getActivity(),PetDetailedInformation.class);
        String pName = pet.getName();
        String pWeight = pet.getWeight();
        String pGender = pet.getGender();
        String pZIp = pet.getZipCode();
        String pBreed = pet.getBreed();
        String pDesc = pet.getDescription();
        String pMicro = pet.getMicrochip();
        String pUrl = pet.getUrlOne();
        //Pass all the info from the Pet via string in the intent extra
        intent.putExtra("PetName",pName);
        intent.putExtra("PetWeight",pWeight);
        intent.putExtra("PetGender",pGender);
        intent.putExtra("PetZip", pZIp);
        intent.putExtra("PetBreed",pBreed);
        intent.putExtra("PetDescription",pDesc);
        intent.putExtra("PetMicrochip",pMicro);
        intent.putExtra("PetUrlOne", pUrl);

        startActivity(intent);
        //TODO remove this after testing
        Toast.makeText(getContext(), position+" was clicked "+ pet.getName(), Toast.LENGTH_SHORT).show();
    }
}
