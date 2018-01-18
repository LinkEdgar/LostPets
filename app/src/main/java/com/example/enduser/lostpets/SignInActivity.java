package com.example.enduser.lostpets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private EditText mUsername,mPassword;
    //keys used for data persistence  
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    //Buttons
    private Button signIn, register;
    //firebase authorization and database reference
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    //Google sign in variables
    private SignInButton googleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 9001;
    //TODO add forgot password functionality

    private String FIREBASE_USERS_ROOT = "Users";
    private String FIREBASE_USERS_EMAIL = "email";
    private String FIREBASE_USERS_NAME = "name";
    private String FIREBASE_USERS_LAST_NAME = "lastname";
    //
    private SharedPreferences.Editor mPreferenceEditor;
    private static String SIGN_IN_PREFERNCES = "signinPreferences";
    private static String GOOGLE_PREFERENCE_KEY = "isUserInDb";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //assigning views
        mUsername = (EditText) findViewById(R.id.et_username);
        mPassword = (EditText) findViewById(R.id.et_password);
        signIn = (Button) findViewById(R.id.bt_login);
        register = (Button) findViewById(R.id.bt_register);
        //preference
        mPreferenceEditor = getSharedPreferences(SIGN_IN_PREFERNCES,MODE_PRIVATE).edit();
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
        mAuth = FirebaseAuth.getInstance();
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
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }
            catch (ApiException e){
                Log.w("onActivityResult", "Google sign in falied", e);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void signInSuccess(){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }
    private void SetupGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        googleSignIn = (SignInButton) findViewById(R.id.google_sign_in);
        googleSignIn.setOnClickListener(this);

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d("Auth with google", "firebaseAuthWithGoole:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful()){
                            Log.d("AuthWithGoogle", "Signin Success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            addGoogleUserToDb(firebaseUser);
                            //switches classes via an intent
                            signInSuccess();
                            finish();
                        }
                        else{
                            Log.d("AuthWithGoogle", "Signin failure");
                            Toast.makeText(SignInActivity.this, "Google Sign-in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                                    finish();
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

        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
    private void addGoogleUserToDb(FirebaseUser firebaseUser){
        boolean userAleardyInDB = getSharedPreferences(SIGN_IN_PREFERNCES,MODE_PRIVATE).getBoolean(GOOGLE_PREFERENCE_KEY,false);
        Log.e("userAlready Exists", " "+ userAleardyInDB);
        if(userAleardyInDB == false){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(FIREBASE_USERS_ROOT);
            reference.child(firebaseUser.getUid()).child(FIREBASE_USERS_EMAIL).setValue(firebaseUser.getEmail());
            reference.child(firebaseUser.getUid()).child(FIREBASE_USERS_NAME).setValue(firebaseUser.getDisplayName());
            mPreferenceEditor.putBoolean(GOOGLE_PREFERENCE_KEY, true);
            mPreferenceEditor.apply();
        }
    }
}
