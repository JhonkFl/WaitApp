package com.softjk.waitapp.Negocio;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.softjk.waitapp.Cliente.C1_Menu_Client;
import com.softjk.waitapp.Cliente.FragmentMenuC.MenuAyuda;
import com.softjk.waitapp.Cliente.FragmentMenuC.MenuConfi;
import com.softjk.waitapp.Cliente.FragmentMenuC.MenuNovedades;
import com.softjk.waitapp.Negocio.FragmentMenu.Menu_Inicio_Ngc;
import com.softjk.waitapp.Negocio.FragmentMenu.Menu_Servicios_Ngc;
import com.softjk.waitapp.Principal.B1_Login;
import com.softjk.waitapp.R;
import com.softjk.waitapp.databinding.C1MenuNegBinding;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class C1_Menu_Neg extends AppCompatActivity {
    C1MenuNegBinding binding;
    FirebaseAuth mAuth;
    TextView lblTrancition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = C1MenuNegBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Menu_Inicio_Ngc());

        mAuth = FirebaseAuth.getInstance();

        binding.bottomNavigationView2.setBackground(null);
        binding.bottomNavigationView2.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.InicioNg) {
                fragment = new Menu_Inicio_Ngc();
            } else if (item.getItemId() == R.id.ServicNgc) {
                fragment = new Menu_Servicios_Ngc();
            } else if (item.getItemId() == R.id.ConfiNgc) {
                fragment = new MenuConfi();
            }else if (item.getItemId() == R.id.ayudaNgc) {
                fragment = new MenuAyuda();
                Bundle bundle = new Bundle();
                bundle.putString("Ayuda", "Negocio");
                fragment.setArguments(bundle);

            }if (fragment != null) {
                replaceFragment(fragment);
            }
            return true;
        });

        //para abrir Novedades cuando llegue una Notificacion --->
        String destino = getIntent().getStringExtra("notificacion");
        if (destino != null && destino.equals("Novedades")){
            Fragment fragment = new MenuNovedades();
            replaceFragment(fragment);
            binding.bottomNavigationView2.setSelectedItemId(R.id.vacio);
        } // <---- para abrir Novedades cuando llegue una Notificacion


        binding.fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameNeg, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.z_custom_button_menu);
        lblTrancition = dialog.findViewById(R.id.lblSesion2);
        lblTrancition.setText("Abrir Sesion Cliente");

        LinearLayout LayoutOpcion1 = dialog.findViewById(R.id.layoutOpcio1);
        LinearLayout LayoutOpcion2 = dialog.findViewById(R.id.layoutOpcio2);
        LinearLayout LayoutOpcion3 = dialog.findViewById(R.id.layoutOpcion3);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        LayoutOpcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(C1_Menu_Neg.this, C1_Menu_Client.class);
                //intent.putExtra("Accion", "Negocio");
                //
                startActivity(intent);
            }
        });

        LayoutOpcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Uri Link = Uri.parse("https://play.google.com/store/apps/developer?id=Soft-jk");
                Intent intent = new Intent(Intent.ACTION_VIEW,Link);
                startActivity(intent);
            }
        });

        LayoutOpcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VentanaMsgDialog(dialog);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void VentanaMsgDialog(Dialog dialog) {
        new SweetAlertDialog(C1_Menu_Neg.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("Cerrar Sesión")
                .setContentText("Quieres Cerrar Sesión?")
                .setCancelText("No").setConfirmText("Si")
                .showCancelButton(true).setCancelClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    // startActivity(new Intent(RegistroLocal.this, HorarioNGC.class));
                    System.out.println("sesion no cerrado");
                }).setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    System.out.println("sesion cerrado");
                    mAuth.signOut();
                    dialog.dismiss();
                    finish();
                    Intent intent = new Intent(C1_Menu_Neg.this, B1_Login.class);
                    startActivity(intent);
                }).show();
    }
}