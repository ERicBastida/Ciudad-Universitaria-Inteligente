package com.eric.ciudaduniversitariainteligente;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Lautaro on 29/11/2016.
 */
public class MyLocationListener implements LocationListener {

    // String utilizado para el tracking de errores

    private LogginCUI log = new LogginCUI();

    //mainActivity es el mapFragment
    private MapsFragment mainActivity;

    public void setMainActivity(MapsFragment mainActivity) throws Exception{
        try {
            this.mainActivity = mainActivity;

        }catch (Exception e){
            log.registrar(this,"setMainActivity",e);
            throw e;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        try {
            mainActivity.setLat(loc.getLatitude());
            mainActivity.setLon(loc.getLongitude());

            /*Utilizo esto para actualizar mi marcador en el mapa
            Si el mapa no está creado, llamo a onMapReady
            Sino, a actualizaPosicion que solamente mueve el marcador
            */
            if (mainActivity.miMapa == null) {
                mainActivity.onMapReady(mainActivity.miMapa);
            } else {
                if (Math.abs(loc.getLatitude() - mainActivity.getLat() + loc.getLongitude() - mainActivity.getLon()) < 0.00025) {   //Revisar esto
                    mainActivity.actualizaPosicion();
                }
            }
        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es desactivado
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es activado

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Este mŽtodo se ejecuta cada vez que se detecta un cambio en el
        // status del proveedor de localizaci—n (GPS)
        // Los diferentes Status son:
        // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
        // TEMPORARILY_UNAVAILABLE -> Temp˜ralmente no disponible pero se
        // espera que este disponible en breve
        // AVAILABLE -> Disponible
    }
}
