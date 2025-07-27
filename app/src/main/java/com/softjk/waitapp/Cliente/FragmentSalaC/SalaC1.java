package com.softjk.waitapp.Cliente.FragmentSalaC;

import static androidx.core.content.ContextCompat.registerReceiver;

import static com.softjk.waitapp.Cliente.E1_Sala_Client.Codigo;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;
import com.softjk.waitapp.Cliente.AdapterC.AdpSalaC;
import com.softjk.waitapp.Cliente.C1_Menu_Client;
import com.softjk.waitapp.Negocio.B2_RegistNeg;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Metodos.TiempoTotal;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.TiempoAlarma;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalaC1 extends Fragment {

    PreferencesManager preferenceSala, preferencesCliente;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    AdpSalaC mAdapter;
    Query query;

    TextView lblServSelecc, lblPrecioSelecc, lblTPagoSelecc,lblPagado;
    TextView lblmsgTiemp,btnPago, lblIdentificacion,lblNPersonas;
    CardView CardLista, CardDatos, CardTraslado;
    RecyclerView listaSala;
    LinearLayout LinerIdent;
    Button HacerFila;
    ImageView ButonList, ImgTraslado, ImgEspera;

    TextView lblTiempoGlobal, lblTiempo;
    ViewGroup viewGroup;
    String idUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sala1_client, container, false);

        preferencesCliente = new PreferencesManager(getActivity(),"Cliente");
        preferenceSala = new PreferencesManager(getActivity(),Codigo);

        String idNegocio = preferencesCliente.getString("idNegocioCliente","");
        String UserFila = preferenceSala.getString("UserFila1","No");

        BD = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        lblTiempoGlobal = view.findViewById(R.id.lblTiempoGlobal1);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempo);
        HacerFila = view.findViewById(R.id.btnReservarTurno);
        viewGroup = view.findViewById(android.R.id.content);
        lblNPersonas = view.findViewById(R.id.NPersonas);
        lblTiempo = view.findViewById(R.id.lblTiempo);
        btnPago = view.findViewById(R.id.btnPago);
        CardLista = view.findViewById(R.id.CardLista);
        CardDatos = view.findViewById(R.id.CardDatos);
        CardTraslado = view.findViewById(R.id.CardTraslado);
        lblServSelecc = view.findViewById(R.id.lblServSelecc);
        lblPrecioSelecc = view.findViewById(R.id.lblPrecioSelecc);
        lblIdentificacion = view.findViewById(R.id.lblIdentificacion1);
        LinerIdent = view.findViewById(R.id.LinerIdentificacion);
        lblTPagoSelecc = view.findViewById(R.id.lblTPagoSelecc);
        lblPagado = view.findViewById(R.id.lblPagado);
        ButonList = view.findViewById(R.id.btnLista1);
        ImgEspera = view.findViewById(R.id.imgEspera);
        ImgTraslado = view.findViewById(R.id.ImgAuto);
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
                Toast.makeText(getActivity(), "Aun no esta Disponible", Toast.LENGTH_SHORT).show();
            }
        });

        ButonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardDatos.setVisibility(View.GONE);
                CardLista.setVisibility(View.VISIBLE);
                LinerIdent.setVisibility(View.GONE);
            }
        });

        if (UserFila.equals("No")){
            LinerIdent.setVisibility(View.GONE);
            //HacerFila.setEnabled(true);
        }else if(UserFila.equals("Si")){
            //HacerFila.setEnabled(false);
            HacerFila.setVisibility(View.GONE);
            btnPago.setVisibility(View.VISIBLE);
            LinerIdent.setVisibility(View.VISIBLE);
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
        String UserFila = preferenceSala.getString("UserFila1","No");
        if (UserFila.equals("Si")){ObtenerPosiciondelDocumet(mAdapter);}
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

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
                HacerFila.setVisibility(View.VISIBLE);
                btnPago.setVisibility(View.GONE);
                LinerIdent.setVisibility(View.GONE);
                lblTiempoGlobal.setText("Disponible");
                lblmsgTiemp.setText("No hay Nadie en la Fila");
            } else if (resultado.equals("1Elemt") || resultado.equals("NoVacio")) {

                // --- Mostrar Tiempo de acuerdo al Usuario --------------------
                if (UserFila.equals("Si")){
                    lblTiempoGlobal.setVisibility(View.GONE);
                    lblTiempo.setVisibility(View.VISIBLE);
                    if (EsperarUser.equals("Esperando") ){           //&& PrimerUser.equals("Si")  .--- REVISAR SI SE AGREGA
                        MostrarTiempoPrimerUser(idNegocio);
                    }else if (EsperarUser.equals("NoEsperando")){
                        MostrarTiempoUser(idNegocio,PrimerUser);
                    }else {System.out.println("---Sala1---> No mostrar Timpo Global User "+EsperarUser+" "+PrimerUser);}

                }else {
                    String NPersonas = String.valueOf(mAdapter.getItemCount());
                    lblNPersonas.setText(NPersonas);
                    System.out.println("---Sala1---> User no esta en la fila Mostrar Tiempo Global "+ UserFila);
                    TiempoTotal.getTiempoGlobal(idNegocio, "1", lblTiempoGlobal, lblmsgTiemp,"No");
                }

            }else {
                System.out.println("Error Lista Vacio Sala1");
            }
        });
    }

    private void ObtenerPosiciondelDocumet(AdpSalaC mAdapter) {
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

            for (int i=0; i<documentSnapshots.size(); i++){
                DocumentSnapshot doc = documentSnapshots.get(i);
                if (doc.getId().equals(idUser)){
                    lblNPersonas.setText(String.valueOf(i));
                    break;
                }
            }
        });
    }


    private void MostrarTiempoPrimerUser(String idNegocio) {
        DocumentReference documentReference = BD.collection("Negocios/"+idNegocio+"/Sala1").document(idUser);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
               String Estado = documentSnapshot.getString("Estado");
                lblIdentificacion.setText(documentSnapshot.getString("User"));
                System.out.println("Ver Dato Estado BD: "+Estado);

                if (Estado != null) {
                    if (Estado.equals("Trasladandose")) {
                        String Colleccion = "Negocios/" + idNegocio + "/Sala1";
                        TiempoGlobalPers.getTiempoTraslado(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, "Sala1", idNegocio, Codigo, viewGroup);
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/b4/a8/a4/b4a8a4625f6b8ef4418150efff833d04.gif",ImgTraslado,"No");
                        System.out.println("---Sala1---> Mostrando Tiempo de Traslado de 10 min");
                        CardTraslado.setVisibility(View.VISIBLE);
                        CardLista.setVisibility(View.GONE);

                    } else if (Estado.equals("En Servicio")) {
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/0d/0c/29/0d0c2920396029d02f976fa21a6b2ff6.gif",ImgEspera,"No");
                        TiempoTotal.getTiempoGUser("Negocios/" + idNegocio + "/Sala1", idUser, lblTiempo, lblmsgTiemp, "Si", getActivity(), viewGroup, Codigo);
                        lblmsgTiemp.setText("En Servicio");
                        lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
                        CardLista.setVisibility(View.GONE);
                        CardDatos.setVisibility(View.VISIBLE);
                        CardTraslado.setVisibility(View.GONE);
                        ButonList.setVisibility(View.GONE);
                        ObtenerDatosSolicitados(idNegocio);
                    }
                }else {
                    AlertDialogMetds.alertOptionGracias(getActivity(),viewGroup,"1");
                    LimpiarDatos.LimpiarSala(getActivity(),"1",Codigo);
                }
            }
        });
    }


    private void MostrarTiempoUser(String idNegocio,String PrimerUser) {
        DocumentReference documentReference = BD.collection("Negocios/"+idNegocio+"/Sala1").document(idUser);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    String Estado = documentSnapshot.getString("Estado");
                    System.out.println("Ver Dato Estado BD: " + Estado);

                    if (Estado == null) {
                        System.out.println("---Sala1----> Mostrando Tiempo Espera Alarma " + PrimerUser);
                        String Colleccion = "Negocios/" + idNegocio + "/Sala1";
                        if (PrimerUser.equals("Si")) {
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "1", lblmsgTiemp, viewGroup, "Si", Codigo);
                        } else {
                            CardLista.setVisibility(View.GONE);
                            CardDatos.setVisibility(View.VISIBLE);
                            ObtenerDatosSolicitados(idNegocio);
                            MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/80/db/17/80db17d18835b3899456716d177db3fd.gif",ImgEspera,"No");
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "1", lblmsgTiemp, viewGroup, "No", Codigo);
                        }

                    } else if (Estado.equals("En Servicio")) {
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/0d/0c/29/0d0c2920396029d02f976fa21a6b2ff6.gif",ImgEspera,"No");
                        String Colleccion = "Negocios/" + idNegocio + "/Sala1";
                        lblmsgTiemp.setText("En Servicio");
                        //lblmsgTiemp.setTextColor(Color.parseColor("#4CAF50"));
                        lblTiempo.setText("Gracias por su preferencia!! ");
                        lblTiempo.setTextSize(18);
                        //lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
                        ButonList.setVisibility(View.GONE);
                        System.out.println("********* PrimerUser:" + PrimerUser);

                        CardLista.setVisibility(View.GONE);
                        CardDatos.setVisibility(View.VISIBLE);
                        ObtenerDatosSolicitados(idNegocio);

                        if (PrimerUser.equals("PrimerUser")) {
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "1", lblmsgTiemp, viewGroup, "Si", Codigo);
                        } else {
                            //TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, viewGroup, Codigo);
                        }
                    }

                }else {
                    AlertDialogMetds.alertOptionGracias(getActivity(),viewGroup,"1");
                    LimpiarDatos.LimpiarSala(getActivity(),"1",Codigo);
                }
            }
        });
    }

    private void ObtenerDatosSolicitados(String idNegocio) {
        BD.collection("Negocios/"+idNegocio+"/Sala1").document(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.exists()) {
                    Double Prec = value.getDouble("Precio");
                    lblServSelecc.setText(value.getString("Servicio"));
                    lblPrecioSelecc.setText(String.valueOf( "$ "+Prec));
                    lblTPagoSelecc.setText(value.getString("Pago"));
                    lblIdentificacion.setText(value.getString("User"));

                }else {
                    if (getActivity() != null){
                        AlertDialogMetds.alertOptionGracias(requireActivity(),viewGroup,"1");
                        LimpiarDatos.LimpiarSala(getActivity(),"1",Codigo);
                    }

                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        String UserFila = preferenceSala.getString("UserFila1","No");
        if (UserFila.equals("No")){mAdapter.startListening();}

    }
}