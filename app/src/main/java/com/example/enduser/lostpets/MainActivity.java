package com.example.enduser.lostpets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toolbar;

import java.util.List;

/**
 * Created by EndUser on 10/23/2017.
 */

public class MainActivity extends AppCompatActivity {

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

    }
}
