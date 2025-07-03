package com.softjk.waitapp.Cliente.FragmentMenuC;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;
import com.softjk.waitapp.Cliente.AdapterC.AdpNegVinculado;
import com.softjk.waitapp.Cliente.D1_BuscarNeg;
import com.softjk.waitapp.Sistema.Metodos.ActualizarIMG;
import com.softjk.waitapp.Sistema.Metodos.MultiMetds;
import com.softjk.waitapp.Sistema.Modelo.Negocio;
import com.softjk.waitapp.R;

public class MenuInicio extends Fragment {
    AdpNegVinculado mAdapter;
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private String idUser;
    static TextView NombreCliente, CorreoClient;
    SearchView Codg;
    Button Buscar;
    ImageView logo;
   // RecyclerView listaLocal;
   CarouselRecyclerview ListaNeg;
    Query query;
    static String DataUserToken, DataEmailToken;
    private static final String CHANNEL_ID = "canal";
    private PendingIntent pendingIntent;

    private ActivityResultLauncher<Intent> seleccionarFotoLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_inicio_client, container, false);

        Buscar = view.findViewById(R.id.btnBuscarMasNG);
        NombreCliente = view.findViewById(R.id.lblUserNombre);
        CorreoClient = view.findViewById(R.id.lblUserCorreo);
        Codg = view.findViewById(R.id.txtBuscarCodigo);
        logo = view.findViewById(R.id.imgMenuCl);
       // listaLocal = view.findViewById(R.id.listaLocal);
        ListaNeg = view.findViewById(R.id.listaLocal);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();

        ObtenerDatos(idUser);
        setUpRecyclerView();

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), D1_BuscarNeg.class);
                startActivity(intent);
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                seleccionarFotoLauncher.launch(i);
            }
        });

        search_view();
        /* Efecto RecyclerView
        listaLocal.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                float center = listaLocal.getWidth() / 2f;
                for (int i = 0; i<listaLocal.getChildCount(); i++){
                    View child = listaLocal.getChildAt(i);
                    float childCenter = (child.getLeft() + child.getRight()) / 2f;
                    float scale = 1 - Math.abs(center - childCenter) / center;
                    child.setScaleX(0.9f + 0.1f * scale);
                    child.setScaleY(0.9f + 0.1f * scale);
                }
            }
        }); */


        // Subir IMG a Firebase
        seleccionarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        Uri imageUrl = result.getData().getData();

                        ActualizarIMG uploader = new ActualizarIMG(requireActivity(), progressDialog, mAuth, mFirestore,
                                storageReference, "User/", "Perfil");
                        System.out.println("Uri: "+imageUrl);
                        uploader.subirFoto(imageUrl, logo, "User", mAuth.getUid(), new ActualizarIMG.FotoUploadCallback() {
                            @Override
                            public void onUploadSuccess(String mensaje) {
                                System.out.println(mensaje);
                            }

                            @Override
                            public void onUploadError(String mensaje) {
                                System.out.println(mensaje);
                            }
                        });
                    }
                }
        );





        return view;
    }



    private void setUpRecyclerView() {


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        ListaNeg.setLayoutManager(layoutManager);
        //listaLocal.setLayoutManager(new GridLayoutManager(getActivity(),2));
        query = mFirestore.collection("User/"+idUser+"/MisNegocios");
        FirestoreRecyclerOptions<Negocio> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Negocio>().setQuery(query, Negocio.class).build();
        mAdapter = new AdpNegVinculado(firestoreRecyclerOptions,getActivity());
        ListaNeg.setAdapter(mAdapter);

        /* Usa un SnapHelper para agregar comportamiento tipo carrusel
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(ListaNeg); */
        mAdapter.startListening();

        //Funbciones CarouselRecycler
        ListaNeg.setAlpha(true);
        ListaNeg.setIsScrollingEnabled(true);

        /*Otras Funciones
        ListaNeg.set3DItem(true);
        ListaNeg.setInfinite(true);
        ListaNeg.setFlat(true);
        ListaNeg.setOrientation(); */

    }

    private void search_view() {
        Codg.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                textSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                textSearch(s);
                return false;
            }
        });
    }

    public void textSearch(String s) {
        FirestoreRecyclerOptions<Negocio> firestoreRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Negocio>()
                        .setQuery(query.orderBy("NombreLocal")
                                .startAt(s).endAt(s + "\uf8ff"), Negocio.class).build();
        mAdapter = new AdpNegVinculado(firestoreRecyclerOptions, getActivity());
        mAdapter.startListening();
        ListaNeg.setAdapter(mAdapter);
    }

    private void ObtenerDatos(String idUser) {
        mFirestore.collection("User").document(idUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                System.out.println("Datos del Usuario Encontrados");

                String Nombre = documentSnapshot.getString("User");
                String Correo = documentSnapshot.getString("Email");
                String ImgUser = documentSnapshot.getString("Perfil");
                NombreCliente.setText(Nombre);
                CorreoClient.setText(Correo);

                if (ImgUser != null){MultiMetds.IMG(getActivity(),ImgUser,logo,"Si");}
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error al obtener los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}