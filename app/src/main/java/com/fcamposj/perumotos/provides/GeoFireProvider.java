package com.fcamposj.perumotos.provides;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {

    private DatabaseReference nDataBase;
    private GeoFire nGeofire;

    public GeoFireProvider (){
        nDataBase = FirebaseDatabase.getInstance().getReference().child("active_drivers");
        nGeofire = new GeoFire(nDataBase);
    }

    public void saveLocation(String idDriver, LatLng latLng) {
        nGeofire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }


    public void removeLocation (String idDriver) {
        nGeofire.removeLocation(idDriver);
        }

    public GeoQuery getActiveDrives (LatLng latLng) {
        GeoQuery geoQuery = nGeofire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 5);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}
