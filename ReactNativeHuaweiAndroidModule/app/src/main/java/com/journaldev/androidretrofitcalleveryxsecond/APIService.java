package com.journaldev.androidretrofitcalleveryxsecond;


import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {


    String BASE_URL = "https://dadelivery.appigo.co/riders-api/";

    @GET("{path}")
    Observable<RiderUpdatedInfo> getRandomJoke( @Header("Authorization") String auth, @Path("path") String path);



    @GET("{path}")
    Call<RiderUpdatedInfo> getSync( @Header("Authorization") String auth, @Path("path") String path);

    @POST("{path}")
    Call<RestResponse> goToOnline(@Header("Authorization") String auth, @Path("path") String path);
}
