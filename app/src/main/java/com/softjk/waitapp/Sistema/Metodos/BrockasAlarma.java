package com.softjk.waitapp.Sistema.Metodos;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import androidx.core.app.NotificationCompat;

import com.softjk.waitapp.R;

public class BrockasAlarma extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Activando alarma");
        // Reproducir sonido de alarma
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarma_voz);
        mediaPlayer.start();

        // Mostrar notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_alarma")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("¡Alarma!")
                .setContentText("Es hora de ir al Negocio!!!.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }
}
