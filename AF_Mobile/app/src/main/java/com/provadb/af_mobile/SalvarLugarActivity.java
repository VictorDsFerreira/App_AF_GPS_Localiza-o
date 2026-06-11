package com.provadb.af_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.provadb.af_mobile.firebase.LugarSalvoRepository;
import com.provadb.af_mobile.model.LugarSalvo;

public class SalvarLugarActivity extends AppCompatActivity {

    private LugarSalvoRepository repo;
    private TextInputEditText campoNome;
    private TextInputEditText campoTipoPoi;
    private TextInputEditText campoCoordenadas;
    private TextInputEditText campoObservacao;
    private Spinner spinnerCategoria;
    private ProgressBar progresso;

    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        setContentView(R.layout.activity_save_place);
        setTitle("Salvar lugar");

        repo = new LugarSalvoRepository();

        campoNome = findViewById(R.id.campoNome);
        campoTipoPoi = findViewById(R.id.campoTipoPoi);
        campoCoordenadas = findViewById(R.id.campoCoordenadas);
        campoObservacao = findViewById(R.id.campoObservacao);
        spinnerCategoria = findViewById(R.id.spinnerCategoriaUsuario);
        progresso = findViewById(R.id.progressoSalvar);
        MaterialButton btnSalvar = findViewById(R.id.btnSalvar);

        String nome = getIntent().getStringExtra(ResultadosBuscaActivity.EXTRA_NOME_LUGAR);
        String tipo = getIntent().getStringExtra(ResultadosBuscaActivity.EXTRA_TIPO_LUGAR);
        lat = getIntent().getDoubleExtra(ResultadosBuscaActivity.EXTRA_LATITUDE_LUGAR, 0);
        lon = getIntent().getDoubleExtra(ResultadosBuscaActivity.EXTRA_LONGITUDE_LUGAR, 0);

        campoNome.setText(nome);
        campoTipoPoi.setText(tipo);
        campoCoordenadas.setText(String.format("%.5f, %.5f", lat, lon));

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_saved_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(spinnerAdapter);

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void salvar() {
        String obs = "";
        if (campoObservacao.getText() != null) {
            obs = campoObservacao.getText().toString().trim();
        }

        if (obs.isEmpty()) {
            campoObservacao.setError("Informe uma observação.");
            return;
        }

        String nome = campoNome.getText() != null ? campoNome.getText().toString() : "";
        String tipo = campoTipoPoi.getText() != null ? campoTipoPoi.getText().toString() : "";
        String classif = spinnerCategoria.getSelectedItem().toString();

        LugarSalvo lugar = new LugarSalvo(nome, tipo, classif, lat, lon, obs);

        progresso.setVisibility(View.VISIBLE);
        findViewById(R.id.btnSalvar).setEnabled(false);

        repo.salvar(lugar, new LugarSalvoRepository.Listener() {
            @Override
            public void onOk() {
                runOnUiThread(() -> {
                    Toast.makeText(SalvarLugarActivity.this, "Lugar salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onErro(Exception e) {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    findViewById(R.id.btnSalvar).setEnabled(true);
                    Toast.makeText(SalvarLugarActivity.this,
                            "Erro ao salvar. Verifique o Firebase.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
