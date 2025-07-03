package com.softjk.waitapp.Principal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.softjk.waitapp.Cliente.C1_Menu_Client;
import com.softjk.waitapp.Negocio.C1_Menu_Neg;
import com.softjk.waitapp.Sistema.Metodos.AlertDialogMetds;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.R;

public class A_Bienvenida extends AppCompatActivity {
    Button Negocio,Cliente;
    TextView Bienvenida;
    ImageView Font1;
    PreferencesManager preferencesManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.a_bienvenida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesManager = new PreferencesManager(A_Bienvenida.this);
        Negocio = findViewById(R.id.btnNegocio);
        Cliente = findViewById(R.id.btnCliente);
        Bienvenida = findViewById(R.id.lblMsgBienvenida);
        mAuth = FirebaseAuth.getInstance();
        Font1 = findViewById(R.id.portBienvenida);

        Negocio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("TUser","Negocio");
                AlertDialogMetds.MsgOpenActiv(A_Bienvenida.this,"Sea Bienvenid@!!!",
                        "Es su primera vez en nuestra App?", B1_RegistUser.class, B1_Login.class,
                        "TUser","Negocio");
            }
        });

        Cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("TUser","Cliente");
                AlertDialogMetds.MsgOpenActiv(A_Bienvenida.this,"Sea Bienvenid@!!!",
                        "Es su primera vez en nuestra App?", B1_RegistUser.class, B1_Login.class,
                        "TUser","Cliente");
            }
        });


        //Detectar Campo de Rotacion de Pantalla
        MultiMetds.OrientaConfi(A_Bienvenida.this,Font1);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        String Tipo = preferencesManager.getString("TUser","Ninguno");
        if (user != null){
            if (Tipo.equals("Cliente")){
                System.out.println(Tipo);
                startActivity(new Intent(A_Bienvenida.this, C1_Menu_Client.class));
            } else if (Tipo.equals("Negocio")) {
                System.out.println(Tipo);
                startActivity(new Intent(A_Bienvenida.this, C1_Menu_Neg.class));
            }else {
                System.out.println("Iniciar Sesion");
            }

        }
    }

}