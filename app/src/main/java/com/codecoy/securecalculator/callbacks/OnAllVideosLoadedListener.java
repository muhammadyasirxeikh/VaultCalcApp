package com.codecoy.securecalculator.callbacks;

import com.codecoy.securecalculator.model.AllVideosModel;
import com.codecoy.securecalculator.model.Model;

import java.util.ArrayList;

public interface OnAllVideosLoadedListener {
    void onAllVideosLoaded(ArrayList<Model> arrayList);
}
