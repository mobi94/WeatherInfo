package com.breezee.sergeystasyuk.weatherinfo.presenters;

import android.os.Bundle;

import com.breezee.sergeystasyuk.weatherinfo.models.ForecastModel;
import com.breezee.sergeystasyuk.weatherinfo.models.ForecastModelImpl;
import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.views.AccuweatherAPIView;

import java.util.List;
import java.util.Map;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by User on 13.02.2017.
 */

public class DailyForecastPresenter extends BasePresenter<ForecastModel, List<DailyForecastResult>> {

    public DailyForecastPresenter(AccuweatherAPIView<List<DailyForecastResult>> view) {
        super(view);
        model = new ForecastModelImpl();
    }

    public void getData(String locationKey, Map<String, String> request) {
        unsubscribeSubscription();

        Action1<List<DailyForecastResult>> onNextAction = dailyForecastResult -> {
            if (dailyForecastResult != null) {
                view.showSearchResult(dailyForecastResult);
            }
        };
        Action1<Throwable> onErrorAction = throwable -> view.showError(throwable.getMessage());
        Action0 onCompletedAction = () -> view.onComplete();

        subscription = model.getDailyForecast(locationKey, request).subscribe(
                onNextAction,
                onErrorAction,
                onCompletedAction
        );
    }

    @Override
    public void onDestroy() {
        unsubscribeSubscription();
    }
}
