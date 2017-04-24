package com.example.vince.youtubeplayertest.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.QueueActivity;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHub;
import com.example.vince.youtubeplayertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by Sam on 4/21/2017.
 */

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.ViewHolder> {

    private static Context mContext;
    private UserItemAdapter.OnItemClickListener listener;
    ArrayList<String> users = null;
    HubSingleton hubSingleton = HubSingleton.getInstance();



    public interface OnItemClickListener {
        void onItemClick(String user);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user;

        ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.useritem);
        }

        public void bind(final String name, final UserItemAdapter.OnItemClickListener listener) {
            user.setText(name);
        }

    }

    public UserItemAdapter(Context context, ArrayList<String> users, UserItemAdapter.OnItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.users = users;

    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);
        View usersListItemView;
        usersListItemView = lf.inflate(R.layout.user_item, parent, false);
        ViewHolder vh = new UserItemAdapter.ViewHolder(usersListItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserItemAdapter.ViewHolder holder, int position) {
        System.out.println(users.size() + "pos: " + position);
        holder.bind(users.get(position), listener);

    }
    public int getItemCount() {return users.size();}
    public Context getContext() {return mContext;}
}
