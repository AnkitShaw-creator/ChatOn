package com.example.chaton;

public class Messages {

    String name;
    String message;
    String time;
    String type;

    public Messages() {
    }

    public Messages(String name, String message, String time, String type) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
