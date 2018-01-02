package com.example.enduser.lostpets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
