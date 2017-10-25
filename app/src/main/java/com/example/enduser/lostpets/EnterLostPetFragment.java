package com.example.enduser.lostpets;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by EndUser on 10/22/2017.
 */

public class EnterLostPetFragment extends Fragment {
    //default constructor
   public EnterLostPetFragment(){

   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //TODO Add spinner logic for the gender
        return inflater.inflate(R.layout.enter_pet,container,false);
    }
}
