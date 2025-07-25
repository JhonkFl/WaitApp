package com.softjk.waitapp.Cliente.AdapterC;

import static com.softjk.waitapp.Cliente.E1_Sala_Client.Codigo;
import static com.softjk.waitapp.Cliente.FragmentSalaC.SalaC1.viewGroup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Cliente.E1_Sala_Client;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Modelo.ServicNg;
import com.softjk.waitapp.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdpServiciosC extends FirestoreRecyclerAdapter<ServicNg, AdpServiciosC.ViewHolder> {
    FirebaseFirestore db;
    Activity activity;
    String idNeg,idUser, N,Vacio;
    //String CodigoNeg;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    PreferencesManager preferenceSala, PreferenceCliente;

    public AdpServiciosC(@NonNull FirestoreRecyclerOptions<ServicNg> options, Activity activity1) {
        super(options);
        this.activity = activity1;

        PreferenceCliente = new PreferencesManager(activity,"Cliente");
        idNeg = PreferenceCliente.getString("idNegocioCliente","");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(activity);
        db = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

       // CodigoNeg = PreferenceCliente.getString("Codigo","");
        preferenceSala = new PreferencesManager(activity,Codigo);
        N = preferenceSala.getString("NSala",""); // 1, 2, 3
        Vacio = preferenceSala.getString("SalaVacio"+N,"Si");


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
                //Obtener Datos del Usuario
                db.collection("User").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressDialog.setMessage("Preparando Fila...");
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        preferenceSala.saveString("UserFila"+N,"Si");

                        String Usuario = documentSnapshot.getString("User");//Obtener Datos del Usuario
                        String Perfil = documentSnapshot.getString("Perfil");
                        System.out.println(Usuario+" Solicitando Servicio. "+"Sala"+N);

                        int TiempoServ = model.getTiempo();
                        String Servic = model.getNombre();
                        double Precio= model.getPrecio();

                        //Si Lista esta Vacio Agregar Campo Inicio al Tiempo Global
                        if (Vacio.equals("Si")){
                            preferenceSala.saveString("NFila"+N, "PrimerUser");
                            preferenceSala.saveInt("ServPrimerClient"+N,TiempoServ);
                            preferenceSala.saveString("EsperandoUser"+N,"PrimeraVez");
                            System.out.println("Lista vacio: Guardar Inicio a Tiempo Global y primerUser en preference y ver TiempServ="+TiempoServ+" Sala"+N);

                            Timestamp fechaHoraActual = Timestamp.now();
                            Map<String, Object> data = new HashMap<>();
                            data.put("Inicio", fechaHoraActual);

                            //Actualizar Inicio del Tiempo Global
                            db.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+N).update(data).addOnSuccessListener(aVoid -> {
                                System.out.println("Inicio Guardado en Tiempo Global");
                                ObtenerTiempo(TiempoServ, Servic, Precio, Usuario,"Si",Perfil);
                            }).addOnFailureListener(e -> {
                                System.out.println("Inicio --- NO --- Guardado en Tiempo Global");
                                Toast.makeText(activity, "Error No se pudo completar el Proceso...", Toast.LENGTH_SHORT).show();
                                preferenceSala.saveInt("ServPrimerClient"+N,0);
                                LimpiarDatos.LimpiarEntrar(activity,N,Codigo);
                                progressDialog.dismiss();
                            });
                        }else {
                            preferenceSala.getString("EsperandoUser"+N,"NoEsperando");
                            preferenceSala.saveInt("ServClient"+N,TiempoServ);
                            ObtenerTiempo(TiempoServ, Servic, Precio, Usuario,"No", Perfil);
                            System.out.println("Ver datos del servicio seleccionado " + Servic + " time:" + TiempoServ + " $" + Precio);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Error al obtener los datos del user: Etapa1", Toast.LENGTH_SHORT).show();
                        preferenceSala.saveInt("ServPrimerClient"+N,0);
                        LimpiarDatos.LimpiarEntrar(activity,N,Codigo);
                        progressDialog.dismiss();
                    }
                });
            }
        });

    }

    private void ObtenerTiempo(int TiempoServicio, String NombreServ, double Precio, String Usuario,String PrimerVez,String Perfil) {

        db.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+N).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.contains("Inicio")) {
                    Timestamp fechaHora = documentSnapshot.getTimestamp("Inicio");
                    System.out.println("Fecha y hora obtenidas: " + fechaHora.toDate());

                    Long TiempoEspera = documentSnapshot.getLong("Tiempo");
                    GuardarDatos(Usuario, NombreServ, Precio, TiempoServicio , fechaHora.toDate() , Math.toIntExact(TiempoEspera),PrimerVez, Perfil);
                } else {
                    System.out.println("El documento no contiene el campo 'Inicio'. Etapa2"); //Ver solocion si llega a pasar eso
                    LimpiarDatos.LimpiarEntrar(activity,N,Codigo);
                    AlertDialogMetds.MsgOpenActiv(activity,"Upps! al parecer hubo un error!!","Vuelva a Intentarlo de Nuevo", E1_Sala_Client.class, E1_Sala_Client.class,"Reiniciar","true");
                    progressDialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al Encontrar el Tiempo Global", Toast.LENGTH_SHORT).show();
                LimpiarDatos.LimpiarEntrar(activity,N,Codigo);
                progressDialog.dismiss();
            }
        });

    }

    private void GuardarDatos(String usuario, String NombreServ, double precio, int TiempoServicio, Date InicioServicio, int TiempoEspera,String PrimeraVez, String Perfil) {
        System.out.println("Ver datos---> User:" + usuario + " Serv:"+NombreServ+ " precio:"+precio+ " TimSer:"+TiempoServicio+ " Inicio:"+InicioServicio+ " TimEspera:"+TiempoEspera + " PrimeraVez:"+PrimeraVez + " Perfil:"+Perfil );
        System.out.println("Guardar Datos a: "+"Negocios/"+idNeg+"/Sala"+N+" Doc:"+idUser);
        //progressDialog.setMessage("Generando Lista...");
        //progressDialog.show();
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
        System.out.println("Revisar datos a guardar:-> TimeEspera:"+TiempoEspera+" TimeTotal:"+SumaTimTotal+" idUser:"+idUser);

        DatosFirestoreBD.GuardarDatos(activity,"Negocios/"+idNeg+"/Sala"+N,idUser,map,"Agregando a la Fila", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
                if ("Guardado".equals(resultado)) {
                    System.out.println("Registro User completado-- PrimeraVez:"+PrimeraVez);
                    ActualizarTiempoGlobal(TiempoServicio,TiempoEspera,precio,PrimeraVez);
                } else {
                    Toast.makeText(activity, "Error al registrar los Datos", Toast.LENGTH_SHORT).show();
                    System.out.println("No se podo completar el registro");
                    LimpiarDatos.LimpiarEntrar(activity,N,Codigo);
                }
            }
        });
    }

    private void ActualizarTiempoGlobal(int TiempoServ,int TiempoEspera, double precio,String Primeravez) {
        //Suma de Tiempos
        int TiempoServiSegunds = TiempoServ * 60;
        int NewTiempoSegundos = TiempoEspera +TiempoServiSegunds;
        System.out.println("Tiempo anterior: "+TiempoEspera+" Nuevo Tiempo: "+NewTiempoSegundos);

        Map<String, Object> map = new HashMap<>();
        map.put("Tiempo", NewTiempoSegundos);
        db.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+N).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Tiempo Global Actualizado - Sala"+N);
                String SalasN = preferenceSala.getString("CantidadSalas","");

                if (Primeravez.equals("Si")){
                    AlertDialogMetds.alertOptionNegCerca(activity,viewGroup,idNeg,idUser,N,progressDialog);
                    preferenceSala.saveString("UserFila"+N,"Si");
                    // progressDialog.dismiss();
                }else {
                    AlertDialogMetds.alertOptionPago(activity,viewGroup,"Negocios/"+idNeg+"/Sala"+N,idUser,progressDialog);
                    preferenceSala.saveString("UserFila"+N,"Si");
                    //progressDialog.dismiss();
                }
                System.out.println("Ver Cantidad Salas del Neg para enviar a Sala "+SalasN +" ----->AdpServiClient");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error en Actualizar el Tiempo Global", Toast.LENGTH_SHORT).show();
                System.out.println("Error al Actualizar el Tiempo Global -- Vulver a Intentar");
                //LimpiarDatos.LimpiarEntrar(activity,N,CodigoNeg);
                //progressDialog.dismiss();
                int TiempoServiSegunds = TiempoServ * 60;
                int NewTiempoSegundos = TiempoEspera +TiempoServiSegunds;
                System.out.println("Tiempo anterior: "+TiempoEspera+" Nuevo Tiempo: "+NewTiempoSegundos);
                ActualizarTiempoGlobal(TiempoServ,TiempoEspera,0,Primeravez);
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
