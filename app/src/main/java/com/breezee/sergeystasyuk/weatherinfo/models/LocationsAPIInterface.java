package com.breezee.sergeystasyuk.weatherinfo.models;

import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by User on 12.02.2017.
 */

public interface LocationsAPIInterface {
    @GET("search")
    Observable<GeopositionSearchResult> getLocationInfo(@QueryMap Map<String, String> request);
}
