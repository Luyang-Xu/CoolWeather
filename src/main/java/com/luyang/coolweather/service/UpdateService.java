package com.luyang.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import com.luyang.coolweather.util.HttpUtil;
import com.luyang.coolweather.util.Utility;
import com.luyang.coolweather.vo.Weather;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by luyang on 2017/12/13.
 */

public class UpdateService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //设置每6小时更新一次后台任务
        int anHour = 6 * 60 * 60 * 3600;
        long triggerTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, UpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);

    }

    public void updateWeather() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherContent = pref.getString("weather", null);
        if (weatherContent != null) {
            Weather w = Utility.handleWeatherData(weatherContent);
            String weatherId = w.basic.weatherId;
            String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0f98def397fe466b86498420b0b17c51";
            HttpUtil.sendHttpRequest(url, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherData = response.body().string();
                    Weather ww = Utility.handleWeatherData(weatherData);
                    if (ww != null && ww.status.equals("ok")) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                        editor.putString("weather", weatherData);
                        editor.apply();
                    }
                }
            });
        }

    }

    public void updateBingPic() {
        String url = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String picUrl = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdateService.this).edit();
                editor.putString("bing_pic",picUrl);
                editor.apply();
            }
        });
    }
}
