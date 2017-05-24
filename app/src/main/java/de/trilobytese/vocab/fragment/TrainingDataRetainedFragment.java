package de.trilobytese.vocab.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.trilobytese.vocab.model.Training;

public class TrainingDataRetainedFragment extends Fragment {

    private Training mData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setData(Training data) {
        mData = data;
    }

    public Training getData() {
        return mData;
    }
}
