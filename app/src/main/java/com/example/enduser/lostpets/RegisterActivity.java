package com.example.enduser.lostpets;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    //keys for bundle
    private final static String REG_NAME = "name";
    private final static String REG_EMAIL = "email";
    private final static String REG_USER_NAME = "username";
    private final static String REG_PASSWORD = "password";
    private final static String REG_PASSWORD_CONFRIM = "confrim_password";
    //setting up variables form the UI fields we need
    private EditText mName;
    private EditText mEmail;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mPasswordConfirm;

    private Button mRegisterBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //finding references for our necessary text fields
        mName = (EditText) findViewById(R.id.et_reg_name);
        mEmail = (EditText) findViewById(R.id.et_reg_email);
        mUserName = (EditText) findViewById(R.id.et_reg_user_name);
        mPassword =(EditText) findViewById(R.id.et_reg_password);
        mPasswordConfirm = (EditText) findViewById(R.id.et_reg_confirm_pass);

        //Data persistence code
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(REG_NAME)){
                mName.setText(savedInstanceState.getString(REG_NAME));
            }
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
     }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(REG_NAME,mName.getText().toString());
        outState.putString(REG_EMAIL, mEmail.getText().toString());
        outState.putString(REG_USER_NAME,mUserName.getText().toString());
        outState.putString(REG_PASSWORD,mPassword.getText().toString());
        outState.putString(REG_PASSWORD_CONFRIM,mPasswordConfirm.getText().toString());
        super.onSaveInstanceState(outState);
    }
}
