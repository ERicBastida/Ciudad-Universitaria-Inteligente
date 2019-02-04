package com.eric.ciudaduniversitariainteligente;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class usuarioFragment extends Fragment implements AdminDB.OnDB_Listener{


    private AdminDB BaseDatos;
    private LottieAnimationView animationView;
    private FloatingActionButton btnUsuario;
    private TextView txtName;
    private TextView txtLastName;
    private AppCompatEditText txtEmail;
    private AppCompatEditText txtPass;
    private AppCompatEditText txtPass2;

    private TextView txtCarrera;
    private Spinner spinner_carreras;
    private loginFragment login;
    private LogginCUI log = new LogginCUI();
    private OnFragmentInteractionListener mListener;
    private Usuario usuario_modificado;

    private boolean MODO_EDICION = true;
    private boolean MODO_EDICION_2 = false;

    private String[] carreras;

    private CircleImageView imagen_perfil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        try {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_usuario, container, false);
            animationView = view.findViewById(R.id.animation_view);
            btnUsuario =  view.findViewById(R.id.btnUsuario);

            // Inflate the layout for this fragment
            txtName =  view.findViewById(R.id.txtName);
            txtLastName =  view.findViewById(R.id.txtLastName);
            txtEmail =  view.findViewById(R.id.txtEmail);
            txtPass =  view.findViewById(R.id.txtPass);
            txtPass2 =  view.findViewById(R.id.txtPass2);
            txtCarrera = view.findViewById(R.id.txtCarrera);
            spinner_carreras =  view.findViewById(R.id.spinner_carreras);
            imagen_perfil = view.findViewById(R.id.profile_image);
            carreras = getResources().getStringArray(R.array.carreras);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.spinner_item_layout, carreras);
            spinner_carreras.setAdapter(adapter);

            txtCarrera.setText(spinner_carreras.getSelectedItem().toString());

            // Si tengo información que mostrar, inhabilito los campos y seteo la información recibida.
            final Bundle info_usuario = getArguments();

            if (info_usuario != null){

                MODO_EDICION = false;

                txtName.setText(info_usuario.getString("nombre"));
                txtLastName.setText(info_usuario.getString("apellido"));
                txtEmail.setText(info_usuario.getString("key")+info_usuario.getString("correo"));
                txtPass.setText(info_usuario.getString("pass"));
                txtPass2.setText(info_usuario.getString("pass"));
                spinner_carreras.setSelection(info_usuario.getInt("carrera"));
                String uri_image = info_usuario.getString("imagen");
                if (uri_image == null) {
                    imagen_perfil.setImageResource(R.drawable.profile_example);
                }else{

                    new DescargarImagenPerfil((CircleImageView) view.findViewById(R.id.profile_image)).execute(uri_image);

                    Toast.makeText(getActivity(),uri_image,Toast.LENGTH_SHORT).show();

                }


                habilitar_campos(false);
                btnUsuario.setImageResource(R.drawable.edit_icon);


            }
            btnUsuario.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (!MODO_EDICION){
                        btnUsuario.setImageResource(R.drawable.save_icon);
                        habilitar_campos(true);
                        MODO_EDICION = !MODO_EDICION;
                        MODO_EDICION_2 = true;

                    }else{
                        String name = txtName.getText().toString();
                        String lastName = txtLastName.getText().toString();
                        String email = txtEmail.getText().toString();
                        String pass = txtPass.getText().toString();
                        String pass2 = txtPass2.getText().toString();


                        if (hay_conexion()){

                            if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty()) {

                                if (spinner_carreras.getSelectedItemPosition() > 0){


                                    int carrera = spinner_carreras.getSelectedItemPosition();

                                    if (pass.equals(pass2)) {
                                        BaseDatos = new AdminDB(getActivity());
                                        String[] correo;
                                        correo  =    email.split("@");
                                        String usuario_key = correo[0];
                                        String server_correo = "@"+correo[1];

                                        //En este caso quire decir que se inicio el fragment con datos y el usuario desea modificarlo.
                                        if (MODO_EDICION_2){

                                            usuario_modificado = new Usuario(usuario_key,name,lastName,server_correo,pass,carrera,info_usuario.getString("imagen"));
                                            BaseDatos.modificarUsuario(new AdminDB.OnDB_Listener(){
                                                                           @Override
                                                                           public void result(int codigo, Usuario usuario, boolean resultado) {
                                                                               resultado_BD(codigo,usuario,resultado);
                                                                           }
                                                                       }
                                                    ,usuario_modificado);
                                        }else {

                                            Usuario nuevo_usuario = new Usuario(usuario_key,name,lastName,server_correo,pass,carrera,info_usuario.getString("imagen"));
                                            BaseDatos.agregarUsuario(new AdminDB.OnDB_Listener(){

                                                                         @Override
                                                                         public void result(int codigo, Usuario usuario, boolean resultado) {
                                                                             resultado_BD(codigo,usuario,resultado);
                                                                         }

                                                                     },
                                                    nuevo_usuario);
                                        }



                                    } else {
                                        Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(getActivity(),"Ingrese una carrera.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(getActivity(),"Compruebe la conexión a internet.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });





            spinner_carreras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("datos", "datos que necesito");
                    txtCarrera.setText(spinner_carreras.getSelectedItem().toString());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Another interface callback
                }
            });


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de gestionar los campos para el inicio de sesion.",getActivity());
        }
        return view;



    }

    //Función encargada de comprobar la conexión a Internet
    private boolean hay_conexion() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void habilitar_campos( boolean is_enable){

        txtName.setEnabled(is_enable);
        txtLastName.setEnabled(is_enable);
        txtEmail.setEnabled(is_enable);
        txtPass.setEnabled(is_enable);
        txtPass2.setEnabled(is_enable);
        txtCarrera.setEnabled(is_enable);
        spinner_carreras.setEnabled(is_enable);



    }
    public void resetear_campos(){

        txtName.clearComposingText();
        txtLastName.clearComposingText();
        txtEmail.clearComposingText();
        txtPass.clearComposingText();
        txtPass2.clearComposingText();

    }

    public void siguiente_pantalla(){
        //Si dentro de CUI, no hace falta ir a algún lado
        if (!MODO_EDICION_2) {
            login = new loginFragment();
            FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, login);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }

    public void result(int codigo, Usuario usuario, boolean resultado) {
        Toast.makeText(getActivity(),"Me llego estooooo : " + usuario, Toast.LENGTH_SHORT).show();
    }

    public void resultado_BD(int codigo, Usuario usuario, boolean resultado) {
        try{
            String mensaje ="";

            switch (codigo){
                case AdminDB.COD_USUARIO_AGREGADO:

                    if (resultado){
                        mensaje = "Se ha agregado exitosamente el usuario : " + usuario.getNombre();
                        resetear_campos();
                        siguiente_pantalla();
                    }else{
                        mensaje = "Ya existe un usuario asocidado con el correo : " + usuario.getKey() + usuario.getCorreo();
                    }
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                    break;
                case AdminDB.COD_USUARIO_MODIFICADO:

                    if (resultado){
                        mensaje = "Se ha modificado exitosamente el usuario : " + usuario.getNombre();
                        resetear_campos();
                        siguiente_pantalla();
                    }else{
                        if (MODO_EDICION_2 == true){
                            BaseDatos.agregarUsuario(new AdminDB.OnDB_Listener(){

                                                         @Override
                                                         public void result(int codigo, Usuario usuario, boolean resultado) {
                                                             resultado_BD(codigo,usuario,resultado);
                                                         }

                                                     },
                                    usuario_modificado);
                            mensaje = "Te has registrado a la aplicación, desde ahora ya puedes ingresar con tu correo a la app.";
                        }else{
                            mensaje = "No se ha podido modificar el usuario.";
                        }

                    }
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
                    break;

            }
        }catch (Exception e){
            log.registrar(this,"resultado_BD",e);
            log.alertar("Ocurrió un error al momento de gestionar el resultado de la base de datos. ",getActivity());

        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Toast.makeText(getActivity(),"AAAh queres irte piyuelo",Toast.LENGTH_SHORT).show();
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Los argumentos (según la documentación de Android) deben ser pasados extrictamente al momento
    // de crear dicho fragment, en caso de enviarlo después no se adjuntarán.
    public static usuarioFragment newInstance(Bundle arguments){
        usuarioFragment f = new usuarioFragment();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }

    public usuarioFragment(){

    }

    private class DescargarImagenPerfil extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DescargarImagenPerfil(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}
