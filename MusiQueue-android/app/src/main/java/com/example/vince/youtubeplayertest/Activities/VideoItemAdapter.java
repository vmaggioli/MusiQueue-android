package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.users_only.QueueSong;
import com.example.vince.youtubeplayertest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;

/*import static com.example.vince.youtubeplayertest.R.id.video_description;
import static com.example.vince.youtubeplayertest.R.id.video_title;*/

/**
 * Created by Not Prasad on 3/2/2017.
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
        public BackgroundWorker voteUpBW;
        public BackgroundWorker voteDownBW;
        BackgroundWorker voteBW;
        BackgroundWorker.AsyncResponse callback;


        ViewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.queueItem_title);
            upButton = (Button) itemView.findViewById(R.id.button3);
            downButton = (Button) itemView.findViewById(R.id.button2);
            videoUser = (TextView) itemView.findViewById(R.id.queueItem_user);


        }
        public void bind(final QueueSong videoItem, final OnItemClickListener listener) {
            if (videoItem.getTitle().replaceAll("\\s+"," ").length() >= 25)
                videoTitle.setText(videoItem.getTitle().trim().substring(0, 28) + "...");
            else
                videoTitle.setText(videoItem.getTitle());
            videoUser.setText(videoItem.getUser());
            upButton.setText(Integer.toString(videoItem.getUpVotes()));
            downButton.setText(Integer.toString(videoItem.getDownVotes()));



            callback = new BackgroundWorker.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        Log.d("foobar", json.toString());
                        //hubSingleton.clearList();
                        JSONArray jsonArray = json.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            QueueSong item = new QueueSong();

                            JSONObject jObj = jsonArray.getJSONObject(i);

                            item.setTitle(jObj.getString("song_title"));
                            item.setUpVotes(jObj.getInt("up_votes"));
                            item.setDownVotes(jObj.getInt("down_votes"));
                            item.setId(jObj.getString("song_id"));
                            item.setUser(jObj.getString("user_name"));
                            item.setPlace(jObj.getInt("id"));
                            //hubSingleton.add(item);
                        }
                        //TODO: Find other way of doing this

                        voteBW = new BackgroundWorker(callback);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
// <<<<<<< SairamSamBranch


// //            itemView.setOnClickListener(new View.OnClickListener() {
//   //              @Override public void onClick(View v) {
//     //                listener.onItemClick(videoItem);
//       //          }
//         //    });
             /*upButton.setOnClickListener(new View.OnClickListener() {
// =======
//             voteBW = new BackgroundWorker(callback);

//             upButton.setOnClickListener(new View.OnClickListener() {
// >>>>>>> master
                public void onClick(View v) {

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    voteBW.execute("voteUpSong",hub,phone,String.valueOf(videoItem.getPlace()));
                    downButton.setBackgroundResource(android.R.drawable.btn_default);
                    upButton.setBackgroundColor(Color.TRANSPARENT);
                    downButton.setClickable(true);
                    upButton.setClickable(false);
                    upButton.setPressed(true);
                    downButton.setPressed(false);
                }
            });*/
            upButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // show interest in events resulting from ACTION_DOWN
                    if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

                    // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
                    if (event.getAction() != MotionEvent.ACTION_UP) return false;

                    voteUpBW = new BackgroundWorker(callback);

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    voteUpBW.execute("voteUpSong",hub,phone,String.valueOf(videoItem.getPlace()));
                    //upButton.setClickable(false);
                    upButton.setPressed(true);
                    //downButton.setClickable(true);
                    downButton.setPressed(false);
                    //upButton.setEnabled(false);
                    return true;
                }
            });
            /*downButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    voteBW.execute("voteDownSong",hub,phone,String.valueOf(videoItem.getPlace()));
                    downButton.setPressed(true);
                    upButton.setPressed(false);

                }
            });*/
            downButton.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {

                    // show interest in events resulting from ACTION_DOWN
                    //if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

                    // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
                    //if (event.getAction() != MotionEvent.ACTION_UP) return false;

                    voteDownBW = new BackgroundWorker(callback);

                    String hub = hubSingleton.getHubId().toString();
                    String phone = hubSingleton.getUserID();
                    voteDownBW.execute("voteDownSong",hub,phone,String.valueOf(videoItem.getPlace()));
                    //downButton.setClickable(false);
                    downButton.setPressed(true);
                    //upButton.setClickable(true);
                    upButton.setPressed(false);
                    //downButton.setEnabled(false);
                    return true;
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

    }

    @Override
    public int getItemCount() {return videos.size();}

    public Context getContext() {return mContext;}
}
