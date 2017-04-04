package csusm.parkingspot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckinDirectSpotActivity extends AppCompatActivity {

    int spotValue;
    private CheckinDirectSpotTask mCheckinSpotTask = null;
    NumberPicker spotPicker;
    String inputLine;
    String[] spotList;
    int spotAbsolute;
    String selectedLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct_spot);


        selectedLot = getIntent().getExtras().getString("lot");

        mCheckinSpotTask = new CheckinDirectSpotTask();
        mCheckinSpotTask.execute((Void) null);

        //set values and settings for the scroll wheels
        spotPicker = (NumberPicker) findViewById(R.id.spotPicker);



        Button directCancelBtn = (Button) findViewById(R.id.directCancelBtn);
        directCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directCancelBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> directCancelBtn.setAlpha((float) 1), 500);
                Intent intent = new Intent(CheckinDirectSpotActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class CheckinDirectSpotTask extends AsyncTask<Void, Void, Boolean> {

        CheckinDirectSpotTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/getSpots.php?lot="+selectedLot;
                URL url = new URL(link);

                conn = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));


                inputLine = in.readLine();
                spotList = inputLine.split(",");



                in.close();

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCheckinSpotTask = null;
            if (success) {
                spotPicker.setMinValue(0);
                spotPicker.setMaxValue(spotList.length-1);
                spotPicker.setDisplayedValues(spotList);
                spotPicker.setWrapSelectorWheel(false);
                spotValue = spotPicker.getValue();
                spotAbsolute = Integer.parseInt(spotList[spotValue]);
                spotPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        spotValue = newVal;
                        spotAbsolute = Integer.getInteger(spotList[spotValue]);
                        System.out.println("Absolute is " + spotAbsolute);

                    }
                });

            }

        }


    }
}