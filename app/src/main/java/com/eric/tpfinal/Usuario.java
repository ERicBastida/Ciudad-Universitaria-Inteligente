package com.eric.tpfinal;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
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
        this.nombre = user.getDisplayName();
        this.imagen = user.getPhotoUrl();
        this.correo = user.getEmail();

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

        return "Usuario{" +
                "key='" + this.key + '\'' +
                ", nombre='" + this.nombre + '\'' +
                ", apellido='" + this.apellido + '\'' +
                ", correo='" + this.correo + '\'' +
                ", pass='" + this.pass + '\'' +
                ", carrera='" + this.carrera + '\'' +
                '}';
    }

}
