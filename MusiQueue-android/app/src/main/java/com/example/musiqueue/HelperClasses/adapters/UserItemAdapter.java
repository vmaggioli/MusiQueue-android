package com.example.musiqueue.HelperClasses.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.musiqueue.HelperClasses.BackgroundWorker;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.User;
import com.example.musiqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserItemAdapter extends RecyclerView.Adapter<UserItemAdapter.ViewHolder> {

    private static Context mContext;
    private UserItemAdapter.OnItemClickListener listener;
    ArrayList<User> users = null;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user;
        public Button removeUser;



        ViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.useritem);
            removeUser = (Button) itemView.findViewById(R.id.removeUser);
        }

        public void bind(final User name, final OnItemClickListener listener) {
            user.setText(name.getName());
            final HubSingleton hubSingleton = HubSingleton.getInstance();
            removeUser.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp);


            removeUser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User Lists")
                            .child(hubSingleton.getHubName()).child(hubSingleton.getUserID());
                    userRef.removeValue();
                }
            });
        }

    }

    public UserItemAdapter(Context context, ArrayList<User> users, OnItemClickListener listener) {
        mContext = context;
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
        return new UserItemAdapter.ViewHolder(usersListItemView);
    }

    @Override
    public void onBindViewHolder(UserItemAdapter.ViewHolder holder, int position) {
        System.out.println(users.size() + "user pos: " + position);
        holder.bind(users.get(position), listener);

    }
    public int getItemCount() {return users.size();}
    public Context getContext() {return mContext;}
}
