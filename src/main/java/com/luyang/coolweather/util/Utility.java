package com.luyang.coolweather.util;

import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.luyang.coolweather.vo.City;
import com.luyang.coolweather.vo.County;
import com.luyang.coolweather.vo.Province;
import com.luyang.coolweather.vo.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by luyang on 2017/12/5.
 */

public class Utility {

    private static Gson gson = new Gson();



    public static Weather handleWeatherData(String response){
        try {
            JSONObject jb=new JSONObject(response);
            JSONArray arr= jb.getJSONArray("HeWeather");
            String weatherContent = arr.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * handle province data
     *
     * @param response:json data
     * @return
     */
    public static boolean handleProvince(String response) {
        try {
            if (!TextUtils.isEmpty(response)) {
                List<Province> provinceArr = gson.fromJson(response, new TypeToken<List<Province>>() {
                }.getType());
                provinceArr.forEach((p) -> {
                    Province pp = new Province();
                    pp.setProvinceName(p.getProvinceName());
                    pp.setProvinceCode(p.getProvinceCode());
                    pp.save();
                });
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleProvinces(String response) {
        try {
            Log.d("RESPONSE", response);
            if (!TextUtils.isEmpty(response)) {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject j = allProvinces.getJSONObject(i);
                    Log.d("Name", j.getString("name"));
                    Province p = new Province();
                    p.setProvinceName(j.getString("name"));
                    p.setProvinceCode(j.getInt("id"));
                    p.save();
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean handleCity(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            List<City> cityArr = gson.fromJson(response, new TypeToken<List<City>>() {
            }.getType());
            cityArr.forEach(c -> {
                City city = new City();
                city.setCityName(c.getCityName());
                city.setCityCode(c.getCityCode());
                city.setProvinceId(provinceId);
                city.save();
            });

            return true;
        } else {
            return false;
        }
    }

    public static boolean handleCities(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject j = allCities.getJSONObject(i);
                    City c = new City();
                    c.setCityName(j.getString("name"));
                    c.setCityCode(j.getInt("id"));
                    c.setProvinceId(provinceId);
                    c.save();
                }

                return true;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCounty(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            List<County> cityArr = gson.fromJson(response, new TypeToken<List<County>>() {
            }.getType());
            cityArr.forEach(c -> {
                County county = new County();
                county.setCountyName(c.getCountyName());
                county.setCityId(cityId);
                county.setWeatherId(c.getWeatherId());
                county.save();
            });
            return true;
        } else {
            return false;
        }
    }

    public static boolean handleCounties(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject j = allCounties.getJSONObject(i);
                    County c = new County();
                    c.setCountyName(j.getString("name"));
                    c.setWeatherId(j.getString("weatherId"));
                    c.setCityId(cityId);
                    c.save();
                }
                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
