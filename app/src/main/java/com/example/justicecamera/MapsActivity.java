package com.example.justicecamera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    List<Violation> list;
    LatLng position;
    Violation violation;
    String latToSend = "";
    String longtToSend = "";
    String searchParameter = "status = 1";
    static final String LATMAP = "lat";
    static final String LONGTMAP = "longt";

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        list = new ArrayList<>();
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng current = marker.getPosition();
                latToSend = Double.toString(current.latitude);
                longtToSend = Double.toString(current.longitude);
                Intent i = new Intent(MapsActivity.this, VideoInfo.class);
                i.putExtra(LATMAP, latToSend);
                i.putExtra(LONGTMAP, longtToSend);
                startActivity(i);
            }
        });

        LatLng bishkek = new LatLng(42.8709181, 74.6144781);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bishkek));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bishkek, 12));

        Toast.makeText(getApplicationContext(), getString(R.string.go_to_video), Toast.LENGTH_LONG).show();
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

        load();
    }

    public void load() {

        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(searchParameter);
        Backendless.Data.of(Violation.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Violation>>() {
            @Override
            public void handleResponse(BackendlessCollection<Violation> foundViolation) {
                list = foundViolation.getData();
                if (list.size() != 0) {

                    String lat = "";
                    String lng = "";

                    // Iterating through all the locations stored
                    for (int i = 0; i < list.size(); i++) {
                        violation = list.get(i);
                        lat = violation.getLat();
                        lng = violation.getLongt();

                        double lat3 = Double.valueOf(lat).doubleValue();
                        double lng3 = Double.valueOf(lng).doubleValue();

                        position = new LatLng(lat3, lng3);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(position);
                        String snippet = violation.getCarMake() + " " + violation.getCarModel();
                        Marker location = mMap.addMarker(new MarkerOptions().position(position).title(violation.getName()).snippet(snippet));
                    }
                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });
    }
}
