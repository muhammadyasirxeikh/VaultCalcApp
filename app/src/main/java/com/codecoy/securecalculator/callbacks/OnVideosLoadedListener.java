package com.codecoy.securecalculator.callbacks;

import com.codecoy.securecalculator.model.AllVideosModel;
import com.codecoy.securecalculator.model.Model;

import java.util.ArrayList;

public interface OnVideosLoadedListener {
    void onVideosLoaded(ArrayList<Model> arrayList);
}
