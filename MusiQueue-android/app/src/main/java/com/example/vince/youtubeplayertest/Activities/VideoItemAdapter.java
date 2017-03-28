package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;
import com.example.vince.youtubeplayertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.vince.youtubeplayertest.R.id.video_description;
import static com.example.vince.youtubeplayertest.R.id.video_title;

/*import static com.example.vince.youtubeplayertest.R.id.video_description;
import static com.example.vince.youtubeplayertest.R.id.video_title;*/

/**
 * Created by Prasad on 3/2/2017.
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {

    private ArrayList<QueueSong> videos;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(QueueSong videoItem);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView videoTitle;
        public Button upButton;
        public Button downButton;
        public TextView videoUser;
        HubSingleton hubSingleton = HubSingleton.getInstance();


        ViewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.queueItem_title);
            upButton = (Button) itemView.findViewById(R.id.button2);
            downButton = (Button) itemView.findViewById(R.id.button3);
            videoUser = (TextView) itemView.findViewById(R.id.queueItem_user);

        }
        public void bind(final QueueSong videoItem, final OnItemClickListener listener) {
            videoTitle.setText(videoItem.getTitle());
            videoUser.setText(videoItem.getUser());
            final BackgroundWorker.AsyncResponse callback;

            callback = new BackgroundWorker.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        Log.d("foobar", json.toString());
                        hubSingleton.clearList();
                        JSONArray jsonArray = json.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            QueueSong item = new QueueSong();

                            JSONObject jObj = jsonArray.getJSONObject(i);

                            item.setTitle(jObj.getString("song_title"));
                            item.setUpVotes(jObj.getInt("up_votes"));
                            item.setDownVotes(jObj.getInt("down_votes"));
                            item.setId(jObj.getString("song_id"));
                            item.setUser(jObj.getString("user_name"));
                            hubSingleton.add(item);

                        }
                        //adapter.notifyDataSetChanged();
                        this.

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            final BackgroundWorker voteBW = new BackgroundWorker(callback);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(videoItem);
                }
            });
            upButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    String song = videoItem.getId();
                    voteBW.execute("voteUpSong",hub,phone,song);

                }
            });
            downButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    String song = videoItem.getId();
                    voteBW.execute("voteDownSong",hub,phone,song);

                }
            });
        }
    }

    public VideoItemAdapter(Context context, ArrayList<QueueSong> videos, OnItemClickListener listener) {
        this.mContext = context;
        this.videos = videos;
        this.listener = listener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);
        View hubsListItemView = lf.inflate(R.layout.queue_item, parent, false);
        ViewHolder vh = new ViewHolder(hubsListItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println(videos.size() + "pos: " + position);
        holder.bind(videos.get(position), listener);
//        QueueSong item = videos.get(position);
//        holder.videoTitle.setText(item.getTitle());
//        holder.videoDescription.setText(item.getDescription());

    }

    @Override
    public int getItemCount() {return videos.size();}

    public Context getContext() {return mContext;}
}
