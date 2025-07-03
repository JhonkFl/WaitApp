package com.softjk.waitapp.Cliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.softjk.waitapp.Cliente.AdapterC.Opc2AdpTabLayout;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.R;

public class E1_Sala_Client extends AppCompatActivity {
    public static E1_Sala_Client currentInstance; //Instancia para cerrar la actividad desde otra clase (AdapterServicioClint) --> OnCreate y onDestroy

    TabLayout Tabla;
    ViewPager2 viewPager2;
    Opc2AdpTabLayout viewPagerAdapter;

    TextView NombreNg;
    ImageView LogoNg;

    static String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.e1_sala_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        crearCanalNotificacion(this); //para alarma del temporizador

        //Obtener los Valores enviados del Adapter
        PreferencesManager preferencesManager = new PreferencesManager(E1_Sala_Client.this);
        String entrar = getIntent().getStringExtra("EntrarSala");
        String idNeg = getIntent().getStringExtra("idNeg");
        String SalasN = getIntent().getStringExtra("NSalas");
        String Nombre = getIntent().getStringExtra("Negocio");
        String Logo = getIntent().getStringExtra("Logo");

        System.out.println("Datos Recibidos del Adaptador: "+SalasN+ " idNeg: "+idNeg);
        NombreNg = findViewById(R.id.lblSalaNomLocal);
        LogoNg = findViewById(R.id.logoSalaLocal);
        System.out.println(Nombre+" "+Logo);


        if (entrar != null) {
            preferencesManager.saveString("NombreNegocio", Nombre);
            preferencesManager.saveString("CantidadSalas", SalasN);
            preferencesManager.saveString("LogoNegocio", Logo);
            preferencesManager.saveString("idNegocioCliente", idNeg);
        }

        //Si Datos del Adapter es Null
        if (Logo == null & Nombre == null){
            Logo = preferencesManager.getString("LogoNegocio","");
            Nombre = preferencesManager.getString("NombreNegocio","");
        }

        MultiMetds.IMG(E1_Sala_Client.this,Logo,LogoNg,"Si");
        NombreNg.setText(Nombre);


        //*****************Tabla Pestaña************************//
        Tabla = findViewById(R.id.tablaSeccionicon);
        viewPager2 = findViewById(R.id.viewPager1);
        viewPagerAdapter = new Opc2AdpTabLayout(this);
        viewPager2.setAdapter(viewPagerAdapter);

        if (SalasN == null){
            SalasN = preferencesManager.getString("CantidadSalas","");
        }
        if (SalasN.equals("1")){ //Ocultar Tabs de acuerdo con la Cantidad de Salas
            Tabla.setVisibility(View.GONE);
            viewPager2.setUserInputEnabled(false); //Desactivar Funcion Touch
        } else if (SalasN.equals("2")) {
            System.out.println("Solo mostrar sala 1 y 2");
            ocultarTab(2);
        }else {
            System.out.println("3");
        }

        Tabla.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                System.out.println("ver tab clic position: "+tab.getPosition());
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
                System.out.println("getSelectedTabPosition(): " + Tabla.getSelectedTabPosition());
                System.out.println("position: " + position);
                if (Tabla.getSelectedTabPosition() != position) {
                    Tabla.getTabAt(position).select();
                    System.out.println("Página seleccionada: " + position);
                } else {
                    System.out.println("La posición ya está seleccionada: " + position);
                }
            }
        });
        //*****************Fin de Tabla Pestaña************************//



        //Seleccionar tabla cuando se actualice la actividad de acuerdo a los Dialogos para el Usuario --- Program personalizado
        String Sala = preferencesManager.getString("FilaSala","");
        System.out.println("Ver Sala Tab: "+Sala);

        if (Sala.equals("Sala1")){
            TabLayout.Tab tab1 = Tabla.getTabAt(0);
            if (tab1 != null) {Tabla.selectTab(tab1);}
        } else if (Sala.equals("Sala2")) {
            TabLayout.Tab tab2 = Tabla.getTabAt(1);
            if (tab2 != null) {Tabla.selectTab(tab2);}
        } else if (Sala.equals("Sala3")) {
            TabLayout.Tab tab3 = Tabla.getTabAt(2);
            if (tab3 != null) {Tabla.selectTab(tab3);}
        }else {
            System.out.println("No realizar accion Tab fila");
        }

    }

    public void ocultarTab(int position) {
        TabLayout.Tab tab = Tabla.getTabAt(position);
        if (tab != null) {
            View tabView = tab.view; // Obtiene la vista de la pestaña
            tabView.setVisibility(View.GONE); // Oculta la vista
            viewPagerAdapter.ocultarFragment(position);
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



    //*******************Cliclos de Vidad Actividad ****************************

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        currentInstance = this; // Guardamos la instancia actual
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentInstance = null; // Limpiamos la referencia para evitar fugas de memoria
    }

    @Override
    public void onBackPressed() {
        finish(); //Cerrar Actividad si presiona el boton de retroceder del dispositivo
        super.onBackPressed();
    }

    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("canal_alarma", "Alarma", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}