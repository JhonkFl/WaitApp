package com.softjk.waitapp.Negocio.FragmentMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.softjk.waitapp.Negocio.B2_RegistNeg;
import com.softjk.waitapp.Cliente.C1_Menu_Client;
import com.softjk.waitapp.Negocio.E1_Sala_Neg;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Negocio.B3_Horario;
import com.softjk.waitapp.R;
import com.softjk.waitapp.Sistema.Metodos.TarjetaPDF;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Menu_Inicio_Ngc extends Fragment {

    FirebaseFirestore mfirestore;
    ImageView Logo;
    TextView Nombre, Correo, Cantidad, Codigo;
    ImageButton Editar, Compartir;
    FirebaseAuth mAuth;
    String idUser;
    ViewGroup viewGroup;
    LinearLayout Sala, Horario;
    static String idHorario;
   // FrameLayout contenedor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_inicio_ngc, container, false);

        Logo = view.findViewById(R.id.imgLocalAdmin);
        Nombre = view.findViewById(R.id.lblNombreLocalSala);
        Correo = view.findViewById(R.id.lblCorreoNgc);
        Cantidad = view.findViewById(R.id.lblCantidad);
        Editar = view.findViewById(R.id.imgBntEdit);
        Codigo = view.findViewById(R.id.lblCodigoLV);
        Sala = view.findViewById(R.id.btnSalaVirtualA);
        Horario = view.findViewById(R.id.btnAbrirHorario);
        Compartir = view.findViewById(R.id.btnCompartir);
        viewGroup = view.findViewById(android.R.id.content);
       // contenedor = view.findViewById(R.id.contenedor);

        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        //idNegocio=mfirestore.collection("Negocios").document(idUser).getId();
       // idHorario=mfirestore.collection("Negocios/"+idUser+"/Horario").document().getId();

        ObtenerDatos();

        Editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), B2_RegistNeg.class);
                i.putExtra("idNeg", idUser);
                System.out.println(idUser);
                startActivity(i);

            }
        });

        Sala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), E1_Sala_Neg.class);
                //intent.putExtra("Horario", idHorario);
                startActivity(intent);
               // preferencesManager.saveString("idNegocioCliente",idUser);
               // preferencesManager.saveString("Admin","Si");
            }
        });


        Horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), B3_Horario.class);
                intent.putExtra("Horario", idUser);
                System.out.println(idHorario);
                startActivity(intent);
            }
        });


        return view;
    }

    public void ObtenerDatos() {
        mfirestore.collection("Negocios").document(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.exists()) {
                    String NomNeg = value.getString("Nombre");
                    String CodNeg = value.getString("Codigo");
                    Nombre.setText(NomNeg);
                    Correo.setText(mAuth.getCurrentUser().getEmail());
                    String NumeroSala = value.getString("Salas");
                    Cantidad.setText(NumeroSala+" Salas");
                    Codigo.setText(CodNeg);
                    String LogoNeg = value.getString("Logo");

                    MultiMetds.IMG(getActivity(),LogoNeg,Logo,"Si");

                    Compartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TarjetaPDF pdf = new TarjetaPDF(getActivity());
                            try {
                                pdf.generarVista(NomNeg,LogoNeg,CodNeg);
                                pdf.generarVista(NomNeg,LogoNeg,CodNeg);
                                AlertDialogMetds.DialogPDF(getActivity(),viewGroup);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                }else {
                    Editar.setVisibility(View.INVISIBLE);
                    AlertDialogMetds.MsgOpenActiv(getActivity(),"Registra tu Negocio","Quieres Registrar tu Negocio?", B2_RegistNeg.class, C1_Menu_Client.class,"","");
                }
            }
        });

    }
}