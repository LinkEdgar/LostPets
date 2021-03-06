package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class LogInActivity extends AppCompatActivity {
    private Button getStartedButton;
    private Button signUpButton;
    private ImageView introImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        introImage = (ImageView) findViewById(R.id.intro_image);
        getStartedButton = (Button) findViewById(R.id.bt_getting_started);
        signUpButton = (Button) findViewById(R.id.bt_sign_up);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO--> write explicit intent to switch to getting started activity
            }
        });
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
                Toast toast = Toast.makeText(LogInActivity.this, "Get started", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //Temp code to display pictures at starting screen
        //TEST CODE!
        Picasso.with(this).load("http://cdn.akc.org/content/hero/Smiling_Shibas_Hero.jpg").into(introImage);
    }
}
