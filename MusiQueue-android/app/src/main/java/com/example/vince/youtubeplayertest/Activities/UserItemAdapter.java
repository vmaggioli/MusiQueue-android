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
import com.example.vince.youtubeplayertest.Activities.hub_admin_only.User;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;
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
    ArrayList<User> users = null;
    HubSingleton hubSingleton = HubSingleton.getInstance();



    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public Button removeUser;
        BackgroundWorker.AsyncResponse callback;
        BackgroundWorker rmUser;



        ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.useritem);
            removeUser = (Button) itemView.findViewById(R.id.removeUser);
        }

        public void bind(final User name, final OnItemClickListener listener) {
            user.setText(name.getName());
            final HubSingleton hubSingleton = HubSingleton.getInstance();


            removeUser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    callback = new BackgroundWorker.AsyncResponse() {
                        @Override
                        public void processFinish(String result) {
                            try {
                                JSONObject json = new JSONObject(result);
                                JSONObject jsonArray = json.getJSONObject("result");
                                JSONArray users = jsonArray.getJSONArray("users");

                                hubSingleton.clearUsers();

                                User user;
                                for(int i = 0; i < users.length();i++){
                                    user = new User();
                                    JSONObject name = users.getJSONObject(i);
                                    user.setName(name.getString("name"));
                                    user.setId(name.getString("id"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    rmUser = new BackgroundWorker(callback);

                    //TODO mlast 2 params may be swapped/wrong
                    if(!hubSingleton.getUsername().equals(name.getName())) {
                        rmUser.execute("removeUser", hubSingleton.getHubId().toString(), hubSingleton.getUserID(), name.getId());
                    }
                }
            });
        }

    }

    public UserItemAdapter(Context context, ArrayList<User> users, OnItemClickListener listener) {
        this.mContext = context;
        this.listener = listener;
        this.users = users;
        System.out.println(users);
        System.out.println("users in adapter size: " + users.size());

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
        System.out.println(users.size() + "user pos: " + position);
        holder.bind(users.get(position), listener);

    }
    public int getItemCount() {return users.size();}
    public Context getContext() {return mContext;}
}
