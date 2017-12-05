package com.luyang.coolweather.vo;

import org.litepal.crud.DataSupport;

/**
 * Created by luyang on 2017/12/4.
 */

public class Province extends DataSupport {
    int id;

    String provinceName;

    int provinceCode;


    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
