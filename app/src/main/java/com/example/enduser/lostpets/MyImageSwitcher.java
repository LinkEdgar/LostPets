package com.example.enduser.lostpets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

/**
 * Created by EndUser on 12/17/2017.
 */

public class MyImageSwitcher extends ViewSwitcher {
    public MyImageSwitcher(Context context) {
        super(context);
    }

    public MyImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImageUrl(String url){
        ImageView imageView = (ImageView) this.getNextView();
        if(!url.equals("invalid")) {
            Picasso.with(getContext()).load(url).into(imageView);
        }
        else{
            imageView.setImageResource(R.drawable.no_image);
        }
        showNext();
    }

}