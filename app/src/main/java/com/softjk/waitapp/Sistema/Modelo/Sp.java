package com.softjk.waitapp.Sistema.Modelo;

public class Sp {

    private static int Tiempo;

    public Sp(){ }

    public Sp(int tiempo) {this.Tiempo = tiempo;}

    public static int getTiempo() {
        return Tiempo;
    }

    public static void setTiempo(String valor) {
        if (valor.equals("5 minutos")){
            Tiempo = 0;
        }else if (valor.equals("10 minutos")){
            Tiempo = 1;
        }else if (valor.equals("15 minutos")){
            Tiempo = 2;
        }else if (valor.equals("20 minutos")){
            Tiempo = 3;
        }else if (valor.equals("25 minutos")){
            Tiempo = 4;
        }else if (valor.equals("30 minutos")){
            Tiempo = 5;
        }else if (valor.equals("45 minutos")){
            Tiempo = 6;
        }else if (valor.equals("50 minutos")){
            Tiempo = 7;
        }else if (valor.equals("1 hora")){
            Tiempo = 8;
        }else if (valor.equals("1:15 Horas")){
            Tiempo = 9;
        }else if (valor.equals("1:30 Horas")){
            Tiempo = 10;
        }else if (valor.equals("1:45 Horas")){
            Tiempo = 11;
        }else if (valor.equals("2 Horas")){
            Tiempo = 12;
        }
    }
}


