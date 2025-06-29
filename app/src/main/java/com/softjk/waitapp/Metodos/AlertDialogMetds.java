package com.softjk.waitapp.Metodos;
import static com.softjk.waitapp.FragSala.Client.SalaC1.viewGroup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.softjk.waitapp.E1_Sala_Client;
import com.softjk.waitapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AlertDialogMetds {

    public static void MsgOpenActiv(Context context, String Tituto, String Mensaje,  Class<?> newActivitySi, Class<?> newActivityNo, String Dato, String Valor) {
        Activity activity = (Activity) context;
        new SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE).setTitleText(Tituto)
            .setContentText(Mensaje)
            .setCancelText("No").setConfirmText("Si")
            .showCancelButton(true).setCancelClickListener(sDialog -> {
                sDialog.dismissWithAnimation();
                 //activity.startActivity(new Intent(activity, newActivity));
            if (Dato.equals("No") && Valor.equals("NO")){
                System.out.println("No abrir Actividad");
            }else {
                Intent intent = new Intent(activity, newActivityNo);
                intent.putExtra(Dato, Valor);
                activity.startActivity(intent);
                activity.finish();
            }
            }).setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismissWithAnimation();
                Intent intent = new Intent(activity, newActivitySi);
                intent.putExtra(Dato,Valor);
                activity.startActivity(intent);
            }).show();
    }

    public static void MsgSaveSiNo(Context context, String Titulo, String Mensaje,String Dato) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE).setTitleText(Titulo)
                .setContentText(Mensaje)
                .setCancelText("No").setConfirmText("Si")
                .showCancelButton(true).setCancelClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    preferencesManager.saveString(Dato, "NO");
                }).setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    preferencesManager.saveString(Dato, "SI");
                }).show();
    }



    public static void alertOptionGracias(Context context, ViewGroup viewGroup) {
        Activity activity = (Activity) context;
        Button btnok;
        ImageView nan;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.z_custom_dialog_gracias, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view1);

        nan = view1.findViewById(R.id.nan);

        btnok = view1.findViewById(R.id.btnokGracias);
        final AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            alertDialog.show();
        }
    }


    public static void alertOptionPago(Context context, ViewGroup viewGroup,String Collection, String Document) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Activity activity = (Activity) context;
        Button Efectivo, Tarjeta;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.z_custom_dialog_pagar, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view1);

        Efectivo = view1.findViewById(R.id.btnEfectivo);
        Tarjeta = view1.findViewById(R.id.btnTarjeta);
        final AlertDialog alertDialogs = builder.create();

        alertDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Permisos.Notificaciones(context,activity);

        Efectivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("Pago","Efectivo");
                Map<String, Object> map = new HashMap<>();
                map.put("Pago", "Efectivo");
                System.out.println("Collection para Pago Efectivo: "+Collection+" Doc: "+Document);

                DatosFirestoreBD.ActualizarDatos(context,Collection,Document,map,"No", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                    }
                });

                alertDialogs.setOnDismissListener(dialog -> {
                    Intent i = new Intent(activity, E1_Sala_Client.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(i);
                    activity.finish();
                });

                alertDialogs.cancel();
            }
        });

        Tarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesManager.saveString("Pago","Tarjeta");
                Map<String, Object> map = new HashMap<>();
                map.put("Pago", "Tarjeta");
                System.out.println("Collection para Pago Tarjeta: "+Collection+" Doc: "+Document);

                DatosFirestoreBD.ActualizarDatos(context,Collection,Document,map,"No", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                    }
                });

                alertDialogs.setOnDismissListener(dialog -> {
                    Intent i = new Intent(activity, E1_Sala_Client.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(i);
                    activity.finish();
                });

                alertDialogs.cancel();
            }
        });
        if (!activity.isFinishing()) {// Acción que solo se debe realizar si la actividad no está terminando
            alertDialogs.show();
        }
    }



    public static void alertOptionNegCerca(Context context, ViewGroup viewGroup,String idNegocio, String idUser,String NSala) {
        PreferencesManager preferencesManager = new PreferencesManager(context);
        Activity activity = (Activity) context;
        Button Si, No;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view1 = LayoutInflater.from(context).inflate(R.layout.z_custom_dialog_preg, viewGroup, false);
        builder.setCancelable(false);
        builder.setView(view1);

        Si = view1.findViewById(R.id.btnSiEstoy);
        No = view1.findViewById(R.id.btnNoEstoy);
        final AlertDialog alertDialog = builder.create();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Si.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int servPrimerCli = preferencesManager.getInt("ServPrimerClient"+NSala,0);
                Log.d("AletDialogMetds", "El Usuario esta en el Negocio");
                preferencesManager.saveString("EsperandoUser"+NSala,"NoEsperando");
                preferencesManager.saveString("AccionesFila"+NSala,"Si");
                preferencesManager.saveString("UserServicio"+NSala, "Si");

                int ServTim = (servPrimerCli * 60);
                Map<String, Object> map3 = new HashMap<>();
                map3.put("Tiempo", ServTim);
                map3.put("Estado", "En Servicio");

                DatosFirestoreBD.ActualizarDatos(activity,"Negocios/"+idNegocio+"/Sala"+NSala,idUser,map3,"", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                    }
                });

                AlertDialogMetds.alertOptionPago(activity,viewGroup,"Negocios/"+idNegocio+"/Sala"+NSala,idUser);
                alertDialog.dismiss();

             /*   Log.d("AdapterSalaCliente", "Actualizando actividad");
                Intent i = new Intent(activity, E1_Sala_Client.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                activity.finish(); */
            }
        });

        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int servPrimerCli = preferencesManager.getInt("ServPrimerClient"+NSala,0);
                Log.d("AdapterSalaCliente", "Esperar 10 min user TimeGlobal + TimeServ:"+servPrimerCli+" min");

                int TiempoTraslado = 600;  // 600 = 10 min
                int Suma = (servPrimerCli * 60) + 600;
                Map<String, Object> map = new HashMap<>();
                map.put("Tiempo", Suma);

                DatosFirestoreBD.ActualizarDatos(activity,"Negocios/"+idNegocio+"/TiempoGlobal","Sala"+NSala,map,"", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                    }
                });

                //Actualizar tiempo Cliente a esperar
                Map<String, Object> map2 = new HashMap<>();
                map2.put("Traslado", TiempoTraslado);
                map2.put("Tiempo", Suma);
                map2.put("AdmTiempoTotal", Suma);
                map2.put("Estado", "Trasladandose");

                DatosFirestoreBD.ActualizarDatos(activity,"Negocios/"+idNegocio+"/Sala"+NSala,idUser,map2,"", new DatosFirestoreBD.GuardarCallback() {
                    @Override
                    public void onResultado(String resultado) {
                    }
                });
                preferencesManager.saveString("EsperandoUser"+NSala,"Esperando");
                preferencesManager.saveString("AccionesFila"+NSala,"Si");

                AlertDialogMetds.alertOptionPago(activity,viewGroup,"Negocios/"+idNegocio+"/Sala"+NSala,idUser);
                alertDialog.dismiss();

               /* Log.d("AdapterSalaCliente", "Actualizando actividad");
                Intent i = new Intent(activity, E1_Sala_Client.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(i);
                activity.finish(); */
            }
        });
        if (!activity.isFinishing()) {// Acción que solo se debe realizar si la actividad no está terminando
            alertDialog.show();
        }
    }


}
