package com.example.vince.youtubeplayertest.Activities.users_only;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.R;

import java.util.ArrayList;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    EditText enterHub;
    Button searchButton;
    ArrayList<HubsListItem> hubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        // initialize views
        enterHub = (EditText) findViewById(R.id.hub_name_search);
        searchButton = (Button) findViewById(R.id.hub_name_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // TODO: find out how to add the data from AWS
        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new ArrayList<>();

        // TODO: delete dummy data
        hubs.add(new HubsListItem("Vincent Maggioli"));
        hubs.add(new HubsListItem("Brian"));

        HubsListAdapter hubsListAdapter = new HubsListAdapter(this, hubs);
        hubsList.setAdapter(hubsListAdapter);
        hubsList.setLayoutManager(new LinearLayoutManager(this));

    }
}
