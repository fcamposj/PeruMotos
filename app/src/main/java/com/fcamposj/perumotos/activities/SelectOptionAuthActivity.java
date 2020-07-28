package com.fcamposj.perumotos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.client.RegisterActivity;
import com.fcamposj.perumotos.activities.driver.RegisterDriverActivity;

public class SelectOptionAuthActivity extends AppCompatActivity {

    SharedPreferences nPrefe;

    Toolbar nToolbar;
    Button nbtnGoToLogin;
    Button nbtnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);

        nToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("Seleccionar una opcion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nbtnGoToLogin = findViewById(R.id.btnLogin);
        nbtnGoToRegister = findViewById(R.id.btnRegister);

        nPrefe = getApplicationContext().getSharedPreferences("typeuser", MODE_PRIVATE);

        nbtnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        nbtnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    private void goToRegister() {

        String typeUser = nPrefe.getString("use","");
        if (typeUser.equals("client")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
        else {
            Intent intentOne = new Intent(SelectOptionAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intentOne);
        }
    }
}
