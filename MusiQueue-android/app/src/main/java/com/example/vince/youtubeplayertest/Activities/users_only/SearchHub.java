package com.example.vince.youtubeplayertest.Activities.users_only;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.util.List;
import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    HubsListAdapter hubsListAdapter;
    EditText enterHub;
    Button searchButton;
    Button nearbyHubsButton;
    Button recentHubsButton;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;
    SearchHubResponse r;
    static boolean dialogShown = false;

    public boolean isTurnedLocationServicesOn() {
        return turnedLocationServicesOn;
    }

    public void setTurnedLocationServicesOn(boolean turnedLocationServicesOn) {
        this.turnedLocationServicesOn = turnedLocationServicesOn;
    }

    private boolean turnedLocationServicesOn = false;

    LocationManager locationmanager;
    LocationListener locationListener;
    Location globalLocation;
    boolean set;

    HubsListAdapter.OnItemClickListener callback;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        context = getApplicationContext();

        // initialize views
        enterHub = (EditText) findViewById(R.id.hub_name_search);
        searchButton = (Button) findViewById(R.id.hub_name_search_button);
        globalLocation = null;
        set = false;

        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new Vector<>();
        hubSingleton = HubSingleton.getInstance();

        final HubsListAdapter hubsListAdapter = new HubsListAdapter(this, hubs, callback);
        hubsList.setAdapter(hubsListAdapter);
        hubsList.setLayoutManager(new LinearLayoutManager(this));

        hubsList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());

        // ALLOWS SEARCHING FROM KEYBOARD INSTEAD OF CLICKING SEARCH BUTTON
        enterHub.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hubs.clear();
                    hubsListAdapter.notifyDataSetChanged();
                    setSearchHubsView();
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hubs.clear();
                hubsListAdapter.notifyDataSetChanged();
                setSearchHubsView();
                search(new View(getApplicationContext()));
            }
        });

        // setup callback
        callback = new HubsListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(HubsListItem hub) {
                selectHub(hub);
            }
        };

        recentHubsButton = (Button) findViewById(R.id.search_hubs_recent_button);
        nearbyHubsButton = (Button) findViewById(R.id.search_hubs_nearby_button);

        recentHubsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hubs.clear();
                hubsListAdapter.notifyDataSetChanged();
                recentHubs();
                setRecentHubsView();
            }
        });

        nearbyHubsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptUserTurnOnLocationServices();
                if (!set && isTurnedLocationServicesOn()) {
                    locationmanager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchHub.this);
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
                                        hubsListAdapter.notifyDataSetChanged();
                                        setNearbyHubsView();
                                        searchByLocation(new View(getApplicationContext()));

                                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchHub.this);
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
                            })
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    globalLocation = null;
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    if (dialogShown) {
                        setNearbyHubsView();
                        searchByLocation(new View(getApplicationContext()));
                    }
                }
            }
        });

        // App starts with recent hubs button pressed
        setRecentHubsView();
        searchButton.setVisibility(View.VISIBLE);
        enterHub.setVisibility(View.VISIBLE);
        recentHubs();
    }

    public void setRecentHubsView() {
        recentHubsButton.setPressed(false);
        recentHubsButton.setEnabled(true);
        recentHubsButton.setBackgroundResource(R.drawable.gray_bubble_button_pressed);
        nearbyHubsButton.setPressed(false);
        nearbyHubsButton.setEnabled(true);
        nearbyHubsButton.setBackgroundResource(R.drawable.gray_bubble_button);
    }

    public void setSearchHubsView() {
        recentHubsButton.setPressed(false);
        recentHubsButton.setEnabled(true);
        recentHubsButton.setBackgroundResource(R.drawable.gray_bubble_button);
        nearbyHubsButton.setPressed(false);
        nearbyHubsButton.setEnabled(true);
        nearbyHubsButton.setBackgroundResource(R.drawable.gray_bubble_button);
    }

    public void setNearbyHubsView() {
        recentHubsButton.setPressed(false);
        recentHubsButton.setEnabled(true);
        recentHubsButton.setBackgroundResource(R.drawable.gray_bubble_button);
        nearbyHubsButton.setPressed(true);
        nearbyHubsButton.setEnabled(false);
        nearbyHubsButton.setBackgroundResource(R.drawable.gray_bubble_button_pressed);
    }

    public void promptUserTurnOnLocationServices() {
        locationmanager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(SearchHub.this);
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

        // check to see if they actually turned it on
        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if (gps_enabled)
            setTurnedLocationServicesOn(true);
        else
            setTurnedLocationServicesOn(false);
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
                    r = gson.fromJson(result, SearchHubResponse.class);

                    hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                    hubsList.setAdapter(hubsListAdapter);
                    hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });


            final HubSingleton appState = HubSingleton.getInstance();
            backgroundWorker.execute("searchHub", contents, appState.getUserID());
            setSearchHubsView();
        }
    }

    public void recentHubs() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                r = gson.fromJson(result, SearchHubResponse.class);

                hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                hubsList.setAdapter(hubsListAdapter);
                hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
        backgroundWorker.execute("recentHubs", hubSingleton.getUserID());
        setRecentHubsView();
    }

    public void nearestHubs() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                r = gson.fromJson(result, SearchHubResponse.class);

                HubsListAdapter hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                hubsList.setAdapter(hubsListAdapter);
                hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
        backgroundWorker.execute("nearestHubs", hubSingleton.getUserID(),
                String.valueOf(globalLocation.getLatitude()), String.valueOf(globalLocation.getLongitude()));
        setNearbyHubsView();
    }

    public void searchByLocation(View view) {
        if (r != null)
            r.result.clear();
        globalLocation = getLastKnownLocation();
        if (globalLocation == null)
            return;
        nearestHubs();
    }

    private Location getLastKnownLocation() {

        locationmanager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationmanager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if ( ContextCompat.checkSelfPermission( this,
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

    private void configureLocation() {
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                }, 10);
                int off;
                try {
                    off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
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
                off = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
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

    public void setIsWifiP2pEnabled(boolean b) {
    }
}
