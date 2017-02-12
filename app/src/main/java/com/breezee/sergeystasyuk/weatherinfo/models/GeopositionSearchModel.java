package com.breezee.sergeystasyuk.weatherinfo.models;

import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;

import java.util.Map;

import rx.Observable;

/**
 * Created by User on 12.02.2017.
 */

public interface GeopositionSearchModel {
    Observable<GeopositionSearchResult> getLocationInfo(Map<String, String> request);
}
