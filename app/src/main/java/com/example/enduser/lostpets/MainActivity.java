package com.example.enduser.lostpets;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by EndUser on 10/23/2017.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolbar;
    private SearchView searchView;
    private ImageButton mSearchFilter;
    private TextView mAppName;
    private ImageButton mSubmitPet;

    //Navigation
    //user information for navigation drawer
    private String userName;
    private String userProfileUrl;
    private CircleImageView mProfilePicture;
    private TextView mUserName;
    private ProgressBar mUserProfileProgressBar;

    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ImageButton mHomeButtonToggle;
    //Firebase for sign out
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        //sets up adapter and viewpager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.fragment_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        mAuth = FirebaseAuth.getInstance();

        //Navigation drawer reference
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        setupProfileInNavigationView();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mHomeButtonToggle = (ImageButton) findViewById(R.id.navigation);
        mHomeButtonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        //Toolbar stuff
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView) findViewById(R.id.searchViewMainActivity);
        mSearchFilter = (ImageButton) findViewById(R.id.main_activity_filter_button);
        mAppName = (TextView) findViewById(R.id.main_activity_app_name);
        mSubmitPet = (ImageButton) findViewById(R.id.main_activity_submit_pet);
        //sets the custom toolbar for the fragments
        setSupportActionBar(mToolbar);
        /*
         changes the toolbar bases on which fragment is currently being displayed
          this provides a search for the petqueryfragment and a button to add pets to the DB for the
          addpetsfragment

         */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position){
                    case 0:
                        searchView.setVisibility(View.GONE);
                        mSearchFilter.setVisibility(View.GONE);
                        mAppName.setVisibility(View.VISIBLE);
                        mSubmitPet.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        searchView.setVisibility(View.VISIBLE);
                        mSearchFilter.setVisibility(View.VISIBLE);
                        mAppName.setVisibility(View.INVISIBLE);
                        mSubmitPet.setVisibility(View.GONE);
                        break;
                    case 2:
                        mAppName.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.GONE);
                        mSearchFilter.setVisibility(View.GONE);
                        mSubmitPet.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }
    //since we are using a navigation we need to change the back button behavior
    @Override
    public void onBackPressed() {
        if(mDrawer.isDrawerOpen(GravityCompat.START)){
            mDrawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }


    /*
    This determines what happens when an item from the navigation drawer is clicked
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //TODO finish the drawer view settings
        int id = item.getItemId();
        switch (id){
            case R.id.drawer_messages_item:
                startActivity(new Intent(MainActivity.this,MessageListActivity.class));
                return true;
            case R.id.drawer_sign_out:
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this ,SignInActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            case R.id.drawer_settings:
                Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent1);
                return true;
        }
        return false;
    }
    private void setupProfileInNavigationView(){
        //TODO load profile url from preference fragment
        View header = mNavigationView.getHeaderView(0);
        mProfilePicture = (CircleImageView) header.findViewById(R.id.drawer_profile_header_imageview);
        mUserName = (TextView) header.findViewById(R.id.drawer_user_name_tv);
        mUserProfileProgressBar = (ProgressBar) header.findViewById(R.id.drawer_profile_progressbar);
        //user's Uid is used to access their information
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(user);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getProfileInformationForNavigation(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserName.setText("Yeet head");
    }
    //TODO didn't work try again buddy
    private void getProfileInformationForNavigation(DataSnapshot snapshot){
        userName = snapshot.child("name").getValue(String.class);
        userProfileUrl = snapshot.child("profileUrl").getValue(String.class);
        if(userName != null){
            mUserName.setText(userName);
        }
        if(userProfileUrl != null){
            mProfilePicture.setVisibility(View.GONE);
            mUserProfileProgressBar.setVisibility(View.VISIBLE);
            Glide.with(this).load(userProfileUrl).error(R.drawable.no_image).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    mUserProfileProgressBar.setVisibility(View.GONE);
                    mProfilePicture.setVisibility(View.VISIBLE);
                    return false;
                }
            }).into(mProfilePicture);
        }

    }
}
