package com.pcgeekbrain.bplocate;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Mendel on 12/12/2016.
 */

public class Branch {
    private static final String TAG = "Branch";
    String name, address, number, current_status, closes_in;
    String[] hours;

    public Branch(String name, String address, String number, String[] hours){
        this.name = name;
        this.address = address;
        this.number = number;
        this.current_status = "N/A";
        this.closes_in = "";
        this.hours = hours;
    }

    public String getClosesIn(){
        return closes_in;
    }
    public String getHours(){
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "getHours: day -> " + day);
        switch (day){
            case Calendar.MONDAY:
                return hours[0];
            case Calendar.TUESDAY:
                return hours[1];
            case Calendar.WEDNESDAY:
                return hours[2];
            case Calendar.THURSDAY:
                return hours[3];
            case Calendar.FRIDAY:
                return hours[4];
            case Calendar.SATURDAY:
                return hours[5];
            case Calendar.SUNDAY:
                return hours[6];
            default:
                return "N/A";
        }
    }
}
