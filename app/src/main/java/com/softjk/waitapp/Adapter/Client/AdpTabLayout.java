package com.softjk.waitapp.Adapter.Client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.softjk.waitapp.E1_Sala_Client;
import com.softjk.waitapp.FragSala.Client.SalaC1;
import com.softjk.waitapp.FragSala.Client.SalaC2;
import com.softjk.waitapp.FragSala.Client.SalaC3;
import com.softjk.waitapp.Metodos.PreferencesManager;

public class AdpTabLayout extends FragmentStateAdapter {

    public AdpTabLayout(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
       // PreferencesManager preferencesManager = new PreferencesManager(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
System.out.println("Ver valor fragTab: "+position);
        switch (position){
           case 0: return new SalaC1();
           case 1: return new SalaC2();
           case 2: return new SalaC3();
           default: return new SalaC1();
       }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
