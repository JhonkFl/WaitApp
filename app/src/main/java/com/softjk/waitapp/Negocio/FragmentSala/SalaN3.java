package com.softjk.waitapp.Negocio.FragmentSala;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Negocio.AdapterN.AdpSala3Neg;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class SalaN3 extends Fragment {
    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnPago,MensajeTime;
    Button HacerFila, Mas5, Mas10;
    RecyclerView listaSala;
    public static TextView lblTiempo, lblmsgTiemp;
    public static ViewGroup viewGroupN3;

    AdpSala3Neg mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sala_n3, container, false);

        preferencesManager = new PreferencesManager(getActivity(),"Negocio");
        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPagoNeg3);
        lblTiempo = view.findViewById(R.id.lblTiempoNeg3);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempoNeg3);
        HacerFila = view.findViewById(R.id.btnReservarTurnoNeg3);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        Mas5 = view.findViewById(R.id.btn5Mas3);
        Mas10 = view.findViewById(R.id.btn10Mas3);
        viewGroupN3 = view.findViewById(android.R.id.content);
        setUpRecyclerView1(view);


        Mas5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarTimeGlobal(5);
            }
        });

        Mas10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarTimeGlobal(10);
            }
        });

        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("FilaSala","Sala3");
                preferencesManager.saveString("NSala","3");

                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                intent.putExtra("Origen","Admin");
                startActivity(intent);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Limpiando datos");
                LimpiarDatos.LimpiarSala(getActivity(), "3","Negocio");
            }
        });

        return view;
    }



    private void setUpRecyclerView1(View view) {
        listaSala = view.findViewById(R.id.rvSala3Neg);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idUser+"/Sala3").orderBy("Creacion",
                Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSala3Neg(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        //Revisar si la lista esta Vacio o si tiene elementos
        ReciclerVacio salaChecker = new ReciclerVacio(BD, preferencesManager);
        salaChecker.verificarRecycler("Negocios/"+idUser+"/Sala3", mAdapter, idUser, "3",getActivity(),resultado -> {
            System.out.println(resultado);
        });

    }


    private void ActualizarTimeGlobal(int valor) {
        BD.collection("Negocios/"+idUser+"/TiempoGlobal").document("Sala3").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String list = preferencesManager.getString("SalaVacio3","");
                int Tiempo = Math.toIntExact(documentSnapshot.getLong("Tiempo"));
                String Tim = String.valueOf(Tiempo);
                System.out.println("Ver valor Tiempo "+Tim+" + "+valor);

                if (documentSnapshot.exists()){
                    if (Tim.equals("") || Tim.isEmpty() || Tim == null || list.equals("") || list == null){
                        System.out.println("No Existe el Dato Tiempo");
                        GuadarInicioServ();
                    }else {
                        int ConvertSegds = valor * 60;
                        int NuevoTime = Tiempo + ConvertSegds;

                        Map<String, Object> map = new HashMap<>();
                        map.put("Tiempo", NuevoTime);

                        DatosFirestoreBD.ActualizarDatos(getActivity(), "Negocios/" + idUser + "/TiempoGlobal", "Sala3", map, "Se agrego " + valor + " Minutos más", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if ("Guardado".equals(resultado)) {
                                    String idUser0 = preferencesManager.getString("Client0-3","");
                                    int AdminTime = preferencesManager.getInt("TimeAdmin3",0);
                                    int NewTimeUser = AdminTime + ConvertSegds;
                                    System.out.println("Actualizar user0: TiempoActual:"+AdminTime+" -IDClient:"+idUser0);
                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("AdmTiempoTotal", NewTimeUser);

                                    DatosFirestoreBD.ActualizarDatos(getActivity(), "Negocios/" + idUser + "/Sala3", idUser0, map2, "Se agrego " + valor + " Minutos más", new DatosFirestoreBD.GuardarCallback() {
                                        @Override
                                        public void onResultado(String resultado) {
                                            System.out.println("Negocios/" + idUser + "/Sala3"+" -Documt: "+idUser0+" Campo: "+"AdmTiempoTotal:"+NewTimeUser);
                                        }
                                    });
                                } else {
                                    // Error
                                }
                            }
                        });


                        System.out.println("Ver new valor Tiempo "+NuevoTime);
                    }
                }else {
                    System.out.println("No Existe el Documento");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void GuadarInicioServ() {
        Timestamp fechaHoraActual = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("Inicio", fechaHoraActual);

        BD.collection("Negocios/"+idUser+"/TiempoGlobal").document("Sala3").set(data).addOnSuccessListener(aVoid -> {
            System.out.println("Fecha Hora Guardado en Tiempo Global");
        }).addOnFailureListener(e -> {
            System.out.println("Fecha Hora --- NO --- Guardado en Tiempo Global");
        });

    }
}