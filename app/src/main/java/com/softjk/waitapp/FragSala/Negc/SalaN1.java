package com.softjk.waitapp.FragSala.Negc;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.softjk.waitapp.Adapter.Negc.AdpSalaNeg;
import com.softjk.waitapp.E1_Servici_Client;
import com.softjk.waitapp.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Metodos.LimpiarDatos;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Metodos.ReciclerVacio;
import com.softjk.waitapp.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class SalaN1 extends Fragment {
    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnPago,MensajeTime;
    Button HacerFila, Mas5, Mas10;
    RecyclerView listaSala;
    public static TextView lblTiempo, lblmsgTiemp;

    AdpSalaNeg mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sala1_neg, container, false);

        preferencesManager = new PreferencesManager(getActivity());
        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPagoNeg);
        lblTiempo = view.findViewById(R.id.lblTiempoNeg);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempoNeg);
        HacerFila = view.findViewById(R.id.btnReservarTurnoNeg);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        Mas5 = view.findViewById(R.id.btn5Mas);
        Mas10 = view.findViewById(R.id.btn10Mas);
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
                preferencesManager.saveString("FilaSala","Sala1");
                preferencesManager.saveString("NSala","1");

                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                intent.putExtra("Origen","Admin");
                startActivity(intent);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Limpiando datos");
                LimpiarDatos.LimpiarSala(getActivity(), "1");
            }
        });

        return view;
    }



    private void setUpRecyclerView1(View view) {
        listaSala = view.findViewById(R.id.rvSala1Neg);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idUser+"/Sala1").orderBy("Creacion",
                Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSalaNeg(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        //Revisar si la lista esta Vacio o si tiene elementos
        ReciclerVacio salaChecker = new ReciclerVacio(BD, preferencesManager);
        salaChecker.verificarRecycler("Negocios/"+idUser+"/Sala1", mAdapter, idUser, "1",getActivity(),resultado -> {
            System.out.println(resultado);
        });

    }


    private void ActualizarTimeGlobal(int valor) {
        BD.collection("Negocios/"+idUser+"/TiempoGlobal").document("Sala1").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String list = preferencesManager.getString("ListaSala1","");
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

                        DatosFirestoreBD.ActualizarDatos(getActivity(), "Negocios/" + idUser + "/TiempoGlobal", "Sala1", map, "Se agrego " + valor + " Minutos más", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if ("Guardado".equals(resultado)) {
                                    // Éxito
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

        BD.collection("Negocios/"+idUser+"/TiempoGlobal").document("Sala1").set(data).addOnSuccessListener(aVoid -> {
            System.out.println("Fecha Hora Guardado en Tiempo Global");
        }).addOnFailureListener(e -> {
            System.out.println("Fecha Hora --- NO --- Guardado en Tiempo Global");
        });

    }


    private void EscucharCambio(AdpSalaNeg mAdapter) {
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                Log.d("RecyclerView", "Se agregaron " + itemCount + " elementos en la posición " + positionStart);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                Log.d("RecyclerView", "Se eliminaron " + itemCount + " elementos desde la posición " + positionStart);
            }

            @Override
            public void onChanged() {
                super.onChanged();
                Log.d("RecyclerView", "Datos cambiaron en el adaptador.");
            }
        });
    }
}