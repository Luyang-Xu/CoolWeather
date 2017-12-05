package com.luyang.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.luyang.coolweather.util.HttpUtil;
import com.luyang.coolweather.util.Utility;
import com.luyang.coolweather.vo.City;
import com.luyang.coolweather.vo.County;
import com.luyang.coolweather.vo.Province;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by luyang on 2017/12/5.
 */

public class ChooseAreaFragment extends Fragment {

    private int currentLevel;

    private static final int province_level = 0;

    private static final int city_level = 1;

    private static final int county_level = 2;

    private Button backButton;

    private ListView listview;

    private TextView titleText;

    private ArrayAdapter<String> adapter;

    private List<String> datalist = new ArrayList<>();

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;

    private City selectedCity;

    private ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false
        );
        backButton = view.findViewById(R.id.back_button);
        titleText = view.findViewById(R.id.title_text);
        listview = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, datalist);
        listview.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == province_level) {
                    selectedProvince = provinceList.get(position);
                    queryCity();
                } else if (currentLevel == city_level) {
                    selectedCity = cityList.get(position);
                    queryCounty();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == county_level) {
                    queryCity();
                } else if (currentLevel == city_level) {
                    queryProvince();
                }
            }
        });
        queryProvince();//数据查询入口
    }

    public void queryProvince() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            datalist.clear();
            provinceList.forEach(p -> {
                datalist.add(p.getProvinceName());
            });
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = province_level;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");

        }

    }

    public void queryCity() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        int pid = selectedProvince.getProvinceCode();
        cityList = DataSupport.where("provinceId=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            datalist.clear();
            cityList.forEach(c -> {
                datalist.add(c.getCityName());
            });
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = city_level;
        } else {
            String address = "http://guolin.tech/api/china/" + pid;
            queryFromServer(address, "city");
        }
    }

    public void queryCounty() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        int cid = selectedCity.getCityCode();
        countyList = DataSupport.where("cityId=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            datalist.clear();
            countyList.forEach(c -> {
                datalist.add(c.getCountyName());
            });
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = county_level;
        } else {
            String address = "http://guolin.tech/api/china/" + selectedProvince.getProvinceCode() + "/" + cid;
            queryFromServer(address, "county");

        }

    }

    public void queryFromServer(String address, final String type) {
        //showDialog();
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //clearDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                Log.d("JSONDATA",jsonData);
                boolean result = false;
                if (type.equals("province")) {
                    result = Utility.handleProvince(jsonData);
                } else if (type.equals("city")) {
                    result = Utility.handleCity(jsonData, selectedProvince.getId());
                } else if (type.equals("county")) {
                    result = Utility.handleCounty(jsonData, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (type.equals("province")) {
                                queryProvince();
                            } else if (type.equals("city")) {
                                queryCity();
                            } else if (type.equals("county")) {
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }

    public void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("正在加载");
        }
        progressDialog.show();

    }

    public void clearDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
