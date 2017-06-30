package com.example.musiqueue.HelperClasses.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.musiqueue.Activities.hub_admin_only.QueueActivity;
import com.example.musiqueue.Activities.users_only.QueueSong;
import com.example.musiqueue.HelperClasses.BackgroundWorker;
import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;



public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {

    private static ArrayList<QueueSong> videos;
    private static Context mContext;
    private OnItemClickListener listener;
    String caller=null;


    public interface OnItemClickListener {
        void onItemClick(QueueSong videoItem);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView videoTitle;
        public Button upButton;
        public Button downButton;
        public Button removeSongButton;
        public TextView videoUser;
        HubSingleton hubSingleton = HubSingleton.getInstance();
        public BackgroundWorker voteUpBW;
        public BackgroundWorker voteDownBW;
        public BackgroundWorker removeSongBW;
        BackgroundWorker voteBW;
        BackgroundWorker.AsyncResponse callback;
        LinearLayout queueItemTitleBkrnd;
        LayerDrawable base = (LayerDrawable) ContextCompat.getDrawable(mContext, R.drawable.hub_search_item);
        GradientDrawable baseColor = (GradientDrawable) base.findDrawableByLayerId(R.id.base);
        LayerDrawable bkrnd = (LayerDrawable) ContextCompat.getDrawable(mContext, R.drawable.hub_search_item_title);
        GradientDrawable lightShape = (GradientDrawable) bkrnd.findDrawableByLayerId(R.id.lighting);
        GradientDrawable main = (GradientDrawable) bkrnd.findDrawableByLayerId(R.id.main);
        public String caller=null;


        public int getColor(String title){
            String hex = String.format("%06x", new BigInteger(1, title.getBytes()));
            int color = Integer.parseInt(hex.substring(1, 2), 16);
            hex = hex.substring(hex.length() - 4, hex.length());
            if (color % 3 == 0) return Integer.parseInt(hex, 16) * 256 + 0xff000096;
            if (color % 3 == 1) return Integer.parseInt(hex, 16) + 0xff960000;

            String hex1 = hex.substring(hex.length() - 2, hex.length());
            String hex2 = hex.substring(hex.length() - 4, hex.length() - 2);
            return Integer.parseInt(hex1, 16) + 0xff009600 + Integer.parseInt(hex2, 16) * 65536;
        }

        ViewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.queueItem_title);
            upButton = (Button) itemView.findViewById(R.id.button3);
            downButton = (Button) itemView.findViewById(R.id.button2);
            videoUser = (TextView) itemView.findViewById(R.id.queueItem_user);
            removeSongButton = (Button) itemView.findViewById(R.id.removeSong);
            if (mContext.getClass() == QueueActivity.class)
                queueItemTitleBkrnd = (LinearLayout) itemView.findViewById(R.id.queue_item_title_bkrnd);
            else
                queueItemTitleBkrnd = (LinearLayout) itemView.findViewById(R.id.queue_item_title_user);
        }
        public static int manipulateColor(int color, float factor) {
            int a = Color.alpha(color);
            int r = Math.round(Color.red(color) * factor);
            int g = Math.round(Color.green(color) * factor);
            int b = Math.round(Color.blue(color) * factor);
            return Color.argb(a,
                    Math.min(r,255),
                    Math.min(g,255),
                    Math.min(b,255));
        }
        public void bind(final QueueSong videoItem, final OnItemClickListener listener,String caller) {
//            if (videoItem.getTitle().replaceAll("\\s+"," ").length() >= 25)
//                videoTitle.setText(videoItem.getTitle().trim().substring(0, 28) + "...");
//            else
            videoTitle.setText(videoItem.getTitle());
            videoTitle.setBackgroundColor(Color.TRANSPARENT);
            //bkrnd.setColorFilter(getColor(videoItem.getTitle()), PorterDuff.Mode.SRC_ATOP);
            lightShape.setColor(manipulateColor(getColor(videoItem.getTitle()), 1.25f));
            main.setColor(getColor(videoItem.getTitle()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    queueItemTitleBkrnd.setBackground(bkrnd);
                else
                    queueItemTitleBkrnd.setBackgroundColor(getColor(videoItem.getTitle()));

            videoUser.setText(videoItem.getUser());
            upButton.setText(Long.toString(videoItem.getUpVotes()));
            downButton.setText(Long.toString(videoItem.getDownVotes()));
            if(caller.equals("owner")) {
                System.out.println("OWNER IS IN HUB");
                removeSongButton.setBackgroundResource(R.drawable.ic_remove_circle_black_24dp);
            }
            if(videoItem.getState() == 0) {
                upButton.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp_2);
                downButton.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp_2);

            }
            else if(videoItem.getState() == -1) {
                downButton.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp);
                upButton.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp_2);

            }
            else {
                upButton.setBackgroundResource(R.drawable.ic_thumb_up_black_24dp);
                downButton.setBackgroundResource(R.drawable.ic_thumb_down_black_24dp_2);

            }



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

                        voteBW = new BackgroundWorker(callback);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            upButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // show interest in events resulting from ACTION_DOWN
                    if (event.getAction() == MotionEvent.ACTION_DOWN) return true;

                    // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
                    if (event.getAction() != MotionEvent.ACTION_UP) return false;

                    // Firebase logic
                    String songId = "";
                    Long currentValue = Long.parseLong(upButton.getText().toString());
                    for (QueueSong song : videos) {
                        if (song.getTitle().equals(videoTitle.getText().toString()))
                            songId = song.getId();
                        break;
                    }
                    DatabaseReference upRef = FirebaseDatabase.getInstance().getReference()
                            .child("/Song Lists/" + hubSingleton.getHubName() + "/"
                                    + songId + "/Up-votes");
                    upRef.setValue((++currentValue).toString());

                    upButton.setPressed(true);
                    downButton.setPressed(false);
                    return true;
                }
            });

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

            if (mContext.getClass() == QueueActivity.class) {
                if (videoItem != hubSingleton.getSongAt(0)) {
                    removeSongButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setMessage("Are you sure you want to remove this song from the playlist?")
                                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            removeSongBW = new BackgroundWorker(callback);
                                            String hubId = hubSingleton.getHubId().toString();
                                            String songId = String.valueOf(videoItem.getPlace());
                                            String phoneId = hubSingleton.getUserID();
                                            videos.remove(videoItem);
                                            removeSongBW.execute("removeSong", hubId, phoneId, songId);
                                            //removeSongButton.setPressed(true);

                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        }
                    });
                } else {
                    //removeSongButton.setVisibility(View.GONE);
                }
            }

        }
    }

    public VideoItemAdapter(Context context, ArrayList<QueueSong> videos,String caller, OnItemClickListener listener) {
        this.mContext = context;
        this.videos = videos;
        this.listener = listener;
        this.caller = caller;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater lf = LayoutInflater.from(context);
        View hubsListItemView;
        if (mContext.getClass() == QueueActivity.class) {
            hubsListItemView = lf.inflate(R.layout.queue_item_admin, parent, false);
        } else {
            hubsListItemView = lf.inflate(R.layout.queue_item, parent, false);
        }
        ViewHolder vh = new ViewHolder(hubsListItemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //System.out.println(videos.size() + "pos: " + position);
        holder.bind(videos.get(position), listener,caller);

    }

    @Override
    public int getItemCount() {return videos.size();}

    public Context getContext() {return mContext;}
}
