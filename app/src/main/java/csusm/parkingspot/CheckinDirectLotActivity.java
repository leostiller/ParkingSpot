package csusm.parkingspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class CheckinDirectLotActivity extends AppCompatActivity {

    int lotValue;
    StringBuffer stringBuffer;
    private CheckinDirectLotTask mCheckinLotTask = null;
    NumberPicker lotPicker;
    String inputLine;
    String[] alphabet;
    String lotAbsolute;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CheckinDirectLotActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct_lot);

        //define Progress Dialog
        progressDialog = new ProgressDialog(CheckinDirectLotActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        // show a loading screen
        progressDialog.show();

        mCheckinLotTask = new CheckinDirectLotTask();
        mCheckinLotTask.execute((Void) null);

        //set values and settings for the scroll wheels
        lotPicker = (NumberPicker) findViewById(R.id.lotPicker);
        setNumberPickerTextColor(lotPicker, Color.WHITE);


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



    }

    public class CheckinDirectLotTask extends AsyncTask<Void, Void, Boolean> {

        CheckinDirectLotTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn1 = null;
            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/getAllLots.php";
                URL url = new URL(link);



                conn1 = (HttpURLConnection) url.openConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(conn1.getInputStream()));

                stringBuffer = new StringBuffer("");
                inputLine = in.readLine();
                alphabet = inputLine.split(",");

                in.close();

                // TODO: catch the case that no values are returned

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn1.disconnect();
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

                //dismiss loading screen
                progressDialog.dismiss();

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