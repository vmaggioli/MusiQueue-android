package com.example.musiqueue.HelperClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.musiqueue.Activities.hub_admin_only.CreateHub;
import com.example.musiqueue.Activities.users_only.SearchHub;

/**
 * Created by Brian on 4/23/2017.
 */

public class WiFiBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager wifiP2pManager;
    private WifiP2pManager.Channel channel;
    WifiP2pManager.PeerListListener peerListListener;
    private CreateHub createHubActivity;
    private SearchHub searchHubActivity;
    private String activity;

    public WiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener, CreateHub createHubActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.peerListListener = peerListListener;
        this.createHubActivity = createHubActivity;
        activity = "create";
    }

    public WiFiBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener peerListListener, SearchHub searchHubActivity) {
        super();
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.peerListListener = peerListListener;
        this.searchHubActivity = searchHubActivity;
        activity = "search";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                if (activity.equals("create"))
                    createHubActivity.setIsWifiP2pEnabled(true);
                else if (activity.equals("search"))
                    searchHubActivity.setIsWifiP2pEnabled(true);
            } else {
                // Wi-Fi P2P is not enabled
                if (activity.equals("create"))
                    createHubActivity.setIsWifiP2pEnabled(false);
                else if (activity.equals("search"))
                    searchHubActivity.setIsWifiP2pEnabled(false);

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (wifiP2pManager != null)
                wifiP2pManager.requestPeers(channel, peerListListener);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
