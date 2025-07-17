package com.softjk.waitapp.Negocio.FragmentSala;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Negocio.AdapterN.AdpSalaNeg;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

public class SalaN1 extends Fragment {
    PreferencesManager preferencesManager;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    Query query;

    TextView btnReparar,MensajeTime;
    Button HacerFila, Mas5, Mas10;
    RecyclerView listaSala;
    public static TextView lblTiempo, lblmsgTiemp;
    public static ViewGroup viewGroup;

    AdpSalaNeg mAdapter;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sala1_neg, container, false);

        preferencesManager = new PreferencesManager(getActivity(),"Negocio");
        BD = FirebaseFirestore.getInstance();
        btnReparar = view.findViewById(R.id.btnPagoNeg);
        lblTiempo = view.findViewById(R.id.lblTiempoNeg);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempoNeg);
        HacerFila = view.findViewById(R.id.btnReservarTurnoNeg);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroup = view.findViewById(android.R.id.content);
        setUpRecyclerView1(view);

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

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // LimpiarDatos.LimpiarSala(getActivity(), "1");

                Intent intent = getActivity().getIntent();
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
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

        mAdapter = new AdpSalaNeg(firestoreRecyclerOptions, getActivity(), new AdpSalaNeg.TiempoListener() {
            @Override
            public void onSolicitarTemporizador(String path, String idDoc, TextView contadorTextView, String Foto,String Nombre) {
                TiempoGlobalPers.getTiempoItemPers(path, idDoc, contadorTextView,Foto,Nombre,viewGroup,getActivity());

            }
        });

        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        //Revisar si la lista esta Vacio o si tiene elementos
        ReciclerVacio salaChecker = new ReciclerVacio(BD, preferencesManager);
        salaChecker.verificarRecycler("Negocios/"+idUser+"/Sala1", mAdapter, idUser, "1",getActivity(),resultado -> {
            System.out.println(resultado);
            if (resultado.equals("Vacio")){
                SharedPreferences prefs = getActivity().getSharedPreferences("alertas_mostradas", getActivity().MODE_PRIVATE);
                prefs.edit().clear().apply(); // Elimina todas las claves
            }
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