package csusm.parkingspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap searchMap;
    GoogleApiClient mGoogleApiClient;
    Spot resultSpot;
    ProgressDialog progressDialog;
    SupportMapFragment mapFragment;

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    final List<Spot> data=new ArrayList<>();
    private boolean finishedSpotTask=false;


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SearchActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
         //Obtain the SupportMapFragment and get notified when the map is ready to be used.


        progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        // show a loading screen
        progressDialog.show();

        new SpotTask().execute();

        Button checkinBtn = (Button) findViewById(R.id.checkinSearchBtn);
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                //Runnable replaced with lambda expression
                //does not work under Java 1.7 and less, so be careful on that
                handler.postDelayed(() -> checkinBtn.setAlpha((float) 1), 500);
                new CheckinExecution().execute();
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
            }
        });

        Button otherBtn = (Button) findViewById(R.id.otherBtn);
        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otherBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                //Runnable replaced with lambda expression
                //does not work under Java 1.7 and less, so be careful on that
                handler.postDelayed(() -> otherBtn.setAlpha((float) 1), 500);
                new SpotTask().execute();
                mapFragment.getMapAsync(SearchActivity.this);
            }
        });


//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
//                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
//                    .addApi(LocationServices.API)
//                    .build();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        searchMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lotB = new LatLng(resultSpot.location_x, resultSpot.location_y);
        searchMap.addMarker(new MarkerOptions().position(lotB).title(resultSpot.lot + " - " + resultSpot.spot));
        //searchMap.moveCamera(CameraUpdateFactory.newLatLng(lotB));
        searchMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lotB,19));
        searchMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            searchMap.setMyLocationEnabled(true);
        }
        searchMap.setBuildingsEnabled(false);
        searchMap.getUiSettings().setTiltGesturesEnabled(false);

        
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
                    resultSpot = data.get((int)(Math.random() * data.size()));
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(SearchActivity.this);
                    progressDialog.dismiss();


                }

                catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }


    public class CheckinExecution extends AsyncTask<Void, Void, Boolean> {

        CheckinExecution() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/executeCheckin.php?lot=" + resultSpot.lot + "&spot="+ resultSpot.spot +"&studentid="+settings.getInt("sessionID", -1);
                URL url = new URL(link);

                conn = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                inputLine = in.readLine();

                in.close();

                if(inputLine.equals("Success")) {
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {

            }

        }


    }

}
