package com.example.vince.youtubeplayertest.Activities.users_only;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsFragmentAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.R;

import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    TextView hubsNearText;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;
    Location globalLocation;
    boolean set;

    FragmentPagerAdapter adapterViewPager;

    boolean isWifiP2pEnabled = false;
    public void setIsWifiP2pEnabled(boolean value) {isWifiP2pEnabled = value;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new HubsFragmentAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // initialize views
        hubsNearText = (TextView) findViewById(R.id.hubs_near_you_text);
        globalLocation = null;
        set = false;

        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new Vector<>();
        hubSingleton = HubSingleton.getInstance();
    }
}