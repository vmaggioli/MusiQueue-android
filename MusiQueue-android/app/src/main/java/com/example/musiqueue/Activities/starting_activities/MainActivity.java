package com.example.musiqueue.Activities.starting_activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.musiqueue.HelperClasses.HubSingleton;
import com.example.musiqueue.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignInTest";
    private TextView createName;
    private EditText usernameText;
    private Button usernameButton;
    private HubSingleton appState;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private GoogleSignInAccount acct;

    /*
        UPON ENTERING THIS ACTIVITY, WE NEED TO CHECK IF THE USER'S
        DEVICE ID IS ALREADY PRESENT WITHIN OUR DATABASE RECORDS. IF
        IT IS THEN WE NEED TO TELL THE BACKEND TO UPDATE THE USERNAME
        INSTEAD OF CREATING A NEW USERNAME WITH THAT SAME ID.
     */

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get global application for global variables
        appState = HubSingleton.getInstance();
        final SharedPreferences settings = getPreferences(MODE_PRIVATE);
        initView();
    }

    protected void initView() {

        // set textView and editText
        createName = (TextView) findViewById(R.id.create_name_text_view);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(final GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            if (acct != null) {
                appState.setUserAccount(acct);

                // TODO: Not sure if this code is still needed, but saved it just in case
                final SharedPreferences settings = getPreferences(MODE_PRIVATE);
                final SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", appState.getUsername());
                editor.apply();

                startActivity(new Intent(MainActivity.this, GettingStarted.class));
                finish();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {

    }
}