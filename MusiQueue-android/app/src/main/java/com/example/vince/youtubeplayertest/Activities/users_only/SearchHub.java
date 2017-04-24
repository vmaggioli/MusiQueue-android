package com.example.vince.youtubeplayertest.Activities.users_only;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsFragmentAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.Activities.helper_classes.SearchHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    HubsListAdapter hubsListAdapter;
    EditText enterHub;
    Button searchButton;
    //Button searchByNameButton;
    //Button searchByLocationButton;
    TextView hubsNearText;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;
    SearchHubResponse r;

    LocationManager locationmanager;
    LocationListener locationListener;
    Location globalLocation;
    boolean set;


    HubsListAdapter.OnItemClickListener callback;
    FragmentPagerAdapter adapterViewPager;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new HubsFragmentAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        // initialize views
        enterHub = (EditText) findViewById(R.id.hub_name_search);
        hubsNearText = (TextView) findViewById(R.id.hubs_near_you_text);
        searchButton = (Button) findViewById(R.id.hub_name_search_button);
        //searchByNameButton = (Button) findViewById(R.id.search_by_name_button);
        //searchByLocationButton = (Button) findViewById(R.id.search_by_location_button);
        globalLocation = null;
        set = false;

        // view starts with searchByNameButton considered pressed
        //searchByNameButton.setEnabled(false);
        //searchByNameButton.setPressed(true);

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
        /*callback = new HubsListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(HubsListItem hub) {
                selectHub(hub);
            }
        };

        HubsListAdapter hubsListAdapter = new HubsListAdapter(this, hubs, callback);
        hubsList.setAdapter(hubsListAdapter);
        hubsList.setLayoutManager(new LinearLayoutManager(this));

        hubsList.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.LTGRAY).sizeResId(R.dimen.divider).marginResId(R.dimen.margin5dp, R.dimen.margin5dp).build());
        recentHubs();*/

    }

    /*protected void selectHub(HubsListItem hub) {
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
    }*/

    public void search(View view) {
        if (enterHub.length() != 0) {
            String contents = enterHub.getText().toString();

            /*BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
                @Override
                public void processFinish(String result) {
                    Gson gson = new Gson();
                    r = gson.fromJson(result, SearchHubResponse.class);

                    hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                    hubsList.setAdapter(hubsListAdapter);
                    hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                }
            });*/


            final HubSingleton appState = HubSingleton.getInstance();
            //backgroundWorker.execute("searchHub", contents, appState.getUserID());*/

            /*final Intent intent = new Intent(SearchHub.this, SearchHubsFragment.class);
            intent.putExtra("hubsFromSearch", contents);
            startActivity(intent);*/
            Bundle bundle = new Bundle();
            bundle.putString("searchString", contents);
            bundle.putString("userId", appState.getUserID());
            SearchHubsFragment fragobj = new SearchHubsFragment();
            fragobj.setArguments(bundle);
        }
    }

    public void recentHubs() {
        /*BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                r = gson.fromJson(result, SearchHubResponse.class);

                hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                hubsList.setAdapter(hubsListAdapter);
                hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
        backgroundWorker.execute("recentHubs", hubSingleton.getUserID());*/
        Bundle bundle = new Bundle();
        bundle.putString("userId", hubSingleton.getUserID());
        RecentHubsFragment fragobj = new RecentHubsFragment();
        fragobj.setArguments(bundle);
    }

    public void nearestHubs() {
        //searchByNameButton.setEnabled(true);
        /*BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                r = gson.fromJson(result, SearchHubResponse.class);

                hubsNearText.setText("Hubs Near You");
                HubsListAdapter hubsListAdapter = new HubsListAdapter(getApplicationContext(), r.result, callback);
                hubsList.setAdapter(hubsListAdapter);
                hubsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });
        backgroundWorker.execute("nearestHubs", hubSingleton.getUserID(),
                String.valueOf(globalLocation.getLatitude()), String.valueOf(globalLocation.getLongitude()));*/
        Bundle bundle = new Bundle();
        bundle.putString("userId", hubSingleton.getUserID());
        bundle.putString("latitude", String.valueOf(globalLocation.getLatitude()));
        bundle.putString("longitude", String.valueOf(globalLocation.getLongitude()));
        NearestHubsFragment fragobj = new NearestHubsFragment();
        fragobj.setArguments(bundle);
    }

    public void searchByName(View view) {
        /*searchByLocationButton.setEnabled(true);
        searchByLocationButton.setPressed(false);
        searchByNameButton.setEnabled(false);
        searchByNameButton.setPressed(true);*/
        hubsNearText.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        enterHub.setVisibility(View.VISIBLE);
        recentHubs();
    }

    public void searchByLocation(View view) {
        /*searchByLocationButton.setEnabled(false);
        searchByLocationButton.setPressed(true);
        searchByNameButton.setEnabled(true);
        searchByNameButton.setPressed(false);*/
        searchButton.setVisibility(View.GONE);
        enterHub.setVisibility(View.GONE);
        hubsNearText.setText("loading...");
        hubsNearText.setVisibility(View.VISIBLE);
        r.result.clear();
        hubsListAdapter.notifyDataSetChanged();

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchHub.this);
        builder.setMessage("We Need To Access Your Location Momentarily, Allow Us To Use Your GPS Location?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //searchByNameButton.setEnabled(false);
                        if (set)
                            nearestHubs();
                        else
                            configureLocation();

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        searchByName(new View(getApplicationContext()));
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void configureLocation() {
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (!set) {
                    globalLocation = location;
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchHub.this);
                    builder.setMessage("Your General Location Has Been Saved. You Can Disable Your GPS Now.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //searchByNameButton.setEnabled(true);
                                }
                            })
                            .setNegativeButton("CANCEL", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //searchByNameButton.setEnabled(true);
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    set = true;
                    nearestHubs();
                }
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

}
