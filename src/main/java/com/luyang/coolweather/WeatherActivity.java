package com.luyang.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.luyang.coolweather.service.UpdateService;
import com.luyang.coolweather.util.HttpUtil;
import com.luyang.coolweather.util.Utility;
import com.luyang.coolweather.vo.Weather;

import java.io.IOException;
import java.util.zip.Inflater;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by luyang on 2017/12/11.
 */

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weaterLayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    //实现下拉刷新天气
    public SwipeRefreshLayout swipeRefreshLayout;

    private String mWeatherId;

    //实现滑动菜单选择城市
    public DrawerLayout drawerLayout;

    private Button nav;


    /**
     * 初始化界面
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏与背景一体化
        View decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_weather);
        //初始化各个控件
        weaterLayout = findViewById(R.id.weather_layout);

        titleCity = findViewById(R.id.title_city);

        titleUpdateTime = findViewById(R.id.title_update_time);

        degreeText = findViewById(R.id.degree_text);

        weatherInfoText = findViewById(R.id.weather_info_text);

        forecastLayout = findViewById(R.id.forecast_layout);

        aqiText = findViewById(R.id.aqi_text);

        pm25Text = findViewById(R.id.pm25_text);

        comfortText = findViewById(R.id.comfort_text);

        carWashText = findViewById(R.id.car_wash_text);

        sportText = findViewById(R.id.sport_text);

        bingPicImg = findViewById(R.id.bing_pic_img);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        drawerLayout = findViewById(R.id.drawer_layout);
        nav=findViewById(R.id.nav_button);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
        String weatherInfo = pref.getString("weather", null);
        String bingPic = pref.getString("bing_pic", null);

        if (weatherInfo != null) {
            Weather weather = Utility.handleWeatherData(weatherInfo);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {

            String weatherId = getIntent().getStringExtra("weather_id");
            mWeatherId = weatherId;
            weaterLayout.setVisibility(View.INVISIBLE);
            queryFromServer(weatherId);
        }

        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        //下拉刷新更新天气监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFromServer(mWeatherId);
            }
        });

        //滑动菜单
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * 初始化时候从服务器获取天气信息
     *
     * @param weatherId
     */
    public void queryFromServer(final String weatherId) {
        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=0f98def397fe466b86498420b0b17c51";
        HttpUtil.sendHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_LONG).show();
                });
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherContent = response.body().string();
                final Weather weather = Utility.handleWeatherData(weatherContent);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", weatherContent);
                        mWeatherId = weather.basic.weatherId;
                        editor.apply();
                        showWeatherInfo(weather);
                        loadBingPic();
                    } else {
                        Toast.makeText(WeatherActivity.this, "加载出错", Toast.LENGTH_LONG).show();
                    }
                    //设置下拉刷新不可用，将加载样式取消掉，取消进度条
                    swipeRefreshLayout.setRefreshing(false);

                });

            }
        });
    }

    /**
     * 展示天气信息，设置布局对应的数据显示
     *
     * @param weather
     */
    public void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.basic.update.updateTime.split("")[1]);
        degreeText.setText(weather.now.temperature + "℃");
        weatherInfoText.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        weather.forecastList.forEach((w) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView maxText = findViewById(R.id.max_text);
            TextView minText = findViewById(R.id.min_text);
            TextView dateText = findViewById(R.id.date_text);
            TextView infoText = findViewById(R.id.info_text);

            maxText.setText(w.temperature.max);
            minText.setText(w.temperature.min);
            dateText.setText(w.date);
            infoText.setText(w.more.info);

            forecastLayout.addView(view);
        });
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        comfortText.setText(weather.suggestion.comfort.info);
        carWashText.setText(weather.suggestion.carWash.info);
        sportText.setText(weather.suggestion.sport.info);
        weaterLayout.setVisibility(View.VISIBLE);
        //激活后台服务更新
        Intent intent = new Intent(this, UpdateService.class);
        startService(intent);

    }

    /**
     * 加载背景图片
     */
    public void loadBingPic() {
        String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String picUrl = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", picUrl);
                editor.apply();
                runOnUiThread(() -> {
                    Glide.with(WeatherActivity.this).load(picUrl).into(bingPicImg);
                });
            }
        });
    }
}
