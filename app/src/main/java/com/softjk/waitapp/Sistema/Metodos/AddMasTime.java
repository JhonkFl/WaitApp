package com.softjk.waitapp.Sistema.Metodos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddMasTime {

    public interface GuardarCallback {
        void onResultado(String resultado);
    }

    public static void ActualizarTimeUser0(Context context, int valor, String idNeg, String N, String idUser0, int AdminTime, String AccionUser, int TimeServ, String list, GuardarCallback callback) {

        FirebaseFirestore BD = FirebaseFirestore.getInstance();
        BD.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala"+N).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                int Tiempo = Math.toIntExact(documentSnapshot.getLong("Tiempo"));
                String Tim = String.valueOf(Tiempo);
                System.out.println("Ver valor Tiempo "+Tim+" + "+valor);

                if (documentSnapshot.exists()){
                    if (Tim.equals("") || Tim.isEmpty() || Tim == null || list.equals("") || list == null){
                        System.out.println("No Existe el Dato Tiempo");
                        GuadarInicioServ(idNeg);
                    }else {
                        int ConvertSegds = valor * 60;
                        int NuevoTime = Tiempo + ConvertSegds;

                        Map<String, Object> map = new HashMap<>();
                        map.put("Tiempo", NuevoTime);

                        DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNeg + "/TiempoGlobal", "Sala"+N, map, "","", new DatosFirestoreBD.GuardarCallback() {
                            @Override
                            public void onResultado(String resultado) {
                                if ("Actualizado".equals(resultado)) {
                                    int NewTimeUser = AdminTime + ConvertSegds;
                                    System.out.println("Actualizar user0: TiempoActual:"+AdminTime+" -IDClient:"+idUser0);
                                    System.out.println("Negocios/" + idNeg + "/Sala"+N+" -Documt: "+idUser0+" Campo: "+"AdmTiempoTotal:"+NewTimeUser);
                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("AdmTiempoTotal", NewTimeUser);

                                    DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNeg + "/Sala"+N, idUser0, map2, "Se agrego " + valor + " Minutos más","", new DatosFirestoreBD.GuardarCallback() {
                                        @Override
                                        public void onResultado(String resultado) {
                                            if (resultado.equals("Actualizado")){
                                                callback.onResultado("Exito");
                                               /* if (AccionUser == null ||AccionUser.equals("En Servicio")){
                                                    System.out.println("________________-----------> User en servicio Actualizar valor");
                                                   // int NewTime = TimeServ + (valor * 60);
                                                  //  ActualizarUserEnServi(NewTime,idUser0,idNeg,context,N);
                                                }*/

                                                if (context instanceof Activity) {
                                                  /*  Activity activity = (Activity) context;
                                                    Intent intent = activity.getIntent();
                                                    activity.overridePendingTransition(0, 0);
                                                    activity.finish();
                                                    activity.overridePendingTransition(0, 0);
                                                    activity.startActivity(intent); */
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    // Error
                                    callback.onResultado("Error");
                                }
                            }
                        });


                        System.out.println("Ver new valor Tiempo "+NuevoTime);
                    }
                }else {
                    System.out.println("No Existe el Documento");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static void ActualizarUserEnServi(int NewTimeUser, String idUser0 ,String idNeg ,Context context, String N) {
        Map<String, Object> map2 = new HashMap<>();
        map2.put("TiempoServicio", NewTimeUser);

        DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNeg + "/Sala"+N, idUser0, map2, "","", new DatosFirestoreBD.GuardarCallback() {
            @Override
            public void onResultado(String resultado) {
                if (resultado.equals("Actualizado")){
                    System.out.println("________________-----------> User Actualizado valor"+NewTimeUser);
                }else {
                    System.out.println("________________-----------> Error Actualizado valor "+NewTimeUser);
                }
            }
        });
    }

    private static void GuadarInicioServ(String idNeg) {
        FirebaseFirestore BD = FirebaseFirestore.getInstance();
        Timestamp fechaHoraActual = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("Inicio", fechaHoraActual);

        BD.collection("Negocios/"+idNeg+"/TiempoGlobal").document("Sala1").set(data).addOnSuccessListener(aVoid -> {
            System.out.println("Fecha Hora Guardado en Tiempo Global");
        }).addOnFailureListener(e -> {
            System.out.println("Fecha Hora --- NO --- Guardado en Tiempo Global");
        });

    }

    public static void ActualizarTimeGeneral(String idNeg, String idCliets, int Time, int Valor, String N, Context context,String Acion,int TiempoAdmin){

       if (Acion==null){
           int Time2 = Time + Valor ; //Sumar el tiempo mas 5 o 10 min
           int TimeAdmin2 = TiempoAdmin + Valor ; //Sumar el tiempo mas 5 o 10 min
           Map<String, Object> data = new HashMap<>();
           data.put("Tiempo", Time2);
           data.put("AdmTiempoTotal",TimeAdmin2);

           DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNeg + "/Sala"+N, idCliets, data, "","", new DatosFirestoreBD.GuardarCallback() {
               @Override
               public void onResultado(String resultado) {
                   System.out.println(resultado+" time para: "+idCliets);
               }
           });
       } else if (Acion.equals("Admin")) {
           System.out.println("No es necesario actualizar nada porque el user fue agregado por Admin");
           int TiempoAdmin2 = TiempoAdmin + Valor ; //Sumar el tiempo Admin mas 5 o 10 min
           Map<String, Object> data = new HashMap<>();
           data.put("AdmTiempoTotal", TiempoAdmin2);

           DatosFirestoreBD.ActualizarDatos(context, "Negocios/" + idNeg + "/Sala"+N, idCliets, data, "","", new DatosFirestoreBD.GuardarCallback() {
               @Override
               public void onResultado(String resultado) {
                   System.out.println(resultado+" time para: "+idCliets);
               }
           });
       }


    }
}
