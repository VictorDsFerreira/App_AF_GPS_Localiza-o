package com.provadb.af_mobile.firebase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.provadb.af_mobile.model.LugarSalvo;

import java.util.ArrayList;
import java.util.List;

public class LugarSalvoRepository {

    private static final String COLECAO = "saved_places";

    private final FirebaseFirestore banco = FirebaseFirestore.getInstance();

    public interface ListenerLista {
        void onOk(List<LugarSalvo> lugares);
        void onErro(Exception e);
    }

    public interface Listener {
        void onOk();
        void onErro(Exception e);
    }

    public void salvar(LugarSalvo lugar, Listener listener) {
        banco.collection(COLECAO)
                .add(lugar.toMap())
                .addOnSuccessListener(docRef -> listener.onOk())
                .addOnFailureListener(listener::onErro);
    }

    public void listar(ListenerLista listener) {
        banco.collection(COLECAO)
                .orderBy("name")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<LugarSalvo> lista = new ArrayList<>();
                    for (QueryDocumentSnapshot documento : snapshot) {
                        lista.add(LugarSalvo.fromMap(documento.getId(), documento.getData()));
                    }
                    listener.onOk(lista);
                })
                .addOnFailureListener(listener::onErro);
    }

    public void atualizar(String id, String classif, String obs, Listener listener) {
        banco.collection(COLECAO).document(id)
                .update("userCategory", classif, "observation", obs)
                .addOnSuccessListener(unused -> listener.onOk())
                .addOnFailureListener(listener::onErro);
    }

    public void excluir(String id, Listener listener) {
        banco.collection(COLECAO).document(id)
                .delete()
                .addOnSuccessListener(unused -> listener.onOk())
                .addOnFailureListener(listener::onErro);
    }
}
