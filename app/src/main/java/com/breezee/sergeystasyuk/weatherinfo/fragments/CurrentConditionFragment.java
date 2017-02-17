package com.breezee.sergeystasyuk.weatherinfo.fragments;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.breezee.sergeystasyuk.weatherinfo.R;
import com.breezee.sergeystasyuk.weatherinfo.TrackLocation;
import com.breezee.sergeystasyuk.weatherinfo.activities.MainActivity;
import com.breezee.sergeystasyuk.weatherinfo.pojos.dailyforecast.DailyForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;
import com.breezee.sergeystasyuk.weatherinfo.presenters.DailyForecastPresenter;
import com.breezee.sergeystasyuk.weatherinfo.presenters.GeopositionSearchPresenter;
import com.breezee.sergeystasyuk.weatherinfo.views.AccuweatherAPIView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 09.02.2017.
 */

public class CurrentConditionFragment extends Fragment implements AccuweatherAPIView<List<DailyForecastResult>> {

    ImageView background;
    TextView temperature;
    TextView city;
    TextView description;
    TextView realFeel;
    TextView realFeelShadow;
    TextView wind;
    TextView pressure;
    TextView relativeHumidity;
    TextView last6Hours;
    TextView last12Hours;
    TextView last24Hours;

    SwipeRefreshLayout swipeRefreshLayout;

    GeopositionSearchPresenter geopositionSearchPresenter;
    DailyForecastPresenter dailyForecastPresenter;

    GeopositionSearchResult geopositionSearchResult;
    DailyForecastResult dailyForecastResult;

