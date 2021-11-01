package com.example.mobilderekuser.Model.EventBus;

public class LoadPemilikUsaha {
    private String idUsaha;

    public LoadPemilikUsaha(String idUsaha) {
        this.idUsaha = idUsaha;
    }

    public String getIdUsaha() {
        return idUsaha;
    }

    public void setIdUsaha(String idUsaha) {
        this.idUsaha = idUsaha;
    }
}
