package com.example.enduser.lostpets;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by ZenithPC on 10/23/2017.
 * This fragment initially attempts to get the user's location to display pets in there postal code
 * if the location of the user is not available it will not display anything. For the queries a hashset is implemented
 * to capture the original keys for each pet in order to prevent duplicate pets. New pet are then added to an arraylist if the key isn't
 * in the hashset. If the user queries again both the hashset and arralist are cleared
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
    private HashSet<String> petsAddedHashSet = new HashSet<>();
    private ProgressBar mloadingBar;
    private TextView mNoPetsFoundTv;
    //query for the search bar in order to re-query once the user reaches the bottom of the recycler view
    private String searchQueryText;
    //location services
    private final int REQUEST_LOCATION = 27;
    private FusedLocationProviderClient mFusedLocationClient;
    private String mUserZipcode;
    //search function related variables
    private SearchView searchView;
    private int typeOfQuery;
    private final static String QUERY_BY_BREED = "breed";
    private final static String QUERY_BY_ZIP = "zip";
    private final static String QUERY_BY_NAME = "name";
    private final static int QUERY_TYPE_ZIP = 1;
    private final static int QUERY_TYPE_NAME =2;
    private final static int QUERY_TYPE_BREED =3;
    private boolean extraOptionMenuInflatedStatus = false;
    private int querySearchLimit = 25;
    //used as a flag to prevent the user from double submitting
    private boolean userDoubleSubmit = false;
    private final static  String LOCATION_NOT_AVAILABLE = "Could not get your location to find pets in your area";
    private final static String LOCATION_HAS_NO_PETS = "No Pets Found in your location. Try using the search function";
    public PetQueryFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.activity_recycler_view,container,false);
        FirebaseApp.initializeApp(root_view.getContext());
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        //location services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        requestUserLocation();

        mNoPetsFoundTv = (TextView) root_view.findViewById(R.id.pet_query_no_pet_found);
        mRecyclerView = (RecyclerView) root_view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(false);
        mLayouManager = new LinearLayoutManager(root_view.getContext());
        mRecyclerView.setLayoutManager(mLayouManager);

        mloadingBar = (ProgressBar) root_view.findViewById(R.id.pet_query_progressbar);

        petArrayList = new ArrayList<>();
        mAdapter = new PetAdapter(petArrayList);
        /*
        mAdapter.hasStableIds();
        mRecyclerView.hasFixedSize();
        */

        mRecyclerView.setAdapter(mAdapter);
        recyclerViewSetScrollListener(mRecyclerView);
        mAdapter.setOnClick(this);

        return root_view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
            else{
                Log.e("Permission granted", "false");

            }

        }
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
    this method queries every pet in the database and adds them to the arraylist
    this method get the number of stored pets via pet id uses it as the limit of pets to query and
    loads them into the array list. As it stands I'm not sure how useful this function is anymore
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
            String petUrl2 = dataSnapshot.child("" + count).child("picture_url2").getValue().toString();
            String petUrl3 = dataSnapshot.child("" + count).child("picture_url3").getValue().toString();

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
    /*
    this method get the  location of user and assumes that the permission is already invoked therefore it is only called on after checking that
    the location permission was given. This method calls for the has an onComplete listener to query pets in the user's area
    */
    private void getLocation(){
        if( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            Address fullAddress = addresses.get(0);
                            mUserZipcode = fullAddress.getPostalCode().toString();
                            Log.i("User Zipcode", mUserZipcode);
                        } catch (IOException e) {
                            Log.e("getLocation", " Could not get location from geocoder");
                        }
                    } else {
                        Log.i("location", "location was equal to null");
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    initialZipQuery();
                }
            });
        }
        else{
            mUserZipcode = null;
            Log.i("getLocation", "Could not get user location");
        }
    }
    @Override
    public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater) {
        if(extraOptionMenuInflatedStatus == true){
            extraOptionMenuInflatedStatus = false;
        }

        inflater.inflate(R.menu.pet_query_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("search");
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extraOptionMenuInflatedStatus == false) {
                    inflater.inflate(R.menu.search_options_menu, menu);
                    extraOptionMenuInflatedStatus = true;
                }
            }
        });
        //Checks if the user double submitted their search and if they didn't then the new result will be displayed
        //the pet arraylist is cleared as well as the hashset used to keep track of the values. The querySearchLimit variable
        //is reset
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(userDoubleSubmit ==false) {
                    userDoubleSubmit = true;
                    petArrayList.clear();
                    petsAddedHashSet.clear();
                    querySearchLimit =25;
                    searchQueryText = searchView.getQuery().toString();
                    if (searchQueryText != null) {
                        if (searchQueryText.length() > 0) {
                            typeOfQueryToPerform(searchQueryText);
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userDoubleSubmit = false;
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.search_option_breed:
                if(typeOfQuery != QUERY_TYPE_BREED){
                    userDoubleSubmit = false;
                }
                typeOfQuery = QUERY_TYPE_BREED;
                searchView.setQueryHint("search by breed");
                searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
            case R.id.search_option_name:
                if(typeOfQuery != QUERY_TYPE_NAME){
                    userDoubleSubmit = false;
                }
                typeOfQuery = QUERY_TYPE_NAME;
                searchView.setQueryHint("search by name");
                searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                break;
            case R.id.search_option_zip:
                if(typeOfQuery != QUERY_TYPE_ZIP){
                    userDoubleSubmit = false;
                }
                typeOfQuery = QUERY_TYPE_ZIP;
                searchView.setQueryHint("search by zip-code");
                searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
        return true;
    }
    //determines
    private void typeOfQueryToPerform(String queryParams){
        switch(typeOfQuery){
            case QUERY_TYPE_BREED:
                performSearchQuery(queryParams, QUERY_BY_BREED);
                break;
            case QUERY_TYPE_NAME:
                performSearchQuery(queryParams,QUERY_BY_NAME);
                break;
            case QUERY_TYPE_ZIP:
                performSearchQuery(queryParams,QUERY_BY_ZIP);
                break;
            default:performSearchQuery(queryParams,QUERY_BY_NAME);
        }
    }
    //calls on queryResults to handle the results and sets the UI accordingly
    private void performSearchQuery(String stringToQuery, String typeOfQuery){
        //TODO: add a way loading bar somehow
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference queryRef = database.getReference("Pets");
        mNoPetsFoundTv.setVisibility(View.VISIBLE);
        mNoPetsFoundTv.setText("No Pets Found");
        queryRef.orderByChild(typeOfQuery).endAt(stringToQuery).startAt(stringToQuery).limitToFirst(querySearchLimit).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                queryResults(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("check", "" +databaseError);

            }
        });
    }
    //handles results of any query and displays search and has a flag to check if the query performed was the first location based search
    private void queryResults(DataSnapshot dataSnapshot){
        Log.e("Key", " "+dataSnapshot.getKey());
        if(!petsAddedHashSet.contains(dataSnapshot.getKey())) {
            petsAddedHashSet.add(dataSnapshot.getKey());
            String petBreed = dataSnapshot.child("breed").getValue(String.class);
            String petName = dataSnapshot.child("name").getValue(String.class);
            String petWeight = dataSnapshot.child(("weight")).getValue(String.class);
            String petZip = dataSnapshot.child(("zip")).getValue(String.class);
            String petGender = dataSnapshot.child("gender").getValue(String.class);
            String petMicro = dataSnapshot.child("microchip").getValue(String.class);
            String petDescription = dataSnapshot.child("description").getValue(String.class);
            String petUrl = dataSnapshot.child("picture_url").getValue(String.class);
            String petUrl2 = dataSnapshot.child("picture_url2").getValue(String.class);
            String petUrl3 = dataSnapshot.child("picture_url3").getValue(String.class);
            petArrayList.add(new Pet(petName, petWeight, petGender, petZip, petBreed, petMicro, petDescription, petUrl, petUrl2, petUrl3));
            mNoPetsFoundTv.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
            //mAdapter.notifyItemInserted();
        }

    }
    //this method is called in onCreate to query pets in the users zip-code if they exist
    private void initialZipQuery(){
        if(mUserZipcode != null){
            performSearchQuery(mUserZipcode,QUERY_BY_ZIP);
            if(petArrayList.size() <1 || petArrayList == null){
                mNoPetsFoundTv.setText(LOCATION_HAS_NO_PETS);
            }
        }
        else{
            mNoPetsFoundTv.setText(LOCATION_NOT_AVAILABLE);
        }
    }

    //invokes "getLocation" if the permission is granted, otherwise it requests the permission
    private void requestUserLocation(){
        if( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLocation();

        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_LOCATION);
        }
    }
    /*
    This method will load more pets if the user hit the pet limit which is arbitrarily set to 25.
    Then the limit is increased by 25 and more pets will be loaded if the amount of pets is greater than or equal
    to the limit
    */
    private void recyclerViewSetScrollListener(RecyclerView mRecyclerView){
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    if(petArrayList.size() >= querySearchLimit){
                        querySearchLimit = querySearchLimit +25;
                        typeOfQueryToPerform(searchQueryText);
                    }
                }
            }
        });
    }
}
