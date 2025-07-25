package com.softjk.waitapp.Negocio.AdapterN;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

public class AdpSalaNeg extends FirestoreRecyclerAdapter<Sala, AdpSalaNeg.ViewHolder> {
    FirebaseFirestore BD = FirebaseFirestore.getInstance();
    Activity activity;
    FirebaseAuth mAuth;
    static String idUser;
    PreferencesManager preferencesManager;
    private final TiempoListener tiempoListener;

    public AdpSalaNeg(@NonNull FirestoreRecyclerOptions<Sala> options, Activity activity, TiempoListener listener) {
        super(options);
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        preferencesManager = new PreferencesManager(activity,"Negocio");
        this.tiempoListener = listener;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() > 0) {
            DocumentSnapshot primerDocumento = getSnapshots().getSnapshot(0);
            String idPrimerElemento = primerDocumento.getId();
            String ac= primerDocumento.getString("Accion");
            String Estado= primerDocumento.getString("Estado");
            String user= primerDocumento.getString("User");
            notifyItemChanged(0); // Solo actualiza el primer elemento

            Log.d("AdapterSala-N", "ID del primer elemento: "+ac+ " " + idPrimerElemento);
            Log.d("AdapterSala-N", "Estado: "+Estado);
            //Actualizar su estado
            if (Estado == null && ac == null){
                System.out.println("Actualizar Estado "+Estado+" - "+user);
            }else {
                System.out.println("No Actualizar - "+Estado+" - "+user);
            }
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSalaNeg.ViewHolder holder, int position, @NonNull Sala model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        String posici= String.valueOf(holder.getAdapterPosition()); //obtener posicion
        int colortext = ContextCompat.getColor(activity, R.color.color_text); //Color definido y personalizado en la carpt colors
        Log.d("AdapterSalaNeg", "ver valor position document: "+posici);

        holder.Usuario.setTextColor(ColorStateList.valueOf(colortext));
        holder.Servicio.setText(model.getServicio());
        holder.Usuario.setText(model.getUser());
        holder.Estado.setText(model.getEstado());
        holder.Pago.setText(model.getPago());
        String precioServ = String.valueOf(model.getPrecio());
        holder.Precio.setText("$ "+precioServ);

        if (model.getFoto() != null){
            MultiMetds.IMG(activity,model.getFoto(),holder.photo_User,"No");
        }else {
            String RutaId = "https://i.pinimg.com/236x/f1/f5/15/f1f5153cabe32239c85842fb4d0ba3c8--ps.jpg";
            MultiMetds.IMG(activity,RutaId,holder.photo_User,"No");
        }

        if (position == 0 && tiempoListener != null){
            int color = ContextCompat.getColor(activity, R.color.color_element); //Color definido y personalizado en la carpt colors
            holder.lis.setBackgroundColor(color);
            holder.lis.invalidate();
            holder.Usuario.setTextColor(Color.parseColor("#97f3a0"));
            holder.Servicio.setTextColor(Color.parseColor("#cdd7e8"));
            holder.Pago.setTextColor(Color.parseColor("#cdd7e8"));
            holder.Precio.setTextColor(Color.parseColor("#cdd7e8"));
            //TiempoGlobalPers.getTiempoItemPers("Negocios/"+idUser+"/Sala1",id,holder.ContadorItem,activity);

           // TiempoGlobalPers.getTiempoItemPers("Negocios/"+idUser+"/Sala1",id,lblTiempo,activity);
           // lblmsgTiemp.setText("");

            holder.Mas5.setVisibility(View.VISIBLE);
            holder.Mas10.setVisibility(View.VISIBLE);

            tiempoListener.onSolicitarTemporizador("Negocios/" + idUser + "/Sala1", id, holder.ContadorItem,model.getFoto(),model.getUser());


        }else if (position > 0){
            //holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
            holder.ContadorItem.setVisibility(View.GONE);
        }


        holder.Mas5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) { //Primera accion
                    int TiempoAdmin = model.getAdmTiempoTotal();
                    int TiempoServ = model.getTiempoServicio();
                    String Accion = model.getAccion();
                    String list = preferencesManager.getString("SalaVacio1", "");
                    AddMasTime.ActualizarTimeUser0(activity, 5, idUser, "1", id, TiempoAdmin, Accion, TiempoServ, list);
                }
                //Segunda acion para todos los elementos
                System.out.println( " NDoc:"+getSnapshots().size());
                for (int i = 0; i < getSnapshots().size(); i++) {
                    DocumentSnapshot snap = getSnapshots().getSnapshot(i);
                    Sala item = snap.toObject(Sala.class);

                    if (item != null) {
                        if (i == 0) continue; //omite el element 0
                        int tiempo = item.getTiempo();
                        int tiempoAdmin = item.getAdmTiempoTotal();
                        String idItem = snap.getId();
                        String acion = item.getAccion();
                        System.out.println("idUser:"+idItem);
                        System.out.println("timeServ:"+tiempo);
                        AddMasTime.ActualizarTimeGeneral(idUser, idItem, tiempo, 300,"1",activity,acion,tiempoAdmin);
                    }else {System.out.println("No entro a la acion2");}
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
                    AddMasTime.ActualizarTimeUser0(activity, 10, idUser, "1", id, TiempoAdmin, Accion, TiempoServ, list);
                }
                //Segunda acion para todos los elementos
                System.out.println( " NDoc:"+getSnapshots().size());
                for (int i = 0; i < getSnapshots().size(); i++) {
                    DocumentSnapshot snap = getSnapshots().getSnapshot(i);
                    Sala item = snap.toObject(Sala.class);

                    if (item != null) {
                        if (i == 0) continue; //omite el element 0
                        int tiempo = item.getTiempo();
                        int tiempoAdmin = item.getAdmTiempoTotal();
                        String idItem = snap.getId();
                        String acion = item.getAccion();
                        System.out.println("idUser:"+idItem);
                        System.out.println("timeServ:"+tiempo);
                        AddMasTime.ActualizarTimeGeneral(idUser, idItem, tiempo, 600,"1",activity,acion,tiempoAdmin);
                    }else {System.out.println("No entro a la acion2");}
                }

            }
        });

        holder.lis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialogMetds.MsgEliminar(activity,"Desea Eliminar a "+model.getUser(),"",id,"Negocios/"+idUser+"/Sala1","Negocio","1","");
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
        TextView Usuario, Servicio, Precio, ContadorItem;
        TextView Pago, Estado, Mas5, Mas10;
        ImageView photo_User;
        LinearLayout lis;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario = itemView.findViewById(R.id.AdmiNameUser);
            Servicio = itemView.findViewById(R.id.AdmiServUser);
            photo_User = itemView.findViewById(R.id.AdminphotoUser);
            Precio = itemView.findViewById(R.id.AdmiPrecio);
            ContadorItem = itemView.findViewById(R.id.AdminTemporizadorItem);
            lis = itemView.findViewById(R.id.AdminlinerListaHorizont);
            Pago = itemView.findViewById(R.id.AdmiPagoUser);
            Estado = itemView.findViewById(R.id.AdminEstadoItem);
            Mas5 = itemView.findViewById(R.id.btn5Mas);
            Mas10 = itemView.findViewById(R.id.btn10Mas);
        }
    }

    public interface TiempoListener {
        void onSolicitarTemporizador(String pathFirestore, String idDocumento, TextView contadorTextView, String Foto, String Nombre);
    }

}
