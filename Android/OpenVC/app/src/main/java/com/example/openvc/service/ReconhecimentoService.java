package com.example.openvc.service;

import com.example.openvc.model.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ReconhecimentoService {
    @GET("/")
    Call<Result> getTextoApi();

    @Multipart
    @POST("/file")
    Call<Result> setFile(@Part("photo\"; filename=\"frame.png\"") RequestBody photo);

    @Multipart
    @POST("/photo/{name}")
    //Call<Result> sendPhoto(@Path("name") String name, @Part("photo\"; filename=\"frame.jpg\"") RequestBody photo);
    Call<Result> sendPhoto(@Path("name") String name, @Part MultipartBody.Part photo);

    @Multipart
    @POST("/photo")
    Call<Result> getSimilar(@Part MultipartBody.Part photo);
}
