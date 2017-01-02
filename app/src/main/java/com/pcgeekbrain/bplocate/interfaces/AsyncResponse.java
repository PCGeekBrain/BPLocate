package com.pcgeekbrain.bplocate.interfaces;

import com.pcgeekbrain.bplocate.Branch;

import java.util.ArrayList;

/**
 * Created by Mendel on 1/2/2017.
 * Response Interface
 */

public interface AsyncResponse {
    void processFinish(ArrayList<Branch> output);
}
