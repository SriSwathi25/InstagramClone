package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.instagram.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTags;
    private List<String> mTagCount;

    public TagAdapter(Context mContext, List<String> mTags, List<String> mTagCount){
        this.mContext = mContext;
        this.mTags = mTags;
        this.mTagCount = mTagCount;
    }


    @NonNull
    @Override
    public TagAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item,parent, false);
        return new TagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {
        holder.tagName.setText("#"+mTags.get(position));
        holder.tagCount.setText(mTagCount.get(position)+" posts");
    }

    @Override
    public int getItemCount() {
        return mTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tagName;
        public TextView tagCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tagNamee);
            tagCount = itemView.findViewById(R.id.tagCountt);
        }
    }

    public void filter(List<String> filtertags, List<String> filterTagCount){
        this.mTags = filtertags;
        this.mTagCount = filterTagCount;
        notifyDataSetChanged();

    }
}
