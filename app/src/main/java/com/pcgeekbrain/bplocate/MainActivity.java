package com.pcgeekbrain.bplocate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse{
    private RecyclerView locations_recycler_view;
    private RecyclerView.Adapter locations_adapter;
    private RecyclerView.LayoutManager locations_layout_manager;
    private ArrayList<Branch> branches = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: see if updated in last 24hrs. if no refresh.

        locations_recycler_view = (RecyclerView) findViewById(R.id.locations_list);
        locations_recycler_view.setHasFixedSize(true);  //Improves Performance

        //Layout Manager Setup
        locations_layout_manager = new LinearLayoutManager(this);
        locations_recycler_view.setLayoutManager(locations_layout_manager);

        //Add an adapter
        locations_adapter = new LocationsAdapter(branches);
        locations_recycler_view.setAdapter(locations_adapter);
    }


    private void updateData(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null){
            UpdateData download = new UpdateData(this);
            download.execute("http://misc.brooklynpubliclibrary.org/BigApps/BPL_branch_001.xml");
        } else {
            Toast.makeText(this, "Cannot Update Data\nError: 141 - No Active Network", Toast.LENGTH_LONG);
        }
    }

    /**
     * Processes the result from the network
     * @param output
     */
    @Override
    public void processFinish(ArrayList<Branch> output) {
        branches.clear();
        branches.addAll(output);
        locations_adapter.notifyDataSetChanged();
    }
}
