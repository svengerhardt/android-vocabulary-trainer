package de.trilobytese.vocab.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.dialog.listener.OnDialogNegativeListener;
import de.trilobytese.vocab.dialog.listener.OnDialogTextInputListener;

public class DialogTextInputFragment extends DialogBaseFragment {

    private String mTitle;
    private String mText;

    public static DialogTextInputFragment newInstance(int requestCode, String title, String text) {
        DialogTextInputFragment fragment = new DialogTextInputFragment();
        Bundle args = new Bundle();
        args.putInt("requestCode", requestCode);
        args.putString("title", title);
        args.putString("text", text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestCode = getArguments().getInt("requestCode");
        mTitle = getArguments().getString("title");
        mText = getArguments().getString("text");
        setCancelable(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());

        if (mTitle != null) {
            builder.title(mTitle);
        }

        builder.customView(R.layout.layout_text_input, true);
        builder.negativeText(R.string.dialog_general_negative);
        builder.positiveText(R.string.dialog_general_positive);

        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                for (OnDialogTextInputListener listener : getTextInputButtonListeners()) {
                    EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_text);
                    listener.onTextInputButtonClicked(mRequestCode, String.valueOf(editText.getText()));
                }
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                for (OnDialogNegativeListener listener : getNegativeButtonListeners()) {
                    listener.onNegativeButtonClicked(mRequestCode);
                }
            }
        });

        MaterialDialog dialog = builder.build();

        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        EditText editText = (EditText)dialog.getCustomView().findViewById(R.id.edit_text);
        if (mText != null) {
            editText.setText(mText);
            editText.setSelection(editText.getText().length());
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        positiveAction.setEnabled(false);
        return dialog;
    }
}
