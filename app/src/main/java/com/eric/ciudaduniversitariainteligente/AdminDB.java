package com.eric.ciudaduniversitariainteligente;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDB {

    private DatabaseReference database;
    private String NOMBRE_RAIZ_USUARIOS = "usuarios";
    private DatabaseReference usuariosRef;
    private String TAG = "AlojaFireBase";
    private boolean existe_usuario = false;

    private AdminDB.OnDB_Listener mListener;

    final static public int COD_USUARIO_EXISTE    = 1;
    final static public int COD_USUARIO_AGREGADO  = 2;
    final static public int COD_USUARIO_ELIMINADO = 3;
    final static public int COD_USUARIO_MODIFICADO= 4;



    public AdminDB(Activity actividad)  {

        database = FirebaseDatabase.getInstance().getReference();
        usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS);

        //----------------------------------------------------------


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Usuario value = dataSnapshot.getValue(Usuario.class);

                Log.d(TAG, "Value is: " + value.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
        // [END read_message]
    }
    public interface OnDB_Listener {

        void result(int codigo, Usuario usuario , boolean resultado);

    }


    public void agregarUsuario(final OnDB_Listener escuchador, final Usuario usuario) {


        final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(usuario.getKey());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    escuchador.result(COD_USUARIO_AGREGADO,usuario,false);


                } else {

                    usuariosRef.setValue(usuario);
                    escuchador.result(COD_USUARIO_AGREGADO,usuario,true);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };

        usuariosRef.addListenerForSingleValueEvent(eventListener);



    }

    public boolean modificarUsuario(final Usuario usuario) {

        final boolean[] resultado = {false};

        final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(usuario.getKey());

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usuariosRef.child(usuario.getKey()).setValue(usuario);
//                    Toast.makeText(actividad, "Se ha realizado la actualización de manera exitosa para el usuario : " + usuario.getKey(), Toast.LENGTH_SHORT).show();

                    resultado[0] = true;
                } else {


//                    Toast.makeText(actividad, "Ya se ha registrado el usuario  : " + usuario.getKey() + " de manera exitosa.", Toast.LENGTH_SHORT).show();
                    resultado[0] = false;


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };

        usuariosRef.addListenerForSingleValueEvent(eventListener);

        return resultado[0];

    }

    public void eliminarUsuario(String key) {

        Task<Void> resultado = usuariosRef.child(key).removeValue();

    }

    public void consultarUsuario(final OnDB_Listener escuchador, String key) {

        final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(key);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Usuario usuario_consultado = dataSnapshot.getValue(Usuario.class);

                    escuchador.result(COD_USUARIO_EXISTE,usuario_consultado,true);


                } else {


                    escuchador.result(COD_USUARIO_EXISTE,null,false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
            }
        };

        usuariosRef.addListenerForSingleValueEvent(eventListener);

    }



}