package com.softjk.waitapp.Negocio;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Sistema.Metodos.DatosFirestoreBD;
import com.softjk.waitapp.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class B3_Horario extends AppCompatActivity implements View.OnClickListener{

    TextView HoraAmL,HoraPmL,HoraAmM,HoraPmM,HoraAmMer,HoraPmMier,HoraAmJ,HoraPmJ,HoraAmV,HoraPmV,HoraAmS,HoraPmS,HoraAmD,HoraPmD;
    Switch Lunes,Martes,Miercules,Jueves,Viernes,Sabado,Domingo;
    LinearLayout ListL, ListM,ListMi,ListJ,ListV,ListS,ListD;
    Button Guardar,Manual;
    private FirebaseFirestore mfirestore;
    private FirebaseAuth mAuth;

    static String DiaL = "lunes";
    static String DiaM = "martes";
    static String DiaMi = "miércoles";
    static String DiaJ = "jueves";
    static String DiaV = "viernes";
    static String DiaS = "sábado";
    static String DiaD = "domingo";
    static String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.b3_horario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        HoraAmL = findViewById(R.id.lblAmL);
        HoraPmL = findViewById(R.id.lblPmL);
        HoraAmM = findViewById(R.id.lblAmM);
        HoraPmM = findViewById(R.id.lblPmM);
        HoraAmMer = findViewById(R.id.lblAmMier);
        HoraPmMier = findViewById(R.id.lblPmMier);
        HoraAmJ = findViewById(R.id.lblAmJ);
        HoraPmJ = findViewById(R.id.lblPmJ);
        HoraAmV = findViewById(R.id.lblAmV);
        HoraPmV = findViewById(R.id.lblPmV);
        HoraAmS = findViewById(R.id.lblAmS);
        HoraPmS = findViewById(R.id.lblPmS);
        HoraAmD = findViewById(R.id.lblAmD);
        HoraPmD = findViewById(R.id.lblPmD);
        Lunes = findViewById(R.id.swLunes);
        Martes = findViewById(R.id.swMartes);
        Miercules = findViewById(R.id.swMiercules);
        Jueves = findViewById(R.id.swJueves);
        Viernes = findViewById(R.id.swViernes);
        Sabado = findViewById(R.id.swSabado);
        Domingo = findViewById(R.id.swDomingo);
        ListL = findViewById(R.id.linerLunes);
        ListM = findViewById(R.id.linerMartes);
        ListMi = findViewById(R.id.linerMiercoles);
        ListJ = findViewById(R.id.linerJueves);
        ListV = findViewById(R.id.linerViernes);
        ListS = findViewById(R.id.linerSabado);
        ListD = findViewById(R.id.linerDomingo);
        Guardar = findViewById(R.id.btnGuardarHorario);
        Manual= findViewById(R.id.btnManual);

        HoraAmL.setOnClickListener(this);
        HoraPmL.setOnClickListener(this);
        HoraAmM.setOnClickListener(this);
        HoraPmM.setOnClickListener(this);
        HoraAmMer.setOnClickListener(this);
        HoraPmMier.setOnClickListener(this);
        HoraAmJ.setOnClickListener(this);
        HoraPmJ.setOnClickListener(this);
        HoraAmV.setOnClickListener(this);
        HoraPmV.setOnClickListener(this);
        HoraAmS.setOnClickListener(this);
        HoraPmS.setOnClickListener(this);
        HoraAmD.setOnClickListener(this);
        HoraPmD.setOnClickListener(this);
        ListL.setVisibility(View.INVISIBLE);
        ListM.setVisibility(View.INVISIBLE);
        ListMi.setVisibility(View.INVISIBLE);
        ListJ.setVisibility(View.INVISIBLE);
        ListV.setVisibility(View.INVISIBLE);
        ListS.setVisibility(View.INVISIBLE);
        ListD.setVisibility(View.INVISIBLE);

        mfirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        String id = getIntent().getStringExtra("Horario");

        if (id == null || id == "") {
            System.out.println("Entrada de Actualizacion-- sin dato");

            //*********************Seccion Actividad Normal*****************************
            Lunes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListL.setVisibility(View.VISIBLE);
                        guardarDatos(DiaL);
                    } else {
                        ListL.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Martes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListM.setVisibility(View.VISIBLE);
                        guardarDatos(DiaM);
                    } else {
                        ListM.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Miercules.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListMi.setVisibility(View.VISIBLE);
                        guardarDatos(DiaMi);
                    } else {
                        ListMi.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Jueves.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListJ.setVisibility(View.VISIBLE);
                        guardarDatos(DiaJ);
                    } else {
                        ListJ.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Viernes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListV.setVisibility(View.VISIBLE);
                        guardarDatos(DiaV);
                    } else {
                        ListV.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Sabado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListS.setVisibility(View.VISIBLE);
                        guardarDatos(DiaS);
                    } else {
                        ListS.setVisibility(View.INVISIBLE);
                    }
                }
            });
            Domingo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        ListD.setVisibility(View.VISIBLE);
                        guardarDatos(DiaD);
                    } else {
                        ListD.setVisibility(View.INVISIBLE);
                    }
                }
            });

        } else {

            Guardar.setVisibility(View.INVISIBLE);
            Manual.setVisibility(View.INVISIBLE);

            System.out.println("Revisando traslado de id Horario " + id);
            ObtenerDatos(HoraAmL, HoraPmL, DiaL, Lunes,ListL);
            ObtenerDatos(HoraAmM, HoraPmM, DiaM, Martes,ListM);
            ObtenerDatos(HoraAmMer, HoraPmMier, DiaMi, Miercules,ListMi);
            ObtenerDatos(HoraAmJ, HoraPmJ, DiaJ, Jueves,ListJ);
            ObtenerDatos(HoraAmV, HoraPmV, DiaV, Viernes,ListV);
            ObtenerDatos(HoraAmS, HoraPmS, DiaS, Sabado,ListS);
            ObtenerDatos(HoraAmD, HoraPmD, DiaD, Domingo,ListD);

        }

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(B3_Horario.this, C1_Menu_Neg.class));
            }
        });
    }

  /*  public void AbrirManualH(View view){
        //finish();
        Intent intent = new Intent(this, Ayuda.class);
        intent.putExtra("Ayuda","Horario");
        startActivity(intent);
    }*/

    @Override
    public void onClick(View v) {
        if(v==HoraAmL){
            HAM(HoraAmL,HoraPmL,DiaL);
        }
        if(v==HoraPmL){
            HPM(HoraAmL,HoraPmL,DiaL);
        }
        if(v==HoraAmM){
            HAM(HoraAmM,HoraPmM,DiaM);
        }
        if(v==HoraPmM){
            HPM(HoraAmM,HoraPmM,DiaM);
        }
        if(v==HoraAmMer){
            HAM(HoraAmMer,HoraPmMier,DiaMi);
        }
        if(v==HoraPmMier){
            HPM(HoraAmMer,HoraPmMier,DiaMi);
        }
        if(v==HoraAmJ){
            HAM(HoraAmJ,HoraPmJ,DiaJ);
        }
        if(v==HoraPmJ){
            HPM(HoraAmJ,HoraPmJ,DiaJ);
        }
        if(v==HoraAmV){
            HAM(HoraAmV,HoraPmV,DiaV);
        }
        if(v==HoraPmV){
            HPM(HoraAmV,HoraPmV,DiaV);
        }
        if(v==HoraAmS){
            HAM(HoraAmS,HoraPmS,DiaS);
        }
        if(v==HoraPmS){
            HPM(HoraAmS,HoraPmS,DiaS);
        }
        if(v==HoraAmD){
            HAM(HoraAmD,HoraPmD,DiaD);
        }
        if(v==HoraPmD){
            HPM(HoraAmD,HoraPmD,DiaD);
        }
    }

    public void guardarDatos(String Dia) {
        System.out.println("Guardando datos Dia: " + Dia);
        DocumentReference id = mfirestore.collection("Negocios/" + idUser + "/Horario").document(Dia); //id_local se guarda con el mismo id_user
        Map<String, Object> map = new HashMap<>();
        map.put("id", id.getId());
        map.put("idUser", idUser);
        map.put("HoraInicio", "0.0");
        map.put("HoraFinal", "0.0");
        map.put("Dia", Dia);

        DatosFirestoreBD.GuardarDatos(B3_Horario.this,"Negocios/" + idUser + "/Horario",Dia,map,"", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
               System.out.println("Horario: " +resultado);
            }
        });
    }

    private void ActualizarHorarioI(String HoraI, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("HoraInicio", HoraI);
        //map.put("HoraFinal", HoraF);

        DatosFirestoreBD.ActualizarDatos(B3_Horario.this,"Negocios/" + idUser + "/Horario",id,map,"","", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
                System.out.println("Horario: " +resultado);
            }
        });
    }

    private void ActualizarHorarioF(String HoraF, String id) {
        Map<String, Object> map = new HashMap<>();
        //  map.put("HoraInicio", HoraI);
        map.put("HoraFinal", HoraF);
        Toast.makeText(this, "Minutos " + HoraF, Toast.LENGTH_SHORT).show();

        DatosFirestoreBD.ActualizarDatos(B3_Horario.this,"Negocios/" + idUser + "/Horario",id,map,"","", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
                System.out.println("Horario: " +resultado);
            }
        });
    }

    private void ObtenerDatos(TextView AM,TextView PM,String id,Switch Dia,LinearLayout Lista) {
        System.out.println("Buscando Datos");
        mfirestore.collection("Negocios/"+idUser+"/Horario").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("Datos Encontrados");

                if (documentSnapshot.getString("HoraInicio") != null) {
                    String horaAM = documentSnapshot.getString("HoraInicio");
                    assert horaAM != null;
                    //horaAM = horaAM.replace(".",":");

                    //Separar horas y minutos
                    String[] partes = horaAM.split("\\.");
                    int hour = Integer.parseInt(partes[0]);
                    int minute = Integer.parseInt(partes[1]);
                    String amPm = (hour >= 12) ? "pm" : "am";                                   //Saber si es AM o PM
                    int hour12 = (hour == 0 || hour == 12) ? 12 : hour % 12;                   //Convertir formato a 12 hrs
                    String minFormat = (minute <10 ) ? "0" + minute : String.valueOf(minute); //Formato min con 2 digitos
                    String horaFormatiada = hour12 + ":" + minFormat + " " + amPm;           //Formato completo
                    AM.setText(horaFormatiada);
                }
                if (documentSnapshot.getString("HoraFinal") != null){
                    String horaPM = documentSnapshot.getString("HoraFinal");
                    assert horaPM != null;
                    //horaPM = horaPM.replace(".",":");
                    //Separar horas y minutos
                    String[] partes2 = horaPM.split("\\.");
                    int hour = Integer.parseInt(partes2[0]);
                    int minute = Integer.parseInt(partes2[1]);
                    String amPm = (hour >= 12) ? "pm" : "am";                                   //Saber si es AM o PM
                    int hour12 = (hour == 0 || hour == 12) ? 12 : hour % 12;                   //Convertir formato a 12 hrs
                    String minFormat = (minute <10 ) ? "0" + minute : String.valueOf(minute); //Formato min con 2 digitos
                    String horaFormatiada = hour12 + ":" + minFormat + " " + amPm;           //Formato completo
                    PM.setText(horaFormatiada);
                }
                if(id.equals(documentSnapshot.getString("id"))){
                    Dia.setChecked(true);
                    Lista.setVisibility(View.VISIBLE);
                    DesactivarLista(Dia,Lista,AM,PM,id);
                }else {
                    Dia.setChecked(false);
                    activarLisActualizar(Dia,Lista,id);
                    AM.setText("00:00");
                    PM.setText("00:00");
                }
                System.out.println("Poniendo Datos Horario");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void HAM(TextView HoraAM,TextView HoraPM,String id){
        int Horas, Minutos;
        final Calendar c= Calendar.getInstance();

        Horas=c.get(Calendar.HOUR);
        Minutos=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(B3_Horario.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = minute + "";
                if (min.length()==1){
                    min="0"+min;
                }

                String amPm = (hourOfDay >= 12) ? "pm" : "am"; //Mostra am o pm al texto
                int hourFormatted = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                HoraAM.setText(hourFormatted + ":" + min + " " + amPm);

                String HoraInicio = hourOfDay + "." + min;
                ActualizarHorarioI(HoraInicio,id);
            }
        }, Horas, Minutos, false);
        timePickerDialog.show();
    }

    public void HPM(TextView HoraAM,TextView HoraPM,String id){
        int Horas, Minutos;
        final Calendar c= Calendar.getInstance();

        Horas = c.get(Calendar.HOUR_OF_DAY);
        if (Horas < 12) { Horas += 12;}    //Forzar a que sea PM
        Minutos=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(B3_Horario.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = minute + "";
                if (min.length()==1){
                    min="0"+min;
                }

                String amPm = (hourOfDay >= 12) ? "PM" : "AM"; //Mostra am o pm al texto
                int hourFormatted = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                HoraPM.setText(hourFormatted + ":" + min + " " + amPm);

                String HoraFinal = hourOfDay + "." + min; // obtener la hora y minuto con el formato original para guardar BD
                ActualizarHorarioF(HoraFinal,id);
            }
        }, Horas, Minutos, false);
        timePickerDialog.show();
    }

    public void activarLisActualizar(Switch Dia, LinearLayout Lista,String id){
        Dia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Lista.setVisibility(View.VISIBLE);
                    guardarDatos(id);
                }else {
                    Lista.setVisibility(View.INVISIBLE);

                    DatosFirestoreBD.EliminarDocument(B3_Horario.this,"Negocios/" + idUser + "/Horario",id,"", new DatosFirestoreBD.GuardarCallback() {
                        @Override
                        public void onResultado(String resultado) {
                            System.out.println("Horario: " +resultado);
                        }
                    });
                }
            }
        });

    }

    public void DesactivarLista(Switch Dia,LinearLayout Lista,TextView AM,TextView PM,String id){
        Dia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Lista.setVisibility(View.VISIBLE);
                    AM.setText("00:00");
                    PM.setText("00:00");
                    guardarDatos(id);
                } else {
                    Lista.setVisibility(View.INVISIBLE);

                    DatosFirestoreBD.EliminarDocument(B3_Horario.this,"Negocios/" + idUser + "/Horario",id,"", new DatosFirestoreBD.GuardarCallback() {
                        @Override
                        public void onResultado(String resultado) {
                            System.out.println("Horario: " +resultado);
                        }
                    });
                }
            }
        });
    }


}