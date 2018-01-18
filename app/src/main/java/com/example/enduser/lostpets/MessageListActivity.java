package com.example.enduser.lostpets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private String uID;
    //
    //chatid for the chat the user picked
    private String userPickedChat;


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


        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference("Users");
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
            mMessageArrayList.add(new MessageList("How was the game?","Edgar", "Reyes",chatId, "invalid"));
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.e("onClick", "What's going on here");
        Intent intent = new Intent(MessageListActivity.this, MessengerActivity.class);
        intent.putExtra("userOneId",uID);
        Log.e("userId", uID);
        userPickedChat = mMessageArrayList.get(position).getUserChatId();
        intent.putExtra("jointChatId",userPickedChat);
        startActivity(intent);
    }
}
