package com.example.enduser.lostpets;

import android.content.Context;
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
 * Created by EndUser on 1/16/2018.
 */

public class UserMessagesAdapter extends RecyclerView.Adapter<UserMessagesAdapter.ViewHolder>{

    private ArrayList<MessageList> messageArrayList;
    //onClick
    private OnItemClicked onClick;
    public interface OnItemClicked{
        void onItemClick(int position);
    }

    public void setOnClick(OnItemClicked onClick){
        this.onClick = onClick;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView mImageView;
        private TextView mNameTextView;
        private TextView mLastMessage;
        private View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            mImageView = (CircleImageView) v.findViewById(R.id.activity_message_item_user_profile_picture);
            mNameTextView = (TextView) v.findViewById(R.id.activity_message_item_name);
            mLastMessage = (TextView) v.findViewById(R.id.activity_message_item_last_message);
        }
    }
    public UserMessagesAdapter(ArrayList<MessageList> messageArrayList){
        this.messageArrayList = messageArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_message_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String firstName = messageArrayList.get(position).getUserFirstName();
        String lastName = messageArrayList.get(position).getUserLastName();
        if(lastName != null){
            holder.mNameTextView.setText(firstName +" " + lastName);

        }
        else{
            holder.mNameTextView.setText(firstName);
        }
        Context context = holder.mImageView.getContext();
        Glide.with(context).load(messageArrayList.get(position).getUserProfileUrl()).error(R.drawable.no_image).override(75,75)
                .into(holder.mImageView);
        holder.mLastMessage.setText(messageArrayList.get(position).getLastMessage());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
