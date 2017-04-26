package com.example.vince.youtubeplayertest.Activities.helper_classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.vince.youtubeplayertest.Activities.users_only.NearestHubsFragment;
import com.example.vince.youtubeplayertest.Activities.users_only.RecentHubsFragment;
import com.example.vince.youtubeplayertest.Activities.users_only.SearchHubsFragment;
import com.example.vince.youtubeplayertest.Activities.users_only.WifiHubsFragment;

/**
 * Created by Sairam on 4/23/2017.
 */

public class HubsFragmentAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;

   public HubsFragmentAdapter (FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment different title
                return RecentHubsFragment.newInstance(0, "Page # 1");
            case 1: // Fragment # 0 - This will show FirstFragment
                return SearchHubsFragment.newInstance(1, "Page # 2");
            case 2:
                return WifiHubsFragment.newInstance(2, "Page # 3");
            case 3: // Fragment # 1 - This will show SecondFragment
                return NearestHubsFragment.newInstance(3, "Page # 4");
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        //return "Page " + position;
        if (position == 0) {
            return "Recent Hubs";
        } else if (position == 1) {
            return "Search Hubs";
        } else if (position == 2){
            return "WiFi Hubs";
        } else {
            return "Nearby Hubs";
        }
    }
}
