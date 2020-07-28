package com.fcamposj.perumotos.activities.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fcamposj.perumotos.R;
import com.fcamposj.perumotos.activities.driver.DriverMapActivity;
import com.fcamposj.perumotos.activities.driver.RegisterDriverActivity;
import com.fcamposj.perumotos.include.MyToolbar;
import com.fcamposj.perumotos.models.Client;
import com.fcamposj.perumotos.provides.AuthProvider;
import com.fcamposj.perumotos.provides.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {


    AlertDialog nDialog;

    AuthProvider nAuthProvides;
    ClientProvider nClientProvider;

    //VIEWS

    Button nbtnRe;
    TextInputEditText txName;
    TextInputEditText txEmail;
    TextInputEditText txPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MyToolbar.show(this,"Registro de Usuario",true );

        nbtnRe = findViewById(R.id.btnRegiste);
        txName = findViewById(R.id.txtInputName);
        txEmail = findViewById(R.id.txtInputEmail);
        txPass  = findViewById(R.id.txtInputPassword);

        nAuthProvides = new AuthProvider();
        nClientProvider = new ClientProvider();


        nDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere unos segundos").build();

        nbtnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });
    }

    void clickRegister() {
        final String name = txName.getText().toString();
        final String email = txEmail.getText().toString();
        final String pass = txPass.getText().toString();

        if (name.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
            if (pass.length() >= 6) {
                nDialog.show();
                register(name, email, pass);
            } else {
                Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            }
        }

        else{ Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            }
        }

        void register(final String name, final String email, String pass){
            nAuthProvides.register(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    nDialog.hide();
                    if (task.isSuccessful()){
                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Client client = new Client(id, name, email);
                        create(client);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        void create(Client client){
            nClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //Toast.makeText(RegisterActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent (RegisterActivity.this, ClientMapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"No se pudo crear el Cliente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /*void saveUser(String name, String email){
            String selectedUser = nPrefe.getString("user", "");
            User user = new User();
            user.setEmail(email);
            user.setName(name);

            if (selectedUser.equals("driver")){
                bDatabase.child("Users").child("Drivers").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Fallo el registro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else if(selectedUser.equals("client")){
                bDatabase.child("Users").child("Clients").push().setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this,"Fallo el registro", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }*/
    }
