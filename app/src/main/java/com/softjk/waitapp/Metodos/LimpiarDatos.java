package com.softjk.waitapp.Metodos;

import android.content.Context;

public class LimpiarDatos {

    public static void LimpiarSala(Context context, String N){
        PreferencesManager preferencesManager = new PreferencesManager(context);
       // String N = preferencesManager.getString("NSala","1"); // 1, 2, 3

        preferencesManager.saveString("NFila"+N, null);
        preferencesManager.saveString("EsperandoUser"+N, null);
        preferencesManager.saveString("UserFila"+N, null);
        preferencesManager.saveString("ActualizarDatos"+N, null);
        preferencesManager.saveString("Pago"+N, null);
        preferencesManager.saveString("AccionesFila"+N,null);
        preferencesManager.saveString("FilaSala",null);
        preferencesManager.saveString("UserServicio"+N,null);
        preferencesManager.saveString("ListaSala"+N,null);
        preferencesManager.saveString("Alarma"+N,null);

        System.out.println("Datos Borrado exitosamente");
    }


    public static void LimpiarEntrar(Context context, String N){
        PreferencesManager preferencesManager = new PreferencesManager(context);
        // String N = preferencesManager.getString("NSala","1"); // 1, 2, 3

        preferencesManager.saveString("NFila"+N, null);
        preferencesManager.saveString("EsperandoUser"+N, null);
        preferencesManager.saveString("UserFila"+N, null);
        preferencesManager.saveString("ActualizarDatos"+N, null);
        preferencesManager.saveString("Pago"+N, null);
        preferencesManager.saveString("AccionesFila"+N,null);
        preferencesManager.saveString("FilaSala",null);
        preferencesManager.saveString("UserServicio"+N,null);
        preferencesManager.saveString("Alarma"+N,null);

        System.out.println("Datos Borrado exitosamente");
    }
}
