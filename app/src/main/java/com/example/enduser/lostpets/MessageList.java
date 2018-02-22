package com.example.enduser.lostpets;

/**
 * Created by EndUser on 1/16/2018.
 * This is a messageList which shows all the current messages that the user has and it populates the last message
 * sent so the user has a preview of what is being sent
 */

public class MessageList {
    private String lastMessage;
    private String userFirstName;
    private String userLastName;
    private String userChatId;
    private String userProfileUrl;
    public MessageList(){

    }
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
    public void setUserFirstName(String name){
        userFirstName = name;
    }
    public void setUserChatId(String chatId){
        userChatId = chatId;
    }
    public void setUserProfileUrl(String profileUrl){
        userProfileUrl = profileUrl;
    }
    public void setLastMessage(String lastMessage){
        this.lastMessage = lastMessage;
    }
}
