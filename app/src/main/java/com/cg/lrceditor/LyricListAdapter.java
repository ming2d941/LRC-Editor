package com.cg.lrceditor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LyricListAdapter extends RecyclerView.Adapter<LyricListAdapter.LyricListItem> {
    public final List<LyricItem> lyricData;

    public boolean isDarkTheme = false;

    private LayoutInflater inflater;
    private SparseBooleanArray flashItems;
    private ItemClickListener clickListener;

    LyricListAdapter(Context context, List<LyricItem> lyricData) {
        inflater = LayoutInflater.from(context);
        this.lyricData = lyricData;
        flashItems = new SparseBooleanArray();
    }

    @Override
    public void onBindViewHolder(@NonNull LyricListItem holder, int position) {
        String lyric = lyricData.get(position).getLyric();
        holder.itemTextview.setText(lyric);

        if (lyricData.get(position).getTimestamp() != null) {
            holder.itemTimeControls.setVisibility(View.VISIBLE);
            holder.itemTimeview.setText(lyricData.get(position).getTimestamp().toString());
        } else {
            holder.itemTimeControls.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setActivated(lyricData.get(position).isSelected);
        holder.itemView.setHovered(flashItems.get(position, false));
        if (holder.itemView.isHovered())
            holder.itemView.setActivated(false);

        applyClickEvents(holder);
    }

    private void applyClickEvents(final LyricListItem holder) {
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onLyricItemClicked(holder.getAdapterPosition());
            }
        });

        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onLyricItemSelected(holder.getAdapterPosition());
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    @NonNull
    @Override
    public LyricListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflater.inflate(R.layout.row_lyriclist_item, parent, false);
        return new LyricListItem(mItemView, this);
    }

    @Override
    public int getItemCount() {
        return lyricData.size();
    }

    public void startFlash(int pos) {
        flashItems.put(pos, true);
        notifyItemChanged(pos);
    }

    public SparseBooleanArray getFlashingItems() {
        return this.flashItems;
    }

    public void stopFlash(int pos) {
        flashItems.delete(pos);
        notifyItemChanged(pos);
    }

    public void toggleSelection(int pos) {
        lyricData.get(pos).isSelected = !lyricData.get(pos).isSelected;
        notifyItemChanged(pos);
    }

    public void selectAll() {
        for (int i = 0; i < getItemCount(); i++)
            lyricData.get(i).isSelected = true;
        notifyDataSetChanged();
    }

    public void clearSelections() {
        for (int i = 0, len = getItemCount(); i < len; i++) {
            LyricItem item = lyricData.get(i);
            if (item.isSelected) {
                item.isSelected = false;
                notifyItemChanged(i);
            }
        }
    }

    public List<Integer> getSelectedItemIndices() {
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            if (lyricData.get(i).isSelected)
                items.add(i);
        }
        return items;
    }

    public int getSelectionCount() {
        int noOfSelectedItems = 0;
        for (LyricItem item : lyricData) {
            if (item.isSelected)
                noOfSelectedItems++;
        }

        return noOfSelectedItems;
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onAddButtonClick(int position);

        void onPlayButtonClick(int position);

        void onIncreaseTimeClick(int position);

        void onDecreaseTimeClick(int position);

        void onLongPressIncrTime(int position);

        void onLongPressDecrTime(int position);

        void onLyricItemSelected(int position);

        void onLyricItemClicked(int position);
    }

    class LyricListItem extends RecyclerView.ViewHolder implements View.OnLongClickListener,
            View.OnClickListener {
        final LyricListAdapter adapter;
        private final LinearLayout linearLayout;
        private final TextView itemTextview;
        private final LinearLayout itemTimeControls;
        private final TextView itemTimeview;
        private final ImageButton itemPlay;
        private final ImageButton itemAdd;

        LyricListItem(final View itemView, LyricListAdapter adapter) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.lyricitem_parent_linearlayout);

            itemTextview = itemView.findViewById(R.id.item_lyric);
            itemAdd = itemView.findViewById(R.id.item_add);
            itemTimeControls = itemView.findViewById(R.id.item_time_controls);
            itemTimeview = itemView.findViewById(R.id.item_time);
            itemPlay = itemView.findViewById(R.id.item_play);
            this.adapter = adapter;

            if (isDarkTheme) {
                Context ctx = inflater.getContext();
                ImageButton time_increase = itemTimeControls.findViewById(R.id.increase_time_button);
                time_increase.setImageDrawable(ctx.getDrawable(R.drawable.ic_add_light));

                ImageButton time_decrease = itemTimeControls.findViewById(R.id.decrease_time_button);
                time_decrease.setImageDrawable(ctx.getDrawable(R.drawable.ic_minus_light));

                itemPlay.setImageDrawable(ctx.getDrawable(R.drawable.ic_play_light));
                itemAdd.setImageDrawable(ctx.getDrawable(R.drawable.ic_add_light));
            }

            itemAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onAddButtonClick(getAdapterPosition());
                }
            });

            itemPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onPlayButtonClick(getAdapterPosition());
                }
            });

            ImageButton incrTime = itemView.findViewById(R.id.increase_time_button);
            ImageButton decrTime = itemView.findViewById(R.id.decrease_time_button);

            incrTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onIncreaseTimeClick(getAdapterPosition());
                }
            });

            incrTime.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onLongPressIncrTime(getAdapterPosition());
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return false;
                }
            });

            decrTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onDecreaseTimeClick(getAdapterPosition());
                }
            });

            decrTime.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onLongPressDecrTime(getAdapterPosition());
                    v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return false;
                }
            });

        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onLyricItemSelected(getAdapterPosition());
            v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return false;
        }

        @Override
        public void onClick(View v) {
            clickListener.onLyricItemClicked(getAdapterPosition());
        }
    }
}
