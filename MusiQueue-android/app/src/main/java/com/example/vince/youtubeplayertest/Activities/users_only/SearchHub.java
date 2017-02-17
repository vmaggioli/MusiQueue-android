package com.example.vince.youtubeplayertest.Activities.users_only;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.vince.youtubeplayertest.R;

public class SearchHub extends AppCompatActivity {
    ListView hubs;
    EditText enterHub;
    Button searchButton;

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

        // TODO: add custom adapter to list view when we know what info to use
        hubs = (ListView) findViewById(R.id.hubs_list);
    }
}
