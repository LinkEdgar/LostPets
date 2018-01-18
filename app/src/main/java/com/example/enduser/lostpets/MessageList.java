package com.example.enduser.lostpets;

/**
 * Created by EndUser on 1/16/2018.
 */

public class MessageList {
    private String lastMessage;
    private String userFirstName;
    private String userLastName;
    private String userChatId;
    private String userProfileUrl;
    public MessageList(String lastMessage, String userFirstName, String userLastName, String userChatId, String userProfileUrl){
        this.lastMessage = lastMessage;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userChatId = userChatId;
        this.userProfileUrl = userProfileUrl;
    }
    public String getLastMessage(){return lastMessage;}
    public String getUserFirstName(){return userFirstName;}
    public String getUserLastName(){return userLastName;}
    public String getUserChatId(){return userChatId;}
    public String getUserProfileUrl(){return userProfileUrl;}
}
