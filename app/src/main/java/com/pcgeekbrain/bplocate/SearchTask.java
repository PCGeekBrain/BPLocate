package com.pcgeekbrain.bplocate;

import android.os.AsyncTask;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import java.util.ArrayList;

/**
 * Created by Mendel on 1/2/2017.
 * moving search to background
 */

public class SearchTask extends AsyncTask<String, Void, ArrayList<Branch>> {
    private AsyncResponse response;
    private ArrayList<Branch> data;

    public SearchTask(AsyncResponse response, ArrayList<Branch> data){
        this.response = response;
        this.data = data;
    }

    @Override
    protected ArrayList<Branch> doInBackground(String... queries) {
        ArrayList<Branch> result = new ArrayList<>(15);
        String query = queries[0];
        for (Branch branch : data){
            if (branch.name.toLowerCase().contains(query.toLowerCase())){
                result.add(branch);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Branch> result) {
        response.searchFinish(result);
    }
}
