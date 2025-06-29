package com.softjk.waitapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Adapter.Client.AdpBuscarNeg;
import com.softjk.waitapp.Modelo.Negocio;

public class D1_BuscarNeg extends AppCompatActivity {
    AdpBuscarNeg mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    String idUser;
    TextView NombreCliente, CorreoClient;
    SearchView Buscar;
    ImageView logo,publicidad;
    RecyclerView lista;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.d1_buscar_neg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Buscar = findViewById(R.id.swEncontrarNgc);
        lista = findViewById(R.id.listaEncontrarNgc);
        publicidad = findViewById(R.id.imgPublic);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser=mAuth.getCurrentUser().getUid();
        setUpRecyclerView();
        search_view();

        Glide.with(D1_BuscarNeg.this)
                .load("https://elhalal.opennemas.com/media/elhalal/images/2020/11/27/2020112716203679225.gif")
                //.circleCrop()
                .into(publicidad);
    }

    private void setUpRecyclerView() {
        lista = findViewById(R.id.listaEncontrarNgc);
        System.out.println("Revisando Recicle");
       // lista.setLayoutManager(new GridLayoutManager(this,2));
        lista.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        query= mFirestore.collection("Negocios");

        FirestoreRecyclerOptions<Negocio> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Negocio>().setQuery(query, Negocio.class).build();
        mAdapter = new AdpBuscarNeg(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        //lista.setAdapter(mAdapter);
        System.out.println(lista);
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

    public void textSearch(String s){
        FirestoreRecyclerOptions<Negocio> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Negocio>()
                        .setQuery(query.orderBy("Codigo")
                                .startAt(s).endAt(s), Negocio.class).build();
        mAdapter = new AdpBuscarNeg(firestoreRecyclerOptions, this);
        mAdapter.startListening();
        lista.setAdapter(mAdapter);
    }
}