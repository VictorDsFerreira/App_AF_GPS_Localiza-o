package com.provadb.af_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.provadb.af_mobile.adapter.LugarProximoAdapter;
import com.provadb.af_mobile.api.BuscaLugaresClient;
import com.provadb.af_mobile.model.LugarProximo;

import java.util.List;

public class ResultadosBuscaActivity extends AppCompatActivity {

    public static final String EXTRA_NOME_LUGAR = "nome_lugar";
    public static final String EXTRA_TIPO_LUGAR = "tipo_lugar";
    public static final String EXTRA_LATITUDE_LUGAR = "latitude_lugar";
    public static final String EXTRA_LONGITUDE_LUGAR = "longitude_lugar";

    private BuscaLugaresClient api;
    private LugarProximoAdapter adapter;
    private TextView txtCabecalho;
    private TextView txtVazio;
    private ProgressBar progresso;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        setContentView(R.layout.activity_search_results);
        setTitle("Resultados");

        double lat = getIntent().getDoubleExtra(MainActivity.EXTRA_LATITUDE_USUARIO, 0);
        double lon = getIntent().getDoubleExtra(MainActivity.EXTRA_LONGITUDE_USUARIO, 0);
        int indice = getIntent().getIntExtra(MainActivity.EXTRA_INDICE_CATEGORIA, 0);

        txtCabecalho = findViewById(R.id.txtCabecalhoResultados);
        txtVazio = findViewById(R.id.txtResultadosVazios);
        progresso = findViewById(R.id.progressoResultados);
        RecyclerView lista = findViewById(R.id.listaResultados);
        lista.setLayoutManager(new LinearLayoutManager(this));

        api = new BuscaLugaresClient(this);
        adapter = new LugarProximoAdapter(this::abrirSalvar);
        lista.setAdapter(adapter);

        buscar(indice, lat, lon);
    }

    private void buscar(int indice, double lat, double lon) {
        progresso.setVisibility(View.VISIBLE);
        txtVazio.setVisibility(View.GONE);

        api.buscar(indice, lat, lon, new BuscaLugaresClient.Listener() {
            @Override
            public void onOk(List<LugarProximo> lugares) {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    adapter.setLista(lugares);
                    txtCabecalho.setText(lugares.size() + " lugar(es) encontrado(s)");
                    txtVazio.setVisibility(lugares.isEmpty() ? View.VISIBLE : View.GONE);
                });
            }

            @Override
            public void onErro() {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    Toast.makeText(ResultadosBuscaActivity.this,
                            "Erro ao buscar lugares. Tente novamente.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void abrirSalvar(LugarProximo lugar) {
        Intent i = new Intent(this, SalvarLugarActivity.class);
        i.putExtra(EXTRA_NOME_LUGAR, lugar.getNome());
        i.putExtra(EXTRA_TIPO_LUGAR, lugar.getCategoria());
        i.putExtra(EXTRA_LATITUDE_LUGAR, lugar.getLatitude());
        i.putExtra(EXTRA_LONGITUDE_LUGAR, lugar.getLongitude());
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (api != null) {
            api.encerrar();
        }
    }
}
