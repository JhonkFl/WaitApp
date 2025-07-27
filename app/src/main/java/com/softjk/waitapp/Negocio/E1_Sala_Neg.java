package com.softjk.waitapp.Negocio;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Cliente.E1_Sala_Client;
import com.softjk.waitapp.Negocio.AdapterN.Opc2AdpTabLayoutNeg;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.R;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class E1_Sala_Neg extends AppCompatActivity {

    private FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;

    TabLayout Tabla;
    ViewPager2 viewPager2;
    Opc2AdpTabLayoutNeg viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.e1_sala_neg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PreferencesManager preference = new PreferencesManager(E1_Sala_Neg.this,"Negocio");
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Tabla = findViewById(R.id.tablaSeccioniconNeg);
        viewPager2 = findViewById(R.id.viewPager1Neg);
        viewPagerAdapter = new Opc2AdpTabLayoutNeg(this);
        viewPager2.setAdapter(viewPagerAdapter);

        ObtenerDatos();

        Tabla.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) { //mostrar viewPager2 al deslizar sala
                super.onPageSelected(position);
                Tabla.getTabAt(position).select();
            }
        });
        //*****************Fin de Tabla Pestaña************************//

        //
        String candado = preference.getString("Candado","");
        if (candado.equals("2")) {
            TabLayout.Tab tab2 = Tabla.getTabAt(1);
            if (tab2 != null) {Tabla.selectTab(tab2);}
        } else if (candado.equals("3")) {
            TabLayout.Tab tab3 = Tabla.getTabAt(2);
            if (tab3 != null) {Tabla.selectTab(tab3);}
        }else {
            System.out.println("No realizar accion Tab fila");
        }
    }

    private void ObtenerDatos() {
    String idUser = mAuth.getCurrentUser().getUid();
        mfirestore.collection("Negocios").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String Salas = documentSnapshot.getString("Salas");
                PonerSalas(Salas);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(E1_Sala_Neg.this, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void PonerSalas(String salas) {
        if (salas.equals("1")){ //Ocultar Tabs de acuerdo con la Cantidad de Salas
            Tabla.setVisibility(View.GONE);
            viewPager2.setUserInputEnabled(false); //Desactivar Funcion Touch
        } else if (salas.equals("2")) {
            ocultarTab(2);
        }else {
            System.out.println("3 Salas");
        }
    }

    public void ocultarTab(int position) {
        TabLayout.Tab tab = Tabla.getTabAt(position);
        if (tab != null) {
            View tabView = tab.view; // Obtiene la vista de la pestaña
            tabView.setVisibility(View.GONE); // Oculta la vista
            OcultarDiseTabla(position);
        }
    }

    private void OcultarDiseTabla(int position) {
        Tabla.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() < position){
                    viewPager2.setUserInputEnabled(false); // Desactivar funcion Touch
                }
                if (tab.getPosition() == 0){
                    viewPager2.setUserInputEnabled(true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}