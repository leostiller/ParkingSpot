package csusm.parkingspot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckoutActivity extends AppCompatActivity {

    char studentLot;
    int studentSpot;
    private CheckoutExecution mCheckoutExecution = null;
    ProgressDialog progressDialog;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CheckoutActivity.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        studentLot = getIntent().getExtras().getChar("studentLot");
        studentSpot = getIntent().getExtras().getInt("studentSpot");

        TextView spotField = (TextView) findViewById(R.id.spotValueView);
        spotField.setText(studentLot + " " + studentSpot);

        Button submitBtn = (Button) findViewById(R.id.submitCheckoutBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtn.setAlpha((float) 0.5);
                Handler handler = new Handler();
                handler.postDelayed(() -> submitBtn.setAlpha((float) 1), 500);
                //define Progress Dialog
                progressDialog = new ProgressDialog(CheckoutActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Loading...");
                // show a loading screen
                progressDialog.show();
                mCheckoutExecution = new CheckoutExecution();
                mCheckoutExecution.execute((Void) null);
            }
        });

    }

    public class CheckoutExecution extends AsyncTask<Void, Void, Boolean> {

        CheckoutExecution() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection conn = null;
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            try {
                String link = "http://csusm-parkingspot.000webhostapp.com/executeCheckout.php?lot=" + studentLot + "&spot="+studentSpot;
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
            mCheckoutExecution = null;
            if (success) {
                progressDialog.dismiss();
                startActivity(new Intent(CheckoutActivity.this,MainActivity.class));
                finish();
            }

        }


    }

}
