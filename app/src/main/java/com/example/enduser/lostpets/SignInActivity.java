package com.example.enduser.lostpets;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private EditText mUsername,mPassword;
    //keys used for data persitence
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private Button signIn, register;
    //firebase authorization
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        //assigning views
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_password);
        signIn = (Button) findViewById(R.id.bt_login);
        register = (Button) findViewById(R.id.bt_register);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(USERNAME)){
                mUsername.setText(savedInstanceState.getString(USERNAME));
            }
            if(savedInstanceState.containsKey(PASSWORD)){
                mPassword.setText(savedInstanceState.getString(PASSWORD));
            }
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SignInActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(SignInActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        outState.putString(USERNAME,mUsername.getText().toString());
        outState.putString(PASSWORD, mPassword.getText().toString());
        super.onSaveInstanceState(outState, outPersistentState);
    }

}
