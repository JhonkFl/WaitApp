package com.softjk.waitapp.Cliente.FragmentMenuC;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Principal.A_Bienvenida;
import com.softjk.waitapp.R;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;

public class MenuNovedades extends Fragment {
    TextView Titulo, Descripcion, DescripcionDer;
    ImageView ImagenDer, ImagenPrincipal;
    FirebaseFirestore mFirestore;
    Button btnActualizarApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_novedades, container, false);

        Titulo = view.findViewById(R.id.lblTituloNoti);
        Descripcion = view.findViewById(R.id.lblDescripcion1);
        DescripcionDer = view.findViewById(R.id.lblDescripcion2);
        ImagenDer = view.findViewById(R.id.img1);
        ImagenPrincipal = view.findViewById(R.id.SoloImagen);
        btnActualizarApp = view.findViewById(R.id.btnActualizarApp);

        mFirestore = FirebaseFirestore.getInstance();

        OntenerDatois();
       // MultiMetds.IniciarServicioNoti(getActivity());
        return view;
    }

    private void OntenerDatois() {
        mFirestore.collection("Q-APP").document("Q-APP-JK").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Titulo.setText(documentSnapshot.getString("Titulo"));
                Descripcion.setText(documentSnapshot.getString("Descripcion1"));
                DescripcionDer.setText(documentSnapshot.getString("Descripcion2"));
                String Img1=documentSnapshot.getString("ImgPub");
                String Img2=documentSnapshot.getString("PublicidadPrincipal");
                String Actualizar = documentSnapshot.getString("Actualizar");

                String Titulo1 = documentSnapshot.getString("Titulo");
                String Des = documentSnapshot.getString("Titulo");
                System.out.println(Titulo1 + "/n" + Des);

                if(Titulo.equals("")){
                    Titulo.setText("No hay Novedades");
                }
                if(Actualizar.equals("Si")){
                    btnActualizarApp.setVisibility(View.VISIBLE);
                }

                Glide.with(getActivity())
                        .load(Img1)
                        //.circleCrop()
                        .into(ImagenDer);

                Glide.with(getActivity())
                        .load(Img2)
                        //.circleCrop()
                        .into(ImagenPrincipal);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });

    }
}