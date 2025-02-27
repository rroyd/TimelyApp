package com.example.ex2.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.example.ex2.models.Job;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JobDataService extends Service {
    private ArrayList<Job> jobs;

    public JobDataService() {
        jobs = new ArrayList<>();
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public void searchJobsWithQuery(String query) {
        if (!query.isEmpty()) {
            URL url;
            String urlStr = "https://ec.europa.eu/esco/api/search?text=" + query;
            BufferedReader reader = null;
            HttpURLConnection request = null;

            StrictMode.ThreadPolicy policy =  new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                url = new URL(urlStr);
            }
            catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            try {
                request = (HttpURLConnection) url.openConnection();
                request.connect();

                int responseCode = request.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;


                    while((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray results = jsonResponse.getJSONObject("_embedded").getJSONArray("results");

                    Log.d("MESSAGE", results.toString());

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String title = result.getString("title");
                        Job job = new Job(title);
                        Log.d("JOB",title);
                        jobs.add(job);
                    }
                } else {
                    throw new Exception("Couldn't fetch jobs.");
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
            if (request != null) {
                request.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}