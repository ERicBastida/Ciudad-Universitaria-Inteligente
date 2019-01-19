package com.eric.tpfinal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.view.WindowManager;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import java.util.Collection;
import static com.eric.tpfinal.IntentIntegrator.list;

/**
 * Created by ERicBastida on 10/01/2019.
 */

public class PantallaCarga extends AppCompatActivity {

    private LottieAnimationView animationView;
    private String nombre_animacion = "loading_screen.json";
    private String STRING_MENSAJE = "PantallaCarga/%s => [Causa]: %s , [Mensaje]: %s , [Origen]: %s";
    private int REQUEST_PERMISSION_PHONE_STATE=1;
    private int seconds_waiting = 5;

    private boolean habilitado = false;
    private int habilitados = 0;
    private int intentos = 0;
    private int max_intentos = 5;


    private Handler mWaitHandler = new Handler();

    private Collection<String> PERMISOS = (Collection<String>) list(Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.ACCESS_COARSE_LOCATION);
    private LogginCUI log = new LogginCUI();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pantalla_carga);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            animationView = (LottieAnimationView) findViewById(R.id.animation_view);


            cargar();
            avanzar();



        }catch (Exception e){

            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de crear la pantalla de carga.",this);



        }

    }

    // Funcion encargada de comprobar los permisos y en caso favorable avanzar al siguiente activity
    private void avanzar(){

        try {

            while (intentos < max_intentos) {
                //            Toast.makeText(this,"Intentando avanzar",Toast.LENGTH_SHORT).show();
                intentos++;
                mWaitHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        //                    Toast.makeText(getApplicationContext(),"Intetoooo",Toast.LENGTH_SHORT).show();
                        comprobar_permisos();


                    }
                }, seconds_waiting * 1000);

            }
        }catch (Exception e){
            log.registrar(this,"avanzar",e);
            log.alertar("Ocurrió un error al momento de comprobar los permisos.",this);
        }





    }

    //Función encargada de mostrar la animación y comprobar los permisos necesarios paara que la aplicación funcione correctamente.
    private void cargar(){

        try {
            animationView.setAnimation(nombre_animacion);
            animationView.loop(true);
            animationView.playAnimation();


        }catch (Exception e){

            log.registrar(this,"cargar",e);
            log.alertar("Ocurrió un error al momento de cargar la animación.",this);

        }
    }






    private boolean comprobar_permisos() {
        try {


            if(habilitados == PERMISOS.size()){

                //Nos vamos a iniciar la actividad encargada del inicio de sesion.
//                Intent intent = new Intent(getApplicationContext(), FirebaseExample.class);
                Intent intent = new Intent(getApplicationContext(), Login_SingUp.class);
                startActivity(intent);

                finish();

            }

            int id_permiso = 0;

            for (String permiso : PERMISOS){
                int permissionCheck = ContextCompat.checkSelfPermission(this, permiso);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(permiso, id_permiso);

                }else {
                    habilitados++;
                }
                id_permiso += 1;
            }



        }catch (Exception e){

            log.registrar(this,"comprobar_permisos",e);
            log.alertar("Ocurrió un error al momento de comprobar los permisos..",this);

        }
        return true;
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestPermission(permission, permissionRequestCode);
                        }
                    });
            builder.create().show();
        }catch (Exception e){
            log.registrar(this,"showExplanation",e);
            log.alertar("Ocurrió un error al momento de mostrar la explicación.",this);

        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,  new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult( int requestCode,  String permissions[],    int[] grantResults) {
        try {
            switch (requestCode) {
                default:

                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setResult(RESULT_OK);

                        habilitados++;
                        //TODO: cambiar los mensjaes
                        Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        setResult(RESULT_CANCELED);
                        habilitado = false;
                        Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }


            }
            if (habilitados == PERMISOS.size()) {
                habilitado = true;
            }
        }catch (Exception e){
            log.registrar(this,"onRequestPermissionsResult",e);
            log.alertar("Ocurrió un error al momento de interpretar el resultado de los permisos.",this);
        }


    }


}
