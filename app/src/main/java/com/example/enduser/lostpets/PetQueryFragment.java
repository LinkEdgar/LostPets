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
import android.widget.TextView;
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
    private ProgressBar mloadingBar;
    private TextView mNoPetsFoundTv;
    public PetQueryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.activity_recycler_view,container,false);
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();
        mNoPetsFoundTv = (TextView) root_view.findViewById(R.id.pet_query_no_pet_found);
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
    /*
    if there are pets in the area then they will be queried otherwise a "no pets found" textview will be
    be set to visible
    */
    private void getAllPetInfo(final int maxQueryCount){
        if(maxQueryCount > 1) {
            mNoPetsFoundTv.setVisibility(View.GONE);
            mDatabase = FirebaseDatabase.getInstance();
            mRef = mDatabase.getReference("Pets");
            mloadingBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    addPetsToArrayList(dataSnapshot, maxQueryCount);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            mNoPetsFoundTv.setVisibility(View.VISIBLE);
        }
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
            String petDescription = dataSnapshot.child(""+count).child("description").getValue().toString();
            String petUrl = dataSnapshot.child(""+count).child("picture_url").getValue().toString();
            String petUrl2 = dataSnapshot.child("" + count).child("picture_url2").getValue().toString();;
            String petUrl3 = dataSnapshot.child("" + count).child("picture_url3").getValue().toString();;

            petArrayList.add(new Pet(petName, petWeight,petGender,petZip, petBreed, petMicro, petDescription, petUrl, petUrl2,petUrl3));
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
        String pUrl2 = pet.getUrlTwo();
        String pUrl3 = pet.getUrlThree();
        //Pass all the info from the Pet via string in the intent extra
        intent.putExtra("PetName",pName);
        intent.putExtra("PetWeight",pWeight);
        intent.putExtra("PetGender",pGender);
        intent.putExtra("PetZip", pZIp);
        intent.putExtra("PetBreed",pBreed);
        intent.putExtra("PetDescription",pDesc);
        intent.putExtra("PetMicrochip",pMicro);
        intent.putExtra("PetUrlOne", pUrl);
        intent.putExtra("PetUrlTwo", pUrl2);
        intent.putExtra("PetUrlThree", pUrl3);
        startActivity(intent);
    }
}
