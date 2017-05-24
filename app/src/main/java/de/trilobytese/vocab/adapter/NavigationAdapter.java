package de.trilobytese.vocab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.data.NavigationDataProvider;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private static final int ITEM_VIEW_TYPE_NAVIGATION = NavigationDataProvider.ITEM_VIEW_TYPE_NAVIGATION;
    private static final int ITEM_VIEW_TYPE_NAVIGATION_DIVIDER = NavigationDataProvider.ITEM_VIEW_TYPE_NAVIGATION_DIVIDER;

    private NavigationDataProvider mProvider;
    private int mItemCheckedPosition = 0;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mIcon;
        public final TextView mTitle;

        public ViewHolder(View v) {
            super(v);
            mIcon = (ImageView)v.findViewById(R.id.icon);
            mTitle = (TextView)v.findViewById(R.id.title);
        }
    }

    public NavigationAdapter(NavigationDataProvider provider, OnItemClickListener onItemClickListener) {
        mProvider = provider;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v;
        switch (viewType) {
            case ITEM_VIEW_TYPE_NAVIGATION:
                v = inflater.inflate(R.layout.item_navigation, parent, false);
                break;
            case ITEM_VIEW_TYPE_NAVIGATION_DIVIDER:
                v = inflater.inflate(R.layout.item_navigation_divider, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case ITEM_VIEW_TYPE_NAVIGATION:
                onBindItemDrawerViewHolder(holder, position);
                break;
        }
    }

    private void onBindItemDrawerViewHolder(ViewHolder holder, final int position) {
        NavigationDataProvider.Data data = mProvider.getItem(position);
        holder.mIcon.setImageResource(data.getResId());
        holder.mTitle.setText(data.getName());

        if (mItemCheckedPosition == position) {
            holder.itemView.setActivated(true);
        } else {
            holder.itemView.setActivated(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    public NavigationDataProvider.Data getItem(int position) {
        return mProvider.getItem(position);
    }

    public void setItemChecked(int position) {
        mItemCheckedPosition = position;
        notifyDataSetChanged();
    }
}
