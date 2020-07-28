package com.fcamposj.perumotos.activities.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.client.RegisterActivity;
import com.fcamposj.perumotos.include.MyToolbar;
import com.fcamposj.perumotos.models.Client;
import com.fcamposj.perumotos.models.Driver;
import com.fcamposj.perumotos.provides.AuthProvider;
import com.fcamposj.perumotos.provides.ClientProvider;
import com.fcamposj.perumotos.provides.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {

    AlertDialog nDialog;

    AuthProvider nAuthProvides;
    DriverProvider ndriverProvider;

    //VIEWS

    Toolbar nToolbar;

    Button nbtnRe;
    TextInputEditText txName;
    TextInputEditText txDni;
    TextInputEditText txAsociate;
    TextInputEditText txEmail;
    TextInputEditText txBrand;
    TextInputEditText txPlate;
    TextInputEditText txPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        //MyToolbar.show(this,"Registro de Conductor",true );
        nToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(nToolbar);
        getSupportActionBar().setTitle("Registro de Conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nbtnRe = findViewById(R.id.btnRegiste);
        txName = findViewById(R.id.txtInputName);
        txDni = findViewById(R.id.txtDni);
        txAsociate = findViewById(R.id.txtAsociate);
        txEmail = findViewById(R.id.txtInputEmail);
        txBrand = findViewById(R.id.txtBikeBrand);
        txPlate = findViewById(R.id.txtBikePlate);
        txPass  = findViewById(R.id.txtInputPassword);

        nAuthProvides = new AuthProvider();
        ndriverProvider = new DriverProvider();



        nDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Espere unos segundos").build();

        nbtnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });
    }

    void clickRegister() {
        final String name = txName.getText().toString();
        final String dni = txDni.getText().toString();
        final String asociate = txAsociate.getText().toString();
        final String email = txEmail.getText().toString();
        final String brand = txBrand.getText().toString();
        final String plate = txPlate.getText().toString();
        final String pass = txPass.getText().toString();

        if (name.isEmpty() && !dni.isEmpty() && !asociate.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !brand.isEmpty() && !plate.isEmpty()) {
            if (pass.length() >= 6) {
                nDialog.show();
                register(name, dni, asociate, email, pass, brand, plate);
            } else {
                Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            }
        }

        else{ Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    void register(final String asociate, final String dni, final String name, final String email, final String pass, final String brand, final String plate){
        nAuthProvides.register(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                nDialog.hide();
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(dni, name, asociate, email, pass, plate, brand);
                    create(driver);
                }
                else {
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void create(Driver driver){
        ndriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(RegisterDriverActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (RegisterDriverActivity.this, DriverMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterDriverActivity.this,"No se pudo crear el Cliente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
