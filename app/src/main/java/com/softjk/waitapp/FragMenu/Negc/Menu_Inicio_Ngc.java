package com.softjk.waitapp.FragMenu.Negc;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.softjk.waitapp.B1_Login;
import com.softjk.waitapp.B2_RegistNeg;
import com.softjk.waitapp.B3_Horario;
import com.softjk.waitapp.C1_Menu_Client;
import com.softjk.waitapp.E1_Sala_Neg;
import com.softjk.waitapp.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Menu_Inicio_Ngc extends Fragment {

    FirebaseFirestore mfirestore;
    ImageView Logo;
    TextView Nombre, Correo, Cantidad, Codigo;
    ImageButton Editar;
    FirebaseAuth mAuth;
    String idUser;
    LinearLayout Sala, Horario;
    static String idHorario;
    PreferencesManager preferencesManager;

    NotificationCompat.Builder notificacion;
    private static final int idUnica = 51623;

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
        //Limpiar = view.findViewById(R.id.btnServicios);

        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        //idNegocio=mfirestore.collection("Negocios").document(idUser).getId();
       // idHorario=mfirestore.collection("Negocios/"+idUser+"/Horario").document().getId();
        preferencesManager = new PreferencesManager(getActivity());
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
                preferencesManager.saveString("idNegocioCliente",idUser);
                preferencesManager.saveString("Admin","Si");
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

      /*  Limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Limpiando datos");
                preferencesManager.saveString("NFila1",null);
                preferencesManager.saveString("EsperandoUser1",null);
                preferencesManager.saveString("UserFila1",null);
                preferencesManager.saveString("ActualizarDatos1", null);
                preferencesManager.saveString("Pago1", null);
                preferencesManager.saveString("AccionesFila1",null);
                preferencesManager.saveString("NSala",null);
                preferencesManager.saveString("FilaSala",null);
            }
        }); */

        return view;
    }

    public void ObtenerDatos() {
        mfirestore.collection("Negocios").document(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value.exists()) {
                    Nombre.setText(value.getString("Nombre"));
                    Correo.setText(mAuth.getCurrentUser().getEmail());
                    String NumeroSala = value.getString("Salas");
                    Cantidad.setText(NumeroSala+" Salas");
                    Codigo.setText(value.getString("Codigo"));
                    String LogoNeg = value.getString("Logo");

                    MultiMetds.IMG(getActivity(),LogoNeg,Logo,"Si");

                }else {
                    Editar.setVisibility(View.INVISIBLE);
                    AlertDialogMetds.MsgOpenActiv(getActivity(),"Registra tu Negocio","Quieres Registrar tu Negocio?", B2_RegistNeg.class, C1_Menu_Client.class,"","");
                }
            }
        });

    }

    public void AbrirSala(View view){
        //notif();
        //finish();
       // Intent intent = new Intent(this, SalaNegocio.class);
       // startActivity(intent);
    }



}