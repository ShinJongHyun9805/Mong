package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.contract.WeatherContract;
import com.puppyland.mongnang.model.WeatherModel;

public class WeatherPresenter implements WeatherContract.presenter {

    WeatherContract.view view;
    WeatherModel weatherModel;

    public WeatherPresenter(WeatherContract.view view) {
        this.view = view;
        weatherModel = new WeatherModel(this);
    }

    @Override
    public void Location(String se, String gu, String dong) {
        weatherModel.Location(se, gu, dong);
    }
}
