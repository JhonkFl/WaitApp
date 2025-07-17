package com.softjk.waitapp.Negocio;

import static com.softjk.waitapp.Sistema.Metodos.MultiMetds.IMG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.R;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class B2_RegistNeg extends AppCompatActivity {

    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    StorageReference storageReference;

    PreferencesManager preferencesManager;
    ProgressDialog progressDialog;

    EditText NumbreLocal, Locatizacion, TipoEstablecimiento, Ciudad;
    TextView MensajeTitulo,Codigo;
    ImageView IconoLocal, publicidad;
    Spinner CantidadSala;
    Button Registrar;

    String idd;
    String codig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.b2_regist_neg);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String id = getIntent().getStringExtra("idNeg");

        progressDialog = new ProgressDialog(this);
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        preferencesManager = new PreferencesManager(B2_RegistNeg.this,"User");

        Registrar = findViewById(R.id.btnCrearLocalV);
        IconoLocal = findViewById(R.id.LogoNegcReg);
        NumbreLocal = findViewById(R.id.txtNombreLocal);
        Locatizacion = findViewById(R.id.txtLocalizacion);
        TipoEstablecimiento = findViewById(R.id.txtTipo);
        Ciudad = findViewById(R.id.txtHorario);
        Codigo = findViewById(R.id.lblCodigo);
        MensajeTitulo = findViewById(R.id.lblTituloActualizarNegocio);
        CantidadSala = findViewById(R.id.spCantidadSalas);
        publicidad = findViewById(R.id.publicidadRegis);

        String[] opciones = {"1","2","3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.z_custom_spinner, opciones);
        CantidadSala.setAdapter(adapter);
        codig = UUID.randomUUID().toString().toUpperCase().substring(0,4);
        Codigo.setText(codig);

        String URL = "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/ab4d8579979831.5cd394e751fc7.gif";
        MultiMetds.IMG(B2_RegistNeg.this,URL,publicidad,"No");

        if (id == null || id == "") {
            Registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String codigo = Codigo.getText().toString().trim();
                    String NomLocal = NumbreLocal.getText().toString().trim();
                    String Localiza = Locatizacion.getText().toString().trim();
                    String TipoNegoci = TipoEstablecimiento.getText().toString().trim();
                    String Municipio = Ciudad.getText().toString().trim();
                    String Cant = CantidadSala.getSelectedItem().toString();
                    String idUser = mAuth.getCurrentUser().getUid();

                    if (TextUtils.isEmpty(NomLocal)) {
                        NumbreLocal.setError("Ingrese el Nombre de su Negocio");
                        NumbreLocal.requestFocus();
                    } else if (TextUtils.isEmpty(TipoNegoci)) {
                        TipoEstablecimiento.setError("Ingrese la Categoria de su Negocio");
                        TipoEstablecimiento.requestFocus();
                    } else if (TextUtils.isEmpty(Localiza)) {
                        Locatizacion.setError("Ingrese la dirección de su local");
                        Locatizacion.requestFocus();
                    } else if (TextUtils.isEmpty(Municipio)) {
                        Ciudad.setError("Ingrese su Ciudad o Municipio");
                        Ciudad.requestFocus();
                    } else {

    // ---------------- Datos a Guardar ----------------------------------------------------
                        Map<String, Object> map = new HashMap<>();
                        map.put("ID", idUser);
                        map.put("Codigo", codigo);
                        map.put("Nombre", NomLocal);
                        map.put("Ubicacion", Localiza);
                        map.put("Tipo", TipoNegoci);
                        map.put("Salas", Cant);
                        map.put("Municipio", Municipio);

                        DatosFirestoreBD.GuardarDatos(B2_RegistNeg.this,"Negocios",idUser, map, "Creando Negocio", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if ("Guardado".equals(resultado)) {
                                    preferencesManager.saveString("TUser","Negocio");
                                    GuardarConfLocal();
                                    Registrar.setText("Siguiente");
                                    finish();
                                    Intent intent = new Intent(B2_RegistNeg.this, B3_Logo.class);
                                    intent.putExtra("Accion","New");
                                    startActivity(intent);
                                } else {
                                    // Error
                                }
                            }
                        });

                    }
                }
            });
        }else {
            idd = id;
            Codigo.setVisibility(View.GONE);
            MensajeTitulo.setText("Ok vamos Actualizar su Negocio");
            Registrar.setText("Actualizar");
            ObtenerDatos(id);

            IconoLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(B2_RegistNeg.this,B3_Logo.class);
                    intent.putExtra("Accion","Actualizar");
                    startActivity(intent);
                }
            });

            Registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String NomLocal = NumbreLocal.getText().toString().trim();
                    String Localiza = Locatizacion.getText().toString().trim();
                    String TipoNegoci = TipoEstablecimiento.getText().toString().trim();
                    String Municipio = Ciudad.getText().toString().trim();
                    String Cant = CantidadSala.getSelectedItem().toString();

                    if (NomLocal.isEmpty() && Localiza.isEmpty() && TipoNegoci.isEmpty() && Municipio.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingrese los Datos", Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Nombre", NomLocal);
                        map.put("Ubicacion", Localiza);
                        map.put("Tipo", TipoNegoci);
                        map.put("Municipio", Municipio);
                        map.put("Salas", Cant);

                        DatosFirestoreBD.ActualizarDatos(B2_RegistNeg.this,"Negocios",id, map, "Actualizando", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if ("Actualizado".equals(resultado)) {
                                    GuardarConfLocal();
                                    startActivity(new Intent(B2_RegistNeg.this, C1_Menu_Neg.class));
                                    finish();
                                } else {
                                    // Error
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    private void ObtenerDatos(String id) {
        System.out.println("Buscando Datos");
        mfirestore.collection("Negocios").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String nombreLocal = documentSnapshot.getString("Nombre");
                String tipo = documentSnapshot.getString("Tipo");
                String localiza = documentSnapshot.getString("Ubicacion");
                String ciudad = documentSnapshot.getString("Municipio");
                //int Cantidad = Integer.parseInt(documentSnapshot.getString("Cantidad"));
                String Canti = documentSnapshot.getString("Salas");
                String Fotolocal = documentSnapshot.getString("Logo");

                System.out.println("ver Cant sala " + Canti);

                if (Canti.equals("1")) {
                    CantidadSala.setSelection(0);
                } else if (Canti.equals("2")) {
                    CantidadSala.setSelection(1);
                    System.out.println("poniendo al 2 posicion de spinner sala");
                } else if (Canti.equals("3")) {
                    CantidadSala.setSelection(2);
                }

                NumbreLocal.setText(nombreLocal);
                TipoEstablecimiento.setText(tipo);
                Ciudad.setText(ciudad);
                Locatizacion.setText(localiza);
                System.out.println("Poniendo Datos");

                IMG(B2_RegistNeg.this, Fotolocal, IconoLocal,"No");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void GuardarConfLocal() {
        String Cant = CantidadSala.getSelectedItem().toString();

        if (Cant.equals("1")){
            GuardarSalas("Sala1");
        } else if (Cant.equals("2")) {
            GuardarSalas("Sala1");
            GuardarSalas("Sala2");
        } else if (Cant.equals("3")) {
            GuardarSalas("Sala1");
            GuardarSalas("Sala2");
            GuardarSalas("Sala3");
        }
    }

    private void GuardarSalas(String sala) {
        String idUser = mAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put("Activacion", "Si");
        map.put("Tiempo", 0);
        String Coleccion = "Negocios/"+idUser+"/TiempoGlobal";

        DatosFirestoreBD.GuardarDatos(B2_RegistNeg.this,Coleccion,sala,map,"Configutrando "+sala, new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
               System.out.println("SalasConfig: "+resultado);
            }
        });
    }
}