package com.example.mobilderekuser.Model.EventBus;

public class LoadTripDetailEvent {
    private String tripKey;

    public LoadTripDetailEvent(String tripKey) {
        this.tripKey = tripKey;
    }

    public String getTripKey() {
        return tripKey;
    }

    public void setTripKey(String tripKey) {
        this.tripKey = tripKey;
    }
}
