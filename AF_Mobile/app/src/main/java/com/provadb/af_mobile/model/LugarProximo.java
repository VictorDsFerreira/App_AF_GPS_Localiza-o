package com.provadb.af_mobile.model;

public class LugarProximo {

    private final String nome;
    private final String categoria;
    private final double latitude;
    private final double longitude;
    private final double distancia;

    public LugarProximo(String nome, String categoria, double latitude, double longitude, double distancia) {
        this.nome = nome;
        this.categoria = categoria;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distancia = distancia;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistancia() {
        return distancia;
    }
}
