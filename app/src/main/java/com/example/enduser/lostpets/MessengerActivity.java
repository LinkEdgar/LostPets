package com.example.enduser.lostpets;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private ImageButton mGalaerySelectButton;
    private EditText mMessageEditText;

    private boolean doesChatOneExist = false;
    private boolean wasMessageJustSent = false;
    //no message textview
    private TextView mNoMessagesTextView;
    //picture message related variables
    private final int RC_SELECT_IMAGE = 1007;
    private ImageView mSelectedImage;
    private ProgressBar mProgressbar;
    private ImageButton mCancelSelectedImage;
    private boolean isPictureMessage = false;
    private CardView mCardView;
    //the key will be three spaces plus the first four digits of pie
    // since the messages are .trimmed it will be unlikely that the user will send this message
    private final String PICTURE_MESSAGE_KEY = "   3141";
    private final int PICTURE_MESSAGE_KEY_COUNTER = 7;
    private Uri pictureUri;
    private StorageReference mStorageRef;

    int counter = 0;


    private HashSet<String> messageKeys;

    //TODO fix scrolling
    //TODO add fullscreen on click
    //TODO add elevation to this to keyboard area

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
        mProgressbar = (ProgressBar) findViewById(R.id.messenger_activity_progress_bar);
        mSelectedImage = (ImageView) findViewById(R.id.messenger_activity_selected_image);
        mCancelSelectedImage = (ImageButton) findViewById(R.id.messenger_cancel_image_button);
        mCancelSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelSelectedImage();
            }
        });
        mCardView = (CardView) findViewById(R.id.messenger_cardview);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mSendMessegeButton = (ImageButton) findViewById(R.id.messenger_send_button);
        mGalaerySelectButton = (ImageButton) findViewById(R.id.messenger_gallery_select_);
        mGalaerySelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
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
    if the message is a picture message then the message will be set with a key appended to
    the beginning. This key is not likely to be shown otherwise so the message adapter will detect
    that the message is a picture and will load it accordingly
     */
    private void sendMessage(){
        if(isPictureMessage == false) {
            String messageToSend = mMessageEditText.getText().toString().trim();
            if (messageToSend != null && messageToSend.isEmpty() == false) {
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
        else{
            //we have to add a means to display the picture message via our message adapter
            //have to edit the method 'addMessagesToArraylist' so that it can detect picture messages
            //sync data
            /*
            strat --> set a special character at the beginning if we get that character then we read the rest of the string
            the rest being the url and then we load it
             */
            mProgressbar.setVisibility(View.VISIBLE);
            mCancelSelectedImage.setVisibility(View.GONE);
            mSelectedImage.setVisibility(View.GONE);
            if(pictureUri != null){
                StorageReference ref = mStorageRef.child(pictureUri.getLastPathSegment());
                ref.putFile(pictureUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String downloadUrl = PICTURE_MESSAGE_KEY+taskSnapshot.getDownloadUrl().toString();
                        if (mChildEventListener != null) {
                            mRef.removeEventListener(mChildEventListener);
                        }
                        DatabaseReference specificReference = mRef.push();
                        specificReference.child(FIREBASE_MESSAGE_CHILD).setValue(downloadUrl);
                        specificReference.child(MESSAGE_USERNAME).setValue(mUserFirstName);
                        isPictureMessage = false;
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
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        pictureUri = null;
                        mCardView.setVisibility(View.GONE);
                        mMessageEditText.setVisibility(View.VISIBLE);
                        mProgressbar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pictureUri = null;
                        mProgressbar.setVisibility(View.GONE);
                        mCardView.setVisibility(View.GONE);
                        mMessageEditText.setVisibility(View.VISIBLE);
                        Toast.makeText(MessengerActivity.this, "Failed to upload and send image", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            }
        wasMessageJustSent = true;
    }

    /*
    Called from the message listener. This method uses a hashset to ensure we do not get repetitive data.
    Since some of the listeners run asynchronously we must set the value of the messages manually so that the user gets
    a proper instant messaging experience.
    This method creates a Message object and builds on it via its setter methods.
    We also implement a smooth scroll to new messages added
     */
    private void addMessageToArrayList(DataSnapshot snapshot){

        counter++;
        Toast.makeText(MessengerActivity.this, ""+ counter, Toast.LENGTH_SHORT).show();

        String key = snapshot.getKey();
        if(!messageKeys.contains(key)) {
            messageKeys.add(key);
            String name = snapshot.child(MESSAGE_USERNAME).getValue(String.class);
            String profileUrl = null;
            String message = snapshot.child(FIREBASE_MESSAGE_CHILD).getValue(String.class);
            Message messageToAdd = new Message();
            //since this name will only be null when the message was just sent and the code run asynchronously
            //we must add the value of user one which will be the current sender
            if(name == null){
                if(wasMessageJustSent) {
                    name = mUserFirstName;
                    profileUrl = mUserOneProfileUrl;
                    wasMessageJustSent = false;
                }
                else{
                    name = mUserTwoName;
                    profileUrl = mUserTwoProfileUrl;
                }
            }
            else{
                if(name.equals(mUserTwoName)){
                    profileUrl = mUserTwoProfileUrl;
                }
                else{
                    profileUrl = mUserOneProfileUrl;
                }
            }
            if(message != null) {
                if (message.length() >= PICTURE_MESSAGE_KEY_COUNTER) {
                    String messageSubString = message.substring(0, 7);
                    if (messageSubString.equals(PICTURE_MESSAGE_KEY)) {
                        messageToAdd.isPictureMessage(true);
                    }
                }
            }
            messageToAdd.setmUserProfileUrl(profileUrl);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SELECT_IMAGE && resultCode == RESULT_OK){
            isPictureMessage = true;
            mCardView.setVisibility(View.VISIBLE);
            mMessageEditText.setVisibility(View.GONE);
            pictureUri = data.getData();
            Glide.with(this).load(pictureUri).into(mSelectedImage);
            mSelectedImage.setVisibility(View.VISIBLE);
            mCancelSelectedImage.setVisibility(View.VISIBLE);

        }
    }

    private void selectImageFromGallery(){
        Intent imageSelectIntent = new Intent();
        imageSelectIntent.setType("image/*");
        imageSelectIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageSelectIntent,RC_SELECT_IMAGE);
    }
    private void cancelSelectedImage(){
        mCardView.setVisibility(View.GONE);
        mMessageEditText.setVisibility(View.VISIBLE);
    }
}
