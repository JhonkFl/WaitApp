package com.softjk.waitapp.Metodos;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.softjk.waitapp.R;

public class MultiMetds {

    public static void OrientaConfi(Context context, ImageView ImgFont){
        Configuration config = context.getResources().getConfiguration();
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ImgFont.setTranslationY(-300);
            System.out.println("Modo Horizontal");
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Modo Vertical
            System.out.println("Modo Vertical");
        }
    }

    public static void toastCorrecto(Context context, String msg) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.z_custom_toast_ok, null);
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast1);
        txtMensaje.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void toastIncorrect(Context context, String msg) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.z_custom_toast_error, null);
        TextView txtMensaje = view.findViewById(R.id.txtMensajeToast2);
        txtMensaje.setText(msg);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void IMG(Context context ,String valorIMG, ImageView Img,String Circular){
        try {
            if (!valorIMG.equals("")) {
                if (Circular.equals("Si")){
                    Glide.with(context)
                            .load(valorIMG)
                            .circleCrop()
                            .into(Img);
                    System.out.println("Foto Agregado");

                } else if (Circular.equals("No") || Circular.equals("")) {
                    Glide.with(context)
                            .load(valorIMG)
                            .into(Img);
                    System.out.println("Foto Agregado");
                }

            }
        } catch (Exception e) {
            Log.v("Error", "e: " + e);
        }
    }




    // Notificaciones Local
    public static class NotificationHelper {
        private static final String CHANNEL_ID = "GeneralChannel";
        private static final String CHANNEL_NAME = "General Notifications";

        public NotificationHelper(Context context) {
            createNotificationChannel(context);
        }

        private void createNotificationChannel(Context context) {
            System.out.println("Preparar Notificacion");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "Canal",
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                System.out.println("Notificacion SDK");
                NotificationManager manager = context.getSystemService(NotificationManager.class);
                if (manager != null) {
                    System.out.println("Creando Notificacion");
                    manager.createNotificationChannel(channel);
                }
            }
        }

        public void showNotification(Context context, String title, String message) {
            System.out.println("Mostrando Notificacion "+context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Canal")
                    .setSmallIcon(R.drawable.casa) // Icono de tu aplicación
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(1, builder.build());
                System.out.println("Notificacion Ok");
            }else {
                Log.e("NotificationHelper", "NotificationManager es nulo.");
            }

        }
    }

    public static void DynamicColumnsRecicler(Context context, RecyclerView recyclerView, int WidthMin) {
        // Obtener el ancho de la pantalla
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        // Calcular el número de columnas
        int numOfColumns = Math.max(2, (int) (screenWidthDp / WidthMin)); // Mínimo de 2 columnas
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numOfColumns);

        // Aplicar el LayoutManager al RecyclerView
        recyclerView.setLayoutManager(gridLayoutManager);
    }




}
