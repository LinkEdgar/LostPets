package com.example.enduser.lostpets;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {
    //firebase's minimum password requirements
    private final static int FIREBASE_MIN_PASSWORD = 6;

    private FirebaseAuth mAuth;
    //keys for bundle
    private final static String REG_EMAIL = "email";
    private final static String REG_USER_NAME = "username";
    private final static String REG_PASSWORD = "password";
    private final static String REG_PASSWORD_CONFRIM = "confrim_password";
    //setting up variables form the UI fields we need
    private EditText mEmail;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mPasswordConfirm;

    private Button mRegisterBt;
    //TODO fix bug with the back button if the user is logged in
    //TODO figure out if there is going to be a need for username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //finding references for our necessary text fields
        mEmail = (EditText) findViewById(R.id.et_reg_email);
        mUserName = (EditText) findViewById(R.id.et_reg_user_name);
        mPassword =(EditText) findViewById(R.id.et_reg_password);
        mPasswordConfirm = (EditText) findViewById(R.id.et_reg_confirm_pass);
        mRegisterBt = (Button) findViewById(R.id.bt_register_activity_register);

        //Data persistence code
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(REG_EMAIL)){
                mEmail.setText(savedInstanceState.getString(REG_EMAIL));
            }
            if(savedInstanceState.containsKey(REG_USER_NAME)){
                mUserName.setText(savedInstanceState.getString(REG_USER_NAME));
            }
            if(savedInstanceState.containsKey(REG_PASSWORD)){
                mPassword.setText(savedInstanceState.getString(REG_USER_NAME));
            }
            if(savedInstanceState.containsKey(REG_PASSWORD_CONFRIM)){
                mPasswordConfirm.setText(savedInstanceState.getString(REG_PASSWORD_CONFRIM));
            }
        }
        //on click for register button
        mRegisterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.trim will remove space after the text
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mPasswordConfirm.getText().toString().trim();
                    if(validatePassword(password)){
                        if(validatePasswordMatch(password,confirmPassword)){

                            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "Account created successfully. Check your email", Toast.LENGTH_SHORT).show();
                                        //mAuth.signOut();
                                        //since a successful creation logs the user in they will be taken to the main activity
                                        Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Email is invalid or already registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Password is not long enough", Toast.LENGTH_SHORT).show();
                    }
            }
        });
     }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(REG_EMAIL, mEmail.getText().toString());
        outState.putString(REG_USER_NAME,mUserName.getText().toString());
        outState.putString(REG_PASSWORD,mPassword.getText().toString());
        outState.putString(REG_PASSWORD_CONFRIM,mPasswordConfirm.getText().toString());
        super.onSaveInstanceState(outState);
    }
    public boolean validatePasswordMatch(String p1, String p2){
        if(p1.equals(p2)){
            return true;
        }
        return false;
    }
    public boolean validatePassword(String password){
        //firebase's only requirement is that the password be more than 6 characters
        if(password.length() >  FIREBASE_MIN_PASSWORD){
            return true;
        }
        return false;
    }
    public boolean validateEmail(String email){
        //TODO: add code to validate the email
        return true;
    }
}
