package com.provadb.af_mobile.api;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.provadb.af_mobile.model.LugarProximo;
import com.provadb.af_mobile.util.CategoriaPoiHelper;
import com.provadb.af_mobile.util.DistanciaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BuscaLugaresClient {

    private static final String AGENTE_USUARIO = "AF_Mobile/1.0 (Android; projeto academico)";
    private static final String[] URLS_OVERPASS = {
            "https://overpass-api.de/api/interpreter",
            "https://lz4.overpass-api.de/api/interpreter",
            "https://z.overpass-api.de/api/interpreter"
    };

    private final Context contexto;
    private final OkHttpClient clienteHttp = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Listener {
        void onOk(List<LugarProximo> lugares);
        void onErro();
    }

    public BuscaLugaresClient(Context contexto) {
        this.contexto = contexto.getApplicationContext();
    }

    public void buscar(int indice, double latUsuario, double lonUsuario, Listener listener) {
        String consulta = CategoriaPoiHelper.montarQuery(contexto, indice, latUsuario, lonUsuario);
        String rotulo = CategoriaPoiHelper.getRotulo(contexto, indice);

        executor.execute(() -> {
            for (String url : URLS_OVERPASS) {
                try {
                    List<LugarProximo> lugares = executar(url, consulta, rotulo, latUsuario, lonUsuario);
                    if (lugares != null) {
                        listener.onOk(lugares);
                        return;
                    }
                } catch (IOException e) {
                }
            }
            listener.onErro();
        });
    }

    private List<LugarProximo> executar(String url, String consulta, String rotulo,
                                        double latUsuario, double lonUsuario) throws IOException {
        Request requisicao = new Request.Builder()
                .url(url)
                .header("User-Agent", AGENTE_USUARIO)
                .post(new FormBody.Builder().add("data", consulta).build())
                .build();

        try (Response resposta = clienteHttp.newCall(requisicao).execute()) {
            if (!resposta.isSuccessful() || resposta.body() == null) {
                return null;
            }
            String corpo = resposta.body().string();
            if (!corpo.contains("\"elements\"")) {
                return null;
            }
            return parsear(corpo, rotulo, latUsuario, lonUsuario);
        }
    }

    private List<LugarProximo> parsear(String corpoJson, String rotulo,
                                       double latUsuario, double lonUsuario) {
        List<LugarProximo> resultado = new ArrayList<>();
        Set<String> jaVistos = new HashSet<>();

        JsonObject raiz = JsonParser.parseString(corpoJson).getAsJsonObject();
        JsonArray elementos = raiz.getAsJsonArray("elements");
        if (elementos == null) {
            return resultado;
        }

        for (JsonElement elemento : elementos) {
            JsonObject objeto = elemento.getAsJsonObject();
            double lat;
            double lon;

            if (objeto.has("lat") && objeto.has("lon")) {
                lat = objeto.get("lat").getAsDouble();
                lon = objeto.get("lon").getAsDouble();
            } else if (objeto.has("center")) {
                JsonObject centro = objeto.getAsJsonObject("center");
                lat = centro.get("lat").getAsDouble();
                lon = centro.get("lon").getAsDouble();
            } else {
                continue;
            }

            String chave = String.format("%.5f_%.5f", lat, lon);
            if (jaVistos.contains(chave)) {
                continue;
            }
            jaVistos.add(chave);

            String nome = extrairNome(objeto);
            double dist = DistanciaUtil.distancia(latUsuario, lonUsuario, lat, lon);
            resultado.add(new LugarProximo(nome, rotulo, lat, lon, dist));
        }

        Collections.sort(resultado, Comparator.comparingDouble(LugarProximo::getDistancia));
        return resultado;
    }

    private String extrairNome(JsonObject objeto) {
        if (objeto.has("tags")) {
            JsonObject tags = objeto.getAsJsonObject("tags");
            if (tags.has("name")) {
                return tags.get("name").getAsString();
            }
            if (tags.has("brand")) {
                return tags.get("brand").getAsString();
            }
        }
        return "Sem nome";
    }

    public void encerrar() {
        executor.shutdown();
    }
}
