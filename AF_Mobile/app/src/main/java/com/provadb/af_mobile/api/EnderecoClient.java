package com.provadb.af_mobile.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EnderecoClient {

    private static final String URL_REVERSA =
            "https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s";
    private static final String AGENTE_USUARIO = "AF_Mobile/1.0 (Android; projeto academico)";

    private final OkHttpClient clienteHttp = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Listener {
        void onOk(String endereco);
        void onErro();
    }

    public void buscar(double lat, double lon, Listener listener) {
        String url = String.format(URL_REVERSA, lat, lon);
        executor.execute(() -> {
            Request requisicao = new Request.Builder()
                    .url(url)
                    .header("User-Agent", AGENTE_USUARIO)
                    .get()
                    .build();
            try (Response resposta = clienteHttp.newCall(requisicao).execute()) {
                if (!resposta.isSuccessful() || resposta.body() == null) {
                    listener.onErro();
                    return;
                }
                String corpo = resposta.body().string();
                JsonObject json = JsonParser.parseString(corpo).getAsJsonObject();
                String endereco = json.has("display_name")
                        ? json.get("display_name").getAsString()
                        : null;
                if (endereco != null && !endereco.isEmpty()) {
                    listener.onOk(endereco);
                } else {
                    listener.onErro();
                }
            } catch (IOException | IllegalArgumentException e) {
                listener.onErro();
            }
        });
    }

    public void encerrar() {
        executor.shutdown();
    }
}
