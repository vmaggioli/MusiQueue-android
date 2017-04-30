package com.example.musiqueue.Activities.users_only;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.TextView;

import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.HelperClasses.HubsListItem;
import com.example.musiqueue.HelperClasses.adapters.HubsFragmentAdapter;
import com.example.musiqueue.R;

import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    TextView hubsNearText;
    TextView hubsWifiText;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;
    Location globalLocation;
    PagerTabStrip pagerTabStrip;
    boolean set;

    FragmentPagerAdapter adapterViewPager;

    boolean isWifiP2pEnabled = false;
    public void setIsWifiP2pEnabled(boolean value) {isWifiP2pEnabled = value;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor("#303F9F"));


        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new HubsFragmentAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // initialize views
        hubsNearText = (TextView) findViewById(R.id.hubs_near_you_text);
        hubsWifiText = (TextView) findViewById(R.id.hubs_over_wifi);
        globalLocation = null;
        set = false;

        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new Vector<>();
        hubSingleton = HubSingleton.getInstance();
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    WifiHubsFragment wifiHubsFragment = (WifiHubsFragment) adapterViewPager.getItem(2);
                    //wifiHubsFragment.setAllowConfig();
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 2) {
                    WifiHubsFragment wifiHubsFragment = (WifiHubsFragment) adapterViewPager.getItem(2);
                    //wifiHubsFragment.setAllowConfig();
                }
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }
}