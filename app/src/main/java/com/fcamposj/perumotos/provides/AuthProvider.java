package com.fcamposj.perumotos.provides;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {

    FirebaseAuth nAuth;

    public AuthProvider() {
        nAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> register(String email, String pass){

        return nAuth.createUserWithEmailAndPassword(email,pass);
    }

    public Task<AuthResult> login(String email, String pass){

        return nAuth.signInWithEmailAndPassword(email,pass);
    }

    public void logout(){
        nAuth.signOut();
    }

    public String getId () {
        return nAuth.getCurrentUser().getUid();
    }

    public boolean existSession() {
        boolean exist = false;
        if (nAuth.getCurrentUser() != null) {
            exist = true;
        }
        return exist;
    }
}
