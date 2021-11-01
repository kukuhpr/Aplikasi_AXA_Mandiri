package com.example.mobilderekuser.Model.EventBus;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class SelectedPlaceEvent {
    private Point originPoint, destinationPoint;
    private String originAddress, destinationAddress;
    private String distanceValue, durationValue;
    private Double jarakValue;

    public SelectedPlaceEvent(Point originPoint, Point destinationPoint, String originAddress, String destinationAddress) {
        this.originPoint = originPoint;
        this.destinationPoint = destinationPoint;
        this.originAddress = originAddress;
        this.destinationAddress = destinationAddress;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

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

    public Point getOriginPoint() {
        return originPoint;
    }

    public void setOriginPoint(Point originPoint) {
        this.originPoint = originPoint;
    }

    public Point getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(Point destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Double getJarakValue() {
        return jarakValue;
    }

    public void setJarakValue(Double jarakValue) {
        this.jarakValue = jarakValue;
    }
}
