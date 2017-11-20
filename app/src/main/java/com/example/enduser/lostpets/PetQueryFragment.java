package com.example.enduser.lostpets;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    public PetQueryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.activity_pet_query,container,false);
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayouManager = new LinearLayoutManager(root_view.getContext());
        mRecyclerView.setLayoutManager(mLayouManager);

        petArrayList = new ArrayList<>();
        getPetInfo(petArrayList);



        //mAdapter = new PetAdapter(petArrayList);
        //mRecyclerView.setAdapter(mAdapter);

        return root_view;
    }
    public void getPetInfo(final ArrayList<Pet> array){
        mDatabase = FirebaseDatabase.getInstance();
        //TODO figure out how to successfully retrieve data from the database
        mRef = mDatabase.getReference("Pets");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Test code
               int count = 1;
               while(count < 3){
                   String iterate = dataSnapshot.child(""+count).child("name").getValue().toString();
                   petArrayList.add(new Pet(iterate, "11","male","11111", "Mix Yeet"));
                   count++;
               }


                petArrayList.add(new Pet("Winda","50", "Female", "30168","German Sheppard"));
                petArrayList.add(new Pet("Toby","30", "Male", "30168","Mix"));
                petArrayList.add(new Pet("Sombra","55", "Female", "30168","Lab"));
                petArrayList.add(new Pet("Binx","14", "Female", "30168","yeet ass dog"));
                petArrayList.add(new Pet("Sofie","8", "Female", "30168","German Sheppard"));

                mRecyclerView.setAdapter(new PetAdapter(petArrayList));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        displayPetInfo("yeet");


    }
    private void displayPetInfo(String petName){
        petArrayList.add(new Pet(petName,"50", "Female", "30168","German Sheppard"));
        petArrayList.add(new Pet("Winda","50", "Female", "30168","German Sheppard"));
        petArrayList.add(new Pet("Toby","30", "Male", "30168","Mix"));
        petArrayList.add(new Pet("Sombra","55", "Female", "30168","Lab"));
        petArrayList.add(new Pet("Binx","14", "Female", "30168","yeet ass dog"));
        petArrayList.add(new Pet("Sofie","8", "Female", "30168","German Sheppard"));
    }
}
