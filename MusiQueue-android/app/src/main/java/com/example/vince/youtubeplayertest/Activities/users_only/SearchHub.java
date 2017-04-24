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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsFragmentAdapter;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubsListItem;
import com.example.vince.youtubeplayertest.R;

import java.util.List;
import java.util.Vector;

public class SearchHub extends AppCompatActivity {
    RecyclerView hubsList;
    TextView hubsNearText;
    Vector<HubsListItem> hubs;
    HubSingleton hubSingleton;
    Location globalLocation;
    boolean set;
    LocationManager locationmanager;
    LocationListener locationListener;

    FragmentPagerAdapter adapterViewPager;

    static boolean dialogShown = false;

    public boolean isTurnedLocationServicesOn() {
        return turnedLocationServicesOn;
    }

    public void setTurnedLocationServicesOn(boolean turnedLocationServicesOn) {
        this.turnedLocationServicesOn = turnedLocationServicesOn;
    }

    private boolean turnedLocationServicesOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_hub);

        final ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new HubsFragmentAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    promptUserTurnOnLocationServices();
                    if (!isTurnedLocationServicesOn())
                        vpPager.setCurrentItem(1);
                    else {
                        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // initialize views
        hubsNearText = (TextView) findViewById(R.id.hubs_near_you_text);
        globalLocation = null;
        set = false;

        hubsList = (RecyclerView) findViewById(R.id.hubs_list);
        hubs = new Vector<>();
        hubSingleton = HubSingleton.getInstance();
    }

    public void promptUserTurnOnLocationServices() {
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private Location getLastKnownLocation() {

        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        List<String> providers = locationmanager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = null;
            if ( ContextCompat.checkSelfPermission(this,
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

}
