package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.R;

import java.util.ArrayList;


public class HubsListAdapter extends RecyclerView.Adapter<HubsListAdapter.ViewHolder> {
    private ArrayList<HubsListItem> hubs;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.hub_name);
        }
    }

    public HubsListAdapter(Context context, ArrayList<HubsListItem> hubs) {
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
        TextView nameView = holder.name;

        nameView.setText(item.getHubName());
    }

    @Override
    public int getItemCount() {return hubs.size();}

    public Context getContext() {return mContext;}
}
