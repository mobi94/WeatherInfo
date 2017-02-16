package com.breezee.sergeystasyuk.weatherinfo.models;

import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.fivedaysforecast.FiveDaysForecastResult;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by User on 13.02.2017.
 */

public class ForecastModelImpl implements ForecastModel {
    private ForecastAPIInterface apiInterface;

    public ForecastModelImpl() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .baseUrl("http://dataservice.accuweather.com/")
                .client(client)
                .build();
        apiInterface = retrofit.create(ForecastAPIInterface.class);
    }

    @Override
    public Observable<List<DailyForecastResult>> getDailyForecast(String locationKey, Map<String, String> request) {
        return apiInterface.getDailyForecast(locationKey, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
    }

    @Override
    public Observable<FiveDaysForecastResult> getFiveDaysForecast(String locationKey, Map<String, String> request) {
        return apiInterface.getFiveDaysForecast(locationKey, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
    }
}
