package com.example.android.examenadolfo.data.network.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailUsersResponse {


    @Expose
    @SerializedName("support")
    Support support;

    @Expose
    @SerializedName("data")
    User data;

    public DetailUsersResponse() {
    }

    public Support getSupport() {
        return support;
    }

    public void setSupport(Support support) {
        this.support = support;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public DetailUsersResponse(Support support, User data) {
        this.support = support;
        this.data = data;
    }
}