package csusm.parkingspot;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap searchMap;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
         //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
//                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
//                    .addApi(LocationServices.API)
//                    .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        searchMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lotB = new LatLng(33.126809, -117.162551);
        searchMap.addMarker(new MarkerOptions().position(lotB).title("Lot B - 352"));
        //searchMap.moveCamera(CameraUpdateFactory.newLatLng(lotB));
        searchMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lotB,19));
        searchMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            searchMap.setMyLocationEnabled(true);
        }
        searchMap.setBuildingsEnabled(false);
        searchMap.getUiSettings().setTiltGesturesEnabled(false);

    }
}
