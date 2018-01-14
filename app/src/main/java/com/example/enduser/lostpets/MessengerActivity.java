package com.example.enduser.lostpets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessengerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Message> messageArrayList;
    private RecyclerView.LayoutManager mLayoutManager;
    private MessageAdapter mAdapter;
    //firebase variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mAuth;
    private String FIREBASE_USER_ID_CHILD ="message";
    private String FIREBASE_MESSAGE_CHILD ="message";
    private String FIREBASE_MESSAGE_ROOT = "messages";
    private String FIREBASE_USERS_ROOT = "Users";
    private String FIREBASE_USERS_PROFILE_CHILD = "profileUrl";
    private String FIREBASE_USERS_FIRST_NAME_CHILD = "firstname";
    private String FIREBASE_USERS_LAST_NAME_CHILD = "lastname";


    private String FIREBASE_USER_CHATS_CHILD = "chats";
    //this variable stores the chat number that corresponds to the two users conversations
    private String mJointUserChat = "jointUserChat";
    private String mUserOneProfileUrl;
    private String mUserTwoProfileUrl;
    private String mUserFirstName;
    private String mUserLastName;
    //user one is the current phone user
    private String userOneUid;
    private String userTwoUid;
    //layout button
    private ImageButton mSendMessegeButton;
    private EditText mMessageEditText;
    //shared preferences for efficiency
    private SharedPreferences.Editor mPreferenceEditor;
    private static String MY_PREFERENCES = "myPreferences";
    private static String DOES_CHAT_EXIST = "doesChatExist";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        //Recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        messageArrayList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessageAdapter(messageArrayList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        //firebase variables
        mAuth = FirebaseAuth.getInstance();
        mPreferenceEditor = getSharedPreferences(MY_PREFERENCES,MODE_PRIVATE).edit();


        mSendMessegeButton = (ImageButton) findViewById(R.id.messenger_send_button);
        mSendMessegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mMessageEditText = (EditText) findViewById(R.id.messenger_edit_text);



        //retrieve messages


        checkIfUserChatExists();
        setUserProfileUrl();



    }
    //looks into the unique chat id and retrieves all the messages. This method calls addMessagesToArrayList to receive
    //the snapshot and add the data to the arraylist
    private void retrieveMessages(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(FIREBASE_MESSAGE_ROOT);
        mRef.child(mJointUserChat).orderByChild("message").addChildEventListener(mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessagesToArrayList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void addMessagesToArrayList(DataSnapshot snapshot){
        String message = snapshot.child(FIREBASE_MESSAGE_CHILD).getValue(String.class);
        String firstName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
        String lastName = snapshot.child(FIREBASE_USERS_LAST_NAME_CHILD).getValue(String.class);
        if(message != null) {
            if(firstName != null) {
                messageArrayList.add(new Message(" ", " " + message, "", firstName, lastName, "invlaid"));
                mAdapter.notifyDataSetChanged();
            }
            //TODO remove this else later
            else{
                messageArrayList.add(new Message(" ", " " + message, "", "Default", "Username", "invlaid"));
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    //sets the joint user chats so users can push their messages to it
    private void setmJointUserChatId(boolean alreadyExists){
        if (alreadyExists == false) {
            DatabaseReference fireRef = FirebaseDatabase.getInstance().getReference(FIREBASE_USERS_ROOT);
            DatabaseReference specificReference = fireRef.child(userOneUid).child(FIREBASE_USER_CHATS_CHILD).push();
            specificReference.setValue(mJointUserChat);
            specificReference = fireRef.child(userTwoUid).child(FIREBASE_USER_CHATS_CHILD).push();
            specificReference.setValue(mJointUserChat);
            mPreferenceEditor.putBoolean(DOES_CHAT_EXIST, true);
            mPreferenceEditor.apply();
        }
    }
    //this method must be called after setmJointUserChatId
    //since it depends on variables initialized in that method
    //This method calls set url which takes the snapshot and sets the url
    //after the second onDataChange is called the messages will be retrieved from 'retrieveMessages'
    //method.
    private void setUserProfileUrl(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_USERS_ROOT);
        databaseReference.child(userOneUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUrlAndName(dataSnapshot, mUserOneProfileUrl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        databaseReference.child(userTwoUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUrlAndName(dataSnapshot, mUserTwoProfileUrl);
                retrieveMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //sets the url from the snapshot
    private void setUrlAndName(DataSnapshot snapshot, String profile){
        profile = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        mUserFirstName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
        mUserFirstName = snapshot.child(FIREBASE_USERS_LAST_NAME_CHILD).getValue(String.class);

    }
    //this goes in parent activity
    private void checkIfUserChatExists(){
        //intent data retrieval
        Intent intent = getIntent();
        userOneUid = intent.getStringExtra("userOneId");
        userTwoUid = intent.getStringExtra("userTwoId");
        mJointUserChat = userOneUid+userTwoUid;
        boolean doesChatExist = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).getBoolean(DOES_CHAT_EXIST,false);
        Log.e("Does chat exist?", " "+ doesChatExist);
        if(doesChatExist == false) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_USERS_ROOT);
            databaseReference.child(mJointUserChat).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (value == null) {
                        setmJointUserChatId(false);
                    } else {
                        setmJointUserChatId(true);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private void sendMessage(){
        String messageToSend = mMessageEditText.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_MESSAGE_ROOT);
        DatabaseReference pushReference = databaseReference.child(mJointUserChat).push();
        pushReference.child(FIREBASE_MESSAGE_CHILD).setValue(messageToSend);
        //pushReference.child(FIREBASE_USER_ID_CHILD).setValue(userOneUid);
        mMessageEditText.setText("");
    }

    @Override
    protected void onPause() {
        mRef.removeEventListener(mChildEventListener);
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mChildEventListener != null) {
            mRef.addChildEventListener(mChildEventListener);
        }
    }
}
