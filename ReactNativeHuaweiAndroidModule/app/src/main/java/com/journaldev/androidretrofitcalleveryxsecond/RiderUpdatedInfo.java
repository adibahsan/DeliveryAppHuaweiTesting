package com.journaldev.androidretrofitcalleveryxsecond;

import com.google.gson.annotations.SerializedName;

public class RiderUpdatedInfo {
    @SerializedName("sequenceId")
    public int sequenceId;

    @SerializedName("riderState")
    public RiderState riderState;
}


enum  RiderState {
    LOGIN,
    LOGOUT,
    NON_EXISTENT,
    ONLINE,
    OFFLINE,
    ON_THE_WAY_TO_PICKUP,
    ON_THE_WAY_TO_TRANSFERRED_PICKUP,
    REACHED_MERCHANT,
    PICKUP_DELIVERY,
    ON_THE_WAY_TO_TRANSFERRED_DELIVERY,
    ON_THE_WAY_TO_DELIVERY,
    NETWORK_ERROR,
    REACHED_CONSUMER,
    DELIVERED,
    COLLECT_CASH,
    RATE_ORDER,
    REQUESTED,
    TRANSFER_REQUESTED,
    TRANSFER_PENDING,
    TRANSFERRED_AFTER_PICKUP,
    TRANSFERRED_BEFORE_PICKUP,
    RESERVED
}