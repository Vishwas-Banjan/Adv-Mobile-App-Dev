package com.uncc.inclass02.ui.location;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;

public abstract class PolyLineParser extends AsyncTask<String, Integer, List<List<HashMap>>> {
    @Override
    protected List<List<HashMap>> doInBackground(String... strings) {
        return null;
    }
}
