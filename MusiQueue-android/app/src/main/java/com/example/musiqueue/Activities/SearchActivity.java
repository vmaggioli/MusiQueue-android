package com.example.musiqueue.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.musiqueue.Activities.hub_admin_only.QueueActivity;
import com.example.musiqueue.Activities.users_only.ViewQueueActivity;
import com.example.musiqueue.HelperClasses.VideoItem;
import com.example.musiqueue.HelperClasses.YoutubeConnector;
import com.example.musiqueue.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchActivity extends Activity {
    private List<VideoItem> searchResults;

    private EditText searchInput;
    private ListView videosFound;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_song);

        searchInput = (EditText) findViewById(R.id.search_input);
        videosFound = (ListView) findViewById(R.id.videos_found);


        handler = new Handler();

        addClickListener();
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

    }

    private void searchOnYoutube(final String keywords) {
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(SearchActivity.this);
                searchResults = yc.search(keywords);
                handler.post(new Runnable() {
                    public void run() {
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);


                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }

    private void addClickListener() {
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent i = getIntent();
                Intent intent;
                if (i.getStringExtra("view_queue").equals("User")) {
                    intent = new Intent(getApplicationContext(), ViewQueueActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), QueueActivity.class);
                }
                Bundle videoInfo = new Bundle();

                videoInfo.putString("title", searchResults.get(pos).getTitle());
                videoInfo.putString("description", searchResults.get(pos).getDescription());
                videoInfo.putString("thumbnailURL", searchResults.get(pos).getThumbnailURL());
                videoInfo.putString("id", searchResults.get(pos).getId());

                intent.putExtras(videoInfo);
                startActivity(intent);
            }

        });
    }

}