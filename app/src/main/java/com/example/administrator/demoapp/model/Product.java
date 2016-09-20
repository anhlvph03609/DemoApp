package com.example.administrator.demoapp.model;

/**
 * Created by Administrator on 01/09/2016.
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {
    @SerializedName("pid")
    public int pid;

    @SerializedName("name")
    public String name;

    @SerializedName("username")
    public String username;

    @SerializedName("price")
    public float price;

    @SerializedName("image_url")
    public String image_url;
}
