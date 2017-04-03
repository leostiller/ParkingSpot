package csusm.parkingspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
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

public class CheckinDirectActivity extends AppCompatActivity {

    int lotValue;
    StringBuffer stringBuffer;
    private CheckinDirectTask mCheckinTask = null;
    List<String> lotList;
    NumberPicker lotPicker;
    String inputLine;
    String[] alphabet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct);

        mCheckinTask = new CheckinDirectTask();
        mCheckinTask.execute((Void) null);

        //set values and settings for the scroll wheels
        lotPicker = (NumberPicker) findViewById(R.id.lotPicker);
        //String[] alphabet = {"A","B","C","D","E","F",}; //TODO
//        lotPicker.setMinValue(0);
//        lotPicker.setMaxValue(5);
//        String[] alphabet = new String[lotList.size()];
//        lotList.toArray(alphabet);
//        lotPicker.setDisplayedValues(alphabet);
//        lotPicker.setWrapSelectorWheel(false);
//        lotValue = lotPicker.getValue(); // 0 is A, 1 is B, and so on


        NumberPicker spotPicker = (NumberPicker) findViewById(R.id.spotPicker);
        spotPicker.setMinValue(301);
        spotPicker.setMaxValue(330);
        spotPicker.setWrapSelectorWheel(false);


        Button directCancelBtn = (Button) findViewById(R.id.directCancelBtn);
        directCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directCancelBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> directCancelBtn.setAlpha((float) 1), 500);
                Intent intent = new Intent(CheckinDirectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class CheckinDirectTask extends AsyncTask<Void, Void, Boolean> {

        CheckinDirectTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/getAllLots.php";
                URL url = new URL(link);

                conn = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                lotList = new ArrayList<String>();

                stringBuffer = new StringBuffer("");
                // for reading one line
                inputLine = in.readLine();
                alphabet = inputLine.split(",");
                // keep reading till readLine returns null
//                while ((inputLine = in.readLine()) != null) {
//                    // keep appending last line read to buffer
////                    stringBuffer.append(inputLine);
//                    lotList.add(inputLine);
//                    lotList.add("Test");
//                }


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
            mCheckinTask = null;
            if (success) {
                lotPicker.setMinValue(0);
                lotPicker.setMaxValue(alphabet.length-1);
                lotPicker.setDisplayedValues(alphabet);
                lotPicker.setWrapSelectorWheel(false);
                lotValue = lotPicker.getValue(); // 0 is A, 1 is B, and so on
                lotPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        lotValue = newVal;
                    }
                });

            }

        }


    }
}