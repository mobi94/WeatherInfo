package com.breezee.sergeystasyuk.weatherinfo.views;

import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;

/**
 * Created by User on 12.02.2017.
 */

public interface GeopositionSearchView {
    void showGeopositionSearchResult(GeopositionSearchResult geopositionSearchResult);
    void showError(String error);
    void onComplete();
}
