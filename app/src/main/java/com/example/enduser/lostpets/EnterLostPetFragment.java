package com.example.enduser.lostpets;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by EndUser on 10/22/2017.
 */

public class EnterLostPetFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Spinner mGenderSpinner;
    //default constructor
   public EnterLostPetFragment(){

   }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //TODO Add spinner logic for the gender
        View root_view = inflater.inflate(R.layout.enter_pet,container,false);
        //gender spinner code
        mGenderSpinner = (Spinner) root_view.findViewById(R.id.gender_spinner);
        String[] genderValues = {"Unknown", "Male", "Female"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_item, genderValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mGenderSpinner.setAdapter(adapter);
        return root_view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //TODO--> add logic for selected gender
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
