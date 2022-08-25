package com.kukuhpr.aplikasiaxamandiri.Interface;

import com.kukuhpr.aplikasiaxamandiri.Model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JSONPlaceHolder {

    @GET("posts")
    Call<List<Post>> getPost();

}
