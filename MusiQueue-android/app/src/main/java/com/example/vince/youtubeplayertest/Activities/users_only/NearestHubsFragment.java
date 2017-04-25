package com.example.vince.youtubeplayertest.Activities.users_only;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.Activities.helper_classes.SearchHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;

import java.util.List;
import java.util.Vector;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Sairam on 4/23/2017.
 */

public class NearestHubsFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    private static boolean set = false;
    private static boolean dialogShown = false;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    private ViewPager vpPager;

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected HubsListAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    HubsListAdapter.OnItemClickListener callback;
    Vector<HubsListItem> hubs;
    SearchHubResponse r;

    public Location globalLocation;
    View rootView;
    LocationManager locationmanager;
    LocationListener locationListener;

    public boolean isTurnedLocationServicesOn() {
        return turnedLocationServicesOn;
    }

    public void setTurnedLocationServicesOn(boolean turnedLocationServicesOn) {
        this.turnedLocationServicesOn = turnedLocationServicesOn;
    }

    private boolean turnedLocationServicesOn = false;

    // newInstance constructor for creating fragment with arguments
    public static NearestHubsFragment newInstance(int page, String title) {
        NearestHubsFragment fragmentSecond = new NearestHubsFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentSecond.setArguments(args);
        return fragmentSecond;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.

        vpPager = (ViewPager) getActivity().findViewById(R.id.vpPager);
        promptUserTurnOnLocationServices();
        if (!isTurnedLocationServicesOn()) {
            vpPager.setCurrentItem(2);
            return;
        }
        promptUserAllowLocation();
        if (!dialogShown) {
            vpPager.setCurrentItem(2);
            return;
        }

        callback = new HubsListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(HubsListItem hub) {
                selectHub(hub);
            }
        };
        hubs = new Vector<>();

        configureLocation();
        globalLocation = getLastKnownLocation();
    }

    private void promptUserAllowLocation() {
        locationmanager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        builder.setMessage("We need to access your location momentarily, allow us to use " +
                "your GPS location?").setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogShown = true;
                        if (!set) {
                            configureLocation();
                            set = true;
                            hubs.clear();
                            globalLocation = getLastKnownLocation();
                            if (globalLocation == null)
                                return;

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
                            builder.setMessage("Your general location has been saved. You can disable your " +
                                    "GPS now.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("CANCEL", new AlertDialog.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                }
        ).setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        vpPager.setCurrentItem(1);
                    }
                });
    }

    private void configureLocation() {
        locationmanager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                globalLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
                int off;
                try {
                    off = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                    if(off==0){
                        Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(onGPS);
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                System.out.println(locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
                return;
            }
        } else {
            int off;
            try {
                off = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);
                if(off==0){
                    Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(onGPS);
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            System.out.println("second: " + locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER));        }
    }

    private Location getLastKnownLocation() {

        locationmanager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationmanager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if ( ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION )
                    == PackageManager.PERMISSION_GRANTED ) {
                l = locationmanager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    protected void selectHub(HubsListItem hub) {
        if(hub.getHub_pin_required()) {
            final Intent i = new Intent(getActivity(), JoinHub.class);
            i.putExtra("hubName", hub.getHub_name());
            startActivity(i);
        }else{
            final Intent i = new Intent(getActivity(), ConnectToHubActivity.class);
            i.putExtra("hubName", hub.getHub_name());
            i.putExtra("hubPin","");
            startActivity(i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.hubs_fragment, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.hubs_list);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity());

            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }

            BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    Gson gson = new Gson();
                    r = gson.fromJson(result, SearchHubResponse.class);

                    setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

                    mAdapter = new HubsListAdapter(getActivity(), r.result, callback);
                    // Set CustomAdapter as the adapter for RecyclerView.
                    mRecyclerView.setAdapter(mAdapter);
                }
            });

            String userId = HubSingleton.getInstance().getUserID();
            String latitude = String.valueOf(globalLocation.getLatitude());
            String longitude = String.valueOf(globalLocation.getLongitude());

            backgroundWorker.execute("nearestHubs", userId, latitude, longitude);
        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }
    public void promptUserTurnOnLocationServices() {
        locationmanager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("Your location services aren't enabled.  " +
                    "Would you like to turn them on?");
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    setTurnedLocationServicesOn(false);
                }
            });
            dialog.show();
        }
    }
}
