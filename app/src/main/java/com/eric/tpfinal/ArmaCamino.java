package com.eric.tpfinal;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

/**
 * Created by Lautaro on 28/11/2016.
 */
public class ArmaCamino{

    private String STRING_MENSAJE = "ArmaCamino/%s => [Causa]: %s , [Mensaje]: %s , [Origen]: %s";
    /*
    Nodos -> Vector de Puntos donde están los nodos y conexiones
    puntoMasCercano -> un objeto de la clase punto que representa el nodo mas cercano a mi posicion
     */
    private Vector<Punto> Nodos;
    private Context contexto;
    private Punto puntoMasCercano = null;
    private String pisoObjetivo = null;

    //Constructor
    public ArmaCamino(Context context){
        try {
            contexto = context;
            Nodos = new Vector<>();
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"ArmaCamino",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    //Funcion para agregar nodos al vector
    public void addNodo(Punto P){
        try {
            Nodos.add(P);
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"addNodo",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    public String getPisoObjetivo(){return pisoObjetivo;}

    //Funcion para armar el camino mediante algoritmo de Costo Uniforme
    public Vector<Punto> camino(String oEdificio, String oNombre){
        Vector<Punto> path = new Vector<>();
        Punto miPosicion = puntoMasCercano;
        Vector<Punto> visitado = new Vector<>();

        try {

            Punto p;
            float costo = 0;

            PriorityQueue<Punto> Cola = new PriorityQueue<>();
            Cola.add(miPosicion);
            visitado.add(miPosicion);

            while (!Cola.isEmpty()) {
                Punto aux = Cola.remove();
                //Si aux es el punto que estoy buscando, corto y empiezo a recorrer hacia atras para armar el camino
                if (aux.getNombre().contains(oNombre) && aux.getEdificio().contains(oEdificio)) {
                    while (aux != null) {
                        path.add(aux);
                        aux = aux.getPadre();
                    }
                    Collections.reverse(path);
                    break;
                }
                //Sino, agrego todos los vecinos de aux a la Cola y sigo buscando
                for (int j = 0; j < aux.cantVecinos(); j++) {
                    Punto vecino = aux.getVecino(j);
                    if (!visitado.contains(vecino)) {
                        vecino.setPadre(aux);
                        vecino.costo = aux.costo + calculaDistancia(new LatLng(vecino.getLatitud(), vecino.getLongitud()), new LatLng(aux.getLatitud(), aux.getLongitud()));
                        visitado.add(vecino);
                        if (!Cola.contains(vecino)) {
                            Cola.add(vecino);
                        }
                        for (Punto i : Cola) {
                            if (i.getLatitud() == vecino.getLatitud() &&
                                    i.getLongitud() == vecino.getLongitud() &&
                                    i.getPiso() == vecino.getPiso() && i.costo > vecino.costo) {
                                Cola.remove(i);
                                Cola.add(vecino);
                            }
                        }
                    }
                }
            }
            if (path.elementAt(path.size() - 1).getPiso() == 0) {
                pisoObjetivo = "Planta Baja";
            } else {
                pisoObjetivo = "Piso " + path.elementAt(path.size() - 1).getPiso();
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"camino",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
        return path;
    }

    //Setear el punto mas cercano. Empiezo tomando al primer elemento del vector
    public void setPuntoMasCercano(LatLng posicion, int pisoActual){
        try {
            puntoMasCercano = Nodos.elementAt(0); //Por defecto, en la entrada de la Ciudad Universitaria
            //Si estoy dentro de la CU, busco dentro
            if (enCiudadUniversitaria(posicion)) {
                double dist = Math.pow(Nodos.elementAt(0).getLatitud() - posicion.latitude, 2) + Math.pow(Nodos.elementAt(0).getLongitud() - posicion.longitude, 2);
                for (int i = 1; i < Nodos.size(); i++) {
                    double dist2 = Math.pow(Nodos.elementAt(i).getLatitud() - posicion.latitude, 2) + Math.pow(Nodos.elementAt(i).getLongitud() - posicion.longitude, 2);
                    if (dist2 < dist && Nodos.elementAt(i).getPiso() == pisoActual) {
                        dist = dist2;
                        puntoMasCercano = Nodos.elementAt(i);
                    }
                }
            }
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"setPuntoMasCercano",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    //Funcion para saber si estoy dentro de la ciudad universitaria
    public boolean enCiudadUniversitaria(LatLng posicion){
        try {
            LatLng limiteInfIzquierdo = new LatLng(-31.641034, -60.674534);
            LatLng limiteSupDerecho = new LatLng(-31.639295, -60.670215);
            boolean esta = true;
            if (posicion.latitude > limiteSupDerecho.latitude || posicion.latitude < limiteInfIzquierdo.latitude || posicion.longitude > limiteSupDerecho.longitude || posicion.longitude < limiteInfIzquierdo.longitude) {
                esta = false;
            }
            return esta;
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"enCiudadUniversitaria",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }

    }

    //Funcion que me devuelve un vector con aquellos nodos que contegan Nombre
    //Lo uso para traerme los baños, bares, etc
    public Vector<Punto> nodosMapa(String Nombre){
        try {
            Vector<Punto> nodos = new Vector<>();
            for (int i = 0; i < Nodos.size(); i++) {
                if (Nodos.elementAt(i).getNombre().contains(Nombre)) {
                    nodos.add(Nodos.elementAt(i));
                }
            }
            return nodos;
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"setPuntoMasCercano",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }

    //Funcion para generar un vector con las aulas por Edifico
    public Vector<Punto> verAulasPorEdificio(String Edificio){
        try{
            Vector<Punto> aulas = new Vector<>();
            for(int i=0;i<Nodos.size();i++){
                if(Nodos.elementAt(i).getEdificio().equals(Edificio) && Nodos.elementAt(i).getNombre().contains("Aula")){
                    aulas.add(Nodos.elementAt(i));
                }
            }
            return aulas;

        }catch (Exception e){


            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"verAulasPorEdificio",e.getCause(),e.getMessage(),e.getClass().toString()));

            throw e;

        }

    }

    //Retorna la cantidad de nodos cargados
    public int cantNodos(){return Nodos.size();}

    //Distancia entre dos puntos, modulo del vector
    public float calculaDistancia(LatLng pos1, LatLng pos2){
        try {
            float dlat = (float) (pos2.latitude - pos1.latitude);
            float dlon = (float) (pos2.longitude - pos1.longitude);
            float dist = (float) Math.sqrt(Math.pow(dlat, 2) + Math.pow(dlon, 2));
            String.format("%.2f", dist);
            return dist;
        }catch (Exception e){

            Log.d("ERROR-CUI",String.format(STRING_MENSAJE,"calculaDistancia",e.getCause(),e.getMessage(),e.getClass().toString()));
            throw e;

        }
    }
}
