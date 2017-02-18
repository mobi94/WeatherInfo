package com.breezee.sergeystasyuk.weatherinfo.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.breezee.sergeystasyuk.weatherinfo.R;
import com.breezee.sergeystasyuk.weatherinfo.TrackLocation;
import com.breezee.sergeystasyuk.weatherinfo.activities.MainActivity;
import com.breezee.sergeystasyuk.weatherinfo.pojos.fivedaysforecast.FiveDaysForecastResult;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;
import com.breezee.sergeystasyuk.weatherinfo.presenters.FiveDaysForecastPresenter;
import com.breezee.sergeystasyuk.weatherinfo.presenters.GeopositionSearchPresenter;
import com.breezee.sergeystasyuk.weatherinfo.views.AccuweatherAPIView;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 09.02.2017.
 */

public class ForecastFragment extends Fragment implements AccuweatherAPIView<FiveDaysForecastResult> {

    ForecastAdapter itemArrayAdapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    GeopositionSearchPresenter geopositionSearchPresenter;
    FiveDaysForecastPresenter fiveDaysForecastPresenter;

    GeopositionSearchResult geopositionSearchResult;
    FiveDaysForecastResult fiveDaysForecastResult;

    TrackLocation trackLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (trackLocation.isNetworkAvailable()) trackLocation.buildRequest();
            else {
                Toast.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        itemArrayAdapter = new ForecastAdapter();
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        trackLocation = new TrackLocation(getContext(), this::sendGeopositionSearchRequest);

        if (savedInstanceState == null) {
            if (trackLocation.isNetworkAvailable()) {
                swipeRefreshLayout.setRefreshing(true);
                trackLocation.buildRequest();
            }
            else loadDataFromLocalStore();
        }
        else loadDataFromLocalStore();

        setupPresenters();

        return view;
    }

    /*
    * GeopositionSearch and FiveDaysForecast presenters initialising.
    * */
    public void setupPresenters(){
        geopositionSearchPresenter = new GeopositionSearchPresenter(new AccuweatherAPIView<GeopositionSearchResult>() {
            @Override
            public void showSearchResult(GeopositionSearchResult geopositionSearchResult) {
                ForecastFragment.this.geopositionSearchResult = geopositionSearchResult;
                sendFiveDaysForecastRequest(geopositionSearchResult);
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
        fiveDaysForecastPresenter = new FiveDaysForecastPresenter(this);
    }

    /*
     * RecyclerView adapter updating.
     * */
    public void updateRecyclerView(){
        itemArrayAdapter.notifyDataSetChanged();
    }

    /*
     * If no internet connection - load data from shared preferences.
     * */
    public void loadDataFromLocalStore(){
        if (getData()) {
            updateRecyclerView();
        }
    }

    public void saveData(){
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, geopositionSearchResult);
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.FIVE_DAYS_FORECAST_DATA, fiveDaysForecastResult);
    }

    public boolean getData() {
        geopositionSearchResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, GeopositionSearchResult.class);
        fiveDaysForecastResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.FIVE_DAYS_FORECAST_DATA, FiveDaysForecastResult.class);
        return geopositionSearchResult != null && fiveDaysForecastResult != null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        geopositionSearchPresenter.onDestroy();
        fiveDaysForecastPresenter.onDestroy();
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
     * Sending REST request for 5 days forecast data retrieving.
     * */
    public void sendFiveDaysForecastRequest(GeopositionSearchResult geopositionSearchResult){
        String locationKey = geopositionSearchResult.getKey();
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("language", getString(R.string.geoposition_result_language));
        request.put("metric", "true");
        fiveDaysForecastPresenter.getData(locationKey, request);
    }

    @Override
    public void showSearchResult(FiveDaysForecastResult fiveDaysForecastResult) {
        swipeRefreshLayout.setRefreshing(false);
        this.fiveDaysForecastResult = fiveDaysForecastResult;
        saveData();
        updateRecyclerView();
    }

    @Override
    public void showError(String error) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {}

    public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

        @Override
        public int getItemCount() {
            if (fiveDaysForecastResult != null)
                return fiveDaysForecastResult.getDailyForecasts().size();
            else return 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
            if (fiveDaysForecastResult != null) {
                holder.dayOfWeek.setText(android.text.format.DateFormat.format("dd.MM, EEEE",
                        new Date(fiveDaysForecastResult.getDailyForecasts().get(listPosition).getEpochDate() * 1000)));
                holder.dayDescription.setText(fiveDaysForecastResult.getDailyForecasts().get(listPosition).getDay().getIconPhrase());
                holder.dayTemperature.setText(fiveDaysForecastResult.getDailyForecasts().get(listPosition).
                        getTemperature().getMaximum().getValue().toString() + " °C");
                holder.dayIcon.setImageResource(MainActivity.getDrawableResourceId(getContext(), "weather",
                        fiveDaysForecastResult.getDailyForecasts().get(listPosition).getDay().getIcon()));
                holder.nightDescription.setText(fiveDaysForecastResult.getDailyForecasts().get(listPosition).getNight().getIconPhrase());
                holder.nightTemperature.setText(fiveDaysForecastResult.getDailyForecasts().get(listPosition).
                        getTemperature().getMinimum().getValue().toString() + " °C");
                holder.nightIcon.setImageResource(MainActivity.getDrawableResourceId(getContext(), "weather",
                        fiveDaysForecastResult.getDailyForecasts().get(listPosition).getNight().getIcon()));
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView dayOfWeek;
            TextView dayDescription;
            TextView dayTemperature;
            ImageView dayIcon;
            TextView nightDescription;
            TextView nightTemperature;
            ImageView nightIcon;

            ViewHolder(View itemView) {
                super(itemView);
                dayOfWeek = (TextView) itemView.findViewById(R.id.day_of_week);
                dayDescription = (TextView) itemView.findViewById(R.id.day_description);
                dayTemperature = (TextView) itemView.findViewById(R.id.day_temperature);
                dayIcon = (ImageView) itemView.findViewById(R.id.day_icon);
                nightDescription = (TextView) itemView.findViewById(R.id.night_description);
                nightTemperature = (TextView) itemView.findViewById(R.id.night_temperature);
                nightIcon = (ImageView) itemView.findViewById(R.id.night_icon);
            }
        }
    }
}