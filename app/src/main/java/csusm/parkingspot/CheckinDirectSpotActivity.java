package csusm.parkingspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CheckinDirectSpotActivity extends AppCompatActivity {

    int spotValue;
    private CheckinDirectSpotTask mCheckinSpotTask = null;
    private CheckinExecution mCheckinExecution = null;
    NumberPicker spotPicker;
    String[] spotList;
    int spotAbsolute;
    public String selectedLot;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CheckinDirectSpotActivity.this,CheckinDirectLotActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct_spot);

        //define Progress Dialog
        progressDialog = new ProgressDialog(CheckinDirectSpotActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");

        // show a loading screen
        progressDialog.show();

        selectedLot = getIntent().getExtras().getString("lot");

        mCheckinSpotTask = new CheckinDirectSpotTask();
        mCheckinSpotTask.execute((Void) null);

        //set values and settings for the scroll wheels
        spotPicker = (NumberPicker) findViewById(R.id.spotPicker);
        setNumberPickerTextColor(spotPicker, Color.WHITE);


        Button checkinBtn  = (Button) findViewById(R.id.checkinBtn);
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> checkinBtn.setAlpha((float) 1), 500);
                mCheckinExecution = new CheckinExecution();
                mCheckinExecution.execute((Void) null);
                Intent intent = new Intent(CheckinDirectSpotActivity.this, MainActivity.class);
                startActivity(intent);
                // TODO: Add Toast message to MainActivity for success show
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

                String inputLine;
                inputLine = in.readLine();
                spotList = inputLine.split(",");

                // TODO: catch the case that no values are returned

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
                        spotAbsolute = Integer.valueOf(spotList[spotValue]);
                        System.out.println("Absolute is " + spotAbsolute);

                    }
                });

                //dismiss loading screen
                progressDialog.dismiss();

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
                String link = "http://csusm-parkingspot.000webhostapp.com/executeCheckin.php?lot=" + selectedLot + "&spot="+spotAbsolute+"&studentid="+settings.getInt("sessionID", -1);
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
            mCheckinExecution = null;
            if (success) {

            }

        }


    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    e.printStackTrace();
                }
                catch(IllegalAccessException e){
                    e.printStackTrace();
                }
                catch(IllegalArgumentException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


}

