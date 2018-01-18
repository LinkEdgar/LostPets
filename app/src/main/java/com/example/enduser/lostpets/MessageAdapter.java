package com.example.enduser.lostpets;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by EndUser on 1/12/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private ArrayList<Message> messageArrayList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mMessageTextView;
        private ImageView mProfilePicture;
        private TextView mUserName;
        private View layout;
        public ViewHolder(View v){
        super(v);
        layout = v;
        mMessageTextView = (TextView) layout.findViewById(R.id.message_item_user_message);
        mProfilePicture = (ImageView) layout.findViewById(R.id.message_item_profile_picture);
        mUserName = (TextView) layout.findViewById(R.id.message_item_user_name);
        }
    }
    public MessageAdapter(ArrayList<Message> array){messageArrayList = array;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String firstName = messageArrayList.get(position).getName();
        //String lastName = messageArrayList.get(position).getUserLastName();
        holder.mUserName.setText(firstName);
        Context context = holder.mProfilePicture.getContext();
        //Glide.with(context).load(messageArrayList.get(position).getProfilePictureUrl()).error(R.drawable.no_image).override(75,75)
                //.into(holder.mProfilePicture);
        holder.mMessageTextView.setText(messageArrayList.get(position).getMessage());

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


}
