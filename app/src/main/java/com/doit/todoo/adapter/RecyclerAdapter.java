package com.doit.todoo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.doit.todoo.R;
import com.doit.todoo.model.Model;
import com.doit.todoo.utils.TextDrawable;

import java.util.List;

/**
 * Created by himanshu on 19/4/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemHolder> {

    public interface OnItemClickListener {
        void onDeleteItem(int delPos);

        void onItemClick(Model model);

        void onLongClick(int pos);
    }

    private final OnItemClickListener listener;
    Context mContext;
    List<Model> mList;


    public RecyclerAdapter(Context context, List<Model> list, OnItemClickListener listener) {
        mContext = context;
        mList = list;
        this.listener = listener;

    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        String string = mList.get(position).getTitle();
        String description = mList.get(position).getContent();

//        Log.e("Testing : " , "String : " + string + "   Description " +description);
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        int color = 0;
        try {
            color = Color.parseColor(mList.get(position).getColor());
        } catch (IllegalArgumentException e) {
            color = Color.parseColor("#000000");
        }
        TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRound(string.substring(0, 1), color);
        if (mList.get(position).getIsPasswordProtected() == 1)
            holder.lockImage.setVisibility(View.VISIBLE);
        holder.backView.setBackgroundColor(color);
        holder.imageView.setImageDrawable(myDrawable);
        holder.textView.setText(string);
        holder.descView.setText(description);
        applyLongClick(holder, position);
    }

    private void applyLongClick(ItemHolder holder, final int position) {
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onLongClick(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView, descView;
        private View backView;
        private ImageView imageView, lockImage;
        private SwipeLayout swipeLayout;
        private Button deleteBtn;
        private LinearLayout frontLayout;

        public ItemHolder(View itemView) {
            super(itemView);
            frontLayout = (LinearLayout) itemView.findViewById(R.id.frontLayout);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            backView = itemView.findViewById(R.id.backView);
            lockImage = (ImageView) itemView.findViewById(R.id.password);
            deleteBtn = (Button) itemView.findViewById(R.id.delete);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            descView = (TextView) itemView.findViewById(R.id.description);
            deleteBtn.setOnClickListener(this);
            frontLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete:
                    listener.onDeleteItem(getAdapterPosition());
                    break;
                case R.id.frontLayout:
                    listener.onItemClick(mList.get(getAdapterPosition()));
                    break;
            }

        }
    }
}

