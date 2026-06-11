package com.provadb.af_mobile.util;

public class DistanciaUtil {

    private static final double RAIO_TERRA_METROS = 6371000;

    public static double distancia(double lat1, double lon1, double lat2, double lon2) {
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_METROS * c;
    }

    public static String formatar(double metros) {
        if (metros < 1000) {
            return Math.round(metros) + " m";
        } else {
            return String.format("%.1f km", metros / 1000.0);
        }
    }
}
