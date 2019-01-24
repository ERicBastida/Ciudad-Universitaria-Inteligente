package com.eric.tpfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Login_SingUp extends AppCompatActivity implements loginFragment.OnFragmentInteractionListener, usuarioFragment.OnFragmentInteractionListener {

    LogginCUI log = new LogginCUI();
    private loginFragment login;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login__sing_up);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            login = new loginFragment();

            getFragmentManager().beginTransaction().add(R.id.fragment_container, login).commit();
        }catch (Exception e){
            log.registrar(this,"onCreate",e);
            log.alertar("Ocurrió un error al momento de gestionar el ingreso al usuario.",this);
        }

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Toast.makeText(this,"Soy la actividad Login/Sing y recibi este código : "+ Integer.toString(requestCode) + " , "+ Integer.toString(resultCode),Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,"Soy la actividad Login/Sing y recibi este código : "+ Integer.toString(requestCode),Toast.LENGTH_SHORT).show();
    }


    public void puto_el_que_lee(String text){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

            }
        });

        Toast.makeText(this,"Puto conchudo" + text,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"GUARDAA comunicaste algo al Activity!"+uri.toString(),Toast.LENGTH_SHORT).show();

    }


}
