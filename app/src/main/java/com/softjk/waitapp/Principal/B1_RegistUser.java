package com.softjk.waitapp.Principal;

import static com.softjk.waitapp.Sistema.Metodos.MultiMetds.toastCorrecto;
import static com.softjk.waitapp.Sistema.Metodos.MultiMetds.toastIncorrect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Metodos.PreferencesManager;
import com.softjk.waitapp.Negocio.B2_RegistNeg;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;

public class B1_RegistUser extends AppCompatActivity {
    Button Guardar;
    EditText email, password, ConfirPass, User;
    ImageView Font1;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.b1_regist_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferencesManager = new PreferencesManager(B1_RegistUser.this);
        progressDialog = new ProgressDialog(this);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        User = findViewById(R.id.txtUser);
        email = findViewById(R.id.txtMail);
        password = findViewById(R.id.txtContraseña);
        ConfirPass = findViewById(R.id.txtContraseñaR);
        Guardar = findViewById(R.id.btnGuardar);
        Font1 = findViewById(R.id.imageView3);

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Usua = User.getText().toString();
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                String TipoUser = preferencesManager.getString("TUser","");

                SharedPreferences preferences = getSharedPreferences("Token", Context.MODE_PRIVATE);
                String tok = preferences.getString("idToken","");

                if (emailUser.isEmpty() && passUser.isEmpty()){
                    Toast.makeText(B1_RegistUser.this, "Complete los datos", Toast.LENGTH_SHORT).show();
                }else if (passUser.length()<6) {
                    password.setError("Su contraseña debe tener minimo 6 digitos");
                    password.requestFocus();
                }else{
                    registerUser(TipoUser, emailUser, passUser,Usua,tok);
                }
            }
        });

        //Detectar Campo de Rotacion de Pantalla
        MultiMetds.OrientaConfi(B1_RegistUser.this,Font1);
    }

    private void registerUser(String TipoUser, String emailUser, String passUser,String User,String Tokn) {
        if (password.getText().toString().equals(ConfirPass.getText().toString())) {
            progressDialog.setMessage("Guardando Datos");
            progressDialog.show();
            progressDialog.setCancelable(false);
            mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String id = mAuth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", id);
                        map.put("User", User);
                        map.put("Tipo", TipoUser);
                        map.put("Email", emailUser);
                        map.put("Token", Tokn);

                        mFirestore.collection("User").document(id).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if (TipoUser.equals("Cliente")) {
                                    finish();
                                    startActivity(new Intent(B1_RegistUser.this, B1_Login.class));
                                    String msg = "Cliente Registrado";
                                    toastCorrecto(B1_RegistUser.this, msg);
                                    progressDialog.dismiss();
                                } else if (TipoUser.equals("Negocio")) {
                                    finish();
                                    startActivity(new Intent(B1_RegistUser.this, B2_RegistNeg.class));
                                    String msg = "Usuario Registrado";
                                    toastCorrecto(B1_RegistUser.this, msg);
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String msg = "Error al guardar";
                                toastIncorrect(B1_RegistUser.this, msg);
                            }
                        });
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(B1_RegistUser.this, "Error de Conexión", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    String msg="Error al registrar";
                    toastIncorrect(B1_RegistUser.this, msg);
                }
            });
        }else {
            String msg="La Contraseña no Coincide";
            toastIncorrect(B1_RegistUser.this, msg);
        }
    }
}