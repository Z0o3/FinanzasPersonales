package com.example.finanzas.config;

import com.example.finanzas.dao.DAOException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton de conexión a Firebase Realtime Database mediante su URL (API REST).
 * Equivale a DBConnection con MySQL: aquí vive la "cadena de conexión".
 */
public class FirebaseConfig {
    // URL de tu Realtime Database (consola de Firebase > Realtime Database > pestaña Datos).
    private static final String DEFAULT_URL = "https://finanzas-personales-94bc9-default-rtdb.firebaseio.com/";

    private static FirebaseConfig instance;
    private final String baseUrl;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();

    private FirebaseConfig() {
        String env = System.getenv("FIREBASE_DB_URL");
        String url = env != null && !env.isBlank() ? env.trim() : DEFAULT_URL;
        baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static synchronized FirebaseConfig getInstance() {
        if (instance == null) {
            instance = new FirebaseConfig();
        }
        return instance;
    }

    /** Comprueba que la URL sea válida y que la base responda. */
    public void verificar() {
        if (baseUrl.contains("TU-PROYECTO")) {
            throw new IllegalStateException("Configura la URL de tu Realtime Database en FirebaseConfig.DEFAULT_URL "
                    + "o en la variable de entorno FIREBASE_DB_URL.");
        }
        try {
            send("GET", baseUrl + "/.json?shallow=true", null);
            System.out.println("Successful Firebase connection.");
        } catch (DAOException e) {
            throw new IllegalStateException("No hay conexión con Firebase (" + baseUrl + "): " + e.getMessage());
        }
    }

    // ---- Operaciones REST sobre un nodo (los segmentos se codifican y se unen con "/")

    public String get(String... nodo) { return send("GET", url(nodo), null); }

    public void put(String json, String... nodo) { send("PUT", url(nodo), json); }

    public void patch(String json, String... nodo) { send("PATCH", url(nodo), json); }

    public void delete(String... nodo) { send("DELETE", url(nodo), null); }

    private String url(String... nodo) {
        List<String> partes = new ArrayList<>();
        for (String s : nodo) partes.add(URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+", "%20"));
        return baseUrl + "/" + String.join("/", partes) + ".json";
    }

    private String send(String metodo, String url, String body) {
        try {
            HttpRequest.Builder rb = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json");
            rb.method(metodo, body == null ? HttpRequest.BodyPublishers.noBody()
                    : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
            HttpResponse<String> r = http.send(rb.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                String ayuda = r.statusCode() == 401 || r.statusCode() == 403
                        ? " (revisa las reglas de la base: .read y .write)" : "";
                throw new DAOException("Firebase respondió " + r.statusCode() + ayuda + ": " + r.body(), null);
            }
            return r.body();
        } catch (DAOException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DAOException("Operación interrumpida", e);
        } catch (Exception e) {
            throw new DAOException("Error de red con Firebase: " + e.getMessage(), e);
        }
    }
}
