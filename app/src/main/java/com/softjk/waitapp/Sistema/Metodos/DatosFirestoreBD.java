package com.softjk.waitapp.Sistema.Metodos;

import static com.softjk.waitapp.Sistema.Metodos.MultiMetds.IMG;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.softjk.waitapp.Negocio.B2_RegistNeg;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DatosFirestoreBD {
    static FirebaseFirestore BD = FirebaseFirestore.getInstance();

    public interface GuardarCallback {
        void onResultado(String resultado);
    }

    public interface GuardarDateCallback {
        void onResultado(Date resultado, Long Time);
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

    public static void ActualizarDatos(Context context, String Collection, String Document, Map map, String MsgDialog,String Origen, GuardarCallback callback) {
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
            if (Origen.equals("Vacio")){
                System.out.println("No mostrar error --- RecyclerVacio");
            }else {
                String msg = "Error al " + MsgDialog;
                MultiMetds.toastIncorrect(context, msg);
                callback.onResultado("Error");
            }
        });
    }

    public static void EliminarDocument(Context context, String Collection, String Document, String MsgDialog, GuardarCallback callback) {
        BD.collection(Collection).document(Document).delete().addOnSuccessListener(unused -> {
            if (MsgDialog.equals("No") || MsgDialog.isEmpty()) {
                System.out.println("Eliminado exitosamente sin mostrar Msg");
            } else {
                String Msg = MsgDialog + " con Éxito";
                MultiMetds.toastCorrecto(context, Msg);
            }
            callback.onResultado("Eliminado");
        }).addOnFailureListener(e -> {
            String msg = "Error al Eliminar" + MsgDialog;
            MultiMetds.toastIncorrect(context, msg);
            callback.onResultado("Error");
        });
    }


    public static void ObtenerTiempoInicio( String collection, String document, GuardarDateCallback callback) {
        BD.collection(collection).document(document).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Timestamp timestamp = documentSnapshot.getTimestamp("Inicio");
                Long TimeGlobal = documentSnapshot.getLong("Tiempo");
                if (timestamp != null) {
                    Date fechaInicio = timestamp.toDate();

                    callback.onResultado(fechaInicio, TimeGlobal);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


                                                                                                      //-------------------------- SECCION PRESONALIZADO -----------------------------------

    public static void ActualizarDatosRegistro( String idUser, String TipoServ,String TipoPago, String Precio ,GuardarCallback callback) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat FormatFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fecha = FormatFecha.format(calendar.getTime());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Negocios/"+idUser+"/Registro").document(fecha);

        Date fechaActual = new Date();
        Map<String, Object> mapFecha = new HashMap<>();
        mapFecha.put("Fecha",fechaActual);
        reference.set(mapFecha, SetOptions.merge());

        // Campo: Efectivo o Tarjeta --- Datos: Precio = NVeces
        String CampoPago = TipoPago+"."+Precio;
        reference.update(CampoPago, FieldValue.increment(1));

        String CampoServ = "Servicio."+TipoServ;
        reference.update(CampoServ, FieldValue.increment(1));

        callback.onResultado("Actualizado");
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