package com.softjk.waitapp;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.softjk.waitapp.FragMenu.Client.MenuAyuda;
import com.softjk.waitapp.FragMenu.Client.MenuConfi;
import com.softjk.waitapp.FragMenu.Client.MenuInicio;
import com.softjk.waitapp.FragMenu.Client.MenuNovedades;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.databinding.C1MenuClienteBinding;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class C1_Menu_Client extends AppCompatActivity {
    C1MenuClienteBinding binding;
    FirebaseAuth mAuth;
    TextView lblTrancition;
    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = C1MenuClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new MenuInicio());

        mAuth = FirebaseAuth.getInstance();
        preferencesManager = new PreferencesManager(C1_Menu_Client.this);
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            if (item.getItemId() == R.id.inicio) {
                fragment = new MenuInicio();
            } else if (item.getItemId() == R.id.novedades) {
                fragment = new MenuNovedades();
            } else if (item.getItemId() == R.id.confi) {
                fragment = new MenuConfi();
            }else if (item.getItemId() == R.id.ayuda) {
                fragment = new MenuAyuda();
                Bundle bundle = new Bundle();
                bundle.putString("Ayuda", "Cliente");
                fragment.setArguments(bundle);
            }
            if (fragment != null) {
                replaceFragment(fragment);
            }
            return true;
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameclient, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.z_custom_button_menu);

        String Accion = preferencesManager.getString("TUser","");
        lblTrancition = dialog.findViewById(R.id.lblSesion2);
        if (Accion.equals("Negocio")){lblTrancition.setText("Regresar a mi Negocio");}

        LinearLayout LayoutOpcion1 = dialog.findViewById(R.id.layoutOpcio1);
        LinearLayout LayoutOpcion2 = dialog.findViewById(R.id.layoutOpcio2);
        LinearLayout LayoutOpcion3 = dialog.findViewById(R.id.layoutOpcion3);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        LayoutOpcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(C1_Menu_Client.this, C1_Menu_Neg.class);
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
        new SweetAlertDialog(C1_Menu_Client.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("Cerrar Sesión")
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
                    Intent intent = new Intent(C1_Menu_Client.this, B1_Login.class);
                    startActivity(intent);
                }).show();
    }
}