package com.example.chaton;

public class Friends {

    boolean online;
    String UID;
    String timeStamp;

    public Friends(){}

    public Friends(boolean online,String UID, String timeStamp) {

        this.online = online;
        this.UID = UID;
        this.timeStamp = timeStamp;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
