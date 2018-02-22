package com.example.enduser.lostpets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by EndUser on 10/23/2017.
 * This class creates and chooses which fragement to display
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager manager){
        super((manager));
    }
    //creates a new fragment based on the position
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new EnterLostPetFragment();
        }
        else{
            return new PetQueryFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    //TODO swap hard coded string for string values
    // the page title for each fragment
    @Override
    public CharSequence getPageTitle(int position) {
       switch(position){
           case 0:
               return "Add Pets";
           case 1:
               return "Find a Pet";
           default:
               return null;

       }

    }
}
