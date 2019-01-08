package com.eric.tpfinal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.ExceptionReporter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Lautaro on 29/11/2016.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {

    // String utilizado para el tracking de errores
    private String STRING_MENSAJE = "MapsFragment/%s => [Causa]: %s , [Mensaje]: %s , [Origen]: %s";

    public GoogleMap miMapa = null;
    private SensorManager miSensorManager;
    private MarkerOptions miPosicion = null;
    private Marker miPosicionMarcador = null;
    private int cantPisos = 0;
    private int pisoActual = 0;
    private int cantidad_edificios = 2;           //Cantidad de edificios relevados

    private Vector<PolylineOptions> misPolilineas = new Vector<>(); //Vector de polilineas, cada elemento será una polilinea para un piso
    private Vector<MarkerOptions> marcadoresPiso = new Vector<>(); //Vector de marcadores por piso, dos marcadores por polilinea por piso
    //uno en cada extremo


    private Vector<MarkerOptions> misMarcadores = new Vector<>();  //Vector de nodos para mostrar nodos sueltos (baños, bares, oficinas, etc)

    private Vector<Vector<GroundOverlayOptions>> misOverlays = new Vector<>(); //Vector de overLays, los planos de cada edificio
    //El vector de afuera es para los pisos, cada elemento es un piso
    //Cada elemento es un vector que tiene overlays de 1 o mas edificios
    //Una polilinea puede pasar por mas de un edificio en un piso

    private HashMaps miHashMaps = new HashMaps();

    private Map<String, LatLngBounds> hashMapBounds = miHashMaps.getHashMapsBound();
    private Map<String, Integer> hashMapID = miHashMaps.getHashMapID();

    private Map<LatLng, Integer> hashMapImagenes = new HashMap<>();


    private float angle = 0;
    private double lat;
    private double lon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {

            View rootView = inflater.inflate(R.layout.fragment_map, container, false);

            MapFragment fragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(this);

            //LocationManager
            LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            MyLocationListener mlocListener = new MyLocationListener();
            mlocListener.setMainActivity(this);


            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (android.location.LocationListener) mlocListener);
                //SensorManager
                SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 1000000);
                this.miSensorManager = mSensorManager;

            }else{
                //Se instancia un objeto AlerteDialog
                AlertDialog.Builder preAlerta = new AlertDialog.Builder(getActivity());
                // Se definen sus respectivos datos para mostrar
                preAlerta.setMessage("Debe dar permisos para utilizar el GPS.").setTitle("Error al buscar proveedor de red.");

                preAlerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().closeContextMenu();
                    }
                });
                AlertDialog alerta = preAlerta.create();
                alerta.show();


            }

            return rootView;



        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"onCreateView",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {


            miMapa = googleMap;

            //Esto es para armar el grafo, clickeando encima del overlay y viendo la lat y long del punto
            miMapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d("Prueba", latLng.latitude + ", " + latLng.longitude);
                }
            });

            //Hago mi propia InfoWindow, para poder mostrar una imagen del nodo cuando hago click en el y ver el lugar que está señalado
            miMapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                    ImageView imagen = (ImageView) v.findViewById(R.id.imageView);
                    TextView titulo = (TextView) v.findViewById(R.id.titulo);

                    //Busco en el mapa por ese punto, si tiene imagen la agrego
                    if (hashMapImagenes.containsKey(marker.getPosition())) {
                        imagen.setImageResource(hashMapImagenes.get(marker.getPosition()));
                    }

                    titulo.setText(marker.getTitle());
                    return v;
                }
            });

            //Muevo la camara hasta mi posicion y agrego un marcador allí
            LatLng position = new LatLng(this.lat, this.lon);
            miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
            miMapa.moveCamera(CameraUpdateFactory.zoomTo(18));
            miPosicion = new MarkerOptions().position(new LatLng(this.lat, this.lon)).title("Usted está aquí");
            miPosicionMarcador = miMapa.addMarker(miPosicion);

            //Agrego los marcadores adicionales (Edificios, baños, bares,etc), si los hay
            for (int i = 0; i < misMarcadores.size(); i++) {
                String texto;
                if (pisoActual == 0) {
                    texto = "Planta Baja";
                } else {
                    texto = "Piso " + pisoActual;
                }
                if (misMarcadores.elementAt(i).getTitle().contains(texto)) {
                    miMapa.addMarker(misMarcadores.elementAt(i));
                }
            }

            //Agrego polilinea si la hay
            if (misPolilineas.size() != 0) {
                miMapa.addPolyline(misPolilineas.elementAt(pisoActual));
                miMapa.addMarker(marcadoresPiso.elementAt(2 * pisoActual));
                miMapa.addMarker(marcadoresPiso.elementAt(2 * pisoActual + 1));
            }

            //Agrego los overlays
            if (misOverlays.size() != 0) {
                for (int i = 0; i < misOverlays.elementAt(pisoActual).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(pisoActual).elementAt(i));
                }
                miMapa.moveCamera(CameraUpdateFactory.newLatLngBounds(misOverlays.elementAt(pisoActual).elementAt(0).getBounds(), 0));
                miMapa.moveCamera(CameraUpdateFactory.zoomTo(18));
            }


        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"onMapReady",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    //Setters, getters y demas utilidades
    public void setLat(double l) {
        this.lat = l;
    }

    public void setLon(double l) {
        this.lon = l;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public int getCantPisos(){
        return cantPisos;
    }

    public void setPisoActual(int p){this.pisoActual = p;}

    public int getPisoActual(){return pisoActual;}

    public boolean modoPolilinea() throws Exception {
        try{
            return !misPolilineas.isEmpty();
        }
        catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"modoPolilinea",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw new Exception("Error sl consultar el estado de modo polilinea..",null);

        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {


            synchronized (this) {
                switch (sensorEvent.sensor.getType()) {
                    case Sensor.TYPE_ORIENTATION:
                        float degree = Math.round(sensorEvent.values[0]);
                        //Si el angulo de rotación con respecto a la rotación de la muestra anterior es mayor a 20
                        //roto la camara, sino no porque sino baila mucho
                        if (Math.abs(degree - angle) > 30) {
                            angle = degree;
                            CameraPosition oldPos = miMapa.getCameraPosition();
                            CameraPosition pos = CameraPosition.builder(oldPos).bearing(degree).build();
                            miMapa.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                        }
                }
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"onSensorChanged",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Limpio el mapa de polilineas, marcadores, etc
    public void limpiarMapa() throws Exception {
        try{
            miPosicionMarcador.remove();
            misPolilineas.clear();
            marcadoresPiso.clear();
            misOverlays.clear();
            miMapa.clear();
            miMapa.addMarker(miPosicion);
            setPisoActual(0);

        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"limpiarMapa",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw new Exception("Error al limpiar el mapa.",null);
        }

    }

    //Actualizo mi posición si me moví. Quito mi marcador y lo pongo en donde corresponde
    @TargetApi(Build.VERSION_CODES.M)
    void actualizaPosicion() throws Exception {
        try {
            LatLng position = new LatLng(this.lat, this.lon);
            miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
            miPosicion.position(position);
            miPosicionMarcador.remove();
            miPosicionMarcador = miMapa.addMarker(miPosicion);

            if (!misPolilineas.isEmpty() && pisoActual + 1 <= misPolilineas.size()) {
                cambiarPolilinea(pisoActual);
            } else if (pisoActual + 1 > misPolilineas.size() && !misPolilineas.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Su objetivo está en un piso inferior", Toast.LENGTH_LONG).show();
            }
            if (!misMarcadores.isEmpty() && pisoActual + 1 <= misMarcadores.size()) {
                cambiarNodos(pisoActual);
            } else if (pisoActual + 1 > misMarcadores.size() && !misMarcadores.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Su objetivo está en un piso inferior", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"actualizaPosicion",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw new Exception("Error al actualizar la posición.",null);
        }

    }

    //Obtengo mi latitud y longitud en un objeto LatLng
    public LatLng getPosicion() throws Exception {
        try {
            return new LatLng(this.lat, this.lon);
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"getPosicion",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw new Exception("Error al tratar de obtener la posición.",null);
        }

    }

    //Recibo un vector de puntos y creo un polilinea con ellos
    public void dibujaCamino(Vector<Punto> path) {
        try {
            misPolilineas.clear();
            misMarcadores.clear();
            marcadoresPiso.clear();
            cantPisos = 0;
            Vector<String> edificios = new Vector<>();

            //Veo cuantos pisos hay
            for (int i = 0; i < path.size(); i++) {
                if (path.elementAt(i).getPiso() > cantPisos) {
                    cantPisos = path.elementAt(i).getPiso();
                }
            }

            //Creo las polilineas y overlays que voy a usar
            cantPisos = cantPisos + 1;
            for (int i = 0; i < cantPisos; i++) {
                PolylineOptions p = new PolylineOptions().width(5).color(Color.RED);
                Vector<GroundOverlayOptions> g = new Vector<>();
                misPolilineas.add(p);
                misOverlays.add(g);
            }

            //Agrego puntos a las polilineas segun piso e identifico por que edificios y pisos pasa mi polilinea
            for (int i = 0; i < path.size(); i++) {
                misPolilineas.elementAt(path.elementAt(i).getPiso()).add(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()));
                for (int j = 0; j < cantidad_edificios; j++) {
                    //Veo si ese marcador está dentro de algun edificio con el mapa y la funcion dentroDeLimites
                    //Tratar de optimizar esto
                    if (hashMapBounds.containsKey("ed" + j + "_" + path.elementAt(i).getPiso())) {
                        if (dentroDeLimites(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + path.elementAt(i).getPiso()))) {
                            if (!edificios.contains("ed" + j + "_" + path.elementAt(i).getPiso())) {
                                edificios.add("ed" + j + "_" + path.elementAt(i).getPiso());
                            }
                        }
                    }
                }
            }

            //Agrego los overlays a mi vector
            for (int i = 0; i < edificios.size(); i++) {
                if (hashMapID.containsKey(edificios.elementAt(i))) {
                    misOverlays.elementAt(Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1)))
                            .add(new GroundOverlayOptions()
                                    .positionFromBounds(hashMapBounds.get(edificios.elementAt(i)))
                                    .image(BitmapDescriptorFactory.fromResource(hashMapID.get(edificios.elementAt(i)))));
                }
            }

            //Busco cuales marcadores por piso voy a tener
            marcadoresPiso.add(new MarkerOptions()
                    .position(new LatLng(path.elementAt(0).getLatitud(), path.elementAt(0).getLongitud()))
                    .title(path.elementAt(0).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            for (int i = 1; i < path.size() - 1; i++) {
                if (path.elementAt(i).getPiso() != path.elementAt(i + 1).getPiso()) {
                    marcadoresPiso.add(new MarkerOptions()
                            .position(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()))
                            .title(path.elementAt(i).getNombre())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    marcadoresPiso.add(new MarkerOptions()
                            .position(new LatLng(path.elementAt(i + 1).getLatitud(), path.elementAt(i + 1).getLongitud()))
                            .title(path.elementAt(i + 1).getNombre())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }

            marcadoresPiso.add(new MarkerOptions()
                    .position(new LatLng(path.elementAt(path.size() - 1).getLatitud(), path.elementAt(path.size() - 1).getLongitud()))
                    .title(path.elementAt(path.size() - 1).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            //Cargo las imagenes en el map
            cargarMapaImagnes(path);
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"dibujaCamino",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }

    //Recibo un conjunto de puntos y creo marcadores para todos ellos
    public void mostrarNodos(Vector<Punto> nodos) {
        try {


            misMarcadores.clear();
            misPolilineas.clear();
            marcadoresPiso.clear();
            Vector<String> edificios = new Vector<>();
            cantPisos = 0;
            for (int i = 0; i < nodos.size(); i++) {
                String texto;
                if (nodos.elementAt(i).getPiso() == 0) {
                    texto = "Planta Baja";
                } else {
                    texto = "Piso " + nodos.elementAt(i).getPiso();
                }
                //Cuento la cantidad de pisos en donde encontre lo que busco
                if (nodos.elementAt(i).getPiso() > cantPisos) {
                    cantPisos = nodos.elementAt(i).getPiso();
                }

                //Agrego los marcadores
                misMarcadores.add(new MarkerOptions().position(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud())).title(nodos.elementAt(i).getNombre() + " - " + texto).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                for (int j = 0; j < cantidad_edificios; j++) {
                    //Veo si ese marcador está dentro de algun edificio
                    if (hashMapBounds.containsKey("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                        if (dentroDeLimites(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + nodos.elementAt(i).getPiso()))) {
                            if (!edificios.contains("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                                edificios.add("ed" + j + "_" + nodos.elementAt(i).getPiso());
                            }
                        }
                    }
                }
            }

            //Creo las polilineas y overlays que voy a usar
            cantPisos = cantPisos + 1;
            for (int i = 0; i < cantPisos; i++) {
                Vector<GroundOverlayOptions> g = new Vector<>();
                misOverlays.add(g);
            }

            //Agrego los overlays a mi vector
            for (int i = 0; i < edificios.size(); i++) {
                if (hashMapID.containsKey(edificios.elementAt(i))) {
                    misOverlays.elementAt(Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1)))
                            .add(new GroundOverlayOptions()
                                    .positionFromBounds(hashMapBounds.get(edificios.elementAt(i)))
                                    .image(BitmapDescriptorFactory.fromResource(hashMapID.get(edificios.elementAt(i)))));
                }
            }

            //Cargo imagenes en el map
            cargarMapaImagnes(nodos);
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"mostrarNodos",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }

    public void cambiarPolilinea(int piso){
        try {


            miMapa.clear();
            miMapa.addMarker(miPosicion);
            miMapa.addPolyline(misPolilineas.elementAt(piso));
            miMapa.addMarker(marcadoresPiso.elementAt(2 * piso));
            miMapa.addMarker(marcadoresPiso.elementAt(2 * piso + 1));

            //Agrego los overlays
            if (misOverlays.size() > piso) {
                for (int i = 0; i < misOverlays.elementAt(piso).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
                }
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"cambiarPolilinea",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }

    }

    //Funcion para actualizar los nodos según el piso que se quiera ver
    public void cambiarNodos(int piso){
        try {
            miMapa.clear();
            miMapa.addMarker(miPosicion);
            for (int i = 0; i < misMarcadores.size(); i++) {
                if (piso == 0) {
                    if (misMarcadores.elementAt(i).getTitle().contains("Planta Baja")) {
                        miMapa.addMarker(misMarcadores.elementAt(i));
                    }
                } else {
                    if (misMarcadores.elementAt(i).getTitle().contains("Piso " + piso)) {
                        miMapa.addMarker(misMarcadores.elementAt(i));
                    }
                }
            }

            //Agrego los overlays
            if (misOverlays.size() > piso) {
                for (int i = 0; i < misOverlays.elementAt(piso).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
                }
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"cambiarNodos",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }

    //Funcion para saber si un punto está dentro de ciertos limites
    public boolean dentroDeLimites(LatLng posicion, LatLngBounds bounds){
        try {
            LatLng limiteInfIzquierdo = bounds.southwest;
            LatLng limiteSupDerecho = bounds.northeast;
            boolean esta = true;
            if (posicion.latitude > limiteSupDerecho.latitude || posicion.latitude < limiteInfIzquierdo.latitude || posicion.longitude > limiteSupDerecho.longitude || posicion.longitude < limiteInfIzquierdo.longitude) {
                esta = false;
            }
            return esta;
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"dentroDeLimites",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }

    //hashMap de Posición de nodo - Imagen del nodod
    public void cargarMapaImagnes(Vector<Punto> puntos){
        try {
            hashMapImagenes.clear();
            for (int i = 0; i < puntos.size(); i++) {
                if (puntos.elementAt(i).getImagen() != null) {
                    hashMapImagenes.put(new LatLng(puntos.elementAt(i).getLatitud(), puntos.elementAt(i).getLongitud()), puntos.elementAt(i).getImagen());
                }
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"cargarMapaImagnes",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }

    @Override
    public void onDestroyView() {
        try {
            super.onDestroyView();
            FragmentManager fm = getActivity().getFragmentManager();
            Fragment fragment = (fm.findFragmentById(R.id.map));
            FragmentTransaction ft = fm.beginTransaction();
            ft.remove(fragment);
            ft.commit();
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"onDestroyView",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;
        }
    }
}

