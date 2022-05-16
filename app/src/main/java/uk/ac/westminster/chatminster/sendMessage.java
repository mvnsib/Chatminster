package uk.ac.westminster.chatminster;

import java.util.Date;

public class sendMessage {
    private String messageText;
    private String messageUser;
    private long messageTime;

    public sendMessage(String messageText, String messageUser){
        this.messageText = messageText;
        this.messageUser = messageUser;
        messageTime = new Date().getTime();
    }
    public sendMessage(){
    }

    public void setMessageText(String messageText){
        this.messageText = messageText;
    }
    public String getMessageText(){
        return messageText;
    }

    public void setMessageUser(String messageUser){
        this.messageUser = messageUser;
    }
    public String getMessageUser(){
        return messageUser;
    }

    public void setMessageTime(long messageTime){
        this.messageTime = messageTime;
    }
    public long getMessageTime(){
        return messageTime;
    }

}
