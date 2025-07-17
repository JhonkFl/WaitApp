package com.softjk.waitapp.Sistema.Metodos;

import android.content.Context;

public class LimpiarDatos {

    public static void LimpiarSala(Context context, String N, String Codigo){
        PreferencesManager preferencesManager = new PreferencesManager(context,Codigo);
       // String N = preferencesManager.getString("NSala","1"); // 1, 2, 3

        preferencesManager.saveString("NFila"+N, null);
        preferencesManager.saveString("EsperandoUser"+N, null);
        preferencesManager.saveString("UserFila"+N, null);
        preferencesManager.saveString("ActualizarDatos"+N, null);
        preferencesManager.saveString("Pago"+N, null);
        preferencesManager.saveString("AccionesFila"+N,null);
       // preferencesManager.saveString("FilaSala",null);
        preferencesManager.saveString("UserServicio"+N,null);
        preferencesManager.saveString("SalaVacio"+N,null);
        preferencesManager.saveString("Alarma"+N,null);
        preferencesManager.saveString("NSala",null);

        System.out.println("-----Metodo------> Datos Borrado exitosamente Sala"+N);
    }


    public static void LimpiarEntrar(Context context, String N, String Codigo){
        PreferencesManager preferencesManager = new PreferencesManager(context,Codigo);

        preferencesManager.saveString("NFila"+N, null);
        preferencesManager.saveString("EsperandoUser"+N, null);
        preferencesManager.saveString("UserFila"+N, null);
        preferencesManager.saveString("ActualizarDatos"+N, null);
        preferencesManager.saveString("Pago"+N, null);
        preferencesManager.saveString("AccionesFila"+N,null);
       // preferencesManager.saveString("FilaSala",null);
        preferencesManager.saveString("UserServicio"+N,null);
        preferencesManager.saveString("Alarma"+N,null);
        preferencesManager.saveString("NSala"+N,null);

        System.out.println("-----Metodo------> Datos Borrado exitosamente "+Codigo+" -- Sala"+N);
    }
}
