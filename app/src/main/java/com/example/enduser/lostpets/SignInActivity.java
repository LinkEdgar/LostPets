package com.example.enduser.lostpets;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private EditText mUsername,mPassword;
    //keys used for data persistence  
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    //Buttons
    private Button signIn, register;
    //firebase authorization and databse reference
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    //Google sign in variables
    private SignInButton googleSignIn;
    GoogleApiClient mGoogleApiClient;
    private final static int RC_SIGN_IN = 9001;


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
        //Bundle for keyfields
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(USERNAME)){
                mUsername.setText(savedInstanceState.getString(USERNAME));
            }
            if(savedInstanceState.containsKey(PASSWORD)){
                mPassword.setText(savedInstanceState.getString(PASSWORD));
            }
        }
        SetupGoogleSignIn();
        // Sign in button code
        SetupSignIn();
        SetupRegistration();
    }
    //ensure app preserves the data the user input into the fields
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(USERNAME,mUsername.getText().toString().trim());
        outState.putString(PASSWORD, mPassword.getText().toString().trim());
        super.onSaveInstanceState(outState, outPersistentState);
    }
    public boolean verifyEmail(String email){
        //TODO implment stronger and better validity check

        if(email != null || email.length() > 0){
            return true;
        }
        return false;
    }
    public boolean verifyPassword(String password){
        //TODO implment stronger and better validity check
        if(password.length() > 6){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }
    //TODO fix google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            //TODO DEVELOPER ERROR in google sign in
            Log.e("YEET 2",""+result.getStatus().toString());
        }
    }
    private  void handleSignInResult(GoogleSignInResult result){
        //TODO -->find way to add users to the database since google sign won't provide this. --> check firebase
        if(result.isSuccess()){
            //get the account for use with this
            GoogleSignInAccount account = result.getSignInAccount();
            signInSuccess();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void signInSuccess(){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void SetupGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        googleSignIn = (SignInButton) findViewById(R.id.google_sign_in);
        googleSignIn.setOnClickListener(this);
    }
    public void SetupSignIn(){
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mUsername.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(verifyEmail(email) && verifyPassword(password)) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    //clears the text fields so that the user's information is protected
                                    ClearTextFields();
                                    signInSuccess();
                                } else {
                                    Toast.makeText(SignInActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignInActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignInActivity.this, "Password or email are invalid", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void SetupRegistration(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    public void ClearTextFields(){
        mUsername.setText("");
        mPassword.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
