package com.example.vince.youtubeplayertest.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;

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

        String testUrl = "http://52.14.50.251/api/backendTest.php";
        String addUserUrl = "http://52.14.50.251/api/addUser.php";
        String createHubUrl = "http://52.14.50.251/api/createHub.php";
        String joinHubUrl = "http://52.14.50.251/api/joinHub.php";
        String searchHubUrl = "http://52.14.50.251/api/searchHub.php";

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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        } else if (type.equals("addUser")) {
            try {
                String username = params[1];
                String userId = params[2];
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
                        .appendQueryParameter("userId", userId)
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        } else if (type.equals("searchHub")) {
            try {
                String hubName = params[1];
                Log.d("blah", hubName);
                URL url = new URL(searchHubUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("hubName", hubName);
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    ArrayList<String> names = new ArrayList<String>();
                    JSONObject json = new JSONObject(result.toString());
                    JSONArray jsonArray = json.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);
                        String hub_name = jObj.getString("hub_name");
                        names.add(hub_name);
                    }
                    // Pass data to onPostExecute method

                    return (names.toString().substring(1, names.toString().length()-1));

                } else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type.equals("createHub")) {
            try {
                String hubName = params[1];
                String passPin = params[2];
                String creatorId = params[3];
                Log.d("blah", hubName);
                URL url = new URL(createHubUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Date date = new Date(System.currentTimeMillis());
                // TODO: send a parameter for the user's phone id
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("hubName", hubName)
                        .appendQueryParameter("passPin", passPin)
                        .appendQueryParameter("creatorId", creatorId);
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    httpURLConnection.disconnect();
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            }
        } else if (type.equals("joinHub")) {
            try {
                String hubName = params[1];
                String hubPin = params[2];
                String phoneId = params[3];
                String username = params[4];
                Log.d("blah", hubName);
                URL url = new URL(joinHubUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("hubName", hubName)
                        .appendQueryParameter("hubPin", hubPin)
                        .appendQueryParameter("phoneID", phoneId)
                        .appendQueryParameter("username", username);
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
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line + "\n");
                    }
                    httpURLConnection.disconnect();

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
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
        // TODO: remove the debug print statements from each condition
        if (type.equals("test")) {
            Log.d("\t\t\tresults: ", result);
            Intent intent = new Intent(context, BackendTestActivity.class);
            intent.putExtra("result", result);
            context.startActivity(intent);
        } else if (type.equals("addUser")) {
            Log.d("\t\t\tresults: ", result);
        } else if (type.equals("createHub")) {
            Log.d("\t\t\tresults: ", result);
        } else if (type.equals("joinHub")) {
            Log.d("\t\t\tresults: ", result);
        } else if (type.equals("searchHub")) {
            Log.d("\t\t\tresults: ", result);
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
