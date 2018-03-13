package com.csci515.subik.mapdirection;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends FragmentActivity implements LocationListener {

    GoogleMap mGoogleMap;
    ArrayList<LatLng> mMarkerPoints;
    double mLatitude  = 0;
    double mLongitude = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status != ConnectionResult.SUCCESS){
            // Google Play Services are not available.
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
            finish();
        }

        // Google Play Services are available.
        // Initializing
        mMarkerPoints = new ArrayList<LatLng>( );
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        // Getting Map for the SupportMapFragment
        mGoogleMap = supportMapFragment.getMap();
        // Enables MyLocation Button in the Map.
        mGoogleMap.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location From GPS
        Location location = locationManager.getLastKnownLocation( provider );

        if ( location != null){
            onLocationChanged( location );
        }

        locationManager.requestLocationUpdates(provider, 200, 0, this);

        // Setting onclick event listener for the map
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                // Map already contains destination location.
                if ( mMarkerPoints.size() > 1 ){
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    mMarkerPoints.clear();
                    mGoogleMap.clear();
                    LatLng startpoint = new LatLng(mLatitude,mLongitude);
                    // Draws the marker at the current position.
                    drawMarker( startpoint );
                }   //  End of if

                // Draw the marker at the currently touched location.
                drawMarker( latLng );

                // Check whether start and end locations are captured.
                if ( mMarkerPoints.size() >= 2 ){
                    LatLng origin = mMarkerPoints.get(0);
                    LatLng destination = mMarkerPoints.get(1);
                    // Building the URL including Google Directions API
                    String url = getDirectionsUrl(origin, destination);
                    Log.d("URl: ", url);
                    DownloadTask downloadTask = new DownloadTask( );
                    // Start downloading JSON data from Google Directions API.
                    downloadTask.execute( url );
                }   // End of if
            }   // End of onMapClick
        }); // End of setOnMapClickListener
    }   // End of onCreate

    // A method to download JSON data from URL
    private String downloadUrl( String strUrl ) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL( strUrl );
            // Creating an HTTP connection to communicate with URL
            urlConnection = (HttpURLConnection) url.openConnection( );
            // Connecting to URL
            urlConnection.connect( );
            // Reading data from URL
            iStream = urlConnection.getInputStream( );
            BufferedReader br = new BufferedReader(
                    new InputStreamReader( iStream ) );
            StringBuffer sb  = new StringBuffer( );
            String line = "";
            while( ( line = br.readLine( ) ) != null ) {
                sb.append( line );
            }
            data = sb.toString( );
            br.close( );
        }  // End of try
        catch( Exception e ) {
            Log.d( "Exception downloading ", e.toString( ) );
        }
        finally {
            iStream.close( );
            urlConnection.disconnect( );
        }
        return data;
    }  // End of downloadUrl

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest  = "destination=" + destination.latitude + "," + destination.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        // Output format
        String output = "json";
        // Building the URL for the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }   // End of getDirectionsUrl


    private void drawMarker(LatLng points) {

        mMarkerPoints.add(points);
        // Creating MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();
        // Setting the position of the marker
        markerOptions.position(points);

        // For the start location, the color of marker is GREEN and
        // for the end location, the color of marker is RED.
        if ( mMarkerPoints.size() == 1 ){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else if ( mMarkerPoints.size() == 2 ){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2.
        mGoogleMap.addMarker( markerOptions );
    }   // End of drawMarker

    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set.
        if ( mMarkerPoints.size() < 2){
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng points = new LatLng(mLatitude, mLongitude);
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLng(points));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            drawMarker ( points );
        }
    }   // End of onLocationChanged

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // A class to download data from Google Directions URL
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground( String... url ) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl( url[0] );
            }
            catch( Exception e ) {
                Log.d( "Background Task", e.toString( ) );
            }
            return data;
        }  // End of doInBackground


        // Executes in UI thread, after the execution of doInBackground( )
        @Override
        protected void onPostExecute( String result ) {
            super.onPostExecute( result );
            ParserTask parserTask = new ParserTask( );
            // Invokes the thread for parsing the JSON data.
            parserTask.execute( result );
        }  // End of onPostExecute

    }  // End of DownloadTask


    // A class to parse the Google Directions in JSON format
    private class ParserTask extends AsyncTask<String, Integer,
            List<List<HashMap<String,String>>> > {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>>
        doInBackground( String... jsonData ) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject( jsonData[0] );
                DirectionJSONParser parser = new DirectionJSONParser( );
                // Starts parsing data.
                routes = parser.parse( jObject );
            }
            catch( Exception e ) {
                e.printStackTrace( );
            }
            return routes;
        }  // End of doInBackground


        // Executes in UI thread, after the parsing process.
        @Override
        protected void onPostExecute(
                List<List<HashMap<String, String>>> result ) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for ( int i=0; i<result.size( ); i++ ) {
                points = new ArrayList<LatLng>( );
                lineOptions = new PolylineOptions( );
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get( i );

                // Fetching all the points in i-th route
                for ( int j=0; j<path.size( ); j++ ) {
                    HashMap<String,String> point = path.get( j );
                    double lat = Double.parseDouble( point.get( "lat" ) );
                    double lng = Double.parseDouble( point.get( "lng" ) );
                    LatLng position = new LatLng( lat, lng );
                    points.add( position );
                }  // End of inner for

                // Adding all the points in the route to LineOptions
                lineOptions.addAll( points );
                lineOptions.width( 2 );
                lineOptions.color( Color.RED );
            }  // End of outer for

            // Drawing polyline in the Google Map for the i-th route
            mGoogleMap.addPolyline( lineOptions );
        }  // End of onPostExecute

    }  // End of ParserTask

}  // End of MainActivity
