package com.shiluying.wordbook;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shiluying.wordbook.ListFragment.OnListFragmentInteractionListener;
import com.shiluying.wordbook.Word.WordContent.WordItem;

import java.util.List;

/*
 * TODO: Replace the implementation with code for your data type.
 */
public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder> {

    private final List<WordItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ListRecyclerViewAdapter(List<WordItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).word);
        holder.mMeaningView.setText(mValues.get(position).meaning);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mMeaningView;
        public WordItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
            mMeaningView = (TextView) view.findViewById(R.id.meaning);
        }

        @Override
        public String toString() {
            return super.toString() +" '" + mContentView.getText() + "'";
        }
    }
}
