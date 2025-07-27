package com.softjk.waitapp.Cliente.AdapterC;

import static com.softjk.waitapp.Cliente.E1_Sala_Client.Codigo;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Cliente.E1_Sala_Client;
import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC1;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.LimpiarDatos;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdpSalaC extends FirestoreRecyclerAdapter<Sala, AdpSalaC.ViewHolder> {
    FirebaseFirestore BD = FirebaseFirestore.getInstance();
    Activity activity;
    FirebaseAuth mAuth;
    String idNeg, NSala;
    static String idUser;

    PreferencesManager preferenceSala, preferenceCliente;
    private OnItemPositionCallback callback;

    public interface OnItemPositionCallback{
        void onPositionFound(int position);
    }

    public void setOnItemPositionCallback(OnItemPositionCallback callback){
        this.callback = callback;
    }


    public AdpSalaC(@NonNull FirestoreRecyclerOptions<Sala> options, Activity activity) {
        super(options);
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        preferenceSala = new PreferencesManager(activity,Codigo);
        preferenceCliente = new PreferencesManager(activity,"Cliente");
        NSala = preferenceSala.getString("NSala","1"); // 1, 2, 3
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSalaC.ViewHolder holder, int position, @NonNull Sala model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        idNeg = preferenceCliente.getString("idNegocioCliente","");

        if (id.equals(idUser)){ // Si soy Yo
            if (model.getEstado() != null) { // Si es Estado del user tiene un valor
                if (model.getEstado().equals("Trasladandose")){
                   /* ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT; // o un valor en píxeles, como 600
                    holder.cardView.setLayoutParams(params);

                    MultiMetds.IMG(activity.getApplicationContext(), "https://i.pinimg.com/originals/b4/a8/a4/b4a8a4625f6b8ef4418150efff833d04.gif",holder.photo_User,"No");
                    holder.Usuario.setVisibility(View.GONE);
                    holder.Estado.setText("El Negocio lo está Esperando!!");
                    holder.Estado.setVisibility(View.VISIBLE);
                    holder.EliminarList.setVisibility(View.GONE); */
                } else if (model.getEstado().equals("Cancelado")) {
                    holder.EliminarList.setVisibility(View.GONE);
                    Long TiempoRest = Long.valueOf(model.getTiempoServicio());
                    ObservElemtDespsDeMi(position,TiempoRest);
                }
            }else {     // Si es Estado del user está vacio
                MultiMetds.IMG(activity,model.getFoto(),holder.photo_User,"");
                holder.Usuario.setText("YO : "+model.getUser());
                if ( position == 0){
                    holder.EliminarList.setVisibility(View.GONE);
                }else if ( position > 0){
                    holder.EliminarList.setVisibility(View.VISIBLE);
                }
            }

        }else { //No soy Yo
            holder.EliminarList.setVisibility(View.GONE);
            if (position == 0){
                holder.Usuario.setText("Persona en Servicio");
                holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
                Glide.with(activity.getApplicationContext())
                        .load("https://i.pinimg.com/originals/48/11/1a/48111a56907a33cce89cbaab4735ab0f.gif")
                        .into(holder.photo_User);
            }else {
                holder.Usuario.setText("Persona "+position);
                holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
                Glide.with(activity.getApplicationContext())
                        .load("https://i.pinimg.com/originals/b4/9f/71/b49f71320c7c94dbdf3539f1ea095e76.gif")
                        .into(holder.photo_User);
            }
        }

        holder.EliminarList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogMetds.MsgEliminar(activity,"Desea Cancelar su Fila?","",id,"Negocios/"+idNeg+"/Sala"+NSala,NSala,Codigo);
            }
        });
    }

    private void ObservElemtDespsDeMi(int Position, Long TiempoResta) {
        getItem(Position);
        AtomicInteger pendientes;

        for (int i = Position + 1; i < getItemCount(); i++) {
            Sala sala = getItem(i);
            String idPosterior = sala.getIdUser();
            pendientes = new AtomicInteger(getItemCount() - Position - 1);
            ObtnTiemClientPoste(idPosterior, TiempoResta, pendientes);
            Log.d("ID Posterior", idPosterior);
        }
        ActualizarTiemGlobal(TiempoResta);
    }

    private void ObtnTiemClientPoste(String Id, Long TiempoResta,AtomicInteger pendientes ) {
        BD.collection("Negocios/"+idNeg+"/Sala"+NSala).document(Id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long TiempoAdmin = documentSnapshot.getLong("AdmTiempoTotal");
                Long TiempoUsers = documentSnapshot.getLong("Tiempo");
                Long TimeRest = TiempoResta;
                String Acion = documentSnapshot.getString("Accion");
                DocumentReference reference = BD.collection("Negocios/" + idNeg + "/Sala" + NSala).document(Id);

                if (Acion == null) {
                    if (TiempoAdmin != null && TiempoUsers != null) {
                        Long NewTimeAdmin = TiempoAdmin - TimeRest;
                        Long NewTimeUsers = TiempoUsers - TimeRest;

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("AdmTiempoTotal", NewTimeAdmin);
                        updates.put("Tiempo", NewTimeUsers);

                        reference.update(updates).addOnSuccessListener(aVoid -> {
                            System.out.println("--------- Client--- -- - - Pendien " + pendientes);
                            System.out.println("----------Client ----- -"+pendientes.decrementAndGet());
                            if (pendientes.decrementAndGet() == 0) {
                                Log.d("Finalización", "Todas las actualizaciones completadas");
                                Toast.makeText(activity, "Actualizacion Users Listo", Toast.LENGTH_SHORT).show();
                                //ActualizarTiemGlobal(TiempoResta);
                            }
                        });
                    }

                }else if ( Acion.equals("Admin")){
                    if (TiempoAdmin != null) {
                        Long NewTimeAdmin = TiempoAdmin - TimeRest;
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("AdmTiempoTotal", NewTimeAdmin);

                        reference.update(updates).addOnSuccessListener(aVoid -> {
                            System.out.println("---------- Admin--- -- - - Pendien "+pendientes);
                            Toast.makeText(activity, "Admin - "+pendientes.decrementAndGet(), Toast.LENGTH_SHORT).show();
                            if (pendientes.decrementAndGet() == 0) {
                                Log.d("Finalización", "Todas las actualizaciones completadas");
                                System.out.println("----------Admin ----- -"+pendientes.decrementAndGet());
                                //ActualizarTiemGlobal(TiempoResta);
                            }
                        });
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirestoreError", "Fallo al actualizar tiempo: " + e.getMessage());
            }
        });
    }

    private void ActualizarTiemGlobal(Long TiemRest) {
        DocumentReference reference = BD.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+NSala);
        BD.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+NSala).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Long TimeGlobal = documentSnapshot.getLong("Tiempo");
                Long NewTimeGlobal = TimeGlobal - TiemRest;
                reference.update("Tiempo",NewTimeGlobal);

                SweetAlertDialog dialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
                dialog.setTitleText("Cancelando Fila");
                dialog.setContentText("Espere un Momento...");
                dialog.setCancelable(false);
                dialog.show();

                new Handler() .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EliminarUser("Negocios/"+idNeg+"/Sala"+NSala,dialog);
                    }
                },900 );

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FirestoreError", "Fallo al actualizar tiempo: " + e.getMessage());
            }
        });
    }

    private void EliminarUser(String Colecction, SweetAlertDialog dialog) {
        BD.collection(Colecction).document(idUser).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                LimpiarDatos.LimpiarSala(activity.getApplication(), NSala,Codigo);
                dialog.dismissWithAnimation();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    @NonNull
    @Override
    public AdpSalaC.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_client, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Estado,EliminarList, Usuario;
        ImageView photo_User;
        LinearLayout lis;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario = itemView.findViewById(R.id.nombreUser);
            photo_User = itemView.findViewById(R.id.photoUser);
            EliminarList = itemView.findViewById(R.id.btn_eliminarList);
            lis = itemView.findViewById(R.id.linerListaHorizont);
            Estado = itemView.findViewById(R.id.lblEstadoLista);
            cardView = itemView.findViewById(R.id.cardIMG);
        }
    }
}
