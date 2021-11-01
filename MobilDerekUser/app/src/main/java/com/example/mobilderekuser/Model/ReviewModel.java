package com.example.mobilderekuser.Model;

public class ReviewModel {


    private String idRider, ratings, review, timeStamp, idPemilik, idSupir, idTrip;

    public ReviewModel() {
    }

    public ReviewModel(String idRider, String ratings, String review, String timeStamp, String idPemilik, String idSupir, String idTrip) {
        this.idRider = idRider;
        this.ratings = ratings;
        this.review = review;
        this.timeStamp = timeStamp;
        this.idPemilik = idPemilik;
        this.idSupir = idSupir;
        this.idTrip = idTrip;
    }

    public String getIdRider() {
        return idRider;
    }

    public void setIdRider(String idRider) {
        this.idRider = idRider;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }

    public String getIdSupir() {
        return idSupir;
    }

    public void setIdSupir(String idSupir) {
        this.idSupir = idSupir;
    }

    public String getIdTrip() {
        return idTrip;
    }

    public void setIdTrip(String idTrip) {
        this.idTrip = idTrip;
    }
}
