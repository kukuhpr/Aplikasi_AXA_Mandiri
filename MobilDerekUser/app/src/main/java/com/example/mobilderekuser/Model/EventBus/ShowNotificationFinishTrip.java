package com.example.mobilderekuser.Model.EventBus;

public class ShowNotificationFinishTrip {
    private String tripKey;

    public ShowNotificationFinishTrip(String tripKey) {
        this.tripKey = tripKey;
    }

    public String getTripKey() {
        return tripKey;
    }

    public void setTripKey(String tripKey) {
        this.tripKey = tripKey;
    }
}
