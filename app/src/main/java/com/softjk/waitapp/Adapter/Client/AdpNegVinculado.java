package com.softjk.waitapp.Adapter.Client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.E1_Sala_Client;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Modelo.Negocio;
import com.softjk.waitapp.R;

import java.util.Date;

public class AdpNegVinculado extends FirestoreRecyclerAdapter<Negocio, AdpNegVinculado.ViewHolder> {
   //Declarar Variables Globales
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    Activity activity;
    static String LogoBD;
    ProgressDialog dialog;
    PreferencesManager preferencesManager;

    public AdpNegVinculado(@NonNull FirestoreRecyclerOptions<Negocio> options, Activity activity1) {
        super(options);
        //Inicializar Variables Globales
        this.activity = activity1;
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(activity1);
        db = FirebaseFirestore.getInstance();
        preferencesManager = new PreferencesManager(activity);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpNegVinculado.ViewHolder holder, int position, @NonNull Negocio model) {
        //Codificacion de todas las Funciones o acciones

        String identi="NoEntrar";
        holder.Eliminar.setVisibility(View.VISIBLE);

        //Obtener la Posicion de cada Vista
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        ObtenerDatos(id,holder);
        HorarioN(holder,identi,id,"1","","");

        //Acciones Butones
        holder.Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Codigo = model.getCodigo();
                deletePet(id,Codigo);
            }
        });
    }

    private void HorarioN(ViewHolder vista, String identi, String idNeg, String Salas,String Nombre, String Logo) {
        SimpleDateFormat simpleformat = new SimpleDateFormat("EEEE");
        String strDayofWeek = simpleformat.format(new Date());
        System.out.println("Día de la semana = " + strDayofWeek);

        db.collection("Negocios/" + idNeg + "/Horario").document(strDayofWeek).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("Horario Encontrado con Exito");

                SimpleDateFormat simpleformat = new SimpleDateFormat("HH.mm");
                String strTime = simpleformat.format(new Date());
                System.out.println("Revisando Formato de la Hora Actual " + strTime);

                String Dia = documentSnapshot.getString("Dia");
                String HoraI = documentSnapshot.getString("HoraInicio");
                String HoraF = documentSnapshot.getString("HoraFinal");
                System.out.println("Obteniendo Datos BD: "+Dia+" : "+HoraI+" a "+HoraF);

                if (Dia == null || HoraI.equals("0.0") || HoraF.equals("0.0")) {
                    vista.Horario.setText("Cerrado");
                    vista.Horario.setTextColor(Color.RED);
                }else {
                    double Inicio = Double.parseDouble(HoraI);
                    double Final = Double.parseDouble(HoraF);
                    System.out.println("Formato Double: Hora Inicio " + Inicio+" Hora Final "+Final);

                    if (Dia.equals(strDayofWeek)){ //Si Dia es igual al Dia de Hoy
                        double Tiem= Double.parseDouble(strTime);
                        System.out.println("HoraActual con formato a Double " + Tiem);

                        if(Tiem >= Inicio){ //Si HoraActualFormat es mayor que la Hora Inicial
                            if(Tiem < Final){ //Si HoraActualFormat es menor que la Hora Final
                                vista.Horario.setText("Abierto");
                                vista.Horario.setTextColor(Color.BLUE);
                                if(identi.equals("Entrar")) { //Si esta Disponible = Entrar
                                    Toast.makeText(activity, "El Negocio esta ABIERTO!!!!!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(activity, E1_Sala_Client.class);
                                    i.putExtra("idNeg", idNeg);
                                    i.putExtra("NSalas",Salas);
                                    i.putExtra("Negocio",Nombre);
                                    i.putExtra("Logo",Logo);
                                    i.putExtra("EntrarSala","Si");
                                    //i.putExtra("id_User", id);
                                    activity.startActivity(i);
                                }
                            }else {
                                if(identi.equals("Entrar")) {
                                    Toast.makeText(activity, "El Negocio esta Cerrado", Toast.LENGTH_SHORT).show();
                                }
                                vista.Horario.setText("Cerrado");
                                vista.Horario.setTextColor(Color.RED);
                            }
                        }else {
                            if(identi.equals("Entrar")) {
                                Toast.makeText(activity, "El Negocio esta Cerrado", Toast.LENGTH_SHORT).show();
                            }
                            vista.Horario.setText("Cerrado");
                            vista.Horario.setTextColor(Color.RED);
                        }
                    }else {
                        Toast.makeText(activity, "El Negocio esta Cerrado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ObtenerDatos(String idNeg, ViewHolder Vista) {
        db.collection("Negocios").document(idNeg).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("Datos del Negocio - Adapter - MenuInicio");

                String Nombre = documentSnapshot.getString("Nombre");
                String Ubicacion = documentSnapshot.getString("Ubicacion");
                String LogoBD = documentSnapshot.getString("Logo");
                String Tipo = documentSnapshot.getString("Tipo");
                String Salas = documentSnapshot.getString("Salas");

                Vista.NombreNg.setText(Nombre);
                Vista.UbicacionNg.setText(Ubicacion);
                Vista.TipoNg.setText(Tipo);

                try {
                    if (!LogoBD.equals(""))
                        Glide.with(activity.getApplicationContext())
                                .load(LogoBD)
                                .into(Vista.Logo);
                }catch (Exception e){
                    Log.d("Exception", "e: "+e);
                }

                //Accion Buton Click Entrar
                Vista.LinerNeg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ident= "Entrar";
                        HorarioN(Vista,ident,idNeg,Salas,Nombre,LogoBD);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity.getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePet(String id,String Codigo) {
        String idUser = auth.getCurrentUser().getUid();
        db.collection("User/"+idUser+"/MisNegocios").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Desvinculado", Toast.LENGTH_SHORT).show();
                preferencesManager.getString("VinculacionNg",Codigo+"-Eliminado");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public AdpNegVinculado.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Vinculacion con la Vista del diseño del RecycleView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_negc,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //Inicializacion de los elemtentos de la Vista Item
        TextView NombreNg, TipoNg, UbicacionNg, Horario;
        ImageView Logo, Eliminar;
        LinearLayout LinerNeg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Inicializacion de las variables de los elemtos
            LinerNeg = itemView.findViewById(R.id.ItemLinerNeg);
            NombreNg = itemView.findViewById(R.id.ItemNombNg);
            TipoNg = itemView.findViewById(R.id.ItemTipoNeg);
            UbicacionNg = itemView.findViewById(R.id.ItemUbicaNeg);
            Logo = itemView.findViewById(R.id.ItemLogoNg);
            Eliminar = itemView.findViewById(R.id.ItemDeletNeg);
            Horario = itemView.findViewById(R.id.ItemHorarioNeg);
        }
    }
}
