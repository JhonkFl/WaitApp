package com.softjk.waitapp.Adapter.Negc;

import static com.softjk.waitapp.E1_Servici_Client.viewGroupSevClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.softjk.waitapp.E1_Sala_Neg;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Modelo.ServicNg;
import com.softjk.waitapp.R;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdpSolicitServiSala extends FirestoreRecyclerAdapter<ServicNg, AdpSolicitServiSala.ViewHolder> {
    FirebaseFirestore db;
    Activity activity;
    FragmentManager fm;
    static String idUser, sala, NSala,listaV;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    PreferencesManager preferencesManager;
    static String Pago;

    public AdpSolicitServiSala(@NonNull FirestoreRecyclerOptions<ServicNg> options, Activity activity1) {
        super(options);
        this.activity = activity1;
        this.fm = fm;
        preferencesManager = new PreferencesManager(activity);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(activity);
        db = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();

        sala = preferencesManager.getString("FilaSala",""); //Sala1, Sala2, Sala3
        NSala = preferencesManager.getString("NSala",""); // 1, 2, 3
        listaV = preferencesManager.getString("ListaSala"+NSala,"Vacio");
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSolicitServiSala.ViewHolder holder, int position, @NonNull ServicNg model) {
        DecimalFormat format = new DecimalFormat("0.00");
        holder.NombreServicio.setText(model.getNombre());
        holder.PrecioServicio.setText(format.format(model.getPrecio()));
        String FotoServi = model.getLogo();
        MultiMetds.IMG(activity,FotoServi,holder.FotoServicio,"No");

        holder.btnTomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Serv = model.getNombre();
                double Prec = model.getPrecio();
                int TiemSer = model.getTiempo();
                Megs(activity,Serv,Prec,TiemSer,viewGroupSevClient);
            }
        });
    }

    public void Megs(Activity activity, String Servi, double Prec, int Tiempo, ViewGroup viewGroup1) {
        Button Agregar;
        EditText User;
        RadioGroup Grupo;
        ImageButton Cerra;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view1 = LayoutInflater.from(activity).inflate(R.layout.z_custom_dialog_info, viewGroup1, false);
        builder.setCancelable(false);
        builder.setView(view1);

        User = view1.findViewById(R.id.itemNombCliAdmin);
        Agregar = view1.findViewById(R.id.btnokAdmin);
        Grupo = view1.findViewById(R.id.RdioGrupAdmin);
        Cerra = view1.findViewById(R.id.MS_Cerra);

        final AlertDialog alertDialogs = builder.create();
        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Grupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) { // Verifica que haya un RadioButton seleccionado
                    RadioButton selectedRadioButton = group.findViewById(checkedId);
                    Pago = selectedRadioButton.getText().toString(); // Obtiene el texto del seleccionado
                    System.out.println(Pago);
                }
            }
        });

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = Grupo.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(activity, "Agregue un Metodo de Pago!!!", Toast.LENGTH_SHORT).show();
                } else if (User.getText().toString().isEmpty()){
                    User.requestFocus();
                    User.setError("Agregue el Nombre del Cliente");
                }else {
                    ClickAgregar(User,Tiempo,Servi,Prec,alertDialogs);
                }
            }
        });




        Cerra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogs.dismiss();
            }
        });
        alertDialogs.show();
    }

    private void ClickAgregar(EditText User,int Tiempo,String Servi, double Prec, AlertDialog alertDialogs){

        String Usuario = String.valueOf(User.getText());
        progressDialog.setMessage("Procesando...");
        progressDialog.show();
        //Si Lista esta Vacio Agregar Campo Inicio al Tiempo Global
        if (listaV.equals("Vacio")){
            preferencesManager.saveString("NFila"+NSala, "PrimerUser");
            preferencesManager.saveInt("ServPrimerClient"+NSala,Tiempo);
            preferencesManager.saveString("EsperandoUser"+NSala,"PrimeraVez");

            System.out.println("Lista vacio asi que ....");
            System.out.println("Guardando PrimerUser: Si  "+" TiempoSer: "+Tiempo+" y actualizando Inicio Global Sala"+NSala);


            Timestamp fechaHoraActual = Timestamp.now();
            Map<String, Object> data = new HashMap<>();
            data.put("Inicio", fechaHoraActual);

            db.collection("Negocios/"+idUser+"/TiempoGlobal").document(sala).update(data).addOnSuccessListener(aVoid -> {
                System.out.println("Fecha Hora Guardado en Tiempo Global");
                ObtenerTiempo(Tiempo, Servi, Prec, Usuario,"Si", alertDialogs);
            }).addOnFailureListener(e -> {
                System.out.println("Fecha Hora --- NO --- Guardado en Tiempo Global");
                Toast.makeText(activity, "Error No se pudo completar el Proceso...", Toast.LENGTH_SHORT).show();
            });
        }else {
            ObtenerTiempo(Tiempo, Servi, Prec, Usuario,"No", alertDialogs);
            preferencesManager.getString("EsperandoUser"+NSala,"NoEsperando");
            preferencesManager.saveInt("ServClient"+NSala,Tiempo);
            System.out.println("Ver datos del servicio seleccionado " + Servi + " " + Tiempo + " $" + Prec);
        }

    }

    private void ObtenerTiempo(int TiempoServicio, String NombreServ, double Precio, String Usuario,String PrimerVez, AlertDialog alertDialogs) {

        db.collection("Negocios/"+idUser+"/TiempoGlobal").document(sala).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.contains("Inicio")) {
                    com.google.firebase.Timestamp fechaHora = documentSnapshot.getTimestamp("Inicio");
                    System.out.println("Fecha y hora obtenidas: " + fechaHora.toDate());

                    Long TiempoEspera = documentSnapshot.getLong("Tiempo");
                    GuardarDatos(Usuario, NombreServ, Precio, TiempoServicio , fechaHora.toDate() , Math.toIntExact(TiempoEspera),PrimerVez, alertDialogs);
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

    public void GuardarDatos(String User, String Servi, double Prec, int TiempoServicio, Date InicioServicio, int TiempoEspera, String PrimeraVez, AlertDialog alertDialogs){
        int SumaTimTotal = (TiempoServicio * 60) + TiempoEspera;
        preferencesManager.saveString("UserFila"+NSala,"Si");

        String Nom = User.replace(" ","");
        Timestamp creacion = Timestamp.now();
        String id = Nom+idUser;
        System.out.println(User+" "+Pago+" "+Servi+" Time: "+TiempoServicio);

        Map<String, Object> map = new HashMap<>();
        map.put("Pago", Pago);
        map.put("User",User);
        map.put("Tiempo",TiempoServicio);
        map.put("Servicio",Servi);
        map.put("Precio",Prec);
        map.put("idUser",id);
        map.put("Creacion",creacion);
        map.put("Inicio",InicioServicio);
        map.put("Accion","Admin");
        map.put("AdmTiempoTotal",SumaTimTotal);


        db.collection("Negocios/"+idUser+"/"+sala).document(Nom+idUser).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(activity, "Agregado a la Fila", Toast.LENGTH_SHORT).show();
                System.out.println("Registro completado");
                ActualizarTiempoGlobal(TiempoServicio,TiempoEspera,Prec,PrimeraVez, alertDialogs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al agregar a la sala BD", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ActualizarTiempoGlobal(int TiempoServ,int TiempoEspera, double precio,String Primeravez, AlertDialog alertDialogs) {

        //Suma de Tiempos
        int TiempoServiSegunds = TiempoServ * 60;
        int NewTiempoSegundos = TiempoEspera +TiempoServiSegunds;
        System.out.println("Tiempo anterior: "+TiempoEspera+" Nuevo Tiempo: "+NewTiempoSegundos);

        DocumentReference id = db.collection("Negocios/"+idUser+"/TiempoGlobal").document(sala);
        Map<String, Object> map = new HashMap<>();
        map.put("Tiempo", NewTiempoSegundos);
        db.collection("Negocios/"+idUser+"/TiempoGlobal").document(id.getId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("Tiempo Global Actualizado - "+sala);

                String SalasN = preferencesManager.getString("CantidadSalas","");
                alertDialogs.setOnDismissListener(dialog -> {
                    progressDialog.dismiss();
                    Intent i = new Intent(activity, E1_Sala_Neg.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(i);
                    activity.finish();
                });

                alertDialogs.cancel();
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
    public AdpSolicitServiSala.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_servi_client,parent,false);
        return new AdpSolicitServiSala.ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView NombreServicio, PrecioServicio;
        ImageView FotoServicio;
        Button btnTomar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NombreServicio = itemView.findViewById(R.id.lblServicioNeg);
            PrecioServicio = itemView.findViewById(R.id.lblPrecioServNeg);
            FotoServicio = itemView.findViewById(R.id.ImagenServicioNeg);
            btnTomar = itemView.findViewById(R.id.btnTomarServicio);
        }
    }
}
