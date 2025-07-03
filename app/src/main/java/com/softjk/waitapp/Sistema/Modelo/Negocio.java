package com.softjk.waitapp.Sistema.Modelo;

public class Negocio {
    private String ID;
    private String Codigo;
    private String Nombre;
    private String Logo;
    private String Ubicacion;
    private String Tipo;
    private String Salas;

    public Negocio(){

    }

    public Negocio(String id,String codigo,String nombre, String logo, String ubicacion, String tipo, String salas){
        this.ID=id;
        this.Codigo=codigo;
        this.Nombre=nombre;
        this.Logo=logo;
        this.Ubicacion=ubicacion;
        this.Tipo=tipo;
        this.Salas=salas;
    }

    //Metodos Get y Set

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        Ubicacion = ubicacion;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getSalas() {
        return Salas;
    }

    public void setSalas(String salas) {
        Salas = salas;
    }
}


