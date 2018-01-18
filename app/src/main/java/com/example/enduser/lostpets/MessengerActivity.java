package com.example.enduser.lostpets;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    //shared preferences for efficiency
    private SharedPreferences.Editor mPreferenceEditor;
    private static String MY_PREFERENCES = "myPreferences";
    private static String DOES_CHAT_EXIST = "doesChatExist";
    private boolean didUserSendMessage = false;


    private HashSet<String> messageKeys;




    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        private TextView mMessageTextView;
        private ImageView mProfilePicture;
        private TextView mUserName;
        private View layout;
        public MessageViewHolder(View v){
            super(v);
            layout = v;
            mMessageTextView = (TextView) layout.findViewById(R.id.message_item_user_message);
            mProfilePicture = (ImageView) layout.findViewById(R.id.message_item_profile_picture);
            mUserName = (TextView) layout.findViewById(R.id.message_item_user_name);
        }
    }

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
        Log.e("intent data", userOneUid);
        mJointUserChat = intent.getStringExtra("jointChatId");
        userTwoUid = intent.getStringExtra("userTwoId");

        getUsersBasicInfo();

        /*
        initializeUserData();
        //retrieve messages


        checkIfUserChatExists();
        //setUserProfileUrl();
        */
        mRef = mDatabase.getReference("messages").child(mJointUserChat);
        mChildEventListener = mRef.limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMessageToArrayList(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                addMessageToArrayList(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                addMessageToArrayList(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(){
        if(mChildEventListener != null){
            mRef.removeEventListener(mChildEventListener);
        }
        String messageToSend = mMessageEditText.getText().toString().trim();
        DatabaseReference specificReference = mRef.push();
        Log.e("message to send", messageToSend);
        specificReference.child("message").setValue(messageToSend);
        specificReference.child("userName").setValue(userOneUid);
        mMessageEditText.setText("");
        specificReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(mChildEventListener != null){
                    mRef.limitToLast(10).addChildEventListener(mChildEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void retrieveMessages(){
        mRef.limitToLast(10).addChildEventListener(mChildEventListener);
    }

    private void addMessageToArrayList(DataSnapshot snapshot){
        String key = snapshot.getKey();
        if(!messageKeys.contains(key)) {
            messageKeys.add(key);
            String name = snapshot.child("userName").getValue(String.class);
            String message = snapshot.child("message").getValue(String.class);
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
        DatabaseReference userReference = mDatabase.getReference("Users");
        userReference.child(userOneUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                     setUserInfo(true, dataSnapshot);
                     Log.e("firstuser name ", dataSnapshot.toString());
                     Log.e("userOneId", userOneUid);
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
            userTwoUid = otherUserUid;
        }
        userReference.child(userTwoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserInfo(false,dataSnapshot);
                Log.e("userOne", " "+ mUserFirstName);
                Log.e("userTwo", " "+ mUserTwoName);
                Log.e("userOneId", userTwoUid);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setUserInfo(boolean isUserOne, DataSnapshot snapshot){
        if(isUserOne){
            mUserFirstName = snapshot.child("firstname").getValue(String.class);
        }
        else{
            mUserTwoName = snapshot.child("firstname").getValue(String.class);
        }
        //TODO add profile image as well
    }

    /*
    //looks into the unique chat id and retrieves all the messages. This method calls addMessagesToArrayList to receive
    //the snapshot and add the data to the arraylist
    private void retrieveMessages(){
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(FIREBASE_MESSAGE_ROOT);
        mRef.child(mJointUserChat).addChildEventListener(mChildEventListener = new ChildEventListener() {
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
        if(didUserSendMessage){
            Message userSentMessage = new Message();
            userSentMessage.setUserFirstName(mUserFirstName);
            userSentMessage.setUserLastName(mUserLastName);
            userSentMessage.setProfilePictureUrl(mUserOneProfileUrl);
            userSentMessage.setMessage(message);
            messageArrayList.add(userSentMessage);
            mAdapter.notifyDataSetChanged();
            didUserSendMessage = false;
        }
        else {
            Message messageToSend = new Message();
            messageToSend.setMessage(message);
            String name = snapshot.child("firstname").getValue(String.class);
            messageToSend.setUserFirstName(name);
            String lastName = snapshot.child("lastname").getValue(String.class);
            messageToSend.setUserLastName(lastName);
            Log.e("firstname * last", name + lastName);
            messageArrayList.add(messageToSend);
            mAdapter.notifyDataSetChanged();
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
                retrieveMessages();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(userTwoUid != null) {
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

        if(mJointUserChat != null){
            retrieveMessages();
        }
        else {
            mJointUserChat = userOneUid + userTwoUid;
            boolean doesChatExist = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).getBoolean(DOES_CHAT_EXIST, false);
            Log.e("Does chat exist?", " " + doesChatExist);
            if (doesChatExist == false) {
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
    }
    private void sendMessage(){
        String messageToSend = mMessageEditText.getText().toString().trim();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_MESSAGE_ROOT);
        DatabaseReference pushReference = databaseReference.child(mJointUserChat).push();
        pushReference.child(FIREBASE_MESSAGE_CHILD).setValue(messageToSend);
        pushReference.child(FIREBASE_USERS_FIRST_NAME_CHILD).setValue(mUserFirstName);
        pushReference.child(FIREBASE_USERS_LAST_NAME_CHILD).setValue(mUserLastName);
        //pushReference.child(FIREBASE_USER_ID_CHILD).setValue(userOneUid);
        mMessageEditText.setText("");
        didUserSendMessage = true;
        //mRecyclerView.smoothScrollToPosition(messageArrayList.size()-1);

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
            mRef.addChildEventListener(mChildEventListener);
        }
    }
    //retrieves user information such as name user profile url etc. Passes the datasnapshot tosetUserData
    private void initializeUserData(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference(FIREBASE_USERS_ROOT);
        databaseReference.child(userOneUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setUserData(DataSnapshot snapshot){
        Log.e("snapshot"," "+ snapshot);
        mUserOneProfileUrl = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        mUserFirstName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
        mUserLastName = snapshot.child(FIREBASE_USERS_LAST_NAME_CHILD).getValue(String.class);
        Log.e("userData", "url"+ mUserOneProfileUrl+ "firstname"+ mUserFirstName+ " " +mUserLastName);
    }

    */
}
