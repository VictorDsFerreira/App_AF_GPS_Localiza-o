package com.provadb.af_mobile.model;

import java.util.HashMap;
import java.util.Map;

public class LugarSalvo {

    private String id;
    private String nome;
    private String tipo;
    private String classif;
    private double latitude;
    private double longitude;
    private String obs;

    public LugarSalvo() {
    }

    public LugarSalvo(String nome, String tipo, String classif,
                      double latitude, double longitude, String obs) {
        this.nome = nome;
        this.tipo = tipo;
        this.classif = classif;
        this.latitude = latitude;
        this.longitude = longitude;
        this.obs = obs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getClassif() {
        return classif;
    }

    public void setClassif(String classif) {
        this.classif = classif;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getCoords() {
        return String.format("%.5f, %.5f", latitude, longitude);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> mapa = new HashMap<>();
        mapa.put("name", nome);
        mapa.put("poiType", tipo);
        mapa.put("userCategory", classif);
        mapa.put("latitude", latitude);
        mapa.put("longitude", longitude);
        mapa.put("observation", obs);
        return mapa;
    }

    public static LugarSalvo fromMap(String id, Map<String, Object> mapa) {
        LugarSalvo lugar = new LugarSalvo();
        lugar.setId(id);
        lugar.nome = (String) mapa.get("name");
        lugar.tipo = (String) mapa.get("poiType");
        lugar.classif = (String) mapa.get("userCategory");
        Object lat = mapa.get("latitude");
        Object lon = mapa.get("longitude");
        lugar.latitude = lat instanceof Number ? ((Number) lat).doubleValue() : 0;
        lugar.longitude = lon instanceof Number ? ((Number) lon).doubleValue() : 0;
        lugar.obs = (String) mapa.get("observation");
        return lugar;
    }
}
