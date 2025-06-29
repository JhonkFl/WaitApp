package com.softjk.waitapp.Metodos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ActualizarIMG {
    private Activity activity;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mfirestore;
    private StorageReference storageReference;
    private String storage_path;
    private String photo;
    private static final int COD_SEL_IMAGE = 100;

    public ActualizarIMG(Activity activity, ProgressDialog progressDialog, FirebaseAuth mAuth, FirebaseFirestore mfirestore,
                         StorageReference storageReference, String CarpetaStorage, String NamePhoto) {
        this.activity = activity;
        this.progressDialog = progressDialog;
        this.mAuth = mAuth;
        this.mfirestore = mfirestore;
        this.storageReference = storageReference;
        this.storage_path = CarpetaStorage;
        this.photo = NamePhoto;
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data, ImageView imageView, String Collection, String Document, FotoUploadCallback callback) {
        if (resultCode == Activity.RESULT_OK && requestCode == COD_SEL_IMAGE && data != null && data.getData() != null) {
            Uri imageUrl = data.getData();
            subirFoto(imageUrl, imageView, Collection, Document, callback);
        }
    }

    public interface FotoUploadCallback {
        void onUploadSuccess(String mensaje);
        void onUploadError(String mensaje);
    }

    public void subirFoto(Uri imageUrl, ImageView imageView, String Collection, String Document, FotoUploadCallback callback) {
        progressDialog.setMessage("Actualizando foto");
        progressDialog.show();
        String rute_storage_photo = storage_path + photo + mAuth.getUid();
        StorageReference reference = storageReference.child(rute_storage_photo);

        reference.putFile(imageUrl).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String download_uri = uri.toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(photo, download_uri);
                        mfirestore.collection(Collection).document(Document).update(map);
                        Toast.makeText(activity, "Foto actualizada", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        imageView.setImageURI(imageUrl);

                        // Notificar éxito
                        callback.onUploadSuccess("IMG-Actualizado");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(activity, "Error al obtener la URL de descarga", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        // Notificar fallo de obtención de URL
                        callback.onUploadError("IMG-ErrorURL");
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(activity, "Error al cargar la foto", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            // Notificar fallo de subida
            callback.onUploadError("IMG-Error");
        });

    }

    public int getCOD_SEL_IMAGE() {
        return COD_SEL_IMAGE;
    }

}
