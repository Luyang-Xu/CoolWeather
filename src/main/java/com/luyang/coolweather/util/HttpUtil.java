package com.luyang.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by luyang on 2017/12/5.
 */

public class HttpUtil {
    public static void sendHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request res= new Request.Builder().url(address).build();
        client.newCall(res).enqueue(callback);
    }
}
