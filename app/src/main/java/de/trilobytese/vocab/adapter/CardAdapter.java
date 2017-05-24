package de.trilobytese.vocab.adapter;

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import de.trilobytese.vocab.R;
import de.trilobytese.vocab.model.Flashcard;

import java.util.List;

public class CardAdapter extends ArrayAdapter<Flashcard> {

    public static final int TYPE_SOURCE = 0;
    public static final int TYPE_TARGET = 1;

    private LayoutInflater mLayoutInflater;

    private int mCurrentRowSelectedPosition = -1;
    private int mCurrentCellSelectedPosition = -1;
    private int mCurrentCellSelectedType = -1;

    private Context mContext;
    private Callbacks mCallbacks;

    private GestureDetector mEditTextGestureDetector;

    public interface Callbacks {
        void onItemSourceClicked(View view, Flashcard flashcard, int position);
        void onItemTargetClicked(View view, Flashcard flashcard, int position);
        void onItemMenuClicked(View view, Flashcard flashcard, int position);
        void onItemDoubleClicked();
    }

    public CardAdapter(Context context, List<Flashcard> items, Callbacks callbacks) {
        super(context, R.layout.item_flashcard, items);
        mContext = context;
        mCallbacks = callbacks;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEditTextGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                mCallbacks.onItemDoubleClicked();
                return true;
            }
        });
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_flashcard, parent, false);
            holder = new ViewHolder();
            holder.layoutItem = convertView.findViewById(R.id.layout_item);
            holder.txtPosition = (TextView)convertView.findViewById(R.id.txtPosition);
            holder.txtSource = (TextView)convertView.findViewById(R.id.txtSource);
            holder.txtTarget = (TextView)convertView.findViewById(R.id.txtTarget);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Flashcard flashcard = getItem(position);
        holder.txtPosition.setText(String.valueOf(position + 1));

        holder.txtPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onItemMenuClicked(view, flashcard, position);
            }
        });

        holder.txtSource.setText(flashcard.getSource());
        holder.txtSource.setActivated(mCurrentCellSelectedType == TYPE_SOURCE && mCurrentCellSelectedPosition == position);
        holder.txtSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onItemSourceClicked(view, flashcard, position);
            }
        });
        holder.txtSource.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return mEditTextGestureDetector.onTouchEvent(event);
            }
        });

        holder.txtTarget.setText(flashcard.getTarget());
        holder.txtTarget.setActivated(mCurrentCellSelectedType == TYPE_TARGET && mCurrentCellSelectedPosition == position);
        holder.txtTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbacks.onItemTargetClicked(view, flashcard, position);
            }
        });
        holder.txtTarget.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return mEditTextGestureDetector.onTouchEvent(event);
            }
        });

        holder.txtPosition.setActivated(mCurrentRowSelectedPosition == position);

        return convertView;
    }

    public void addItem(Flashcard flashcard) {
        insert(flashcard, getCount());
        notifyDataSetChanged();
    }

    public void removeItem(Flashcard flashcard) {
        remove(flashcard);
        notifyDataSetChanged();
    }

    public void setRowSelected(int position) {
        mCurrentRowSelectedPosition = position;
        notifyDataSetChanged();
    }

    public void clearRowSelection() {
        setRowSelected(-1);
    }

    public void setCellSelected(int position, int inputType) {
        mCurrentCellSelectedType = inputType;
        mCurrentCellSelectedPosition = position;
        notifyDataSetChanged();
    }

    public void clearCellSelection() {
        setCellSelected(-1, -1);
    }

    static class ViewHolder {
        View layoutItem;
        TextView txtPosition;
        TextView txtSource;
        TextView txtTarget;
    }
}
