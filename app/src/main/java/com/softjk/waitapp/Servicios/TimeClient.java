package com.softjk.waitapp.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softjk.waitapp.Metodos.MultiMetds;
import com.softjk.waitapp.Metodos.PreferencesManager;
import com.softjk.waitapp.R;

import java.util.Date;

public class TimeClient extends Service {
    private Handler handler;
    private Runnable countdownRunnable;
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();
    public static final String PAQETE = "com.softjk.waitapp";
    Intent bi = new Intent(PAQETE);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Recuperar datos desde el Intent
        String collection = intent.getStringExtra("Collecction");
        String document = intent.getStringExtra("Document");
        Context context = this;

        // Inicializar lógica aquí
        startGlobalTimer(collection, document, context);
        return START_STICKY; // Hace que el servicio se mantenga activo
    }

    private void startGlobalTimer(String collection, String document, Context context) {
        DocumentReference id = BD.collection(collection).document(document);

        handler = new Handler();
        id.addSnapshotListener((snapshot, e) -> {
            if (snapshot != null && snapshot.exists()) {
                long startTime = snapshot.getTimestamp("Inicio").toDate().getTime();
                long durationSeconds = snapshot.getLong("Tiempo") * 1000;

                // Iniciar temporizador
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

                            String Tiempo = String.format("%02d", hours) + ":" +
                                    String.format("%02d", minutes) + ":" +
                                    String.format("%02d", seconds);


                            //Alarma en 5 minutos
                            if (remainingTimeMillis <= 300000 && remainingTimeMillis >= 299000){
                                System.out.println("Alarma Activando");
                                MediaPlayer alarma = MediaPlayer.create(context, R.raw.alarma_voz);
                                alarma.start();

                                MultiMetds.NotificationHelper notificationHelper = new MultiMetds.NotificationHelper(TimeClient.this);
                                notificationHelper.showNotification(TimeClient.this,"waitapp","Hora de Trasladarse al negocio");

                                alarma.setOnCompletionListener(mp -> {
                                    mp.release();
                                });
                            }

                            bi.putExtra("Tiempo", Tiempo);
                            sendBroadcast(bi);

                            handler.postDelayed(this, 1000);
                        } else {
                            handler.removeCallbacks(this); // Eliminar el Runnable para evitar más ejecuciones

                            if (!bi.hasExtra("Fin")) {// Asegurarnos de que solo se envíe el Intent una vez
                                bi.putExtra("Fin", "En Servicio");
                                sendBroadcast(bi);
                            }
                            stopSelf(); // Detiene completamente el servicio
                        }
                    }
                };
                handler.post(countdownRunnable);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && countdownRunnable != null) {
            handler.removeCallbacks(countdownRunnable); // Detener el temporizador
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
