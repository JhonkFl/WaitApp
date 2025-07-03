package com.softjk.waitapp.Negocio;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Sistema.Modelo.SpMinutSegunds;
import com.softjk.waitapp.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegistrarServicios extends AppCompatActivity {
    String idd;
    ProgressDialog progressDialog;
    StorageReference storageReference;
    String storage_path = "Servicios/*"; //Crear carpeta para guardar los datosIMG
    private static final int COD_SEL_STOREGE = 200;
    private static final int COD_SEL_IMAGE = 300;
    private Uri image_url_Serv;
    String photo = "LogoServices";
    TextView Mensaje;
    ImageView ImagenServicio, LogoLocal, publicidad;
    EditText Categoria, Precio, NomServicio;
    Button GuardarServ;
    Spinner TiempoServicio;
    private String idLocal;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;
    String idUser;

    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.f_registrar_servicios);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        preferencesManager = new PreferencesManager(this);

        String idServicio = getIntent().getStringExtra("id_Servicios");
        String idUser = mAuth.getCurrentUser().getUid();

        NomServicio = findViewById(R.id.txtNombreServicio);
        Categoria = findViewById(R.id.txtCategoria);
        Precio = findViewById(R.id.txtPrecioServicio);
        GuardarServ = findViewById(R.id.btnGuardarServicios);
        ImagenServicio= findViewById(R.id.ImagenServicio);
        publicidad = findViewById(R.id.publicidadserv);
        TiempoServicio = findViewById(R.id.spTipoServicio); //*********Falta Agregar los datos a Registrar**********

        String [] opciones = {"5 minutos","10 minutos","15 minutos","20 minutos","25 minutos","30 minutos","45 minutos"
                ,"50 minutos","1 hora","1:15 Horas", "1:30 Horas",  "1:45 Horas",  "2 Horas"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.z_custom_spinner,opciones);
        TiempoServicio.setAdapter(adapter);
        String URL = "https://drafterscorner.com/wp-content/uploads/2022/10/AV_BLOG_1.gif";
        MultiMetds.IMG(RegistrarServicios.this,URL,publicidad,"No");

        if (idServicio==null || idServicio==""){
            GuardarServ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String NombreServ = NomServicio.getText().toString().trim();
                    String CategoriServ = Categoria.getText().toString().trim();
                    String TiempoServ = TiempoServicio.getSelectedItem().toString();
                    int newPosition = TiempoServicio.getSelectedItemPosition();
                    Double PrecioServ = Double.parseDouble(Precio.getText().toString().trim());
                    SpMinutSegunds.setTiempo(TiempoServ);

                    if(NombreServ.isEmpty() && CategoriServ.isEmpty() && TiempoServ.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    }else{
                        //FirestoreMetds.GuardarDatos(RegistrarServicios.this,"Negocios/"+idUser+"/Servicios",id.getId(),map,"Guardando Servicio");
                        GuardSerBD(NombreServ,CategoriServ,PrecioServ,newPosition);
                    }
                }
            });
        }else {
            idd=idServicio;
            ObtenerDatos(idServicio);
            GuardarServ.setText("Actualizar");
            GuardarServ.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String NombreSer = NomServicio.getText().toString().trim();
                    String CategoSer = Categoria.getText().toString().trim();
                    String TiempoSer = TiempoServicio.getSelectedItem().toString().trim();
                    int newPosition = TiempoServicio.getSelectedItemPosition();
                    Double PrecioSer = Double.parseDouble(Precio.getText().toString().trim());

                    SpMinutSegunds.setTiempo(TiempoSer);

                    if(NombreSer.isEmpty() && CategoSer.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Ingresar los datos", Toast.LENGTH_SHORT).show();
                    }else{
                        ActualizarDatos(NombreSer, CategoSer, PrecioSer, idServicio, newPosition);
                    }
                }
            });

            ImagenServicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPhoto();
                }
            });
        }

    }

    private void GuardSerBD(String NombreServ, String CategoriServ,double PrecioServ, int position) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando Servicio");
        progressDialog.show();

        DocumentReference id = mfirestore.collection("Negocios/"+idUser+"/Servicios").document();
        int TServ = SpMinutSegunds.getTiempo();
        Map<String, Object> map = new HashMap<>();
        map.put("Id", idUser);
        map.put("Nombre", NombreServ);
        map.put("Categoria", CategoriServ);
        map.put("Precio", PrecioServ);
        map.put("Tiempo", TServ);
        map.put("SPosition",position);
        mfirestore.collection("Negocios/"+idUser+"/Servicios").document(id.getId()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(RegistrarServicios.this, "Datos Guardados con Exito", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                idd = id.getId();
                Msg();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrarServicios.this, "Error al Crear el Local Virtual", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarDatos(String nombreSer, String categoSer, Double precioSer, String idServicio, int position) {
        int TServ = SpMinutSegunds.getTiempo();
        Map<String, Object> map = new HashMap<>();
        map.put("Nombre", nombreSer);
        map.put("Categoria", categoSer);
        map.put("Precio", precioSer);
        map.put("Tiempo",TServ);
        map.put("SPosition",position);

        DatosFirestoreBD.ActualizarDatos(RegistrarServicios.this, "Negocios/" + idUser + "/Servicios", idServicio, map, "Actualizando Servicio", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
                System.out.println(resultado+ "Servicio");
            }
        });

    }

    private void ObtenerDatos(String idServicio) {
        mfirestore.collection("Negocios/"+idUser+"/Servicios").document(idServicio).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                DecimalFormat format = new DecimalFormat("0.00");
//            format.setMaximumFractionDigits(2);
                String Nombre = documentSnapshot.getString("Nombre");
                String Categori = documentSnapshot.getString("Categoria");
                Double precio = documentSnapshot.getDouble("Precio");
                String FotoServicio = documentSnapshot.getString("Logo");
                Long posit = documentSnapshot.getLong("SPosition");

                NomServicio.setText(Nombre);
                Categoria.setText(Categori);
                Precio.setText(format.format(precio));
                TiempoServicio.setSelection(Math.toIntExact(posit));

                MultiMetds.IMG(RegistrarServicios.this,FotoServicio,ImagenServicio,"No");




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Msg() {
        new SweetAlertDialog(RegistrarServicios.this, SweetAlertDialog.NORMAL_TYPE).setTitleText("Subir Imagen")
                .setContentText("Quiere Agregar una Imagen del Servicio?")
                .setCancelText("No").setConfirmText("Si")
                .showCancelButton(true).setCancelClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    Precio.setText("");
                    Categoria.setText("");
                    NomServicio.setText("");
                    TiempoServicio.setSelection(0);
                }).setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    uploadPhoto();
                }).show();
    }

    private void uploadPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, COD_SEL_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("image_url","requestCode - RESULT_OK: "+requestCode+" "+RESULT_OK);
        if (resultCode == RESULT_OK){
            if (requestCode == COD_SEL_IMAGE){
                image_url_Serv = data.getData();

                subirFoto(image_url_Serv);
                this.ImagenServicio.setImageURI(image_url_Serv);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void subirFoto(Uri imageUrlServ) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String rute_storage_photo = storage_path + "" + photo + "" + mAuth.getUid() +""+ idd;
        StorageReference reference = storageReference.child(rute_storage_photo);
        reference.putFile(imageUrlServ).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String download_uri = uri.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("Logo", download_uri);
                            mfirestore.collection("Negocios/"+idUser+"/Servicios").document(idd).update(map);
                            Toast.makeText(RegistrarServicios.this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistrarServicios.this, C1_Menu_Neg.class));
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrarServicios.this, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
            }
        });
    }
}