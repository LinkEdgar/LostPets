package com.example.enduser.lostpets;

import android.os.Bundle;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by EndUser on 10/23/2017.
 */

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private SearchView searchView;
    private ImageButton mSearchFilter;
    private TextView mAppName;
    private ImageButton mSubmitPet;

    //Navigation
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private ImageButton mHomeButtonToggle;

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

        //Navigation stuff
        //TODO--> find out what's wrong with the navigation bar since it doesn't close when swiped
        ListView optionsList = (ListView)findViewById(R.id.navigation_list_view);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mAdapter.add("My Pets");
        mAdapter.add("Messages");
        mAdapter.add("Notifications");
        mAdapter.add("Settings");
        optionsList.setAdapter(mAdapter);
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
        Log.e("Bar", "Yeet");
        searchView = (SearchView) findViewById(R.id.searchViewMainActivity);
        mSearchFilter = (ImageButton) findViewById(R.id.main_activity_filter_button);
        mAppName = (TextView) findViewById(R.id.main_activity_app_name);
        mSubmitPet = (ImageButton) findViewById(R.id.main_activity_submit_pet);

        setSupportActionBar(mToolbar);
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
}
