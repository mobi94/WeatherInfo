package com.breezee.sergeystasyuk.weatherinfo.presenters;

import com.breezee.sergeystasyuk.weatherinfo.models.GeopositionSearchModel;
import com.breezee.sergeystasyuk.weatherinfo.models.GeopositionSearchModelImpl;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;
import com.breezee.sergeystasyuk.weatherinfo.views.GeopositionSearchView;

import java.util.Map;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;


/**
 * Created by User on 12.02.2017.
 */

public class GeopositionSearchPresenter implements BasePresenter<Map<String, String>> {
    private GeopositionSearchModel model = new GeopositionSearchModelImpl();
    private Subscription subscription = Subscriptions.empty();
    private GeopositionSearchView view;

    public GeopositionSearchPresenter(GeopositionSearchView view) {
        this.view = view;
    }

    @Override
    public void getData(Map<String, String> request) {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        Action1<GeopositionSearchResult> onNextAction = geopositionSearchResult -> {
            if (geopositionSearchResult != null) {
                view.showGeopositionSearchResult(geopositionSearchResult);
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
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
