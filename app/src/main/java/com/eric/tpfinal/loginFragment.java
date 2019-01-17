package com.eric.tpfinal;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class loginFragment extends Fragment  implements AdapterView.OnItemClickListener
{

    private String BD_NAME = "login_bd";
    private Button btnIniciar;
    private Button btnGoToSignUp;
    private Button btnIngresante;
    private TextView txtUser;
    private TextView txtPass;
    private signUpFragment signUp;
    private OnFragmentInteractionListener mListener;
    private LogginCUI log  = new LogginCUI();





    public loginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        final AdminSQLite adminSQL = new AdminSQLite(getActivity(),BD_NAME,null,1);


        btnIniciar = (Button) view.findViewById(R.id.btnIniciar);
        btnGoToSignUp = (Button) view.findViewById(R.id.btnGoToSignUp);
        btnIngresante = view.findViewById(R.id.btnIngresante);

        txtUser = (TextView) view.findViewById(R.id.txtUser);
        txtPass = (TextView) view.findViewById(R.id.txtPass);



        try {


            btnIniciar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String user = txtUser.getText().toString();
                    String pass = txtPass.getText().toString();
                    if (user.isEmpty() && pass.isEmpty()) {
                        Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
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
                    Intent gotoCUI = new Intent(getActivity(),MainActivity.class);
                    startActivity(gotoCUI);
                    getActivity().finish();




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
                    Toast.makeText(getActivity(),"Tocaste " + Integer.toString(position) + ": " + texto.getText(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Another interface callback
                }
            });


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de crear el login",getActivity());

        }
        return view;


    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
                Toast.makeText(getActivity(),"Tocaste " + Integer.toString(arg2),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(getActivity(),"Tocaste " + Integer.toString(i),Toast.LENGTH_LONG).show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
