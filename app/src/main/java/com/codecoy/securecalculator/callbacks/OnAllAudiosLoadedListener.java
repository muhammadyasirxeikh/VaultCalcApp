package com.codecoy.securecalculator.callbacks;

import com.codecoy.securecalculator.model.AllAudioModel;
import com.codecoy.securecalculator.model.Model;

import java.util.ArrayList;

public interface OnAllAudiosLoadedListener {
    void onAllAudiosLoaded(ArrayList<Model> arrayList);
}
