package com.provadb.af_mobile.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocalizacaoHelper {

    private Context contexto;
    private FusedLocationProviderClient clienteLocalizacao;
    private LocationCallback callbackAtivo;

    public interface Listener {
        void onOk(double latitude, double longitude);
        void onErro(String msg);
    }

    public LocalizacaoHelper(Context contexto) {
        this.contexto = contexto.getApplicationContext();
        this.clienteLocalizacao = LocationServices.getFusedLocationProviderClient(this.contexto);
    }

    public boolean temPermissao() {
        return ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void obterLocal(Listener listener) {
        encerrar();

        if (!temPermissao()) {
            listener.onErro("Permissão de localização negada. Ative nas configurações do aparelho.");
            return;
        }

        try {
            clienteLocalizacao.getLastLocation()
                    .addOnSuccessListener(localizacao -> {
                        if (localizacao != null) {
                            listener.onOk(localizacao.getLatitude(), localizacao.getLongitude());
                        } else {
                            pedirUpdate(listener);
                        }
                    })
                    .addOnFailureListener(e ->
                            listener.onErro("Não foi possível obter a localização. Verifique o GPS."));
        } catch (SecurityException e) {
            listener.onErro("Permissão de localização negada. Ative nas configurações do aparelho.");
        }
    }

    private void pedirUpdate(Listener listener) {
        LocationRequest requisicao = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdates(1)
                .build();

        callbackAtivo = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult resultado) {
                encerrar();
                Location localizacao = resultado.getLastLocation();
                if (localizacao != null) {
                    listener.onOk(localizacao.getLatitude(), localizacao.getLongitude());
                } else {
                    listener.onErro("Não foi possível obter a localização. Verifique o GPS.");
                }
            }
        };

        try {
            clienteLocalizacao.requestLocationUpdates(requisicao, callbackAtivo, Looper.getMainLooper());
        } catch (SecurityException e) {
            encerrar();
            listener.onErro("Permissão de localização negada. Ative nas configurações do aparelho.");
        }
    }

    public void encerrar() {
        if (callbackAtivo != null) {
            clienteLocalizacao.removeLocationUpdates(callbackAtivo);
            callbackAtivo = null;
        }
    }
}
