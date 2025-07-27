package com.softjk.waitapp.Negocio.FragmentSala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Negocio.AdapterN.AdpSala2Neg;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;


public class SalaN2 extends Fragment {
    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnReparar;
    Button HacerFila, Receso;
    RecyclerView listaSala;
    ImageButton candado;
    public static TextView lblmsgTiemp2;
    public static ViewGroup viewGroupN2;

    AdpSala2Neg mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sala_n2, container, false);

        preferencesManager = new PreferencesManager(getActivity(),"Negocio");
        BD = FirebaseFirestore.getInstance();
        btnReparar = view.findViewById(R.id.btnPagoNeg2);
        lblmsgTiemp2 = view.findViewById(R.id.lblTiempoNeg2);
        HacerFila = view.findViewById(R.id.btnReservarTurnoNeg2);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroupN2 = view.findViewById(android.R.id.content);
        Receso = view.findViewById(R.id.Break2);
        candado = view.findViewById(R.id.CandadoSala2);
        setUpRecyclerView1(view);

        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("FilaSala","Sala2");
                preferencesManager.saveString("NSala","2");

                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                intent.putExtra("Origen","Admin");
                startActivity(intent);
            }
        });

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                //getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });

        Receso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogMetds.DialogReceso(getActivity(),viewGroupN2,idUser,"2");
            }
        });

        candado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("Candado","2");
                candado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#06A60D")));
            }
        });

        String ColorearCandado = preferencesManager.getString("Candado","0");
        if (ColorearCandado.equals("2")){
            candado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#06A60D")));
        }

        return view;
    }


    private void setUpRecyclerView1(View view) {
        listaSala = view.findViewById(R.id.rvSala2Neg);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idUser+"/Sala2").orderBy("Creacion", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSala2Neg(firestoreRecyclerOptions, getActivity());
        FinalizacionClient();
        IniciarTemporizador();
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        //Revisar si la lista esta Vacio o si tiene elementos
        ReciclerVacio salaChecker = new ReciclerVacio(BD, preferencesManager);
        salaChecker.verificarRecycler("Negocios/"+idUser+"/Sala2", mAdapter, idUser, "2",getActivity(),resultado -> {
           System.out.println(resultado);
            if (resultado.equals("Vacio")){
                SharedPreferences prefs = getActivity().getSharedPreferences("alertas_mostradas", getActivity().MODE_PRIVATE);
                prefs.edit().clear().apply(); // Elimina todas las claves
                LimpiarDatos.LimpiarEntrar(getActivity(),"2","Negocio");
            }
        });
    }

    private void IniciarTemporizador() {
        mAdapter.setOnIDContador(id -> {
            TiempoGlobalPers.reiniciarTemporizador("Negocios/" + idUser + "/Sala2", id, lblmsgTiemp2, getActivity(), "2");
        });
    }


    private void FinalizacionClient() {
        mAdapter.setOnItemId(idCliente -> {
            DocumentReference docRef = FirebaseFirestore.getInstance()
                    .collection("Negocios")
                    .document(idUser)
                    .collection("Sala2")
                    .document(idCliente);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()){
                    Sala model = documentSnapshot.toObject(Sala.class);
                    String foto = model.getFoto();
                    String nombreUser = model.getUser();
                    String servicio = model.getServicio();
                    String tipoPago = model.getPago();
                    int precio = model.getPrecio();

                    if (nombreUser.equals("RECESO")){
                        Toast.makeText(getActivity(), "Eliminar Documento", Toast.LENGTH_SHORT).show();
                        DatosFirestoreBD.EliminarDocument(getActivity(), "Negocios/"+idUser+"/Sala2", idCliente, "Receso Finalizado", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                System.out.println("Documento eliminado correctamente.");
                                LimpiarDatos.LimpiarEntrar(getActivity(),"2","Negocio");
                                Intent intent = getActivity().getIntent();
                                //getActivity().overridePendingTransition(0, 0);
                                getActivity().finish();
                                getActivity().overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        });
                    }else {
                        AlertDialogMetds.alertOptionMSG(getActivity(), viewGroupN2, "Negocios/" + idUser + "/Sala2",
                                idCliente, foto, nombreUser, servicio, tipoPago, String.valueOf(precio));
                    }
                }
            });
        });
    }

}