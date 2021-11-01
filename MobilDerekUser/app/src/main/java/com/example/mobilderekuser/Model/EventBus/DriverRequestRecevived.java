package com.example.mobilderekuser.Model.EventBus;

public class DriverRequestRecevived {
    private String key;
    private String pickupLocation,pickupLocationString;
    private String destinationLocation,destinationLocationString;
    private String distanceValue, durationValue;
    private Double jarakValue;

    public String getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(String distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(String durationValue) {
        this.durationValue = durationValue;
    }



    public DriverRequestRecevived() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getPickupLocationString() {
        return pickupLocationString;
    }

    public void setPickupLocationString(String pickupLocationString) {
        this.pickupLocationString = pickupLocationString;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDestinationLocationString() {
        return destinationLocationString;
    }

    public void setDestinationLocationString(String destinationLocationString) {
        this.destinationLocationString = destinationLocationString;
    }

    public Double getJarakValue() {
        return jarakValue;
    }

    public void setJarakValue(Double jarakValue) {
        this.jarakValue = jarakValue;
    }
}
