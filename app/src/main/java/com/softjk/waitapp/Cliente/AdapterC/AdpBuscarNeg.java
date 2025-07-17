package com.softjk.waitapp.Cliente.AdapterC;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Modelo.Negocio;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class AdpBuscarNeg extends FirestoreRecyclerAdapter<Negocio, AdpBuscarNeg.ViewHolder> {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    PreferencesManager preferencesManager;
    Activity activity;

    public AdpBuscarNeg(@NonNull FirestoreRecyclerOptions<Negocio> options, Activity activity1) {
        super(options);
        this.activity = activity1;
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        preferencesManager = new PreferencesManager(activity,"Negocio");
    }

    @Override
    protected void onBindViewHolder(@NonNull AdpBuscarNeg.ViewHolder holder, int position, @NonNull Negocio model) {
        holder.add.setVisibility(View.VISIBLE);

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String idNeg = documentSnapshot.getId();

        // Cambiar el Tamaño height del liner layout
      /*  ViewGroup.LayoutParams params = holder.LinerItem.getLayoutParams();  // Obtener los parámetros de diseño actuales
        params.width = 330; // Modificar el valor de layout_height - Cambia a la altura deseada en píxeles
        holder.LinerItem.setLayoutParams(params); // Aplicar los nuevos parámetros al LinearLayout  */

        holder.LinerDatos.setGravity(Gravity.CENTER);
        holder.LinerUbic.setGravity(Gravity.CENTER);
        holder.NombreLocal.setText(model.getNombre());
        holder.NombreLocal.setTextSize(18);
        holder.TipoNegocio.setText(model.getTipo());
        holder.Localizacion.setText(model.getUbicacion());
        holder.Horario.setVisibility(View.GONE);
        holder.add.setVisibility(View.VISIBLE);
        String photoPet = model.getLogo();
        try {
            if (!photoPet.equals(""))
                Glide.with(activity.getApplicationContext())
                        .load(photoPet)
                        .circleCrop()
                        .into(holder.Logo);
        }catch (Exception e){
            Log.d("Exception", "e: "+e);
        }

        String VinculacionNg = preferencesManager.getString("VinculacionNg","No");
        if (VinculacionNg.equals(model.getCodigo()+"-Vinculado")){
            holder.add.setText("Vinculado");
            holder.add.setTextColor(Color.BLUE);
            holder.add.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F5F5F5")));
        }else {
            //Accion evento Buton
            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Local= holder.NombreLocal.getText().toString().trim();
                    holder.add.setText("Agregado");
                    holder.add.setTextColor(Color.CYAN);
                    GuardarNegocioCliente(Local,idNeg,model.getCodigo());
                }
            });
        }
    }

    private void GuardarNegocioCliente(String Local, String idNeg,String Codigo){
        String idUser = mAuth.getCurrentUser().getUid();
        DocumentReference idNgcVinc = mFirestore.collection("User/"+idUser+"/MisNegocios").document(idNeg);
        Map<String, Object> map = new HashMap<>();
        map.put("idUser", idUser);
        map.put("idNegocio", idNeg);
        map.put("NombreLocal", Local);
        map.put("Codigo", Codigo);

        mFirestore.collection("User/"+idUser+"/MisNegocios").document(idNgcVinc.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Negocio Guardado", Toast.LENGTH_SHORT).show();
                preferencesManager.saveString("VinculacionNg",Codigo+"-Vinculado");
               // activity.finish();
//                Intent i = new Intent(activity, C1_Menu_Neg.class);
//                i.putExtra("Agregado",Local);
//                activity.startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al ingresar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public AdpBuscarNeg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_negc, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView NombreLocal, TipoNegocio, Horario, Localizacion;
        ImageView Logo;
        Button add;
        LinearLayout LinerItem,LinerDatos,LinerUbic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NombreLocal = itemView.findViewById(R.id.ItemNombNg);
            TipoNegocio = itemView.findViewById(R.id.ItemTipoNeg);
            Horario = itemView.findViewById(R.id.ItemHorarioNeg);
            Localizacion = itemView.findViewById(R.id.ItemUbicaNeg);
            Logo = itemView.findViewById(R.id.ItemLogoNg);
            add = itemView.findViewById(R.id.ItemAddNeg);
            LinerItem = itemView.findViewById(R.id.ItemLinerNItem);
            LinerDatos = itemView.findViewById(R.id.ItemLinerNeg);
            LinerUbic = itemView.findViewById(R.id.ItemLinerUbic);
        }
    }
}
