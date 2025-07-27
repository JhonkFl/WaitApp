package com.softjk.waitapp.Sistema.Metodos;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TiempoAlarma {

    private static Handler handler;
    private static Runnable countdownRunnable;
    private static ListenerRegistration firestoreListener;
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Tiempo Total users perosnal
    public static void getTiempoGlobalPersonal(String Collecction, String Document, TextView lblTiempo, Context context , String N, TextView lblmsgTiemp, ViewGroup viewGroup,String PrimerUser,String Codigo){
        DocumentReference id = BD.collection(Collecction).document(Document);
        PreferencesManager preferenceSala = new PreferencesManager(context,Codigo);
        System.out.println("Ruta BD Tiempo: "+ Collecction + " ---> Ducument: "+Document);

        handler = new Handler();
        final boolean[] alarmaCancelada = {false};
        final boolean[] isCountdownRunning = {true};
        final long[] lastStartTime = {0};
        final long[] lastDurationMillis = {0};
        final Runnable[] countdownRunnable = {null}; // asegúrate de que esté accesible globalmente

        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long startTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long durationSeconds = snapshot.getLong("Tiempo") * 1000;

                if (startTime != lastStartTime[0] || durationSeconds != lastDurationMillis[0]) {
                    lastStartTime[0] = startTime;
                    lastDurationMillis[0] = durationSeconds;

                    if (countdownRunnable[0] != null) {
                        handler.removeCallbacks(countdownRunnable[0]);
                        handler.removeCallbacksAndMessages(null);
                    }
                    //para la Alarma
                    long tiempoFinal = startTime + durationSeconds;
                    long tiempoAlarma = tiempoFinal - (10 * 60 * 1000); // Restar 10 minutos en milisegundos

                    if (PrimerUser.equals("No") || PrimerUser.isEmpty()) {
                        //solo para pruebas
                        Date fechaFinal = new Date(startTime + durationSeconds);
                        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String alarmaFinalizado = formato.format(fechaFinal);

                        Date fechaAlarma = new Date(tiempoAlarma); //10 minutos antes
                        SimpleDateFormat formato2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String fechaLegible2 = formato2.format(fechaAlarma);
                        System.out.println("La alarma está programada para: " + fechaLegible2);           //*

                        String alarma = preferenceSala.getString("Alarma" + N, "Activar");
                        if (alarma.equals("Activar")) {
                            programarAlarma(context, tiempoAlarma, Integer.parseInt(N)); //Importante
                        }
                    }


                    countdownRunnable[0] = new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = new Date().getTime();
                            long remainingTimeMillis = Math.max(startTime + durationSeconds - currentTime, 0);

                            if (remainingTimeMillis > 0) {
                                long seconds = remainingTimeMillis / 1000;
                                long minutes = seconds / 60;
                                long hours = minutes / 60;

                                seconds %= 60;
                                minutes %= 60;

                                String tiempo;
                                if (hours > 0) {
                                    tiempo = String.format("%02d:%02d:%02d hrs", hours, minutes, seconds);
                                } else if (minutes > 0) {
                                    tiempo = String.format("%02d:%02d min", minutes, seconds);
                                } else {
                                    tiempo = String.format("%02d seg", seconds);
                                }
                                lblTiempo.setText(tiempo);
                                handler.postDelayed(this, 1000);

                                if (!alarmaCancelada[0] && remainingTimeMillis <= 180000) {
                                    preferenceSala.saveString("Alarma" + N, "Cancelado");
                                    cancelarAlarma(context, Integer.parseInt(N));
                                    alarmaCancelada[0] = true;
                                }

                                //Finaliza el Conteo del temporizador
                            } else if (remainingTimeMillis == 0) {
                                if (isCountdownRunning[0]) {
                                    isCountdownRunning[0] = false; // Desactivar Contador por completo para evitar ciclos

                              /*  cancelarAlarma(context, Integer.parseInt(N));
                                alarmaCancelada[0] = true;*/
                                    if (firestoreListener != null) {
                                        firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                        firestoreListener = null;
                                    }

                                    Activity activity = (Activity) context;
                                    if (PrimerUser.equals("No") || PrimerUser.isEmpty()) {
                                        Toast.makeText(activity, "Tiempo Alarma Finalizado", Toast.LENGTH_SHORT).show();
                                        ActuaDatosEnServ(N, lblTiempo, lblmsgTiemp, viewGroup, activity, Codigo);
                                    } else {
                                        AlertDialogMetds.alertOptionGracias(context, viewGroup, N);
                                        LimpiarDatos.LimpiarSala(context, N, Codigo);
                                    }

                                }

                            }
                        }
                    };
                    handler.post(countdownRunnable[0]); // Iniciar el temporizador
                }
            }
        });
    }

    public static void programarAlarma(Context context, long tiempoFinal, int N) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BrockasAlarma.class); //Utilizo el valor de N como codigo Unico para la Alarma
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, N, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //Permiso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            if (!alarmManager.canScheduleExactAlarms()) {
                intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                }
                return;
            }
        }
        // Programar la alarma para la fecha exacta de 'tiempoFinal'
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tiempoFinal, pendingIntent);
        System.out.println("Programando alarma: "+tiempoFinal);
    }

    public static void cancelarAlarma(Context context, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BrockasAlarma.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel(); //  liberar recursos
            System.out.println("Alarma cancelada: " + requestCode);
        } else {
            System.out.println("No se encontró PendingIntent para cancelar.");
        }
    }



    private static void ActuaDatosEnServ(String N,TextView lblTiempo, TextView lblmsgTiemp, ViewGroup viewGroup, Activity activity,String Codigo) {
        Context context = activity;
        PreferencesManager preferenceSala = new PreferencesManager(context,Codigo);
        PreferencesManager preferencesCliente = new PreferencesManager(context,"Cliente");

        String idNegocio = preferencesCliente.getString("idNegocioCliente","");
        String UserFila = preferenceSala.getString("UserFila"+N,"No");
        String esperando1 = preferenceSala.getString("EsperandoUser"+N,"NoEsperando");
        String Colleccion = "Negocios/"+idNegocio+"/Sala"+N;
        String idUser = mAuth.getCurrentUser().getUid();
       // String NS = preferencesManager.getString("NSala","1"); // 1, 2, 3
      //  int TiempoServicio = preferenceSala.getInt("ServClient"+N,0);
       // int TiempoServSeg = TiempoServicio * 60;
        if (UserFila.equals("Si") && esperando1.equals("NoEsperando")) {

            String actualizarDatos = preferenceSala.getString("ActualizarDatos" + N, "Si");
            System.out.println("valor Actualizar " + actualizarDatos);

            if (actualizarDatos.equals("Si")) { //Condicion para que solo se actualice una vez
                Timestamp fechaHoraActual = Timestamp.now();
                Map<String, Object> map2 = new HashMap<>();
                map2.put("InicioServ", fechaHoraActual);
              //  map2.put("TiempoServicio", TiempoServSeg);
                map2.put("Estado", "En Servicio");

                BD.collection(Colleccion).document(idUser).update(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        preferenceSala.saveString("ActualizarDatos" + N, "No");
                        System.out.println(" Actualizacion ...exitosa ");
                       // TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, activity, lblmsgTiemp, viewGroup,Codigo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            } else {
                System.out.println("NO Actualizar solo mostrar TiempoServi");
                //TiempoGlobalPers.getTiempoServPers(Colleccion, idUser, lblTiempo, activity, lblmsgTiemp, viewGroup, Codigo);
            }
        }
    }


}
