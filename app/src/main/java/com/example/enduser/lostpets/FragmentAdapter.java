package com.example.enduser.lostpets;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by EndUser on 10/23/2017.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(FragmentManager manager){
        super((manager));
    }
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
