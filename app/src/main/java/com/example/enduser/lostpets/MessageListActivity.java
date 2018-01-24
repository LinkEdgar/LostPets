package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageListActivity extends AppCompatActivity implements UserMessagesAdapter.OnItemClicked{
    //recyclerview variables
    private ArrayList<MessageList> mMessageArrayList;
    private RecyclerView mRecyclerView;
    private UserMessagesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManeger;
    //firebase
    private String FIREBASE_USERS_ROOT = "Users";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private String uID;
    //
    //chatid for the chat the user picked
    private String userPickedChat;
    //
    private TextView mNoMessagesTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.message_list_recyclerview);
        mMessageArrayList = new ArrayList<>();
        mLayoutManeger = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManeger);
        mAdapter = new UserMessagesAdapter(mMessageArrayList);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClick(this);
        mNoMessagesTextView = (TextView) findViewById(R.id.message_list_no_pet_textview);


        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(FIREBASE_USERS_ROOT);
        mRef.child(uID).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadCurrentMessages(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void loadCurrentMessages(DataSnapshot dataSnapshot){
        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
            String chatId = snapshot.getValue(String.class);
            getChats(chatId);
        }
    }
    private void getChats(final String chatId){
        StringBuilder builder = new StringBuilder();
        String otherUserUid;
        int chatIdSize = chatId.length();
        for(int x = 0; x < chatIdSize/2; x++){
            builder.append(chatId.charAt(x));
        }
        otherUserUid = builder.toString();
        if(uID.equals(otherUserUid)){
            otherUserUid = chatId.substring(chatIdSize/2,chatIdSize);
        }
        mRef.child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setUserInfo(dataSnapshot, chatId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setUserInfo(DataSnapshot snapshot, String chatId){
        String name = snapshot.child("name").getValue(String.class);
        String url = snapshot.child("profileurl").getValue(String.class);
        final MessageList messageList = new MessageList();
        messageList.setUserFirstName(name);
        messageList.setUserChatId(chatId);
        messageList.setUserProfileUrl(url);
        DatabaseReference messageRef = FirebaseDatabase.getInstance()
                .getReference("messages").child(messageList.getUserChatId());
        messageRef.limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getLastMessage(dataSnapshot, messageList);
                if(mMessageArrayList.size() <1 || mMessageArrayList.isEmpty()){
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
    private void getLastMessage(DataSnapshot snapshot, MessageList messageList){
       String lastMessage = null; // = snapshot.child("message").getValue(String.class);
       for(DataSnapshot snapshot1 : snapshot.getChildren()){
           lastMessage = snapshot1.child("message").getValue(String.class);
       }
        messageList.setLastMessage(lastMessage);
        mMessageArrayList.add(messageList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MessageListActivity.this, MessengerActivity.class);
        intent.putExtra("userOneId",uID);
        Log.e("userId", uID);
        userPickedChat = mMessageArrayList.get(position).getUserChatId();
        intent.putExtra("jointChatId",userPickedChat);
        startActivity(intent);
    }
}
