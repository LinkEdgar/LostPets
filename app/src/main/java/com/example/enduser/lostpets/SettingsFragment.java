package com.example.enduser.lostpets;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EndUser on 3/8/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat{
    private final int PICK_IMAGE = 2007;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.pref_frag);

        //gets the preference setting for the image selection
        Preference preference = findPreference("image_select");
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == PICK_IMAGE && data != null){
                handleImageSelection(data);
            }
        }
    }
    private void handleImageSelection(Intent data){
        Uri imageUri = data.getData();
        //TODO -->
        //upload image to firebase db profile
    }
}
