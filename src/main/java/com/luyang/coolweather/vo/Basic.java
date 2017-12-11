package com.luyang.coolweather.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luyang on 2017/12/8.
 */

public class Basic {

    @SerializedName("city")
     String cityName;

    @SerializedName("id")
     int weatherId;

     Update update;

     class Update{

         @SerializedName("loc")
         public  String updateTime;

     }



}
