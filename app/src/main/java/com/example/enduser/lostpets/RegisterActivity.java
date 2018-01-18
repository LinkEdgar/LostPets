package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    //firebase's minimum password requirements
    private final static int FIREBASE_MIN_PASSWORD = 6;

    private FirebaseAuth mAuth;
    //setting up variables fromm the UI fields we need
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;
    private EditText mFirstName, mLastName;
    private Button mRegisterBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //finding references for our necessary text fields
        mEmail = (EditText) findViewById(R.id.et_reg_email);
        mPassword =(EditText) findViewById(R.id.et_reg_password);
        mPasswordConfirm = (EditText) findViewById(R.id.et_reg_confirm_pass);
        mRegisterBt = (Button) findViewById(R.id.bt_register_activity_register);
        mFirstName = (EditText) findViewById(R.id.register_username_first_name);
        mLastName = (EditText) findViewById(R.id.register_user_last_name);
        //on click for register button
        mRegisterBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //.trim will remove space after the text
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mPasswordConfirm.getText().toString().trim();
                final String firstName = mFirstName.getText().toString().trim();
                final String lastName = mLastName.getText().toString().trim();
                if(firstName.length() > 0 && lastName.length()>0) {
                    if (validatePassword(password)) {
                        if (validatePasswordMatch(password, confirmPassword)) {
                            if (email.isEmpty() == false) {
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            setUserNameAndBasicInfo(user, firstName,lastName);
                                            user.sendEmailVerification();
                                            Toast.makeText(RegisterActivity.this, "Account created successfully. Check your email", Toast.LENGTH_SHORT).show();
                                            //mAuth.signOut();
                                            //since a successful creation logs the user in they will be taken to the main activity
                                            Intent intent = new Intent(RegisterActivity.this, SignInActivity.class);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Email is invalid or already registered", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(RegisterActivity.this, "Password is not long enough", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "First and last names must be valid", Toast.LENGTH_SHORT).show();
                }

            }
        });

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
    //sets display name and other user basic info into the database
    private void setUserNameAndBasicInfo(FirebaseUser user, String firstName, String lastName){
        //TODO add a preference to tell that the user data is present in the database
            String userId = user.getUid().toString();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("Users");
            ref.child(userId).child("email").setValue(user.getEmail());
            ref.child(userId).child("name").setValue(firstName+" "+ lastName);
    }
}
