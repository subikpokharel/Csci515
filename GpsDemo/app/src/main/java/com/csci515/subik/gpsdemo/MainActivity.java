package com.csci515.subik.gpsdemo;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use the LocationManager class to obtain GPS locations.
        LocationManager mlocManager =
                (LocationManager) getSystemService( Context.LOCATION_SERVICE );
         //mlocListener = null;
        try {
            LocationListener mlocListener = new MyLocationListener( );
            mlocManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, mlocListener );
        }catch (SecurityException se){
            Toast.makeText(getApplicationContext(),"We have a security exception"
                    ,Toast.LENGTH_LONG).show();
        }

    }

    // Class My Location Listener
    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged( Location loc ) {
            String Text = "My current location is\n  " +
                    "Latitude = " + loc.getLatitude( ) +
                    "\n  Longitude = " + loc.getLongitude( );
            Toast.makeText( getApplicationContext( ),
                    Text, Toast.LENGTH_LONG ).show( );
        }

        @Override
        public void onProviderDisabled( String provider ) {
            Toast.makeText( getApplicationContext( ),
                    "GPS Disabled", Toast.LENGTH_LONG ).show( );
        }

        @Override
        public void onProviderEnabled( String provider ) {
            Toast.makeText( getApplicationContext( ),
                    "GPS Enabled", Toast.LENGTH_LONG ).show( );
        }

        @Override
        public void onStatusChanged( String provider,
                                     int status, Bundle extras ) {
        }

    } // End of Class MyLocationListener
} // End of GPSDemo Activity

