package com.example.btai.remote;


import com.example.btai.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RemoteService {
    @Multipart
    @POST("/gen-image")
    Call<Result> getArtImage(@Part MultipartBody.Part file,
                             @Part("style") RequestBody style);
}
