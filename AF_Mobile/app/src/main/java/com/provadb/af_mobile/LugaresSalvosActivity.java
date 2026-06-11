package com.provadb.af_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.provadb.af_mobile.adapter.LugarSalvoAdapter;
import com.provadb.af_mobile.firebase.LugarSalvoRepository;
import com.provadb.af_mobile.model.LugarSalvo;

import java.util.List;

public class LugaresSalvosActivity extends AppCompatActivity {

    private LugarSalvoRepository repo;
    private LugarSalvoAdapter adapter;
    private TextView txtVazio;
    private ProgressBar progresso;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        setContentView(R.layout.activity_saved_places);
        setTitle("Lugares salvos");

        repo = new LugarSalvoRepository();
        txtVazio = findViewById(R.id.txtSalvosVazios);
        progresso = findViewById(R.id.progressoSalvos);
        RecyclerView lista = findViewById(R.id.listaSalvos);
        lista.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LugarSalvoAdapter(new LugarSalvoAdapter.Listener() {
            @Override
            public void onClick(LugarSalvo lugar) {
                abrirEditar(lugar);
            }

            @Override
            public void onLongClick(LugarSalvo lugar) {
                confirmarDelete(lugar);
            }
        });
        lista.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void carregar() {
        progresso.setVisibility(View.VISIBLE);
        txtVazio.setVisibility(View.GONE);

        repo.listar(new LugarSalvoRepository.ListenerLista() {
            @Override
            public void onOk(List<LugarSalvo> lugares) {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    adapter.setLista(lugares);
                    txtVazio.setVisibility(lugares.isEmpty() ? View.VISIBLE : View.GONE);
                });
            }

            @Override
            public void onErro(Exception e) {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    Toast.makeText(LugaresSalvosActivity.this,
                            "Erro ao carregar lugares salvos.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void abrirEditar(LugarSalvo lugar) {
        Intent i = new Intent(this, EditarLugarSalvoActivity.class);
        i.putExtra(EditarLugarSalvoActivity.EXTRA_ID, lugar.getId());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_NOME, lugar.getNome());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_TIPO_POI, lugar.getTipo());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_LATITUDE, lugar.getLatitude());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_LONGITUDE, lugar.getLongitude());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_CATEGORIA_USUARIO, lugar.getClassif());
        i.putExtra(EditarLugarSalvoActivity.EXTRA_OBSERVACAO, lugar.getObs());
        startActivity(i);
    }

    private void confirmarDelete(LugarSalvo lugar) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir lugar")
                .setMessage("Deseja excluir \"" + lugar.getNome() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> excluir(lugar))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluir(LugarSalvo lugar) {
        repo.excluir(lugar.getId(), new LugarSalvoRepository.Listener() {
            @Override
            public void onOk() {
                runOnUiThread(() -> {
                    Toast.makeText(LugaresSalvosActivity.this, "Lugar excluído.", Toast.LENGTH_SHORT).show();
                    carregar();
                });
            }

            @Override
            public void onErro(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(LugaresSalvosActivity.this, "Erro ao excluir.", Toast.LENGTH_LONG).show());
            }
        });
    }
}
