package com.example.enduser.lostpets;

/**
 * Created by EndUser on 1/12/2018.
 */

public class Message {
    private String mUserProfileUrl;
    private String mMessage;
    private String mName;
    private String mUid;

    public Message(){

    }
    public Message(String mMessage, String mName, String mUid, String mUserProfileUrl){
        this.mMessage = mMessage;
        this.mName = mName;
        this.mUid = mUid;
        this.mUserProfileUrl = mUserProfileUrl;
    }

    public void setMessage(String mMessage){
        this.mMessage = mMessage;
    }
    public void setName(String mName){
        this.mName = mName;
    }
    public void setUid(String mUid){
        this.mUid = mUid;
    }
    public void setmUserProfileUrl(String mUserProfileUrl){
        this.mUserProfileUrl =mUserProfileUrl;
    }
    public String getMessage(){
        return mMessage;
    }
    public String getName(){
        return mName;
    }
    public String getUid(){
        return mUid;
    }
    public String getmUserProfileUrl(){
        return mUserProfileUrl;
    }
}
