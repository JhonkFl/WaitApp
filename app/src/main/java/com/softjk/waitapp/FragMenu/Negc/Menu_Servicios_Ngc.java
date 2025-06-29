package com.softjk.waitapp.FragMenu.Negc;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Adapter.Client.AdpNegVinculado;
import com.softjk.waitapp.Adapter.Negc.AdpServiciosNeg;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Modelo.Negocio;
import com.softjk.waitapp.Modelo.ServicNg;
import com.softjk.waitapp.R;
import com.softjk.waitapp.RegistrarServicios;

public class Menu_Servicios_Ngc extends Fragment {
    AdpServiciosNeg mAdapter;
    RecyclerView RecyclerServicios;
    ImageView Nuevo;
    private String idLocal;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    String idUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SearchView Buscar;
    Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_servicios_ngc, container, false);

        Buscar = view.findViewById(R.id.svBuscarServicios);
        Nuevo = view.findViewById(R.id.btnAddNewServi);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        RecyclerServicios = view.findViewById(R.id.listaServicios);

        setUpRecyclerView();
        search_view();

        Nuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegistrarServicios.class);
                intent.putExtra("identificacion","");
                startActivity(intent);
            }
        });

        return view;
    }

    private void setUpRecyclerView() {
        MultiMetds.DynamicColumnsRecicler(getActivity(),RecyclerServicios,150);
        query = mFirestore.collection("Negocios/"+idUser+"/Servicios");//.whereEqualTo("id_user", mAuth.getCurrentUser().getUid());

        FirestoreRecyclerOptions<ServicNg> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ServicNg>().setQuery(query, ServicNg.class).build();

        mAdapter = new AdpServiciosNeg(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        RecyclerServicios.setAdapter(mAdapter);
        mAdapter.startListening();

    }


    private void search_view() {
        Buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    public void textSearch(String s) {
        FirestoreRecyclerOptions<ServicNg> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ServicNg>()
                        .setQuery(query.orderBy("Nombre")
                                .startAt(s).endAt(s + "\uf8ff"), ServicNg.class).build();
        mAdapter = new AdpServiciosNeg(firestoreRecyclerOptions, getActivity());
        mAdapter.startListening();
        RecyclerServicios.setAdapter(mAdapter);
    }
}