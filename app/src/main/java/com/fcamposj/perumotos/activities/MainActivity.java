package com.fcamposj.perumotos.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.client.ClientMapActivity;
import com.fcamposj.perumotos.activities.driver.DriverMapActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button nButtomIamClient;
    Button nButtomIamDriver;
    SharedPreferences nPrefe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nPrefe = getApplicationContext().getSharedPreferences("typeuser", MODE_PRIVATE);
        final SharedPreferences.Editor editor = nPrefe.edit();

        nButtomIamClient = findViewById(R.id.btnIamClient);
        nButtomIamDriver = findViewById(R.id.btnIamDriver);

        nButtomIamClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","client");
                editor.apply();
                goToSelectAuth();
            }
        });

        nButtomIamDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user","driver");
                editor.apply();
                goToSelectAuth();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user = nPrefe.getString("Users", "");
            if (user.equals("client")){
                Intent intent = new Intent(MainActivity.this, ClientMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, DriverMapActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void goToSelectAuth() {

        Intent intentselected = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intentselected);

    }
}
