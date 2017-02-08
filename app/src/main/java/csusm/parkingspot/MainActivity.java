package csusm.parkingspot;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        }

    protected void startSearchActivity(View view) {
        final Button searchBtn = (Button) findViewById(R.id.findBtn);
        searchBtn.setAlpha((float) 0.5);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                searchBtn.setAlpha((float) 1);
            }
        }, 500);
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    protected void startCheckinDirectActivity(View View) {
        final Button checkinDirectBtn = (Button) findViewById(R.id.directCheckinBtn);
        checkinDirectBtn.setAlpha((float)0.5);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                checkinDirectBtn.setAlpha((float) 1);
            }
        }, 500);
        Intent intent = new Intent(this, CheckinDirectActivity.class);
        startActivity(intent);
    }

}
