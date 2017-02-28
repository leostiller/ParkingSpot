package csusm.parkingspot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        System.out.println("ProfileActivity SessionID is "+settings.getInt("sessionID",-1));

        Button signOutBtn = (Button) findViewById(R.id.signOutBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                signOutBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                //Runnable replaced with lambda expression
                //does not work under Java 1.7 and less, so be careful on that
                handler.postDelayed(() -> signOutBtn.setAlpha((float) 1), 500);

                SharedPreferences.Editor editor = settings.edit();
                editor.remove("sessionID");
                editor.apply();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                finish();
            }
        });

    }
}
