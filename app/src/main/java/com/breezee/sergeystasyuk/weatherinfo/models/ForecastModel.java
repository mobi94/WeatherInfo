package com.breezee.sergeystasyuk.weatherinfo.models;

import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.fivedaysforecast.FiveDaysForecastResult;

import java.util.Map;

import rx.Observable;

/**
 * Created by User on 13.02.2017.
 */

public interface ForecastModel {
    Observable<DailyForecastResult> getDailyForecast(String locationKey, Map<String, String> request);
    Observable<FiveDaysForecastResult> getFiveDaysForecast(String locationKey, Map<String, String> request);
}
