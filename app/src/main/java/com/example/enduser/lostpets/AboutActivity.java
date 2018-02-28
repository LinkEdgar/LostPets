package com.example.enduser.lostpets;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/*
    This class provides is meant to give information about the application
 */
public class AboutActivity extends AppCompatActivity {
    private ImageSwitcherAdapter adapter;
    private ViewPager viewPager;
    private ArrayList<String> urlArrayList;
    private int totalImages;
    //TODO fix UI
    //TODO add two pictures, one of linda and one of duky

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        viewPager = (ViewPager) findViewById(R.id.about_activity_pager);
        totalImages = 2;
        urlArrayList = new ArrayList<>();
        urlArrayList.add("http://www.daytondailynews.com/rf/image_lowres/Pub/Web/DaytonDailyNews/Special%20Contents/Links/Images/GettyImages-512366437.jpg");
        urlArrayList.add("https://www.grit.com/-/media/Images/GRT/Editorial/Articles/Magazine-Articles/2012/02-01/Raising-Rabbits-as-Pets/Lop-Eared-Pet-Rabbit.jpg");
        setupImageSlider();
    }
    private void setupImageSlider(){
        adapter = new ImageSwitcherAdapter(AboutActivity.this, urlArrayList, totalImages );
        viewPager.setAdapter(adapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.about_activty_circle_idicator);
        indicator.setViewPager(viewPager);

    }
}
