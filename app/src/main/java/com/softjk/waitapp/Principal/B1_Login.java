package com.softjk.waitapp.Principal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Cliente.C1_Menu_Client;
import com.softjk.waitapp.Negocio.C1_Menu_Neg;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.R;

public class B1_Login extends AppCompatActivity {
    Button Iniciar, Register;
    EditText email, password;
    ImageView img;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private FirebaseFirestore mfirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.b1_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializacion
        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        email = findViewById(R.id.txtCorreo);
        password = findViewById(R.id.txtCont);
        Iniciar = findViewById(R.id.btnIniciarS);
        Register = findViewById(R.id.btnRegistr);
        img = findViewById(R.id.imgLogin);

        //Aciones
        Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = email.getText().toString().trim();
                String passUser = password.getText().toString().trim();
                if (TextUtils.isEmpty(emailUser)) {
                    email.setError("Ingrese su correo electronico");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(passUser)) {
                    password.setError("Ingrese su contraseña");
                    password.requestFocus();
                }
                else if (passUser.length()<6) {
                    password.setError("Su contraseña es mayor de 5 digitos");
                    password.requestFocus();
                } else {
                    loginUser(emailUser, passUser);
                }
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(B1_Login.this, B1_RegistUser.class));
            }
        });
    } //Fin del OnCreate

    private void loginUser(String emailUser, String passUser) {

        mAuth.signInWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.setMessage("Iniciando Sesión");
                progressDialog.show();
                if (task.isSuccessful()){
                    String User = getIntent().getStringExtra("TUser");
                    if (User == null || User.equals("Cliente")) {
                        startActivity(new Intent(B1_Login.this, C1_Menu_Client.class));
                        progressDialog.dismiss();
                        finish();
                    } else if (User.equals("Negocio")) {
                        startActivity(new Intent(B1_Login.this, C1_Menu_Neg.class));
                        progressDialog.dismiss();
                        finish();
                    }

                }else{
                    progressDialog.dismiss();
                    // Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    String msg = "Error de Usuario";
                    toastIncorrecto(msg);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(LoginActivity.this, "Error al inciar sesión", Toast.LENGTH_SHORT).show();
                String msg = "Error al iniciar sesión";
                progressDialog.dismiss();
                toastIncorrecto(msg);
            }
        });
    }

    public void toastIncorrecto(String msg) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.z_custom_toast_error, (ViewGroup) findViewById(R.id.ll_custom_toast_error));
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast2);
        txtMensaje.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }


}