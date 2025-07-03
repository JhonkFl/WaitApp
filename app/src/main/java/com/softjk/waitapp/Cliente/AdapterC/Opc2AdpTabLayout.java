package com.softjk.waitapp.Cliente.AdapterC;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC1;
import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC2;
import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Opc2AdpTabLayout extends FragmentStateAdapter {

    private List<Fragment> fragments = new ArrayList<>(Arrays.asList(
            new SalaC1(),
            new SalaC2(),
            new SalaC3()
    ));

    public Opc2AdpTabLayout(@NonNull FragmentActivity fragmentActivity) {
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
