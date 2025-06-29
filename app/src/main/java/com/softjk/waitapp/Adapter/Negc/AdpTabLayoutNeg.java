package com.softjk.waitapp.Adapter.Negc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.softjk.waitapp.FragSala.Negc.SalaN1;
import com.softjk.waitapp.FragSala.Client.SalaC2;
import com.softjk.waitapp.FragSala.Client.SalaC3;
import com.softjk.waitapp.FragSala.Negc.SalaN2;

public class AdpTabLayoutNeg extends FragmentStateAdapter {
    public AdpTabLayoutNeg(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       switch (position){
           case 0: return new SalaN1();
           case 1: return new SalaN2();
           case 2: return new SalaC3();
           default: return new SalaN1();
       }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
