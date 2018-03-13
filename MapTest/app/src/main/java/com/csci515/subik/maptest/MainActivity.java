package com.csci515.subik.maptest;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener {


    //location: longitude-->-97.0693
    //latitude-->47.9221
    GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Show error dialog if GoolglePlayServices not available
        if ( !isGooglePlayServicesAvailable( )) {
            finish();
        }

        setContentView(R.layout.activity_main);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager( ).findFragmentById( R.id.googleMap );
        googleMap = supportMapFragment.getMap();

        try {
            googleMap.setMyLocationEnabled( true );
            LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
            Criteria criteria = new Criteria( );
            String bestProvider = locationManager.getBestProvider( criteria, true );
            Location location = locationManager.getLastKnownLocation( bestProvider );
            if ( location != null ){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates( bestProvider, 20000, 0, this );

        }catch (SecurityException se){
            Toast.makeText(getApplicationContext(), "We have a Security Exception",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
       googleMap.clear();
        TextView locationTv = findViewById(R.id.latlongLocation);
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng)
                .title("Current Location")
                .snippet("You are around UND memorial union."));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
        locationTv.setText("Latitude:\t"+latitude+" ,\nLongitude:\t"+longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable( ) {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( this );
        if ( ConnectionResult.SUCCESS == status ) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog( status, this, 0 ).show( );
            return false;
        }
    }
}
