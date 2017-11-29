package com.example.enduser.lostpets;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ZenithPC on 10/23/2017.
 */

public class PetQueryFragment extends Fragment {
    //firebase related variables
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    //Recyclerview related elements
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayouManager;
    private ArrayList<Pet> petArrayList;
    private ProgressDialog mloadingPetProgressBar;
    public PetQueryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.activity_pet_query,container,false);
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();
        mloadingPetProgressBar = new ProgressDialog(getContext());

        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayouManager = new LinearLayoutManager(root_view.getContext());
        mRecyclerView.setLayoutManager(mLayouManager);

        petArrayList = new ArrayList<>();
        //mAdapter = new PetAdapter(petArrayList);

        mRecyclerView.setAdapter(mAdapter);
        //this method queries all pets and calls getAllPetInfo
        queryAllPets();
        return root_view;
    }

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
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test code
                mloadingPetProgressBar.setMessage("Loading Pets");
                mloadingPetProgressBar.show();
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

                //adds fake pets for funzies
                /*
                petArrayList.add(new Pet("Winda","50", "Female", "30168","German Sheppard"));
                petArrayList.add(new Pet("Doby","30", "Male", "30168","Mix"));
                petArrayList.add(new Pet("Sombra","55", "Female", "30168","Lab"));
                petArrayList.add(new Pet("Binx","14", "Female", "30168","yeet ass dog"));
                petArrayList.add(new Pet("Sofie","8", "Female", "30168","German Sheppard"));
                */
                mloadingPetProgressBar.dismiss();
                mRecyclerView.setAdapter(new PetAdapter(petArrayList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
