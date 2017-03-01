package csusm.parkingspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //test and print session id
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        System.out.println("MainActivity SessionID is " + settings.getInt("sessionID", -1));

        //set text of the profile button and button availability in dependence of session status
        Button profileBtn = (Button) findViewById(R.id.profileBtn);
        Button checkinDirectBtn = (Button) findViewById(R.id.checkinDirectBtn);
        Button searchBtn = (Button) findViewById(R.id.findBtn);

        if (settings.getInt("sessionID", -1) == -1) {
            profileBtn.setText("Log in");
            searchBtn.setEnabled(false);
            checkinDirectBtn.setEnabled(false);
            //TODO: give message when tried to click

        } else {
            profileBtn.setText("My Profile");
            searchBtn.setEnabled(true);
            checkinDirectBtn.setEnabled(true);
        }


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                //Runnable replaced with lambda expression
                //does not work under Java 1.7 and less, so be careful on that
                handler.postDelayed(() -> searchBtn.setAlpha((float) 1), 500);
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }
        });

        checkinDirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinDirectBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> checkinDirectBtn.setAlpha((float) 1), 500);

                startActivity(new Intent(MainActivity.this, CheckinDirectActivity.class));
            }
        });

        Button mapBtn = (Button) findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> mapBtn.setAlpha((float) 1), 500);

                startActivity(new Intent(MainActivity.this, MapActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> profileBtn.setAlpha((float) 1), 500);

                if (settings.getInt("sessionID", -1) == -1) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                }
            }
        });

    }


}
