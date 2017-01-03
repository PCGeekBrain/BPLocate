package com.pcgeekbrain.bplocate;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pcgeekbrain.bplocate.interfaces.AsyncResponse;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private static String filename = "library.cache";
    private ArrayList<Branch> branches = new ArrayList<>();
    private ArrayList<Branch> searchResults = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>(50);
    private SearchView searchView;
    private ImageView refresh;
    private GoogleMap mGoogleMap;
    private int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadCache();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Search and refresh
        searchView = (SearchView) findViewById(R.id.search);
        refresh = (ImageView) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        searchView.setOnQueryTextListener(searchListener);
    }

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return search(query);
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    private boolean search(String query) {
        if (query.length() > 0) {
            SearchTask searchTask = new SearchTask(this, branches);
            searchTask.execute(query);
        }
        return true;
    }

    private void updateData() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            UpdateData download = new UpdateData(this, this);
            download.execute("http://misc.brooklynpubliclibrary.org/BigApps/BPL_branch_001.xml");
        } else {
            Toast.makeText(getApplicationContext(), "Cannot Update Data\nError: 141 - No Active Network", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Processes the result from the network
     * @param output
     */
    @Override
    public void processFinish(ArrayList<Branch> output) {
        Log.d(TAG, "processFinish: output.size -> " + output.size());
        branches.clear();
        branches.addAll(output);
        //TODO Update MAP
        setUpMap();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput(filename, Context.MODE_PRIVATE));
            oos.writeObject(branches);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void searchFinish(ArrayList<Branch> result) {
        Log.d(TAG, "searchFinish: updating search");
        searchResults.clear();
        searchResults.addAll(result);
        //TODO Show search Result
    }

    @Override
    public void onClick(View view) {
        updateData();
        //TODO make thing spin
        Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_LONG).show();
    }

    private void loadCache() {
        File file = new File(getFilesDir(), filename);
        if (file.exists()) {
            Log.d(TAG, "onCreate: file exists");
            try {
                ObjectInputStream ois = new ObjectInputStream(openFileInput(filename));
                branches = (ArrayList<Branch>) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onCreate: file does not exist");
            updateData();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng ny = new LatLng(40.7128, -74.0059);
        Geocoder gc = new Geocoder(this);
        List<Address> results;
        try {
            results = gc.getFromLocationName("NYC", 1);
            ny = new LatLng(results.get(0).getLatitude(), results.get(0).getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_LOCATION);

            //return; //Remove this?
        } else {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny, 10));
            this.setUpMap();
        }
        
        //TODO add all the branches
        mGoogleMap.addMarker(new MarkerOptions()
                .title("NYC")
                .snippet("The Big Apple")
                .position(ny));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case MY_PERMISSION_REQUEST_LOCATION:
                setUpMap();
                break;
        }
    }
    
    private void setUpMap(){
        for (Marker marker : markers){
            marker.remove();
        }
        markers.clear();
        LatLng ny = new LatLng(40.7128, -74.0059);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ny, 10));
        Log.d(TAG, "setUpMap: branches size -> "+branches.size());
        for (Branch branch : branches){
            Log.d(TAG, "setUpMap: adding marker to branch ("+branch.getName()+") at ("+branch.getLat()+", "+branch.getLng()+")");
            Marker temp = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(branch.getLat(), branch.getLng()))
                    .title(branch.getName())
                    .snippet(branch.getHours(day)));
            markers.add(temp);
        }
    }
}
