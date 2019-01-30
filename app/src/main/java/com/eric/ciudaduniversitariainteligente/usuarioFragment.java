package com.eric.ciudaduniversitariainteligente;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;

import de.hdodenhof.circleimageview.CircleImageView;


public class usuarioFragment extends Fragment implements AdminDB.OnDB_Listener{


    private AdminDB BaseDatos;
    private LottieAnimationView animationView;
    private FloatingActionButton btnUsuario;
    private TextView txtName;
    private TextView txtLastName;
    private TextView txtEmail;
    private TextView txtPass;
    private TextView txtPass2;
    private TextView txtCarrera;
    private Spinner spinner_carreras;
    private loginFragment login;
    private LogginCUI log = new LogginCUI();
    private OnFragmentInteractionListener mListener;
    private boolean MODO_EDICION = true;

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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.spinner_item_layout, getResources().getStringArray(R.array.carreras));
            spinner_carreras.setAdapter(adapter);

            txtCarrera.setText(spinner_carreras.getSelectedItem().toString());

            // Si tengo información que mostrar, inhabilito los campos y seteo la información recibida.
            Bundle info_usuario = getArguments();

            if (info_usuario != null){
                Toast.makeText(getActivity(),"Me enviaste un usuario",Toast.LENGTH_SHORT).show();
                String texto = "Texto de ejemplo";
                MODO_EDICION = false;

                txtName.setText(info_usuario.getString("nombre"));
                txtLastName.setText(info_usuario.getString("apellido"));
                txtEmail.setText(info_usuario.getString("key")+info_usuario.getString("correo"));
                txtPass.setText(info_usuario.getString("pass"));
                txtPass2.setText(info_usuario.getString("pass"));
                spinner_carreras.setSelection(0);
                String uri_image = info_usuario.getString("imagen");
                if (!uri_image.isEmpty() && uri_image != null) {
                    imagen_perfil.setImageURI(Uri.parse(uri_image));

                }else{
                    imagen_perfil.setImageResource(R.drawable.profile_example2);
                }

                habilitar_campos(false);
                btnUsuario.setImageResource(R.drawable.edit_icon);
//                txtCarrera.setText(info_usuario.getString("carrera"));




            }
            btnUsuario.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (!MODO_EDICION){
                        btnUsuario.setImageResource(R.drawable.save_icon);
                        habilitar_campos(true);
                        MODO_EDICION = !MODO_EDICION;
                    }else{
                        String name = txtName.getText().toString();
                        String lastName = txtLastName.getText().toString();
                        String email = txtEmail.getText().toString();
                        String pass = txtPass.getText().toString();
                        String pass2 = txtPass2.getText().toString();


                        if (hay_conexion()){
                            //TODO: ver si lo dejo en el stack
                            if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty()) {
                                if (!spinner_carreras.getSelectedItem().toString().equals("Elige una carrera")){
                                    String carrera = spinner_carreras.getSelectedItem().toString();
                                    if (pass.equals(pass2)) {
                                        BaseDatos = new AdminDB(getActivity());
                                        String[] correo;
                                        correo  =    email.split("@");
                                        String usuario_key = correo[0];
                                        String server_correo = "@"+correo[1];
                                        Usuario nuevo_usuario = new Usuario(usuario_key,name,lastName,server_correo,pass,carrera);
                                        BaseDatos.agregarUsuario(new AdminDB.OnDB_Listener(){

                                                                     @Override
                                                                     public void result(int codigo, Usuario usuario, boolean resultado) {
                                                                         resultado_BD(codigo,usuario,resultado);
                                                                     }

                                                                 },
                                                nuevo_usuario);

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
                    Toast.makeText(getActivity(),"Asignaste " + Integer.toString(position),Toast.LENGTH_SHORT).show();
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

        login = new loginFragment();

        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, login);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    public void result(int codigo, Usuario usuario, boolean resultado) {
        Toast.makeText(getActivity(),"Me llego estooooo : " + usuario, Toast.LENGTH_SHORT).show();
    }

    public void resultado_BD(int codigo, Usuario usuario, boolean resultado) {

        if (AdminDB.COD_USUARIO_AGREGADO == codigo){
            String mensaje = "";

            if (resultado){
                mensaje = "Se ha agregado exitosamente el usuario : " + usuario.getNombre();
                resetear_campos();
                siguiente_pantalla();
            }else{
                mensaje = "Ya existe un usuario asocidado con el correo : " + usuario.getKey() + usuario.getCorreo();
            }

            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();

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



}