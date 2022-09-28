package com.codecoy.securecalculator.callbacks;

import com.codecoy.securecalculator.model.AllImagesModel;
import com.codecoy.securecalculator.model.Model;

import java.util.ArrayList;

public interface OnImagesLoadedListener {
    void onImagesLoaded(ArrayList<Model> arrayList);
}
