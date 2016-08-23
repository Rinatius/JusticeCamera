package com.example.justicecamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddViolationLocation extends FragmentActivity implements OnMapReadyCallback {
    static final String LAT = "lat";
    static final String LONGT = "longt";
    String lat = "";
    String longt = "";
    Button buttonAddLocation;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_violation_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;
        buttonAddLocation = (Button) findViewById(R.id.buttonAddLocation);
        buttonAddLocation.setEnabled(false);
        Toast showMessage = Toast.makeText(getApplicationContext(),
                getString(R.string.choose_location), Toast.LENGTH_LONG);
        showMessage.show();

        // Add a marker in Sydney and move the camera
        LatLng bishkek = new LatLng(42.8709181, 74.6144781);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bishkek));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 12));

        UiSettings set = mMap.getUiSettings();
        set.setZoomControlsEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (!lat.equals("")) {
                    mMap.clear();
                }

                lat = Double.toString(latLng.latitude);
                longt = Double.toString(latLng.longitude);
                LatLng position = new LatLng(latLng.latitude, latLng.longitude);
                Marker location = mMap.addMarker(new MarkerOptions().position(position));
                buttonAddLocation.setEnabled(true);
            }
        });

        buttonAddLocation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                 Intent i = new Intent(AddViolationLocation.this, MainActivity.class);
                 i.putExtra(LAT, lat);
                 i.putExtra(LONGT, longt);
                 setResult(RESULT_OK, i);
                 finish();
            }
        });
    }
}
