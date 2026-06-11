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

public class EditarLugarSalvoActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id_salvo";
    public static final String EXTRA_NOME = "nome_salvo";
    public static final String EXTRA_TIPO_POI = "tipo_poi_salvo";
    public static final String EXTRA_LATITUDE = "latitude_salva";
    public static final String EXTRA_LONGITUDE = "longitude_salva";
    public static final String EXTRA_CATEGORIA_USUARIO = "categoria_usuario_salva";
    public static final String EXTRA_OBSERVACAO = "observacao_salva";

    private LugarSalvoRepository repo;
    private String id;
    private TextInputEditText campoObs;
    private Spinner spinnerCategoria;
    private ProgressBar progresso;

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        setContentView(R.layout.activity_edit_saved_place);
        setTitle("Editar lugar");

        repo = new LugarSalvoRepository();
        id = getIntent().getStringExtra(EXTRA_ID);

        TextInputEditText campoNome = findViewById(R.id.campoNome);
        TextInputEditText campoTipo = findViewById(R.id.campoTipoPoi);
        TextInputEditText campoCoords = findViewById(R.id.campoCoordenadas);
        campoObs = findViewById(R.id.campoObservacao);
        spinnerCategoria = findViewById(R.id.spinnerCategoriaUsuario);
        progresso = findViewById(R.id.progressoEdicao);
        MaterialButton btnAtualizar = findViewById(R.id.btnAtualizar);

        campoNome.setText(getIntent().getStringExtra(EXTRA_NOME));
        campoTipo.setText(getIntent().getStringExtra(EXTRA_TIPO_POI));
        double lat = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        double lon = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);
        campoCoords.setText(String.format("%.5f, %.5f", lat, lon));
        campoObs.setText(getIntent().getStringExtra(EXTRA_OBSERVACAO));

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_saved_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(spinnerAdapter);

        String classif = getIntent().getStringExtra(EXTRA_CATEGORIA_USUARIO);
        String[] categorias = getResources().getStringArray(R.array.user_saved_categories);
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(classif)) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }

        btnAtualizar.setOnClickListener(v -> atualizar());
    }

    private void atualizar() {
        String obs = "";
        if (campoObs.getText() != null) {
            obs = campoObs.getText().toString().trim();
        }

        if (obs.isEmpty()) {
            campoObs.setError("Informe uma observação.");
            return;
        }

        String classif = spinnerCategoria.getSelectedItem().toString();
        progresso.setVisibility(View.VISIBLE);
        findViewById(R.id.btnAtualizar).setEnabled(false);

        repo.atualizar(id, classif, obs, new LugarSalvoRepository.Listener() {
            @Override
            public void onOk() {
                runOnUiThread(() -> {
                    Toast.makeText(EditarLugarSalvoActivity.this, "Lugar atualizado!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onErro(Exception e) {
                runOnUiThread(() -> {
                    progresso.setVisibility(View.GONE);
                    findViewById(R.id.btnAtualizar).setEnabled(true);
                    Toast.makeText(EditarLugarSalvoActivity.this, "Erro ao atualizar.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
