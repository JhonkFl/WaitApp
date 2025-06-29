package com.softjk.waitapp;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.softjk.waitapp.Adapter.Negc.AdpTabLayoutNeg;
import com.softjk.waitapp.Adapter.Negc.Opc2AdpTabLayoutNeg;
import com.softjk.waitapp.Metodos.MultiMetds;

public class E1_Sala_Neg extends AppCompatActivity {

    private FirebaseFirestore mfirestore;
    TextView NombreLocal, lblTiempo;
    ImageView logoLocal;

    //SalaAdapterNGC mAdapter;
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

        mfirestore = FirebaseFirestore.getInstance();
        NombreLocal = findViewById(R.id.lblSalaNomLocalNGC);
        logoLocal = findViewById(R.id.logoSalaLocalNGC);
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
                String Num = String.valueOf(tab.getPosition());
                System.out.println("ver tab "+Num);
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
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Tabla.getTabAt(position).select();
            }
        });
        //*****************Fin de Tabla Pestaña************************//


    }

    private void ObtenerDatos() {
    String idUser = mAuth.getCurrentUser().getUid();
        mfirestore.collection("Negocios").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("Datos del Usuario Encontrados");
                String Nombre = documentSnapshot.getString("Nombre");
                String Logo = documentSnapshot.getString("Logo");
                String Salas = documentSnapshot.getString("Salas");
                NombreLocal.setText(Nombre);
                MultiMetds.IMG(E1_Sala_Neg.this,Logo,logoLocal,"Si");
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

    public void mostrarTab(int position){
        TabLayout.Tab tab = Tabla.getTabAt(position);
        if(tab != null){
            View tabView = tab.view;
            tabView.setVisibility(View.VISIBLE);
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
             /*   if (tab.getPosition() == position) {
                    viewPager2.setVisibility(View.GONE); // Ocultar toda la vista

                } else {
                    viewPager2.setVisibility(View.VISIBLE); // Mostrar
                } */
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