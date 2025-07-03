package com.softjk.waitapp.Sistema.Modelo;

public class Sala {
    String Servicio, User, idUser, Estado, Pago, Accion,Foto;
    Double Precio;
    int Tiempo, AdmTiempoTotal, TiempoServicio;

    public Sala(){}

    public Sala(int tiempo, String servicio, String user, Double precio, String iduser, String estado,String pago, String accion, String foto, int admTiempoTotal, int tiempoServicio){
        this.Tiempo=tiempo;
        this.Servicio=servicio;
        this.User=user;
        this.Precio=precio;
        this.idUser=iduser;
        this.Estado=estado;
        this.Pago=pago;
        this.Accion=accion;
        this.Foto=foto;
        this.AdmTiempoTotal=admTiempoTotal;
        this.TiempoServicio = tiempoServicio;
    }

    public int getTiempo() {
        return Tiempo;
    }

    public void setTiempo(int tiempo) {
        Tiempo = tiempo;
    }

    public String getServicio() {
        return Servicio;
    }

    public void setServicio(String servicio) {
        Servicio = servicio;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Double getPrecio() {
        return Precio;
    }

    public void setPrecio(Double precio) {
        Precio = precio;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public String getPago() {
        return Pago;
    }

    public void setPago(String pago) {
        Pago = pago;
    }

    public String getAccion() {
        return Accion;
    }

    public void setAccion(String accion) {
        Accion = accion;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public int getAdmTiempoTotal() {
        return AdmTiempoTotal;
    }

    public void setAdmTiempoTotal(int admTiempoTotal) {
        AdmTiempoTotal = admTiempoTotal;
    }

    public int getTiempoServicio() {
        return TiempoServicio;
    }

    public void setTiempoServicio(int tiempoServicio) {
        TiempoServicio = tiempoServicio;
    }
}
