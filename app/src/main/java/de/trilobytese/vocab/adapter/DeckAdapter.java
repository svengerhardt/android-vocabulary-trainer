package de.trilobytese.vocab.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.model.Deck;
import de.trilobytese.vocab.util.TimeUtils;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.ViewHolder> {

    private List<Deck> mItems;

    private OnItemClickListener mOnItemClickListener;
    private OnMenuClickListener mOnMenuClickListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, Deck deck, int position);
    }

    public interface OnMenuClickListener {
        public void onMenuClick(View view, Deck deck, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtLastUpdate;
        private ImageView iconStar;
        private View menu;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
            txtLastUpdate = (TextView)itemView.findViewById(R.id.txtLastUpdate);
            iconStar = (ImageView)itemView.findViewById(R.id.iconStar);
            menu = itemView.findViewById(R.id.menu);
        }
    }

    public DeckAdapter(List<Deck> items, OnItemClickListener onItemClickListener, OnMenuClickListener onMenuClickListener) {
        mItems = items;
        mOnItemClickListener = onItemClickListener;
        mOnMenuClickListener = onMenuClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        View v = li.inflate(R.layout.card_view_deck, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Deck deck = mItems.get(position);
        holder.txtName.setText(deck.name);
        holder.txtLastUpdate.setText(TimeUtils.getDateTime(deck.getTimestampModified()));

        if (deck.isStarred()) {
            holder.iconStar.setVisibility(View.VISIBLE);
        } else {
            holder.iconStar.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, deck, position);
            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnMenuClickListener.onMenuClick(view, deck, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public boolean exists(Deck deck) {
        return mItems.contains(deck);
    }

    public Deck getItem(int position) {
        return mItems.get(position);
    }

    public Deck getItem(Deck deck) {
        int position = mItems.indexOf(deck);
        if (position != -1) {
            return mItems.get(position);
        } else {
            return null;
        }
    }

    public void addItem(Deck deck) {
        mItems.add(0, deck);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void addItem(Deck deck, int position) {
        mItems.add(position, deck);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void update(Deck deck) {
        int position = mItems.indexOf(deck);
        if (position != -1) {
            mItems.set(position, deck);
            notifyItemChanged(position);
        }
    }

    public void update(Deck deck, int position) {
        mItems.set(position, deck);
        notifyItemChanged(position);
    }

    public void removeItem(Deck deck) {
        int position = mItems.indexOf(deck);
        if (position != -1) {
            mItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }

    public void removeItem(int position) {
        if (position != -1) {
            mItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }
    }
}
