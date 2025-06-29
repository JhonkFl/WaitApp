package com.softjk.waitapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Adapter.Client.AdpServiciosC;
import com.softjk.waitapp.Adapter.Negc.AdpSolicitServiSala;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Modelo.ServicNg;

public class E1_Servici_Client extends AppCompatActivity {
    AdpServiciosC mAdapter;
    AdpSolicitServiSala mAdapterAdmin;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    ImageView Iconolocal;
    TextView NombreLocal;
    private String idUser;
    Query query;
    RecyclerView listaServicioCLi;
    SearchView Buscar;
    static String idNeg;
    static String salaC;
    PreferencesManager preferencesManager;
    public static ViewGroup viewGroupSevClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.e1_servici_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Iconolocal = findViewById(R.id.logoNegServClient);
        NombreLocal = findViewById(R.id.lblListServNomNeg);
        Buscar = findViewById(R.id.svBuscarServ);
        listaServicioCLi = findViewById(R.id.rvListaServices);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        preferencesManager = new PreferencesManager(this);
        viewGroupSevClient = findViewById(android.R.id.content);
        String ventanaOrigen = getIntent().getStringExtra("Origen");

        if (ventanaOrigen == null) {
            idNeg = preferencesManager.getString("idNegocioCliente", "");
            String NombreNeg = preferencesManager.getString("NombreNegocio", "");
            String Logo = preferencesManager.getString("LogoNegocio", "");
            NombreLocal.setText(NombreNeg);
            MultiMetds.IMG(E1_Servici_Client.this, Logo, Iconolocal, "Si");
            setUpRecyclerViewNGC(idNeg);
        }else {
            setUpRecyclerViewNGCAdmin(idUser);
            NombreLocal.setText("Mis Servicios");
            MultiMetds.IMG(E1_Servici_Client.this, "https://media.tenor.com/images/2bd1bf1843e1ddf7957a67589ca72612/tenor.gif", Iconolocal, "Si");
        }

        search_view();
    }

    private void setUpRecyclerViewNGC(String idNeg) {
        System.out.println("Revisando Recicle");
        MultiMetds.DynamicColumnsRecicler(E1_Servici_Client.this,listaServicioCLi,150);
        query = mFirestore.collection("Negocios/"+idNeg+"/Servicios"); //.whereEqualTo("id_Servicios", idUser)

        FirestoreRecyclerOptions<ServicNg> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ServicNg>().setQuery(query, ServicNg.class).build();
        mAdapter = new AdpServiciosC(firestoreRecyclerOptions, this);
        mAdapter.notifyDataSetChanged();
        listaServicioCLi.setAdapter(mAdapter);
        System.out.println(listaServicioCLi);
    }

    private void setUpRecyclerViewNGCAdmin(String idNeg) {
        listaServicioCLi = findViewById(R.id.rvListaServices);
        System.out.println("Revisando Recicle");
        MultiMetds.DynamicColumnsRecicler(E1_Servici_Client.this,listaServicioCLi,150);
        query = mFirestore.collection("Negocios/"+idNeg+"/Servicios"); //.whereEqualTo("id_Servicios", idUser)

        FirestoreRecyclerOptions<ServicNg> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<ServicNg>().setQuery(query, ServicNg.class).build();
        mAdapterAdmin = new AdpSolicitServiSala(firestoreRecyclerOptions, this);
        mAdapterAdmin.notifyDataSetChanged();
        listaServicioCLi.setAdapter(mAdapterAdmin);
        System.out.println(listaServicioCLi);
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
        String ventanaOrigen = getIntent().getStringExtra("Origen");
        if (ventanaOrigen == null){
        mAdapter = new AdpServiciosC(firestoreRecyclerOptions, E1_Servici_Client.this);
        mAdapter.startListening();
        listaServicioCLi.setAdapter(mAdapter);
        }else {
            mAdapterAdmin = new AdpSolicitServiSala(firestoreRecyclerOptions, E1_Servici_Client.this);
            mAdapterAdmin.startListening();
            listaServicioCLi.setAdapter(mAdapterAdmin);
        }
    }

    @Override
    protected void onStart() {
        String ventanaOrigen = getIntent().getStringExtra("Origen");
        if (ventanaOrigen == null){
            mAdapter.startListening();
        }else {
            mAdapterAdmin.startListening();
        }
        super.onStart();
    }
}