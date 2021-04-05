package com.journaldev.androidretrofitcalleveryxsecond;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;
    public TextView textView;
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

    Button b1, b2, b3;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = findViewById(R.id.startButton);
        b2 = findViewById(R.id.stopButton);
        b3 = findViewById(R.id.getSync);

        intent = new Intent(this, MyService.class);



        textView = findViewById(R.id.textView);
        initiateRetrofit();


    }

    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, MyAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, 30000, pi);
        Log.d("Jokes", "Alarm just fired at"+time);

        Toast.makeText(this, "Alarm is set "+time, Toast.LENGTH_SHORT).show();
    }


    private void stopAlarm(){
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, MyAlarm.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        am.cancel(pi);

        Log.d("Jokes", "Alarm just stopped at"+System.currentTimeMillis());
        Toast.makeText(this, "Alarm is Stopped", Toast.LENGTH_SHORT).show();


    }

    public void StartForeGroundService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.d("Jokes","Starting Service");
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }


    public void startService(View view){
//        StartForeGroundService();
        sendToOnline("online");
    }

    public void stopService(View view){

       sendToOnline("offline");
    }

    public void getSync(View view){

        getSync("sync");
    }

    public void initiateRetrofit() {
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


        disposable = Observable.interval(1000, 5000,
                TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::callJokesEndpoint, this::onError);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(intent);
        stopAlarm();

        if (disposable.isDisposed()) {
            disposable = Observable.interval(1000, 5000,
                    TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::callJokesEndpoint, this::onError);
        }
    }

    private void callJokesEndpoint(Long aLong) {


        Observable<RiderUpdatedInfo> observable = apiService.getRandomJoke(authToken, "sync");
        observable.subscribeOn(Schedulers.newThread()).
                observeOn(AndroidSchedulers.mainThread())
                .map(result -> result.riderState.toString())
                .subscribe(this::handleResults, this::handleError);
    }


    private void sendToOnline(String path) {

        Call<RestResponse> observable = apiService.goToOnline(authToken, path);
        observable.enqueue(new Callback<RestResponse>() {
            @Override
            public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                if(response.isSuccessful()){
                    Log.d("Jokes",response.toString());
                }
            }

            @Override
            public void onFailure(Call<RestResponse> call, Throwable t) {
                    Log.e("Jokes",t.getLocalizedMessage());
            }
        });

    }


    private void getSync(String path) {

        Call<RiderUpdatedInfo> observable = apiService.getSync(authToken, path);
        observable.enqueue(new Callback<RiderUpdatedInfo>() {
            @Override
            public void onResponse(Call<RiderUpdatedInfo> call, Response<RiderUpdatedInfo> response) {
                if(response.isSuccessful()){
                    Log.d("Jokes", response.body().toString());
                    textView.setText(response.body().riderState + " " + LocalDateTime.now().toString());
                }
            }

            @Override
            public void onFailure(Call<RiderUpdatedInfo> call, Throwable t) {
                Log.e("Jokes",t.getLocalizedMessage());
            }
        });

    }

    private void onError(Throwable throwable) {
        Toast.makeText(this, "OnError in Observable Timer",
                Toast.LENGTH_LONG).show();
    }


    private void handleResults(String joke) {


        if (!TextUtils.isEmpty(joke)) {
            Log.d("Jokes", joke);

            textView.setText(joke + " " + LocalDateTime.now().toString());


        } else {
            Toast.makeText(this, "NO RESULTS FOUND",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(Throwable t) {

        //Add your error here.
    }

    @Override
    protected void onPause() {
        super.onPause();
        disposable.dispose();
        StartForeGroundService();
        setAlarm(System.currentTimeMillis()+30000);
    }
}
