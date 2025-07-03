package com.softjk.waitapp.Cliente.FragmentMenuC;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class MenuConfi extends Fragment {

    ImageView ImgConf;
    TextView Perfil;
    static TextView NuevoUser;
    Button EditarPerfil, GuardarPerfil;
    LinearLayout LinerPerfil,linerEditarPerfil;
    Switch swichtMode;
    boolean nigthMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore mFirestore;
    String ImgP;
    FirebaseAuth mAuth;
    private static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_confi, container, false);

        ImgConf = view.findViewById(R.id.imgConfiguracion);
        Perfil = view.findViewById(R.id.Perfil);
        EditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        NuevoUser = view.findViewById(R.id.txtNuevoUser);
        GuardarPerfil = view.findViewById(R.id.btnGuardarPerfil);
        swichtMode = view.findViewById(R.id.swMOscoro);
        LinerPerfil = view.findViewById(R.id.linerPerfil);
        linerEditarPerfil = view.findViewById(R.id.linerEditarPerfil);
        mFirestore = FirebaseFirestore.getInstance();

        LinerPerfil.setVisibility(View.GONE);
        linerEditarPerfil.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

      /*  sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
          nigthMode = sharedPreferences.getBoolean("nightMode", false);

        if(nigthMode){
            swichtMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        swichtMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(nigthMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode",false);
                    editor.putString("Oscoro", "si");
                    editor.commit();

                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode",true);
                    editor.putString("Oscoro", "no");
                    editor.commit();
                }
                editor.apply();
            }
        }); */


        GuardarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User = NuevoUser.getText().toString().trim();
                Map<String, Object> map = new HashMap<>();
                map.put("User", User);

                DatosFirestoreBD.GuardarDatos(getActivity(),"User",idUser,map,"Actualizando", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                        if ("Guardado".equals(resultado)) {
                            // Éxito
                        } else {
                            // Error
                        }
                    }
                });

            }
        });

        Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinerPerfil.setVisibility(View.VISIBLE);
                Perfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinerPerfil.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        EditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linerEditarPerfil.setVisibility(View.VISIBLE);
                ontenerUser();
            }
        });

        return view;
    }


    public void ontenerUser(){
        mFirestore.collection("User").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String NUser = documentSnapshot.getString("User");
                NuevoUser.setText(NUser);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });

    }
}