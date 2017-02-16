package com.breezee.sergeystasyuk.weatherinfo.views;

import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;

/**
 * Created by User on 12.02.2017.
 */

public interface AccuweatherAPIView<P> {
    void showSearchResult(P p);
    void showError(String error);
    void onComplete();
}
