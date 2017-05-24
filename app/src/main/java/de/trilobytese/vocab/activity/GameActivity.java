package de.trilobytese.vocab.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.fragment.GameFragment;

public class GameActivity extends FragmentActivity implements AndroidFragmentApplication.Callbacks, GameFragment.OnGameProgressListener {

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    public void progressMax(int value) {
        mProgressBar.setMax(value);
    }

    @Override
    public void progress(int value) {
        mProgressBar.setProgress(value);
    }
}