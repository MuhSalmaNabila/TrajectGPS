package com.nabil.radeon.trajectgps.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nabil.radeon.trajectgps.R;
import com.nabil.radeon.trajectgps.model.AndroidVersion;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<AndroidVersion> person;


    public DataAdapter(ArrayList<AndroidVersion> person) {
        this.person = person;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder viewHolder, int i) {
        viewHolder.p_id.setText(person.get(i).getId());
        viewHolder.p_name.setText(person.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return person.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.p_id) TextView p_id;
        @BindView(R.id.p_name) TextView p_name;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}