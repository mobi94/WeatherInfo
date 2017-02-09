package com.breezee.sergeystasyuk.weatherinfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.R.id.list;

/**
 * Created by User on 09.02.2017.
 */

public class CurrentConditionFragment extends Fragment {

    String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей", "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
            "Костя", "Игорь", "Анна", "Денис", "Андрей" };

    RecyclerView recyclerView;
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