package com.provadb.af_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.provadb.af_mobile.R;
import com.provadb.af_mobile.model.LugarProximo;
import com.provadb.af_mobile.util.DistanciaUtil;

import java.util.ArrayList;
import java.util.List;

public class LugarProximoAdapter extends RecyclerView.Adapter<LugarProximoAdapter.ViewHolder> {

    public interface Listener {
        void onClick(LugarProximo lugar);
    }

    private List<LugarProximo> itens = new ArrayList<>();
    private Listener listener;

    public LugarProximoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setLista(List<LugarProximo> lugares) {
        itens.clear();
        if (lugares != null) {
            itens.addAll(lugares);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int tipo) {
        View visao = LayoutInflater.from(pai.getContext())
                .inflate(R.layout.item_nearby_place, pai, false);
        return new ViewHolder(visao);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        LugarProximo lugar = itens.get(pos);

        holder.txtNome.setText(lugar.getNome());
        holder.txtCategoria.setText(lugar.getCategoria());
        holder.txtDistancia.setText("Distância: " + DistanciaUtil.formatar(lugar.getDistancia()));
        holder.txtCoordenadas.setText(String.format("%.5f, %.5f", lugar.getLatitude(), lugar.getLongitude()));

        holder.itemView.setOnClickListener(v -> listener.onClick(lugar));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        TextView txtCategoria;
        TextView txtDistancia;
        TextView txtCoordenadas;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeLugar);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaLugar);
            txtDistancia = itemView.findViewById(R.id.txtDistanciaLugar);
            txtCoordenadas = itemView.findViewById(R.id.txtCoordenadasLugar);
        }
    }
}
