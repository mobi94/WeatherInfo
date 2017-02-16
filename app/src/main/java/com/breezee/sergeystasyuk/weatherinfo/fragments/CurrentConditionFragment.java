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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by User on 09.02.2017.
 */

public class CurrentConditionFragment extends Fragment implements AccuweatherAPIView<DailyForecastResult> {

    String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей", "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей" };

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    GeopositionSearchPresenter geopositionSearchPresenter;
    DailyForecastPresenter dailyForecastPresenter;

    GeopositionSearchResult geopositionSearchResult;
    DailyForecastResult dailyForecastResult;

    TrackLocation trackLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_condition, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            trackLocation.buildRequest();
        });
        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item);
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
        }
        else getData();
//        if (!getData()) {
//            if (trackLocation.isNetworkAvailable())
//                trackLocation.buildRequest();
//        }

        setupPresenters();

        return view;
    }

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

    public void saveData(){
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, geopositionSearchResult);
        MainActivity.saveObjectToSharedPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.DAILY_FORECAST_DATA, dailyForecastResult);
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
//        sharedPreferences.edit().putString(MainActivity.GEOPOSITION_DATA, new Gson().toJson(geopositionSearchResult)).apply();
//        sharedPreferences.edit().putString(MainActivity.DAILY_FORECAST_DATA, new Gson().toJson(dailyForecastResult)).apply();
    }

    public void getData() {
        geopositionSearchResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.GEOPOSITION_DATA, GeopositionSearchResult.class);
        dailyForecastResult = MainActivity.getSavedObjectFromPreference(getContext(),
                MainActivity.MY_PREFERENCES, MainActivity.DAILY_FORECAST_DATA, DailyForecastResult.class);
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
//        if (sharedPreferences.contains(MainActivity.GEOPOSITION_DATA) && sharedPreferences.contains(MainActivity.DAILY_FORECAST_DATA)) {
//            geopositionSearchResult = new Gson().fromJson(sharedPreferences.getString(MainActivity.GEOPOSITION_DATA, "{}"),
//                    new TypeToken<GeopositionSearchResult>() {}.getType());
//            dailyForecastResult = new Gson().fromJson(sharedPreferences.getString(MainActivity.DAILY_FORECAST_DATA, "{}"),
//                    new TypeToken<DailyForecastResult>() {}.getType());
//            return true;
//        }
//        else return false;
    }

    private void sendGeopositionSearchRequest(Location location){
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("q", String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude()));
        request.put("language", getString(R.string.geoposition_result_language));
        geopositionSearchPresenter.getData(request);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable(MainActivity.GEOPOSITION_DATA, new Gson().toJson(geopositionSearchPresenter));
//        outState.putSerializable(MainActivity.DAILY_FORECAST_DATA, new Gson().toJson(dailyForecastResult));
//        geopositionSearchPresenter.unsubscribeSubscription();
//        dailyForecastPresenter.unsubscribeSubscription();
    }

    @Override
    public void onStart() {
        super.onStart();
//        trackLocation.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
//        trackLocation.onStop();
//        geopositionSearchPresenter.onDestroy();
//        dailyForecastPresenter.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dailyForecastPresenter.onDestroy();
        geopositionSearchPresenter.onDestroy();
    }

    public void sendDailyForecastRequest(GeopositionSearchResult geopositionSearchResult){
        String locationKey = geopositionSearchResult.getKey();
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("language", getString(R.string.geoposition_result_language));
        request.put("details", "true");
        request.put("metric", "true");
        dailyForecastPresenter.getData(locationKey, request);

    }

    @Override
    public void showSearchResult(DailyForecastResult dailyForecastResult) {
        swipeRefreshLayout.setRefreshing(false);
        this.dailyForecastResult = dailyForecastResult;
        saveData();
        Toast.makeText(getActivity(), dailyForecastResult.getDailyForecasts().get(0).getTemperature().getMinimum().getValue().toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String error) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {}

    public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

        //All methods in this adapter are required for a bare minimum recyclerview adapter
        private int listItemLayout;
        // Constructor of the class
        ItemArrayAdapter(int layoutId) {
            listItemLayout = layoutId;
        }

        // get the size of the list
        @Override
        public int getItemCount() {
            return names.length;
        }


        // specify the row layout file and click for each row
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
            return new ViewHolder(view);
        }

        // load data in each row element
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
            TextView item = holder.item;
            item.setText(names[listPosition]);
        }

        // Static inner class to initialize the views of rows
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView item;
            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                item = (TextView) itemView.findViewById(R.id.row_item);
            }
            @Override
            public void onClick(View view) {
            }
        }
    }
}