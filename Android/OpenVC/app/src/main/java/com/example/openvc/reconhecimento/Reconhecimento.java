package com.example.openvc.reconhecimento;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.FileUtils;
import android.widget.Toast;

import com.example.openvc.MainActivity;
import com.example.openvc.model.Result;
import com.example.openvc.open_cv.OpenCVCamera;
import com.example.openvc.service.ReconhecimentoService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Reconhecimento {
    private Retrofit retrofit;
    //private String retorno;

    /*public String getRetorno() {
        return retorno;
    }

    public void setRetorno(String retorno) {
        this.retorno = retorno;
    }*/

    public Reconhecimento(){
        retrofit = new Retrofit.Builder()
                //.baseUrl("http://192.168.68.112:5000")
                .baseUrl("https://fcde-2804-431-c7f2-88b1-5029-2e5d-23f0-99f9.ngrok.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void Teste(Context context){
        ReconhecimentoService reconhecimento = retrofit.create(ReconhecimentoService.class);
        Call<Result> call = reconhecimento.getTextoApi();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    Toast toast = Toast.makeText(context, result.getResult(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast toast = Toast.makeText(context, "falha", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //return getRetorno();
    }

    public void setFile(Context context, byte[] b){

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/png"),
                        b
                );
        //MultipartBody.Part file = MultipartBody.Part.createFormData("photo", "frame.png", requestFile);

        ReconhecimentoService reconhecimento = retrofit.create(ReconhecimentoService.class);
        //Call<Result> call = reconhecimento.setFile(file);
        Call<Result> call = reconhecimento.setFile(requestFile);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    Toast toast = Toast.makeText(context, result.getResult(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast toast = Toast.makeText(context, "falha", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void setPhoto(Context context, byte[] b, String nome){

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/png"),
                        b
                );

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", "frame.png", requestFile);

        ReconhecimentoService reconhecimento = retrofit.create(ReconhecimentoService.class);
        Call<Result> call = reconhecimento.sendPhoto(nome, filePart);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    Result result = response.body();
                    Toast toast = Toast.makeText(context, result.getResult(), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast toast = Toast.makeText(context, "falha", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void getSimilar(Context context, byte[] b){

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/png"),
                        b
                );

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("photo", "frame.png", requestFile);

        ReconhecimentoService reconhecimento = retrofit.create(ReconhecimentoService.class);
        Call<Result> call = reconhecimento.getSimilar(filePart);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                String ret = "";
                if (response.isSuccessful()) {
                    Result result = response.body();
                    ret = result.getResult();
                }else{
                    ret = "Rosto não identificado!";
                }
                Toast toast = Toast.makeText(context, ret, Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast toast = Toast.makeText(context, "falha", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
