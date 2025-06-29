package com.softjk.waitapp.Adapter.Client;

import static com.softjk.waitapp.FragSala.Client.SalaC1.viewGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.E1_Sala_Client;
import com.softjk.waitapp.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Modelo.ServicNg;
import com.softjk.waitapp.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdpServiciosC extends FirestoreRecyclerAdapter<ServicNg, AdpServiciosC.ViewHolder> {
    FirebaseFirestore db;
    Activity activity;
    FragmentManager fm;
    String idNeg,idUser, sala, NSala,listaV;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    PreferencesManager preferencesManager;

    public AdpServiciosC(@NonNull FirestoreRecyclerOptions<ServicNg> options, Activity activity1) {
        super(options);
        this.activity = activity1;
        this.fm = fm;
        preferencesManager = new PreferencesManager(activity);
        idNeg = preferencesManager.getString("idNegocioCliente","");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(activity);
        db = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        sala = preferencesManager.getString("FilaSala",""); //Sala1, Sala2, Sala3
        NSala = preferencesManager.getString("NSala",""); // 1, 2, 3
        listaV = preferencesManager.getString("ListaSala"+NSala,"Vacio");
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpServiciosC.ViewHolder holder, int position, @NonNull ServicNg model) {
        DecimalFormat format = new DecimalFormat("0.00");
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idserv = documentSnapshot.getId();

        holder.NombreServicio.setText(model.getNombre());
        holder.PrecioServicio.setText(format.format(model.getPrecio()));
        String FotoServi = model.getLogo();
        MultiMetds.IMG(activity,FotoServi,holder.FotoServicio,"No");


        holder.btnTomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("User").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.setMessage("Preparando Fila...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        preferencesManager.saveString("UserFila"+NSala,"Si");

                        String Usuario = documentSnapshot.getString("User");//Obtener Datos del Usuario
                        String Perfil = documentSnapshot.getString("Perfil");
                        System.out.println(Usuario+" Solicitando Servicio. "+"Sala"+NSala);

                        int TiempoServ = model.getTiempo();
                        String Servic = model.getNombre();
                        double Precio= model.getPrecio();

                        //Si Lista esta Vacio Agregar Campo Inicio al Tiempo Global
                        if (listaV.equals("Vacio")){
                            preferencesManager.saveString("NFila"+NSala, "PrimerUser");
                            preferencesManager.saveInt("ServPrimerClient"+NSala,TiempoServ);
                            preferencesManager.saveString("EsperandoUser"+NSala,"PrimeraVez");
                            System.out.println("Lista vacio asi que ....");
                            System.out.println("Guardando PrimerUser: Si  "+" TiempoSer: "+TiempoServ+" y actualizando Inicio Global Sala"+NSala);

                            Timestamp fechaHoraActual = Timestamp.now();
                            Map<String, Object> data = new HashMap<>();
                            data.put("Inicio", fechaHoraActual);

                            db.collection("Negocios/"+idNeg+"/TiempoGlobal").document(sala).update(data).addOnSuccessListener(aVoid -> {
                                System.out.println("Fecha Hora Guardado en Tiempo Global");
                                ObtenerTiempo(TiempoServ, Servic, Precio, Usuario,"Si",Perfil);
                            }).addOnFailureListener(e -> {
                                System.out.println("Fecha Hora --- NO --- Guardado en Tiempo Global");
                                Toast.makeText(activity, "Error No se pudo completar el Proceso...", Toast.LENGTH_SHORT).show();
                            });
                        }else {
                            ObtenerTiempo(TiempoServ, Servic, Precio, Usuario,"No", Perfil);
                            preferencesManager.getString("EsperandoUser"+NSala,"NoEsperando");
                            preferencesManager.saveInt("ServClient"+NSala,TiempoServ);
                            System.out.println("Ver datos del servicio seleccionado " + Servic + " " + TiempoServ + " $" + Precio);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void ObtenerTiempo(int TiempoServicio, String NombreServ, double Precio, String Usuario,String PrimerVez,String Perfil) {

        db.collection("Negocios/"+idNeg+"/TiempoGlobal").document(sala).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.contains("Inicio")) {
                    com.google.firebase.Timestamp fechaHora = documentSnapshot.getTimestamp("Inicio");
                    System.out.println("Fecha y hora obtenidas: " + fechaHora.toDate());

                    Long TiempoEspera = documentSnapshot.getLong("Tiempo");
                    GuardarDatos(Usuario, NombreServ, Precio, TiempoServicio , fechaHora.toDate() , Math.toIntExact(TiempoEspera),PrimerVez, Perfil);
                } else {
                    System.out.println("El documento no contiene el campo 'Inicio'.");
                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al Encontrar el Tiempo Global", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void GuardarDatos(String usuario, String NombreServ, double precio, int TiempoServicio, Date InicioServicio, int TiempoEspera,String PrimeraVez, String Perfil) {
        System.out.println("Ver dato Inicio Servicio = "+InicioServicio);
        progressDialog.setMessage("Generando Lista...");
        progressDialog.show();
        preferencesManager.saveString("UserFila"+NSala,"Si");

        DocumentReference id = db.collection("Negocios/"+idNeg+"/"+sala).document(idUser);
        int SumaTimTotal = (TiempoServicio * 60) + TiempoEspera;
        Timestamp fechaHoraCreacionDoc = Timestamp.now();
        Map<String, Object> map = new HashMap<>();
        map.put("idUser", idUser);
        map.put("User", usuario);
        map.put("Servicio", NombreServ);
        map.put("Inicio", InicioServicio);
        map.put("Tiempo", TiempoEspera);
        map.put("Precio", precio);
        map.put("Creacion",fechaHoraCreacionDoc);
        map.put("AdmTiempoTotal",SumaTimTotal);
        map.put("Foto",Perfil);

        System.out.println("Datos listos a Guardar BD");
        db.collection("Negocios/"+idNeg+"/"+sala).document(id.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(activity, "Agregado a la Fila", Toast.LENGTH_SHORT).show();
                System.out.println("Registro completado");
                progressDialog.dismiss();
                ActualizarTiempoGlobal(TiempoServicio,TiempoEspera,precio,PrimeraVez);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al agregar a la sala BD", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarTiempoGlobal(int TiempoServ,int TiempoEspera, double precio,String Primeravez) {

        //Suma de Tiempos
        int TiempoServiSegunds = TiempoServ * 60;
        int NewTiempoSegundos = TiempoEspera +TiempoServiSegunds;
        System.out.println("Tiempo anterior: "+TiempoEspera+" Nuevo Tiempo: "+NewTiempoSegundos);

        DocumentReference id = db.collection("Negocios/"+idNeg+"/TiempoGlobal").document(sala);
        Map<String, Object> map = new HashMap<>();
        map.put("Tiempo", NewTiempoSegundos);
        db.collection("Negocios/"+idNeg+"/TiempoGlobal").document(id.getId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Tiempo Global Actualizado - "+sala);

                String SalasN = preferencesManager.getString("CantidadSalas","");

                if (Primeravez.equals("Si")){
                    AlertDialogMetds.alertOptionNegCerca(activity,viewGroup,idNeg,idUser,NSala);
                }else {
                    AlertDialogMetds.alertOptionPago(activity,viewGroup,"Negocios/"+idNeg+"/Sala"+NSala,idUser);
                    /* Cierra la actividad de la Sala
                    if (E1_Sala_Client.currentInstance != null) {
                        E1_Sala_Client.currentInstance.finish();
                    }
                    //Abrir de Nuevo la Actyvidad Sala
                    Intent i = new Intent(activity, E1_Sala_Client.class);
                    i.putExtra("PrecioA", precio);
                    i.putExtra("NSalas", SalasN);
                    i.putExtra("idNeg", idNeg);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(i);
                    if (activity instanceof Activity) {
                        ((Activity) activity).finish();
                    }
                    activity.finish(); */
                }
                System.out.println("Ver Cantidad Salas para enviar a Sala "+SalasN +" ----->AdpServiClient");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error en Actualizar el Tiempo Global", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @NonNull
    @Override
    public AdpServiciosC.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_servi_client, parent, false);
        return new AdpServiciosC.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView NombreServicio, PrecioServicio;
        ImageView FotoServicio;
        Button btnTomar;
        String Tiempo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            NombreServicio = itemView.findViewById(R.id.lblServicioNeg);
            PrecioServicio = itemView.findViewById(R.id.lblPrecioServNeg);
            FotoServicio = itemView.findViewById(R.id.ImagenServicioNeg);
            btnTomar = itemView.findViewById(R.id.btnTomarServicio);
        }
    }
}
