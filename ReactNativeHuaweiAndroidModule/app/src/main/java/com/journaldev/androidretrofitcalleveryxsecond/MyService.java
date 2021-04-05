package com.journaldev.androidretrofitcalleveryxsecond;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.journaldev.androidretrofitcalleveryxsecond.APIService.BASE_URL;

public class MyService extends Service {
    private String channelId = "ChannelId1";
    private int notificationId = 1231231;
    Retrofit retrofit;
    TextView textView;
    APIService apiService;
    Disposable disposable;
    String authToken = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJERU1hbGlNTU1uR05iQlFqMnp" +
            "IblJVaExiZTRfal9aZEVzdnNFa1hDbkFZIn0.eyJleHAiOjE2MjM2ODcyNzIsImlhdCI6MTYxNjQ4NzI3MSwiYX" +
            "V0aF90aW1lIjoxNjE2NDg3MjcwLCJqdGkiOiJmYjMzOTJjNC05OTI4LTRhMjAtOGFhNy0zMmY0MmEzZDE3NGYiL" +
            "CJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwOTAvYXV0aC9yZWFsbXMvQXBwaWdvLURlbGl2ZXJ5IiwiYXVkIjoi" +
            "YWNjb3VudCIsInN1YiI6ImI1YWY2ZTQ3LWE5OTAtNDc1NS1hOGI2LTQ2MTU1YmUyYThhZSIsInR5cCI6IkJlYXJ" +
            "lciIsImF6cCI6ImFwcGlnby1kZWxpdmVyeS1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiZjFjYmFlODEtZGY4Zi00Y2" +
            "IwLWEzODgtOTRkOThjNTE5M2I5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL2RlbGl2" +
            "ZXJ5LmFwcGlnby8iXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGh" +
            "vcml6YXRpb24iLCJSSURFUiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYW" +
            "dlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3Bl" +
            "bmlkIGVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InJ" +
            "pZGVyIiwiZW1haWwiOiJyaWRlckBnbWFpbC5jb20ifQ.S6S8WhUUeI5_RXCTM9QML0gIEPQeT84NpdyncAC-BBE" +
            "fR7xdnAXowC_tSahEx3VRzBtqOc_sT_P_C_WLU6xu1128IMHizJALYif-ZjH30K2Vh7cSMtc9YONMG7WCEo7cmR" +
            "xjMCbaWM5HFiSEPhjhl5hch7fugMZfXHE17BJe7etJk84dcmhMfmcb0dMhKB5bS0ERyqYGxoqcJulyYCuELN_XL" +
            "IIjB5YXsANlnwYzIjfopR47GUi7ixLvhBP_ZAmRMv4o-yVCxSqNo9-mAcg4riz3uNjXKb0jh__v55zFBYG1zJ4I" +
            "Kqg56wLtBYMKuhHEMRWAgZfrCP5gPdVFUlyBxA";



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();




        startForeground(notificationId,getNotification("Huawei Running Service","Huawei Service"));
        initiateRetrofit();
        return START_STICKY;
    }

    public Notification getNotification (String text, String title){
        Intent myIntent  = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,myIntent,0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        return  notification;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateNotification(String text, String title){
        Notification notification  = getNotification(text,title);
        final NotificationManager notificationManager = getSystemService(
                NotificationManager.class
        );
        notificationManager.notify(notificationId,notification);
    }



    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel  = new NotificationChannel(
                    channelId, "Foreground Notification", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager notificationManager = getSystemService(
                    NotificationManager.class
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public void initiateRetrofit() {
        Log.d("Jokes","Initiating Retrofit");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(APIService.class);


        disposable = Observable.interval(1000, 10000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callJokesEndpoint, this::onError);
    }


    private void callJokesEndpoint(Long aLong) {


        Observable<RiderUpdatedInfo> observable = apiService.getRandomJoke(authToken, "sync");
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(result -> result.riderState.toString())
                .subscribe(this::handleResults, this::handleError);
    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
    }

    private void getSync(String path) {

        Call<RiderUpdatedInfo> observable = apiService.getSync(authToken, path);
        observable.enqueue(new Callback<RiderUpdatedInfo>() {
            @Override
            public void onResponse(Call<RiderUpdatedInfo> call, Response<RiderUpdatedInfo> response) {
                if(response.isSuccessful()){
                    Log.d("Jokes", response.body().riderState + " " + LocalDateTime.now().toString());
//                    textView.setText(response.body().riderState + " " + LocalDateTime.now().toString());
                }
            }

            @Override
            public void onFailure(Call<RiderUpdatedInfo> call, Throwable t) {
                Log.e("Jokes",t.getLocalizedMessage());
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleResults(String joke) {


        if (!TextUtils.isEmpty(joke)) {
            Log.d("Jokes", joke + " " + LocalDateTime.now().toString());

            textView.setText(joke + " " + LocalDateTime.now().toString());
            updateNotification("Updating", "Updating");


        } else {
            Toast.makeText(this, "NO RESULTS FOUND",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(Throwable t) {

        //Add your error here.
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        disposable.dispose();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
