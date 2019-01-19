package com.eric.tpfinal;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class loginFragment extends Fragment
{
    private ImageView imgLogoUNL;
    private String BD_NAME = "login_bd";
    private Button btnIniciar;
    private Button btnGoToSignUp;
    private Button btnIngresante;
    private TextView txtUser;
    private TextView txtPass;
    private signUpFragment signUp;
    private signUpFragment.OnFragmentInteractionListener mListener;
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
        final AdminSQLite adminSQL = new AdminSQLite(getActivity(),BD_NAME,null,1);


        btnIniciar =  view.findViewById(R.id.btnIniciar);
        btnGoToSignUp =  view.findViewById(R.id.btnGoToSignUp);
        btnIngresante = view.findViewById(R.id.btnIngresante);
        txtUser =  view.findViewById(R.id.txtUser);
        txtPass =  view.findViewById(R.id.txtPass);
        imgLogoUNL = view.findViewById(R.id.imgLogoUNL);

        try {


            btnIniciar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String user = txtUser.getText().toString();
                    String pass = txtPass.getText().toString();

                    switch (id_proveedor) {
                        //Se ingresa con cuenta de UNL, es decir, cuenta local.
                        case 0:
                            if (user.isEmpty() && pass.isEmpty()) {
                                Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                            }else {

                                //Aca debo conectarme a la base de datos de Firebase...
                                SQLiteDatabase BaseDeDatos = adminSQL.getWritableDatabase();

                                Cursor fila = BaseDeDatos.rawQuery("SELECT * FROM person where email = '" + user + "'", null);

                                if (fila.moveToFirst()) {

                                    Toast.makeText(getActivity(), "Tu contraseña es: "+fila.getString(fila.getColumnIndex("pass")), Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(getActivity(), "No existe el usuario", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getActivity(), "Registrese.", Toast.LENGTH_SHORT).show();


                                }
                                BaseDeDatos.close();
                            }
                            break;

                        default:
                            createSignInIntent();

                    }

                }

            });

            // Inflate the layout for this fragmen
            btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //TODO: ver si lo dejo en el stack
                    signUp = new signUpFragment();
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
                    TextView texto = view.findViewById(R.id.txtItemSpinner);


                    asignarServicio(position);

                    Toast.makeText(getActivity(),"Asignaste " + Integer.toString(position) + ": " + texto.getText(),Toast.LENGTH_SHORT).show();
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

    // Función encargada de preparar el servicio para iniciar sesion al momento de presionar el boton "INICIAR"
    private void asignarServicio(int id_red_social) {

        try {
            AuthUI.IdpConfig prov = null;


            switch (id_red_social) {

                case 1: //Facebook
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());
                    break;

                case 2: //Google +
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
                    break;

                case 3: //Twitter
                    //                proveedor = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());
                    proveedor = null;
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


    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    public void delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });

    }

    public void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Collections.emptyList();

        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.my_great_logo)      // Set logo drawable
                        .setTheme(R.style.MySuperAppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
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
                    ir_a_CUI(user);

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






    private void ir_a_CUI(FirebaseUser user){
        try{
            Intent gotoCUI = new Intent(getActivity(),MainActivity.class);
            gotoCUI.putExtra("user",user);
            startActivity(gotoCUI);
            getActivity().finish();
        }catch (Exception e ){
            log.registrar(this,"ir_a_CUI",e);
            log.alertar("Ocurrió un error al momento de intentar ir a CUI.",getActivity());

    }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
