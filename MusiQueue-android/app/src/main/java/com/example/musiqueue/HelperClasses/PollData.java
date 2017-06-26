package com.example.musiqueue.HelperClasses;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Brian on 3/28/2017.
 */

public class PollData extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    HubSingleton hubSingleton = HubSingleton.getInstance();
    BackgroundWorker.AsyncResponse callback;
    StringBuilder result;

    public PollData() {
        super("PollData");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("entering into the onHandleIntent");
        int min = 0;
        int sec = 1;

        try {
            synchronized (this) {
                this.wait(((min * 60) + sec) * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finished waiting 30 seconds");

        ResultReceiver receiver = intent.getParcelableExtra("receiver");

        String urlBase = "http://52.14.50.251/api/";
        String urlEnd = "hubSongList.php";

        // setup and make the request
        try {
            URL url = new URL(urlBase + urlEnd);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("hubId", hubSingleton.getHubId().toString())
                    .appendQueryParameter("phoneId", hubSingleton.getUserID());
            String post_data = builder.build().getEncodedQuery();
            bw.write(post_data);
            bw.flush();
            bw.close();
            os.close();
            httpURLConnection.connect();

            // CHECK RESPONSE CODE FOR ANY ERRORS
            int responseCode = httpURLConnection.getResponseCode();
            Log.d("response: ", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line + "\n");
                }
                httpURLConnection.disconnect();

                // Pass data to onPostExecute method

            } else {
                httpURLConnection.disconnect();
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("result", result.toString());
        receiver.send(0, bundle);
    }
}
