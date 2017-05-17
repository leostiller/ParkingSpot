package csusm.parkingspot;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by lstiller on 5/2/2017.
 */

public class SpotFinder {
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private SpotTask mSpotTask = null;
    final List<Spot> data=new ArrayList<>();
    Spot resultSpot;
    private boolean finishedSpotTask=false;
    ProgressDialog progressDialog;
    SpotTask spotTask;

    SpotFinder() {
        finishedSpotTask=false;
    }

    public void executeSpotTask(){
        try {
            String res = new SpotTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean isFinished() {
        if(finishedSpotTask==false) {
            return false;
        } else {
            return true;
        }
    }

    public Spot getSpot() {
        resultSpot = data.get(0);
//            Spot resultSpot = data.get((int)(Math.random() * data.size()));
        return resultSpot;
    }

    private class SpotTask extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;


        @Override
        protected String doInBackground(String... params) {

            try {

                // Enter URL address where your json file resides
                // Even you can make call to php file which returns json data
                url = new URL("http://csusm-parkingspot.000webhostapp.com/getAllFreeSpots.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);

            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

        mSpotTask = null;

            try {

                JSONArray jArray = new JSONArray(result);

                // Extract data from json and store into ArrayList as class objects
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    Spot spotData = new Spot();
                    spotData.lot= json_data.getString("lot").charAt(0);
                    spotData.spot= json_data.getInt("spot");
                    spotData.location_x= json_data.getDouble("location_x");
                    spotData.location_y= json_data.getDouble("location_y");
                    data.add(spotData);
                }
                finishedSpotTask=true;
            }

            catch (JSONException e) {
                e.printStackTrace();
            }


            }

        }

    }
