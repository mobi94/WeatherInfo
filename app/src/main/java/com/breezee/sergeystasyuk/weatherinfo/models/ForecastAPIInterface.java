package com.breezee.sergeystasyuk.weatherinfo.models;

import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.fivedaysforecast.FiveDaysForecastResult;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by User on 13.02.2017.
 */

public interface ForecastAPIInterface {
    @GET("1day/{location_key}")
    Observable<DailyForecastResult> getDailyForecast(@Path("location_key") String locationKey,
                                                     @QueryMap Map<String, String> request);
    @GET("5day/{location_key}")
    Observable<FiveDaysForecastResult> getFiveDaysForecast(@Path("location_key") String locationKey,
                                                           @QueryMap Map<String, String> request);
}
