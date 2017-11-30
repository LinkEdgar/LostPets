package com.example.enduser.lostpets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ZenithPC on 11/12/2017.
 */

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {
    private ArrayList<Pet> myArray;
    private final String DEFAULT_PICTURE_URL = "https://firebasestorage.googleapis.com/v0/b/lostpets-60064.appspot.com/o/Image_Not_Available.jpg?alt=media&token=22d201f6-9a01-4fdc-b55b-2fe228a6726e";
    //onclick variables
    private OnItemClicked onClick;

    public interface OnItemClicked{
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CardView mCardView;
        private TextView mPetNameTV;
        private TextView mPetBreedTV;
        private TextView mPetWeightTV;
        private TextView mPetGenderTV;
        private ImageView mPetImageIV;
        private View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            mCardView = (CardView) v.findViewById(R.id.pet_card_view);
            mPetImageIV = (ImageView) v.findViewById(R.id.pet_query_display_image);
            mPetNameTV = (TextView) v.findViewById(R.id.pet_query_display_name);
            mPetBreedTV = (TextView) v.findViewById(R.id.pet_query_display_breed);
            mPetWeightTV = (TextView) v.findViewById(R.id.pet_query_display_weight);
            mPetGenderTV = (TextView) v.findViewById(R.id.pet_query_display_gender);

        }
    }
    public PetAdapter(ArrayList<Pet> array){myArray = array;}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.pet_layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mPetNameTV.setText(myArray.get(position).getName());
        holder.mPetGenderTV.setText(myArray.get(position).getGender());
        holder.mPetWeightTV.setText(myArray.get(position).getWeight());
        holder.mPetBreedTV.setText(myArray.get(position).getBreed());
        Context context = holder.mPetImageIV.getContext();
        String imageUrl = myArray.get(position).getUrlOne();
        //TODO figure out what to do about entrances without pictures
        if(!imageUrl.equals("invalid")){
            Picasso.with(context).load(imageUrl).into(holder.mPetImageIV);
        }
        else{
            /*
            Drawable imageNotAvailable = context.getResources().getDrawable(R.drawable.no_image);
            holder.mPetImageIV.setImageDrawable(imageNotAvailable);
            Log.e("Settign image", "DOES this fire?");
            */
            Picasso.with(context).load(DEFAULT_PICTURE_URL).into(holder.mPetImageIV);

        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return myArray.size();
    }

    public Pet getPet(int position){

        return myArray.get(position);
    }


    public void setOnClick(OnItemClicked onClick){
        this.onClick = onClick;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
