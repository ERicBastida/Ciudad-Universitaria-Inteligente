package com.eric.tpfinal;

import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
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

import com.airbnb.lottie.LottieAnimationView;


public class signUpFragment extends Fragment {


    private String BD_NAME = "login_bd";
    private LottieAnimationView animationView;

    private Button btnRegistrar;

    private TextView txtName;
    private TextView txtLastName;
    private TextView txtEmail;
    private TextView txtPass;
    private TextView txtPass2;

    private loginFragment login;
    private LogginCUI log = new LogginCUI();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        try {
            animationView = (LottieAnimationView) view.findViewById(R.id.animation_view);
            btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);
            // Inflate the layout for this fragment
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtLastName = (TextView) view.findViewById(R.id.txtLastName);
            txtEmail = (TextView) view.findViewById(R.id.txtEmail);
            txtPass = (TextView) view.findViewById(R.id.txtPass);
            txtPass2 = (TextView) view.findViewById(R.id.txtPass2);

            final AdminSQLite adminSQL = new AdminSQLite(getActivity(), BD_NAME, null, 1);

            btnRegistrar.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    String name = txtName.getText().toString();
                    String lastName = txtLastName.getText().toString();
                    String email = txtEmail.getText().toString();
                    String pass = txtPass.getText().toString();
                    String pass2 = txtPass2.getText().toString();
                    //TODO: ver si lo dejo en el stack
                    if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty()) {
                        if (pass.equals(pass2)) {
                            SQLiteDatabase database = adminSQL.getWritableDatabase();
                            ContentValues person = new ContentValues();
                            person.put("name", name);
                            person.put("last_name", lastName);
                            person.put("pass", pass);
                            person.put("email", email);
                            //person.put("user",email);


                            Cursor fila = database.rawQuery("SELECT * FROM person where email = '" + email + "'", null);
                            if (fila.moveToFirst()) {
                                Toast.makeText(getActivity(), "Ya existe un usuario con el mismo correo.", Toast.LENGTH_SHORT).show();
                            } else {
                                // insert retorna un valor negativo en caso de algún tipo de error
                                if (database.insert("person", null, person) > 0) {


                                    Toast.makeText(getActivity(), "Se ha registrado con éxito.", Toast.LENGTH_SHORT).show();
                                    login = new loginFragment();

                                    FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.container, login);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                } else {
                                    txtName.clearComposingText();
                                    txtLastName.clearComposingText();
                                    txtEmail.clearComposingText();
                                    txtPass.clearComposingText();
                                    txtPass2.clearComposingText();


                                    Toast.makeText(getActivity(), "No se ha registrado.", Toast.LENGTH_SHORT).show();

                                }


                            }
                            database.close();


                        } else {
                            Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }

                        Toast.makeText(getActivity(), "Todos los datos estan completos", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de gestionar los compos para el inicio de sesion.",getActivity());
        }
        return view;



    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
