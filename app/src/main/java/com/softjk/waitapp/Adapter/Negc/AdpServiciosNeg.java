package com.softjk.waitapp.Adapter.Negc;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Modelo.ServicNg;
import com.softjk.waitapp.R;
import com.softjk.waitapp.RegistrarServicios;

import java.text.DecimalFormat;

public class AdpServiciosNeg extends FirestoreRecyclerAdapter<ServicNg, AdpServiciosNeg.ViewHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    Activity activity;

    public AdpServiciosNeg(@NonNull FirestoreRecyclerOptions<ServicNg> options, Activity activity1) {
        super(options);
        this.activity = activity1;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpServiciosNeg.ViewHolder holder, int position, @NonNull ServicNg model) {
        DecimalFormat format = new DecimalFormat("0.00");
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        String idUser = mAuth.getCurrentUser().getUid();

        holder.Nombre.setText(model.getNombre());
        String pre = String.valueOf(model.getPrecio());
        holder.Precio.setText(pre);
        String FotoServici = model.getLogo();
        MultiMetds.IMG(activity,FotoServici,holder.FotoServicio,"No");

        holder.EditarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, RegistrarServicios.class);
                i.putExtra("id_Servicios", id);
                activity.startActivity(i);

            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatosFirestoreBD.EliminarDocument(activity, "Negocios/" + idUser + "/Servicios", id, "Eliminando", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                        if ("Eliminado".equals(resultado)) {
                            // Éxito
                        } else {
                            // Error
                        }
                    }
                });

            }
        });


    }

    @NonNull
    @Override
    public AdpServiciosNeg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_servicios_neg, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Nombre, Categoria, Tiempo, Precio;
        ImageView btn_delete, FotoServicio, EditarServicio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Nombre = itemView.findViewById(R.id.lblServicio);
            Precio = itemView.findViewById(R.id.lblPrecio);
            //Categoria = itemView.findViewById(R.id.lblCategoriaServicio)
            //Tiempo = itemView.findViewById(R.id.lblTiempoServicio);
            FotoServicio = itemView.findViewById(R.id.ImagenServicio);
            btn_delete = itemView.findViewById(R.id.btnEliminarServicio);
            EditarServicio= itemView.findViewById(R.id.btnEditarServicio);
        }
    }
}
