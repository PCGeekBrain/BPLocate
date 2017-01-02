package com.pcgeekbrain.bplocate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncResponse{
    public final String TAG = "MainActivity";
    private RecyclerView locations_recycler_view;
    private LocationsAdapter locations_adapter;
    private RecyclerView.LayoutManager locations_layout_manager;
    private ArrayList<Branch> branches = new ArrayList<>();
    private ArrayList<Branch> searchResults = new ArrayList<>();
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File cache = this.getCacheDir();
        Log.d(TAG, "onCreate: cache ->"+cache);

        locations_recycler_view = (RecyclerView) findViewById(R.id.locations_list);
        //locations_recycler_view.setHasFixedSize(true);  //Improves Performance
        searchView = (SearchView)findViewById(R.id.search);

        //Layout Manager Setup
        locations_layout_manager = new LinearLayoutManager(this);
        locations_recycler_view.setLayoutManager(locations_layout_manager);

        //Add an adapter
        locations_adapter = new LocationsAdapter(branches);
        locations_recycler_view.setAdapter(locations_adapter);

        searchView.setOnQueryTextListener(searchListener);

        updateData();
    }

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return search(newText);
        }
    };

    private boolean search(String query){
        Log.d(TAG, "search: started query -> "+query);
        searchResults.clear();
        Branch currentBranch;
        Log.d(TAG, "search: started query.length() -> "+query.length());
        if (query.length() > 0){
            //feels faster then index pulling. NEEDS RESEARCH
            for (Branch branch : branches){
                if (branch.name.toLowerCase().contains(query.toLowerCase())){
                    searchResults.add(branch);
                }
            }
            locations_adapter.swap(searchResults);
        } else {
            locations_adapter.swap(branches);
        }
        Log.d(TAG, "search: branches -> " + branches);
        Log.d(TAG, "search: result -> " + searchResults);
        return true;
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
        locations_adapter.swap(output);
    }
}
