package com.techroof.pkpropertyzone.Chat;

/**
 * Created by SAAD on 12/24/2017.
 */
public class Messages {

    public String message,messageId, type;
    private long  time;
    private boolean seen;


    public String from;

    public Messages() {
            }

    public Messages(String message, String messageId, String type, long time, boolean seen, String from) {
        this.message = message;
        this.messageId = messageId;
        this.type = type;
        this.time = time;
        this.seen = seen;
        this.from = from;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

}
