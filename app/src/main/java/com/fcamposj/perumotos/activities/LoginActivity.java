package com.fcamposj.perumotos.activities;

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
import com.fcamposj.perumotos.activities.client.ClientMapActivity;
import com.fcamposj.perumotos.activities.client.RegisterActivity;
import com.fcamposj.perumotos.activities.driver.DriverMapActivity;
import com.fcamposj.perumotos.include.MyToolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText ntextInputEmail;
    TextInputEditText ntextInputPassword;
    Button btnLgn;

    FirebaseAuth nAuth;
    DatabaseReference nDatabase;

    AlertDialog nDialog;

    SharedPreferences nPrefe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolbar.show(this,"Login de Usuario",true );

        ntextInputEmail = findViewById(R.id.txtInputEmail);
        ntextInputPassword = findViewById(R.id.txtInputPassword);
        btnLgn = findViewById(R.id.btnLogin);

        nAuth = FirebaseAuth.getInstance();
        nDatabase = FirebaseDatabase.getInstance().getReference();

        nDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();
        nPrefe = getApplicationContext().getSharedPreferences("typeuser", MODE_PRIVATE);

        btnLgn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = ntextInputEmail.getText().toString();
        String password = ntextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                nDialog.show();
                nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user  = nPrefe.getString("user", "");
                            if (user.equals("client")){
                                Intent intent = new Intent(LoginActivity.this, ClientMapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Intent intent = new Intent(LoginActivity.this, DriverMapActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "El email y la contraseña, son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        nDialog.dismiss();
                    }
                });
            }
        }
    }
}
