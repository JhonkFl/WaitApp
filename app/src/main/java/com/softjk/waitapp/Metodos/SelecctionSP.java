package com.softjk.waitapp.Metodos;

import android.widget.Spinner;

public class SelecctionSP {

    public void PonerPositionSelecc(String valor, Spinner sp) {
        if (valor.equals("5 minutos")){
            sp.setSelection(0);
        } else if (valor.equals("10 minutos")) {
            sp.setSelection(1);
        }else if (valor.equals("15 minutos")) {
            sp.setSelection(2);
        }
    }
}
