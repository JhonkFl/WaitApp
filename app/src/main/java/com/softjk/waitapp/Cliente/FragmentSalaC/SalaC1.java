package com.softjk.waitapp.Cliente.FragmentSalaC;

import static androidx.core.content.ContextCompat.registerReceiver;

import static com.softjk.waitapp.Cliente.E1_Sala_Client.Codigo;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Cliente.AdapterC.AdpSalaC;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Metodos.TiempoTotal;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.TiempoAlarma;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class SalaC1 extends Fragment {

    PreferencesManager preferenceSala, preferencesCliente;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    AdpSalaC mAdapter;
    Query query;

    TextView lblTiempo, lblmsgTiemp, lblNPersonas,btnPago;
    RecyclerView listaSala;
    Button HacerFila;

    public static ViewGroup viewGroup;
    //String Codigo;
    static String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sala1_client, container, false);

        preferencesCliente = new PreferencesManager(getActivity(),"Cliente");
       // Codigo = preferencesCliente.getString("Codigo","");
        preferenceSala = new PreferencesManager(getActivity(),Codigo);

        String idNegocio = preferencesCliente.getString("idNegocioCliente","");
        String UserFila = preferenceSala.getString("UserFila1","No");
        //Double Precio = getIntent().getDoubleExtra("PrecioA",0); Cambiar a instru para fragment

        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPago);
        lblTiempo = view.findViewById(R.id.lblTiempo);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempo);
        HacerFila = view.findViewById(R.id.btnReservarTurno);
        lblNPersonas = view.findViewById(R.id.NPersonas);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroup = view.findViewById(android.R.id.content);
        setUpRecyclerView1(view, idNegocio);

        //Accion Buton
        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceSala.saveString("NSala","1");
                Intent intent = new Intent(getActivity(), E1_Servici_Client.class);
                startActivity(intent);
            }
        });

        btnPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (UserFila.equals("No")){
            HacerFila.setEnabled(true);
        }else if(UserFila.equals("Si")){
            HacerFila.setEnabled(false);
        }

        return view;
    }


    private void setUpRecyclerView1(View view, String idNegocio) {
        listaSala = view.findViewById(R.id.rvSala1Client);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idNegocio+"/Sala1").orderBy("Creacion", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSalaC(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();


        String UserFila = preferenceSala.getString("UserFila1","No");
        String Vacio = preferenceSala.getString("SalaVacio1","");
        String Primeruser = preferenceSala.getString("NFila1", "");
        String EsperaUser = preferenceSala.getString("EsperandoUser1","NoEsperando");
        String Pago = preferenceSala.getString("Pago1","No");
        String accionFila = preferenceSala.getString("AccionesFila1","");
        String UserrServ = preferenceSala.getString("UserServicio1","No");

        VerRecyclerVacio(mAdapter, idNegocio,UserFila,EsperaUser,Primeruser);

        System.out.println("----Sala1------> UserFila:"+UserFila+" Pago:"+Pago + " AccionFila:"+accionFila+" Vacio:"+Vacio+" servicio:"+UserrServ);
        if (UserrServ.equals("Si")){
            lblmsgTiemp.setText("EN SERVICIO");
            lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
        }
    }

    private void VerRecyclerVacio(AdpSalaC mAdapter, String idNegocio, String UserFila, String EsperarUser, String PrimerUser) {
        ReciclerVacio verRecycler = new ReciclerVacio(BD,preferenceSala);
        verRecycler.verificarRecycler("Negocios/"+idNegocio+"/Sala1",mAdapter,idNegocio,"1",getActivity(),resultado -> {

            if (resultado.equals("Vacio")){
                LimpiarDatos.LimpiarEntrar(getActivity(),"1",Codigo);

            } else if (resultado.equals("1Elemt") || resultado.equals("NoVacio")) {
                String NPersonas = String.valueOf(mAdapter.getItemCount());
                lblNPersonas.setText(NPersonas);
                ObtenerTiempoGlobalMostrar(UserFila,idNegocio,EsperarUser,PrimerUser);

            }else {
                System.out.println("Error Lista Vacio Sala1");
            }
        });
    }

    private void ObtenerTiempoGlobalMostrar(String UserFila, String idNegocio, String EsperaUser, String PrimerUser) {
        if (UserFila.equals("Si")){
            if (EsperaUser.equals("Esperando") ){           //&& PrimerUser.equals("Si")  .--- REVISAR SI SE AGREGA
                System.out.println("---Sala1---> Mostrando Tiempo de Traslado de 10 min");
                String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                TiempoGlobalPers.getTiempoTraslado(Colleccion,idUser,lblTiempo,getActivity(),lblmsgTiemp,"Sala1",idNegocio, Codigo, viewGroup);

            }else if (EsperaUser.equals("NoEsperando")){
                System.out.println("---Sala1----> Mostrando Tiempo Espera Alarma "+PrimerUser);
                String Colleccion = "Negocios/"+idNegocio+"/Sala1";
                if (PrimerUser.equals("Si")){
                    TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(),"1",lblmsgTiemp,viewGroup,"Si",Codigo);
                }else {
                    TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(),"1",lblmsgTiemp,viewGroup,"No",Codigo);
                }

            }else {
                System.out.println("---Sala1---> No mostrar Timpo Global User "+EsperaUser+" "+PrimerUser);
            }

        }else {
            System.out.println("---Sala1---> User no esta en la fila Mostrar Tiempo Global");
            TiempoTotal.getTiempoGlobal(idNegocio, "1", lblTiempo, lblmsgTiemp,"No");
        }
    }

}