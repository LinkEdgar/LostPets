package com.example.enduser.lostpets;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {
    private EditText userName,password;
    //keys used for data persitence
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private Button signIn, register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //assigning views
        userName = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        signIn = (Button) findViewById(R.id.bt_login);
        register = (Button) findViewById(R.id.bt_register);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(USERNAME)){
                userName.setText(savedInstanceState.getString(USERNAME));
            }
            if(savedInstanceState.containsKey(PASSWORD)){
                password.setText(savedInstanceState.getString(PASSWORD));
            }
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO--> validate credentials and if they are valid login
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO--> set intent to go to register activity
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    //ensure app preserves the data the user input into the fields
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(USERNAME,userName.getText().toString());
        outState.putString(PASSWORD, password.getText().toString());
        super.onSaveInstanceState(outState, outPersistentState);
    }

}
