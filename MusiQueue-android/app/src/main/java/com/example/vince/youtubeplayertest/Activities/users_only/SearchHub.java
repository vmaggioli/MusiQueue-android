package com.example.vince.youtubeplayertest.Activities.users_only;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.Activities.helper_classes.SearchHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    EditText enterHub;
    Button searchButton;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;

    HubsListAdapter.OnItemClickListener callback;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        Context context = getApplicationContext();

        // initialize views
        enterHub = (EditText) findViewById(R.id.hub_name_search);
        searchButton = (Button) findViewById(R.id.hub_name_search_button);

        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new Vector<>();
        hubSingleton = HubSingleton.getInstance();

        // ALLOWS SEARCHING FROM KEYBOARD INSTEAD OF CLICKING SEARCH BUTTON
        enterHub.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(findViewById(R.id.hub_name_search_button));
                    return true;
                }
                return false;
            }
        });

        // setup callback
        callback = new HubsListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(HubsListItem hub) {
                selectHub(hub);
            }
        };

        HubsListAdapter hubsListAdapter = new HubsListAdapter(this, hubs, callback);
        hubsList.setAdapter(hubsListAdapter);
        hubsList.setLayoutManager(new LinearLayoutManager(this));

        hubsList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());
        recentHubs();

    }

    protected void selectHub(HubsListItem hub) {
        if(hub.getHub_pin_required()) {
            final Intent i = new Intent(SearchHub.this, JoinHub.class);
            i.putExtra("hubName", hub.getHub_name());
            startActivity(i);
        }else{
            final Intent i = new Intent(SearchHub.this, ConnectToHubActivity.class);
            i.putExtra("hubName", hub.getHub_name());
            i.putExtra("hubPin","");
            startActivity(i);
        }
    }

    public void search(View view) {
        if (enterHub.length() != 0) {
            String contents = enterHub.getText().toString();

            BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    Gson gson = new Gson();
                    SearchHubResponse r = gson.fromJson(result, SearchHubResponse.class);

                    HubsListAdapter hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                    hubsList.setAdapter(hubsListAdapter);
                    hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });
            backgroundWorker.execute("searchHub", contents);
        }
    }

    public void recentHubs() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                SearchHubResponse r = gson.fromJson(result, SearchHubResponse.class);

                HubsListAdapter hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                hubsList.setAdapter(hubsListAdapter);
                hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
        backgroundWorker.execute("recentHubs", hubSingleton.getUserID());
    }

}
