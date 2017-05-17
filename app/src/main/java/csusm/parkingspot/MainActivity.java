package csusm.parkingspot;

import android.app.ProgressDialog;
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

    int studentID;
    int studentSpot;
    char studentLot;
    ProgressDialog progressDialog;
    GetStudentSpot mGetStudentSpot;
    Button searchBtn;
    Button checkinDirectBtn;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define Progress Dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //test and print session id
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        System.out.println("MainActivity SessionID is " + settings.getInt("sessionID", -1));
        studentID = settings.getInt("sessionID", -1);
        searchBtn = (Button) findViewById(R.id.findBtn);
        mGetStudentSpot = new GetStudentSpot();
        mGetStudentSpot.execute((Void) null);



        //set text of the profile button and button availability in dependence of session status
        Button profileBtn = (Button) findViewById(R.id.profileBtn);
        checkinDirectBtn = (Button) findViewById(R.id.checkinDirectBtn);

        if (settings.getInt("sessionID", -1) == -1) {
            profileBtn.setText("Log in");
            searchBtn.setEnabled(false);
            checkinDirectBtn.setEnabled(false);
            //TODO: give message when tried to click

        } else {
            profileBtn.setText("Log out");
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

                if (studentSpot==0) {
                    startActivity(new Intent(MainActivity.this, CheckinDirectLotActivity.class));
                    finish();
                } else {
                    Intent intent = new Intent(MainActivity.this, CheckoutActivity.class);
                    Bundle params = new Bundle();
                    params.putChar("studentLot",studentLot);
                    params.putInt("studentSpot",studentSpot);
                    intent.putExtras(params);
                    startActivity(intent);
                    finish();
                }
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
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("sessionID");
                            editor.apply();
                            startActivity(new Intent(MainActivity.this,MainActivity.class));
                            finish();
                }
            }
        });
    }

public class GetStudentSpot extends AsyncTask<Void, Void, Boolean> {

    GetStudentSpot() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpURLConnection conn = null;
        try {
            String link = "http://csusm-parkingspot.000webhostapp.com/getStudentSpot.php?studentid=" + studentID;
            URL url = new URL(link);

            conn = (HttpURLConnection) url.openConnection();


            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            inputLine = in.readLine();
            String[] spotKey = inputLine.split(",");
            if(inputLine.equals("none")) {
                studentSpot = 0;
                studentLot = 0;
            } else {
                studentSpot = Integer.valueOf(spotKey[0]);
                studentLot = spotKey[1].charAt(0);
            }
            in.close();

            if (inputLine!="") {
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
        mGetStudentSpot = null;
        progressDialog.dismiss();
        if (success) {


            if (studentSpot==0) {
                checkinDirectBtn.setText(R.string.check_in);
                checkinDirectBtn.setEnabled(true);
                searchBtn.setEnabled(true);
                //TODO: give message when tried to click

            } else {
                checkinDirectBtn.setText("Check-Out");
                searchBtn.setEnabled(false);
                checkinDirectBtn.setEnabled(true);
            }

        } else {
            System.err.println("Something went wrong");
        }
    }
}
}


