package com.csci515.subik.serverconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by subik on 3/11/18.
 */

class SigninActivity extends AsyncTask<String, Void, String> {

    private Context context;
    private TextView tv;


    public SigninActivity(Context applicationContext, TextView textView) {
        this.context     = applicationContext;
        this.tv = textView;
    }

    @Override
    protected String doInBackground(String... args) {
        String name = args[0];
        String age = args[1];
        String gender = args[2];
        String email = args[3];
        String key = "Login";
        //String link     = "http://192.168.0.26/test.php";
        String link = "http://undcemcs02.und.edu/~subik.pokharel/515/1/Customer.php";

        try {
            /*link += "?name=" + URLEncoder.encode( name, "UTF-8" );
            link += "?age=" + URLEncoder.encode( age, "UTF-8" );
            link += "?gender=" + URLEncoder.encode( gender, "UTF-8" );
            link += "?email=" + URLEncoder.encode( email, "UTF-8" );*/

            // Connect to the server.
            URL            url = new URL( link );
            URLConnection conn = url.openConnection( );
            conn.setDoOutput( true );

            String data = URLEncoder.encode( "name", "UTF-8" ) + "=";
            data += URLEncoder.encode( name,   "UTF-8" ) + "&";
            data += URLEncoder.encode( "age", "UTF-8" ) + "=";
            data += URLEncoder.encode( age,   "UTF-8" )+ "&";
            data += URLEncoder.encode( "gender", "UTF-8" ) + "=";
            data += URLEncoder.encode( gender,   "UTF-8" )+ "&";
            data += URLEncoder.encode( "email", "UTF-8" ) + "=";
            data += URLEncoder.encode( email,   "UTF-8" )+ "&";
            data += URLEncoder.encode( "key", "UTF-8" ) + "=";
            data += URLEncoder.encode( key,   "UTF-8" );

            Log.d("data sending: ", data);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream( ) );
            wr.write( data );
            wr.flush( );

            // Read server response.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader( conn.getInputStream( ) ));
            StringBuilder sb = new StringBuilder( );
            String      line;
            while (( line = reader.readLine( ) ) != null ) {
                sb.append( line );
                break;
            }
            return sb.toString( );

        } catch (Exception e) {
            return new String( "Exception here: " + e.getMessage( ) );
        }



        //return link;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("Link: ",s);
        this.tv.setText(s);
    }
}
