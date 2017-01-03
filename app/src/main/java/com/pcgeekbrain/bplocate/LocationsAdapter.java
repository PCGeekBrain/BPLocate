package com.pcgeekbrain.bplocate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mendel on 12/12/2016.
 * Adapter for the list
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsViewHolder>{
    ArrayList<Branch> branches = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    int hour = calendar.get(Calendar.HOUR);

    LocationsAdapter(ArrayList<Branch> branches){
        this.branches.addAll(branches);
    }

    @Override
    public LocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View locationListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        LocationsViewHolder viewHolder = new LocationsViewHolder(locationListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationsViewHolder holder, int position) {
        holder.name.setText(branches.get(position).getName());
        holder.address.setText(branches.get(position).getAddress());
        holder.number.setText(branches.get(position).getNumber());
        holder.hours.setText(branches.get(position).getHours(this.day));
        holder.closes_in.setText(branches.get(position).getClosesIn(this.hour, this.day));
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    public void swap(ArrayList<Branch> data){
        this.branches.clear();
        this.branches.addAll(data);
        notifyDataSetChanged();
    }
}
