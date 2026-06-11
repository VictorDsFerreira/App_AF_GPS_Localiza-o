package com.provadb.af_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.provadb.af_mobile.R;
import com.provadb.af_mobile.model.LugarSalvo;

import java.util.ArrayList;
import java.util.List;

public class LugarSalvoAdapter extends RecyclerView.Adapter<LugarSalvoAdapter.ViewHolder> {

    public interface Listener {
        void onClick(LugarSalvo lugar);
        void onLongClick(LugarSalvo lugar);
    }

    private List<LugarSalvo> itens = new ArrayList<>();
    private Listener listener;

    public LugarSalvoAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setLista(List<LugarSalvo> lugares) {
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
                .inflate(R.layout.item_saved_place, pai, false);
        return new ViewHolder(visao);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        LugarSalvo lugar = itens.get(pos);

        holder.txtNome.setText(lugar.getNome());
        holder.txtCategoria.setText("Categoria: " + lugar.getClassif());
        holder.txtCoordenadas.setText("Coordenadas: " + lugar.getCoords());
        holder.txtObservacao.setText("Obs: " + lugar.getObs());

        holder.itemView.setOnClickListener(v -> listener.onClick(lugar));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(lugar);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNome;
        TextView txtCategoria;
        TextView txtCoordenadas;
        TextView txtObservacao;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.txtNomeSalvo);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaSalva);
            txtCoordenadas = itemView.findViewById(R.id.txtCoordenadasSalvas);
            txtObservacao = itemView.findViewById(R.id.txtObservacaoSalva);
        }
    }
}
