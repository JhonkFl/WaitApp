package com.softjk.waitapp.Metodos;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReciclerVacio {
    private final FirebaseFirestore BD;
    private final PreferencesManager preferencesManager;

    public ReciclerVacio(FirebaseFirestore BD, PreferencesManager preferencesManager) {
        this.BD = BD;
        this.preferencesManager = preferencesManager;
    }

    public interface Callback {
        void onResult(String result);
    }

    public void verificarRecycler(String Coleccion, RecyclerView.Adapter<?> mAdapter, String idNegocio, String N, Activity activity, Callback callback) {

        if (mAdapter.getItemCount() == 0) {
            BD.collection(Coleccion).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {

                        preferencesManager.saveString("ListaSala"+N,"Vacio");
                        Map<String, Object> map = new HashMap<>();
                        map.put("Tiempo", 0);

                        DatosFirestoreBD.ActualizarDatos(activity,"Negocios/"+idNegocio+"/TiempoGlobal","Sala"+N,map,"No", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                            }
                        });
                        System.out.println("RecyclerView está vacio y Actualizamos Tiempo Global a 0 seg -- Sala"+N);
                        callback.onResult("Vacio");
                    } else {
                        preferencesManager.saveString("ListaSala"+N,"NoVacio");
                        System.out.println("Primer Elemnto en la lista");
                        callback.onResult("1Elemt");
                    }
                } else {
                    callback.onResult("Error al obtener los datos: " + task.getException());
                }
            });
        } else {
            preferencesManager.saveString("ListaSala"+N,"NoVacio");
            callback.onResult("NoVacio");
        }
    }


    // llamar el Metodo
  /*  ReciclerVacio verRecycler = new ReciclerVacio(BD);
        verRecycler.verificarRecycler(MiColleccionBD, MiAdaptador,resultado -> {
        System.out.println(resultado);
    });                                                                                         */

}
