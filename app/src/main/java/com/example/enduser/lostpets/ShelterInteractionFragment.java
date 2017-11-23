package com.example.enduser.lostpets;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShelterInteractionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ShelterInteractionFragment extends Fragment {
    private Button mSignOutButton;
    private FirebaseAuth mAuth;

    public ShelterInteractionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root_view = inflater.inflate(R.layout.fragment_shelter_interaction,container,false);
        mAuth = FirebaseAuth.getInstance();
        mSignOutButton = (Button) root_view.findViewById(R.id.shelter_sign_out);

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity() ,SignInActivity.class);
                startActivity(intent);
            }
        });

        return root_view;
    }


}
