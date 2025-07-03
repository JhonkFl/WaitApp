package com.softjk.waitapp.Sistema.Metodos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.softjk.waitapp.R;

import java.util.Random;

public class Notificaciones extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e("token", "mi token es: " + token);

        SharedPreferences preferences = getSharedPreferences("Token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idToken", token.toString());
        editor.commit();

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        String from = message.getFrom();

        if (message.getData().size() > 0) {
            String Titulo = message.getData().get("titulo");
            String Detalle = message.getData().get("detalle");

            mayorqueoreo(Titulo, Detalle);
        }
    }

    private void mayorqueoreo(String Titulo, String Detalle) {

        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
        nc.setShowBadge(true);
        assert nm != null;
        nm.createNotificationChannel(nc);

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(Titulo)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(Detalle)
         //       .setContentIntent(clickNotif())
                .setContentInfo("nuevo");

        Random random = new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify, builder.build());
    }

  /*  public PendingIntent clickNotif() {
        Intent nf = new Intent(getApplicationContext(), Novedades.class);
        //nf.putExtra("Color","Rojo");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //si la actividad esta abierta no abrir de nuevo solo actualizar datos
        return PendingIntent.getActivity(this, 0, nf, 0);
    } */

}
