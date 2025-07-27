package com.softjk.waitapp.Sistema.Metodos.Tiempo;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.softjk.waitapp.R;

import java.util.List;

public class RecyclerAdapterNumeros extends RecyclerView.Adapter<RecyclerAdapterNumeros.ViewHolder> {
    private List<Integer> numeros;
    private OnNumeroClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    public interface OnNumeroClickListener {
        void onClick(int numero);
    }

    public RecyclerAdapterNumeros(List<Integer> numeros, OnNumeroClickListener listener) {
        this.numeros = numeros;
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textoNumero;

        public ViewHolder(View itemView) {
            super(itemView);
            textoNumero = itemView.findViewById(R.id.textoNumero);

            itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = getAdapterPosition();

                notifyItemChanged(previousSelected); // Quita selección anterior
                notifyItemChanged(selectedPosition); // Aplica nueva selección

                listener.onClick(numeros.get(selectedPosition)); // Callback externo si lo usas
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_numero, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int numero = numeros.get(position);
        holder.textoNumero.setText(String.valueOf(numeros.get(position)));

        if (position == selectedPosition) {
            holder.textoNumero.setBackgroundTintList( ColorStateList.valueOf(Color.parseColor("#ADD8E6")));
            holder.textoNumero.setTextColor(Color.BLUE);
            //holder.textoNumero.setScaleX(1.2f);
           // holder.textoNumero.setScaleY(1.2f);
        } else {
            holder.textoNumero.setBackgroundTintList( ColorStateList.valueOf(Color.parseColor("#ffffff")));
            holder.textoNumero.setTextColor(Color.parseColor("#FF5722")); // Color base
            //holder.textoNumero.setScaleX(1f);
            //holder.textoNumero.setScaleY(1f);
        }


    }

    @Override
    public int getItemCount() {
        return numeros.size();
    }

}
