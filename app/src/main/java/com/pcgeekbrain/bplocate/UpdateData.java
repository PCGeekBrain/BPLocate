package com.pcgeekbrain.bplocate;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mendel on 12/12/2016.
 * Static class for updating data.
 */

public class UpdateData extends AsyncTask<String, Void, ArrayList<Branch>>{
    static final String TAG = "UPDATE DATA";

    @Override
    protected ArrayList<Branch> doInBackground(String... strings) {
        //TODO: update the data form strings[0]
        return downloadData(strings[0]);
    }

    private ArrayList<Branch> downloadData(String input_url) {
        ArrayList<Branch> result = new ArrayList<>();
        InputStream inputStream = null;
        String data = null;
        //download the data
        try {
            try {
                //Create the url and the connection object
                URL url = new URL(input_url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000 /*10 Sec*/);
                connection.setConnectTimeout(15000 /*15 Sec*/);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();   //Connect.
                //Post Connection operations:
                int responseCode = connection.getResponseCode();
                Log.d(TAG, "response code: " + responseCode);
                inputStream = connection.getInputStream();

                //convert the input stream into a string
                data = readInput(new BufferedReader(new InputStreamReader(inputStream)));
                result = parseData(data);
            } finally {
                if (inputStream != null) {inputStream.close();}
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private ArrayList<Branch> parseData(String data) {
        return new ArrayList<>();
    }

    private String readInput(BufferedReader bufferedReader) {
        String status = "" , result = "";
        int lines = 0;

        while (status != null)
            try {
                status = bufferedReader.readLine();
                if (status != null){
                    result += status;
                    lines++;
                }
            } catch (Exception e) {
                status = null;
                Log.e(TAG, "readInput -> Exception:" + e.getMessage() +"\n", e.fillInStackTrace());
            }
        Log.d(TAG, "readInput -> Lines: " + lines);
        return result;
    }
}
