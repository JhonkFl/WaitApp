package com.softjk.waitapp.Negocio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softjk.waitapp.Sistema.Metodos.ActualizarIMG;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.R;

public class B3_Logo extends AppCompatActivity {

    private ActivityResultLauncher<Intent> seleccionarFotoLauncher;
    StorageReference storageReference;
    FirebaseFirestore mfirestore;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    Button Siguiente;
    ImageView IconoLocal, Publicidad;

    static String Accion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.b3_logo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        IconoLocal = findViewById(R.id.AddIMG);
        Siguiente = findViewById(R.id.btnSiguiente);
        Publicidad = findViewById(R.id.imgPublicidad);

        Accion = getIntent().getStringExtra("Accion");

        IconoLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                seleccionarFotoLauncher.launch(i);
            }
        });

        Siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Accion.equals("New")){
                    Intent intent = new Intent(B3_Logo.this, B3_Horario.class);
                    finish();
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(B3_Logo.this, C1_Menu_Neg.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

        String URL = "https://drafterscorner.com/wp-content/uploads/2022/10/AV_BLOG_1.gif";
        MultiMetds.IMG(B3_Logo.this,URL,Publicidad,"No");


        // Subir IMG a Firebase
        seleccionarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri imageUrl = result.getData().getData();

                        ActualizarIMG uploader = new ActualizarIMG(B3_Logo.this, progressDialog, mAuth, mfirestore,
                                storageReference, "ImgNegocio/", "Logo");
                        System.out.println("Uri: "+imageUrl);
                        uploader.subirFoto(imageUrl,IconoLocal, "Negocios", mAuth.getUid(), new ActualizarIMG.FotoUploadCallback() {
                            @Override
                            public void onUploadSuccess(String mensaje) {
                                System.out.println(mensaje);
                                if (Accion.equals("New")){
                                    finish();
                                    startActivity(new Intent(B3_Logo.this, B3_Horario.class));
                                } else if (Accion.equals("Actualizar")) {
                                    finish();
                                    startActivity(new Intent(B3_Logo.this, C1_Menu_Neg.class));
                                }
                            }

                            @Override
                            public void onUploadError(String mensaje) {
                                System.out.println(mensaje);
                            }
                        });
                    }
                }
        );

    }



}