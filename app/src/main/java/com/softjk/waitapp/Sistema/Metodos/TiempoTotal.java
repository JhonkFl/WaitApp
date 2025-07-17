package com.softjk.waitapp.Sistema.Metodos;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

public class TiempoTotal {

    StorageReference storageReference;
    private static boolean Accion;
    //TIEMPO SERVER
    private static Handler handler;
    private static Runnable countdownRunnable;
    private static ListenerRegistration firestoreListener;
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public static void getTiempoGlobal(String idNegocio, String N, TextView lblTiempo, TextView lblmsgTiemp, String adminServ){
        //Tiempo Global Firebase Firestore
        DocumentReference id = BD.collection("Negocios/" + idNegocio + "/TiempoGlobal").document("Sala"+N);

        handler = new Handler();
        final boolean[] isCountdownRunning = {true};
        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {

                if (snapshot.contains("Inicio") && snapshot.getTimestamp("Inicio") != null) {
                    long startTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                    long durationSeconds = snapshot.getLong("Tiempo") * 1000;

                System.out.println("Ver valor Tiempo Global: "+durationSeconds);
               // ComenzarContador Tiempo Global
                if (countdownRunnable != null) {  // Cancelar cualquier temporizador existente
                    handler.removeCallbacks(countdownRunnable);
                }
                countdownRunnable = new Runnable() {
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

                            StringBuilder tiempo = new StringBuilder();
                            String tiempoFormateado;
                            if (hours > 0) {
                                tiempo.append(String.format("%02d:%02d:%02d hrs", hours, minutes, seconds));
                            } else if (minutes > 0) {
                                tiempo.append(String.format("%02d:%02d min", minutes, seconds));
                            } else {
                                tiempo.append(String.format("%02d seg", seconds));
                            }
                            lblTiempo.setText(tiempo.toString());
                            handler.postDelayed(this, 1000);


                            //Finaliza el Conteo del temporizador
                        } else {
                            if (isCountdownRunning[0]) {
                                isCountdownRunning[0] = false; // Desactivar Contador por completo para evitar ciclos

                                if (adminServ.equals("Si")){
                                    lblTiempo.setText("Mis Servicios");
                                }else {
                                    lblTiempo.setText("Disponible");
                                    lblmsgTiemp.setText("No hay Nadie en la Fila");
                                }
                            }

                        }
                    }
                };
                handler.post(countdownRunnable); // Iniciar el temporizador
            }
            } else {
                Log.d("Firestore", "'Inicio' no disponible aún. Esperando actualización...");
            }
        });
    }


    public static void getTiempoGUser(String Collecction, String Document, TextView lblTiempo, TextView lblmsgTiemp, String PrimerUser, Context context, ViewGroup viewGroup,String Codigo){
        //Tiempo Global Firebase Firestore
        DocumentReference id = BD.collection(Collecction).document(Document);
        PreferencesManager preferencesManager = new PreferencesManager(context,"Cliente");
        System.out.println("ver Ruta Tiempo Global: "+ Collecction + " ---> Ducument: "+Document);

        handler = new Handler();
        final boolean[] isCountdownRunning = {true};
        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long startTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long durationSeconds = snapshot.getLong("Tiempo") * 1000;

                System.out.println("Ver valor Tiempo Global: "+durationSeconds);
                // ComenzarContador Tiempo Global
                if (countdownRunnable != null) {  // Cancelar cualquier temporizador existente
                    handler.removeCallbacks(countdownRunnable);
                }
                countdownRunnable = new Runnable() {
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

                            StringBuilder tiempo = new StringBuilder();
                            String tiempoFormateado;
                            if (hours > 0) {
                                tiempo.append(String.format("%02d:%02d:%02d hrs", hours, minutes, seconds));
                            } else if (minutes > 0) {
                                tiempo.append(String.format("%02d:%02d min", minutes, seconds));
                            } else {
                                tiempo.append(String.format("%02d seg", seconds));
                            }

                            lblTiempo.setText(tiempo.toString());
                            handler.postDelayed(this, 1000);

                            //Finaliza el Conteo del temporizador
                        } else if (remainingTimeMillis == 0){
                            if (isCountdownRunning[0]) {
                                handler.removeCallbacks(this); // Cancela el Runnable
                                isCountdownRunning[0] = false; // Desactivar Contador por completo para evitar ciclos

                                if (firestoreListener != null) {
                                    firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                    firestoreListener = null;
                                }

                                if (PrimerUser.equals("Si")){
                                    lblTiempo.setText("00:00");
                                    lblmsgTiemp.setText("Gracias por su preferencia... ");

                                    String N = preferencesManager.getString("NSala","1"); // 1, 2, 3
                                    LimpiarDatos.LimpiarSala(context,N,Codigo);
                                    //  FirestoreMetds.EliminarDocument(context, collectioUser, idUser);
                                    AlertDialogMetds.alertOptionGracias(context,viewGroup,N);
                                }else {
                                    lblTiempo.setText("Disponible");
                                   // lblmsgTiemp.setText("No hay Nadie en la Fila");
                                }
                            }

                        }
                    }
                };
                handler.post(countdownRunnable); // Iniciar el temporizador
            }
        });
    }


}
