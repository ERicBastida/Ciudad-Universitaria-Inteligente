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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class loginFragment extends Fragment {

    private String BD_NAME = "login_bd";
    private Button btnIniciar;
    private Button btnGoToSignUp;
    private Button btnIngresante;
    private TextView txtUser;
    private TextView txtPass;
    private signUpFragment signUp;
    private OnFragmentInteractionListener mListener;





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



        }catch (Exception e){
            Toast.makeText(getActivity(),"Elololsoksdjf", Toast.LENGTH_SHORT).show();
        }
        return view;


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
