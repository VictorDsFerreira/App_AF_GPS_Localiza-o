package com.provadb.af_mobile.util;

import android.content.Context;

import com.provadb.af_mobile.R;

public final class CategoriaPoiHelper {

    private CategoriaPoiHelper() {
    }

    public static String montarQuery(Context contexto, int indice, double lat, double lon) {
        String filtro = getFiltro(indice);
        return "[out:json][timeout:15];"
                + "("
                + "nwr" + filtro + "(around:1500," + lat + "," + lon + ");"
                + ");"
                + "out center 30;";
    }

    private static String getFiltro(int indice) {
        switch (indice) {
            case 0:
                return "[\"amenity\"=\"pharmacy\"]";
            case 1:
                return "[\"amenity\"=\"hospital\"]";
            case 2:
                return "[\"amenity\"=\"school\"]";
            case 3:
                return "[\"amenity\"=\"restaurant\"]";
            case 4:
                return "[\"leisure\"=\"park\"]";
            case 5:
                return "[\"shop\"=\"supermarket\"]";
            default:
                return "[\"amenity\"=\"pharmacy\"]";
        }
    }

    public static String getRotulo(Context contexto, int indice) {
        return contexto.getResources().getStringArray(R.array.poi_search_categories)[indice];
    }
}
