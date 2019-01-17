package com.eric.tpfinal;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_SingUp extends AppCompatActivity implements loginFragment.OnFragmentInteractionListener, signUpFragment.OnFragmentInteractionListener {


    private loginFragment login;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__sing_up);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        login = new loginFragment();

        getFragmentManager().beginTransaction().add(R.id.fragment_container,login).commit();
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

//https://tpfinal-3b46f.firebaseapp.com/__/auth/handler
