package csusm.parkingspot;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class CheckinDirectActivity extends AppCompatActivity {

    int lotValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_direct);


        //set values and settings for the scroll wheels
        NumberPicker lotPicker = (NumberPicker) findViewById(R.id.lotPicker);
        String[] alphabet = {"A","B","C","D","E","F",}; //TODO
        lotPicker.setMinValue(0);
        lotPicker.setMaxValue(5);
        lotPicker.setDisplayedValues(alphabet);
        lotPicker.setWrapSelectorWheel(false);
        lotValue = lotPicker.getValue(); // 0 is A, 1 is B, and so on
        lotPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                lotValue = newVal;
            }
        });

        NumberPicker spotPicker = (NumberPicker) findViewById(R.id.spotPicker);
        spotPicker.setMinValue(301);
        spotPicker.setMaxValue(330);
        spotPicker.setWrapSelectorWheel(false);


        Button directCancelBtn = (Button) findViewById(R.id.directCancelBtn);
        directCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                directCancelBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> directCancelBtn.setAlpha((float) 1), 500);
                Intent intent = new Intent(CheckinDirectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}