package com.softjk.waitapp.FragSala.Client;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.softjk.waitapp.Adapter.Client.AdpSalaC;
import com.softjk.waitapp.E1_Servici_Client;
import com.softjk.waitapp.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Metodos.TiempoTotal;
import com.softjk.waitapp.Metodos.LimpiarDatos;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Metodos.TiempoAlarma;
import com.softjk.waitapp.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class SalaC1 extends Fragment {

    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnPago;
    Button HacerFila;
    RecyclerView listaSala;
    TextView lblTiempo, lblmsgTiemp;
    public static ViewGroup viewGroup;

    AdpSalaC mAdapter;
    //AdpSalaItem mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sala1_client, container, false);

        preferencesManager = new PreferencesManager(getActivity());
        String NombreNeg = preferencesManager.getString("NombreNegocio","");
        String SalasNeg = preferencesManager.getString("CantidadSalas","1");
        String idNegocio = preferencesManager.getString("idNegocioCliente","");
        String UserFila = preferencesManager.getString("UserFila1","No");
        //Double Precio = getIntent().getDoubleExtra("PrecioA",0); Cambiar a instru para fragment
        System.out.println("ver idNeg de la Sala1: "+idNegocio);

        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPago);
        lblTiempo = view.findViewById(R.id.lblTiempo);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempo);
        HacerFila = view.findViewById(R.id.btnReservarTurno);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroup = view.findViewById(android.R.id.content);
        setUpRecyclerView1(view, idNegocio);

        //Accion Buton
        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("FilaSala","Sala1");
                preferencesManager.saveString("NSala","1");
                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                startActivity(intent);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Limpiando datos");
                LimpiarDatos.LimpiarSala(getActivity(),"1");
            }
        });

        if (UserFila.equals("No")){
            HacerFila.setEnabled(true);
        }else {
            HacerFila.setEnabled(false);
        }

        return view;
    }


    private void setUpRecyclerView1(View view, String idNegocio) {
        listaSala = view.findViewById(R.id.rvSala1Client);
        System.out.println("Revisando idNeg para Listar" + idNegocio);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idNegocio+"/Sala1").orderBy("Creacion",
                Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
      /*  mAdapter = new AdpSalaC(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();*/

        mAdapter = new AdpSalaC(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        String user1 = preferencesManager.getString("NFila1", "");
        String UserFila = preferencesManager.getString("UserFila1","No");
        String Vacio = preferencesManager.getString("ListaSala1","Vacio");
        String Primeruser = preferencesManager.getString("NFila1", "");
        String EsperaUser = preferencesManager.getString("EsperandoUser1","NoEsperando");
        String Pago = preferencesManager.getString("Pago1","No");
        String accionFila = preferencesManager.getString("AccionesFila1","");
        String UserrServ = preferencesManager.getString("UserServicio1","No");

        VerRecyclerVacio(mAdapter, idNegocio,UserFila,EsperaUser,Primeruser);

        System.out.println("UserFila:"+UserFila+" Pago:"+Pago + " AccionFila:"+accionFila+" Fila:"+user1+" Vacio:"+Vacio+" servicio:"+UserrServ);
        if (UserrServ.equals("Si")){
            lblmsgTiemp.setText("EN SERVICIO");
            lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
        }
    }

    private void VerRecyclerVacio(AdpSalaC mAdapter, String idNegocio, String UserFila, String EsperarUser, String PrimerUser) {
        if (mAdapter.getItemCount() == 0) {

            //Ver si DB esta Vacio
            BD.collection("Negocios/"+idNegocio+"/Sala1").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {// No hay elementos en la colección
                        preferencesManager.saveString("ListaSala1","Vacio");
                        Map<String, Object> map = new HashMap<>();
                        map.put("Tiempo", 0);

                        DatosFirestoreBD.GuardarDatos(getActivity(),"Negocios/"+idNegocio+"/TiempoGlobal","Sala1", map, "No", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if (resultado.equals("Guardado")){
                                    System.out.println("RecyclerView está vacio y Actualizamos Tiempo Global a 0 seg");
                                    LimpiarDatos.LimpiarEntrar(getActivity(),"1");
                                }
                                else {System.out.println("RecyclerView vacio ERROR: Actualizamos Tiempo Global a 0 seg");}
                            }
                        });

                    } else {// Hay elementos en la colección
                        preferencesManager.saveString("ListaSala1","NoVacio");
                        System.out.println("-- Hay elementos en la colección. Obtener Tiempo y Ver User Fila Sala1: "+ UserFila);
                        ObtenerTiempoGlobalMostrar(UserFila,idNegocio,EsperarUser,PrimerUser);

                    }
                } else {
                    System.out.println("Error al obtener los datos: " + task.getException());
                }
            });

        } else { //Si NO esta vacio entonces Obtener tIempo Global de Forestore para Mostrar

            if (mAdapter.getItemCount() > 0) { //Primer Elemento
                Object primerElemento = mAdapter.getItem(0);
                System.out.println("Primer elemento: " + primerElemento.toString());

                if (primerElemento.equals(0) && PrimerUser.equals("PrimerUser") && EsperarUser.equals("Esperando")){
                    System.out.println("******* Mostrando Tiempo Global de 10 min....");
                    String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                    TiempoGlobalPers.getTiempoTraslado(Colleccion,idUser,lblTiempo,getActivity(),lblmsgTiemp,"Sala1",idNegocio, Colleccion, viewGroup);
                }else {
                    if (UserFila.equals("Si")){
                        System.out.println("Mostrar tiempo Alarma ***********");
                        String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                        TiempoAlarma.getTiempoGlobalPersonal(Colleccion,idUser,lblTiempo,getActivity(),"1",lblmsgTiemp,preferencesManager,viewGroup,"No");
                    }else {
                        System.out.println("Mostrar tiempo Global ***********");
                        TiempoTotal.getTiempoGlobal(idNegocio, "1", lblTiempo,lblmsgTiemp,getActivity());
                    }
                }



            } else {
                System.out.println("El RecyclerView está vacío.");
            }

        }

    }

    private void ObtenerTiempoGlobalMostrar(String UserFila, String idNegocio, String EsperaUser, String PrimerUser) {
        if (UserFila.equals("Si")){
            System.out.println("User esta en la Fila y "+EsperaUser);
            if (EsperaUser.equals("Esperando")){
                System.out.println("Mostrando Tiempo Global de 10 min");
                String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                TiempoGlobalPers.getTiempoTraslado(Colleccion,idUser,lblTiempo,getActivity(),lblmsgTiemp,"Sala1",idNegocio, Colleccion, viewGroup);
            }else {
                if (EsperaUser.equals("NoEsperando")){
                    System.out.println("Mostrando Tiempo Espera Alarma "+PrimerUser);
                    String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                    if (PrimerUser.equals("Si")){
                        TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(),"1",lblmsgTiemp,preferencesManager,viewGroup,"Si");
                    }else {
                        TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(),"1",lblmsgTiemp,preferencesManager,viewGroup,"No");
                    }


                   /* Intent intent = new Intent(getActivity(), TimeClient.class);
                    intent.putExtra("Collecction", Colleccion);
                    intent.putExtra("Document", idUser);
                   // getActivity().startService(intent); */
                }else {
                    System.out.println("No mostrar Timpo Global User "+EsperaUser+" "+PrimerUser);
                }

            }
        }else {
            System.out.println("-- User no esta en la fila Mostrar Tiempo Global Total --");

            TiempoTotal.getTiempoGlobal(idNegocio, "1", lblTiempo, lblmsgTiemp,getActivity());
        }
    }

  /*  //Obtener el Dato a travez del Intent que manda el Servicio
    private BroadcastReceiver tiempoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.softjk.waitapp")) {
                String tiempo = intent.getStringExtra("Tiempo");
               lblTiempo.setText(tiempo);

               String Fin = intent.getStringExtra("Fin");
               if (Fin != null ){
                   VincularTimeServi();
                   lblmsgTiemp.setText("En Servicio");
               }
            }
        }
    };

    private void VincularTimeServi() {
        String idNegocio = preferencesManager.getString("idNegocioCliente","");
        String UserFila = preferencesManager.getString("UserFila1","No");
        String esperando1 = preferencesManager.getString("EsperandoUser1","NoEsperando");
        String Colleccion = "Negocios/"+idNegocio+"/Sala1";
        String NS = preferencesManager.getString("NSala","1"); // 1, 2, 3
        int TiempoServicio = preferencesManager.getInt("ServClient"+NS,0);
        int TiempoServSeg = TiempoServicio * 60;
        if (UserFila.equals("Si") && esperando1.equals("NoEsperando")) {

            String actualizarDatos = preferencesManager.getString("ActualizarDatos" + NS, "Si");
            System.out.println("valor Actualizar " + actualizarDatos);

            if (actualizarDatos.equals("Si")) { //Condicion para que solo se actualice una vez
                Timestamp fechaHoraActual = Timestamp.now();
                Map<String, Object> map2 = new HashMap<>();
                map2.put("InicioServ", fechaHoraActual);
                map2.put("TiempoServicio", TiempoServSeg);
                map2.put("Estado", "En Servicio");

                BD.collection(Colleccion).document(idUser).update(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferencesManager.saveString("ActualizarDatos" + NS, "No");
                        System.out.println(" Actualizacion ...exitosa ");
                        TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, viewGroup);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } else {
                System.out.println("NO Actualizar solo mostrar TiempoServi");
                TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, viewGroup);
            }
        }
    } */

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.softjk.waitapp");
       // getActivity().registerReceiver(tiempoReceiver, filter); //Service
    }

    @Override
    public void onStop() {
        super.onStop();
       // getActivity().unregisterReceiver(tiempoReceiver); //Service
    }

}