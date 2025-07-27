package com.softjk.waitapp.Sistema.Metodos;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TiempoGlobalPers {
    //TIEMPO SERVER
    //   private DocumentReference countdownRef;
    private static Handler handler;
    private static Runnable countdownRunnable;
    private static ListenerRegistration firestoreListener;
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String CHANNEL_ID = "canal";
    private static PendingIntent pendingIntent;

    public static void getTiempoTraslado(String Collecction, String Document, TextView lblTiempo, Context context, TextView lblEstado, String Sala, String idNegocio, String Codigo, ViewGroup viewGroup){
        //Tiempo Global Firebase
        System.out.println("ver Ruta Tiempo 10 min: "+ Collecction);
        DocumentReference id = BD.collection(Collecction).document(Document);
        PreferencesManager preferenceSala = new PreferencesManager(context,Codigo);
        handler = new Handler();
        final boolean[] isCountdownRunningTrasl = {true};
        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long startTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long milisegundos = snapshot.getLong("Traslado") * 1000;


                //Comenzar Contador
                if (countdownRunnable != null) {  // Cancelar cualquier temporizador existente
                    handler.removeCallbacks(countdownRunnable);
                }
                countdownRunnable = new Runnable() {
                    @Override
                    public void run() {
                        long currentTime = new Date().getTime();
                        long remainingTimeMillis = Math.max(startTime + milisegundos - currentTime, 0);

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
                        } else {

                            if (isCountdownRunningTrasl[0]) {
                                isCountdownRunningTrasl[0] = false; // Desactivar Contador por completo para evitar ciclos

                                String N = preferenceSala.getString("NSala","1"); // 1, 2, 3
                                String Actualizar = preferenceSala.getString("ActualizarDatos"+N, "Si");
                                String idUser = mAuth.getCurrentUser().getUid();
                                //lblEstado.setText("Usuario en Servicio");
                                //lblTiempo.setTextColor(Color.parseColor("#f5eeee"));

                                if (Actualizar.equals("Si")) {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("Estado", "En Servicio");

                                    DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNegocio + "/" + Sala, idUser, map, "","", new DatosFirestoreBD.GuardarCallback() {
                                        @Override
                                        public void onResultado(String resultado) {
                                        }
                                    });

                                    System.out.println("Tiempo Traslado Finalizado");
                                 //   TiempoTotal.getTiempoGUser("Negocios/" + idNegocio + "/" + Sala, idUser, lblTiempo,  lblEstado,"Si",context,viewGroup,Codigo);
                                    preferenceSala.saveString("ActualizarDatos"+N, "No");
                                } else {
                                  //  TiempoTotal.getTiempoGUser("Negocios/" + idNegocio + "/" + Sala, idUser, lblTiempo, lblEstado,"Si",context,viewGroup,Codigo);
                                }

                                handler.removeCallbacks(this); // Cancela el Runnable
                                if (firestoreListener != null) {
                                    firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                    firestoreListener = null;
                                }
                            }
                        }
                    }
                };
                // Iniciar el temporizador
                handler.post(countdownRunnable);
            }
        });
    }


    public static void getTiempoServPers(String collectioUser, String idUser, TextView lblTiempo, Context context, TextView lblEstado, ViewGroup viewGroup, String Codigo){
        //Tiempo Global Firebase Firestore
        DocumentReference id = BD.collection(collectioUser).document(idUser);
        PreferencesManager preferenceSala = new PreferencesManager(context,Codigo);
        System.out.println("ver Ruta Tiempo Servi Personal: "+ collectioUser + "  Ducument: "+idUser);
       // lblEstado.setText("En Servicio");
        //lblTiempo.setTextColor(Color.parseColor("#f5eeee"));

        handler = new Handler();
        final long[] lastStartTime = {0};
        final long[] lastDurationMillis = {0};
        final boolean[] isCountdownRunningServ = {true};

        if (firestoreListener != null) {
            firestoreListener.remove(); // Detén el listener anterior
        }

        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {

                long startTime = snapshot.getTimestamp("InicioServ").toDate().getTime();
                long durationSeconds = snapshot.getLong("TiempoServicio") * 1000;
                System.out.println("Ver valor Tiempo Global: "+durationSeconds);

                if (startTime != lastStartTime[0] || durationSeconds != lastDurationMillis[0]) {
                    lastStartTime[0] = startTime;
                    lastDurationMillis[0] = durationSeconds;

                    // ComenzarContador Tiempo Global
                    if (countdownRunnable != null) {  // Cancelar cualquier temporizador existente
                        handler.removeCallbacks(countdownRunnable);
                        handler.removeCallbacksAndMessages(null);
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

                            } else if (remainingTimeMillis == 0) { //Finaliza Contador

                                if (isCountdownRunningServ[0]) {
                                    isCountdownRunningServ[0] = false; // Desactivar Contador por completo para evitar ciclos

                                    if (firestoreListener != null) {
                                        firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                        firestoreListener = null;
                                    }

                                    String N = preferenceSala.getString("NSala", "1"); // 1, 2, 3
                                    String UserFila = preferenceSala.getString("UserFila" + N, "No");
                                    String idUser = mAuth.getCurrentUser().getUid();
                                    System.out.println("User Fila TimeServices: " + UserFila);

                                    if (UserFila.equals("No") && startTime != 0) {

                                    } else {
                                        lblTiempo.setText("Finalizado");
                                        lblEstado.setText("Gracias por su Preferencia!!");

                                        String esperando1 = preferenceSala.getString("EsperandoUser" + N, "NoEsperando");
                                        System.out.println("Ver user esperando TimeStarGlobal: " + esperando1);
                                        if (esperando1.equals("NoEsperando") && UserFila.equals("Si")) {

                                            //preferenceSala.saveString("UserFila"+N, null);
                                            AlertDialogMetds.alertOptionGracias(context, viewGroup, N);
                                        }

                                        System.out.println("Limpiando preferences si user No esta esperando pero SI esta en la Fila");
                                        LimpiarDatos.LimpiarSala(context, N, Codigo);

                                    }
                                }
                            }
                        }
                    };
                    handler.post(countdownRunnable); // Iniciar el temporizador
                }
            }
        });
    }



    public static void getTiempoItemPers(String collectioUser, String idUser, TextView lblTiempo,String Foto,String NombreUser, ViewGroup viewGroup, Context context,String N,String Servicio, String TIpPago, String Precio){
        //Tiempo Global Firebase Firestore
        DocumentReference id = BD.collection(collectioUser).document(idUser);
        System.out.println("ver Ruta Tiempo Item Lista Persona: "+ collectioUser + "  Ducument: "+idUser);

        handler = new Handler();
        final long[] lastStartTime = {0};// Variables para guardar el último tiempo
        final long[] lastDurationMillis = {0};
        final boolean[] isRunningCont = {true};

        if (firestoreListener != null) {
            firestoreListener.remove(); // Detén el listener anterior
        }

        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long newStartTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long newDurationMillis = snapshot.getLong("AdmTiempoTotal") * 1000;

                // Solo continuar si hubo algún cambio real
                if (newStartTime != lastStartTime[0] || newDurationMillis != lastDurationMillis[0]) {
                    System.out.println("Cambio detectado en Firestore. Reiniciando temporizador...");

                    // Guardamos los nuevos valores
                    lastStartTime[0] = newStartTime;
                    lastDurationMillis[0] = newDurationMillis;

                    // Detener cualquier temporizador previo
                    if (countdownRunnable != null) {
                        handler.removeCallbacks(countdownRunnable);
                        handler.removeCallbacksAndMessages(null);
                    }

                    // Crear nuevo temporizador con los valores actualizados
                    countdownRunnable = new Runnable() {

                        @Override
                        public void run() {
                            long currentTime = new Date().getTime();
                            long remaining = Math.max(newStartTime + newDurationMillis - currentTime, 0);

                            if (remaining > 0) {
                                long seconds = remaining / 1000;
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

                            } else if (remaining == 0) {
                                if (isRunningCont[0]) {
                                    isRunningCont[0] = false; // Desactivar Contador por completo para evitar ciclos

                                    if (firestoreListener != null) {
                                        firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                        firestoreListener = null;
                                        System.out.println("************* Temporizador finalizado *********");
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("Estado","Finalizado");
                                        DatosFirestoreBD.ActualizarDatos(context, collectioUser, idUser, map, "","", new DatosFirestoreBD.GuardarCallback() {
                                            @Override
                                            public void onResultado(String resultado) {

                                            }
                                        });
                                    }
                                    LimpiarDatos.LimpiarSala(context, N, "Negocio");
                                    System.out.println("Temporizador finalizado...");

                                    long cincoMinutos = 5 * 60 * 1000;
                                    long CicnoMinDespues = newDurationMillis + cincoMinutos;

                                    long Tiempo5MinDespues = (CicnoMinDespues / 1000) / 60; // solo para pruebas en minutos
                                    long HoraFin = (newDurationMillis / 1000) / 60;// solo para pruebas en minutos

                                    Date Hora5MinDesp = new Date(newStartTime + CicnoMinDespues);
                                    System.out.println("Hora a Fimalizar = " + HoraFin + " min");
                                    System.out.println("5 min despues = " + Tiempo5MinDespues + " min");

                                    Date TiempoAFinalizar = new Date(newStartTime + newDurationMillis);
                                    Date HoraActual = new Date();

                                   // AlertDialogMetds.alertOptionMSG(context, viewGroup, collectioUser, idUser, Foto, NombreUser,Servicio, TIpPago,Precio);

                                   /* if ( HoraActual.after(TiempoAFinalizar) && HoraActual.before(Hora5MinDesp) ){
                                        System.out.println("********** Solo mostrar el mensaje "+NombreUser);
                                        AlertDialogMetds.alertOptionMSG(context, viewGroup, collectioUser, idUser, Foto, NombreUser,Servicio, TIpPago,Precio);
                                    }else if (HoraActual.after(Hora5MinDesp)){
                                        System.out.println("********** NO mostrar el mensaje, eliminar y actualizar "+NombreUser);
                                        Activity activity = (Activity) context;
                                        DatosFirestoreBD.ActualizarDatosRegistro( idUser, Servicio, TIpPago, Precio, new DatosFirestoreBD.GuardarCallback() {
                                            @Override
                                            public void onResultado(String resultado) {
                                                if (resultado.equals("Actualizado")){
                                                    DatosFirestoreBD.EliminarDocument(context, collectioUser, idUser, "", new DatosFirestoreBD.GuardarCallback() {
                                                        @Override
                                                        public void onResultado(String resultado) {
                                                            System.out.println("Documento eliminado correctamente.");
                                                            Intent intent = new Intent(context,activity.getClass());
                                                            activity.finish();
                                                            activity.overridePendingTransition(0,0);
                                                            activity.startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } */




                               /* DatosFirestoreBD.EliminarDocument(context, collectioUser, idUser, "", new DatosFirestoreBD.GuardarCallback() {
                                    @Override
                                    public void onResultado(String resultado) {
                                        System.out.println("Documento eliminado correctamente.");
                                    }
                                });*/
                                }
                            }
                        }
                    };

                    // Iniciar nuevo temporizador
                    handler.post(countdownRunnable);
                }
            }else {

            }
        });
    }



    public static void reiniciarTemporizador(String collectioUser, String idUser, TextView lblTiempo, Context context,String N) {
        DocumentReference id = BD.collection(collectioUser).document(idUser);

        handler = new Handler();
        final long[] lastStartTime = {0};
        final long[] lastDurationMillis = {0};
        final boolean[] isCountdownRunning = {true};
        final Runnable[] countdownRunnable = {null}; // asegúrate de que esté accesible globalmente

        firestoreListener = id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long newStartTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long newDurationMillis = snapshot.getLong("AdmTiempoTotal") * 1000;

                if (newStartTime != lastStartTime[0] || newDurationMillis != lastDurationMillis[0]) {
                    System.out.println("Cambio detectado en Firestore. Reiniciando temporizador...");

                    // Guardar nuevos valores
                    lastStartTime[0] = newStartTime;
                    lastDurationMillis[0] = newDurationMillis;

                    // Cancelar temporizador anterior
                    if (countdownRunnable[0] != null) {
                        handler.removeCallbacks(countdownRunnable[0]);
                    }

                    // Crear nuevo temporizador
                    countdownRunnable[0] = new Runnable() {
                        @Override
                        public void run() {
                            long currentTime = new Date().getTime();
                            long remainingMillis = Math.max((newStartTime + newDurationMillis) - currentTime, 0);
                            long remainingMin = remainingMillis / 1000 / 60;

                            // Mostrar resultado solo si hay tiempo restante
                            if (remainingMillis > 0) {
                                long seconds = remainingMillis / 1000;
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
                            } else {
                                if (isCountdownRunning[0]) {
                                    isCountdownRunning[0] = false;
                                }

                                if (firestoreListener != null) {
                                    firestoreListener.remove(); // Elimina el listener de Firestore si ya no es necesario
                                    firestoreListener = null;
                                }

                                System.out.println("Temporizador finalizado");
                                System.out.println("************* Temporizador finalizado *********");
                                Map<String, Object> map = new HashMap<>();
                                map.put("Estado","Finalizado");
                                DatosFirestoreBD.ActualizarDatos(context, collectioUser, idUser, map, "","", new DatosFirestoreBD.GuardarCallback() {
                                    @Override
                                    public void onResultado(String resultado) {
                                        LimpiarDatos.LimpiarSala(context, N, "Negocio");
                                    }
                                });
                            }
                        }
                    };

                    handler.post(countdownRunnable[0]); // inicia el temporizador
                }
            }
        });
    }

}
