package de.trilobytese.vocab.dialog;

import android.app.Dialog;
import android.os.Bundle;
import com.afollestad.materialdialogs.MaterialDialog;
import de.trilobytese.vocab.dialog.listener.OnDialogNegativeListener;
import de.trilobytese.vocab.dialog.listener.OnDialogPositiveListener;

public class DialogInfoFragment extends DialogBaseFragment {

    private String mTitle;
    private String mContent;
    private String mPositiveText;
    private String mNegativeText;

    public static DialogInfoFragment newInstance(int requestCode, boolean cancelable, String title, String content, String positiveText, String negativeText) {
        DialogInfoFragment fragment = new DialogInfoFragment();
        Bundle args = new Bundle();
        args.putInt("requestCode", requestCode);
        args.putString("title", title);
        args.putString("content", content);
        args.putString("positiveText", positiveText);
        args.putString("negativeText", negativeText);
        args.putBoolean("cancelable", cancelable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestCode = getArguments().getInt("requestCode");
        mTitle = getArguments().getString("title");
        mContent = getArguments().getString("content");
        mPositiveText = getArguments().getString("positiveText");
        mNegativeText = getArguments().getString("negativeText");
        setCancelable(getArguments().getBoolean("cancelable"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        if (mTitle != null) {
            builder.title(mTitle);
        }

        if (mContent != null) {
            builder.content(mContent);
        }

        if (mPositiveText != null) {
            builder.positiveText(mPositiveText);
        }

        if (mNegativeText != null) {
            builder.negativeText(mNegativeText);
        }

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                for (OnDialogPositiveListener listener : getPositiveButtonListeners()) {
                    listener.onPositiveButtonClicked(mRequestCode, 0);
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                for (OnDialogNegativeListener listener : getNegativeButtonListeners()) {
                    listener.onNegativeButtonClicked(mRequestCode);
                }
            }
        });

        return builder.build();
    }
}
