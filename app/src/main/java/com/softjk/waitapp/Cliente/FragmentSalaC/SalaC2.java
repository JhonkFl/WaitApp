package com.softjk.waitapp.Cliente.FragmentSalaC;

import static com.softjk.waitapp.Cliente.E1_Sala_Client.Codigo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.Cliente.AdapterC.AdpSalaC;
import com.softjk.waitapp.Principal.E1_Servici_Client;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.ReciclerVacio;
import com.softjk.waitapp.Sistema.Metodos.TiempoTotal;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.TiempoAlarma;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SalaC2 extends Fragment {
    PreferencesManager preferenceSala,preferencesCliente;
    FirebaseFirestore BD;
    FirebaseAuth mAuth;
    AdpSalaC mAdapter;
    Query query;

    TextView lblServSelecc, lblPrecioSelecc, lblTPagoSelecc,lblPagado;
    TextView lblmsgTiemp,btnPago, lblIdentificacion,lblNPersonas;
    TextView lblTiempoGlobal, lblTiempo;
    CardView CardLista, CardDatos, CardTraslado;
    LinearLayout LinerIdent;
    RecyclerView listaSala;
    ViewGroup viewGroup;
    ImageView ButonList, ImgTraslado, ImgEspera;
    Button HacerFila;

    String idUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sala2_client, container, false);

        preferencesCliente = new PreferencesManager(getActivity(),"Cliente");
        preferenceSala = new PreferencesManager(getActivity(),Codigo);

        String idNegocio = preferencesCliente.getString("idNegocioCliente","");
        String UserFila = preferenceSala.getString("UserFila2","No");

        BD = FirebaseFirestore.getInstance();
        btnPago = view.findViewById(R.id.btnPago2);
        lblTiempo = view.findViewById(R.id.lblTiempo2);
        lblTiempoGlobal = view.findViewById(R.id.lblTiempoGlobal2);
        lblmsgTiemp = view.findViewById(R.id.lblMensajeTiempo2);
        HacerFila = view.findViewById(R.id.btnReservarTurno2);
        lblNPersonas = view.findViewById(R.id.NPersonas2);
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        viewGroup = view.findViewById(android.R.id.content);
        CardLista = view.findViewById(R.id.CardLista2);
        CardDatos = view.findViewById(R.id.CardDatos2);
        CardTraslado = view.findViewById(R.id.CardTraslado2);
        lblServSelecc = view.findViewById(R.id.lblServSelecc2);
        lblPrecioSelecc = view.findViewById(R.id.lblPrecioSelecc2);
        lblTPagoSelecc = view.findViewById(R.id.lblTPagoSelecc2);
        lblPagado = view.findViewById(R.id.lblPagado2);
        lblIdentificacion = view.findViewById(R.id.lblIdentificacion2);
        LinerIdent = view.findViewById(R.id.LinerIdentificacion2);
        ButonList = view.findViewById(R.id.btnLista2);
        ImgTraslado = view.findViewById(R.id.ImgAuto2);
        ImgEspera = view.findViewById(R.id.imgEspera2);

        setUpRecyclerView(view, idNegocio);

        //Accion Buton
        HacerFila.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceSala.saveString("NSala","2");
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
        }else {
            HacerFila.setVisibility(View.GONE);
            btnPago.setVisibility(View.VISIBLE);
            LinerIdent.setVisibility(View.VISIBLE);
        }

        return view;
    }


    private void setUpRecyclerView(View view, String idNegocio) {
        listaSala = view.findViewById(R.id.rvSala2Client);
        listaSala.setLayoutManager(new LinearLayoutManager(getActivity()));
        query= BD.collection("Negocios/"+idNegocio+"/Sala2").orderBy("Creacion", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Sala> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Sala>().setQuery(query, Sala.class).build();
        mAdapter = new AdpSalaC(firestoreRecyclerOptions, getActivity());
        mAdapter.notifyDataSetChanged();
        String UserFila = preferenceSala.getString("UserFila2","No");
        if (UserFila.equals("Si")){ObtenerPosiciondelDocumet();}
        listaSala.setAdapter(mAdapter);
        mAdapter.startListening();

        String Vacio = preferenceSala.getString("SalaVacio2","");
        String Primeruser = preferenceSala.getString("NFila2", "");
        String EsperaUser = preferenceSala.getString("EsperandoUser2","NoEsperando");
        String Pago = preferenceSala.getString("Pago2","No");
        String accionFila = preferenceSala.getString("AccionesFila2","");
        String UserrServ = preferenceSala.getString("UserServicio2","No");

        VerRecyclerVacio(mAdapter, idNegocio,UserFila,EsperaUser,Primeruser);

        System.out.println("----Sala2------> UserFila:"+UserFila+" Pago:"+Pago + " AccionFila:"+accionFila+" Vacio:"+Vacio+" servicio:"+UserrServ);
        if (UserrServ.equals("Si")){
            lblmsgTiemp.setText("EN SERVICIO");
            lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
        }
    }


    private void VerRecyclerVacio(AdpSalaC mAdapter, String idNegocio, String UserFila, String EsperarUser, String PrimerUser) {
        ReciclerVacio verRecycler = new ReciclerVacio(BD,preferenceSala);
        verRecycler.verificarRecycler("Negocios/"+idNegocio+"/Sala2",mAdapter,idNegocio,"2",getActivity(),resultado -> {

            if (resultado.equals("Vacio")){
                LimpiarDatos.LimpiarEntrar(getActivity(),"2",Codigo);
                HacerFila.setVisibility(View.VISIBLE);
                btnPago.setVisibility(View.GONE);
                LinerIdent.setVisibility(View.GONE);
                lblTiempoGlobal.setText("Disponible");
                lblmsgTiemp.setText("No hay Nadie en la Fila");

            } else if (resultado.equals("1Elemt") || resultado.equals("NoVacio")) {
                if (UserFila.equals("Si")){
                    lblTiempoGlobal.setVisibility(View.GONE);
                    lblTiempo.setVisibility(View.VISIBLE);
                    if (EsperarUser.equals("Esperando") ){           //&& PrimerUser.equals("Si")  .--- REVISAR SI SE AGREGA
                        MostrarTiempoPrimerUser(idNegocio);
                    }else if (EsperarUser.equals("NoEsperando")){
                        MostrarTiempoUser(idNegocio,PrimerUser);
                    }else {System.out.println("---Sala2---> No mostrar Timpo Global User "+EsperarUser+" "+PrimerUser);}

                }else {
                    String NPersonas = String.valueOf(mAdapter.getItemCount());
                    lblNPersonas.setText(NPersonas);
                    System.out.println("---Sala2---> User no esta en la fila Mostrar Tiempo Global ");
                    TiempoTotal.getTiempoGlobal(idNegocio, "2", lblTiempoGlobal, lblmsgTiemp,"No");
                }

            }else {
                System.out.println("Error Lista Vacio Sala2");
            }
        });
    }

    private void MostrarTiempoPrimerUser(String idNegocio) {
        String Colleccion = "Negocios/"+idNegocio+"/Sala2";
        DocumentReference documentReference = BD.collection(Colleccion).document(idUser);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                String Estado = documentSnapshot.getString("Estado");
                lblIdentificacion.setText(documentSnapshot.getString("User"));
                System.out.println("Ver Dato Estado BD: "+Estado);

                if (Estado != null) {
                    if (Estado.equals("Trasladandose")) {
                        TiempoGlobalPers.getTiempoTraslado(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, "Sala2", idNegocio, Codigo, viewGroup);
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/b4/a8/a4/b4a8a4625f6b8ef4418150efff833d04.gif",ImgTraslado,"No");
                        CardTraslado.setVisibility(View.VISIBLE);
                        CardLista.setVisibility(View.GONE);
                        System.out.println("---Sala2---> Mostrando Tiempo de Traslado de 10 min");

                    } else if (Estado.equals("En Servicio")) {
                        TiempoTotal.getTiempoGUser(Colleccion, idUser, lblTiempo, lblmsgTiemp, "Si", getActivity(), viewGroup, Codigo);
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/0d/0c/29/0d0c2920396029d02f976fa21a6b2ff6.gif",ImgEspera,"No");
                        lblmsgTiemp.setText("En Servicio");
                        lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
                        CardLista.setVisibility(View.GONE);
                        CardDatos.setVisibility(View.VISIBLE);
                        CardTraslado.setVisibility(View.GONE);
                        ButonList.setVisibility(View.GONE);
                        ObtenerDatosSolicitados(idNegocio);
                    }
                }else {
                    AlertDialogMetds.alertOptionGracias(getActivity(),viewGroup,"2");
                    LimpiarDatos.LimpiarSala(getActivity(),"2",Codigo);
                }
            }
        });
    }


    private void MostrarTiempoUser(String idNegocio,String PrimerUser) {
        String Colleccion = "Negocios/" + idNegocio + "/Sala2";
        DocumentReference documentReference = BD.collection(Colleccion).document(idUser);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot.exists()) {
                    String Estado = documentSnapshot.getString("Estado");
                    System.out.println("Ver Dato Estado BD: " + Estado);

                    if (Estado == null) {
                        System.out.println("---Sala2----> Mostrando Tiempo Espera Alarma " + PrimerUser);
                        if (PrimerUser.equals("Si")) {
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "2", lblmsgTiemp, viewGroup, "Si", Codigo);
                        } else {
                            CardLista.setVisibility(View.GONE);
                            CardDatos.setVisibility(View.VISIBLE);
                            ObtenerDatosSolicitados(idNegocio);
                            MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/80/db/17/80db17d18835b3899456716d177db3fd.gif",ImgEspera,"No");
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "2", lblmsgTiemp, viewGroup, "No", Codigo);
                        }

                    } else if (Estado.equals("En Servicio")) {
                        MultiMetds.IMG(getActivity(), "https://i.pinimg.com/originals/0d/0c/29/0d0c2920396029d02f976fa21a6b2ff6.gif",ImgEspera,"No");
                        lblmsgTiemp.setText("En Servicio");
                        lblmsgTiemp.setTextColor(Color.parseColor("#4CAF50"));
                        lblTiempo.setText("Gracias por su preferencia!! ");
                        lblTiempo.setTextSize(18);
                        //lblTiempo.setTextColor(Color.parseColor("#f5eeee"));
                        System.out.println("********* PrimerUser:" + PrimerUser);

                        ButonList.setVisibility(View.GONE);
                        CardLista.setVisibility(View.GONE);
                        CardDatos.setVisibility(View.VISIBLE);
                        ButonList.setVisibility(View.GONE);
                        ObtenerDatosSolicitados(idNegocio);

                        if (PrimerUser.equals("PrimerUser")) {
                            TiempoAlarma.getTiempoGlobalPersonal(Colleccion, idUser, lblTiempo, getActivity(), "2", lblmsgTiemp, viewGroup, "Si", Codigo);
                        } else {
                            //TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, getActivity(), lblmsgTiemp, viewGroup, Codigo);
                        }
                    }

                }else {
                    AlertDialogMetds.alertOptionGracias(getActivity(),viewGroup,"2");
                    LimpiarDatos.LimpiarSala(getActivity(),"2",Codigo);
                }
            }
        });
    }


    private void ObtenerPosiciondelDocumet() {
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


    private void ObtenerDatosSolicitados(String idNegocio) {
        BD.collection("Negocios/"+idNegocio+"/Sala2").document(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                        AlertDialogMetds.alertOptionGracias(requireActivity(),viewGroup,"2");
                        LimpiarDatos.LimpiarSala(getActivity(),"2",Codigo);
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