    TrackLocation trackLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_condition, container, false);

        setupViews(view);
        setupPresenters();

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (trackLocation.isNetworkAvailable()) trackLocation.buildRequest();
            else {
                Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        trackLocation = new TrackLocation(getContext(), this::sendGeopositionSearchRequest);

        if (savedInstanceState == null) {
            if (trackLocation.isNetworkAvailable()) {
                swipeRefreshLayout.setRefreshing(true);
                trackLocation.buildRequest();
            }
            else {
                getData();
                inflateViews();
            }
        }
        else {
            getData();
            inflateViews();
        }

        return view;
    }

    /*
    * GeopositionSearch and DailyForecast presenters initialising .
    * */
    public void setupPresenters(){
        geopositionSearchPresenter = new GeopositionSearchPresenter(new AccuweatherAPIView<GeopositionSearchResult>() {
            @Override
            public void showSearchResult(GeopositionSearchResult geopositionSearchResult) {
                CurrentConditionFragment.this.geopositionSearchResult = geopositionSearchResult;
                sendDailyForecastRequest(geopositionSearchResult);
            }

            @Override
            public void showError(String error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });
        dailyForecastPresenter = new DailyForecastPresenter(this);
    }

    public void setupViews(View view){
        background = (ImageView) getActivity().findViewById(R.id.background);
        temperature = (TextView) getActivity().findViewById(R.id.current_temperature);
        city = (TextView) getActivity().findViewById(R.id.city);
        description = (TextView) getActivity().findViewById(R.id.weather_description);
        realFeel = (TextView) view.findViewById(R.id.real_feel_value);
        realFeelShadow = (TextView) view.findViewById(R.id.real_feel_shadow_value);
        wind = (TextView) view.findViewById(R.id.wind_value);
        pressure = (TextView) view.findViewById(R.id.pressure_value);
        relativeHumidity = (TextView) view.findViewById(R.id.relative_humidity_value);
        last6Hours = (TextView) view.findViewById(R.id.last6hours_value);
        last12Hours = (TextView) view.findViewById(R.id.last12hours_value);
        last24Hours = (TextView) view.findViewById(R.id.last24hours_value);
    }

    /*
    * Weather background drawable resource choosing.
    * (I couldn`t find all backgrounds, so there are these comparisons)
    * */
    public void setBackground(){
        int iconId = dailyForecastResult.getWeatherIcon();
        if (iconId >= 13 && iconId < 15) iconId = 13;
        if (iconId >= 16 && iconId < 19) iconId = 16;
        if (iconId >= 20 && iconId < 22) iconId = 20;
        if (iconId >= 22 && iconId < 24) iconId = 22;
        if (iconId >= 25 && iconId < 30) iconId = 25;
        if (iconId >= 35 && iconId < 37) iconId = 35;
        if (iconId >= 39 && iconId < 41) iconId = 39;
        if (iconId >= 41 && iconId < 43) iconId = 41;
        if (iconId >= 43) iconId = 43;
        background.setImageResource(MainActivity.getDrawableResourceId(getContext(), "bg", iconId));
    }

    /*
     * Using REST request processing results to inflate all the views.
     * */
    public void inflateViews(){
        setBackground();
        ((TextView) getActivity().findViewById(R.id.separator)).setText("|");
        temperature.setText(dailyForecastResult.getTemperature().getMetric().getValue().toString() + " °C");
        city.setText(geopositionSearchResult.getLocalizedName());
        description.setText(dailyForecastResult.getWeatherText());
        realFeel.setText(dailyForecastResult.getRealFeelTemperature().getMetric().getValue().toString() + " °C");
        realFeelShadow.setText(dailyForecastResult.getRealFeelTemperatureShade().getMetric().getValue().toString() + " °C");
        wind.setText(dailyForecastResult.getWind().getSpeed().getMetric().getValue().toString() + " "
                + getString(R.string.current_forecast_wind_value) + " "
                + dailyForecastResult.getWind().getDirection().getLocalized());
        pressure.setText(dailyForecastResult.getPressure().getMetric().getValue().toString() + " "
                + getString(R.string.current_forecast_pressure_value));
        relativeHumidity.setText(dailyForecastResult.getRelativeHumidity().toString() + " %");
        last6Hours.setText(dailyForecastResult.getTemperatureSummary().getPast6HourRange().getMinimum().getMetric().getValue().toString() +
                "/" + dailyForecastResult.getTemperatureSummary().getPast6HourRange().getMaximum().getMetric().getValue().toString() + " °C");
        last12Hours.setText(dailyForecastResult.getTemperatureSummary().getPast12HourRange().getMinimum().getMetric().getValue().toString() +
                "/" + dailyForecastResult.getTemperatureSummary().getPast12HourRange().getMaximum().getMetric().getValue().toString() + " °C");
        last24Hours.setText(dailyForecastResult.getTemperatureSummary().getPast24HourRange().getMinimum().getMetric().getValue().toString() +
                "/" + dailyForecastResult.getTemperatureSummary().getPast24HourRange().getMaximum().getMetric().getValue().toString() + " °C");
    }

    public void saveData(){
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, geopositionSearchResult);
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.DAILY_FORECAST_DATA, dailyForecastResult);
    }

    public void getData() {
        geopositionSearchResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, GeopositionSearchResult.class);
        dailyForecastResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.DAILY_FORECAST_DATA, DailyForecastResult.class);
    }

    /*
    * Sending REST request for current location data retrieving.
    * */
    private void sendGeopositionSearchRequest(Location location){
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("q", String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude()));
        request.put("language", getString(R.string.geoposition_result_language));
        geopositionSearchPresenter.getData(request);
    }

    /*
     * Sending REST request for current forecast data retrieving.
     * */
    public void sendDailyForecastRequest(GeopositionSearchResult geopositionSearchResult){
        String locationKey = geopositionSearchResult.getKey();
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("language", getString(R.string.geoposition_result_language));
        request.put("details", "true");
        dailyForecastPresenter.getData(locationKey, request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dailyForecastPresenter.onDestroy();
        geopositionSearchPresenter.onDestroy();
    }

    @Override
    public void showSearchResult(List<DailyForecastResult> dailyForecastResult) {
        swipeRefreshLayout.setRefreshing(false);
        this.dailyForecastResult = dailyForecastResult.get(0);
        saveData();
        inflateViews();
    }

    @Override
    public void showError(String error) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {}
}