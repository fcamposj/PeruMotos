package com.fcamposj.perumotos.activities.driver;

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
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.MainActivity;
import com.fcamposj.perumotos.include.MyToolbar;
import com.fcamposj.perumotos.provides.AuthProvider;
import com.fcamposj.perumotos.provides.GeoFireProvider;
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

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap nMap;
    private SupportMapFragment nMapFragment;
    private AuthProvider nAuthProvider;
    private GeoFireProvider nGeoFireProvider;

    private LocationRequest nlocationRequest;
    private FusedLocationProviderClient nFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;


    private Marker nMarket;
    private Button btnConnect;
    private boolean isConnect = false;

    private LatLng nlatLng;


    LocationCallback nLocationCallBack = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    nlatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (nMarket != null) {
                        nMarket.remove();
                    }
                    nMarket = nMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("Tú posición")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_casco))
                    );
                    //obtener la localizacion del usuario en tiempo real
                    nMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));

                    updateLocation();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        MyToolbar.show(this, "Mapa del Conductor", false);

        nAuthProvider = new AuthProvider();
        nGeoFireProvider = new GeoFireProvider();

        nFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        nMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        nMapFragment.getMapAsync(this);

        btnConnect = findViewById(R.id.btnConnect);


        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    disconnect();
                } else {
                    startLocation();
                }
            }
        });
    }

    private void updateLocation() {
        if (nAuthProvider.existSession() && nlatLng != null) {
            nGeoFireProvider.saveLocation(nAuthProvider.getId(), nlatLng);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        nMap = googleMap;
        nMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        nMap.getUiSettings().setZoomControlsEnabled(true);


        nlocationRequest = new LocationRequest();
        nlocationRequest.setInterval(2000);
        nlocationRequest.setFastestInterval(2000);
        nlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        nlocationRequest.setSmallestDisplacement(5);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (activedGps()) {
                        nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
                        nMap.setMyLocationEnabled(true);
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermision();
                }
            } else {
                checkLocationPermision();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && activedGps()) {
            nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
            nMap.setMyLocationEnabled(true);
        } else {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Es necesario activar la ubicación para continar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean activedGps() {
        boolean activeIs = false;
        LocationManager managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (managerLocation.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            activeIs = true;
        }
        return activeIs;
    }

    private void disconnect() {

        if (nFusedLocation != null) {
            btnConnect.setText("Conectarse");
            isConnect = false;
            nFusedLocation.removeLocationUpdates(nLocationCallBack);
            if (nAuthProvider.existSession()) {
                nGeoFireProvider.removeLocation(nAuthProvider.getId());
            }
        } else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (activedGps()) {
                    btnConnect.setText("Desconectarse");
                    isConnect = true;
                    nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
                    nMap.setMyLocationEnabled(true);
                } else {
                    showAlertDialogNOGPS();
                }
            } else {
                checkLocationPermision();
            }
        } else {
            if (activedGps()) {
                nFusedLocation.requestLocationUpdates(nlocationRequest, nLocationCallBack, Looper.myLooper());
                nMap.setMyLocationEnabled(false);
            } else {
                showAlertDialogNOGPS();
            }
        }

    }

    private void checkLocationPermision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicación para que la puedas utilizar")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

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
        if (item.getItemId() == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }


    void logout() {
        disconnect();
        Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

