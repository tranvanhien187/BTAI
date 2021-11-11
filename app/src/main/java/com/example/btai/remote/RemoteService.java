package com.example.btai.remote;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RemoteService {
    @Multipart
    @GET("")
    Call<String> getArtImage(@Part MultipartBody.Part part,
                             @Query("api_key") String key);
}
