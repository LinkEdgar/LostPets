package com.example.enduser.lostpets;

/**
 * Created by EndUser on 1/12/2018.
 */

public class Message {
    //type determines whether the message was received or sent
    private String userFirstName;
    private String userLastName;
    private String profilePictureUrl;
    private String message;
    private String senderUserId;
    //determines who the sender or receiver is. UID will be used to determine this
    private String chattingWith;
    public Message(String senderUserId, String message, String chattingWith, String userFirstName, String userLastName, String profilePictureUrl){
        this.chattingWith = chattingWith;
        this.message = message;
        this.senderUserId = senderUserId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.profilePictureUrl = profilePictureUrl;
    }
    public void setUserFirstName(String userFirstName){this.userFirstName = userFirstName;}
    public void setUserLastName(String userLastName){this.userLastName = userLastName;}
    public void setProfilePictureUrl(String profilePictureUrl){this.profilePictureUrl = profilePictureUrl;}
    public void setMessage(String message){this.message = message;}
    public void setChattingWith(String chattingWith){this.chattingWith = chattingWith;}
    public String getMessage(){return message;}
    public String getChattingWith(){return chattingWith;}
    public String getUserFirstName(){return userFirstName;}
    public  String getUserLastName(){return userLastName;}
    public String getProfilePictureUrl(){return profilePictureUrl;}
}
