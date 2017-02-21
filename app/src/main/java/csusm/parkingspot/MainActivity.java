package csusm.parkingspot;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.*;

public class MainActivity extends AppCompatActivity {

    private static final String url = "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3160067";
    private static final String user = "sql3160067";
    private static final String pass = "qRFt7K8WKF";
//    Statement st;
//    ResultSet rs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new DBConnection().execute();



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


    protected void startMapActivity(View View) {
        final Button mapBtn = (Button) findViewById(R.id.mapBtn);
        mapBtn.setAlpha((float)0.5);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mapBtn.setAlpha((float) 1);
            }
        }, 500);
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    // TODO: move out into own class
    // (maybe like DBConnector.java)
    // class has to be async task
    // implement own methods for different statements like lot, spot, ... (via arguments)
    private class DBConnection extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {

            Statement st = null;

            try {
                System.out.println("-------- Establish MySQL JDBC Connection ------");
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(url, user, pass);
                System.out.println("-------- MySQL JDBC Connection established ------");


                st = con.createStatement();
                String sql = "SELECT * FROM parking_lot";
                final ResultSet rs = st.executeQuery(sql);
                while(rs.next()) {
                    int lotID = rs.getInt("Lot_ID");
                    String lotName = rs.getString("Lot_Name");
                    int capacity = rs.getInt("Capacity");
                    double locX = rs.getDouble("Location_X");
                    double locY = rs.getDouble("Location_Y");
                    System.out.println(lotID + "\t" + lotName +
                            "\t" + capacity + "\t" + locX +
                            "\t" + locY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }



            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }
    }

}
