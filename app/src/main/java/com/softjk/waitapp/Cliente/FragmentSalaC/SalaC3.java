package com.softjk.waitapp.Cliente.FragmentSalaC;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Cliente.AdapterC.AdpSalaC;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.TiempoTotal;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.TiempoAlarma;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class SalaC3 extends Fragment {

    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnPago;
    Button HacerFila;
    RecyclerView listaSala;
    TextView lblTiempo, lblmsgTiemp;
    ViewGroup viewGroup;

    AdpSalaC mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sala3_client, container, false);

        preferencesManager = new PreferencesManager(getActivity());
        String SalasNeg = preferencesManager.getString("CantidadSalas","1");
        String idNegocio = preferencesManager.getString("idNegocioCliente","");
        String UserFila = preferencesManager.getString("UserFila3","No");
        System.out.println("ver idNeg de la Sala3--: "+idNegocio);

        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPago3);
        lblTiempo = view.findViewById(R.id.lblTiempo3);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempo3);
        HacerFila = view.findViewById(R.id.btnReservarTurno3);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroup = view.findViewById(android.R.id.content);
        setUpRecyclerView(view, idNegocio);

        //Accion Buton
        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("FilaSala","Sala3");
                preferencesManager.saveString("NSala","3");
                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                startActivity(intent);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Limpiando datos");
                preferencesManager.saveString("NFila3",null);
                preferencesManager.saveString("EsperandoUser3",null);
                preferencesManager.saveString("UserFila3",null);
                preferencesManager.saveString("ActualizarDatos3", null);
                preferencesManager.saveString("Pago3", null);
                preferencesManager.saveString("AccionesFila3",null);
                preferencesManager.saveString("FilaSala",null);
            }
        });

        if (UserFila.equals("No")){
            HacerFila.setEnabled(true);
        }else {
            HacerFila.setEnabled(false);
        }
        return view;
    }



    private void setUpRecyclerView(View view, String idNegocio) {
        listaSala = view.findViewById(R.id.rvSala3Client);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idNegocio+"/Sala3").orderBy("Creacion",
                Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSalaC(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        String user = preferencesManager.getString("NFila3", "");
        String UserFila = preferencesManager.getString("UserFila3","No");
        String Vacio = preferencesManager.getString("ListaSala3","Vacio");
        String Primeruser = preferencesManager.getString("NFila3", "");
        String EsperaUser = preferencesManager.getString("EsperandoUser3","NoEsperando");
        String Pago = preferencesManager.getString("Pago3","No");
        String accionFila = preferencesManager.getString("AccionesFila3","");
        String UserrServ = preferencesManager.getString("UserServicio3","No");

        VerRecyclerVacio(mAdapter, idNegocio,UserFila,EsperaUser,Primeruser);

        System.out.println("Ver valor UserFila: "+UserFila+" Pago: "+Pago + "AccionFila: "+accionFila);
        if (UserrServ.equals("Si")){
            lblmsgTiemp.setText("EN SERVICIO");
            lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
        }
    }

    private void VerRecyclerVacio(AdpSalaC mAdapter, String idNegocio, String UserFila, String EsperarUser, String PrimerUser) {
        if (mAdapter.getItemCount() == 0) {

            //Ver si DB esta Vacio
            BD.collection("Negocios/"+idNegocio+"/Sala3").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {// No hay elementos en la colección
                        preferencesManager.saveString("ListaSala3","Vacio");
                        Map<String, Object> map = new HashMap<>();
                        map.put("Tiempo", 0);

                        DatosFirestoreBD.ActualizarDatos(getActivity(),"Negocios/"+idNegocio+"/TiempoGlobal","Sala3",map,"", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if (resultado.equals("Actualizado")){
                                    LimpiarDatos.LimpiarEntrar(getActivity(),"3");
                                    System.out.println("RecyclerView está vacio y Actualizamos Tiempo Global a 0 seg sala3");
                                }
                            }
                        });

                    } else {// Hay elementos en la colección
                        preferencesManager.saveString("ListaSala3","NoVacio");
                        System.out.println("Hay elementos en la colección. Obtener TiempoGlobal y Ver User Fila Sala3: "+ UserFila);
                        ObtenerTiempoGlobalMostrar(UserFila,idNegocio,EsperarUser,PrimerUser);

                    }
                } else {
                    System.out.println("Error al obtener los datos: " + task.getException());
                }
            });

        } else { //Si NO esta vacio entonces Obtener tIempo Global de Forestore para Mostrar
            System.out.println("Acciones cuando la Lista tiene mas de 2 elementos Sala3");
            if (mAdapter.getItemCount() > 0) { //Primer Elemento
                Object primerElemento = mAdapter.getItem(0);
                System.out.println("Primer elemento Sala3: " + primerElemento.toString());

                if (primerElemento.equals(0) && PrimerUser.equals("PrimerUser") && EsperarUser.equals("Esperando")){
                    System.out.println("Mostrando Tiempo Global de 10 min sala3....");
                    String Colleccion = "Negocios/"+idNegocio+"/Sala3";
                    TiempoGlobalPers.getTiempoTraslado(Colleccion,idUser,lblTiempo,getActivity(),lblmsgTiemp,"Sala3",idNegocio, Colleccion, viewGroup);
                }else {
                    if (UserFila.equals("Si")){
                        String Colleccion = "Negocios/"+idNegocio+"/Sala3";
                        TiempoAlarma.getTiempoGlobalPersonal(Colleccion,idUser,lblTiempo,getActivity(),"3",lblmsgTiemp,preferencesManager,viewGroup,"No");
                    }else {
                        System.out.println("Mostrar tiempo Global ***********");
                        TiempoTotal.getTiempoGlobal(idNegocio, "3", lblTiempo,lblmsgTiemp,"No");
                    }
                }

            } else {
                System.out.println("El RecyclerView está vacío.");
            }

        }

    }


    private void ObtenerTiempoGlobalMostrar(String UserFila, String idNegocio, String EsperaUser, String PrimerUser) {
        System.out.println("Acciones si lista tiene 0 a 1 Elementos para mostrar Tiempo Sala3");
        if (UserFila.equals("Si")){
            System.out.println("User esta en la Fila y "+EsperaUser);
            if (EsperaUser.equals("Esperando")){
                System.out.println("Mostrando Tiempo Global de 10 min");
                String Colleccion = "Negocios/"+idNegocio+"/Sala3";
                TiempoGlobalPers.getTiempoTraslado(Colleccion,idUser,lblTiempo,getActivity(),lblmsgTiemp,"Sala3",idNegocio, Colleccion, viewGroup);
            }else {
                if (EsperaUser.equals("NoEsperando") || PrimerUser.equals("No")){
                    System.out.println("Mostrando Tiempo Global user");
                    String Colleccion = "Negocios/"+idNegocio+"/Sala3";
                    TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(),"3",lblmsgTiemp,preferencesManager,viewGroup,"No");
                }else {
                    System.out.println("No mostrar Timpo Global User");
                }

            }
        }else {
            System.out.println("User no esta en la fila Mostrar Tiempo Global Total");
            String ColleccionDelet = "Negocios/"+idNegocio+"/Sala3";
            TiempoTotal.getTiempoGlobal( idNegocio, "3", lblTiempo,lblmsgTiemp,"No");
        }
    }
}