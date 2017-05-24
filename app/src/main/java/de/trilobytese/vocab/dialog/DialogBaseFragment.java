package de.trilobytese.vocab.dialog;

import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import de.trilobytese.vocab.dialog.listener.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DialogBaseFragment extends DialogFragment {

    protected int mRequestCode;

    public void showAllowingStateLoss(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        for (OnDialogCancelListener listener : getCancelListeners()) {
            listener.onCancelled(mRequestCode);
        }
    }

    protected List<OnDialogCancelListener> getCancelListeners() {
        return getDialogListeners(OnDialogCancelListener.class);
    }

    protected List<OnDialogPositiveListener> getPositiveButtonListeners() {
        return getDialogListeners(OnDialogPositiveListener.class);
    }

    protected List<OnDialogNegativeListener> getNegativeButtonListeners() {
        return getDialogListeners(OnDialogNegativeListener.class);
    }

    protected List<OnDialogTextInputListener> getTextInputButtonListeners() {
        return getDialogListeners(OnDialogTextInputListener.class);
    }

    protected List<OnDialogGameChooseListener> getGameChooseListeners() {
        return getDialogListeners(OnDialogGameChooseListener.class);
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
        final Fragment targetFragment = getTargetFragment();
        List<T> listeners = new ArrayList<>(2);
        if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
            listeners.add((T)targetFragment);
        }
        if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
            listeners.add((T)getActivity());
        }
        return Collections.unmodifiableList(listeners);
    }
}
