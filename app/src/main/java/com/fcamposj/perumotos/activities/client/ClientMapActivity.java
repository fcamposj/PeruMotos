package com.fcamposj.perumotos.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.MainActivity;
import com.fcamposj.perumotos.activities.driver.DriverMapActivity;
import com.fcamposj.perumotos.include.MyToolbar;
import com.fcamposj.perumotos.provides.AuthProvider;
import com.fcamposj.perumotos.provides.GeoFireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClientMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment nMapFragment;
    private AuthProvider nAuthProvider;

    private LocationRequest nlocationRequest;
    private FusedLocationProviderClient nFusedLocation;
    private GeoFireProvider ngeoFireProvider;

    private Marker nMarket;
    private LatLng nlatlng;

    private List<Marker> nDriversExixt = new ArrayList<>();

    private boolean mIsFirstTime = true;

    private AutocompleteSupportFragment nAutoComplete;
    private AutocompleteSupportFragment nAutoCompleteDestination;
    private PlacesClient nPlaces;

    private String nOriginClient;
    private LatLng nOriginLatLng;

    private String nDestinati;
    private LatLng nLatLngDes;

    private GoogleMap.OnCameraIdleListener onCameraIdleListener;


    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    LocationCallback nLocationCallBack = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    /*if (nMarket != null) {
                        nMarket.remove();
                    }*/

                    nlatlng = new LatLng(location.getLatitude(), location.getLongitude());
                    /*
                    nMarket = nMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("Tú posición")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icons_i))
                    );
                     */
                    //obtener la localizacion del usuario en tiempo real
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));
                    if (mIsFirstTime){
                        mIsFirstTime = false;
                        getActiveDrivers();
                    }
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);

        MyToolbar.show(this, "Mapa del Cliente", false);

        nAuthProvider = new AuthProvider();
        ngeoFireProvider = new GeoFireProvider();

        nFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        nMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        nMapFragment.getMapAsync(this);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        nPlaces = Places.createClient(this);
        nAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.originAutoComplete);
        nAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        nAutoComplete.setHint("Lugar de Origen");
        nAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                nOriginClient = place.getName();
                nOriginLatLng = place.getLatLng();
                Log.d("PLACE", "Name: " + nOriginClient);
                Log.d("PLACE", "Lat: " + nOriginLatLng.latitude);
                Log.d("PLACE", "Lng: " + nOriginLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        nAutoCompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.originAutoDestination);
        nAutoCompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        nAutoCompleteDestination.setHint("Destino");
        nAutoCompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                nDestinati = place.getName();
                nLatLngDes = place.getLatLng();
                Log.d("PLACE", "Name: " + nDestinati);
                Log.d("PLACE", "Lat: " + nLatLngDes.latitude);
                Log.d("PLACE", "Lng: " + nLatLngDes.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(ClientMapActivity.this);
                    nOriginLatLng = nMap.getCameraPosition().target;
                    List<Address> addresses = geocoder.getFromLocation(nOriginLatLng.latitude, nOriginLatLng.longitude, 1);
                    String city = addresses.get(0).getLocality();
                    String country = addresses.get(0).getCountryName();
                    String address = addresses.get(0).getAddressLine(0);
                    nOriginClient = address + " " + city;
                    nAutoComplete.setText(address + " " + city);
                } catch (Exception e) {
                    Log.d("Error: ", "Mensaje de error" + e.getMessage());
                }
            }
        };

    }

    private void getActiveDrivers(){
        ngeoFireProvider.getActiveDrives(nlatlng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //SE AÑADEN LOS CONDUCTORES QUE SE CONECTAN A LA APLICACION
                for (Marker marker: nDriversExixt) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            return;
                        }
                    }
                }

                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = nMap.addMarker(new MarkerOptions().position(driverLatLng).title("Conductor Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_casco)));
                marker.setTag(key);
                nDriversExixt.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker: nDriversExixt) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.remove();
                            nDriversExixt.remove(marker);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //ACTUALIZAR LA POSICION DE CADA CONDUCTOR
                for (Marker marker: nDriversExixt) {
                    if (marker.getTag() != null) {
                        if (marker.getTag().equals(key)) {
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(true);
        nMap.setMyLocationEnabled(true);
        nMap.setOnCameraIdleListener(onCameraIdleListener);

        nlocationRequest = new LocationRequest();
        nlocationRequest.setInterval(2000);
        nlocationRequest.setFastestInterval(2000);
        nlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        nlocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (activedGps()){
                        nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
                    }
                    else {
                        showAlertDialogNOGPS();
                    }
                }
                else {
                    checkLocationPermision();
                }
            }
            else {
                checkLocationPermision();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && activedGps()){
            nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
        }
        else if (requestCode == SETTINGS_REQUEST_CODE && !activedGps()){
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Es necesario activar la ubicación para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean activedGps(){
        boolean activeIs = false;
        LocationManager managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (managerLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            activeIs = true;
        }
        return activeIs;
    }

    private  void startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (activedGps()){
                    nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
                }
                else {
                    showAlertDialogNOGPS();
                }
            }
            else {
                checkLocationPermision();
            }
        } else {
            if (activedGps()){
                nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
            }
            else {
                showAlertDialogNOGPS();
            }
        }

    }

    private void checkLocationPermision(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicación para que la puedas utilizar")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(ClientMapActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(ClientMapActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }


    void logout() {
        Intent intent= new Intent(ClientMapActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
