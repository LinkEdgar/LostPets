package com.example.enduser.lostpets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by ZenithPC on 11/12/2017.
 */

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.ViewHolder> {
    private ArrayList<Pet> myArray;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mPetNameTV;
        public TextView mPetBreedTV;
        public TextView mPetWeightTV;
        public TextView mPetGenderTV;
        public ImageView mPetImageIV;
        public View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            mPetImageIV = (ImageView) v.findViewById(R.id.pet_query_display_image);
            mPetNameTV = (TextView) v.findViewById(R.id.pet_query_display_name);
            mPetBreedTV = (TextView) v.findViewById(R.id.pet_query_display_breed);
            mPetWeightTV = (TextView) v.findViewById(R.id.pet_query_display_weight);
            mPetGenderTV = (TextView) v.findViewById(R.id.pet_query_display_gender);

        }
    }
    public PetAdapter(ArrayList<Pet> array){
        myArray = array;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.pet_layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mPetNameTV.setText(myArray.get(position).getName());
        holder.mPetGenderTV.setText(myArray.get(position).getGender());
        holder.mPetWeightTV.setText(myArray.get(position).getWeight());
        holder.mPetBreedTV.setText(myArray.get(position).getBreed());
        Context context = holder.mPetImageIV.getContext();
        String imageUrl = myArray.get(position).getUrlOne();
        Picasso.with(context).load(imageUrl).into(holder.mPetImageIV);

    }

    @Override
    public int getItemCount() {
        return myArray.size();
    }




}
