package com.eric.ciudaduniversitariainteligente;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class loginFragment extends Fragment implements AdminDB.OnDB_Listener
{
    private ImageView imgLogoUNL;

    private AdminDB baseDatos;
    private Button btnIniciar;
    private Button btnGoToSignUp;
    private Button btnIngresante;
    private TextView txtUser;
    private TextView txtPass;
    private usuarioFragment signUp;
    private usuarioFragment.OnFragmentInteractionListener mListener;
    private LogginCUI log  = new LogginCUI();
    private static final int RC_SIGN_IN = 123;
    private int id_proveedor = 0;



    List<AuthUI.IdpConfig> proveedor = null;

    public loginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);



        btnIniciar =  view.findViewById(R.id.btnIniciar);
        btnGoToSignUp =  view.findViewById(R.id.btnGoToSignUp);
        btnIngresante = view.findViewById(R.id.btnIngresante);
        txtUser =  view.findViewById(R.id.txtUser);
        txtPass =  view.findViewById(R.id.txtPass);
        imgLogoUNL = view.findViewById(R.id.imgLogoUNL);

        try {


            btnIniciar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //El usuario debe ser el nombre del correo sin @servidor.com
                    String user = txtUser.getText().toString();
                    String pass = txtPass.getText().toString();

                    if (hay_conexion()){
                        //                    createSignInIntent();
                        switch (id_proveedor) {

                            //Se ingresa con cuenta de UNL, es decir, cuenta local.
                            case 0:
                                if (user.isEmpty() && pass.isEmpty()) {
                                    Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                                }else {

                                    //Aca debo conectarme a la base de datos de Firebase...

                                    baseDatos = new AdminDB(getActivity());
                                    baseDatos.consultarUsuario(new AdminDB.OnDB_Listener(){

                                        @Override
                                        public void result(int codigo, Usuario usuario, boolean resultado) {
                                            resultado_BD(codigo,usuario,resultado);
                                        }

                                    },user);
//
                                }
                                break;
                            default:
                                createSignInIntent();
                        }
                    }else{
                        Toast.makeText(getActivity(),"Compruebe la conexión a Internet.",Toast.LENGTH_SHORT).show();
                    }

                }

            });

            // Inflate the layout for this fragmen
            btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO: ver si lo dejo en el stack
                    signUp = new usuarioFragment();
                    FragmentTransaction fragmentManager = getActivity().getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.fragment_container, signUp);
                    fragmentManager.addToBackStack(null);
                    fragmentManager.commit();


                }
            });


            btnIngresante.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Toast.makeText(getActivity(),"Te vas a la puta", Toast.LENGTH_SHORT).show();
                    ir_a_CUI(null);

                }
            });

            ArrayList<ItemData> list=new ArrayList<>();
            list.add(new ItemData("Iniciar con cuenta UNL",R.drawable.unl_icon));
            list.add(new ItemData("Iniciar con Facebook",R.drawable.facebook_icon));
            list.add(new ItemData("Iniciar con Google+",R.drawable.google_icon));
            list.add(new ItemData("Iniciar con Twitter",R.drawable.twitter_icon));

            Spinner spinner=(Spinner) view.findViewById(R.id.spinner2);

            SpinnerAdapter adapter= new SpinnerAdapter(getActivity(),R.layout.spinner_layout_signup, R.id.txtItemSpinner,list);

            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    TextView texto = view.findViewById(R.id.txtItemSpinner);


                    asignarServicio(position);

//                    Toast.makeText(getActivity(),"Asignaste " + Integer.toString(position) + ": " + texto.getText(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Another interface callback
                }
            });


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de crear el login.",getActivity());

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

    // Función encargada de preparar el servicio para iniciar sesion al momento de presionar el boton "INICIAR"
    private void asignarServicio(int id_red_social) {

        try {
            AuthUI.IdpConfig prov = null;


            switch (id_red_social) {

                case 0:

                    proveedor = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
                    break;
                case 1: //Facebook
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());
                    break;

                case 2: //Google +
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
                    break;

                case 3: //Twitter
                    //                proveedor = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());;
                    break;

                default:
                    proveedor = null;

            }

            id_proveedor = id_red_social;
        }catch (Exception e){
            log.registrar(this,"asignarServicio",e);
            log.alertar("Ocurrió un error al momento de asignar la red social para iniciar sesión.",getActivity());

        }

    }

    public void createSignInIntent() {
        try {

            if (proveedor != null) {
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(proveedor)
                                .build(),
                        RC_SIGN_IN);
            } else {
                Toast.makeText(getActivity(), "Se debe seleccionar un proveedor para iniciar sesion.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            log.registrar(this,"createSignInIntent",e);
            log.alertar("Ocurrió un error al momento de iniciar sesión con la red social.",getActivity());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RC_SIGN_IN) {
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if (resultCode == getActivity().RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Toast.makeText(getActivity(), "Iniciaste sesion " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    Usuario usuario_red_social = new Usuario();
                    usuario_red_social.copy(user);
                    Toast.makeText(getActivity(), "Bien! ingresaste con una red social : " + usuario_red_social.getNombre(), Toast.LENGTH_SHORT).show();
                    ir_a_CUI(usuario_red_social);

                } else {
                    Toast.makeText(getActivity(), "No se ha podido iniciar sesión.", Toast.LENGTH_SHORT).show();

                }
            }
        }catch (Exception e ){
            log.registrar(this,"onActivityResult",e);
            log.alertar("Ocurrió un error al momento de gestionar el inicio de sesión.",getActivity());

        }
    }

    public void privacyAndTerms() {
        try{
            List<AuthUI.IdpConfig> providers = Collections.emptyList();
            // [START auth_fui_pp_tos]
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTosAndPrivacyPolicyUrls(
                                    "https://example.com/terms.html",
                                    "https://example.com/privacy.html")
                            .build(),
                    RC_SIGN_IN);
        }catch (Exception e ){
            log.registrar(this,"privacyAndTerms",e);
            log.alertar("Ocurrió un error al momento de querer mostrar los terminos de privacidad.",getActivity());

        }

    }


    private void ir_a_CUI(Usuario user){
        try{
            Intent gotoCUI = new Intent(getActivity(),MainActivity.class);
            String mensaje = "";
            if (user != null){
                gotoCUI.putExtra("existe_usuario", true);
                gotoCUI.putExtra("usuario",user.toBundle());

                mensaje = "con usuario -> " + user.getNombre();
            }else{
                gotoCUI.putExtra("existe_usuario", false);
                mensaje = "sin usuario.";
            }
            Toast.makeText(getActivity(),"me voy a CUI "+ mensaje, Toast.LENGTH_SHORT).show();
            startActivity(gotoCUI);
            getActivity().finish();

        }catch (Exception e ){
            log.registrar(this,
                    "ir_a_CUI",e);
            log.alertar("Ocurrió un error al momento de intentar ir a CUI.",getActivity());

    }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Toast.makeText(getActivity(),"Tocaste el boton",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void result(int codigo, Usuario usuario, boolean resultado) {

    }

    public void resultado_BD(int codigo, Usuario usuario, boolean resultado) {

        if (AdminDB.COD_USUARIO_EXISTE == codigo){
            String mensaje = "";

            if (resultado){
                mensaje = "(IR A LA APLICACION) Tu contraseña -> " + usuario.getPass();
                //TODO: ir a la aplicación
                ir_a_CUI(usuario);

            }else{
                mensaje = "No existe el usuario. Por favor registrese o ingrese por alguna red social.";
            }

            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();

        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
