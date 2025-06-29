package com.softjk.waitapp.Adapter.Negc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.softjk.waitapp.FragSala.Client.SalaC1;
import com.softjk.waitapp.FragSala.Client.SalaC2;
import com.softjk.waitapp.FragSala.Client.SalaC3;
import com.softjk.waitapp.FragSala.Negc.SalaN1;
import com.softjk.waitapp.FragSala.Negc.SalaN2;
import com.softjk.waitapp.FragSala.Negc.SalaN3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Opc2AdpTabLayoutNeg extends FragmentStateAdapter {

    private List<Fragment> fragments = new ArrayList<>(Arrays.asList(
            new SalaN1(),
            new SalaN2(),
            new SalaN3()
    ));

    public Opc2AdpTabLayoutNeg(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void ocultarFragment(int position) {
        if (position >= 0 && position < fragments.size()) {
            fragments.remove(position); // Elimina el fragmento correspondiente
            notifyDataSetChanged(); // Notifica al Adapter que los datos han cambiado
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
