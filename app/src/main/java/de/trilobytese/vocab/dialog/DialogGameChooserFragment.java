package de.trilobytese.vocab.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.afollestad.materialdialogs.MaterialDialog;
import de.trilobytese.vocab.MainApplication;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.dialog.listener.OnDialogGameChooseListener;

public class DialogGameChooserFragment extends DialogBaseFragment {

    public static final int GAME_TYPE_1 = R.drawable.ic_game_1;
    //public static final int GAME_TYPE_2 = R.drawable.ic_game_2;
    public static final int GAME_TYPE_3 = R.drawable.ic_game_3;
    public static final int GAME_TYPE_4 = R.drawable.ic_game_4;

    private Integer[] mResourceIds = {
            GAME_TYPE_1, /*GAME_TYPE_2,*/ GAME_TYPE_3, GAME_TYPE_4
    };

    private String[] content = {
            MainApplication.getInstance().getString(R.string.game_type_1),
            //MainApplication.getInstance().getString(R.string.game_type_2),
            MainApplication.getInstance().getString(R.string.game_type_3),
            MainApplication.getInstance().getString(R.string.game_type_4)
    };

    private long mDeckId;

    // -----------------------------------------------------------------------
    //
    // NewInstance
    //
    // -----------------------------------------------------------------------

    public static DialogGameChooserFragment newInstance(int requestCode, long deckId) {
        DialogGameChooserFragment fragment = new DialogGameChooserFragment();
        Bundle args = new Bundle();
        args.putInt("requestCode", requestCode);
        args.putLong("deck_id", deckId);
        fragment.setArguments(args);
        return fragment;
    }

    // -----------------------------------------------------------------------
    //
    // OnCreate
    //
    // -----------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestCode = getArguments().getInt("requestCode");
        mDeckId = getArguments().getLong("deck_id");
        setCancelable(true);
    }

    // -----------------------------------------------------------------------
    //
    // OnCreateDialog
    //
    // -----------------------------------------------------------------------

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.customView(R.layout.layout_game_chooser, false);
        MaterialDialog dialog = builder.build();

        GridView gridView = (GridView) dialog.getCustomView().findViewById(R.id.grid_view);
        gridView.setAdapter(new GridAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (OnDialogGameChooseListener listener : getGameChooseListeners()) {
                    listener.onGameClicked(mRequestCode, mDeckId, mResourceIds[position]);
                }
                dismiss();
            }
        });

        return dialog;
    }

    // -----------------------------------------------------------------------
    //
    // Adapter
    //
    // -----------------------------------------------------------------------

    public class GridAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public GridAdapter() {
            mInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return mResourceIds.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_game_chooser, viewGroup, false);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.text);
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.name.setText(content[position]);
            holder.image.setImageResource(mResourceIds[position]);

            return convertView;
        }

        class ViewHolder {
            TextView name;
            ImageView image;
        }
    }
}