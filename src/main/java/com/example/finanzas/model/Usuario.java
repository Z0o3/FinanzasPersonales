package com.example.finanzas.model;

public class Usuario implements Identifiable {
    private String id;
    private String nombre;
    private String nombreCompleto;
    private String passwordHash;
    private String salt;

    public Usuario(String id, String nombre, String nombreCompleto, String passwordHash, String salt) {
        this.id = id;
        this.nombre = nombre;
        this.nombreCompleto = nombreCompleto;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNombre() { return nombre; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getPasswordHash() { return passwordHash; }
    public String getSalt() { return salt; }
}
