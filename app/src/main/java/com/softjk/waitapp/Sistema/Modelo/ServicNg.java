package com.softjk.waitapp.Sistema.Modelo;

public class ServicNg {

    private String Id;
    private String Nombre;
    private String Categoria;
    private int Precio;
    private String Logo;
    private int Tiempo;

    public ServicNg(){

    }

    public ServicNg(String id, String nombre, String categoria, int precio, String logo, int tiempo) {
        Id = id;
        Nombre = nombre;
        Categoria = categoria;
        Precio = precio;
        Logo = logo;
        Tiempo = tiempo;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public int getTiempo() {
        return Tiempo;
    }

    public void setTiempo(int tiempo) {
        Tiempo = tiempo;
    }
}
