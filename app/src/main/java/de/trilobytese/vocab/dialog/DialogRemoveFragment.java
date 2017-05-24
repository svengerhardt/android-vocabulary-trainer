package de.trilobytese.vocab.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import com.afollestad.materialdialogs.MaterialDialog;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.dialog.listener.OnDialogNegativeListener;
import de.trilobytese.vocab.dialog.listener.OnDialogPositiveListener;

public class DialogRemoveFragment extends DialogBaseFragment {

    private String mTitle;
    private String mContent;
    private long mId;

    public static DialogRemoveFragment newInstance(int requestCode, String title, String content, long id) {
        DialogRemoveFragment fragment = new DialogRemoveFragment();
        Bundle args = new Bundle();
        args.putInt("request_code", requestCode);
        args.putString("title", title);
        args.putString("content", content);
        args.putLong("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestCode = getArguments().getInt("requestCode");
        mTitle = getArguments().getString("title");
        mContent = getArguments().getString("content");
        mId = getArguments().getLong("id");
        setCancelable(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        if (mTitle != null) {
            builder.title(mTitle);
        }

        if (mContent != null) {
            builder.content(Html.fromHtml(mContent));
        }

        builder.positiveText(getString(R.string.dialog_remove_deck_positive));
        builder.negativeText(getString(R.string.dialog_remove_deck_negative));

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                for (OnDialogPositiveListener listener : getPositiveButtonListeners()) {
                    listener.onPositiveButtonClicked(mRequestCode, mId);
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
