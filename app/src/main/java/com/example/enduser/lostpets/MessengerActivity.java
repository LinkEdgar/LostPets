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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;

public class MessengerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ArrayList<Message> messageArrayList;
    private RecyclerView.LayoutManager mLayoutManager;
    private MessageAdapter mAdapter;
    //Firebase variables
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
    private String FIREBASE_USERS_FIRST_NAME_CHILD = "name";
    //TODO add image selection

    private String FIREBASE_USER_CHATS_CHILD = "chats";
    //this variable stores the chat number that corresponds to the two users conversations
    private String mJointUserChat = "jointUserChat";
    //user-one information
    private String mUserOneProfileUrl;
    private String mUserFirstName;
    //user two information
    private String mUserTwoProfileUrl;
    private String mUserTwoName;

    //user one is the current phone user
    private String userOneUid;
    private String userTwoUid;
    //layout button
    private ImageButton mSendMessegeButton;
    private EditText mMessageEditText;

    private boolean doesChatOneExist = false;
    //no message textview
    private TextView mNoMessagesTextView;


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
        //gets information passed from previous activity
        Intent intent = getIntent();
        userOneUid = intent.getStringExtra("userOneId");
        Log.e("userOneuid from intent", " "+ userOneUid);
        mJointUserChat = intent.getStringExtra("jointChatId");
        userTwoUid = intent.getStringExtra("userTwoId");

        mNoMessagesTextView = (TextView) findViewById(R.id.messenger_no_message_textview);

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
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(messageKeys.isEmpty()){
                    mNoMessagesTextView.setVisibility(View.VISIBLE);
                }
                else{
                    mNoMessagesTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*
    This method sends a messages by removing the childevent listener until the data is synchronized.
    It gets the message from 'mMessageEditText' and clears the text field by setting it to ''
     */
    private void sendMessage(){
        String messageToSend = mMessageEditText.getText().toString().trim();
        if(messageToSend != null && messageToSend.isEmpty() ==false) {
            if (mChildEventListener != null) {
                mRef.removeEventListener(mChildEventListener);
            }
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
    /*
    Called from the message listener. This method uses a hashset to ensure we do not get repetitive data.
    Since some of the listeners run asynchronously we must set the value of the messages manually so that the user gets
    a proper instant messaging experience.
    This method creates a Message object and builds on it via its setter methods.
    We also implement a smooth scroll to new messages added
     */
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
    /*
    we are guaranteed a 'userOneUid' by the intent that started this activity, however; that is not the case for
    'userTwoUid' and so in the case that the value is null we have extra work. That work being, splitting the joint chat and
    determining which userTwo's id. After finding it we can get its basic info as we did for the current user
     */
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
        if(doesChatOneExist == false) {
            addChatToUserData();
        }
    }
    /*
    Because of the asynchronous nature of firebase the basic information of each user must be obtained. Otherwise when a new
    message is sent it will have null values. The calling method must specify the user via a boolean value
     */
    private void setUserInfo(boolean isUserOne, DataSnapshot snapshot){
        if(isUserOne){
            mUserFirstName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
            Log.e("UserOnenamesetUserInfo", " "+ mUserFirstName);
            mUserOneProfileUrl = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        }
        else{
            mUserTwoName = snapshot.child(FIREBASE_USERS_FIRST_NAME_CHILD).getValue(String.class);
            mUserTwoProfileUrl = snapshot.child(FIREBASE_USERS_PROFILE_CHILD).getValue(String.class);
        }
    }
    /*
    if the users do not have the joint chat id in their chat then it is added
    by calling 'verifyChatExistence' which accepts a datasnapshot and a boolean stating if the user is the current user
     */
    private void addChatToUserData(){
        mUserRef.child(userOneUid).child(FIREBASE_USER_CHATS_CHILD)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        verifyChatExistence(dataSnapshot,true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mUserRef.child(userTwoUid).child(FIREBASE_USER_CHATS_CHILD)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        verifyChatExistence(dataSnapshot,false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
    /*
    this method checks the user's profile to check if the joint chat id is present. It iterates through the 'chats' root
    and checks if any of them are equal to 'mJointUserChat' variable and then check the counter to see if adding the chat is
    necessary or not.
    This method also checks if the calling method is verifying the current user or the other user
     */
    private void verifyChatExistence(DataSnapshot snapshot, boolean isChatOne){
        if(isChatOne){
            int chatCounter = (int) snapshot.getChildrenCount();
            int counter = 0;
            for(DataSnapshot snap: snapshot.getChildren()){
                String value = snap.getValue(String.class);
                if(!value.equals(mJointUserChat)){
                    counter++;
                }
                else{
                    doesChatOneExist =true;
                    break;
                }
            }
            if(doesChatOneExist == false) {
                if (chatCounter == counter) {
                    setChatToUserDb(true);
                }
            }
        }
        else{
            int chatCounter = (int) snapshot.getChildrenCount();
            int counter = 0;
            for(DataSnapshot snap: snapshot.getChildren()){
                String value = snap.getValue(String.class);
                if(!value.equals(mJointUserChat)){
                    counter++;
                }
                else{
                    doesChatOneExist =true;
                    break;
                }
            }
            if(doesChatOneExist == false) {
                if (chatCounter == counter) {
                    setChatToUserDb(false);
                }
            }
        }
    }
    /*
    checks whether or not the datasnapshot is from the current user's chat or the other user
    base on that information it will add the chat into the user' profile
     */
    private void setChatToUserDb(boolean isChatOne){
        Log.e("Yeet squad", "setting new chat");
        if(isChatOne){
            DatabaseReference specificRef =mUserRef.child(userOneUid)
                    .child(FIREBASE_USER_CHATS_CHILD).push();
            specificRef.setValue(mJointUserChat);
        }
        else{
            DatabaseReference specificRef = mUserRef.child(userTwoUid)
                    .child(FIREBASE_USER_CHATS_CHILD).push();
            specificRef.setValue(mJointUserChat);
        }
    }
    //remove the child listener as to align with firebase best practices
    @Override
    protected void onPause() {
        if(mChildEventListener != null) {
            mRef.removeEventListener(mChildEventListener);
        }
        super.onPause();
    }
    //must add listener once the app restarts and have a null check for the first time instance of the class
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(mChildEventListener != null) {
            mRef.limitToLast(10).addChildEventListener(mChildEventListener);
        }
    }

    private void setmRecyclerViewScrollListener(){
        //TODO implement a way to detect the top of the recyclerview has been reached to check over old messages
    }
}
