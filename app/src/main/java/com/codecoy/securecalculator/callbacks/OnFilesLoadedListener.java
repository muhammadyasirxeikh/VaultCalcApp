package com.codecoy.securecalculator.callbacks;

import com.codecoy.securecalculator.model.AllFilesModel;

import java.util.ArrayList;

public interface OnFilesLoadedListener {
    void onFilesLoaded(ArrayList<AllFilesModel> arrayList);
}
