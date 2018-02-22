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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


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
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        /*
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mAdapter.add("My Pets");
        mAdapter.add("Messages");
        mAdapter.add("Notifications");
        mAdapter.add("Settings");
        //optionsList.setAdapter(mAdapter);
        */
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
        }
        return false;
    }
}
