package com.softjk.waitapp.Adapter.Negc;

import static com.softjk.waitapp.FragSala.Negc.SalaN1.viewGroup;
import static com.softjk.waitapp.FragSala.Negc.SalaN3.viewGroupN3;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.Metodos.TiempoGlobalPers;
import com.softjk.waitapp.Modelo.Sala;
import com.softjk.waitapp.R;

public class AdpSala3Neg extends FirestoreRecyclerAdapter<Sala, AdpSala3Neg.ViewHolder> {
    FirebaseFirestore BD = FirebaseFirestore.getInstance();
    Activity activity;
    FirebaseAuth mAuth;
    static String idUser;
    PreferencesManager preferencesManager;

    public AdpSala3Neg(@NonNull FirestoreRecyclerOptions<Sala> options, Activity activity) {
        super(options);
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        preferencesManager = new PreferencesManager(activity);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() > 0) {
            DocumentSnapshot primerDocumento = getSnapshots().getSnapshot(0);
            String idPrimerElemento = primerDocumento.getId();
            String ac= primerDocumento.getString("Accion");

            notifyItemChanged(0); // Solo actualiza el primer elemento

            Log.d("RecyclerView", "ID del primer elemento: "+ac+ " " + idPrimerElemento);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSala3Neg.ViewHolder holder, int position, @NonNull Sala model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();

        String posici= String.valueOf(holder.getAdapterPosition()); //obtener posicion
        int colortext = ContextCompat.getColor(activity, R.color.color_text); //Color definido y personalizado en la carpt colors
        Log.d("AdapterSalaNeg", "ver valor position document: "+posici);

        holder.Usuario.setTextColor(ColorStateList.valueOf(colortext));
        holder.Servicio.setText(model.getServicio());
        holder.Usuario.setText(model.getUser());
        holder.Pago.setText(model.getPago());
        String precioServ = String.valueOf(model.getPrecio());
        holder.Precio.setText("$ "+precioServ);

        if (model.getFoto() != null){
            MultiMetds.IMG(activity,model.getFoto(),holder.photo_User,"No");
        }else {
            String RutaId = "https://i.pinimg.com/236x/f1/f5/15/f1f5153cabe32239c85842fb4d0ba3c8--ps.jpg";
            MultiMetds.IMG(activity,RutaId,holder.photo_User,"No");
        }


        if (position == 0){
            int color = ContextCompat.getColor(activity, R.color.color_element); //Color definido y personalizado en la carpt colors
            holder.lis.setBackgroundColor(color);
            holder.lis.invalidate();
            holder.Usuario.setTextColor(Color.parseColor("#97f3a0"));
            holder.Servicio.setTextColor(Color.parseColor("#cdd7e8"));
            holder.Estado.setText(model.getEstado());
            holder.Pago.setTextColor(Color.parseColor("#cdd7e8"));
            holder.Precio.setTextColor(Color.parseColor("#cdd7e8"));

            TiempoGlobalPers.getTiempoItemPers("Negocios/"+idUser+"/Sala3",id,holder.ContadorItem,model.getFoto(), model.getUser(), viewGroupN3,activity);
            preferencesManager.saveString("Client0-3",id);
            preferencesManager.saveInt("TimeAdmin3",model.getAdmTiempoTotal());
            // TiempoGlobalPers.getTiempoItemPers("Negocios/"+idUser+"/Sala1",id,lblTiempo,activity);
           // lblmsgTiemp.setText("");


        }else if (position > 0){
            //holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
            holder.ContadorItem.setVisibility(View.GONE);
        }
    }




    private void deletePet(String id) {
        //  System.out.println("revisando Sala "+NSala);
        BD.collection("Negocios/"+idUser+"/Sala1").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                if(id.equals(idUser)) {
                    Toast.makeText(activity, "Eliminado correctamente", Toast.LENGTH_SHORT).show();
                }
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
    public AdpSala3Neg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_client_neg, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Usuario, Servicio, Precio, ContadorItem;
        TextView Pago, Estado;
        ImageView  photo_User;
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
        }
    }
}
