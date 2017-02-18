package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.sql.Date;
import java.sql.Timestamp;

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
    public BackgroundWorker(Context context) { this.context = context; }
    String type;

    // TODO: NONE OF THE FOLLOWING FUNCTIONS SHOULD EVER BE CALLED MANUALLY FROM AN ACTIVITY
    // TODO: BACKGROUNDWORKER.EXECUTE() SHOULD BE CALLED AND THESE WILL RUN AUTOMATICALLY

    @Override
    protected String doInBackground(String... params) {
        type = params[0];
        String testUrl = "http://ec2-52-14-107-232.us-east-2.compute.amazonaws.com/backendTest.php";
        String addUserUrl = "http://ec2-52-14-107-232.us-east-2.compute.amazonaws.com/addUser.php";

        if (type.equals("test")) {
            try {
                String name = params[1];
                String addr = params[2];
                Log.d("blah", name + " " + addr);
                URL url = new URL(testUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("name", name)
                        .appendQueryParameter("addr", addr);
                String post_data = builder.build().getEncodedQuery();
                Log.d("post_data: ", post_data);
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        }
        else if (type.equals("addUser")) {
            try {
                String username = params[1];
                Log.d("blah", username);
                URL url = new URL(addUserUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Date date = new Date(System.currentTimeMillis());
                // TODO: send a parameter for the user's phone id
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("dateTime", String.valueOf(date));
                String post_data = builder.build().getEncodedQuery();
                Log.d("post_data: ", post_data);
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        // THIS IS FOR SETTING UP THE TASK
        // FOR EXAMPLE, SHOWING A PROGRESS BAR IN THE UI
    }
    @Override
    protected void onPostExecute(String result) {
        // RECEIVES THE RESULT FROM 'doInBackgroung()' AS ITS PARAMATER
        if (type.equals("test")) {
            Log.d("\t\t\tresults: ", result);
            Intent intent = new Intent(context, BackendTestActivity.class);
            intent.putExtra("result", result);
            context.startActivity(intent);
        }
        else if (type.equals("addUser")) {
            Log.d("\t\t\tresults: ", result);
        }
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        // UPDATES THE USER (UI) WITH ANY USEFUL INFORMATION
        // ABOUT THIS ASYNC TASK RUNNING IN THE BACKGROUND
        super.onProgressUpdate(values);
    }

}
