package com.luyang.coolweather.util;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luyang.coolweather.vo.City;
import com.luyang.coolweather.vo.County;
import com.luyang.coolweather.vo.Province;

import java.util.List;

/**
 * Created by luyang on 2017/12/5.
 */

public class Utility {

    private static Gson gson = new Gson();

    /**
     * handle province data
     *
     * @param response:json data
     * @return
     */
    public static boolean handleProvince(String response) {
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
        } else {
            return false;
        }
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
}