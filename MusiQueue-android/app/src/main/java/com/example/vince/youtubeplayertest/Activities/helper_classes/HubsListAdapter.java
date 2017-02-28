package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.R;

import java.util.Vector;


public class HubsListAdapter extends RecyclerView.Adapter<HubsListAdapter.ViewHolder> {
    private Vector<HubsListItem> hubs;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView creatorName;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.hub_name);
            creatorName = (TextView) itemView.findViewById(R.id.hub_creator_name);
        }
    }

    public HubsListAdapter(Context context, Vector<HubsListItem> hubs) {
        mContext = context;
        this.hubs = hubs;
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
        HubsListItem item = hubs.get(position);

        holder.name.setText(item.getHub_name());
        holder.creatorName.setText("Created by " + item.getHub_creator_name());
    }

    @Override
    public int getItemCount() {return hubs.size();}

    public Context getContext() {return mContext;}
}
