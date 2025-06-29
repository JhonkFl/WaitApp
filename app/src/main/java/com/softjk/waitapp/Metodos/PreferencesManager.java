package com.softjk.waitapp.Metodos;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "AppPreferences"; // Nombre del archivo SharedPreferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    // Constructor
    public PreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    // Método para guardar un valor String
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply(); // Guarda los cambios de manera asíncrona
    }

    // Método para obtener un valor String
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Método para guardar un valor entero
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    // Método para obtener un valor entero
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Método para eliminar un valor
    public void removeKey(String key) {
        editor.remove(key);
        editor.apply();
    }

    // Método para limpiar todos los datos
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
