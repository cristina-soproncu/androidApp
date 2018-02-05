package mape3.project;

/**
 * Created by crist on 12.06.2017.
 */
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Navigation extends DetaliiFragmente implements OnMapReadyCallback {
    MapView mapView;
    GoogleMap map;
    private TextView latitude;
    private TextView longitude;
    private LocationManager locationManager;
    private Navigation.MyLocationListener mylistener;
    private static final int ACCESS_FINE_LOCATION_COD=1;
    private String provider;
    private Criteria criteria;
    Location location;

    public static Navigation newInstance(int index) {
        Navigation f = new Navigation();
        // preluam argumentele
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.navigation_layout, container, false);
        TextView text = (TextView)view.findViewById(R.id.titlu);
        text.setText(Resurse.TITLURI[daIndexSelectat()]);
        text = (TextView)view.findViewById(R.id.titlu);
        TableLayout tabel=(TableLayout)view.findViewById(R.id.tabel);

        /* Start GeoLocation Code */
        latitude = (TextView)view.findViewById(R.id.lat);
        longitude = (TextView)view.findViewById(R.id.lon);
        locationManager = (LocationManager) view.getContext().getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);

        if (Build.VERSION.SDK_INT >= 23) {
            //Runtime permission request
            if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_COD);
            } else {
                provider = locationManager.getBestProvider(criteria, false);
                location = locationManager.getLastKnownLocation(provider);
            }
        }
        else{
            provider = locationManager.getBestProvider(criteria, false);
            location = locationManager.getLastKnownLocation(provider);
        }

        if (location != null) {
            Log.i("Location info:", "not null");
        } else {
            Log.i("Location info:", "null");
        }

        mylistener = new Navigation.MyLocationListener();

        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        // location updates: at least 1 meter and 200 millsecs change
        locationManager.requestLocationUpdates(provider, 200, 1, mylistener);
        /**/

        TableRow rand = new TableRow(getActivity());
        rand.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        Button btn = new Button(getActivity());
        btn.setTextColor(Color.BLACK);
        btn.setPadding(5, 5, 5, 5);
        btn.setText("Arata Locatia");
        btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            if (location != null) {
                LatLng coordIasi = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate locatie = CameraUpdateFactory.newLatLngZoom(coordIasi, 15);
                if (map != null) map.animateCamera(locatie);
            }
            }
        });
        rand.addView(btn);

        tabel.addView(rand, new
                TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        if (location != null) {
            googleMapOptions.camera(new CameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 3, 0, 0));
        }
        mapView = new MapView(getActivity(), googleMapOptions);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        MapsInitializer.initialize(getActivity());
        tabel.addView(mapView);
        return view;
    }

    @Override public void onMapReady(GoogleMap map) {
        this.map = map;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*Grant Location Permissions*/
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_COD: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Permission", "Permission granted");
                } else {
                    Log.i("Permission", "Permission denied");
                }
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private class MyLocationListener implements LocationListener {
        /*My Location Listener*/
        @Override
        public void onLocationChanged(Location location) {
            latitude.setText("Latitude: "+String.valueOf(location.getLatitude()));
            longitude.setText("Longitude: "+String.valueOf(location.getLongitude()));
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getContext(), provider + "'s status changed to "+status +"!",
                    Toast.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getContext(), "Provider " + provider + " enabled!",
                    Toast.LENGTH_SHORT).show();

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getContext(), "Provider " + provider + " disabled!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}