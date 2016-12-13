package com.pcgeekbrain.bplocate;

/**
 * Created by Mendel on 12/12/2016.
 */

public class Branch {
    String name, address, number, current_status, closes_in;

    public Branch(String name, String address, String number, String current_status, String closes_in){
        this.name = name;
        this.address = address;
        this.number = number;
        this.current_status = current_status;
        this.closes_in = closes_in;
    }

    public String getClosesIn(){
        return closes_in;
    }
}
