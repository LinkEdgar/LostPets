package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;

public class MessengerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Message> messageArrayList;
    private RecyclerView.LayoutManager mLayoutManager;
    private MessageAdapter mAdapter;
    //firebase variables
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private DatabaseReference mUserRef;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mAuth;
    private String MESSAGE_USERNAME = "userName";
    private String FIREBASE_MESSAGE_CHILD ="message";
    private String FIREBASE_MESSAGE_ROOT = "messages";
    private String FIREBASE_USERS_ROOT = "Users";
    private String FIREBASE_USERS_PROFILE_CHILD = "profileUrl";
    //TODO change firstname to name one the db is updated
    private String FIREBASE_USERS_FIRST_NAME_CHILD = "firstname";


    private String FIREBASE_USER_CHATS_CHILD = "chats";
    //this variable stores the chat number that corresponds to the two users conversations
    private String mJointUserChat = "jointUserChat";
    //userone information
    private String mUserOneProfileUrl;
    private String mUserFirstName;
    private String mUserLastName;
    //user two information
    private String mUserTwoProfileUrl;
    private String mUserTwoName;

    //user one is the current phone user
    private String userOneUid;
    private String userTwoUid;
    //layout button
    private ImageButton mSendMessegeButton;
    private EditText mMessageEditText;


    private HashSet<String> messageKeys;

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

        messageKeys = new HashSet<>();

        mSendMessegeButton = (ImageButton) findViewById(R.id.messenger_send_button);
        mSendMessegeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mMessageEditText = (EditText) findViewById(R.id.messenger_edit_text);

        mDatabase = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        userOneUid = intent.getStringExtra("userOneId");
        mJointUserChat = intent.getStringExtra("jointChatId");
        userTwoUid = intent.getStringExtra("userTwoId");

        getUsersBasicInfo();
        mRef = mDatabase.getReference(FIREBASE_MESSAGE_ROOT).child(mJointUserChat);
        mChildEventListener = mRef.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessageToArrayList(dataSnapshot);
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
    private void sendMessage(){
        String messageToSend = mMessageEditText.getText().toString().trim();
        if(messageToSend != null) {
            if (mChildEventListener != null) {
                mRef.removeEventListener(mChildEventListener);
            }
            //TODO add chat to user profile if the chat is empty
            DatabaseReference specificReference = mRef.push();
            specificReference.child(FIREBASE_MESSAGE_CHILD).setValue(messageToSend);
            specificReference.child(MESSAGE_USERNAME).setValue(mUserFirstName);
            mMessageEditText.setText("");
            specificReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (mChildEventListener != null) {
                        mRef.limitToLast(10).addChildEventListener(mChildEventListener);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void addMessageToArrayList(DataSnapshot snapshot){
        String key = snapshot.getKey();
        if(!messageKeys.contains(key)) {
            messageKeys.add(key);
            String name = snapshot.child(MESSAGE_USERNAME).getValue(String.class);
            String message = snapshot.child(FIREBASE_MESSAGE_CHILD).getValue(String.class);
            Message messageToAdd = new Message();
            //since this name will only be null when the message was just sent and the code run asynchronously
            //we must add the value of user one which will be the current sender
            if(name == null){
                name = mUserTwoName;
            }
            messageToAdd.setName(name);
            messageToAdd.setMessage(message);
            messageArrayList.add(messageToAdd);
            mAdapter.notifyDataSetChanged();
            if (messageArrayList.size() > 1) {
                mRecyclerView.smoothScrollToPosition(messageArrayList.size() - 1);
            }
        }
    }
    private void getUsersBasicInfo(){
        mUserRef = mDatabase.getReference(FIREBASE_USERS_ROOT);
        mUserRef.child(userOneUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                     setUserInfo(true, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(userTwoUid == null){
            //get user id by splitting the chat in half and searching
            StringBuilder builder = new StringBuilder();
            String otherUserUid;
            int chatIdSize = mJointUserChat.length();
            for(int x = 0; x < chatIdSize/2; x++){
                builder.append(mJointUserChat.charAt(x));
            }
            otherUserUid = builder.toString();
            if(userOneUid.equals(otherUserUid)){
                userTwoUid = mJointUserChat.substring(chatIdSize/2,chatIdSize);
            }
            else {
                userTwoUid = otherUserUid;
            }
        }
        mUserRef.child(userTwoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserInfo(false,dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUserInfo(boolean isUserOne, DataSnapshot snapshot){
        if(isUserOne){
            mUserFirstName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
            mUserOneProfileUrl = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        }
        else{
            mUserTwoName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
            mUserTwoProfileUrl = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        }
    }
    private void addChatToUserData(){
        //TODO
    }

    @Override
    protected void onPause() {
        if(mChildEventListener != null) {
            mRef.removeEventListener(mChildEventListener);
        }
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mChildEventListener != null) {
            mRef.limitToLast(10).addChildEventListener(mChildEventListener);
        }
    }
}
