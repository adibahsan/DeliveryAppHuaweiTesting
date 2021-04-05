package com.journaldev.androidretrofitcalleveryxsecond;

import com.google.gson.annotations.SerializedName;

public class RestResponse {

    @SerializedName("status")
    int status;
    @SerializedName("message")
    public String message;
    @SerializedName("result")
    public String result;
}