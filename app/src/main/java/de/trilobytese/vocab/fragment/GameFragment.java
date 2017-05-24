package de.trilobytese.vocab.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.dialog.listener.OnDialogConfirmListener;
import de.trilobytese.vocab.game.GameApp;

public class GameFragment extends AndroidFragmentApplication implements GameApp.RequestHandler {

    private long mDeckId;
    private int mGameType;
    private View mGameView;

    private OnGameProgressListener mCallback;

    public interface OnGameProgressListener {
        void progressMax(int value);
        void progress(int value);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            mDeckId = extras.getLong("deck_id");
            mGameType = extras.getInt("game_type");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useCompass = false;
        config.useAccelerometer = false;
        mGameView = initializeForView(new GameApp(this, MainApplication.getInstance(), mDeckId, mGameType), config);
        return mGameView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnGameProgressListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void confirm(final int size, final OnDialogConfirmListener listener) {
        mGameView.post(new Runnable() {
            public void run() {

                final Dialog dialog = new Dialog(getActivity(), R.style.AppTheme_Transparent);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.layout_dialog_next_round);

                TextView txtQuit = (TextView)dialog.findViewById(R.id.txtQuit);
                txtQuit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        listener.onNegativeButtonClicked(0);
                    }
                });

                TextView txtContinue = (TextView) dialog.findViewById(R.id.txtContinue);
                if (size == 0) {
                    txtContinue.setText(getString(R.string.dialog_game_repeat));
                }
                txtContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        listener.onPositiveButtonClicked(0, mDeckId);
                    }
                });

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);

                dialog.show();
            }
        });
    }

    @Override
    public void progressMax(int value) {
        mCallback.progressMax(value);
    }

    @Override
    public void progress(int value) {
        mCallback.progress(value);
    }
}