package com.pcgeekbrain.bplocate;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mendel on 12/12/2016.
 */

public class LocationsViewHolder extends RecyclerView.ViewHolder{
    TextView name, address, number, closes_in, hours;

    public LocationsViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.loc_name);
        address = (TextView) itemView.findViewById(R.id.loc_address);
        number = (TextView) itemView.findViewById(R.id.loc_number);
        closes_in = (TextView) itemView.findViewById(R.id.loc_closes_in);
        hours = (TextView) itemView.findViewById(R.id.loc_hours);
    }
}
