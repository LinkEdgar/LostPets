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

        ArrayList<Pet> myArray = new ArrayList<>();
        updatePets(myArray);



        mAdapter = new PetAdapter(myArray);
        mRecyclerView.setAdapter(mAdapter);

        return root_view;
    }
    public void updatePets(final ArrayList<Pet> array){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("PetId");
        final String[] searchLimt = new String[1];
        //TODO figure out how to successfully retrieve data from the database
        mRef = mDatabase.getReference("Pets").child("2");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String petName = dataSnapshot.child("name").getValue().toString();
                Log.e("retrieve pet info test", ""+ petName);
                //this works to retrieve the info but since it's an anonymous inner class we cannot post the changes to the
                //arraylist 
                array.add(new Pet(petName,"50", "Female", "30168","German Sheppard"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        array.add(new Pet("Linda","50", "Female", "30168","German Sheppard"));
        array.add(new Pet("Linda","50", "Female", "30168","German Sheppard"));
        array.add(new Pet("Linda","50", "Female", "30168","German Sheppard"));
        array.add(new Pet("Linda","50", "Female", "30168","German Sheppard"));
        array.add(new Pet("Linda","50", "Female", "30168","German Sheppard"));
    }
}
