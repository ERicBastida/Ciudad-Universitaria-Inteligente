package com.eric.tpfinal;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Login_SingUp extends AppCompatActivity implements loginFragment.OnFragmentInteractionListener, signUpFragment.OnFragmentInteractionListener {

    private loginFragment login;

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
