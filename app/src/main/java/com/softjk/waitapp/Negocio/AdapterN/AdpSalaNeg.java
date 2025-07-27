package com.softjk.waitapp.Negocio.AdapterN;

import static com.softjk.waitapp.Negocio.FragmentSala.SalaN1.viewGroupN;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.softjk.waitapp.Sistema.Metodos.AddMasTime;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

public class AdpSalaNeg extends FirestoreRecyclerAdapter<Sala, AdpSalaNeg.ViewHolder> {
    Activity activity;
    FirebaseAuth mAuth;
    static String idUser;
    PreferencesManager preferencesManager;

    // ********* Interfaz ID para Receso o Finalizacion **********
    private OnItemPrimerID callback;
    public interface OnItemPrimerID{
        void OnItemPrimerIDFound(String id);
    }
    public void setOnItemID(OnItemPrimerID callback){
        this.callback = callback;
    }


    // ********* Interfaz ID para Iniciar Contador **********
    private IDContador callbackContador;
    public interface IDContador{
        void OnIDContadorFound(String id);
    }
    public void setOnIDContador(IDContador callbackContador){this.callbackContador = callbackContador;}



    public AdpSalaNeg(@NonNull FirestoreRecyclerOptions<Sala> options, Activity activity) {
        super(options);
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        preferencesManager = new PreferencesManager(activity,"Negocio");
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSalaNeg.ViewHolder holder, int position, @NonNull Sala model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        int colortext = ContextCompat.getColor(activity, R.color.color_text); //Color definido y personalizado en la carpt colors

        if (model.getUser().equals("RECESO")){
            holder.lis.setBackgroundColor(Color.parseColor("#c3e4ea"));
            holder.Usuario.setText(model.getUser());
            ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
            params.width = 300; // en píxeles, como 600
            holder.cardView.setLayoutParams(params);

            String Ruta = "https://media.istockphoto.com/id/2151003931/es/vector/un-hombre-de-negocios-relajado-disfruta-de-una-taza-de-café-o-té-junto-al-despertador.jpg?s=612x612&w=0&k=20&c=Huls2HlMDqRb4Xn2a_5ooRvxb4PhsvRyaZrv_JsoN8k=";
            MultiMetds.IMG(activity,Ruta,holder.photo_User,"No");

            holder.Usuario.setTextColor(Color.parseColor("#1037ba"));
            holder.Servicio.setVisibility(View.GONE);
            holder.Estado.setVisibility(View.GONE);
            holder.Pago.setVisibility(View.GONE);
            holder.Precio.setVisibility(View.GONE);
            holder.Mas5.setVisibility(View.GONE);
            holder.Mas10.setVisibility(View.GONE);

            if (position == 0){
                callbackContador.OnIDContadorFound(id);
                String Estado = model.getEstado();
                if (Estado != null && Estado.equals("Finalizado")) { //Enviar el Id a Fragment
                    callback.OnItemPrimerIDFound(id);
                }
            }
        }else {

            holder.Usuario.setTextColor(ColorStateList.valueOf(colortext));
            holder.Servicio.setText(model.getServicio());
            holder.Usuario.setText(model.getUser());
            holder.Estado.setText(model.getEstado());
            holder.Pago.setText(model.getPago());
            String precioServ = String.valueOf(model.getPrecio());
            holder.Precio.setText("$ " + precioServ);

            if (model.getFoto() != null) {
                MultiMetds.IMG(activity, model.getFoto(), holder.photo_User, "No");
            } else {
                String RutaId = "https://i.pinimg.com/236x/f1/f5/15/f1f5153cabe32239c85842fb4d0ba3c8--ps.jpg";
                MultiMetds.IMG(activity, RutaId, holder.photo_User, "No");
            }


            if (position == 0) {
                int color = ContextCompat.getColor(activity, R.color.color_element); //Color definido y personalizado en la carpt colors
                holder.lis.setBackgroundColor(color);
                holder.lis.invalidate();
                holder.Usuario.setTextColor(Color.parseColor("#97f3a0"));
                holder.Servicio.setTextColor(Color.parseColor("#cdd7e8"));
                holder.Pago.setTextColor(Color.parseColor("#cdd7e8"));
                holder.Precio.setTextColor(Color.parseColor("#cdd7e8"));
                holder.Mas5.setVisibility(View.VISIBLE);
                holder.Mas10.setVisibility(View.VISIBLE);

                /// TiempoGlobalPers.getTiempoItemPers("Negocios/"+idUser+"/Sala1",id,holder.ContadorItem, model.getFoto(),model.getUser(),viewGroupN ,activity,"1",
                //  model.getServicio(), model.getPago(), String.valueOf(model.getPrecio()));
                callbackContador.OnIDContadorFound(id);
                //TiempoGlobalPers.reiniciarTemporizador("Negocios/" + idUser + "/Sala1", id, holder.ContadorItem, activity, "1");

                String Estado = model.getEstado();
                if (Estado != null && Estado.equals("Finalizado")) { //Enviar el Id a Fragment
                    callback.OnItemPrimerIDFound(id);
                }

                String Precio = String.valueOf(model.getPrecio());
                ClicElim(holder.lis, Precio, model.getUser(), model.getServicio(), id, model.getPago());

            } else if (position > 0) {
                //holder.ContadorItem.setVisibility(View.GONE);
            }


            holder.Mas5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) { //Primera accion
                        int TiempoAdmin = model.getAdmTiempoTotal();
                        int TiempoServ = model.getTiempoServicio();
                        String Accion = model.getAccion();
                        String list = preferencesManager.getString("SalaVacio1", "");
                        AddMasTime.ActualizarTimeUser0(activity, 5, idUser, "1", id, TiempoAdmin, Accion, TiempoServ, list, new AddMasTime.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if (resultado.equals("Exito")){

                                    System.out.println(" NDoc:" + getSnapshots().size()); //Segunda acion para todos los elementos
                                    for (int i = 0; i < getSnapshots().size(); i++) {
                                        DocumentSnapshot snap = getSnapshots().getSnapshot(i);
                                        Sala item = snap.toObject(Sala.class);

                                        if (item != null) {
                                            if (i == 0) continue; //omite el element 0
                                            int tiempo = item.getTiempo();
                                            int tiempoAdmin = item.getAdmTiempoTotal();
                                            String idItem = snap.getId();
                                            String acion = item.getAccion();
                                            System.out.println("idUser:" + idItem);
                                            System.out.println("timeServ:" + tiempo);
                                            AddMasTime.ActualizarTimeGeneral(idUser, idItem, tiempo, 300, "1", activity, acion, tiempoAdmin);
                                        } else {
                                            Toast.makeText(activity, "Finalizo de actualizar", Toast.LENGTH_SHORT).show();
                                            System.out.println("No entro a la acion1");
                                        }
                                    }
                                    Intent intent = activity.getIntent();
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                    activity.startActivity(intent);
                                }
                            }
                        });

                    }


                }
            });

            holder.Mas10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == 0) { //Primera accion
                        int TiempoAdmin = model.getAdmTiempoTotal();
                        int TiempoServ = model.getTiempoServicio();
                        String Accion = model.getAccion();
                        String list = preferencesManager.getString("SalaVacio1", "");
                        AddMasTime.ActualizarTimeUser0(activity, 10, idUser, "1", id, TiempoAdmin, Accion, TiempoServ, list, new AddMasTime.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if (resultado.equals("Exito")){

                                    System.out.println(" NDoc:" + getSnapshots().size());  //Segunda acion para todos los elementos
                                    for (int i = 0; i < getSnapshots().size(); i++) {
                                        DocumentSnapshot snap = getSnapshots().getSnapshot(i);
                                        Sala item = snap.toObject(Sala.class);

                                        if (item != null) {
                                            if (i == 0) continue; //omite el element 0
                                            int tiempo = item.getTiempo();
                                            int tiempoAdmin = item.getAdmTiempoTotal();
                                            String idItem = snap.getId();
                                            String acion = item.getAccion();
                                            System.out.println("idUser:" + idItem);
                                            System.out.println("timeServ:" + tiempo);
                                            AddMasTime.ActualizarTimeGeneral(idUser, idItem, tiempo, 600, "1", activity, acion, tiempoAdmin);
                                        } else {
                                            System.out.println("No entro a la acion1");
                                        }
                                    }
                                    Intent intent = activity.getIntent();
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                    activity.startActivity(intent);
                                }
                            }
                        });
                    }

                }
            });

        }
    }

    private void ClicElim(LinearLayout lis, String Precio, String Cliente, String Servicio, String IdCliet, String Pago) {
        lis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String Collecction = "Negocios/"+idUser+"/Sala1";

                AlertDialogMetds.MsgEliminarAdmin(activity,"Desea Eliminar a "+Cliente,Servicio,IdCliet,Collecction,Pago,Precio,viewGroupN,"1");
                return true;
            }
        });
    }


    @NonNull
    @Override
    public AdpSalaNeg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_client_neg, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Usuario, Servicio, Precio;
        TextView Pago, Estado, Mas5, Mas10;
        ImageView photo_User;
        CardView cardView;
        LinearLayout lis;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario = itemView.findViewById(R.id.AdmiNameUser);
            Servicio = itemView.findViewById(R.id.AdmiServUser);
            photo_User = itemView.findViewById(R.id.AdminphotoUser);
            Precio = itemView.findViewById(R.id.AdmiPrecio);
            lis = itemView.findViewById(R.id.AdminlinerListaHorizont);
            Pago = itemView.findViewById(R.id.AdmiPagoUser);
            Estado = itemView.findViewById(R.id.AdminEstadoItem);
            Mas5 = itemView.findViewById(R.id.btn5Mas);
            Mas10 = itemView.findViewById(R.id.btn10Mas);
            cardView = itemView.findViewById(R.id.AdmincardIMG);
        }
    }

}
