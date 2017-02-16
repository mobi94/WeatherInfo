package com.breezee.sergeystasyuk.weatherinfo.presenters;

import android.os.Bundle;

import com.breezee.sergeystasyuk.weatherinfo.models.GeopositionSearchModel;
import com.breezee.sergeystasyuk.weatherinfo.models.GeopositionSearchModelImpl;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;
import com.breezee.sergeystasyuk.weatherinfo.views.AccuweatherAPIView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;


/**
 * Created by User on 12.02.2017.
 */

public class GeopositionSearchPresenter extends BasePresenter<GeopositionSearchModel, GeopositionSearchResult> {

    public GeopositionSearchPresenter(AccuweatherAPIView<GeopositionSearchResult> view) {
        super(view);
        this.view = view;
        model = new GeopositionSearchModelImpl();
    }

    public void getData(Map<String, String> request) {
        unsubscribeSubscription();

        Action1<GeopositionSearchResult> onNextAction = geopositionSearchResult -> {
            if (geopositionSearchResult != null) {
                view.showSearchResult(geopositionSearchResult);
            }
        };
        Action1<Throwable> onErrorAction = throwable -> view.showError(throwable.getMessage());
        Action0 onCompletedAction = () -> view.onComplete();

        subscription = model.getLocationInfo(request).subscribe(
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
