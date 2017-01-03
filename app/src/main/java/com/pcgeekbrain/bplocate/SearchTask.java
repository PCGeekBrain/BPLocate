package com.pcgeekbrain.bplocate;

import android.os.AsyncTask;
import android.util.Log;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import java.util.ArrayList;

/**
 * Created by Mendel on 1/2/2017.
 * moving search to background
 */

public class SearchTask extends AsyncTask<String, Void, ArrayList<Branch>> {
    private static final String TAG = "SearchTask";
    private AsyncResponse response;
    private ArrayList<Branch> data;

    public SearchTask(AsyncResponse response, ArrayList<Branch> data){
        this.response = response;
        this.data = data;
        Log.d(TAG, "SearchTask: Created");
    }

    @Override
    protected ArrayList<Branch> doInBackground(String... queries) {
        Log.d(TAG, "doInBackground: Started");
        ArrayList<Branch> result = new ArrayList<>(15);
        String query = queries[0];
        Log.d(TAG, "doInBackground: variables created. data size -> "+data.size());
        for (Branch branch : data){
            if (branch.getName().toLowerCase().contains(query.toLowerCase())){
                result.add(branch);
            }
        }
        Log.d(TAG, "doInBackground: done. returning result");
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Branch> result) {
        response.searchFinish(result);
    }
}
