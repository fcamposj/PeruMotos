package com.fcamposj.perumotos.provides;


import com.fcamposj.perumotos.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverProvider {

    DatabaseReference bDatabase;

    public DriverProvider(){

        bDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver driver){
        return  bDatabase.child(driver.getId()).setValue(driver);
    }
}
