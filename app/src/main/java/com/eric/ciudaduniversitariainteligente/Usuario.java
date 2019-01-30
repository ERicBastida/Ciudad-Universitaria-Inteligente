package com.eric.ciudaduniversitariainteligente;

import android.net.Uri;
import android.os.Bundle;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Usuario {

    private String nombre ="";
    private String key;
    private String apellido;
    private String correo;
    private String pass;
    private String carrera;
    private Uri imagen = null;



    public Usuario(){

    }

    public Usuario(String key, String nombre, String apellido, String correo, String pass, String carrera) {

        this.key = key;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.pass = pass;
        this.carrera = carrera;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void copy(FirebaseUser user){
        String[] nombre_completo = user.getDisplayName().split(" ");
        this.nombre = nombre_completo[0];
        this.imagen = user.getPhotoUrl();


        String[] correo_completo = user.getEmail().split("@");
        this.key = correo_completo[0];
        this.correo = "@"+ correo_completo[1];
        this.apellido = nombre_completo[1];

        this.carrera = "";
        this.pass = "";

    }
    public void copy(Bundle user){

        this.nombre= user.getString("nombre");
        this.apellido= user.getString("apellido");
        this.key= user.getString("key");
        this.correo= user.getString("correo");
        this.pass= user.getString("pass");
        this.carrera= user.getString("carrera");


    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Uri getImagen() {
        return imagen;
    }

    public void setImagen(Uri imagen) {
        this.imagen = imagen;
    }
    @Override
    public String toString() {
        if (this.key != null && this.apellido!=null && this.carrera != null && this.correo != null && this.nombre!=null){
            return "Usuario{" +
                    "key='" + this.key + '\'' +
                    ", nombre='" + this.nombre + '\'' +
                    ", apellido='" + this.apellido + '\'' +
                    ", correo='" + this.correo + '\'' +
                    ", pass='" + this.pass + '\'' +
                    ", carrera='" + this.carrera + '\'' +
                    '}';
        }else{
            return "Usuario nulo";
        }

    }

    //Función encargada de enviar la info del usuario en formato Bundle (Util para pasarlo entre Activities)
    public Bundle toBundle(){
        Bundle usuario_bundle = new Bundle();
        if (this.key != null && this.apellido!=null && this.carrera != null && this.correo != null && this.nombre!=null) {
            usuario_bundle.putString("nombre", this.nombre);
            usuario_bundle.putString("apellido", this.apellido);
            usuario_bundle.putString("key", this.key);
            usuario_bundle.putString("correo", this.correo);
            usuario_bundle.putString("pass", this.pass);
            usuario_bundle.putString("carrera", this.carrera);
            if (this.imagen != null) {
                usuario_bundle.putString("imagen", this.imagen.toString());
            }else{
                usuario_bundle.putString("imagen", null);
            }
        }
        return usuario_bundle;

    }

    public boolean cerrar_sesion(){
        FirebaseAuth user = FirebaseAuth.getInstance();
        if (user != null){
            user.signOut();
            LoginManager.getInstance().logOut();

            return true;
        }else{
            return false;
        }



    }



}