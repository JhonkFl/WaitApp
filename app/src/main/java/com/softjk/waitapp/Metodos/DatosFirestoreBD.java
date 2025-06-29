package com.softjk.waitapp.Metodos;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DatosFirestoreBD {
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();

    public interface GuardarCallback {
        void onResultado(String resultado);
    }

    public static void GuardarDatos(Context context, String Collection, String Document, Map map, String MsgDialog, GuardarCallback callback) {
        DocumentReference id = BD.collection(Collection).document(Document);
        BD.collection(Collection).document(id.getId()).set(map)
                .addOnSuccessListener(unused -> {
                    if (MsgDialog.equals("No") || MsgDialog.equals("")) {
                        System.out.println("Datos Guardados con Éxito sin mostrar Msg");
                    } else {
                        String Msg = MsgDialog + " Guardado con Éxito";
                        MultiMetds.toastCorrecto(context, Msg);
                    }
                    callback.onResultado("Guardado");
                })
                .addOnFailureListener(e -> {
                    String msg = "Error al " + MsgDialog;
                    MultiMetds.toastIncorrect(context, msg);
                    callback.onResultado("Error");
                });
    }

    public static void ActualizarDatos(Context context, String Collection, String Document, Map map, String MsgDialog, GuardarCallback callback) {
        DocumentReference id = BD.collection(Collection).document(Document);
        BD.collection(Collection).document(id.getId()).update(map).addOnSuccessListener(unused -> {
                    if (MsgDialog.equals("No") || MsgDialog.isEmpty()) {
                        System.out.println("Actualizado exitosamente sin mostrar Msg");
                    } else {
                        String Msg = MsgDialog + " Guardado con Éxito";
                        MultiMetds.toastCorrecto(context, Msg);
                    }
                    callback.onResultado("Actualizado");
                })
                .addOnFailureListener(e -> {
                    String msg = "Error al " + MsgDialog;
                    MultiMetds.toastIncorrect(context, msg);
                    callback.onResultado("Error");
                });
    }

    public static void EliminarDocument(Context context, String Collection, String Document, String MsgDialog, GuardarCallback callback) {
        BD.collection(Collection).document(Document).delete().addOnSuccessListener(unused -> {
            if (MsgDialog.equals("No") || MsgDialog.isEmpty()) {
                System.out.println("Eliminado exitosamente sin mostrar Msg");
            } else {
                String Msg = MsgDialog + " Guardado con Éxito";
                MultiMetds.toastCorrecto(context, Msg);
            }
            callback.onResultado("Eliminado");
        }).addOnFailureListener(e -> {
            String msg = "Error al " + MsgDialog;
            MultiMetds.toastIncorrect(context, msg);
            callback.onResultado("Error");
        });
    }
}



// ********************* Llamar Metodo para Usar ******************************

/*          DatosFirestoreBD.FirestoreBD(MiActivity.this,"collection","document", DatosMap, "Mensaje", new DatosFirestoreBD.GuardarCallback() {
                @Override
                public void onResultado(String resultado) {
                    if ("Guardado".equals(resultado)) {
                        // Éxito
                    } else {
                        // Error
                    }
                }
            });                                                                                                                             */