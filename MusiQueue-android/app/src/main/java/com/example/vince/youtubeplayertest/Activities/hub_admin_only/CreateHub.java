package com.example.vince.youtubeplayertest.Activities.hub_admin_only;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vince.youtubeplayertest.Activities.BackgroundWorker;
import com.example.vince.youtubeplayertest.Activities.helper_classes.HubSingleton;
import com.example.vince.youtubeplayertest.Activities.helper_classes.JoinHubResponse;
import com.example.vince.youtubeplayertest.R;
import com.google.gson.Gson;

import java.util.List;

public class CreateHub extends AppCompatActivity  {
    EditText hubNameText;
    EditText passPin;
    Button createHubButton;
    HubSingleton appState;
    LocationManager locationmanager;
    LocationListener locationListener;
    Location globalLocation;
    static boolean set = false;
    static boolean runningThrough = false;

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
        setContentView(R.layout.activity_create_hub);

        globalLocation = null;
        // set editTexts
        hubNameText = (EditText) findViewById(R.id.hub_name);
        passPin = (EditText) findViewById(R.id.pass_pin);

        // find current app state for super globals
        appState = HubSingleton.getInstance();

        // set button
        createHubButton = (Button) findViewById(R.id.create_hub_button);
        createHubButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hubNameText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "Must Have Hub Name",
                            Toast.LENGTH_LONG).show();
                    return;
                } else if (passPin.getText().length() != 4) {
                    Toast.makeText(getApplicationContext(), "Pass Pin Must Be 4 Digits",
                            Toast.LENGTH_LONG).show();
                    return;

                }

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateHub.this);
                builder.setMessage("Would You Like To Allow Users To Search Your Hub Based On " +
                        "Your Current Location?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                configureLocation();

                                boolean gps_enabled = false;
                                try {
                                    gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                } catch(Exception ex) {}
                                if (gps_enabled)
                                    setTurnedLocationServicesOn(true);
                                else
                                    setTurnedLocationServicesOn(false);

                                if (!isTurnedLocationServicesOn()) {
                                    promptUserTurnOnLocationServices();
                                    return;
                                }
                                startCreate();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                createHubButton.setEnabled(false);
                                startCreate();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
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

    public void startCreate() {
        BackgroundWorker backgroundWorker = new BackgroundWorker(new BackgroundWorker.AsyncResponse() {
            @Override
            public void processFinish(String result) {
                Gson gson = new Gson();
                JoinHubResponse r = gson.fromJson(result, JoinHubResponse.class);
                if (r.error) {
                    connectError(r.errorCode, r.errorMessage);
                } else {
                    connectSuccess(r.getHubId());
                }
            }
        });

        globalLocation = getLastKnownLocation();

        if (globalLocation == null)
            backgroundWorker.execute("createHub", hubNameText.getText().toString(),
                    passPin.getText().toString(), appState.getUserID(),
                    appState.getUsername(), "0", "0");
        else
            backgroundWorker.execute("createHub", hubNameText.getText().toString(),
                    passPin.getText().toString(), appState.getUserID(), appState.getUsername(),
                    String.valueOf(globalLocation.getLatitude()),
                    String.valueOf(globalLocation.getLongitude()));
        appState.setHubName(hubNameText.getText().toString());
    }

    public void promptUserTurnOnLocationServices() {
        runningThrough = true;
        locationmanager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(CreateHub.this);
            dialog.setMessage("Your location services aren't enabled.  " +
                    "Would you like to turn them on?");
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivity
                            (new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }

        try {
            gps_enabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        if (gps_enabled)
            setTurnedLocationServicesOn(true);
        else
            setTurnedLocationServicesOn(false);
    }

    private void connectSuccess(Integer hubId) {
        appState = HubSingleton.getInstance();

        Log.d("", "Created hub successfully!");
        appState.setHubId(hubId);

        final Intent i = new Intent(CreateHub.this, QueueActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void connectError(String errorCode, String errorMessage) {
        // TODO?
        Toast.makeText(this, errorCode + " - " + errorMessage, Toast.LENGTH_LONG).show();
        finish();
    }

    private void configureLocation() {
        locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (!set) {
                    globalLocation = location;
                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateHub.this);
                    builder.setMessage("Your General Location Has Been Saved. You Can Disable Your GPS Now.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    createHubButton.setEnabled(false);
                                    startCreate();
                                }
                            })
                            .setNegativeButton("CANCEL", new AlertDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    createHubButton.setEnabled(false);
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    set = true;
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
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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
            System.out.println("second: " + locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER));}*/
    }
}
