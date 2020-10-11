package com.example.chaton;

public class Requests {
    String UID;
    String request_status;

    public Requests() {
    }
    public Requests(String UID, String requests_status) {
        this.UID = UID;
        this.request_status = requests_status;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequests_status(String requests_status) {
        this.request_status = requests_status;
    }


}
