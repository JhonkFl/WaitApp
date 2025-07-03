package com.softjk.waitapp.Cliente.AdapterC;

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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Modelo.Sala;
import com.softjk.waitapp.R;

public class AdpSalaC extends FirestoreRecyclerAdapter<Sala, AdpSalaC.ViewHolder> {
    FirebaseFirestore BD = FirebaseFirestore.getInstance();
    Activity activity;
    FirebaseAuth mAuth;
    String idNeg, NSala;
    static String idUser;
    static String Admin;
    int Tiempo;
    PreferencesManager preferencesManager;

    public AdpSalaC(@NonNull FirestoreRecyclerOptions<Sala> options, Activity activity) {
        super(options);
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getUid();
        preferencesManager = new PreferencesManager(activity);
        NSala = preferencesManager.getString("NSala","1"); // 1, 2, 3

    }

    @Override
    protected void onBindViewHolder(@NonNull AdpSalaC.ViewHolder holder, int position, @NonNull Sala model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        idNeg = preferencesManager.getString("idNegocioCliente","");
        Admin = preferencesManager.getString("Admin","");
        String posici= String.valueOf(holder.getAdapterPosition()); //obtener posicion
        Log.d("AdapterSalaCliente", "ver valor idNego Adp: "+idNeg);
        Log.d("AdapterSalaCliente", "ver valor position Adp: "+position);
        Log.d("AdapterSalaCliente", "ver valor position document: "+posici);

        SiSoyYo(holder,id,model, position);
        holder.Servicio.setVisibility(View.GONE);

        if (idUser.equals(id) && position == 0){
            holder.EliminarList.setVisibility(View.GONE);
        }else if (idUser.equals(id) && position > 0){
            holder.EliminarList.setVisibility(View.VISIBLE);
        }

        //Si Soy el Primero en la Lista y el primero en formarse, (para sala1, sal2, sal3)
        String user = preferencesManager.getString("NFila"+NSala, "");

       /* if(position==0  && id.equals(idUser)){
            Log.d("AdapterSalaCliente", "ver valor preference Primer User: "+user);
            if (user.equals("PrimerUser")){
                Log.d("AdapterSalaCliente", "Somo el primer Cliente en la lista");

                String EsperaUser = preferencesManager.getString("EsperandoUser"+NSala,"PrimeraVez");

                Log.d("AdapterSalaCliente", "Ver valor EsperandoUser: "+EsperaUser);
                if (EsperaUser.equals("PrimeraVez")){

                }else {System.out.println("No volver a Mostrar Dialog");}
            }
        } */
    }

    private void SiSoyYo(ViewHolder holder,String id, Sala model, int position) {
        if (id.equals(idUser)){
            holder.Estado.setText("Disfruta de tu tiempo mientras esperas!!!");
            holder.Estado.setVisibility(View.VISIBLE);

            if (model.getEstado() != null) {
                if (model.getEstado().equals("Trasladandose")){
                    ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT; // o un valor en píxeles, como 600
                    holder.cardView.setLayoutParams(params);

                    MultiMetds.IMG(activity.getApplicationContext(), "https://i.pinimg.com/originals/b4/a8/a4/b4a8a4625f6b8ef4418150efff833d04.gif",holder.photo_User,"No");
                    holder.Usuario.setVisibility(View.GONE);
                    holder.Estado.setText("El Negocio lo está Esperando!!");
                }else {
                    holder.Usuario.setText("Estoy en Servicio");
                    holder.Usuario.setTextColor(Color.parseColor("#fafaf9"));
                    holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1e9a05")));
                    holder.Estado.setText("Gracias por su Preferencia!!! ");
                    String Foto = model.getFoto();
                    MultiMetds.IMG(activity.getApplicationContext(),Foto,holder.photo_User,"Si");
                }
            } else {
                holder.Usuario.setText("Yo!!!");
                holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#8cb1ea")));
                String Foto = model.getFoto();
                MultiMetds.IMG(activity.getApplicationContext(),Foto,holder.photo_User,"Si");
            }


            holder.EliminarList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialogMetds.MsgEliminar(activity,"Desea Cancelar su Fila?","",id,"Negocios/"+idNeg+"/Sala"+NSala,"Cliente",NSala);
                }
            });

        }else { //No soy Yo
            holder.EliminarList.setVisibility(View.INVISIBLE);
            if (position == 0){
                holder.Usuario.setText("Usuario en Servicio");
                holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
                Glide.with(activity.getApplicationContext())
                        .load("https://i.pinimg.com/originals/48/11/1a/48111a56907a33cce89cbaab4735ab0f.gif")
                        .into(holder.photo_User);
            }else {
                holder.Usuario.setText("Usuario en Espera");
                holder.lis.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fafaf9")));
                Glide.with(activity.getApplicationContext())
                        .load("https://i.pinimg.com/originals/b4/9f/71/b49f71320c7c94dbdf3539f1ea095e76.gif")
                        .into(holder.photo_User);
            }
        }
    }


    @NonNull
    @Override
    public AdpSalaC.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.z_item_list_client, parent, false);
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Usuario, Servicio, Precio, ContadorItem;
        public static TextView Estado;
        ImageView EliminarList, photo_User;
        LinearLayout lis;
        CardView cardView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Usuario = itemView.findViewById(R.id.nombreUser);
            Servicio = itemView.findViewById(R.id.lblTemporizadorCL);
            photo_User = itemView.findViewById(R.id.photoUser);
            EliminarList = itemView.findViewById(R.id.btn_eliminarList);
            Precio = itemView.findViewById(R.id.btnPago);
            lis = itemView.findViewById(R.id.linerListaHorizont);
            Estado = itemView.findViewById(R.id.lblEstadoLista);
            cardView = itemView.findViewById(R.id.cardIMG);
        }
    }
}
