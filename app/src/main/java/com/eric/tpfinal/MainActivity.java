package com.eric.tpfinal;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private LogginCUI log = new LogginCUI();

    /* Atributos de la clase*/
    private ArmaCamino oArmaCamino = null;
    private MapsFragment mapsFragment = null;
    private FragmentManager fm = getFragmentManager();
    private FloatingActionButton qrBoton = null;
    private IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    private ultimasBusquedas ultimasBusquedas = null;
    private Menu menu = null;
    private BaseDatos CUdb = null;
    private boolean inicio = false;
    private usuarioFragment usuario = null;

    private int codigo_solicitud_pantalla_inicio = 1;
    /*Funciones*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("Ciudad Inteligente");
            setSupportActionBar(toolbar);


            //Instancio la base de datos
            CUdb = new BaseDatos(getApplicationContext(), "DBCUI", null, 1);

            //Instancio los objetos para ArmaCamino y el MapFragment
            oArmaCamino = new ArmaCamino(this);
            mapsFragment = new MapsFragment();
            ultimasBusquedas = new ultimasBusquedas();
            ultimasBusquedas.setMainActivity(this);
            usuario = new usuarioFragment();


            //Agrego Nodos a mi vector de nodos en oArmaCamino
            cargaNodos();

            //Boton Flotante que está abajo a la derecha, para leer QR
            qrBoton = (FloatingActionButton) findViewById(R.id.fab);
            qrBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Se procede con el proceso de scaneo
                    scanIntegrator.initiateScan();
                }
            });


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //Cambio el fragment por defecto por mi mapFragment
            fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();

            // Compruebo que se haya iniciado sesión

            Intent i = getIntent();
            if(i.getBooleanExtra("usuario",false)){
                Toast.makeText(this,"Vas a iniciar con usuario",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Vas en modo normal iniciar con usuario",Toast.LENGTH_SHORT).show();
            }

            




        }catch (Exception e) {

            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de inicializar la actividad principal.",this);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    //Do your work here
                    //Perform operations here only which requires permission
                    alert_info("Si tengo permisos", "jaja");

                } else {
                    alert_info("No tengo permisos", "jaja");
                }

            } else {
                alert_info("No tengo permisos", "jaja");
            }
        }catch (Exception e ){
            log.registrar(this,"onRequestPermissionsResult",e);
            log.alertar("Ocurrió un error al momento de consultar los permisos.",this);
        }

    }


    // Función encargada de gestionar el DialogAlert para informar al usuario un respectivo mensaje.
    private void alert_info(String mensaje, String titulo){

            //Se instancia un objeto AlerteDialog
            AlertDialog.Builder preAlerta = new AlertDialog.Builder(this);
            // Se definen sus respectivos datos para mostrar
            preAlerta.setMessage(mensaje).setTitle(titulo);

            preAlerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    closeContextMenu();
                }
            });
            AlertDialog alerta = preAlerta.create();
            alerta.show();

    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if(fm.getBackStackEntryCount() == 0){
                    super.onBackPressed();
                }
                else{
                    if(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment){
                        finish();
                    }
                    else {
                        fm.popBackStack();
                        mapsFragment.limpiarMapa();

                    }
                }
            }
        } catch (Exception e) {
            log.registrar(this,"onBackPressed",e);
            log.alertar("Ocurrió un error al momento de presionar el botón atrás.",this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().charAt(0) == '*') {
                    menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                    item.setTitle("*" + item.getTitle());
                    break;
                }
            }
            /*Si estoy mostrando una polilinea, la cambio segun la opcion de piso seleccionada*/
            if (mapsFragment.modoPolilinea()) {
                if (item.toString().contains("Baja")) {
                    mapsFragment.cambiarPolilinea(0);
                    return true;
                } else {
                    mapsFragment.cambiarPolilinea(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
                }
            }
            /*Esto es si estoy mostrando nodos*/
            else {
                if (item.toString().contains("Baja")) {
                    mapsFragment.cambiarNodos(0);
                    return true;
                } else {
                    mapsFragment.cambiarNodos(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
                }
            }

        }catch (Exception e){
            log.registrar(this,"onOptionsItemSelected",e);
            log.alertar("Ocurrió un error al momento de seleccionar el item del menú.",this);
        }

        return super.onOptionsItemSelected(item);
    }

    //Switch segun en que opcion del menu desplegable se selecciona
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.buscar) {
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof Busqueda)) {
                    qrBoton.hide();
                    menu.clear();
                    Busqueda busqueda = new Busqueda();
                    fm.popBackStack();
                    fm.beginTransaction().replace(R.id.fragment_container, busqueda).addToBackStack(null).commit();
                }

            } else if (id == R.id.mapa_completo) {
                mapsFragment.limpiarMapa();
                menu.clear();
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment)) {
                    qrBoton.show();
                    fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).commit();
                }

            } else if (id == R.id.ultimas) {
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof ultimasBusquedas)) {
                    qrBoton.hide();
                    menu.clear();
                    fm.popBackStack();
                    fm.beginTransaction().replace(R.id.fragment_container, ultimasBusquedas).addToBackStack(null).commit();
                }

            } else if (id == R.id.usuario) {
                fm.findFragmentById(R.id.fragment_container);
                qrBoton.hide();
                menu.clear();
                fm.popBackStack();
                fm.beginTransaction().replace(R.id.fragment_container, usuario).addToBackStack(null).commit();


            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }catch (Exception e){
            log.registrar(this,"onNavigationItemSelected",e);
            log.alertar("Ocurrió un error al momento de seleccionar una opción del menú desplegable.",this);
        }


        return true;
    }

    /*
    Mostrar busqueda llama a las funciones del mapFragment que:
    -muestran un conjunto de puntos en el mapa
    -muestran una polilinea desde el punto mas cercano hasta el objetivo
    *setPuntoMasCercano setea en oArmaCamino el nodo mas cercano a la posición donde estoy parado
    Luego reemplazo el fragment de Busqueda por el de mapa
    */
    public void mostrarBusqueda(String Edificio, String Nombre) {
        try {
            mapsFragment.setPisoActual(0);
            if (Edificio.equals("*")) {
                mapsFragment.mostrarNodos(oArmaCamino.nodosMapa(Nombre));
                menu.clear();
                menu.add("Planta Baja");
                for (int i = 1; i < mapsFragment.getCantPisos(); i++) {
                    menu.add("Piso " + i);
                }
                menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                getMenuInflater().inflate(R.menu.main, menu);
            } else {
                oArmaCamino.setPuntoMasCercano(mapsFragment.getPosicion(), mapsFragment.getPisoActual());
                mapsFragment.dibujaCamino(oArmaCamino.camino(Edificio, Nombre));
                menu.clear();
                menu.add("Planta Baja");
                for (int i = 1; i < mapsFragment.getCantPisos(); i++) {
                    menu.add("Piso " + i);
                }
                menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                getMenuInflater().inflate(R.menu.main, menu);
                String texto = "Su objetivo está en " + oArmaCamino.getPisoObjetivo();
                Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
            }
            fm.beginTransaction().replace(R.id.fragment_container, mapsFragment).addToBackStack(null).commit();
            qrBoton.show();
        }catch (Exception e){
            log.registrar(this,"mostrarBusqueda",e);
            log.alertar("Ocurrió un error al momento de mostrar la búsqueda.",this);
        }
    }

    //Funcion que le pasa a oArmaCamino un edificio y devuelve un Vector con todas las aulas de ese edificio
    public Vector<Punto> verAulasPorEdificio(String Edificio) {

        Vector<Punto> resultado = new Vector<Punto>();
        try {
            resultado =  oArmaCamino.verAulasPorEdificio(Edificio);
        }catch (Exception e){
            log.registrar(this,"verAulasPorEdificio",e);
            log.alertar("Ocurrió un error al querer mostrar las aulas.",this);
        }
        return  resultado;
    }


    //Funcion para crear nodos del mapa y sus conexiones
    private void cargaNodos() {
        try {
            Vector<Punto> puntos = new Vector<>();
            SQLiteDatabase db1 = CUdb.getReadableDatabase();
            Cursor c = db1.rawQuery("SELECT *  FROM Punto", null);
            c.moveToFirst();

            //Creo y agrego los nodos
            if (c.getCount() > 0) {
                do {
                    Punto oPunto = new Punto(c.getInt(0), Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)), c.getString(3), c.getInt(4), c.getString(5), c.getInt(6));
                    puntos.add(oPunto);
                } while (c.moveToNext());
            }

            //Genero las conexiones
            for (int i = 0; i < puntos.size(); i++) {
                Cursor d = db1.rawQuery("SELECT idHasta FROM Conexiones WHERE idDesde = " + puntos.elementAt(i).getId(), null);
                d.moveToFirst();
                if (d.getCount() > 0) {
                    do {
                        puntos.elementAt(i).addVecino(puntos.elementAt(d.getInt(0)));
                    } while (d.moveToNext());
                }
                oArmaCamino.addNodo(puntos.elementAt(i));
            }
            //Cierro DB
            puntos.clear();
            db1.close();
        }catch (Exception e){
            log.registrar(this,"cargaNodos",e);
            log.alertar("Ocurrió un error al momento de cargar los nodos.",this);
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {

            Toast.makeText(this,"Soy la actividad Main y recibi este código : "+ Integer.toString(requestCode) + " , "+ Integer.toString(resultCode),Toast.LENGTH_SHORT).show();
            switch (requestCode){
                case 1:
                    if (resultCode == RESULT_OK){
                        Toast.makeText(this,"Recibiste estooooooooo",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this,"No se puede iniciar la apliacion sin los permisos",Toast.LENGTH_SHORT).show();
                    }
            }

            //Se obtiene el resultado del proceso de scaneo y se parsea
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                //Quiere decir que se obtuvo resultado pro lo tanto:
                //Desplegamos en pantalla el contenido del código de barra scaneado
                String scanContent = scanningResult.getContents();
                mapsFragment.setLat(Double.parseDouble(scanContent.toString().substring(0, (scanContent.toString().indexOf(',')))));
                mapsFragment.setLon(Double.parseDouble(scanContent.toString().substring((scanContent.toString().indexOf(',')) + 1, scanContent.length() - 2)));
                mapsFragment.setPisoActual(Integer.parseInt(scanContent.toString().substring(scanContent.toString().length() - 1)));
                mapsFragment.actualizaPosicion();

                //Actualizo el * del menu de pisos cuando cambio el piso por QR
                if (mapsFragment.getPisoActual() + 1 <= mapsFragment.getCantPisos()) {
                    for (int i = 0; i < menu.size(); i++) {
                        if (menu.getItem(i).getTitle().charAt(0) == '*') {
                            menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                            menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                            break;
                        }
                    }
                }
            } else {
                //Quiere decir que NO se obtuvo resultado
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
                toast.show();
            }


        }catch (Exception e){
            log.registrar(this,"onActivityResult",e);
            log.alertar("Ocurrió un error al momento de obtener el resultado del escaneo del QR..",this);
        }
    }
}
