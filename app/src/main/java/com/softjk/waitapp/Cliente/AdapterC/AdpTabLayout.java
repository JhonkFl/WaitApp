package com.softjk.waitapp.Cliente.AdapterC;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC1;
import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC2;
import com.softjk.waitapp.Cliente.FragmentSalaC.SalaC3;

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
