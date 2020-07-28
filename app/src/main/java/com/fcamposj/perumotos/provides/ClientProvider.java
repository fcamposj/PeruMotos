package com.fcamposj.perumotos.provides;

import com.fcamposj.perumotos.models.Client;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {

    DatabaseReference bDatabase;

    public ClientProvider(){

        bDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");
     }

     public Task<Void> create(Client client){

        Map<String, Object> map = new HashMap<>();
        map.put("name", client.getName());
        map.put("email", client.getEmail());

         return  bDatabase.child(client.getId()).setValue(map);
     }
}