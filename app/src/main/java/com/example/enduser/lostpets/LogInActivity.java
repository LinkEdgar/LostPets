package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    private Button getStartedButton;
    private Button signUpButton;
    private ImageView introImage;
    //auth used to check if the user is already signed in
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Since this is the on start activity this code decides whether or not the user is sign in
        // if they are then the main activity will show, otherwise the app will run from the sign in screen
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_log_in);
        introImage = (ImageView) findViewById(R.id.intro_image);
        getStartedButton = (Button) findViewById(R.id.bt_getting_started);
        signUpButton = (Button) findViewById(R.id.bt_sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LogInActivity.this,SignInActivity.class);
                startActivity(intent);

            }
        });

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, AboutActivity.class));
            }
        });

        //Temp code to display pictures at starting screen
        //TEST CODE!
        Glide.with(this).load("http://cdn.akc.org/content/hero/Smiling_Shibas_Hero.jpg").into(introImage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
