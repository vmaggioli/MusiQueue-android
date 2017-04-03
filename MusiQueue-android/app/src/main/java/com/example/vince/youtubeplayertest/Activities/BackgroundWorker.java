package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Brian on 2/16/2017.
 */

/* ASYNCTASK<X, Y, Z>
 * X IS THE TYPE FOR A LIST OF PARAMETERS THAT IS SENT TO 'doInBackground()'
 * Y IS THE TYPE FOR A LIST OF PARAMETERS THAT IS SENT TO 'onProgressUpdate()'
 * Z IS THE TYPE FOR A LIST OF PARAMETERS THAT IS SENT TO 'onPostExecute()'
 *
 * 'doInBackground()' can access any declared variables in 'onPreExecute()'
 * 'onProgressUpdate()' and 'onPostExecute()' can access any declared variables in 'doInBackground()'
 */
public class BackgroundWorker extends AsyncTask<String, Void, String> {
    // ALLOW ACCESS TO THE ACTIVITY THAT STARTED THE TASK
    Context context;
    public BackgroundWorker(Context context) {
        this.context = context;
    }


    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;
    public BackgroundWorker(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    String type;

    // TODO: NONE OF THE FOLLOWING FUNCTIONS SHOULD EVER BE CALLED MANUALLY FROM AN ACTIVITY
    // TODO: BACKGROUNDWORKER.EXECUTE() SHOULD BE CALLED AND THESE WILL RUN AUTOMATICALLY

    @Override
    protected String doInBackground(String... params) {
        type = params[0];

        System.out.println("background type: " + type);
        String urlBase = "https://musiqueue.com/api/";
        String urlEnd = type + ".php";

        Vector<String> paramNames = new Vector<>();

        switch(type) {
            case "test":
                urlEnd = "backendTest.php";
                paramNames.add("name");
                paramNames.add("addr");
                break;
            case "searchHub":
                paramNames.add("hubName");
                break;
            case "createHub":
                paramNames.add("hubName");
                paramNames.add("hubPin");
                paramNames.add("phoneId");
                paramNames.add("username");
                break;
            case "joinHub":
                paramNames.add("hubName");
                paramNames.add("hubPin");
                paramNames.add("phoneId");
                paramNames.add("username");
                break;
            case "hubSongList":
                paramNames.add("hubId");
                paramNames.add("phoneId");
                break;
            case "addSong":
                paramNames.add("hubId");
                paramNames.add("phoneId");
                paramNames.add("songId");
                paramNames.add("songTitle");
                break;
            case "recentHubs":
                paramNames.add("phoneId");
            case "removeSong":
                paramNames.add("hubId");
                paramNames.add("phoneId");
                paramNames.add("songId");
                break;
            case "voteUpSong":
            case "voteDownSong":
                paramNames.add("hubId");
                paramNames.add("phoneId");
                paramNames.add("songId");
                break;

        }

        // setup and make the request
        try {
            URL url = new URL(urlBase + urlEnd);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            Uri.Builder builder = new Uri.Builder();
            int i = 1;
            Log.d("paramNames: ", paramNames.toString());
            for (String p: paramNames) {
                builder.appendQueryParameter(p, params[i++]);
            }
            String post_data = builder.build().getEncodedQuery();
            bw.write(post_data);
            bw.flush();
            bw.close();
            os.close();
            conn.connect();

            // CHECK RESPONSE CODE FOR ANY ERRORS
            int responseCode = conn.getResponseCode();
            Log.d("response: ", String.valueOf(responseCode));
            if (responseCode == conn.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line + "\n");
                }
                conn.disconnect();

                // Pass data to onPostExecute method
                return (result.toString());

            } else {
                conn.disconnect();
                return ("error");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPreExecute() {
        // THIS IS FOR SETTING UP THE TASK
        // FOR EXAMPLE, SHOWING A PROGRESS BAR IN THE UI
    }

    @Override
    protected void onPostExecute(String result) {
        // RECEIVES THE RESULT FROM 'doInBackgroung()' AS ITS PARAMATER
        Log.d("\t\t\tresults: ", result);

        if(result.equals("error")) {
            // The request failed. Or something else bad happened. Either way, we shouldn't
            // call processFinish cause it'll probably crash the app.
            Log.e("", "Background Worker Failing.");
            return;
        }

        // check the json for an error message
        try {
            JSONObject json = new JSONObject(result);
            if(json.getBoolean("error")) {
                String errorCode = json.getString("errorCode");
                String errorMessage = json.getString("errorMessage");
                Log.e("Request error code: ", errorCode);
                Log.e("Request error message: ", errorMessage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: remove the debug print statements from each condition
        if (type.equals("test")) {
            Log.d("\t\t\tresults: ", result);
            Intent intent = new Intent(context, BackendTestActivity.class);
            intent.putExtra("result", result);
            context.startActivity(intent);
        } else {
            delegate.processFinish(result);
        }

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // UPDATES THE USER (UI) WITH ANY USEFUL INFORMATION
        // ABOUT THIS ASYNC TASK RUNNING IN THE BACKGROUND
        super.onProgressUpdate(values);
    }

}


