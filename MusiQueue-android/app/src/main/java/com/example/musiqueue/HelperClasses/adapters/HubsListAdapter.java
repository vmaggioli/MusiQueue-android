package com.example.musiqueue.HelperClasses.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musiqueue.HelperClasses.HubsListItem;
import com.example.musiqueue.R;

import java.util.Vector;


public class HubsListAdapter extends RecyclerView.Adapter<HubsListAdapter.ViewHolder> {
    private Vector<HubsListItem> hubs;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HubsListItem hub);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView creatorName;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.hub_name);
            creatorName = (TextView) itemView.findViewById(R.id.hub_creator_name);
        }

        // https://antonioleiva.com/recyclerview-listener/
        public void bind(final HubsListItem item, final OnItemClickListener listener) {
            name.setText(item.getHub_name());
            creatorName.setText("Created by " + item.getHub_creator_name());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public HubsListAdapter(Context context, Vector<HubsListItem> hubs, OnItemClickListener listener) {
        mContext = context;
        this.hubs = hubs;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);
        View hubsListItemView = lf.inflate(R.layout.hubs_list_item, parent, false);
        ViewHolder vh = new ViewHolder(hubsListItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(hubs.get(position), listener);
    }


    @Override
    public int getItemCount() {
        return hubs.size();
    }

    public Context getContext() {
        return mContext;
    }
}
