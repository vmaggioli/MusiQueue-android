package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
//import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.R;

import java.util.Vector;

import static com.example.vince.youtubeplayertest.R.id.video_description;
import static com.example.vince.youtubeplayertest.R.id.video_title;

/*import static com.example.vince.youtubeplayertest.R.id.video_description;
import static com.example.vince.youtubeplayertest.R.id.video_title;*/

/**
 * Created by Prasad on 3/2/2017.
 */

public class VideoItemAdapter extends RecyclerView.Adapter<VideoItemAdapter.ViewHolder> {

    private Vector<VideoItem> videos;
    private Context mContext;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(VideoItem videoItem);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView videoTitle;
        public Button upButton;
        public Button downButton;
        //public TextView videoDescription;


        ViewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.queueItem_title);
            upButton = (Button) itemView.findViewById(R.id.button2);
            downButton = (Button) itemView.findViewById(R.id.button3);
            //videoDescription = (TextView) itemView.findViewById(R.id.video_description);
        }
        public void bind(final VideoItem videoItem, final VideoItemAdapter.OnItemClickListener listener) {
            videoTitle.setText(videoItem.getTitle());
            //videoDescription.setText(videoItem.getDescription());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(videoItem);
                }
            });
        }
    }

    public VideoItemAdapter(Context context, Vector<VideoItem> videos, OnItemClickListener listener) {
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
//        VideoItem item = videos.get(position);
//        holder.videoTitle.setText(item.getTitle());
//        holder.videoDescription.setText(item.getDescription());

    }

    @Override
    public int getItemCount() {return videos.size();}

    public Context getContext() {return mContext;}
}
