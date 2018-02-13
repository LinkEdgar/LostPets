package com.example.enduser.lostpets;

import android.content.Context;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by EndUser on 1/12/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private ArrayList<Message> messageArrayList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mMessageTextView;
        private CircleImageView mProfilePicture;
        private TextView mUserName;
        private ImageView mPictureMessage;
        private View layout;
        public ViewHolder(View v){
        super(v);
        layout = v;
        mPictureMessage = (ImageView) layout.findViewById(R.id.message_item_picture_message);
        mMessageTextView = (TextView) layout.findViewById(R.id.message_item_user_message);
        mProfilePicture = (CircleImageView) layout.findViewById(R.id.message_item_profile_picture);
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
        Glide.with(context).load(messageArrayList.get(position).getmUserProfileUrl()).dontAnimate().error(R.drawable.no_image).override(75,75).into(holder.mProfilePicture);
        if(messageArrayList.get(position).getPictureType()){
            holder.mMessageTextView.setVisibility(View.GONE);
            holder.mPictureMessage.setVisibility(View.VISIBLE);
            String message = messageArrayList.get(position).getMessage();
            String pictureUrl = message.substring(7, message.length());
            Glide.with(context).load(pictureUrl).error(R.drawable.no_image).into(holder.mPictureMessage);
        }
        else {
            holder.mMessageTextView.setText(messageArrayList.get(position).getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


}
