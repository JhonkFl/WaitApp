package com.softjk.waitapp.Sistema.Servicios;

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
import com.softjk.waitapp.Cliente.E1_Sala_Client;
import com.softjk.waitapp.Negocio.E1_Sala_Neg;
import com.softjk.waitapp.R;

import java.util.Random;

public class Notificaciones extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.e("token", "mi token es: " + token);

        if (token != null && !token.isEmpty()) {
            SharedPreferences preferences = getSharedPreferences("Token", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("idToken", token);
            editor.apply();
        }
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
        SharedPreferences preferences = getSharedPreferences("TUser", Context.MODE_PRIVATE);
        String Tipo = preferences.getString("TUser","Ninguno");
        String id = "mensaje";
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id);

        NotificationChannel nc = new NotificationChannel(id, "nuevo", NotificationManager.IMPORTANCE_HIGH);
        nc.setShowBadge(true);
        assert nm != null;
        nm.createNotificationChannel(nc);

        if (Tipo.equals("Cliente")){
            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(Titulo)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(Detalle)
                    .setContentIntent(clickNotifClient())
                    .setContentInfo("nuevo");

            Random random = new Random();
            int idNotify = random.nextInt(8000);

            assert nm != null;
            nm.notify(idNotify, builder.build());
        } else if (Tipo.equals("Negocio")) {
            builder.setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(Titulo)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(Detalle)
                    .setContentIntent(clickNotifNego())
                    .setContentInfo("nuevo");

            Random random = new Random();
            int idNotify = random.nextInt(8000);

            assert nm != null;
            nm.notify(idNotify, builder.build());
        }


    }

    public PendingIntent clickNotifNego() {
        Intent nf = new Intent(getApplicationContext(), E1_Sala_Neg.class);
        nf.putExtra("notificacion","Novedades");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //si la actividad esta abierta no abrir de nuevo solo actualizar datos
        return PendingIntent.getActivity(this, 0, nf, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent clickNotifClient() {
        Intent nf = new Intent(getApplicationContext(), E1_Sala_Client.class);
        nf.putExtra("notificacion","Novedades");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); //si la actividad esta abierta no abrir de nuevo solo actualizar datos
        return PendingIntent.getActivity(this, 0, nf, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
