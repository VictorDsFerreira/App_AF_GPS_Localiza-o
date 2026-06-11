package com.provadb.af_mobile;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.provadb.af_mobile.api.EnderecoClient;
import com.provadb.af_mobile.location.LocalizacaoHelper;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_LATITUDE_USUARIO = "latitude_usuario";
    public static final String EXTRA_LONGITUDE_USUARIO = "longitude_usuario";
    public static final String EXTRA_INDICE_CATEGORIA = "indice_categoria";

    private LocalizacaoHelper gps;
    private EnderecoClient endereco;

    private TextView txtCoordenadas;
    private TextView txtEndereco;
    private Spinner spinnerCategoria;
    private Button btnObterLocalizacao;
    private Button btnBuscar;
    private Button btnLugaresSalvos;
    private ProgressBar progressoPrincipal;

    private double lat;
    private double lon;
    private boolean temGps = false;
    private boolean buscandoGps = false;

    private final ActivityResultLauncher<String[]> launcherPermissao =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), resultados -> {
                Boolean ok = resultados.get(Manifest.permission.ACCESS_FINE_LOCATION);
                if (Boolean.TRUE.equals(ok)) {
                    obterGps();
                } else {
                    Toast.makeText(this,
                            "Permissão de localização negada. Ative nas configurações do aparelho.",
                            Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle estadoSalvo) {
        super.onCreate(estadoSalvo);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.principal), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        gps = new LocalizacaoHelper(this);
        endereco = new EnderecoClient();

        txtCoordenadas = findViewById(R.id.txtCoordenadas);
        txtEndereco = findViewById(R.id.txtEndereco);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        btnObterLocalizacao = findViewById(R.id.btnObterLocalizacao);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnLugaresSalvos = findViewById(R.id.btnLugaresSalvos);
        progressoPrincipal = findViewById(R.id.progressoPrincipal);

        btnObterLocalizacao.setOnClickListener(v -> pedirGps());
        btnBuscar.setOnClickListener(v -> abrirBusca());
        btnLugaresSalvos.setOnClickListener(v ->
                startActivity(new Intent(this, LugaresSalvosActivity.class)));

        if (estadoSalvo == null && gps.temPermissao()) {
            obterGps();
        }
    }

    private void pedirGps() {
        if (!gps.temPermissao()) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setMessage("Precisamos da sua localização para buscar lugares próximos.")
                        .setPositiveButton("OK", (d, w) -> pedirPermissao())
                        .show();
            } else {
                pedirPermissao();
            }
            return;
        }
        obterGps();
    }

    private void pedirPermissao() {
        launcherPermissao.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void obterGps() {
        if (buscandoGps) {
            return;
        }
        buscandoGps = true;
        mostrarLoad(true);
        txtEndereco.setText("Buscando endereço…");

        gps.obterLocal(new LocalizacaoHelper.Listener() {
            @Override
            public void onOk(double latitude, double longitude) {
                runUi(() -> {
                    buscandoGps = false;
                    lat = latitude;
                    lon = longitude;
                    temGps = true;
                    btnBuscar.setEnabled(true);
                    txtCoordenadas.setText("Coordenadas: " + String.format("%.5f, %.5f", latitude, longitude));
                    mostrarLoad(false);
                    buscarEndereco(latitude, longitude);
                });
            }

            @Override
            public void onErro(String msg) {
                runUi(() -> {
                    buscandoGps = false;
                    mostrarLoad(false);
                    btnBuscar.setEnabled(false);
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void buscarEndereco(double latitude, double longitude) {
        endereco.buscar(latitude, longitude, new EnderecoClient.Listener() {
            @Override
            public void onOk(String texto) {
                runUi(() -> txtEndereco.setText("Endereço aproximado: " + texto));
            }

            @Override
            public void onErro() {
                runUi(() -> txtEndereco.setText("Endereço indisponível"));
            }
        });
    }

    private void runUi(Runnable acao) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        runOnUiThread(acao);
    }

    private void abrirBusca() {
        if (!temGps) {
            Toast.makeText(this, "Não foi possível obter a localização. Verifique o GPS.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, ResultadosBuscaActivity.class);
        i.putExtra(EXTRA_LATITUDE_USUARIO, lat);
        i.putExtra(EXTRA_LONGITUDE_USUARIO, lon);
        i.putExtra(EXTRA_INDICE_CATEGORIA, spinnerCategoria.getSelectedItemPosition());
        startActivity(i);
    }

    private void mostrarLoad(boolean ativo) {
        progressoPrincipal.setVisibility(ativo ? View.VISIBLE : View.GONE);
        btnObterLocalizacao.setEnabled(!ativo);
    }

    @Override
    protected void onDestroy() {
        if (gps != null) {
            gps.encerrar();
        }
        if (endereco != null) {
            endereco.encerrar();
        }
        super.onDestroy();
    }
}
