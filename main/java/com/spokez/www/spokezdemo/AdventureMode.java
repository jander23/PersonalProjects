package com.spokez.www.spokezdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdventureMode extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure_mode);
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
        // Add a marker in Sydney and move the camera
        try{
            LocationListener ll = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, ll);
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng here = new LatLng(l.getLatitude(), l.getLongitude());
            mMap.addMarker(new MarkerOptions().position(here).title("you!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 15));
            mMap.setMyLocationEnabled(true);
        }
        catch(SecurityException se) {
            // Default behavior if permission fails
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    }

    String m_Text="0";
    boolean proceed=false;

    public void makeRoute(View view){
        mMap.clear();
        double rand1;
        double rand2;
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        LatLng coords = new LatLng(0,0);
        try {
            LocationListener ll = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, ll);
            Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            coords = new LatLng(l.getLatitude(), l.getLongitude());
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
        }
        catch (SecurityException se) {
            se.printStackTrace();
        }
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        points.add(coords);
        // get max distance from user
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("How far do you want to go?");

// Set up the input
        final EditText input = new EditText(this);
        input.setHint("Distance in kilometers");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                proceed = true;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                proceed = false;
            }
        });

        builder.show();
        if(proceed) {
            double max = Double.parseDouble(m_Text);
            double current = 0;
            int iterations = 0;
            while (current < max && points.size() < 8) {
                current = 0;
                // TODO: try to get distance as close to max as possible without too many points
                rand1 = points.get(iterations).latitude + max / 300 * (Math.random() - .5);
                rand2 = points.get(iterations).longitude + max / 300 * (Math.random() - .5);
                List<Address> ad = null;
                try {
                    ad = gc.getFromLocation(rand1, rand2, 1);
                } catch (IOException ioe) {

                }
                if (ad != null && ad.size() > 0) {
                    points.add(new LatLng(rand1, rand2));
                    int k = 0;
                    for (int i = 0; i <= points.size()-2; i++) {
                    current = current + Double.parseDouble(getDistance(points.get(i).latitude,
                            points.get(i).longitude,
                            points.get(i + 1).latitude,
                            points.get(i + 1).longitude))/1000;
                        k = i;
                    }
                    iterations++;
                    // Add the distance back to the starting point as well.
                current = current + Double.parseDouble(getDistance(points.get(k).latitude,
                        points.get(k).longitude,
                        points.get(0).latitude,
                        points.get(0).longitude))/1000;
                }

                for (int j = 0; j < iterations; j++) {
                    MarkerOptions marker = new MarkerOptions();
                    marker.position(points.get(j));
                    marker.title(""+j);
                    mMap.addMarker(marker);
                }
                proceed = false;
            }
            Context context = getApplicationContext();
            CharSequence text = "Generated route is about " + current + " km.";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            // TODO: make calls to convert markers into route lines
            // TODO: allow user to accept markers or generate new
            // TODO: why does it only run on second click? Confusing.
        }
    }

    String response;
    String parsedDistance;

    // Use JSON to calculate route distance between two points.
    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+lat1+","+lon1+"&destinations="+lat2+","+lon2+"&mode=bicycling&key=AIzaSyAczqjetLynO8utPvBU_Fp67IAp7YVsoH0");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("rows");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("elements");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("value");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }

    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=YOUR_API_KEY");
        return urlString.toString();
    }

    // Calculates linear distance, not currently used.
    public double pythag(LatLng p1, LatLng p2){
        double result = 0;
        double dx = p1.latitude - p2.latitude;
        double dy = p1.longitude - p2.longitude;
        result = Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
        return result*90;
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))//Google maps blue color
                            .geodesic(true)
            );
           /*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
           */
        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
}
