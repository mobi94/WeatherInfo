package com.breezee.sergeystasyuk.weatherinfo.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.breezee.sergeystasyuk.weatherinfo.R;
import com.breezee.sergeystasyuk.weatherinfo.pojos.geoposition.GeopositionSearchResult;
import com.breezee.sergeystasyuk.weatherinfo.presenters.GeopositionSearchPresenter;
import com.breezee.sergeystasyuk.weatherinfo.views.GeopositionSearchView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.R.id.list;

/**
 * Created by User on 09.02.2017.
 */

public class CurrentConditionFragment extends Fragment implements GeopositionSearchView{

    String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей", "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей" };

    RecyclerView recyclerView;
    GeopositionSearchPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_condition, container, false);

        ItemArrayAdapter itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item);
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemArrayAdapter);

        return view;
    }

    public void onLocationDefined(Location location){
        presenter = new GeopositionSearchPresenter(this);
        Map<String, String> request = new HashMap<>();
        request.put("apikey", getString(R.string.accuweather_api_key));
        request.put("q", String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude()));
        request.put("language", getString(R.string.geoposition_result_language));
        presenter.getData(request);
    }

    @Override
    public void showGeopositionSearchResult(GeopositionSearchResult geopositionSearchResult) {
        Toast.makeText(getActivity(), geopositionSearchResult.getLocalizedName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {

    }

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