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

public class CheckinDirectLotActivity extends AppCompatActivity {

    int lotValue;
    StringBuffer stringBuffer;
    private CheckinDirectLotTask mCheckinLotTask = null;
    NumberPicker lotPicker;
    String inputLine;
    String[] alphabet;
    String lotAbsolute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct_lot);

        mCheckinLotTask = new CheckinDirectLotTask();
        mCheckinLotTask.execute((Void) null);

        //set values and settings for the scroll wheels
        lotPicker = (NumberPicker) findViewById(R.id.lotPicker);


        Button nextBtn = (Button) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckinDirectLotActivity.this, CheckinDirectSpotActivity.class);
                Bundle params = new Bundle();
                params.putString("lot",lotAbsolute);
                intent.putExtras(params);
                startActivity(intent);
            }
        });

        Button directCancelBtn = (Button) findViewById(R.id.directCancelBtn);
        directCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directCancelBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> directCancelBtn.setAlpha((float) 1), 500);
                Intent intent = new Intent(CheckinDirectLotActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class CheckinDirectLotTask extends AsyncTask<Void, Void, Boolean> {

        CheckinDirectLotTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/getAllLots.php";
                URL url = new URL(link);

                conn = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                stringBuffer = new StringBuffer("");
                inputLine = in.readLine();
                alphabet = inputLine.split(",");

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
            mCheckinLotTask = null;
            if (success) {
                lotPicker.setMinValue(0);
                lotPicker.setMaxValue(alphabet.length-1);
                lotPicker.setDisplayedValues(alphabet);
                lotPicker.setWrapSelectorWheel(false);
                lotValue = lotPicker.getValue(); // 0 is A, 1 is B, and so on
                lotAbsolute = alphabet[lotValue];
                lotPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        lotValue = newVal;
                        lotAbsolute = alphabet[lotValue];
                    }
                });

            }

        }


    }
}