package com.example.openvc.open_cv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.openvc.R;
import com.example.openvc.model.Result;
import com.example.openvc.reconhecimento.Reconhecimento;
import com.example.openvc.service.ReconhecimentoService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class Img{
    Mat img;
    boolean temface;
}

public class OpenCVCamera extends CameraActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase nCameraBridgeViewBase;
    //##########NOVA##########
    File cascFile;
    CascadeClassifier faceDetector;
    Mat mRGBA, mRGBAT;
    //##########FIM###########

    FloatingActionButton btnFlip;
    FloatingActionButton btnCaptura;
    int flipCameraState = CameraBridgeViewBase.CAMERA_ID_FRONT;

    Img img = new Img();
    Reconhecimento reconhecimento;
    String nome;

    private CountDownTimer countDownTimer;
    private long timeLeftInMilliseconds = 20000;
    private boolean timerRunning;

    public void startStop(){
        if(timerRunning){
            stopTime();
        }else {
            startTime();
        }
    }

    private void startTime(){
        countDownTimer = new CountDownTimer(timeLeftInMilliseconds, 3500) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(img.temface) {
                    Bitmap bmp = Bitmap.createBitmap(img.img.cols(), img.img.rows(), Bitmap.Config.ARGB_8888);

                    Utils.matToBitmap(img.img, bmp);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 0, baos);
                    byte[] b = baos.toByteArray();
                    //reconhecimento.Teste(getApplicationContext());
                    //reconhecimento.setFile(getApplicationContext(), b);
                    reconhecimento.getSimilar(getApplicationContext(), b);
                }
            }

            @Override
            public void onFinish() {
                start();//quando acado o tempo inicia novamente, para ser loop infinito
            }
        }.start();
        timerRunning = true;
    }
    public void stopTime(){
        countDownTimer.cancel();
        timerRunning = false;
    }




    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) /*Nova parte*/throws IOException {
            //super.onManagerConnected(status);
            switch (status){
                case SUCCESS:
                    //##########NOVA##########
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                    File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                    cascFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");
                    FileOutputStream fos = new FileOutputStream(cascFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1){
                        fos.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    fos.close();
                    faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());
                    if (faceDetector.empty()){
                        faceDetector = null;
                    }else{
                        cascadeDir.delete();
                    }
                    //##########FIM###########
                    nCameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

        @Override
        public void onPackageInstall(int operation, InstallCallbackInterface callback) {
            super.onPackageInstall(operation, callback);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);

        reconhecimento = new Reconhecimento();

        btnFlip = findViewById(R.id.btnFlipCamera);
        btnFlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapCamera();
            }
        });

        btnCaptura = findViewById(R.id.btnCaptura);
        btnCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast toast = Toast.makeText(getApplicationContext(), "Capturar",Toast.LENGTH_SHORT);
                //toast.show();

                if(img.temface) {
                    Bitmap bmp = Bitmap.createBitmap(img.img.cols(), img.img.rows(), Bitmap.Config.ARGB_8888);

                    Utils.matToBitmap(img.img, bmp);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 0, baos);
                    byte[] b = baos.toByteArray();
                    reconhecimento.setPhoto(getApplicationContext(), b, nome);
                    finish();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Nenhum rosto na imagem",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        nCameraBridgeViewBase = findViewById(R.id.simple_Open_cv_camera);
        nCameraBridgeViewBase.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        nCameraBridgeViewBase.setCvCameraViewListener(this);

        Intent intent = getIntent();
        String acao = intent.getStringExtra("acao");
        //Toast toast = Toast.makeText(this, acao,Toast.LENGTH_SHORT);
        //toast.show();


        if(acao.equals("reconhecer")){
            startStop();
            btnCaptura.setVisibility(View.INVISIBLE);
        }else{
            nome = intent.getStringExtra("nome");
        }
    }

    private void swapCamera() {
        startStop();
        flipCameraState = flipCameraState^CameraBridgeViewBase.CAMERA_ID_FRONT;
        nCameraBridgeViewBase.disableView();
        nCameraBridgeViewBase.setCameraIndex(flipCameraState);
        nCameraBridgeViewBase.enableView();
        startStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(nCameraBridgeViewBase != null){
            nCameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(nCameraBridgeViewBase);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,  baseLoaderCallback);
        }else{
            //##########NOVA##########
            try {
                baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //##########FIM###########
            //baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(nCameraBridgeViewBase != null){
            nCameraBridgeViewBase.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        //##########NOVA##########
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
        //##########FIM###########
    }

    @Override
    public void onCameraViewStopped() {
        //##########NOVA##########
        mRGBA.release();
        //##########FIM###########
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //return inputFrame.rgba();

        //##########NOVA##########
        mRGBA = inputFrame.rgba();
        mRGBAT = mRGBA.t();
        if(flipCameraState == CameraBridgeViewBase.CAMERA_ID_FRONT){
            Core.flip(mRGBA, mRGBAT, 1);
            Core.rotate(mRGBAT, mRGBAT, Core.ROTATE_90_CLOCKWISE);
        }else{
            Core.flip(mRGBA, mRGBAT, -1);
            Core.rotate(mRGBAT, mRGBAT, Core.ROTATE_90_COUNTERCLOCKWISE);
        }//

        Imgproc.resize(mRGBAT, mRGBAT, mRGBA.size());


        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mRGBAT, faceDetections);
        img.temface = false;
        for (Rect rect : faceDetections.toArray()){
            Imgproc.rectangle(mRGBAT, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255 , 0));
            //Imgproc.putText(mRGBAT, "Hello World", new Point(rect.x, rect.y - 10), Imgproc.FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(36, 255 , 12), 2);
            img.temface = true;
        }
        img.img = mRGBAT;

        return mRGBAT;
        //##########FIM###########
    }
}