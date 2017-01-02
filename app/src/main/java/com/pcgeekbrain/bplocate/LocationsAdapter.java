package com.pcgeekbrain.bplocate;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mendel on 12/12/2016.
 * Adapter for the list
 */

public class LocationsAdapter extends RecyclerView.Adapter<LocationsViewHolder>{
    ArrayList<Branch> branches;

    LocationsAdapter(ArrayList<Branch> branches){
        this.branches = branches;
    }

    @Override
    public LocationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View locationListItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item, parent, false);
        LocationsViewHolder viewHolder = new LocationsViewHolder(locationListItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationsViewHolder holder, int position) {
        holder.name.setText(branches.get(position).name);
        holder.address.setText(branches.get(position).address);
        holder.number.setText(branches.get(position).number);
        holder.current_status.setText(branches.get(position).getClosesIn());
        holder.closes_in.setText(branches.get(position).getHours());
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }
}
