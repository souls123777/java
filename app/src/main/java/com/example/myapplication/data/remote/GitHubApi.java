package com.example.myapplication.data.remote;

import com.example.myapplication.data.model.GitHubUser;
import com.example.myapplication.data.model.GitHubCommit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GitHubApi {
    @GET("users/{user}")
    Call<GitHubUser> getUser(@Path("user") String username);

    @GET("users/{user}/events")
    Call<GitHubCommit[]> getEvents(@Path("user") String username);
}