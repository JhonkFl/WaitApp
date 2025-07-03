package com.softjk.waitapp.Cliente.FragmentMenuC;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.softjk.waitapp.R;

public class MenuAyuda extends Fragment {

    WebView Horario;
    WebView NuevaCuenta;
    TextView btnRegistrUser, btnAgregarNgc,btnHacerfila,btnRegistNeg,btnSelecHora,btnCompartCDG,btnMasClienteFila, btnEditarFecha,btnAgreServ,btnImagenServ,btnEdutServ,btnEditLocal;
    LinearLayout lineaNegocio,lineaCliente,lineaNegocio2;
    String videoRegiUser ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/sYU2K_kf9rE?si=EmIcpSRb5cgkVWs2\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
    String videoRegiHorario ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/QZCIwsucsMg?si=y9O_EXCtdnUHnUp8\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_menu_ayuda, container, false);

        NuevaCuenta = view.findViewById(R.id.vdNuevaCuenta);
        WebView AgregarNeg = view.findViewById(R.id.vdAgregarNeg);
        WebView HacerFila = view.findViewById(R.id.vdHacerfila);
        WebView RegistrNgc = view.findViewById(R.id.vdRegNegocio);
        Horario = view.findViewById(R.id.vdHorario);
        WebView Codi = view.findViewById(R.id.vdCodig);
        WebView ClienteSalaNg = view.findViewById(R.id.vdMasCliFila);
        WebView EditHora = view.findViewById(R.id.vdEditHora);
        WebView AgreServ = view.findViewById(R.id.vdAgregarServ);
        WebView ImgSer = view.findViewById(R.id.vdFotoServ);
        WebView EditServ = view.findViewById(R.id.vdEditServ);
        WebView EditLoc = view.findViewById(R.id.vdEditLocal);

        btnRegistrUser = view.findViewById(R.id.btnManualRegiUser);
        btnAgregarNgc = view.findViewById(R.id.btnAgregarNogocio);
        btnHacerfila = view.findViewById(R.id.btnHacerfila);
        btnRegistNeg = view.findViewById(R.id.btnManualRegNeg);
        btnSelecHora = view.findViewById(R.id.btnManualSelecHorar);
        btnCompartCDG = view.findViewById(R.id.btnManualCompartirCdg);
        btnMasClienteFila = view.findViewById(R.id.btnManualMasClienteFiLA);
        btnEditarFecha = view.findViewById(R.id.btnManualEditHorario);
        btnAgreServ = view.findViewById(R.id.btnManualAgrServ);
        btnImagenServ = view.findViewById(R.id.btnManualFotServ);
        btnEdutServ = view.findViewById(R.id.btnManualEditServ);
        btnEditLocal = view.findViewById(R.id.btnManualEditLocal);

        lineaCliente = view.findViewById(R.id.linerCliente);
        lineaNegocio = view.findViewById(R.id.linerNegocio);
        lineaNegocio2 = view.findViewById(R.id.linerSecc2);


        //String videoRegiUser ="";
        String videoHacerfila = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/1X-TA56VuLw?si=IlrNa40ArPQ6nJ-A\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        String videoAgregarNGC = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/YjsrPc3Jyyc?si=ou5kRrikSP-DpCmN\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        String videoRegiNeg ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/1DQm0iMQg4E?si=iIchzBwRYMVD8tH1\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        //String videoRegiHorario ="";
        String videoCodigo ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/oO6GnpOz4Go?si=a-djjUWHZzFHCWP7\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        String videoRegiClientSalaNgc ="";
        String videoEditHorar ="";
        String videoRegiServ ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/vOC6bevIn3Y?si=qODFMOLldIh0-uXb\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        String videoAddIMGServ ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/jKPkHkLIkHs?si=1sT-cw_6QFYC1v-S\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        String videoEditServ ="";
        String videoEditLocal ="";

        Bundle args = getArguments();
        if (args != null) {
            String Ayuda = args.getString("Ayuda");
            System.out.println(Ayuda);
            ilumunacionLineasColr(Ayuda);
        }

        btnRegistrUser.setOnClickListener(v -> {
            NuevaCuenta.setVisibility(View.VISIBLE);
            NuevaCuenta.loadData(videoRegiUser,"text/html","utf-8");
            NuevaCuenta.getSettings().setJavaScriptEnabled(true);
            NuevaCuenta.setWebChromeClient(new WebChromeClient());
        });
        btnAgregarNgc.setOnClickListener(v -> {
            AgregarNeg.setVisibility(View.VISIBLE);
            AgregarNeg.loadData(videoAgregarNGC,"text/html","utf-8");
            AgregarNeg.getSettings().setJavaScriptEnabled(true);
            AgregarNeg.setWebChromeClient(new WebChromeClient());
        });
        btnHacerfila.setOnClickListener(v -> {
            HacerFila.setVisibility(View.VISIBLE);
            HacerFila.loadData(videoHacerfila,"text/html","utf-8");
            HacerFila.getSettings().setJavaScriptEnabled(true);
            HacerFila.setWebChromeClient(new WebChromeClient());
        });
        btnRegistNeg.setOnClickListener(v -> {
            RegistrNgc.setVisibility(View.VISIBLE);
            RegistrNgc.loadData(videoRegiNeg,"text/html","utf-8");
            RegistrNgc.getSettings().setJavaScriptEnabled(true);
            RegistrNgc.setWebChromeClient(new WebChromeClient());
        });
        btnSelecHora.setOnClickListener(v -> {
            Horario.setVisibility(View.VISIBLE);
            Horario.loadData(videoRegiHorario,"text/html","utf-8");
            Horario.getSettings().setJavaScriptEnabled(true);
            Horario.setWebChromeClient(new WebChromeClient());
        });
        btnCompartCDG.setOnClickListener(v -> {
            Codi.setVisibility(View.VISIBLE);
            Codi.loadData(videoCodigo,"text/html","utf-8");
            Codi.getSettings().setJavaScriptEnabled(true);
            Codi.setWebChromeClient(new WebChromeClient());
        });
        btnMasClienteFila.setOnClickListener(v -> {
            ClienteSalaNg.setVisibility(View.VISIBLE);
            ClienteSalaNg.loadData(videoRegiClientSalaNgc,"text/html","utf-8");
            ClienteSalaNg.getSettings().setJavaScriptEnabled(true);
            ClienteSalaNg.setWebChromeClient(new WebChromeClient());
        });
        btnEditarFecha.setOnClickListener(v -> {
            EditHora.setVisibility(View.VISIBLE);
            EditHora.loadData(videoEditHorar,"text/html","utf-8");
            EditHora.getSettings().setJavaScriptEnabled(true);
            EditHora.setWebChromeClient(new WebChromeClient());
        });
        btnAgreServ.setOnClickListener(v -> {
            AgreServ.setVisibility(View.VISIBLE);
            AgreServ.loadData(videoRegiServ,"text/html","utf-8");
            AgreServ.getSettings().setJavaScriptEnabled(true);
            AgreServ.setWebChromeClient(new WebChromeClient());
        });
        btnImagenServ.setOnClickListener(v -> {
            ImgSer.setVisibility(View.VISIBLE);
            ImgSer.loadData(videoAddIMGServ,"text/html","utf-8");
            ImgSer.getSettings().setJavaScriptEnabled(true);
            ImgSer.setWebChromeClient(new WebChromeClient());
        });
        btnEdutServ.setOnClickListener(v -> {
            EditServ.setVisibility(View.VISIBLE);
            EditServ.loadData(videoEditServ,"text/html","utf-8");
            EditServ.getSettings().setJavaScriptEnabled(true);
            EditServ.setWebChromeClient(new WebChromeClient());
        });
        btnEditLocal.setOnClickListener(v -> {
            EditLoc.setVisibility(View.VISIBLE);
            EditLoc.loadData(videoEditLocal,"text/html","utf-8");
            EditLoc.getSettings().setJavaScriptEnabled(true);
            EditLoc.setWebChromeClient(new WebChromeClient());
        });

        return view;
    }

    public void ilumunacionLineasColr(String Ayuda){

        if(Ayuda.equals("Login")){
            lineaNegocio.setVisibility(View.GONE);
            btnRegistrUser.setVisibility(View.VISIBLE);
            System.out.println("Visible Boton de ReUser");
            NuevaCuenta.setVisibility(View.VISIBLE);
            System.out.println("Visible Video de ReUser");
            NuevaCuenta.loadData(videoRegiUser,"text/html","utf-8");
            NuevaCuenta.getSettings().setJavaScriptEnabled(true);
            NuevaCuenta.setWebChromeClient(new WebChromeClient());
            System.out.println("Visible Datos de Video de ReUser");
        }else if(Ayuda.equals("RegistrarNeg")){
            lineaCliente.setVisibility(View.GONE);
            btnRegistNeg.setVisibility(View.VISIBLE);
        }else if(Ayuda.equals("SeleccionarHora")){
            lineaCliente.setVisibility(View.GONE);
            btnSelecHora.setVisibility(View.VISIBLE);
        }else if(Ayuda.equals("Negocio")){
            lineaCliente.setVisibility(View.GONE);
            /*btnCompartCDG.setTextColor(Color.BLUE);
            btnMasClienteFila.setTextColor(Color.BLUE);
            btnEditarFecha.setTextColor(Color.BLUE);
            btnAgreServ.setTextColor(Color.BLUE);
            btnImagenServ.setTextColor(Color.BLUE);
            btnEdutServ.setTextColor(Color.BLUE);
            btnEditLocal.setTextColor(Color.BLUE);*/

        }else if(Ayuda.equals("Cliente")){
            lineaNegocio.setVisibility(View.GONE);
            btnRegistrUser.setVisibility(View.GONE);
            btnAgregarNgc.setVisibility(View.VISIBLE);
            btnHacerfila.setVisibility(View.VISIBLE);
        } else if (Ayuda.equals("Paso6")) {
            lineaCliente.setVisibility(View.GONE);
        } else if (Ayuda.equals("Horario")) {
            Horario.setVisibility(View.VISIBLE);
            Horario.loadData(videoRegiHorario,"text/html","utf-8");
            Horario.getSettings().setJavaScriptEnabled(true);
            Horario.setWebChromeClient(new WebChromeClient());
            lineaCliente.setVisibility(View.GONE);
            lineaNegocio2.setVisibility(View.GONE);
            btnSelecHora.setVisibility(View.VISIBLE);
        } else {
            lineaCliente.setVisibility(View.GONE);
            btnSelecHora.setTextColor(Color.BLUE);
        }

    }
}